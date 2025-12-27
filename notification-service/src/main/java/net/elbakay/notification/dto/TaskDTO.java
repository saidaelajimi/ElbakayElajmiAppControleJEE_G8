package net.elbakay.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String titre;
    private String description;
    private LocalDate date;
    private String heure;
    private String userEmail;
}
