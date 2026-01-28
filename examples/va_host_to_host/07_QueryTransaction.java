/**
 * Ví dụ 7: Tra cứu giao dịch VA
 * 
 * Run: java -cp .:../target/b2b-example-1.0.0.jar QueryTransaction VA_NUMBER
 */

import vn.baokim.b2b.*;
import com.google.gson.*;

public class QueryTransaction {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Tra Cứu Giao Dịch VA ===\n");
        
        if (args.length == 0) {
            System.out.println("Sử dụng: java QueryTransaction VA_NUMBER");
            return;
        }
        
        String accNo = args[0];
        System.out.println("VA Number: " + accNo + "\n");
        
        try {
            Config.load("../");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimVA vaService = new BaokimVA(auth);
            
            BaokimOrder.ApiResponse result = vaService.queryTransaction(accNo);
            
            System.out.println("Success: " + result.success);
            System.out.println("Code: " + result.code);
            
            if (result.success && result.data != null) {
                JsonObject vaInfo = result.data.getAsJsonObject("va_info");
                System.out.println("VA: " + vaInfo.get("acc_no").getAsString());
                System.out.println("Bank: " + vaInfo.get("bank_name").getAsString());
                
                JsonArray txs = result.data.getAsJsonArray("transactions");
                System.out.println("Transactions: " + txs.size());
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
