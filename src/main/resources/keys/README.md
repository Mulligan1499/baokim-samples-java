# Keys Directory

Đặt các file RSA key vào thư mục này:

- `merchant_private.pem` - Private key của Merchant
- `baokim_public.pem` - Public key của Baokim

## Tạo RSA Key Pair

```bash
openssl genrsa -out merchant_private.pem 2048
openssl rsa -in merchant_private.pem -pubout -out merchant_public.pem
```
