package vn.baokim.b2b.examples;

import vn.baokim.b2b.*;
import java.util.*;

/**
 * Ví dụ 2: Tạo Đơn Hàng
 */
public class CreateOrder {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Tạo Đơn Hàng ===\n");
        
        try {
            Config.load("");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimOrder orderService = new BaokimOrder(auth);
            
            // Tạo mã đơn hàng unique
            String mrcOrderId = "ORDER_" + System.currentTimeMillis() + "_" + new Random().nextInt(9999);
            int amount = 350000;
            
            System.out.println("1. Chuẩn bị dữ liệu đơn hàng");
            System.out.println("   Mã đơn hàng: " + mrcOrderId);
            System.out.println("   Tổng tiền: " + String.format("%,d", amount) + " VND\n");
            
            System.out.println("2. Đang gọi API tạo đơn hàng...");
            
            Map<String, Object> orderData = new HashMap<String, Object>();
            orderData.put("mrcOrderId", mrcOrderId);
            orderData.put("totalAmount", amount);
            orderData.put("description", "Thanh toan don hang " + mrcOrderId);
            orderData.put("customerInfo", BaokimOrder.buildCustomerInfo(
                "Nguyen Van A", "nguyenvana@email.com", "0901234567", "123 Nguyen Hue, Q.1"
            ));
            
            BaokimOrder.ApiResponse result = orderService.createOrder(orderData);
            
            System.out.println("3. Kết quả:");
            System.out.println("   Success: " + (result.success ? "TRUE" : "FALSE"));
            System.out.println("   Code: " + result.code);
            System.out.println("   Message: " + result.message + "\n");
            
            if (result.success) {
                System.out.println("=== Thông tin thanh toán ===");
                System.out.println("Redirect URL: " + result.data.get("redirect_url").getAsString() + "\n");
            }
            
            System.out.println("=== HOÀN THÀNH ===");
            
        } catch (Exception e) {
            System.err.println("\n!!! LỖI !!!");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
