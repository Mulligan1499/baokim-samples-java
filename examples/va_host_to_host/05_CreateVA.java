/**
 * Ví dụ 5: Tạo Virtual Account
 * 
 * Run: java -cp .:../target/b2b-example-1.0.0.jar CreateVA
 */

import vn.baokim.b2b.*;
import vn.baokim.b2b.dto.*;
import vn.baokim.b2b.hosttohost.BaokimVA;
import vn.baokim.b2b.mastersub.BaokimOrder;

public class CreateVA {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Tạo Virtual Account ===\n");
        
        try {
            Config.load("../");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimVA vaService = new BaokimVA(auth.getToken());
            
            String mrcOrderId = "VA_" + System.currentTimeMillis();
            int amount = 500000;
            
            System.out.println("Mã đơn hàng: " + mrcOrderId);
            System.out.println("Số tiền: " + String.format("%,d", amount) + " VND\n");
            
            CreateVARequest request = new CreateVARequest();
            request.setAccName("NGUYEN VAN A");
            request.setMrcOrderId(mrcOrderId);
            request.setCollectAmountMin(amount);
            request.setCollectAmountMax(amount);
            request.setDescription("Thanh toan " + mrcOrderId);
            
            BaokimOrder.ApiResponse result = vaService.createDynamicVA(request);
            
            System.out.println("Success: " + result.success);
            System.out.println("Code: " + result.code);
            
            if (result.success) {
                System.out.println("VA Number: " + result.data.get("acc_no").getAsString());
                System.out.println("Bank: " + result.data.get("bank_name").getAsString());
                System.out.println("QR: " + result.data.get("qr_path").getAsString());
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
