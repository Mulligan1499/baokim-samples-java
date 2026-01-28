package vn.baokim.b2b;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * SignatureHelper - Ký số RSA SHA256
 */
public class SignatureHelper {
    
    /**
     * Ký dữ liệu bằng private key
     */
    public static String sign(String data) throws Exception {
        return sign(data, null);
    }
    
    public static String sign(String data, String keyPath) throws Exception {
        String path = keyPath != null ? keyPath : Config.getBasePath() + Config.get("merchant_private_key_path");
        PrivateKey privateKey = loadPrivateKey(path);
        
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes("UTF-8"));
        
        byte[] signedBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signedBytes);
    }
    
    /**
     * Xác thực chữ ký
     */
    public static boolean verify(String data, String signatureStr) throws Exception {
        return verify(data, signatureStr, null);
    }
    
    public static boolean verify(String data, String signatureStr, String keyPath) throws Exception {
        String path = keyPath != null ? keyPath : Config.getBasePath() + Config.get("baokim_public_key_path");
        PublicKey publicKey = loadPublicKey(path);
        
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes("UTF-8"));
        
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signatureBytes);
    }
    
    /**
     * Load private key từ PEM file (hỗ trợ PKCS#1 và PKCS#8)
     */
    private static PrivateKey loadPrivateKey(String path) throws Exception {
        String keyContent = readPemFile(path);
        boolean isPkcs1 = keyContent.contains("BEGIN RSA PRIVATE KEY");
        
        keyContent = keyContent
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        
        if (isPkcs1) {
            // Convert PKCS#1 to PKCS#8 format
            keyBytes = convertPkcs1ToPkcs8(keyBytes);
        }
        
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
    
    /**
     * Convert PKCS#1 to PKCS#8 format
     */
    private static byte[] convertPkcs1ToPkcs8(byte[] pkcs1Bytes) throws Exception {
        // PKCS#8 header for RSA key
        byte[] pkcs8Header = new byte[] {
            0x30, (byte) 0x82, 0x00, 0x00, // SEQUENCE with length placeholder
            0x02, 0x01, 0x00,              // INTEGER version = 0
            0x30, 0x0d,                     // SEQUENCE
            0x06, 0x09,                     // OID
            0x2a, (byte) 0x86, 0x48, (byte) 0x86, (byte) 0xf7, 0x0d, 0x01, 0x01, 0x01, // rsaEncryption OID
            0x05, 0x00,                     // NULL
            0x04, (byte) 0x82, 0x00, 0x00  // OCTET STRING with length placeholder
        };
        
        int totalLen = pkcs8Header.length + pkcs1Bytes.length;
        byte[] pkcs8Bytes = new byte[totalLen];
        
        System.arraycopy(pkcs8Header, 0, pkcs8Bytes, 0, pkcs8Header.length);
        System.arraycopy(pkcs1Bytes, 0, pkcs8Bytes, pkcs8Header.length, pkcs1Bytes.length);
        
        // Fix lengths
        int innerLen = pkcs1Bytes.length;
        pkcs8Bytes[pkcs8Header.length - 2] = (byte) ((innerLen >> 8) & 0xff);
        pkcs8Bytes[pkcs8Header.length - 1] = (byte) (innerLen & 0xff);
        
        int outerLen = totalLen - 4;
        pkcs8Bytes[2] = (byte) ((outerLen >> 8) & 0xff);
        pkcs8Bytes[3] = (byte) (outerLen & 0xff);
        
        return pkcs8Bytes;
    }
    
    /**
     * Load public key từ PEM file
     */
    private static PublicKey loadPublicKey(String path) throws Exception {
        String keyContent = readPemFile(path);
        keyContent = keyContent
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
    
    /**
     * Đọc file PEM
     */
    private static String readPemFile(String path) throws Exception {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);
        fis.close();
        return new String(bytes, "UTF-8");
    }
}
