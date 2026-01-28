/**
 * Ví dụ 4: Hoàn Tiền
 * 
 * Run: java -cp .:../target/b2b-example-1.0.0.jar RefundOrder ORDER_ID AMOUNT
 */

import vn.baokim.b2b.*;

public class RefundOrder {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Hoàn Tiền ===\n");
        
        String mrcOrderId = args.length > 0 ? args[0] : "YOUR_ORDER_ID";
        int amount = args.length > 1 ? Integer.parseInt(args[1]) : 0;
        
        System.out.println("Mã đơn hàng: " + mrcOrderId);
        System.out.println("Số tiền hoàn: " + amount + "\n");
        
        try {
            Config.load("../");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimOrder orderService = new BaokimOrder(auth);
            
            BaokimOrder.ApiResponse result = orderService.refundOrder(mrcOrderId, amount, "Hoan tien");
            
            System.out.println("Success: " + result.success);
            System.out.println("Code: " + result.code);
            System.out.println("Message: " + result.message);
            
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
