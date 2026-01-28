/**
 * Ví dụ 1: Lấy Access Token
 * 
 * Compile: javac -cp .:../target/b2b-example-1.0.0.jar GetToken.java
 * Run: java -cp .:../target/b2b-example-1.0.0.jar GetToken
 */

import vn.baokim.b2b.*;

public class GetToken {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Lấy Access Token ===\n");
        
        try {
            Config.load("../");
            
            System.out.println("1. Config đã load từ: " + Config.get("base_url"));
            System.out.println("   Merchant Code: " + Config.get("merchant_code") + "\n");
            
            System.out.println("2. Đang gọi API lấy token...");
            
            BaokimAuth auth = new BaokimAuth();
            String token = auth.getToken();
            
            System.out.println("3. Lấy token thành công!\n");
            System.out.println("=== Thông tin Token ===");
            System.out.println("Access Token: " + token.substring(0, 50) + "...\n");
            
            System.out.println("=== HOÀN THÀNH ===");
            
        } catch (Exception e) {
            System.err.println("\n!!! LỖI !!!");
            System.err.println("Message: " + e.getMessage());
        }
    }
}
