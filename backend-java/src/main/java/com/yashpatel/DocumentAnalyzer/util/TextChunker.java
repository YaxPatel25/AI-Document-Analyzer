package com.yashpatel.DocumentAnalyzer.util;

import org.springframework.stereotype.Component;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class TextChunker {

    private static final int TARGET_TOKENS = 450;
    private static final int OVERLAP_TOKENS = 60;

    private static final Pattern PARAGRAPH_BREAK =
            Pattern.compile("\\n\\s*\\n+");

    private static final Pattern HEADING = Pattern.compile(
            "^(?:\\d+(?:\\.\\d+)*[.)]?\\s+.+|[A-Z][A-Z\\s,&:/-]{3,}|"
                    + "(?:Chapter|Section|Article)\\s+.+)$"
    );

    public List<String> chunk(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<String> chunks = new ArrayList<>();
        List<String> currentParts = new ArrayList<>();

        String currentSection = null;
        int currentTokens = 0;

        String[] paragraphs = PARAGRAPH_BREAK.split(text.trim());

        for (String rawParagraph : paragraphs) {
            String paragraph = rawParagraph
                    .replaceAll("\\s*\\n\\s*", " ")
                    .replaceAll("\\s+", " ")
                    .trim();

            if (paragraph.isBlank()) {
                continue;
            }

            if (isHeading(paragraph)) {
                if (!currentParts.isEmpty()) {
                    chunks.add(buildChunk(currentSection, currentParts));
                    currentParts.clear();
                    currentTokens = 0;
                }
                currentSection = paragraph;
                continue;
            }

            List<String> units = splitOversizedParagraph(paragraph);

            for (String unit : units) {
                int unitTokens = estimateTokens(unit);
                int headerTokens = currentParts.isEmpty()
                        ? estimateTokens(sectionPrefix(currentSection))
                        : 0;

                if (!currentParts.isEmpty()
                        && currentTokens + unitTokens > TARGET_TOKENS) {

                    chunks.add(buildChunk(currentSection, currentParts));

                    currentParts = overlapTail(currentParts);
                    currentTokens = currentParts.stream()
                            .mapToInt(this::estimateTokens)
                            .sum();
                }

                // A sentence longer than the target is still retained intact.
                currentParts.add(unit);
                currentTokens += unitTokens + headerTokens;
            }
        }

        if (!currentParts.isEmpty()) {
            chunks.add(buildChunk(currentSection, currentParts));
        }

        return chunks;
    }

    private boolean isHeading(String text) {
        return text.length() <= 120
                && !text.endsWith(".")
                && HEADING.matcher(text).matches();
    }

    private List<String> splitOversizedParagraph(String paragraph) {
        if (estimateTokens(paragraph) <= TARGET_TOKENS) {
            return List.of(paragraph);
        }

        List<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.ROOT);
        iterator.setText(paragraph);

        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {

            String sentence = paragraph.substring(start, end).trim();
            if (!sentence.isBlank()) {
                sentences.add(sentence);
            }
        }

        return sentences.isEmpty() ? List.of(paragraph) : sentences;
    }

    private List<String> overlapTail(List<String> parts) {
        List<String> overlap = new ArrayList<>();
        int tokens = 0;

        for (int i = parts.size() - 1; i >= 0; i--) {
            String part = parts.get(i);
            int partTokens = estimateTokens(part);

            if (tokens + partTokens > OVERLAP_TOKENS && !overlap.isEmpty()) {
                break;
            }

            overlap.add(0, part);
            tokens += partTokens;
        }

        return overlap;
    }

    private String buildChunk(String section, List<String> parts) {
        return sectionPrefix(section) + String.join("\n\n", parts);
    }

    private String sectionPrefix(String section) {
        return section == null || section.isBlank()
                ? ""
                : "Section: " + section + "\n\n";
    }

    // Approximation only. Replace with the embedding model's tokenizer later.
    private int estimateTokens(String text) {
        return text.trim().isBlank()
                ? 0
                : text.trim().split("\\s+").length;
    }
}