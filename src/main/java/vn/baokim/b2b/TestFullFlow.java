package vn.baokim.b2b;


import java.text.SimpleDateFormat;
import java.util.*;
import vn.baokim.b2b.dto.*;
import vn.baokim.b2b.mastersub.BaokimOrder;
import vn.baokim.b2b.hosttohost.BaokimVA;
import vn.baokim.b2b.direct.BaokimDirect;

/**
 * Test Full API Flow - Baokim B2B Java
 * 
 * Unified test script supporting multiple connection types:
 * - basic_pro: MasterSub Order APIs (Create, Query, Auto Debit)
 * - host_to_host: VA APIs (Create Dynamic/Static VA, Query)
 * - direct: Direct Order APIs (Create, Query, Cancel)
 * 
 * Usage:
 * mvn exec:java -Dexec.mainClass="vn.baokim.b2b.TestFullFlow"
 * [-Dexec.args="connection_type"]
 * 
 * Examples:
 * mvn exec:java -Dexec.mainClass="vn.baokim.b2b.TestFullFlow" # Run all
 * mvn exec:java -Dexec.mainClass="vn.baokim.b2b.TestFullFlow"
 * -Dexec.args="basic_pro"
 * mvn exec:java -Dexec.mainClass="vn.baokim.b2b.TestFullFlow"
 * -Dexec.args="host_to_host"
 * mvn exec:java -Dexec.mainClass="vn.baokim.b2b.TestFullFlow"
 * -Dexec.args="direct"
 */
public class TestFullFlow {

    public static void main(String[] args) {
        // Parse CLI arguments
        String connectionType = (args.length > 0) ? args[0].toLowerCase() : "all";
        List<String> validTypes = Arrays.asList("all", "basic_pro", "host_to_host", "direct");

        if (!validTypes.contains(connectionType)) {
            System.out.println("❌ Invalid connection type: " + connectionType + "\n");
            System.out.println("Usage: java TestFullFlow [connection_type]\n");
            System.out.println("Valid types:");
            System.out.println("  all          - Run all tests (default)");
            System.out.println("  basic_pro    - Test MasterSub Order APIs");
            System.out.println("  host_to_host - Test Host-to-Host VA APIs");
            System.out.println("  direct       - Test Direct Order APIs");
            System.exit(1);
        }

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       BAOKIM B2B API - FULL TEST FLOW (Java)             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        Map<String, Map<String, Boolean>> results = new HashMap<String, Map<String, Boolean>>();
        results.put("basic_pro", new LinkedHashMap<String, Boolean>());
        results.put("host_to_host", new LinkedHashMap<String, Boolean>());
        results.put("direct", new LinkedHashMap<String, Boolean>());

        try {
            Config.load("");

            System.out.println("📌 Environment: " + Config.get("base_url"));
            System.out.println("📌 Connection Type: " + connectionType.toUpperCase() + "\n");

            // Get Token (shared)
            BaokimAuth auth = new BaokimAuth();
            String token = auth.getToken();
            System.out.println("✅ Token acquired successfully\n");

            // ============================================================
            // BASIC/PRO TESTS
            // ============================================================
            if (connectionType.equals("all") || connectionType.equals("basic_pro")) {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("🔷 BASIC/PRO (MasterSub) TESTS");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

                BaokimOrder orderService = new BaokimOrder(token);
                String mrcOrderId = "TEST_" + System.currentTimeMillis() + "_" + new Random().nextInt(9999);

                // Create Order
                CreateOrderRequest orderRequest = new CreateOrderRequest();
                orderRequest.setMrcOrderId(mrcOrderId);
                orderRequest.setTotalAmount(100000);
                orderRequest.setDescription("Test order " + mrcOrderId);
                orderRequest.setCustomerInfo(new CustomerInfo("NGUYEN VAN A", "test@example.com", "0901234567", "123 Test Street"));

                BaokimOrder.ApiResponse orderResult = orderService.createOrder(orderRequest);
                results.get("basic_pro").put("create_order", orderResult.success);
                System.out.println(
                        "   Create Order: " + (orderResult.success ? "✅ " + mrcOrderId : "❌ " + orderResult.message));

                // Query Order
                BaokimOrder.ApiResponse queryResult = orderService.queryOrder(mrcOrderId);
                results.get("basic_pro").put("query_order", queryResult.success);
                System.out.println("   Query Order: " + (queryResult.success ? "✅" : "❌ " + queryResult.message));

                // Auto Debit Order
                String autoDebitOrderId = "TT" + System.currentTimeMillis();
                CreateOrderRequest autoDebitRequest = new CreateOrderRequest();
                autoDebitRequest.setMrcOrderId(autoDebitOrderId);
                autoDebitRequest.setTotalAmount(0);
                autoDebitRequest.setDescription("Auto debit " + autoDebitOrderId);
                autoDebitRequest.setPaymentMethod(BaokimOrder.PAYMENT_METHOD_AUTO_DEBIT);
                autoDebitRequest.setServiceCode("QL_THU_HO_1");
                autoDebitRequest.setCustomerInfo(new CustomerInfo("NGUYEN VAN A", "test@example.com", "0901234567", "123 Test Street"));

                BaokimOrder.ApiResponse autoDebitResult = orderService.createOrder(autoDebitRequest);
                results.get("basic_pro").put("auto_debit", autoDebitResult.success);
                System.out.println("   Auto Debit: "
                        + (autoDebitResult.success ? "✅ " + autoDebitOrderId : "❌ " + autoDebitResult.message) + "\n");
            }

            // ============================================================
            // HOST-TO-HOST TESTS
            // ============================================================
            if (connectionType.equals("all") || connectionType.equals("host_to_host")) {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("🔷 HOST-TO-HOST (VA) TESTS");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

                BaokimVA vaService = new BaokimVA(token);

                // Create Dynamic VA
                String vaOrderId = "DVA" + (System.currentTimeMillis() % 10000000000L) + new Random().nextInt(999);
                CreateVARequest dynamicVARequest = new CreateVARequest();
                dynamicVARequest.setAccName("NGUYEN VAN A");
                dynamicVARequest.setMrcOrderId(vaOrderId);
                dynamicVARequest.setCollectAmountMin(100000);
                dynamicVARequest.setCollectAmountMax(100000);

                BaokimOrder.ApiResponse vaResult = vaService.createDynamicVA(dynamicVARequest);
                results.get("host_to_host").put("dynamic_va", vaResult.success);
                String vaNumber = vaResult.success && vaResult.data != null ? vaResult.data.get("acc_no").getAsString()
                        : null;
                System.out.println("   Dynamic VA: " + (vaResult.success ? "✅ " + vaNumber : "❌ " + vaResult.message));

                // Create Static VA
                String staticOrderId = "SVA" + (System.currentTimeMillis() % 10000000000L) + new Random().nextInt(999);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 30);
                String expireDate = sdf.format(cal.getTime());

                CreateVARequest staticVARequest = new CreateVARequest();
                staticVARequest.setAccName("TRAN VAN B");
                staticVARequest.setMrcOrderId(staticOrderId);
                staticVARequest.setExpireDate(expireDate);
                staticVARequest.setCollectAmountMin(10000);
                staticVARequest.setCollectAmountMax(10000000);

                BaokimOrder.ApiResponse staticResult = vaService.createStaticVA(staticVARequest);
                results.get("host_to_host").put("static_va", staticResult.success);
                String staticVaNumber = staticResult.success && staticResult.data != null
                        ? staticResult.data.get("acc_no").getAsString()
                        : null;
                System.out.println("   Static VA: "
                        + (staticResult.success ? "✅ " + staticVaNumber : "❌ " + staticResult.message));

                // Query VA
                if (vaNumber != null) {
                    BaokimOrder.ApiResponse queryVaResult = vaService.queryTransaction(vaNumber);
                    results.get("host_to_host").put("query_va", queryVaResult.success);
                    System.out.println(
                            "   Query VA: " + (queryVaResult.success ? "✅" : "❌ " + queryVaResult.message) + "\n");
                } else {
                    results.get("host_to_host").put("query_va", false);
                    System.out.println("   Query VA: ⏭️ Skipped\n");
                }
            }

            // ============================================================
            // DIRECT TESTS
            // ============================================================
            if (connectionType.equals("all") || connectionType.equals("direct")) {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("🔷 DIRECT CONNECTION TESTS");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

                // Direct connection uses different credentials
                BaokimAuth directAuth = BaokimAuth.forDirectConnection();
                BaokimDirect directService = new BaokimDirect(directAuth.getToken());
                String directOrderId = "DRT" + (System.currentTimeMillis() % 10000000000L) + new Random().nextInt(999);

                // Create Order
                DirectCreateOrderRequest directOrderRequest = new DirectCreateOrderRequest();
                directOrderRequest.setMrcOrderId(directOrderId);
                directOrderRequest.setTotalAmount(100000);
                directOrderRequest.setDescription("Direct order " + directOrderId);
                directOrderRequest.setCustomerInfo(new CustomerInfo("NGUYEN VAN A", "test@example.com", "0901234567", "123 Test Street"));

                BaokimOrder.ApiResponse directOrderResult = directService.createOrder(directOrderRequest);
                results.get("direct").put("create_order", directOrderResult.success);
                System.out.println("   Create Order: "
                        + (directOrderResult.success ? "✅ " + directOrderId : "❌ " + directOrderResult.message));

                // Query Order
                BaokimOrder.ApiResponse directQueryResult = directService.queryOrder(directOrderId);
                results.get("direct").put("query_order", directQueryResult.success);
                System.out.println(
                        "   Query Order: " + (directQueryResult.success ? "✅" : "❌ " + directQueryResult.message));

                // Cancel Order
                String cancelOrderId = "CXL" + (System.currentTimeMillis() % 10000000000L) + new Random().nextInt(999);
                DirectCreateOrderRequest cancelOrderRequest = new DirectCreateOrderRequest();
                cancelOrderRequest.setMrcOrderId(cancelOrderId);
                cancelOrderRequest.setTotalAmount(50000);
                cancelOrderRequest.setDescription("Order to cancel");
                cancelOrderRequest.setCustomerInfo(new CustomerInfo("TRAN VAN B", "cancel@example.com", "0901234567", "456 Cancel Street"));

                BaokimOrder.ApiResponse cancelCreateResult = directService.createOrder(cancelOrderRequest);
                if (cancelCreateResult.success) {
                    BaokimOrder.ApiResponse cancelResult = directService.cancelOrder(cancelOrderId, "Test cancel");
                    results.get("direct").put("cancel_order", cancelResult.success);
                    System.out.println(
                            "   Cancel Order: " + (cancelResult.success ? "✅" : "❌ " + cancelResult.message) + "\n");
                } else {
                    results.get("direct").put("cancel_order", false);
                    System.out.println("   Cancel Order: ❌ Could not create order\n");
                }
            }

            // ============================================================
            // SUMMARY
            // ============================================================
            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                    TEST COMPLETED                        ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝\n");

            System.out.println("📋 Summary:");

            if (connectionType.equals("all") || connectionType.equals("basic_pro")) {
                System.out.println("\n   🔷 BASIC/PRO:");
                for (Map.Entry<String, Boolean> entry : results.get("basic_pro").entrySet()) {
                    String testName = entry.getKey().replace("_", " ");
                    testName = testName.substring(0, 1).toUpperCase() + testName.substring(1);
                    System.out.println("      " + testName + ": " + (entry.getValue() ? "✅" : "❌"));
                }
            }

            if (connectionType.equals("all") || connectionType.equals("host_to_host")) {
                System.out.println("\n   🔷 HOST-TO-HOST:");
                for (Map.Entry<String, Boolean> entry : results.get("host_to_host").entrySet()) {
                    String testName = entry.getKey().replace("_", " ");
                    testName = testName.substring(0, 1).toUpperCase() + testName.substring(1);
                    System.out.println("      " + testName + ": " + (entry.getValue() ? "✅" : "❌"));
                }
            }

            if (connectionType.equals("all") || connectionType.equals("direct")) {
                System.out.println("\n   🔷 DIRECT:");
                for (Map.Entry<String, Boolean> entry : results.get("direct").entrySet()) {
                    String testName = entry.getKey().replace("_", " ");
                    testName = testName.substring(0, 1).toUpperCase() + testName.substring(1);
                    System.out.println("      " + testName + ": " + (entry.getValue() ? "✅" : "❌"));
                }
            }

            SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("\n📁 Log file: logs/api_" + logDateFormat.format(new Date()) + ".log");

        } catch (Exception e) {
            System.err.println("\n❌ EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
