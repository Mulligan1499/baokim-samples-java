package vn.baokim.b2b.examples;

import vn.baokim.b2b.*;

/**
 * Ví dụ 5: Tạo Virtual Account
 */
public class CreateVA {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Tạo Virtual Account ===\n");
        
        try {
            Config.load("");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimVA vaService = new BaokimVA(auth);
            
            // ==== Tạo Dynamic VA ====
            System.out.println("=== Tạo Dynamic VA (1 lần dùng) ===\n");
            
            String mrcOrderId = "VA_DYN_" + System.currentTimeMillis();
            int amount = 500000;
            
            System.out.println("Mã đơn hàng: " + mrcOrderId);
            System.out.println("Số tiền: " + String.format("%,d", amount) + " VND");
            System.out.println("Đang tạo VA...\n");
            
            BaokimOrder.ApiResponse result = vaService.createDynamicVA(
                "NGUYEN VAN A", mrcOrderId, amount, "Thanh toan don hang " + mrcOrderId
            );
            
            System.out.println("Success: " + (result.success ? "TRUE" : "FALSE"));
            System.out.println("Code: " + result.code + " - " + ErrorCode.getMessage(result.code) + "\n");
            
            if (result.success) {
                System.out.println("Thông tin VA:");
                System.out.println("   VA Number: " + result.data.get("acc_no").getAsString());
                System.out.println("   Ngân hàng: " + result.data.get("bank_name").getAsString());
                System.out.println("   Tên TK: " + result.data.get("acc_name").getAsString());
                System.out.println("   Số tiền: " + String.format("%,d", result.data.get("user_amount").getAsInt()) + " VND");
                System.out.println("   Hết hạn: " + result.data.get("expire_date").getAsString());
                System.out.println("   QR: " + result.data.get("qr_path").getAsString());
            }
            
            System.out.println("\n=== HOÀN THÀNH ===");
            
        } catch (Exception e) {
            System.err.println("\n!!! LỖI !!!");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
