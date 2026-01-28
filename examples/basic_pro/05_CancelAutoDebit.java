/**
 * Ví dụ 5: Hủy Thu Hộ Tự Động
 * 
 * Run: java -cp .:../target/b2b-example-1.0.0.jar CancelAutoDebit TOKEN
 */

import vn.baokim.b2b.*;

public class CancelAutoDebit {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Hủy Thu Hộ Tự Động ===\n");
        
        if (args.length == 0) {
            System.out.println("Sử dụng: java CancelAutoDebit TOKEN");
            return;
        }
        
        String token = args[0];
        System.out.println("Token: " + token.substring(0, Math.min(20, token.length())) + "...\n");
        
        try {
            Config.load("../");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimOrder orderService = new BaokimOrder(auth);
            
            BaokimOrder.ApiResponse result = orderService.cancelAutoDebit(token);
            
            System.out.println("Success: " + result.success);
            System.out.println("Code: " + result.code);
            System.out.println("Message: " + result.message);
            
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
