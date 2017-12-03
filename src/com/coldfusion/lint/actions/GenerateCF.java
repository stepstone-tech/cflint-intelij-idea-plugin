package com.coldfusion.lint.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GenerateCF extends AnAction {
    private String templateContent = "";
    private VirtualFile currentFile;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        Document document = editor != null ? editor.getDocument() : null;
        String actionName = e.getActionManager().getId(this);
        ClassLoader classLoader = super.getClass().getClassLoader();
        currentFile = FileDocumentManager.getInstance().getFile(document);

        try {
            InputStream is = classLoader.getResourceAsStream("/" + actionName + ".template");
            templateContent = readText(is, "utf-8");
            templateContent = templateContent.replace("\r","");
            if(actionName.equals("constructor")){
                templateContent = replacementsForConstructor(templateContent);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        int lineNumber = editor.getCaretModel().getLogicalPosition().line;
        new WriteCommandAction(e.getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                editor.getDocument().insertString(document.getLineStartOffset(lineNumber), templateContent);
            }
        }.execute();

    }

    public static String readText(InputStream is, String charset) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[4096];
        for(int len;(len = is.read(bytes))>0;)
            baos.write(bytes, 0, len);
        return new String(baos.toByteArray(), charset);
    }

    private String replacementsForConstructor(String content) {
        String componentName = currentFile.getName().replace(".cfc","").replace(".cfml","").replace(".cfm","");
        return content.replace("{ComponentName}", componentName);
    }
}
