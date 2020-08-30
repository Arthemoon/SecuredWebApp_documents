package com.patrikmaryska.bc_prace.bc_prace.service;

import com.patrikmaryska.bc_prace.bc_prace.email.EmailService;
import com.patrikmaryska.bc_prace.bc_prace.model.*;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.DocumentBearer;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.SimplifiedUser;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.UsersApproval;
import com.patrikmaryska.bc_prace.bc_prace.utils.DocumentComparator;
import com.patrikmaryska.bc_prace.bc_prace.utils.EncrypterDecrypter;
import com.patrikmaryska.bc_prace.bc_prace.repository.DocumentRepository;
import javassist.NotFoundException;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @Value("${pdf.key}")
    private String key;

    @Transactional(noRollbackFor = TransactionSystemException.class)
    public void blockDocument(Document document){
        documentRepository.blockDocument(document);
    }

    public List<Document> getAllDocuments() {

        return documentRepository.findAll();
    }

    public List<Document> getAllUsersDocuments(long id) {
        return documentRepository.getAllUsersDocuments(id);
    }

    public Optional<Document> getDocumentById(long id) {
        return documentRepository.findById(id);
    }

    public List<Document> getAllUsersDocumentsByEmail(String email, int page) {
        List<Document> dcs = documentRepository.getAllDocsByEmail(email, page);
        return dcs;
    }



    public String write(MultipartFile file, String fileType, Document document) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        createDirectory(date);
        String folder = System.getProperty("user.home");
        String generatedName = generateName();

        String encPath = folder + "\\pdf\\" + date + "/" + generatedName + ".pdf"; // .pdf

        encryptFile(file, encPath);

        document.setResourcePath(Paths.get(encPath).toString());
        document.setName(generatedName);

        return generatedName;
    }


    public Document getDocumentByName(String name) {
        return documentRepository.getDocumentByName(name);
    }

    private boolean createDirectory(String date) {
        boolean success = new java.io.File(System.getProperty("user.home"), "pdf\\" + date).mkdirs();

        return success;
    }

    public void saveDocument(Document document) throws PersistenceException {
        documentRepository.saveDocument(document);
    }

    public String generateName() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public void insertSharing(String name, List<User> users, long documentTypeId, boolean emailSent, Date appTime) {
        documentRepository.insertSharing(name, users, documentTypeId, emailSent, appTime);
    }

    public void updateSharing(long userId, long documentId, int approval) {
        Optional<Document> optionalDocument = documentRepository.findById(documentId);
        User user = userService.findUserById(userId);

        if(optionalDocument.isPresent()){
            Document document = optionalDocument.get();

            if(document.getDocumentState().getId() == 2){
                if(documentRepository.getUsersDocuments(userId, documentId, 1) == null){
                    return;
                }

                documentRepository.updateSharing(userId, documentId, approval);

                if(approval == 0){
                    List<User> users = userService.getUsersForApprovingDocument(documentId);
                    Map<String, String> map = emailService.createMessage(2, user, document);

                    if(document.getUser().isActive()){
                        users.add(document.getUser());
                    }

                    setDocumentType(documentId, 3);

                    sendEmail(users, map.get("subject"), map.get("message"));

                } else if(isDocumentApproved(documentId)){
                    setDocumentType(documentId, 1);

                    List<User> users = userService.getUsersForApprovingDocument(documentId);
                    if(document.getUser().isActive()){
                        users.add(document.getUser());
                    }

                    Map<String, String> map = emailService.createMessage(5, user, document);
                    sendEmail(users, map.get("subject"), map.get("message"));
                }
            } else if(document.getDocumentState().getId() == 1){
                if(documentRepository.getUsersDocuments(userId, documentId, 2) == null){
                    return;
                }
                documentRepository.updateSharing(userId, documentId, approval);
            } else {
                return;
            }
        }
    }



    private void encryptFile(MultipartFile file, String name) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
            SecretKey secretKey = EncrypterDecrypter.getSecretKey(key);
            EncrypterDecrypter encrypterDecrypter
                    = new EncrypterDecrypter(secretKey, "AES/CBC/PKCS5Padding");
            encrypterDecrypter.encrypt(name, file);

    }

    public byte[] decryptFile(String name) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        SecretKey secretKey = EncrypterDecrypter.getSecretKey(key);
        EncrypterDecrypter encrypterDecrypter
                = new EncrypterDecrypter(secretKey, "AES/CBC/PKCS5Padding");

        return encrypterDecrypter.decrypt(name);
    }

    private void setDocumentType(long docId, long documentTypeId){
        DocumentState documentState = documentRepository.getDocumentState(documentTypeId);
        Optional<Document> updatedDocument = documentRepository.findById(docId);
        if(updatedDocument.isPresent()){
            Document document = updatedDocument.get();
            document.setDocumentState(documentState);
            documentRepository.updateDocument(document);
        }
    }

    public void sendEmail(List<User> users, String subject, String message){
        users.forEach(user -> {
            emailService.sendSimpleMessage(user.getEmail(), subject, message);
        });
    }

    public boolean hasUserAccessToDocument(long documentId, long userId){
        return documentRepository.hasUserAccessToDocument(documentId, userId);
    }

    public List<Document> findDocumentByTitle(String name, String email, int page) throws NotFoundException {
        List<Document> documents = new ArrayList<>();
        if(name.trim().length() > 0 && name.length() <= 12){
            Optional<User> opt = userService.getUserByEmail(email);
            if(opt.isPresent()){
                long id = opt.get().getId();
                documents = documentRepository.findDocumentByTitle(name, id, page);
            } else {
                throw new NotFoundException("User was not found.");
            }
        }
        return documents;
    }

    public List<Document> getUsersCreatedDocuments(long id, int year, int month, int pageNumber) {
        List<Document> documents = documentRepository.getUsersCreatedDocuments(id, year, month, pageNumber);

        return documents;
    }

    public List<UsersApproval> getUsersForDocument(long docId, long userId) {
        return documentRepository.getUsersForDocument(docId, userId);
    }


    public boolean isDocumentApproved(long docId){
        if(documentRepository.getExpectedCountOfApprovals(docId) == documentRepository.getCountOfApprovals(docId)){
            return true;
        }
        return false;
    }

    public DocumentState getDocumentState(long id){
        return documentRepository.getDocumentState(id);
    }

    public List<Document> getAllPassedDocuments() {
        return documentRepository.getAllPassedDocuments();
    }

    public List<Document> getDocumentsByYearAndByMonth(int year, int month, String email, int page) {

        List<Document> docs =  documentRepository.getDocumentsByYearAndMonth(year, month, email, page);
    //    docs.sort(new DocumentComparator(userService.getUserByEmail(email).get()));
        return docs;
    }

    public List<UsersDocuments> getNewActiveDocuments() {
        return documentRepository.getNewActiveDocuments();
    }

    public void updateEmailSent(UsersDocuments usersDocuments){
        documentRepository.updateEmailSent(usersDocuments);
    }


    public boolean documentDatesValidity(String approvalTime, String startOfReading, String endOfReading) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            if(formatter.parse(approvalTime).after(formatter.parse(formatter.format(new Date()))) ||
                    formatter.parse(approvalTime).equals(formatter.parse(formatter.format(new Date())))){
                if(formatter.parse(startOfReading).after(formatter.parse(formatter.format(new Date())))
                        && formatter.parse(endOfReading).after(formatter.parse(formatter.format(new Date()))))
                    if(formatter.parse(endOfReading).after(formatter.parse(startOfReading))){
                        return true;
                    }
            }
        }catch (ParseException e){
            return false;
        }
        return false;
    }

    private boolean checkIfArraysAreTheSame(String[] array1, String[] array2){
        return Arrays.equals(array1, array2);
    }

    public boolean checkDocumentValidity(String title, String desc, String approvalGroup, String readers) {
        if(title.length() < 3 || title.length() > 15 || title.trim().length() == 0 && !title.matches("^[a-zá-žA-ZÁ-Ž0-9\\)\\(\\!\\?\\s]+$") &&
                desc.length() <= 3 || desc.length() > 255 || desc.trim().length() == 0 &&
                 approvalGroup.trim().length() == 0 && !approvalGroup.matches("\\d,|\\d") && readers.trim().length() == 0 && !readers.matches("\\d,|\\d")
                && readers.equals(approvalGroup) && checkIfArraysAreTheSame(approvalGroup.split(","), readers.split(","))){
            return false;
        }
        return true;
    }

    public boolean checkFileValidity(MultipartFile multipartFile) {
        try {
            if(!multipartFile.isEmpty())
            {
                Tika tika = new Tika();
                String detectedType = tika.detect(multipartFile.getBytes());
                if (detectedType.equals("application/pdf")) {
                    return true;
                }
            }
        } catch (IOException e){
            return false;
        }
        return false;
    }

    public boolean deleteDocument(long id){
        boolean x = false;
        Document document = documentRepository.getOne(id);
        if(deleteDocumentFromDisk(document.getResourcePath())){
            documentRepository.deleteAllUsersDocumentsByDocumentId(id);
            x =  documentRepository.deleteDocument(id);
        }
        return x;
    }

    private boolean deleteDocumentFromDisk(String path){
        File file = new File(path);
        return file.delete();
    }


    public List<Document> getAllDocumentsByYearAndMonth(int year, int month, int page, String email){
        List<Document> documents = documentRepository.getAllDocumentsByYearAndMonth(year, month, page);

        return documents;

    }

    public List<Long> getLongsFromString(String value){
        String[] values = value.split(",");

        List<Long> longs = new ArrayList<>();

        for(int i = 0; i < values.length; i++){
            longs.add(Long.parseLong(values[i]));
        }

        return longs;
    }

    public void createDocument(String readers, String approvalGroup, String userEmail, String title, String desc,
                               String approvalTime, String startOfReading, String endOfReading, MultipartFile file) throws ParseException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {

        List<User> approvalUsers = userService.getUsersFromGroupIds(getLongsFromString(approvalGroup));
        List<User> usersForReading;

        List<Long> ids  = getLongsFromString(readers);
        if(ids.contains(0L)){
            usersForReading = userService.getAllUsers();
        } else {
            usersForReading = userService.getUsersFromGroupIds(ids);
        }

        usersForReading.removeAll(approvalUsers);

        Optional<User> user = userService.getUserByEmail(userEmail);
        Document document = new Document(title, desc);
        document.setUploadDatetime(new Date());
        document.setDocumentState(getDocumentState(2));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        document.setApprovalEndTime(formatter.parse(approvalTime));
        document.setActiveStartTime(formatter.parse(startOfReading));
        document.setActiveEndTime(formatter.parse(endOfReading));

        String name = "";

        if(user.isPresent()){
                document.setUser(user.get());
                try {
                    name = write(file, file.getName() + ".pdf", document);
                } catch(IOException e){
                    throw new IOException("Document error while saving on the disk...");
                }

                try {
                    saveDocument(document);
                }catch (PersistenceException e){
                    deleteDocumentFromDisk(document.getResourcePath());
                    return;
                }
        }

        Document finDoc = getDocumentByName(document.getName());

        insertSharing(name, approvalUsers, 1, true, document.getUploadDatetime());
        insertSharing(name, usersForReading, 2, false, document.getActiveStartTime());

        Document document2 =getDocumentByName(name);
        Map<String,String> map = emailService.createMessage(3, document2.getUser(), document2);
        sendEmail( userService.getUsersForApprovingDocument(document2.getId()), map.get("subject"), map.get("message"));
    }

    public List<SimplifiedUser> getReaders(int page, long docId, int st, int approval){
        return documentRepository.getReaders(page, docId, st, approval);
    }

    public void generateReport(String resourcePath, HttpServletResponse response) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        byte[] fileContent =  decryptFile(resourcePath);
        streamReport(response, fileContent, resourcePath);
    }

    public void streamReport(HttpServletResponse response, byte[] data, String name)
            throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=" + name);
        response.setContentLength(data.length);

        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    public boolean hasUserPermissionToAccessDocument(long documentId, long userId) {
        return documentRepository.hasUserPermissionToAccessDocument(documentId, userId);
    }
}
