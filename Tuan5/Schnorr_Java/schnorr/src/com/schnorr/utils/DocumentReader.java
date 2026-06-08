package com.schnorr.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class DocumentReader {
    
    public static String readContent(File file) throws IOException {
        String fileName = file.getName().toLowerCase();
        
        if (fileName.endsWith(".pdf")) {
            return readPdf(file);
        } else if (fileName.endsWith(".docx")) {
            return readDocx(file);
        } else if (fileName.endsWith(".txt")) {
            return readTxt(file);
        } else {
            throw new IOException("Định dạng file không được hỗ trợ. Vui lòng chọn .txt, .pdf hoặc .docx");
        }
    }

    private static String readTxt(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), "UTF-8").trim();
    }

    private static String readPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document).trim();
            } else {
                throw new IOException("File PDF bị mã hóa (yêu cầu mật khẩu).");
            }
        }
    }

    private static String readDocx(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText().trim();
        }
    }
}
