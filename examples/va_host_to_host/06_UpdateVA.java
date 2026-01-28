/**
 * Ví dụ 6: Cập nhật VA
 * 
 * Run: java -cp .:../target/b2b-example-1.0.0.jar UpdateVA VA_NUMBER
 */

import vn.baokim.b2b.*;
import java.util.*;

public class UpdateVA {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Cập nhật VA ===\n");
        
        if (args.length == 0) {
            System.out.println("Sử dụng: java UpdateVA VA_NUMBER");
            return;
        }
        
        String accNo = args[0];
        System.out.println("VA Number: " + accNo + "\n");
        
        try {
            Config.load("../");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimVA vaService = new BaokimVA(auth);
            
            Map<String, Object> updateData = new HashMap<String, Object>();
            updateData.put("acc_name", "NGUYEN VAN B");
            updateData.put("collect_amount_min", 200000);
            updateData.put("collect_amount_max", 200000);
            
            BaokimOrder.ApiResponse result = vaService.updateVA(accNo, updateData);
            
            System.out.println("Success: " + result.success);
            System.out.println("Code: " + result.code);
            System.out.println("Message: " + result.message);
            
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
