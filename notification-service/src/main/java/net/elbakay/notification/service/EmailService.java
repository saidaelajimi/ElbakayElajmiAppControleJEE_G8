package net.elbakay.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        // Pour Gmail, il est préférable que le 'From' corresponde à l'adresse authentifiée
        message.setFrom("saida.elajimi@etu.fstm.ac.ma");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        System.out.println("Sending real email to: " + to);
        mailSender.send(message);
        System.out.println("Email sent successfully!");
    }
}
