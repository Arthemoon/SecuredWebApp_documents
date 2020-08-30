package com.patrikmaryska.bc_prace.bc_prace.utils;

import com.patrikmaryska.bc_prace.bc_prace.model.Document;
import com.patrikmaryska.bc_prace.bc_prace.model.User;
import com.patrikmaryska.bc_prace.bc_prace.model.UsersDocuments;

import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

public class DocumentComparator implements Comparator<Document> {
    private User user;

    public DocumentComparator(User user){
        this.user = user;
    }

    @Override
    public int compare(Document d1, Document d2) {
        Date date1 = new Date();
        Date date2 = new Date();

        Optional<UsersDocuments> ud1 = d1.getDocumentsForUsers().stream().
                filter(usersDocuments -> usersDocuments.getUser().getId() == user.getId()).findFirst();
        Optional<UsersDocuments> ud2 = d2.getDocumentsForUsers().stream().
                filter(usersDocuments -> usersDocuments.getUser().getId() == user.getId()).findFirst();

        if(ud1.get().getSharingType().getId() == 1){
            date1 = d1.getUploadDatetime();
        }  else {
            date1 = d1.getActiveStartTime();
        }

        if(ud2.get().getSharingType().getId() == 1){
            date2 = d2.getUploadDatetime();
        }  else{
            date2 = d2.getActiveStartTime();
        }

        return date1.compareTo(date2);
    }
}
