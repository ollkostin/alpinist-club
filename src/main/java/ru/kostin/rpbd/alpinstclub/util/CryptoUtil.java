package ru.kostin.rpbd.alpinstclub.util;

import java.security.InvalidParameterException;
import java.security.MessageDigest;

public class CryptoUtil {
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfByte = (b >>> 4) & 0x0F;
            int twoHalfs = 0;
            do {
                buf.append((0 <= halfByte) && (halfByte <= 9) ? (char) ('0' + halfByte) : (char) ('a' + (halfByte - 10)));
                halfByte = b & 0x0F;
            } while (twoHalfs++ < 1);
        }
        return buf.toString();
    }

    public static String toSHA1(String text) {
        if (text == null) {
            throw new InvalidParameterException("Пароль не может быть пустым");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
