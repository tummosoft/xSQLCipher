package com.tummosoft;

import android.util.Base64;
import anywheresoftware.b4a.BA;
import static java.lang.Math.random;
import static java.lang.StrictMath.random;
import java.security.SecureRandom;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
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

}
