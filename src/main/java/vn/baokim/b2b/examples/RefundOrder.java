package vn.baokim.b2b.examples;

import vn.baokim.b2b.*;

/**
 * Ví dụ 4: Hoàn Tiền Đơn Hàng
 */
public class RefundOrder {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Hoàn Tiền Đơn Hàng ===\n");
        
        // Lấy params từ command line
        String mrcOrderId = args.length > 0 ? args[0] : "YOUR_ORDER_ID";
        int amount = args.length > 1 ? Integer.parseInt(args[1]) : 0;
        String reason = args.length > 2 ? args[2] : "Hoan tien theo yeu cau";
        
        System.out.println("Mã đơn hàng: " + mrcOrderId);
        System.out.println("Số tiền hoàn: " + (amount > 0 ? String.format("%,d", amount) + " VND" : "Toàn bộ"));
        System.out.println("Lý do: " + reason + "\n");
        System.out.println("(Sử dụng: java RefundOrder ORDER_ID AMOUNT \"REASON\")\n");
        
        try {
            Config.load("");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimOrder orderService = new BaokimOrder(auth);
            
            System.out.println("Đang gọi API hoàn tiền...\n");
            
            BaokimOrder.ApiResponse result = orderService.refundOrder(mrcOrderId, amount, reason);
            
            System.out.println("=== Kết quả ===");
            System.out.println("Success: " + (result.success ? "TRUE" : "FALSE"));
            System.out.println("Code: " + result.code + " - " + ErrorCode.getMessage(result.code));
            System.out.println("Message: " + result.message + "\n");
            
            if (result.success) {
                System.out.println("✓ Hoàn tiền thành công!");
            } else {
                System.out.println("✗ Hoàn tiền thất bại!");
                System.out.println("Vui lòng kiểm tra lại mã đơn hàng và trạng thái đơn.");
            }
            
            System.out.println("\n=== HOÀN THÀNH ===");
            
        } catch (Exception e) {
            System.err.println("\n!!! LỖI !!!");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
