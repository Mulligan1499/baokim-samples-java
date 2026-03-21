package examples.direct;

import vn.baokim.b2b.*;
import vn.baokim.b2b.dto.*;
import vn.baokim.b2b.direct.BaokimDirect;
import vn.baokim.b2b.mastersub.BaokimOrder;

/**
 * Example: Tạo đơn hàng Direct Connection
 */
public class CreateOrder {
    public static void main(String[] args) {
        try {
            Config.load();
            
            // Sử dụng Direct credentials
            BaokimAuth auth = BaokimAuth.forDirectConnection();
            BaokimDirect directService = new BaokimDirect(auth.getToken());
            
            String mrcOrderId = "DRT_" + System.currentTimeMillis();
            
            DirectCreateOrderRequest request = new DirectCreateOrderRequest();
            request.setMrcOrderId(mrcOrderId);
            request.setTotalAmount(100000);
            request.setDescription("Test Direct Order");
            request.setCustomerInfo(new CustomerInfo("NGUYEN VAN A", "test@example.com", "0901234567", "123 Test Street"));
            
            BaokimOrder.ApiResponse result = directService.createOrder(request);
            
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
