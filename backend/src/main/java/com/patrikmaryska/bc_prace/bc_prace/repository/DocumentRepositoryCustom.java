package com.patrikmaryska.bc_prace.bc_prace.repository;

import com.patrikmaryska.bc_prace.bc_prace.model.*;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.SimplifiedUser;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.UsersApproval;

import java.util.Date;
import java.util.List;

public interface DocumentRepositoryCustom {
   List<Document> getAllUsersDocuments(long id);
   List<Document> getAllDocsByEmail(String email, int page);
   void saveDocument(Document document);
   Document getDocumentByName(String name);

    List<SimplifiedUser> getReaders(int page, long docId, int sharingType, int approval);

   void insertSharing(String name, List<User> users, long documentTypeId, boolean emailSent, Date appTime);
   void updateSharing(long userId, long documentId, int approval);

    boolean hasUserAccessToDocument(long documentId, long userId);

    List<Document> findDocumentByTitle(String name, long userId, int page);

    List<Document> getUsersCreatedDocuments(long id, int year, int month, int pageNumber);

    List<UsersApproval> getUsersForDocument(long docId, long userId);
    SharingType getDocumentType(long id);

    boolean isDocumentApproved(long docId);
    long getCountOfApprovals(long docId);
    long getExpectedCountOfApprovals(long docId);

    DocumentState getDocumentState(long id);

    void updateDocument(Document updatedDocument);

    List<Document> getAllPassedDocuments();

    void blockDocument(Document document);

    List<Document> getDocumentsByYearAndMonth(int year, int month, String email, int page);

    List<UsersDocuments> getNewActiveDocuments();

    void updateEmailSent(UsersDocuments usersDocuments);

    boolean deleteDocument(long id);

    boolean deleteAllUsersDocumentsByDocumentId(long id);

    List<Document> getAllDocumentsByYearAndMonth(int year, int month, int page);

    boolean hasUserPermissionToAccessDocument(long documentId, long userId);

    UsersDocuments getUsersDocuments(long userId, long docId, long sharingTypeId);
}
