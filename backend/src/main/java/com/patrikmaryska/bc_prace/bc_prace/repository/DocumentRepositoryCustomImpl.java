package com.patrikmaryska.bc_prace.bc_prace.repository;

import com.patrikmaryska.bc_prace.bc_prace.model.*;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.SimplifiedUser;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.UsersApproval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DocumentRepositoryCustomImpl implements DocumentRepositoryCustom {

    private final int PAGE_SIZE = 10;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Document> getAllUsersDocuments(long id) {
        TypedQuery<Document> query = entityManager.createQuery("SELECT d FROM Document d join d.documentsForUsers ud WHERE ud.user = :id ORDER BY d.uploadDateTime DESC", Document.class);
        query.setParameter("id", id);

        return query.getResultList();
    }

    @Override
    public List<Document> getAllDocsByEmail(String email, int page) {
        User user = userRepository.getUserByEmail(email).get();

         TypedQuery<Document> q2 = entityManager.createQuery("SELECT d FROM Document d INNER JOIN d.documentsForUsers ud WHERE d.documentState > 0 " +
                 "AND ud.user.id=:id AND ud.approval=2 AND current_timestamp >= ud.applicationStartTime ORDER BY ud.applicationStartTime DESC", Document.class);
        q2.setParameter("id", user.getId());
        q2.setFirstResult((page - 1 ) * PAGE_SIZE);
        q2.setMaxResults(PAGE_SIZE);

        return updateDocumentList(q2.getResultList(), user.getId());
    }


    @Override
    public void saveDocument(Document document) throws PersistenceException {
        entityManager.persist(document);
    }

    @Override
    public Document getDocumentByName(String name){
        TypedQuery<Document> query = entityManager.createQuery("SELECT d FROM Document d WHERE d.name=:name", Document.class);
        query.setParameter("name", name);

        return query.getSingleResult();
    }

    @Override
    public void insertSharing(String name, List<User> users, long documentTypeId, boolean emailSent, Date appTime) {
        Document document = getDocumentByName(name);

        Query query;
        if(document != null){
            for(int i = 0; i < users.size(); i++){
                query = entityManager.createNativeQuery("INSERT INTO users_documents (document_id, user_id, approval, sharing_type_id, email_sent, application_date) VALUES (:value1, :value2, 2, :value3, :value4, :value5)");
                query.setParameter("value1", document.getId());
                query.setParameter("value2", users.get(i).getId());
                query.setParameter("value3", documentTypeId);
                query.setParameter("value5", appTime);
                query.setParameter("value4", emailSent);

                query.executeUpdate();
            }
        }
    }

    @Override
    public void updateSharing(long userId, long documentId, int approval) {
        Document document = getDocumentById(documentId);
        UserDocumentsId userDocumentsId = new UserDocumentsId(userId, documentId);

        if(document.getDocumentState().getId() == 2 || document.getDocumentState().getId() == 3){
            userDocumentsId.setSharingTypeId(1); // approval
        } else if(document.getDocumentState().getId() == 1){
            // approved, readers
            userDocumentsId.setSharingTypeId(2);
        } else {
            // cancelled do nothing
            return;
        }

        UsersDocuments usersDocuments = entityManager.find(UsersDocuments.class, userDocumentsId);

        usersDocuments.setApproval(approval);
        entityManager.merge(usersDocuments);
    }

    public Document getDocumentById(long docId){
       return (Document) entityManager.find(Document.class, docId);
    }


    @Override
    public boolean hasUserAccessToDocument(long documentId, long userId) {
        Document document = getDocumentById(documentId);
        String sql = "";

        if(document.getUser().getId() == userId){
            return true;
        }

        User user = userRepository.getOne(userId);

        if(user.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"))){
            return true;
        }

        if(document.getDocumentState().getId() == 2 || document.getDocumentState().getId() == 3){
            // IS PROCESSING, OR CANCELLED
            sql = "SELECT ud FROM UsersDocuments ud WHERE ud.sharingType.id=1 AND ud.document.id=:docId AND ud.user.id=:userId";
        } else if(document.getDocumentState().getId() == 1) {
            sql = "SELECT ud FROM UsersDocuments ud WHERE (ud.sharingType.id=1 OR ud.sharingType.id=2) AND ud.document.id=:docId AND ud.user.id=:userId";
        } else {
            // nic
            return false;
        }

        TypedQuery<UsersDocuments> usersDocuments = entityManager.createQuery(sql, UsersDocuments.class);
        usersDocuments.setParameter("docId", documentId);
        usersDocuments.setParameter("userId", userId);

        if(usersDocuments.getResultList().size() > 0){
            return true;
        }

        return false;
    }

    @Override
    public List<Document> findDocumentByTitle(String name, long userId, int page) {

        TypedQuery<Document> q2 = entityManager.createQuery("SELECT d FROM Document d " +
                "INNER JOIN d.documentsForUsers ud WHERE (d.documentState.id != 0 AND ud.sharingType = 1 AND" +
                        " ud.user.id=:id AND d.title LIKE :value)" +
                        " OR(d.documentState.id = 1 AND ud.sharingType.id=2 AND ud.user.id=:id" +
                        " AND current_timestamp > d.activeStartTime AND " +
                        " d.title LIKE :value) ORDER BY d.title"
                , Document.class);
        q2.setParameter("id", userId);
        q2.setParameter("value",name+"%"); //
        q2.setMaxResults(PAGE_SIZE);
        q2.setFirstResult((page - 1 ) * PAGE_SIZE);

        return updateDocumentList(q2.getResultList(), userId);
    }

    private List<Document> updateDocumentList(List<Document> list, long userId){
        List<Document> docs = list.stream().map(document -> {
            UsersDocuments ud = document.getDocumentsForUsers().stream().filter(usersDocuments -> usersDocuments.getUser().getId() == userId
            && usersDocuments.getDocument().getId() == document.getId()).findFirst().get();
            document.setApproval(ud.getApproval());
            document.setApplicationStartTime(ud.getApplicationStartTime());
            return document;
        }).collect(Collectors.toList());

        return docs;
    }


    public Date minmaxDate(int year, int month, final String type){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        if(type.equals("MIN")){
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        }


        int intDate = type.equals("MAX") ? calendar.getActualMaximum(Calendar.DATE) : calendar.getActualMinimum(Calendar.DATE);

        calendar.set(Calendar.DATE, intDate);

        System.out.println("TYPE: " + type + " MAX: " + calendar.getTime());

        return calendar.getTime();
    }

    @Override
    public List<Document> getUsersCreatedDocuments(long id, int year, int month, int pageNumber) {

        Date minDate = minmaxDate(year, month, "MIN");
        Date maxDate = minmaxDate(year, month, "MAX");

        TypedQuery<Document> query = entityManager.createQuery("SELECT d FROM Document d JOIN d.user u " +
                " WHERE d.uploadDatetime BETWEEN :minDate AND :maxDate and d.user.id=:value " +
                "ORDER BY d.uploadDatetime DESC", Document.class);

        query.setParameter("value", id);
        query.setParameter("minDate", minDate);
        query.setParameter("maxDate", maxDate);
        query.setFirstResult((pageNumber - 1 ) * PAGE_SIZE);
        query.setMaxResults(PAGE_SIZE);


     //   return updateDocumentList(query.getResultList());
        return query.getResultList();
    }

    @Override
    public List<UsersApproval> getUsersForDocument(long docId, long userId) {

        Query query = entityManager.createNativeQuery("SELECT u.first_name, u.surname, ud.approval FROM user u JOIN users_documents ud ON u.id = ud.user_id " +
                "WHERE ud.document_id = :x");

        query.setParameter("x", docId);
        List<UsersApproval> usersApprovals = new ArrayList<>();

        List<Object[]> results = query.getResultList();

        results.forEach(object -> {
            String firstName = (String) object[0];
            String surname = (String) object[1];
            int approval = (Integer) object[2];


            usersApprovals.add(new UsersApproval(firstName, surname, approval));
        });

        return usersApprovals;
    }

    @Override
    public SharingType getDocumentType(long id){
        TypedQuery<SharingType> documentTypeTypedQuery = entityManager.createQuery("SELECT d FROM DocumentType d WHERE d.id=:id", SharingType.class);
        documentTypeTypedQuery.setParameter("id", id);

        return documentTypeTypedQuery.getSingleResult();
    }

    @Override
    public boolean isDocumentApproved(long documentId){
        Query query = entityManager.createQuery("");

        return true;
    }

    @Override
    public long getCountOfApprovals(long docId) {
        Query query = entityManager.createQuery("SELECT count(d.id) from Document d INNER JOIN d.documentsForUsers ud " +
                "INNER JOIN ud.sharingType dt WHERE dt.id = :val1 AND ud.document.id = :val2 AND ud.approval = :val3");

        query.setParameter("val1", 1L);
        query.setParameter("val2", docId);
        query.setParameter("val3", 1);

        return (long) query.getSingleResult();
    }

    @Override
    public long getExpectedCountOfApprovals(long documentId){
        Query query = entityManager.createQuery("SELECT count(d.id) from Document d INNER JOIN d.documentsForUsers ud " +
                "INNER JOIN ud.sharingType dt WHERE dt.id = :val1 AND ud.document.id = :val2");
        query.setParameter("val1", 1L);
        query.setParameter("val2", documentId);

        return (long) query.getSingleResult();
    }

    @Override
    public DocumentState getDocumentState(long id){
        TypedQuery<DocumentState> query = entityManager.createQuery("SELECT st FROM DocumentState st WHERE st.id=:id", DocumentState.class);
        query.setParameter("id", id);

        return query.getSingleResult();
    }

    @Override
    public void updateDocument(Document document){
        entityManager.merge(document);
    }

    @Override
    public List<Document> getAllPassedDocuments() {
        TypedQuery<Document> documents = entityManager.createQuery("SELECT d FROM Document d " +
                "WHERE d.approvalEndTime < CURRENT_TIMESTAMP and d.documentState.id=2 ", Document.class);

        return documents.getResultList();
    }


    @Override
    public void blockDocument(Document document) {
        DocumentState documentState = getDocumentState(3);
        document.setDocumentState(documentState);

        entityManager.merge(document);
    }

    @Override
    public List<Document> getDocumentsByYearAndMonth(int year, int month, String email, int page) {

        Date minDate = minmaxDate(year, month, "MIN");
        Date maxDate = minmaxDate(year, month, "MAX");
        User user = userRepository.getUserByEmail(email).get();

        TypedQuery<Document> q3 = entityManager.createQuery("SELECT d FROM Document d " +
                "INNER JOIN d.documentsForUsers ud WHERE (" +
                " ud.sharingType = 1" +
                " AND ud.user.id=:id AND ud.approval != 2 AND" +
                " d.uploadDatetime between :minDate and :maxDate)" +
                " OR(d.documentState.id = 1 AND ud.sharingType.id=2" +
                " AND ud.user.id=:id" +
                " AND current_timestamp > d.activeStartTime AND ud.approval != 2" +
                " AND d.activeStartTime between :minDate AND :maxDate) ORDER BY ud.applicationStartTime DESC", Document.class);

        q3.setParameter("id", user.getId());
        q3.setParameter("minDate", minDate);
        q3.setParameter("maxDate", maxDate);
        q3.setFirstResult((page - 1) * PAGE_SIZE);
        q3.setMaxResults(PAGE_SIZE);

        Optional<Document> doc = q3.getResultList().stream().filter(document -> document.getId() == 280319L).findFirst();

        System.out.println("DOC " + doc.isPresent());

        return updateDocumentList(q3.getResultList(), user.getId());

    //    return q3.getResultList();
    }

    @Override
    public List<UsersDocuments> getNewActiveDocuments() {
        TypedQuery<UsersDocuments> documentTypedQuery = entityManager.createQuery("SELECT ud FROM Document d" +
                " JOIN d.documentsForUsers ud WHERE d.activeStartTime < current_timestamp " +
                "AND ud.emailSent=0 AND d.documentState.id=1", UsersDocuments.class);

        return documentTypedQuery.getResultList();
    }

    @Override
    public void updateEmailSent(UsersDocuments usersDocuments){
        entityManager.merge(usersDocuments);
    }


    @Override
    public boolean deleteDocument(long id){
        Query query = entityManager.createNativeQuery("DELETE FROM document WHERE id=:id");
        query.setParameter("id", id);

        return query.executeUpdate() > 0;
    }

    @Override
    public boolean deleteAllUsersDocumentsByDocumentId(long id){
        Query query = entityManager.createNativeQuery("DELETE FROM users_documents WHERE document_id=:id");
        query.setParameter("id", id);

        return query.executeUpdate() > 0;
    }

    @Override
    public List<Document> getAllDocumentsByYearAndMonth(int year, int month, int page){

        Date minDate = minmaxDate(year, month, "MIN");
        Date maxDate = minmaxDate(year, month, "MAX");

        TypedQuery<Document> documentTypedQuery = entityManager.createQuery("SELECT d FROM Document d " +
                "WHERE d.uploadDatetime BETWEEN :minDate AND :maxDate " +
                "ORDER BY d.uploadDatetime DESC", Document.class);

        documentTypedQuery.setParameter("minDate", minDate);
        documentTypedQuery.setParameter("maxDate", maxDate);
        documentTypedQuery.setFirstResult((page - 1 ) * PAGE_SIZE);
        documentTypedQuery.setMaxResults(PAGE_SIZE);

        return documentTypedQuery.getResultList();
    }

    @Override
    public boolean hasUserPermissionToAccessDocument(long documentId, long userId) {
        User user = userRepository.getOne(userId);
        Document document = getDocumentById(documentId);

        if(user.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"))){
            return true;
        }

        if(document.getUser().getId() == userId){
            return true;
        }

        return false;
    }

    public List<SimplifiedUser> getReaders (int page, long docId, int sharingType, int approval){
        Query query = entityManager.createNativeQuery("select u.first_name, u.surname FROM User u INNER JOIN users_documents ud " +
                "ON ud.user_id=u.id WHERE ud.document_id=? AND ud.sharing_type_id=? AND ud.approval=? ORDER BY u.surname");

        query.setParameter(1, docId);
        query.setParameter(2, sharingType);
        query.setParameter(3, approval);
        query.setMaxResults(PAGE_SIZE);
        query.setFirstResult((page-1) * PAGE_SIZE);

        List<Object[]> users = query.getResultList();
        List<SimplifiedUser> usersFinal = new ArrayList<>();

        for(Object[] o : users){
            SimplifiedUser u = new SimplifiedUser();
            u.setFirstName(o[0].toString());
            u.setSurname(o[1].toString());
            usersFinal.add(u);
        }
        return usersFinal;
    }

    public List<SimplifiedUser> getReadersWhoRead (int page, long docId){
        Query query = entityManager.createNativeQuery("select u.first_name, u.surname FROM User u INNER JOIN users_documents ud " +
                "ON ud.user_id=u.id WHERE ud.document_id=? AND ud.sharing_type_id=2 AND ud.approval=2");

        query.setParameter(1, docId);
        query.setMaxResults(PAGE_SIZE);
        query.setFirstResult((page-1) * PAGE_SIZE);

        List<Object[]> users = query.getResultList();
        List<SimplifiedUser> usersFinal = new ArrayList<>();

        for(Object[] o : users){
            SimplifiedUser u = new SimplifiedUser();
            u.setFirstName(o[0].toString());
            u.setSurname(o[1].toString());
            usersFinal.add(u);
        }
        return usersFinal;
    }

    @Override
    public UsersDocuments getUsersDocuments(long userId, long docId, long sharingTypeId){
        UserDocumentsId userDocumentsId = new UserDocumentsId(userId, docId, sharingTypeId);
        try {
            return entityManager.find(UsersDocuments.class, userDocumentsId);
        } catch (Exception e){
            return null;
        }
    }

}
