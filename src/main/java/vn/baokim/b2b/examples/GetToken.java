package vn.baokim.b2b.examples;

import vn.baokim.b2b.*;

/**
 * Ví dụ 1: Lấy Access Token
 */
public class GetToken {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Lấy Access Token ===\n");
        
        try {
            Config.load("");
            
            System.out.println("1. Config đã load từ: " + Config.get("base_url"));
            System.out.println("   Merchant Code: " + Config.get("merchant_code") + "\n");
            
            System.out.println("2. Đang gọi API lấy token...");
            
            BaokimAuth auth = new BaokimAuth();
            String token = auth.getToken();
            
            System.out.println("3. Lấy token thành công!\n");
            System.out.println("=== Thông tin Token ===");
            System.out.println("Access Token: " + token.substring(0, 50) + "...");
            System.out.println("Authorization header: Bearer " + token + "\n");
            
            System.out.println("4. Thử lấy token lần 2 (sẽ dùng cache)...");
            String token2 = auth.getToken();
            System.out.println("   => Token được lấy từ cache (không gọi lại API)\n");
            
            System.out.println("=== HOÀN THÀNH ===");
            
        } catch (Exception e) {
            System.err.println("\n!!! LỖI !!!");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
