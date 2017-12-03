package com.coldfusion.lint;

import com.cflint.main.CFLintMain;
import com.coldfusion.lint.highlighters.Highlighter;
import com.coldfusion.lint.listeners.AnalyzeCodeButtonActionListener;
import com.coldfusion.lint.listeners.EditorMouseClickListener;
import com.coldfusion.lint.listeners.ListResultsMouseClickListener;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;



public class LintAnalyzer implements ToolWindowFactory {
    private JButton analyzeToolWindowButton;
    private JPanel myToolWindowContent;
    private JList listResults;
    private JTabbedPane tabbedPane1;
    private JList listResultsInfo;
    private JList listResultsError;
    private JPanel panelResultsAll;
    private JList listResultsAll;
    private ToolWindow myToolWindow;
    private int numberOfErrors = 0;
    private static Project project;
    private static Editor editor;
    private static Document document;
    private static VirtualFile file;
    private  EditorMouseClickListener editorMouseClickListener;
    private static HashMap<Integer, String> errorMessages = new HashMap<>();
    private Highlighter highlighter;

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public LintAnalyzer() {
        analyzeToolWindowButton.addActionListener(new AnalyzeCodeButtonActionListener(this));
    }

    private void setEnvironmentSettings() {
        setProject();
        setEditor();
        setDocument();
        setFile();
        setEditorMouseClickListener();
    }

    private void setEditorMouseClickListener() {
        try {
            editor.removeEditorMouseListener(editorMouseClickListener);
        } catch (Exception e) {
            //Implement logger here
        }
        editorMouseClickListener = new EditorMouseClickListener();
        editor.addEditorMouseListener(editorMouseClickListener);
    }

    public void analyzeFile() {
        setEnvironmentSettings();
        runCflint();
    }

    private void runCflint() {
        String filePath = file.getPath();

        DefaultListModel listModel = new DefaultListModel();
        DefaultListModel listModelAll = new DefaultListModel();
        DefaultListModel listModelInfo = new DefaultListModel();
        DefaultListModel listModelError = new DefaultListModel();

        int numberOfErrorsErors = 0;
        int numberOfErrorsInfo = 0;
        int numberOfErrorsWarnings = 0;

        errorMessages = new HashMap<>();

        //Remove all highlighters
        editor.getMarkupModel().removeAllHighlighters();

        try {
            String[] arguments = new String[]{"-file", filePath, "-json", "-stdout", ""};

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));
            CFLintMain.main(arguments);
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));


            JSONObject obj = new JSONObject(baos.toString());

            JSONArray arr = obj.getJSONArray("issues");
            String output = "";
            String typeOfError;
            String expression;
            int line;
            int column;
            int i = 0;

            for (i = 0; i < arr.length(); i++) {
                typeOfError = arr.getJSONObject(i).getString("severity");

                JSONArray newArray = arr.getJSONObject(i).getJSONArray("locations");
                numberOfErrors = newArray.length();

                output = "<html>";

                if (typeOfError.equals("WARNING")) {
                    output += String.valueOf(++numberOfErrorsWarnings) + ".";
                } else if (typeOfError.equals("INFO")) {
                    output += String.valueOf(++numberOfErrorsInfo) + ".";
                } else if (typeOfError.equals("ERROR")) {
                    output += String.valueOf(++numberOfErrorsErors) + ".";
                }

                for (int k = 0; k < numberOfErrors; k++) {
                    expression = newArray.getJSONObject(k).getString("expression");
                    line = newArray.getJSONObject(k).getInt("line");
                    column = newArray.getJSONObject(k).getInt("column");
                    output += "<b>" + newArray.getJSONObject(k).getString("message") + "</b> <br />";
                    output += "<span id='expression'>" + expression + "</span>";
                    output += " line and column : ";
                    output += " " + line + ":" + column;
                    output += "</html>";
                    if (typeOfError.equals("WARNING")) {
                        listModel.addElement(output);
                        ++numberOfErrorsWarnings;
                    } else if (typeOfError.equals("INFO")) {
                        listModelInfo.addElement(output);
                        ++numberOfErrorsErors;
                    } else if (typeOfError.equals("ERROR")) {
                        listModelInfo.addElement(output);
                        ++numberOfErrorsInfo;
                    }
                    listModelAll.addElement(output);
                    Highlighter.highlightErrors(line, output);
                    errorMessages.put(line, output);
                }
            }


            listResultsInfo.setModel(listModelInfo);
            listResultsError.setModel(listModelError);
            listResults.setModel(listModel);
            listResultsAll.setModel(listModelAll);

            listResults.addMouseListener(new ListResultsMouseClickListener(listResults));
            listResultsInfo.addMouseListener(new ListResultsMouseClickListener(listResultsInfo));
            listResultsError.addMouseListener(new ListResultsMouseClickListener(listResultsError));
            listResultsAll.addMouseListener(new ListResultsMouseClickListener(listResultsAll));

            tabbedPane1.setTitleAt(0, "All (" + i + ")");
            tabbedPane1.setTitleAt(1, "Warnings (" + String.valueOf(listResults.getModel().getSize()) + ")");
            tabbedPane1.setTitleAt(2, "Info (" + String.valueOf(listResultsInfo.getModel().getSize()) + ")");
            tabbedPane1.setTitleAt(3, "Error (" + String.valueOf(listResultsError.getModel().getSize()) + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProject(){
        project = (Project) DataManager.getInstance().getDataContext().getData(DataConstants.PROJECT);
    }

    public  static Project getProject(){
        return project;
    }

    private void setEditor(){
        editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    }

    public static Editor getEditor(){
        return editor;
    }

    private void setDocument(){
        document = editor.getDocument();
    }

    public static Document getDocument(){
        return document;
    }

    private void setFile(){
        file = FileDocumentManager.getInstance().getFile(document);
    }

    public static VirtualFile getFile(){
        return file;
    }

    public static HashMap<Integer, String> getErrorMessages(){
        return errorMessages;
    }

}
