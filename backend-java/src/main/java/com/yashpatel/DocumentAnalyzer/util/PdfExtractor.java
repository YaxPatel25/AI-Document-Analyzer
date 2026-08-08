package com.yashpatel.DocumentAnalyzer.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

public class PdfExtractor {

    public static String extractText(File file) {

        try (
                PDDocument document =
                        Loader.loadPDF(file)
        ) {

            PDFTextStripper stripper =
                    new PDFTextStripper();

            return stripper.getText(document);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to extract PDF text",
                    e
            );
        }
    }
}