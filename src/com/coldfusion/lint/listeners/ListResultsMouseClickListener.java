package com.coldfusion.lint.listeners;

import com.coldfusion.lint.LintAnalyzer;
import com.coldfusion.lint.highlighters.Highlighter;
import com.coldfusion.lint.parsers.Parser;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ListResultsMouseClickListener implements MouseListener {

    private JList jList;
    private Editor editor;
    private Highlighter highlighter;

    public ListResultsMouseClickListener(JList jList) {
        this.editor = LintAnalyzer.getEditor();
        this.jList = jList;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String listValue = this.jList.getSelectedValue().toString();
        int line = 0;
        int column = 0;

        String[] lineAndColumn = Parser.parseLineAndColumn(listValue);

        line = Integer.parseInt(lineAndColumn[0]);
        column = Integer.parseInt(lineAndColumn[1]);

        editor.getMarkupModel().removeAllHighlighters();
        Highlighter.highlightErrors(line, listValue);

        editor.getCaretModel().moveToLogicalPosition(new LogicalPosition(line - 1, 0));
        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);


        if (SwingUtilities.isRightMouseButton(e)) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem itemGenerateHint = new JMenuItem("Generate annotation");
            itemGenerateHint.addActionListener(new MenuActionListener(jList, line, column));

            JMenuItem itemGenerateConstructors = new JMenuItem("Generate constructor");
            itemGenerateConstructors.addActionListener(new MenuActionListener(jList, line, column));

            menu.add(itemGenerateHint);
            menu.add(itemGenerateConstructors);

            menu.show(jList, e.getPoint().x, e.getPoint().y);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}