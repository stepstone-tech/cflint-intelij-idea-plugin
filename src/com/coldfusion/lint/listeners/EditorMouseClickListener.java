package com.coldfusion.lint.listeners;

import com.coldfusion.lint.LintAnalyzer;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;

import java.awt.*;

public class EditorMouseClickListener implements EditorMouseListener {

    @Override
    public void mousePressed(EditorMouseEvent editorMouseEvent) {

    }

    @Override
    public void mouseClicked(EditorMouseEvent editorMouseEvent) {
        try {
            final Point locationOnScreen = editorMouseEvent.getMouseEvent().getLocationOnScreen();
            final int lineNumber = EditorUtil.yPositionToLogicalLine(editorMouseEvent.getEditor(), editorMouseEvent.getMouseEvent()) + 1;

            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(LintAnalyzer.getErrorMessages().get(lineNumber), MessageType.ERROR, null)
                    .setFadeoutTime(7500)
                    .createBalloon()
                    .show(RelativePoint.fromScreen(locationOnScreen), Balloon.Position.atRight);

        } catch (Exception e) {
            //Create a logger
        }
    }

    @Override
    public void mouseReleased(EditorMouseEvent editorMouseEvent) {

    }

    @Override
    public void mouseEntered(EditorMouseEvent editorMouseEvent) {

    }

    @Override
    public void mouseExited(EditorMouseEvent editorMouseEvent) {

    }
}