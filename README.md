# Baokim B2B API - Java 8+ SDK

Bộ SDK tích hợp Baokim B2B API, viết bằng Java 8+ với Maven.

## 🔧 Yêu cầu
- Java 8+
- Maven 3.x

---

## 📦 Tích hợp vào project của bạn

### Bước 1: Clone SDK

```bash
git clone https://github.com/ITBaoKim/baokim-samples-java.git
```

### Bước 2: Copy thư mục `src/` vào project

```bash
cp -r baokim-samples-java/src /path/to/your-project/baokim-sdk
```

Thư mục `src/` đã bao gồm sẵn config và keys, bạn chỉ cần copy 1 folder duy nhất:

```
your-project/
├── baokim-sdk/                        # Chỉ cần copy folder src/ này
│   └── main/
│       ├── java/vn/baokim/b2b/        # Java source code
│       │   ├── BaokimAuth.java
│       │   ├── Config.java
│       │   ├── HttpClient.java
│       │   ├── mastersub/
│       │   │   └── BaokimOrder.java
│       │   ├── hosttohost/
│       │   │   └── BaokimVA.java
│       │   ├── direct/
│       │   │   └── BaokimDirect.java
│       │   └── merchanthosted/
│       │       └── BaokimMerchantVA.java
│       └── resources/
│           ├── config/                # ← Config nằm sẵn trong SDK
│           │   └── config.properties  # File cấu hình (điền thông tin ở bước 3)
│           └── keys/                  # ← Keys nằm sẵn trong SDK
│               ├── merchant_private.pem
│               └── baokim_public.pem
├── pom.xml
├── logs/
└── YourApp.java
```

### Bước 3: Thêm dependency vào pom.xml

Nếu project của bạn sử dụng Maven, hãy thêm dependency sau vào file `pom.xml` để SDK có thể thực hiện parse JSON:

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.9</version>
</dependency>
```

### Bước 4: Cấu hình

Mở file `baokim-sdk/main/resources/config/config.properties` và điền thông tin Baokim cung cấp:
```properties
base_url=https://devtest.baokim.vn
timeout=30000

merchant_code=YOUR_MERCHANT_CODE
client_id=YOUR_CLIENT_ID
client_secret=YOUR_CLIENT_SECRET

master_merchant_code=YOUR_MASTER_MERCHANT_CODE
sub_merchant_code=YOUR_SUB_MERCHANT_CODE

# RSA Key paths (tương đối từ thư mục gốc project)
merchant_private_key_path=src/main/resources/keys/merchant_private.pem
baokim_public_key_path=src/main/resources/keys/baokim_public.pem

url_success=https://your-domain.com/payment/success
url_fail=https://your-domain.com/payment/fail
webhook_url=https://your-domain.com/webhook/baokim
```

> [!IMPORTANT]
> **Lưu ý lên môi trường Production:**
> - Thay `base_url` thành `https://openapi.baokim.vn`.
> - Thay đổi các thông tin `merchant_code`, `client_id`, `client_secret` sang thông tin môi trường Production do Baokim cung cấp.
> - Cập nhật cặp RSA Keys (Private Key của Merchant và Public Key của Baokim) tương ứng với môi trường Production.

### Bước 5: Đặt RSA Keys

Đặt file Private Key (Baokim cung cấp) vào `baokim-sdk/main/resources/keys/`:
```bash
# Copy merchant_private.pem vào baokim-sdk/main/resources/keys/
```

### Bước 6: Build project

```bash
mvn clean compile
```

---

## 🚀 Sử dụng SDK

### Khởi tạo (bắt buộc ở đầu mỗi file)

```java
import vn.baokim.b2b.*;

// Load config
Config.load();

// Khởi tạo Auth
BaokimAuth auth = new BaokimAuth();
```

---

## 🔷 API 1: Lấy Access Token

```java
import vn.baokim.b2b.*;

Config.load();

BaokimAuth auth = new BaokimAuth();
String token = auth.getToken();

System.out.println("Token: " + token.substring(0, 50) + "...");
```

```bash
mvn exec:java -Dexec.mainClass="vn.baokim.b2b.examples.GetToken"
```

---

## 🔷 API 2: Tạo đơn hàng (Basic Pro - Master/Sub)

```java
import vn.baokim.b2b.*;
import vn.baokim.b2b.mastersub.BaokimOrder;

Config.load();

BaokimAuth auth = new BaokimAuth();
BaokimOrder orderService = new BaokimOrder(auth.getToken());

Map<String, Object> orderData = new HashMap<>();
orderData.put("mrcOrderId", "ORDER_" + System.currentTimeMillis());
orderData.put("totalAmount", 100000);
orderData.put("description", "Thanh toan don hang");
orderData.put("customerInfo", BaokimOrder.buildCustomerInfo(
    "NGUYEN VAN A", "test@email.com", "0901234567", "123 Street"
));

BaokimOrder.ApiResponse result = orderService.createOrder(orderData);

System.out.println("Success: " + result.success);
if (result.success) {
    System.out.println("Payment URL: " + result.data.get("redirect_url").getAsString());
}
```

---

## 🔷 API 3: Tra cứu đơn hàng

```java
BaokimOrder.ApiResponse result = orderService.queryOrder("ORDER_123456");
System.out.println("Success: " + result.success);
```

---

## 🔷 API 4: Hoàn tiền (Refund)

```java
BaokimOrder.ApiResponse result = orderService.refundOrder("ORDER_123456", 50000, "Hoan tien cho khach");
System.out.println("Success: " + result.success);
```

---

## 🔷 API 5: Tạo Virtual Account - VA (Host-to-Host)

```java
import vn.baokim.b2b.hosttohost.BaokimVA;

BaokimVA vaService = new BaokimVA(auth.getToken());

BaokimOrder.ApiResponse result = vaService.createDynamicVA(
    "NGUYEN VAN A",    // Tên khách hàng
    "ORDER_123",       // Mã đơn hàng
    100000,            // Số tiền
    ""                 // Mô tả
);

System.out.println("Success: " + result.success);
if (result.success) {
    System.out.println("Số VA: " + result.data.get("acc_no").getAsString());
}
```

---

## 🔷 API 6: Tra cứu giao dịch VA

```java
BaokimOrder.ApiResponse result = vaService.queryTransaction("00812345678901");
System.out.println("Success: " + result.success);
```

---

## 🔷 API 7: Tạo đơn hàng Direct Connection

> ⚠️ Direct connection sử dụng credentials riêng (`direct_client_id`, `direct_client_secret`). Thêm vào config nếu có.

```java
import vn.baokim.b2b.direct.BaokimDirect;

BaokimAuth directAuth = BaokimAuth.forDirectConnection();
BaokimDirect directService = new BaokimDirect(directAuth.getToken());

Map<String, Object> orderData = new HashMap<>();
orderData.put("mrc_order_id", "DRT_" + System.currentTimeMillis());
orderData.put("total_amount", 150000);
orderData.put("description", "Thanh toan Direct");

Map<String, Object> customerInfo = new HashMap<>();
customerInfo.put("name", "NGUYEN VAN A");
customerInfo.put("email", "customer@email.com");
customerInfo.put("phone", "0901234567");
customerInfo.put("address", "123 Nguyen Hue, HCM");
customerInfo.put("gender", 1);
orderData.put("customer_info", customerInfo);

BaokimOrder.ApiResponse result = directService.createOrder(orderData);
System.out.println("Success: " + result.success);
```

---

## 🔷 API 9: Tạo Virtual Account - VA (Merchant Hosted / Direct)

> ⚠️ Merchant Hosted dùng credentials riêng (`direct_client_id`, `direct_client_secret`).
> Khác với Host-to-Host (Master/Sub), Merchant Hosted dùng `merchant_code` thay vì `master_merchant_code` + `sub_merchant_code`.

```java
import vn.baokim.b2b.*;
import vn.baokim.b2b.merchanthosted.BaokimMerchantVA;
import vn.baokim.b2b.mastersub.BaokimOrder;

Config.load();

BaokimAuth directAuth = BaokimAuth.forDirectConnection();
BaokimMerchantVA vaService = new BaokimMerchantVA(directAuth.getToken());

BaokimOrder.ApiResponse result = vaService.createDynamicVA(
    "NGUYEN VAN A",    // Tên khách hàng
    "MH_VA_" + System.currentTimeMillis(),  // Mã đơn hàng
    100000,            // Số tiền
    ""                 // Ghi chú
);

System.out.println("Success: " + result.success);
if (result.success) {
    System.out.println("Số VA: " + result.data.get("acc_no").getAsString());
}
```

### Tạo VA với đầy đủ options

```java
Map<String, Object> vaData = new LinkedHashMap<>();
vaData.put("acc_name", "NGUYEN VAN A");
vaData.put("acc_type", 1);                   // 1=Dynamic, 2=Static
vaData.put("mrc_order_id", "ORDER_001");
vaData.put("collect_amount_min", 100000);     // Required khi acc_type=1
vaData.put("collect_amount_max", 100000);     // Required
vaData.put("store_code", "STORE_001");       // Optional
vaData.put("staff_code", "STAFF_001");       // Optional
vaData.put("bank_code", "BIDV");             // Optional
vaData.put("memo", "Ghi chú");               // Optional (max 255)

BaokimOrder.ApiResponse result = vaService.createVA(vaData);
```

---

## 🔷 API 10: Cập nhật VA (Merchant Hosted)

```java
Map<String, Object> updateData = new HashMap<>();
updateData.put("acc_name", "NGUYEN VAN B");
updateData.put("collect_amount_max", 500000);
updateData.put("expire_date", "2027-06-30 23:59:59");

BaokimOrder.ApiResponse result = vaService.updateVA("ORDER_001", updateData);
System.out.println("Success: " + result.success);
```

---

## 🔷 API 11: Tra cứu chi tiết VA (Merchant Hosted)

```java
// Tra cứu cơ bản
BaokimOrder.ApiResponse result = vaService.detailVA("00812345678901");
System.out.println("Success: " + result.success);

// Tra cứu với bộ lọc
Map<String, Object> queryData = new HashMap<>();
queryData.put("start_date", "2026-01-01 00:00:00");
queryData.put("end_date", "2026-12-31 23:59:59");
queryData.put("current_page", 1);
queryData.put("per_page", 20);

BaokimOrder.ApiResponse result2 = vaService.detailVA("00812345678901", queryData);
```

---

## 🔷 API 8: Xử lý Webhook từ Baokim (Verify Signature)

Khi có giao dịch thành công (thanh toán, hoàn tiền, VA...), **Baokim sẽ gửi HTTP POST** đến webhook URL của merchant.

### Cấu hình

Đặt file **Baokim Public Key** (do Baokim cung cấp) vào `baokim-sdk/main/resources/keys/baokim_public.pem`.

### Code example (Servlet hoặc Spring Controller)

```java
import vn.baokim.b2b.*;
import java.io.BufferedReader;

// Trong method xử lý POST request (ví dụ: Servlet hoặc Spring @PostMapping)
public void handleWebhook(HttpServletRequest request, HttpServletResponse response) {
    try {
        Config.load();
        
        // Lấy signature từ header
        String signature = request.getHeader("Signature");
        if (signature == null || signature.isEmpty()) {
            throw new Exception("Signature header not found");
        }

        // Đọc body
        StringBuilder body = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        // Verify signature bằng Baokim Public Key
        boolean isValid = SignatureHelper.verify(body.toString(), signature);
        if (!isValid) {
            throw new Exception("Invalid signature");
        }

        System.out.println("✅ Webhook verified! Data: " + body.toString());
        
        // TODO: Parse JSON và cập nhật trạng thái đơn hàng trong database

        // Trả về success
        response.setContentType("application/json");
        response.getWriter().write("{\"code\": 0, \"message\": \"Success\"}");
    } catch (Exception e) {
        System.err.println("❌ Webhook error: " + e.getMessage());
        response.setContentType("application/json");
        try {
            response.getWriter().write("{\"code\": 1, \"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception ignored) {}
    }
}
```

### Ví dụ test với file Java thuần (không cần Servlet)

```java
import vn.baokim.b2b.*;

public class TestWebhookVerify {
    public static void main(String[] args) throws Exception {
        Config.load();
        
        // Giả lập data và signature nhận từ Baokim
        String webhookBody = "{\"order\":{\"mrc_order_id\":\"ORDER_123\"}}";
        String signature = "BASE64_SIGNATURE_FROM_BAOKIM";
        
        boolean isValid = SignatureHelper.verify(webhookBody, signature);
        System.out.println("Signature valid: " + isValid);
    }
}
```

### Response format

Merchant cần trả về JSON với `code = 0` khi xử lý thành công:
```json
{"code": 0, "message": "Success"}
```

---

## 📚 API Endpoints

### Basic Pro (Master/Sub)
| API | Endpoint |
|-----|----------|
| Tạo đơn | `/b2b/core/api/ext/mm/order/send` |
| Tra cứu | `/b2b/core/api/ext/mm/order/get-order` |
| Hoàn tiền | `/b2b/core/api/ext/mm/refund/send` |

### VA Host to Host
| API | Endpoint |
|-----|----------|
| Tạo VA | `/b2b/core/api/ext/mm/bank-transfer/create` |
| Cập nhật VA | `/b2b/core/api/ext/mm/bank-transfer/update` |
| Tra cứu VA | `/b2b/core/api/ext/mm/bank-transfer/detail` |

### VA Merchant Hosted (Direct)
| API | Endpoint |
|-----|----------|
| Tạo VA | `/b2b/core/api/merchant-hosted/bank-transfer/create` |
| Cập nhật VA | `/b2b/core/api/merchant-hosted/bank-transfer/update` |
| Tra cứu VA | `/b2b/core/api/merchant-hosted/bank-transfer/detail` |

### Direct Connection
| API | Endpoint |
|-----|----------|
| Tạo đơn | `/b2b/core/api/ext/order/send` |
| Tra cứu | `/b2b/core/api/ext/order/get-order` |
| Hủy đơn | `/b2b/core/api/ext/order/cancel` |

---

## ❓ Troubleshooting

| Lỗi | Nguyên nhân | Cách sửa |
|-----|-------------|----------|
| `Chữ ký số không hợp lệ` | Private key không đúng | Kiểm tra file `keys/merchant_private.pem` |
| `Token expired` | Token hết hạn | SDK tự động refresh, không cần xử lý |
| `Invalid merchant_code` | Sai mã merchant | Kiểm tra config |
| `Config file not found` | Chưa cấu hình config.properties | Mở file `config.properties` và điền thông tin |

---
© 2026 Baokim
