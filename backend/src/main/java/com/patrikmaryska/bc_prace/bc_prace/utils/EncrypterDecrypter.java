package com.patrikmaryska.bc_prace.bc_prace.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncrypterDecrypter {

    private SecretKey secretKey;
    private String transformation;
    private Cipher cipher;

    private Logger logger = LoggerFactory.getLogger(EncrypterDecrypter.class);

    public EncrypterDecrypter(SecretKey secretKey, String transformation) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.secretKey = secretKey;
        this.cipher = Cipher.getInstance(transformation);
    }

    public void encrypt(String fileName, MultipartFile file) throws InvalidKeyException, IOException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] iv = cipher.getIV();

        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {
            fileOut.write(iv);

            cipherOut.write(file.getBytes()); // file.getBytes() původně
        } catch (IOException e){
            throw new IOException("Error while processing file");
        }
    }

    public byte[] decrypt(String fileName) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        Path path = new File(fileName).toPath();
        try (FileInputStream fileIn = new FileInputStream(path.toFile())) {
            byte[] fileIv = new byte[16];
            fileIn.read(fileIv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));

            try (
                    CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
            ) {
                return cipherIn.readAllBytes();
            }
        } catch (InvalidKeyException e) {
            throw new InvalidKeyException("Invalid key exception");
        }
    }




    public static SecretKey getSecretKey(String key){
        byte[] decodedKey = Base64.getDecoder().decode(key);

        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        return originalKey;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }
}
