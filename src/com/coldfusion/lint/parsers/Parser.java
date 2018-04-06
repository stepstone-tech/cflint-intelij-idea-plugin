package com.coldfusion.lint.parsers;

import com.coldfusion.lint.LintAnalyzer;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private VirtualFile file;

    public Parser(){
        this.file = LintAnalyzer.getFile();
    }

    public static String generateHint(String expression) {
        StringBuilder comment = new StringBuilder("/**\n");


        String[] content = expression.split("\\(");
        String functionName = content[0].replace("function", "Method ");
        String functionArgs = content[1];
        String argRequired = "";
        String argType = "";
        String argName = "";
        String argValue = "";

        String[] argsInFunction = functionArgs.split(",");

        comment.append("* ").append(functionName).append("\n");
        String[] argsElements;
        for (String anArgsInFunction : argsInFunction) {
            String argsInFunctionStr = anArgsInFunction;
            argsInFunctionStr = argsInFunctionStr.replace("{", "");
            argsInFunctionStr = argsInFunctionStr.replace(")", "");
            argsInFunctionStr = argsInFunctionStr.trim().replaceAll("\\s+", " ");

            if (anArgsInFunction.contains(anArgsInFunction)) {
                argRequired = "required";
                argsInFunctionStr.replaceAll(argRequired + "\\s?\\s+", "");
            }

            argsElements = argsInFunctionStr.split(" ");

            argName = argsElements[0];

            if (argsElements.length > 1) {
                argName = argsElements[1];
                argType = argsElements[0];
            }

            if (argName.length() > 0) {
                comment.append("* @")
                        .append(argName)
                        .append(" ").append(argType)
                        .append(" ").append(argValue)
                        .append(" ").append(argRequired)
                        .append("\n");
            }
        }
        comment.append("*/\n");
        return comment.toString();
    }

    public static String findPhrase(String expression, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(expression);
        String match = "";
        while (m.find()) {
            match = m.group();
        }
        return match;
    }

    public String getResults(String expression) {
        String msg = "";
        String expMsg;
        String missingHint = findPhrase(expression, ".*Function.*is missing a hint.*");
        String componentMissingHint = findPhrase(expression, ".*Component.*is missing a hint.*");

        if (missingHint.length() > 0) {
            expMsg = findPhrase(missingHint, "function.*(.*)");
            if (expMsg.length() > 0) {
                msg = generateHint(expMsg);
            }
        }
        if (componentMissingHint.length() > 0) {
            msg = generateComponentHint();
        }
        new LintAnalyzer().analyzeFile();
        return msg;
    }

    private String generateComponentHint() {
        String comment = "/**\n";
        String componentName;
        componentName = file.getName();
        componentName = componentName.replace(".cfm", "");
        componentName = componentName.replace(".cfc", "");
        componentName = componentName.replace(".cfml", "");
        comment += "* Component " + componentName + "\n";
        comment += "*/\n";
        return comment;
    }

    public static String[] parseLineAndColumn(String data) {
        Pattern p = Pattern.compile("[0-9]{1,10}:[0-9]{1,10}");
        Matcher m = p.matcher(data);
        String match = "";
        while (m.find()) {
            match = m.group();
        }
        return match.split(":");
    }
}
