package com.patrikmaryska.bc_prace.bc_prace.email;

import com.patrikmaryska.bc_prace.bc_prace.model.Document;
import com.patrikmaryska.bc_prace.bc_prace.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    public JavaMailSender emailSender;

    private String address = "https://docmanager.com";


    @Async
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }


    public Map<String, String> createMessage(int type, User user, Document document){
        Map<String, String > map = new HashMap<>();
        String message = "";
        String subject = "";
        switch (type){
            case 1: // DOCUMENT APPROVED
                message = "Document " + document.getTitle() + " has been approved. Document will be available in application from " + formatTimeMessage(document.getActiveStartTime()) +
                        " to " + formatTimeMessage(document.getActiveEndTime())+ ". It is your responsibility to check out the document.\n" +
                        "Please visit this site to make appropriate action. " + address;
                subject = "Document " + document.getTitle() + " is available.";
                 break;
            case 2: // DOCUMENT FAILED
                message = "Document " + document.getTitle() + " has not been approved. User " + user.getFirstName() + " " + user.getSurname() +
                        " does not approve with this document. Document is cancelled now. New version of the document should be made.\n"+
                        "Please visit this site to make appropriate action. " + address;
                subject = "Document " + document.getTitle() + " has been cancelled.";
                break;
            case 3: // DOCUMENT FOR APPROVING
                message = "Document " + document.getTitle() + " by " + document.getUser().getFirstName() + " " + document.getUser().getSurname() +
                        " has been created. It is your responsibility to approve the document until " + formatTimeMessage(document.getApprovalEndTime())+". Document is " +
                        " available in application now.\n"+
                        "Please visit this site to make appropriate action. " + address;
                subject = "Document " + document.getTitle() + " needs to be approved,";
                break;
            case 4: // DOCUMENT WAS BLOCK DUE TO RUNNING OUT OF TIME
                message = "Document " + document.getTitle() + " has not been approved. Document was not approved by some users in the given time period." + " Document is cancelled now. New version of the document should be made.\n"
                + "Please visit this site to make appropriate action. " + address;
                subject = "Document " + document.getTitle() + " has run out of time.";
                break;
            case 5: // APPROVED BY ALL USERS
                message = "Document " + document.getTitle() + " by " + document.getUser().getFirstName() + " "  + document.getUser().getSurname() + " has been approved by all users. Document will be available in the application for readers from " + formatTimeMessage(document.getActiveStartTime()) + ".";
                subject = "Document " + document.getTitle() + " is approved.";
                break;
        }
        map.put("message", message);
        map.put("subject", subject);

        return map;
    }

    @Override
    public String formatTimeMessage(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm");

        return format.format(date);
    }
}
