package com.tummosoft;

import android.content.Context;
import android.util.Base64;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.streams.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Math.random;
import static java.lang.StrictMath.random;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

@BA.ShortName("xEncrypter")
public class xEncrypter {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static SecretKey _secretKey = null;

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keyGen.init(128, random);
        _secretKey = keyGen.generateKey();

        return _secretKey;
    }

    public byte[] getSecretKey() {
        return _secretKey.getEncoded();
    }
    
     public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

  
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    
    public static String encrypt(SecretKey secretKey, String PASSWORD) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(PASSWORD.getBytes());
            return Base64.encodeToString(encryptedData, Base64.DEFAULT);
        } catch (Exception e) {
            BA.LogError(e.getMessage());
            return null;
        }
    }

    public static byte[] encrypt2(SecretKey secretKey, String PASSWORD) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(PASSWORD.getBytes());
            return encryptedData;
        } catch (Exception e) {
            BA.LogError(e.getMessage());
            return null;
        }
    }

    public static String decrypt(SecretKey secretKey, String PASSWORD) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = cipher.doFinal(Base64.decode(PASSWORD, Base64.DEFAULT));
            return new String(decryptedData);
        } catch (Exception e) {
            BA.LogError(e.getMessage());
            return null;
        }
    }
    
   static public void backupDatabase(Context context, String dbName, String backupPath) {
    java.io.File dbFile = context.getDatabasePath(dbName);
    java.io.File backupFile = new java.io.File(backupPath);
    
    try {
        FileChannel source = new FileInputStream(dbFile).getChannel();
        FileChannel destination = new FileOutputStream(backupFile).getChannel();
        destination.transferFrom(source, 0, source.size());
        
        source.close();
        destination.close();
        
        BA.Log("Database backup successful.");
    } catch (IOException e) {        
        BA.Log("Database backup failed.");
    }
}
   static public void restoreDatabase(Context context, String dbName, String backupPath) {
    java.io.File dbFile = context.getDatabasePath(dbName);
    java.io.File backupFile = new java.io.File(backupPath);
    
    try {
        FileChannel source = new FileInputStream(backupFile).getChannel();
        FileChannel destination = new FileOutputStream(dbFile).getChannel();
        destination.transferFrom(source, 0, source.size());
        
        source.close();
        destination.close();
        
        BA.Log("Database restore successful.");
    } catch (IOException e) {
        e.printStackTrace();
         BA.Log("Database restore failed.");
    }
}
   
    static public void CopyResource(Context context, String sourcePath, String backupPath) {        
    java.io.File s = new java.io.File(sourcePath);
    java.io.File b = new java.io.File(backupPath);
    
    try {
        FileChannel source = new FileInputStream(s).getChannel();
        FileChannel destination = new FileOutputStream(b).getChannel();
        destination.transferFrom(source, 0, source.size());
        
        source.close();
        destination.close();
        
        BA.Log("Database restore successful.");
    } catch (IOException e) {
        e.printStackTrace();
         BA.LogError("Database restore failed.");
         BA.LogError(e.getMessage());
    }
}

    public static OutputStream createFileOutputStream(String filename, SecretKeySpec symetricKey)
            throws FileNotFoundException {
            
        java.io.File path = new java.io.File(filename);
        FileOutputStream fos = new FileOutputStream(path);
        if (symetricKey == null) {
            return fos;
        } else {
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, symetricKey);
                return new BufferedOutputStream(new CipherOutputStream(fos, cipher));
            } catch (InvalidKeyException e) {                
                BA.LogError("TYPE_ERROR_CRYPTO" + "Invalid key: " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            } catch (NoSuchAlgorithmException e) {                
                BA.LogError("TYPE_ERROR_CRYPTO" + "Unavailable Crypto algorithm: " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            } catch (NoSuchPaddingException e) {                
                BA.LogError("TYPE_ERROR_CRYPTO" + "Bad Padding: " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public static InputStream getFileInputStream(String filepath,
                                                 SecretKeySpec symetricKey) throws FileNotFoundException {
        java.io.File file = new java.io.File(filepath);
        InputStream is;
        try {
            is = new FileInputStream(file);
            if (symetricKey != null) {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, symetricKey);
                is = new BufferedInputStream(new CipherInputStream(is, cipher));
            }
            return is;
        } catch (InvalidKeyException | NoSuchPaddingException
                | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
