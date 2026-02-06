package examples.direct;

import vn.baokim.b2b.*;
import vn.baokim.b2b.direct.BaokimDirect;
import java.util.*;

/**
 * Example: Tạo đơn hàng Direct Connection
 */
public class CreateOrder {
    public static void main(String[] args) {
        try {
            Config.load();
            
            // Sử dụng Direct credentials
            BaokimAuth auth = BaokimAuth.forDirectConnection();
            BaokimDirect directService = new BaokimDirect(auth);
            
            String mrcOrderId = "DRT_" + System.currentTimeMillis();
            
            Map<String, Object> orderData = new HashMap<String, Object>();
            orderData.put("mrc_order_id", mrcOrderId);
            orderData.put("total_amount", 100000);
            orderData.put("description", "Test Direct Order");
            
            Map<String, Object> customerInfo = new HashMap<String, Object>();
            customerInfo.put("name", "NGUYEN VAN A");
            customerInfo.put("email", "test@example.com");
            customerInfo.put("phone", "0901234567");
            customerInfo.put("address", "123 Test Street");
            customerInfo.put("gender", 1);
            orderData.put("customer_info", customerInfo);
            
            BaokimOrder.ApiResponse result = directService.createOrder(orderData);
            
            if (result.success) {
                System.out.println("✅ Tạo đơn Direct thành công!");
                System.out.println("   Order ID: " + result.data.get("order_id").getAsString());
                System.out.println("   Payment URL: " + result.data.get("redirect_url").getAsString());
            } else {
                System.out.println("❌ Lỗi: " + result.message);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
