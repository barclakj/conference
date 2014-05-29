package com.stellarmap.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by barclakj on 29/05/2014.
 */
public class HashUtils {
    private static Logger log = Logger.getLogger(HashUtils.class.getCanonicalName());
    private static boolean HEX = true;

    public static char toHexChar(int i)
    {
        if ((0 <= i) && (i <= 9 ))
            return (char)('0' + i);
        else
            return (char)('a' + (i-10));
    }

    public static String byteToHex(byte data) {
        StringBuffer buf = new StringBuffer();
        buf.append(toHexChar((data>>>4)&0x0F));
        buf.append(toHexChar(data&0x0F));
        return buf.toString();
    }

    public static String bytesToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for ( int i = 0; i < data.length; i++ ) {
            buf.append( byteToHex(data[i]) );
        }
        return(buf.toString());
    }

    public static String hash(String message) {
        if (message==null) return null;

        MessageDigest digest = null;
        byte[] hash = null;
        String output = null;

        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(message.getBytes());
            hash = digest.digest();
            if (HEX) {
                output = new String(bytesToHex(hash));
            } else {
                output = new String(hash);
            }
        } catch (NoSuchAlgorithmException e) {
            log.log(Level.SEVERE, "HashUtils::hash(): Unable to hash message! No Algorithm! " + e.getMessage(), e);
            throw new RuntimeException("HashUtils::hash(): Unable to hash message! No Algorithm! " + e.getMessage(), e);
        } finally {
            digest = null;
            hash = null;
        }
        return output;
    }


}
