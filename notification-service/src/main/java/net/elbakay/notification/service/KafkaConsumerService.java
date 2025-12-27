package net.elbakay.notification.service;

import net.elbakay.notification.dto.TaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "task-events", groupId = "notification-group")
    public void consume(net.elbakay.notification.dto.TaskEvent event) {
        String action = event.getAction();
        net.elbakay.notification.dto.TaskDTO task = event.getTask();
        
        System.out.println("=== NOTIFICATION SERVICE RECEIVED EVENT ===");
        System.out.println("Action: " + action + " | Task: " + task.getTitre());
        
        String subject = "";
        String messageAction = "";
        
        switch (action) {
            case "CREATED":
                subject = "Nouvelle Tâche : " + task.getTitre();
                messageAction = "créée";
                break;
            case "UPDATED":
                subject = "Tâche Modifiée : " + task.getTitre();
                messageAction = "mise à jour";
                break;
            case "DELETED":
                subject = "Tâche Supprimée : " + task.getTitre();
                messageAction = "supprimée";
                break;
            default:
                subject = "Notification Agenda : " + task.getTitre();
                messageAction = "traitée";
        }

        String body = String.format(
            "Bonjour,\n\nUne tâche a été %s dans votre agenda :\n\n" +
            "Titre : %s\n" +
            "Description : %s\n" +
            "Date : %s\n" +
            "Heure : %s\n\n" +
            "Cordialement,\nL'équipe Agenda J2E",
            messageAction, task.getTitre(), task.getDescription(), task.getDate(), task.getHeure()
        );
        
        try {
            emailService.sendSimpleEmail(task.getUserEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
