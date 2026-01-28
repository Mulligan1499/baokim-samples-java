/**
 * Ví dụ 2: Tạo Đơn Hàng
 * 
 * Compile: javac -cp .:../target/b2b-example-1.0.0.jar CreateOrder.java
 * Run: java -cp .:../target/b2b-example-1.0.0.jar CreateOrder
 */

import vn.baokim.b2b.*;
import java.util.*;

public class CreateOrder {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Tạo Đơn Hàng ===\n");
        
        try {
            Config.load("../");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimOrder orderService = new BaokimOrder(auth);
            
            String mrcOrderId = "ORDER_" + System.currentTimeMillis();
            int amount = 350000;
            
            System.out.println("Mã đơn hàng: " + mrcOrderId);
            System.out.println("Tổng tiền: " + String.format("%,d", amount) + " VND\n");
            
            Map<String, Object> orderData = new HashMap<String, Object>();
            orderData.put("mrcOrderId", mrcOrderId);
            orderData.put("totalAmount", amount);
            orderData.put("description", "Thanh toan don hang " + mrcOrderId);
            orderData.put("customerInfo", BaokimOrder.buildCustomerInfo(
                "Nguyen Van A", "test@email.com", "0901234567", "123 Street"
            ));
            
            BaokimOrder.ApiResponse result = orderService.createOrder(orderData);
            
            System.out.println("Success: " + result.success);
            System.out.println("Code: " + result.code);
            
            if (result.success) {
                System.out.println("Payment URL: " + result.data.get("redirect_url").getAsString());
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
