package com.coldfusion.lint.listeners;

import com.coldfusion.lint.LintAnalyzer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnalyzeCodeButtonActionListener implements ActionListener{
    private LintAnalyzer lintAnalyzer;

    public AnalyzeCodeButtonActionListener(LintAnalyzer lintAnalyzer){
        this.lintAnalyzer = lintAnalyzer;
    }
    public void actionPerformed(ActionEvent e) {
        lintAnalyzer.analyzeFile();
    }
}