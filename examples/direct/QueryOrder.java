package examples.direct;

import vn.baokim.b2b.*;
import vn.baokim.b2b.direct.BaokimDirect;

/**
 * Example: Tra cứu đơn hàng Direct
 */
public class QueryOrder {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java QueryOrder <mrc_order_id>");
            return;
        }
        
        try {
            Config.load();
            
            BaokimAuth auth = BaokimAuth.forDirectConnection();
            BaokimDirect directService = new BaokimDirect(auth);
            
            BaokimOrder.ApiResponse result = directService.queryOrder(args[0]);
            
            if (result.success) {
                System.out.println("✅ Tra cứu thành công!");
                System.out.println("   Order: " + result.data.toString());
            } else {
                System.out.println("❌ Lỗi: " + result.message);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
