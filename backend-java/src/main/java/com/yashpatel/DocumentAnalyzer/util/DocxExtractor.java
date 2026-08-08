package com.yashpatel.DocumentAnalyzer.util;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class DocxExtractor {

    public static String extractText(File file) {
        try (FileInputStream fis = new FileInputStream(file);
                XWPFDocument document = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();

            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph paragraph : paragraphs) {
                sb.append(paragraph.getText()).append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract DOCX text", e);
        }
    }
}