package com.schnorr.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class SignatureFileManager {
    
    /**
     * Lưu chữ ký vào file theo định dạng chuẩn.
     * Định dạng lưu: e và s trên từng dòng.
     */
    public static void saveSignature(File file, String e, String s) throws IOException {
        String content = e + "\n" + s;
        Files.write(file.toPath(), content.getBytes("UTF-8"), 
                StandardOpenOption.CREATE, 
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Đọc chữ ký từ file.
     * Trả về mảng String: index 0 là e, index 1 là s.
     */
    public static String[] loadSignature(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), "UTF-8").trim();
        String[] parts = content.split("\\r?\\n");
        if (parts.length < 2) {
            throw new IOException("File chữ ký không đúng định dạng. Cần ít nhất 2 dòng (e và s).");
        }
        return new String[]{ parts[0].trim(), parts[1].trim() };
    }
}
