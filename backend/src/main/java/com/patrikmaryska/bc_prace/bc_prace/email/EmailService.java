package com.patrikmaryska.bc_prace.bc_prace.email;

import com.patrikmaryska.bc_prace.bc_prace.model.Document;
import com.patrikmaryska.bc_prace.bc_prace.model.User;
import org.springframework.mail.SimpleMailMessage;

import java.util.Date;
import java.util.Map;

public interface EmailService {
    void sendSimpleMessage(String to,
                           String subject,
                           String text);

    Map<String, String> createMessage(int type, User user, Document document);

    String formatTimeMessage(Date date);
}