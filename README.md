# Baokim B2B API - Java 7 Example

Bộ source code mẫu tích hợp Baokim B2B API, viết bằng Java 7 với Maven.

## 🔧 Yêu cầu
- Java 7+
- Maven 3.x

## 📦 Cài đặt

```bash
git clone https://github.com/Mulligan1499/baokim-b2b-java7-example.git
cd java7-b2b-example
cp src/main/resources/config.properties src/main/resources/config.local.properties
# Chỉnh sửa config.local.properties
```

## 🚀 Quick Start

```bash
# Build
mvn clean package

# Run
java -jar target/b2b-example-1.0.0.jar

# Test với refund
java -jar target/b2b-example-1.0.0.jar ORDER_ID AMOUNT
```

## 📁 Cấu trúc

```
├── pom.xml
├── src/main/
│   ├── java/vn/baokim/b2b/
│   │   ├── Config.java
│   │   ├── Logger.java
│   │   ├── SignatureHelper.java
│   │   ├── HttpClient.java
│   │   ├── BaokimAuth.java
│   │   ├── BaokimOrder.java
│   │   ├── BaokimVA.java
│   │   ├── ErrorCode.java
│   │   └── TestFullFlow.java
│   └── resources/
│       └── config.properties
├── keys/
└── logs/
```

## 📚 APIs

### Basic Pro
| API | Endpoint |
|-----|----------|
| Lấy Token | `/b2b/auth-service/api/oauth/get-token` |
| Tạo đơn | `/b2b/core/api/ext/mm/order/send` |
| Tra cứu | `/b2b/core/api/ext/mm/order/get-order` |
| Hoàn tiền | `/b2b/core/api/ext/mm/refund/send` |
| Hủy thu hộ | `/b2b/core/api/ext/mm/autodebit/cancel` |

### VA Host to Host
| API | Endpoint |
|-----|----------|
| Tạo VA | `/b2b/core/api/ext/mm/bank-transfer/create` |
| Cập nhật VA | `/b2b/core/api/ext/mm/bank-transfer/update` |
| Tra cứu VA | `/b2b/core/api/ext/mm/bank-transfer/detail` |

## 🖥️ Replit

Import repo → Run `mvn clean package && java -jar target/b2b-example-1.0.0.jar`

---
© 2026 Baokim
