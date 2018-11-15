package com.library.base.utils;

import com.blankj.utilcode.util.LogUtils;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密工具类
 */
public class AESUtils {

    public static final String AES_TYPE = "AES/CBC/PKCS5Padding";
    private static final char HEX_DIGITS[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * AES加密
     * @param key 需要传入长度为16的密钥
     * @param data
     * @return
     */
    public static String encrypt(final byte[] key, final String data){
        byte[] encryptResult = symmetricTemplate(data.getBytes(), key, true);
        if (encryptResult != null) {
            return bytes2HexString(encryptResult);
        }
        return "";
    }

    /**
     * AES解密
     * @param key 需要传入长度为16的密钥
     * @param data
     * @return
     */
    public static String decrypt(final byte[] key, final String data) {
        byte[] decryptResult = symmetricTemplate(hexString2Bytes(data), key, false);
        if (decryptResult != null){
            return new String(decryptResult);
        }
        return "";
    }

    private static byte[] symmetricTemplate(final byte[] data,
                                            final byte[] key,
                                            final boolean isEncrypt) {
        if (data == null || data.length == 0 || key == null || key.length == 0) return null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            AlgorithmParameterSpec params = new IvParameterSpec(new byte[cipher.getBlockSize()]);
            cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keySpec, params);
            return cipher.doFinal(data);
        } catch (Throwable e) {
            LogUtils.e(e.toString());
            return null;
        }
    }

//    private static byte[] generatorKey(String key){
//        KeyGenerator.getInstance()
//        return toByte(key);
//    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];

        for(int i = 0; i < len; ++i) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }

        return result;
    }

    /**
     * Bytes to hex string.
     * <p>e.g. bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns "00A8"</p>
     *
     * @param bytes The bytes.
     * @return hex string
     */
    public static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) return "";
        int len = bytes.length;
        if (len <= 0) return "";
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = HEX_DIGITS[bytes[i] >>> 4 & 0x0f];
            ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * Hex string to bytes.
     * <p>e.g. hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }</p>
     *
     * @param hexString The hex string.
     * @return the bytes
     */
    public static byte[] hexString2Bytes(String hexString) {
        if (isSpace(hexString)) return null;
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len = len + 1;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) (hex2Int(hexBytes[i]) << 4 | hex2Int(hexBytes[i + 1]));
        }
        return ret;
    }

    private static int hex2Int(final char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            return hexChar - 'A' + 10;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
