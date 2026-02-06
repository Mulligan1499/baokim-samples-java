package examples.direct;

import vn.baokim.b2b.*;
import vn.baokim.b2b.direct.BaokimDirect;

/**
 * Example: Hủy đơn hàng Direct
 */
public class CancelOrder {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java CancelOrder <mrc_order_id>");
            return;
        }
        
        try {
            Config.load();
            
            BaokimAuth auth = BaokimAuth.forDirectConnection();
            BaokimDirect directService = new BaokimDirect(auth);
            
            String reason = args.length > 1 ? args[1] : "Customer requested cancellation";
            BaokimOrder.ApiResponse result = directService.cancelOrder(args[0], reason);
            
            if (result.success) {
                System.out.println("✅ Hủy đơn thành công!");
            } else {
                System.out.println("❌ Lỗi: " + result.message);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
