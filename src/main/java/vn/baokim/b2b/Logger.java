package vn.baokim.b2b;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger - Ghi log request/response
 */
public class Logger {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static boolean enabled = true;
    
    public static void setEnabled(boolean value) {
        enabled = value;
    }
    
    public static void log(String level, String message) {
        log(level, message, null);
    }
    
    public static void log(String level, String message, String context) {
        if (!enabled) return;
        
        try {
            String timestamp = dateFormat.format(new Date());
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(timestamp).append("] [").append(level).append("] ").append(message);
            if (context != null && !context.isEmpty()) {
                sb.append("\n").append(context);
            }
            sb.append("\n").append(repeat("-", 80)).append("\n");
            
            // Write to file
            String logDir = Config.getBasePath() + "logs/";
            File dir = new File(logDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String logFile = logDir + "api_" + fileDateFormat.format(new Date()) + ".log";
            PrintWriter writer = new PrintWriter(new FileWriter(logFile, true));
            writer.print(sb.toString());
            writer.close();
            
        } catch (Exception e) {
            System.err.println("Logger error: " + e.getMessage());
        }
    }
    
    public static void logRequest(String method, String url, String headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("Headers: ").append(sanitizeHeaders(headers)).append("\n");
        if (body != null) {
            sb.append("Body: ").append(body);
        }
        log("INFO", "REQUEST: " + method + " " + url, sb.toString());
    }
    
    public static void logResponse(int httpCode, String body, long duration) {
        String msg = "RESPONSE: HTTP " + httpCode + " (" + duration + "ms)";
        log("INFO", msg, "Body: " + body);
    }
    
    public static void error(String message) {
        log("ERROR", message);
    }
    
    public static void info(String message) {
        log("INFO", message);
    }
    
    private static String sanitizeHeaders(String headers) {
        if (headers == null) return "";
        // Hide sensitive data
        return headers.replaceAll("(Authorization: Bearer ).+", "$1***");
    }
    
    private static String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
