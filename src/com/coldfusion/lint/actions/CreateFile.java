package com.coldfusion.lint.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;

public class CreateFile extends AnAction {
    private VirtualFile file;
    private Document currentDoc;
    private Editor editor;

    @Override
    public void actionPerformed(AnActionEvent e) {
        editor = e.getData(CommonDataKeys.EDITOR);
        currentDoc = editor.getDocument();
        String actionName = e.getActionManager().getId(this);

        if(actionName.equals("createunittestfile")) {
            file = FileDocumentManager.getInstance().getFile(currentDoc);
            System.out.println(file.getPath());
        }
    }

}
