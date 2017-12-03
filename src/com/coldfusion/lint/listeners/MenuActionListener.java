package com.coldfusion.lint.listeners;

import com.coldfusion.lint.LintAnalyzer;
import com.coldfusion.lint.parsers.Parser;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuActionListener implements ActionListener {
    private JList jList;
    private Project project;
    private Document document;
    private int line;
    private int column;
    int offset;

    public MenuActionListener(JList jList, int line, int column) {
        this.project = LintAnalyzer.getProject();
        this.document = LintAnalyzer.getDocument();

        this.jList = jList;
        this.line = line;
        this.column = column;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String expression = String.valueOf(jList.getSelectedValue());
        new WriteCommandAction(project) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                offset = document.getLineStartOffset(line - 1);
                document.insertString(offset, new Parser().getResults(expression));
            }
        }.execute();
    }
}