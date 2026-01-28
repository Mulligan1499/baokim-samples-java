package vn.baokim.b2b.examples;

import vn.baokim.b2b.*;

/**
 * Ví dụ 7: Hủy Thu Hộ Tự Động
 */
public class CancelAutoDebit {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Hủy Thu Hộ Tự Động ===\n");
        
        // Token từ webhook khi đăng ký thu hộ tự động thành công
        String autoDebitToken = args.length > 0 ? args[0] : null;
        
        if (autoDebitToken == null) {
            System.out.println("Sử dụng: java CancelAutoDebit YOUR_TOKEN");
            return;
        }
        
        System.out.println("Token thu hộ tự động: " + autoDebitToken.substring(0, Math.min(20, autoDebitToken.length())) + "...\n");
        
        try {
            Config.load("");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimOrder orderService = new BaokimOrder(auth);
            
            System.out.println("Đang gọi API hủy thu hộ tự động...\n");
            
            BaokimOrder.ApiResponse result = orderService.cancelAutoDebit(autoDebitToken);
            
            System.out.println("=== Kết quả ===");
            System.out.println("Success: " + (result.success ? "TRUE" : "FALSE"));
            System.out.println("Code: " + result.code + " - " + ErrorCode.getMessage(result.code));
            System.out.println("Message: " + result.message + "\n");
            
            if (result.success) {
                System.out.println("✓ Hủy thu hộ tự động thành công!");
            } else {
                System.out.println("✗ Hủy thu hộ tự động thất bại!");
                System.out.println("Vui lòng kiểm tra lại token.");
            }
            
            System.out.println("\n=== HOÀN THÀNH ===");
            
        } catch (Exception e) {
            System.err.println("\n!!! LỖI !!!");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
