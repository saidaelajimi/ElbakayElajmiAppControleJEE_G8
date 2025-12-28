package net.elbakay.backend.service;


import net.elbakay.backend.dto.TaskDTO;
import net.elbakay.backend.model.Task;
import net.elbakay.backend.model.User;
import net.elbakay.backend.repository.TaskRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public TaskDTO createTask(TaskDTO taskDTO, Long userId) {
        User user = userService.findById(userId);

        Task task = new Task();
        BeanUtils.copyProperties(taskDTO, task);
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        TaskDTO savedDto = convertToDTO(savedTask);
        
        // Notifier Kafka
        kafkaProducerService.sendTaskEvent("CREATED", savedDto);
        
        return savedDto;
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDTO, Long userId) {
        Task task = getTaskByIdAndUserId(id, userId);

        BeanUtils.copyProperties(taskDTO, task, "id", "createdAt", "user");

        Task updatedTask = taskRepository.save(task);
        TaskDTO updatedDto = convertToDTO(updatedTask);
        
        // Notifier Kafka
        kafkaProducerService.sendTaskEvent("UPDATED", updatedDto);
        
        return updatedDto;
    }

    public void deleteTask(Long id, Long userId) {
        Task task = getTaskByIdAndUserId(id, userId);
        TaskDTO deletedDto = convertToDTO(task);
        taskRepository.delete(task);
        
        // Notifier Kafka
        kafkaProducerService.sendTaskEvent("DELETED", deletedDto);
    }

    public TaskDTO getTaskById(Long id, Long userId) {
        Task task = getTaskByIdAndUserId(id, userId);
        return convertToDTO(task);
    }

    public List<TaskDTO> getAllTasksByUser(Long userId) {
        User user = userService.findById(userId);
        return taskRepository.findByUserOrderByDateDesc(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByDate(Long userId, LocalDate date) {
        User user = userService.findById(userId);
        return taskRepository.findByUserAndDateOrderByDate(user, date)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByMonth(Long userId, int year, int month) {
        User user = userService.findById(userId);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return taskRepository.findByUserAndDateBetweenOrderByDate(user, start, end)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Task getTaskByIdAndUserId(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Accès non autorisé à cette tâche");
        }

        return task;
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        BeanUtils.copyProperties(task, dto);
        dto.setUserId(task.getUser().getId());
        dto.setUserEmail(task.getUser().getEmail());
        return dto;
    }
}
