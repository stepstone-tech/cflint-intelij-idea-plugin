package com.coldfusion.lint.model;

import com.coldfusion.lint.LintAnalyzer;

public class EditorContent {

    public static String selectedFromLineToEnd(int line) {
        try {
            int documentLines = LintAnalyzer.getDocument().getLineCount();
            int startOffset = LintAnalyzer.getDocument().getLineStartOffset(line - 1);
            int endOffset = LintAnalyzer.getDocument().getLineEndOffset(documentLines - 2);

            return LintAnalyzer.getDocument().getCharsSequence().subSequence(startOffset, endOffset).toString();

        } catch (Exception e) {
            System.out.println(e);
        }

        return "";
    }

}

