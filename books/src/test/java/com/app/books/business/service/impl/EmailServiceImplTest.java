package com.app.books.business.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImpl();

        // Using reflection to set the private mailSender field
        Field field = EmailServiceImpl.class.getDeclaredField("mailSender");
        field.setAccessible(true);  // Make the field accessible
        field.set(emailService, mailSender);  // Inject mock into the private field
    }

    @Test
    void sendSimpleMessage_ShouldSendEmail() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test message content";

        // Act
        emailService.sendSimpleMessage(to, subject, text);

        // Assert
        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setTo(to);
        expectedMessage.setSubject(subject);
        expectedMessage.setText(text);

        // Verify that JavaMailSender's send method was called with the expected message
        verify(mailSender, times(1)).send(expectedMessage);
    }
}
