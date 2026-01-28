package vn.baokim.b2b.examples;

import vn.baokim.b2b.*;
import com.google.gson.JsonObject;

/**
 * Ví dụ 3: Tra Cứu Đơn Hàng
 */
public class QueryOrder {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Tra Cứu Đơn Hàng ===\n");
        
        // Lấy mã đơn hàng từ command line
        String mrcOrderId = args.length > 0 ? args[0] : "YOUR_ORDER_ID";
        
        System.out.println("Mã đơn hàng tra cứu: " + mrcOrderId);
        System.out.println("(Truyền mã đơn qua command line: java QueryOrder YOUR_ORDER_ID)\n");
        
        try {
            Config.load("");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimOrder orderService = new BaokimOrder(auth);
            
            System.out.println("Đang gọi API tra cứu...\n");
            
            BaokimOrder.ApiResponse result = orderService.queryOrder(mrcOrderId);
            
            System.out.println("=== Kết quả ===");
            System.out.println("Success: " + (result.success ? "TRUE" : "FALSE"));
            System.out.println("Code: " + result.code + " - " + ErrorCode.getMessage(result.code));
            System.out.println("Message: " + result.message + "\n");
            
            if (result.success && result.data != null) {
                JsonObject order = result.data.getAsJsonObject("order");
                System.out.println("=== Thông tin đơn hàng ===");
                System.out.println("Mã đơn BK: " + order.get("id").getAsString());
                System.out.println("Mã đơn MRC: " + order.get("mrc_order_id").getAsString());
                System.out.println("Số tiền: " + String.format("%,d", order.get("total_amount").getAsInt()) + " VND");
                int status = order.get("status").getAsInt();
                System.out.println("Trạng thái: " + (status == 1 ? "Đã thanh toán" : "Chưa thanh toán"));
                System.out.println("Ngày tạo: " + order.get("created_at").getAsString() + "\n");
            }
            
            System.out.println("=== HOÀN THÀNH ===");
            
        } catch (Exception e) {
            System.err.println("\n!!! LỖI !!!");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
