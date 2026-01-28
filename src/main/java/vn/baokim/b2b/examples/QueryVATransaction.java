package vn.baokim.b2b.examples;

import vn.baokim.b2b.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

/**
 * Ví dụ 6: Tra Cứu Giao Dịch VA
 */
public class QueryVATransaction {
    
    public static void main(String[] args) {
        System.out.println("=== Baokim B2B - Tra Cứu Giao Dịch VA ===\n");
        
        // Lấy số VA từ command line
        String accNo = args.length > 0 ? args[0] : null;
        
        if (accNo == null) {
            System.out.println("Sử dụng: java QueryVATransaction VA_NUMBER");
            return;
        }
        
        System.out.println("Số VA tra cứu: " + accNo + "\n");
        
        try {
            Config.load("");
            
            BaokimAuth auth = new BaokimAuth();
            BaokimVA vaService = new BaokimVA(auth);
            
            System.out.println("Đang tra cứu...\n");
            
            BaokimOrder.ApiResponse result = vaService.queryTransaction(accNo);
            
            System.out.println("Success: " + (result.success ? "TRUE" : "FALSE"));
            System.out.println("Code: " + result.code + "\n");
            
            if (result.success && result.data != null) {
                // VA Info
                JsonObject vaInfo = result.data.getAsJsonObject("va_info");
                System.out.println("=== Thông tin VA ===");
                System.out.println("Số VA: " + vaInfo.get("acc_no").getAsString());
                System.out.println("Ngân hàng: " + vaInfo.get("bank_name").getAsString());
                System.out.println("Tên TK: " + vaInfo.get("acc_name").getAsString());
                System.out.println("QR: " + vaInfo.get("qr_path").getAsString() + "\n");
                
                // Transactions
                JsonArray transactions = result.data.getAsJsonArray("transactions");
                System.out.println("=== Danh sách giao dịch ===");
                if (transactions.size() > 0) {
                    for (int i = 0; i < transactions.size(); i++) {
                        JsonObject tx = transactions.get(i).getAsJsonObject();
                        System.out.println("--- Giao dịch " + (i + 1) + " ---");
                        System.out.println("   ID: " + tx.get("id").getAsString());
                        System.out.println("   Số tiền: " + String.format("%,d", tx.get("amount").getAsInt()) + " VND");
                        int status = tx.get("status").getAsInt();
                        System.out.println("   Trạng thái: " + (status == 1 ? "Thành công" : "Chờ xử lý"));
                        System.out.println("   Thời gian: " + tx.get("created_at").getAsString());
                    }
                } else {
                    System.out.println("Chưa có giao dịch");
                }
            }
            
            System.out.println("\n=== HOÀN THÀNH ===");
            
        } catch (Exception e) {
            System.err.println("\n!!! LỖI !!!");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
