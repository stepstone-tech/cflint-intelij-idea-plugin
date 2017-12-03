package com.coldfusion.lint.actions;

import com.coldfusion.lint.LintAnalyzer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class AnalyzeCode extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
       new LintAnalyzer().analyzeFile();
    }
}
