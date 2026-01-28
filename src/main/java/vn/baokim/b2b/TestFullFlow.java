package vn.baokim.b2b;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.text.NumberFormat;
import java.util.*;

/**
 * Test Full API Flow - Baokim B2B Java 7
 */
public class TestFullFlow {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       BAOKIM B2B API - FULL TEST FLOW (Java 7)           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        
        // Parse args
        String refundOrderId = args.length > 0 ? args[0] : null;
        Integer refundAmount = args.length > 1 ? Integer.parseInt(args[1]) : null;
        String autoDebitToken = args.length > 2 ? args[2] : null;
        
        Map<String, Object> results = new HashMap<String, Object>();
        String mrcOrderId = "";
        String vaNumber = null;
        String autoDebitOrderId = "";
        
        try {
            Config.load("");
            
            System.out.println("📌 Environment: " + Config.get("base_url"));
            System.out.println("📌 Merchant: " + Config.get("merchant_code") + "\n");
            
            // 1. GET TOKEN
            printSection(1, 8, "LẤY ACCESS TOKEN");
            BaokimAuth auth = new BaokimAuth();
            String token = auth.getToken();
            results.put("token", true);
            System.out.println("✅ Token: " + token.substring(0, 50) + "...\n");
            
            // 2. CREATE ORDER
            printSection(2, 8, "TẠO ĐƠN HÀNG THƯỜNG");
            BaokimOrder orderService = new BaokimOrder(auth);
            mrcOrderId = "TEST_" + System.currentTimeMillis() + "_" + new Random().nextInt(9999);
            int amount = 100000;
            
            Map<String, Object> orderData = new HashMap<String, Object>();
            orderData.put("mrcOrderId", mrcOrderId);
            orderData.put("totalAmount", amount);
            orderData.put("description", "Test order " + mrcOrderId);
            orderData.put("customerInfo", BaokimOrder.buildCustomerInfo(
                "Nguyen Van A", "test@example.com", "0901234567", "123 Test Street"
            ));
            
            BaokimOrder.ApiResponse orderResult = orderService.createOrder(orderData);
            results.put("createOrder", orderResult.success);
            
            if (orderResult.success) {
                System.out.println("✅ Tạo đơn thành công!");
                System.out.println("   Order ID: " + orderResult.data.get("order_id").getAsString());
                System.out.println("   MRC Order ID: " + mrcOrderId);
                System.out.println("   Amount: " + formatNumber(amount) + " VND");
                System.out.println("   Payment URL: " + orderResult.data.get("redirect_url").getAsString() + "\n");
            } else {
                System.out.println("❌ Lỗi: " + orderResult.message + "\n");
            }
            
            // 3. QUERY ORDER
            printSection(3, 8, "TRA CỨU ĐƠN HÀNG");
            BaokimOrder.ApiResponse queryResult = orderService.queryOrder(mrcOrderId);
            results.put("queryOrder", queryResult.success);
            
            if (queryResult.success) {
                JsonObject order = queryResult.data.getAsJsonObject("order");
                System.out.println("✅ Tra cứu thành công!");
                System.out.println("   Order ID: " + order.get("id").getAsString());
                int status = order.get("status").getAsInt();
                System.out.println("   Status: " + status + " (" + (status == 1 ? "Đã thanh toán" : "Chưa thanh toán") + ")");
                System.out.println("   Amount: " + formatNumber(order.get("total_amount").getAsInt()) + " VND\n");
            } else {
                System.out.println("❌ Lỗi: " + queryResult.message + "\n");
            }
            
            // 4. CREATE DYNAMIC VA
            printSection(4, 8, "TẠO DYNAMIC VA (Host to Host)");
            BaokimVA vaService = new BaokimVA(auth);
            String vaOrderId = "VA_" + System.currentTimeMillis() + "_" + new Random().nextInt(9999);
            int vaAmount = 100000;
            
            BaokimOrder.ApiResponse vaResult = vaService.createDynamicVA(
                "NGUYEN VAN A", vaOrderId, vaAmount, "Test VA " + vaOrderId
            );
            results.put("createVA", vaResult.success);
            
            if (vaResult.success) {
                vaNumber = vaResult.data.get("acc_no").getAsString();
                System.out.println("✅ Tạo VA thành công!");
                System.out.println("   VA Number: " + vaNumber);
                System.out.println("   Bank: " + vaResult.data.get("bank_name").getAsString());
                System.out.println("   Account Name: " + vaResult.data.get("acc_name").getAsString());
                System.out.println("   Amount: " + formatNumber(vaAmount) + " VND");
                System.out.println("   QR: " + vaResult.data.get("qr_path").getAsString() + "\n");
            } else {
                System.out.println("❌ Lỗi: " + vaResult.message + "\n");
            }
            
            // 5. QUERY VA TRANSACTION
            printSection(5, 8, "TRA CỨU GIAO DỊCH VA (bank-transfer/detail)");
            if (vaNumber != null) {
                BaokimOrder.ApiResponse vaQueryResult = vaService.queryTransaction(vaNumber);
                results.put("queryVA", vaQueryResult.success);
                
                if (vaQueryResult.success) {
                    JsonObject vaInfo = vaQueryResult.data.getAsJsonObject("va_info");
                    JsonArray transactions = vaQueryResult.data.getAsJsonArray("transactions");
                    System.out.println("✅ Tra cứu VA thành công!");
                    System.out.println("   Endpoint: /bank-transfer/detail");
                    System.out.println("   VA: " + vaInfo.get("acc_no").getAsString());
                    System.out.println("   Bank: " + vaInfo.get("bank_name").getAsString());
                    System.out.println("   Transactions: " + transactions.size() + "\n");
                } else {
                    System.out.println("❌ Lỗi: " + vaQueryResult.message + "\n");
                }
            } else {
                results.put("queryVA", false);
                System.out.println("⚠️ Bỏ qua vì không có VA number\n");
            }
            
            // 6. CREATE AUTO DEBIT ORDER
            printSection(6, 8, "TẠO ĐƠN THU HỘ TỰ ĐỘNG (payment_method=22)");
            autoDebitOrderId = "TT" + System.currentTimeMillis();
            
            Map<String, Object> autoDebitData = new HashMap<String, Object>();
            autoDebitData.put("mrcOrderId", autoDebitOrderId);
            autoDebitData.put("totalAmount", 0);
            autoDebitData.put("description", "Don hang Test " + autoDebitOrderId);
            autoDebitData.put("paymentMethod", BaokimOrder.PAYMENT_METHOD_AUTO_DEBIT);
            autoDebitData.put("serviceCode", "QL_THU_HO_1");
            autoDebitData.put("saveToken", 0);
            
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("code", "PROD001");
            item.put("name", "San pham A");
            item.put("amount", 0);
            item.put("quantity", 1);
            item.put("link", "https://example.com/product-a");
            items.add(item);
            autoDebitData.put("items", items);
            
            Map<String, Object> customerInfo = new HashMap<String, Object>();
            customerInfo.put("code", "KH01");
            customerInfo.put("name", "AUTOMATION TEST");
            customerInfo.put("email", "test@example.com");
            customerInfo.put("phone", "0911830977");
            customerInfo.put("address", "123 Nguyen Trai, Hanoi");
            customerInfo.put("gender", 1);
            autoDebitData.put("customerInfo", customerInfo);
            
            BaokimOrder.ApiResponse autoDebitResult = orderService.createOrder(autoDebitData);
            results.put("autoDebit", autoDebitResult.success);
            
            if (autoDebitResult.success) {
                System.out.println("✅ Tạo đơn Thu hộ tự động thành công!");
                System.out.println("   Order ID: " + autoDebitResult.data.get("order_id").getAsString());
                System.out.println("   MRC Order ID: " + autoDebitOrderId);
                System.out.println("   Payment Method: 22 (Thu hộ tự động)");
                System.out.println("   Redirect URL: " + autoDebitResult.data.get("redirect_url").getAsString() + "\n");
            } else {
                System.out.println("❌ Lỗi: " + autoDebitResult.message);
                System.out.println("   Code: " + autoDebitResult.code + "\n");
            }
            
            // 7. CANCEL AUTO DEBIT
            printSection(7, 8, "HỦY THU HỘ TỰ ĐỘNG");
            if (autoDebitToken != null) {
                System.out.println("   Token: " + autoDebitToken.substring(0, 20) + "...");
                BaokimOrder.ApiResponse cancelResult = orderService.cancelAutoDebit(autoDebitToken);
                results.put("cancelAutoDebit", cancelResult.success);
                
                if (cancelResult.success) {
                    System.out.println("✅ Hủy thu hộ tự động thành công!");
                    System.out.println("   Code: " + cancelResult.code);
                    System.out.println("   Message: " + cancelResult.message + "\n");
                } else {
                    System.out.println("❌ Lỗi: " + cancelResult.message + "\n");
                }
            } else {
                results.put("cancelAutoDebit", "skipped");
                System.out.println("⚠️ Để test hủy thu hộ tự động, chạy:");
                System.out.println("   java -jar target/b2b-example-1.0.0.jar ORDER_ID AMOUNT AUTO_DEBIT_TOKEN\n");
            }
            
            // 8. REFUND
            printSection(8, 8, "HOÀN TIỀN");
            if (refundOrderId != null && refundAmount != null) {
                System.out.println("   Order ID: " + refundOrderId);
                System.out.println("   Amount: " + formatNumber(refundAmount) + " VND");
                
                BaokimOrder.ApiResponse refundResult = orderService.refundOrder(refundOrderId, refundAmount, "Test refund");
                results.put("refund", refundResult.success);
                
                if (refundResult.success) {
                    System.out.println("✅ Hoàn tiền thành công!");
                    System.out.println("   Code: " + refundResult.code);
                    System.out.println("   Message: " + refundResult.message + "\n");
                } else {
                    System.out.println("❌ Lỗi: " + refundResult.message + "\n");
                }
            } else {
                results.put("refund", "skipped");
                System.out.println("⚠️ Để test refund, chạy:");
                System.out.println("   java -jar target/b2b-example-1.0.0.jar ORDER_ID AMOUNT\n");
            }
            
            // SUMMARY
            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                    TEST COMPLETED                        ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝\n");
            
            System.out.println("📋 Summary:");
            System.out.println("   [1] Token: ✅");
            System.out.println("   [2] Create Order: " + (Boolean.TRUE.equals(results.get("createOrder")) ? "✅" : "❌") + " (" + mrcOrderId + ")");
            System.out.println("   [3] Query Order: " + (Boolean.TRUE.equals(results.get("queryOrder")) ? "✅" : "❌"));
            System.out.println("   [4] Create VA (H2H): " + (Boolean.TRUE.equals(results.get("createVA")) ? "✅" : "❌") + (vaNumber != null ? " (" + vaNumber + ")" : ""));
            System.out.println("   [5] Query VA (H2H): " + (Boolean.TRUE.equals(results.get("queryVA")) ? "✅" : "❌"));
            System.out.println("   [6] Auto Debit Order: " + (Boolean.TRUE.equals(results.get("autoDebit")) ? "✅" : "❌") + " (" + autoDebitOrderId + ")");
            System.out.println("   [7] Cancel Auto Debit: " + ("skipped".equals(results.get("cancelAutoDebit")) ? "⏭️ Skipped" : (Boolean.TRUE.equals(results.get("cancelAutoDebit")) ? "✅" : "❌")));
            System.out.println("   [8] Refund: " + ("skipped".equals(results.get("refund")) ? "⏭️ Skipped" : (Boolean.TRUE.equals(results.get("refund")) ? "✅" : "❌")) + "\n");
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            System.out.println("📁 Log file: logs/api_" + sdf.format(new java.util.Date()) + ".log");
            
        } catch (Exception e) {
            System.err.println("\n❌ EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printSection(int num, int total, String title) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📍 [" + num + "/" + total + "] " + title);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    private static String formatNumber(int number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }
}
