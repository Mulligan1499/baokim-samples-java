/**
 * Ví dụ 3: Tra Cứu Đơn Hàng
 * 
 * Run: java -cp .:../target/b2b-example-1.0.0.jar QueryOrder ORDER_ID
 */

import vn.baokim.b2b.*;
import com.google.gson.JsonObject;

public class QueryOrder {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Tra Cứu Đơn Hàng ===\n");
        
        String mrcOrderId = args.length > 0 ? args[0] : "YOUR_ORDER_ID";
        System.out.println("Mã đơn hàng: " + mrcOrderId + "\n");
        
        try {
            Config.load("../");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimOrder orderService = new BaokimOrder(auth);
            
            BaokimOrder.ApiResponse result = orderService.queryOrder(mrcOrderId);
            
            System.out.println("Success: " + result.success);
            System.out.println("Code: " + result.code);
            
            if (result.success && result.data != null) {
                JsonObject order = result.data.getAsJsonObject("order");
                System.out.println("Order ID: " + order.get("id").getAsString());
                System.out.println("Amount: " + order.get("total_amount").getAsInt());
                System.out.println("Status: " + order.get("status").getAsInt());
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
