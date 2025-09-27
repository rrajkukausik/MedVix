package com.medivex.user.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    public void sendEmailVerification(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify your email - MedVix");
            message.setText(buildEmailVerificationMessage(token));
            
            mailSender.send(message);
            log.info("Email verification sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email verification to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email verification");
        }
    }
    
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Reset your password - MedVix");
            message.setText(buildPasswordResetMessage(token));
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send password reset email");
        }
    }
    
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to MedVix!");
            message.setText(buildWelcomeMessage(firstName));
            
            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
            // Don't throw exception for welcome email as it's not critical
        }
    }
    
    private String buildEmailVerificationMessage(String token) {
        return String.format("""
            Hello,
            
            Thank you for registering with MedVix! Please click the link below to verify your email address:
            
            %s/verify-email?token=%s
            
            This link will expire in 24 hours.
            
            If you didn't create an account with MedVix, please ignore this email.
            
            Best regards,
            The MedVix Team
            """, frontendUrl, token);
    }
    
    private String buildPasswordResetMessage(String token) {
        return String.format("""
            Hello,
            
            You requested to reset your password for your MedVix account. Please click the link below to reset your password:
            
            %s/reset-password?token=%s
            
            This link will expire in 1 hour.
            
            If you didn't request a password reset, please ignore this email.
            
            Best regards,
            The MedVix Team
            """, frontendUrl, token);
    }
    
    private String buildWelcomeMessage(String firstName) {
        return String.format("""
            Hello %s,
            
            Welcome to MedVix! Your account has been successfully created and verified.
            
            You can now access all the features of our pharmacy management system.
            
            If you have any questions, please don't hesitate to contact our support team.
            
            Best regards,
            The MedVix Team
            """, firstName);
    }
}
