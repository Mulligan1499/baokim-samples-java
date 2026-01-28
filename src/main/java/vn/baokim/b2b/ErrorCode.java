package vn.baokim.b2b;

import java.util.HashMap;
import java.util.Map;

/**
 * ErrorCode - Mapping mã lỗi Baokim
 */
public class ErrorCode {
    private static final Map<Integer, String> ERROR_CODES = new HashMap<Integer, String>();
    
    static {
        ERROR_CODES.put(0, "Thành công");
        ERROR_CODES.put(100, "Đang xử lý");
        ERROR_CODES.put(101, "Thành công - Cần redirect");
        ERROR_CODES.put(102, "Lỗi từ nhà cung cấp dịch vụ");
        ERROR_CODES.put(103, "Chữ ký số không hợp lệ");
        ERROR_CODES.put(104, "Signature không hợp lệ");
        ERROR_CODES.put(111, "Xác thực thất bại");
        ERROR_CODES.put(200, "Thành công");
        ERROR_CODES.put(422, "Dữ liệu đầu vào không hợp lệ");
        ERROR_CODES.put(707, "Mã đơn hàng đã tồn tại");
    }
    
    public static String getMessage(int code) {
        String msg = ERROR_CODES.get(code);
        return msg != null ? msg : "Mã lỗi không xác định: " + code;
    }
    
    public static boolean isSuccess(int code) {
        return code == 0 || code == 100 || code == 101 || code == 200;
    }
}
