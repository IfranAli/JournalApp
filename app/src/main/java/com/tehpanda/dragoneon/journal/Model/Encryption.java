package com.tehpanda.dragoneon.journal.Model;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public final class Encryption {
    private static byte[] GenKey(String password){
        byte[] passphrase = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            passphrase = digest.digest(password.getBytes());
            passphrase = Arrays.copyOf(passphrase, 16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passphrase;
    }

    public static String Encrypt(String data, String key) {
        try {
            // Create Key and chiper
            Key aesKey = new SecretKeySpec(GenKey(key), "AES");
            Cipher ciper = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Encrypt text
            ciper.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = ciper.doFinal(data.getBytes());
            byte[] base64encrypted = Base64.encode(encrypted, Base64.DEFAULT);
            return new String(base64encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String Decrypt(byte[] base64encrypted, String key) throws InvalidKeyException, BadPaddingException {
        try {
            // Get key
            Key aesKey = new SecretKeySpec(GenKey(key), "AES");
            // Decrypt base64 string
            byte[] base64decrypted = Base64.decode(base64encrypted, Base64.DEFAULT);
            // Decrypt text
            Cipher ciper = Cipher.getInstance("AES/ECB/PKCS5Padding");
            ciper.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decrypted = ciper.doFinal(base64decrypted);
            String s = new String(decrypted);
            return s;
        } catch (InvalidKeyException e){
            throw e;
        } catch (BadPaddingException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
