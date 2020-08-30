package com.patrikmaryska.bc_prace.bc_prace.service;

import com.patrikmaryska.bc_prace.bc_prace.email.EmailService;
import com.patrikmaryska.bc_prace.bc_prace.model.Document;
import com.patrikmaryska.bc_prace.bc_prace.model.User;
import com.patrikmaryska.bc_prace.bc_prace.model.UsersDocuments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleService {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;


    @Scheduled(cron = "0 00 23 * * ?")
    public void blockPassedDocuments() {
        List<Document> passedDocuments = documentService.getAllPassedDocuments();

        if (passedDocuments.size() > 0) {
            passedDocuments.forEach(document -> {
                documentService.blockDocument(document);
                List<User> users = userService.getUsersForApprovingDocument(document.getId());

                if(document.getUser().isActive()){
                    users.add(document.getUser());
                }

                Map<String, String> map = emailService.createMessage(4, document.getUser(), document);
                documentService.sendEmail(users, map.get("subject"), map.get("message"));
            });
        }
    }

    @Transactional
    @Scheduled(cron = "0 0/30 * * * ?")
    public void sendEmailAboutReleasingNewDocumentToReaders(){
        List<UsersDocuments> releasingDocuments = documentService.getNewActiveDocuments();
        if(releasingDocuments.size() > 0){
            releasingDocuments.forEach(document -> {
                List<User> users = userService.getUsersForReadingDocument(document.getDocument().getId());

                if(document.getUser().isActive()){
                    users.add(document.getUser());
                }

                Map<String, String> map = emailService.createMessage(1, document.getUser(), document.getDocument());
                documentService.sendEmail(users, map.get("subject"), map.get("message"));
                document.setEmailSent(true);
                documentService.updateEmailSent(document);
            });
        }
    }
}