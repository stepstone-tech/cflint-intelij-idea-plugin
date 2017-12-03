package com.coldfusion.lint.highlighters;

import com.coldfusion.lint.LintAnalyzer;
import com.intellij.openapi.editor.markup.*;
import com.intellij.ui.JBColor;

import java.awt.*;

public class Highlighter {
    final static Color errorStripeColor = JBColor.RED;
    final static TextAttributes textattributes = new TextAttributes();

    public static void highlightErrors(int line, String toolTipMsg) {

        textattributes.setEffectColor(JBColor.RED);
        textattributes.setEffectType(EffectType.WAVE_UNDERSCORE);

        int startOffset = LintAnalyzer.getDocument().getLineStartOffset(line - 1);
        int endOffset = LintAnalyzer.getDocument().getLineEndOffset(line - 1);
        String lineText = LintAnalyzer.getDocument().getCharsSequence().subSequence(startOffset, endOffset).toString();
        int lineStartOffset = startOffset + lineText.length() - lineText.trim().length();

        RangeHighlighter highlighter = LintAnalyzer.getEditor().getMarkupModel().addRangeHighlighter(lineStartOffset, endOffset, HighlighterLayer.SYNTAX, textattributes, HighlighterTargetArea.EXACT_RANGE);
        highlighter.setErrorStripeMarkColor(errorStripeColor);
        highlighter.setErrorStripeTooltip(toolTipMsg);
    }
}
