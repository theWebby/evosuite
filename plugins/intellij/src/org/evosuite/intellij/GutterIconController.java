package org.evosuite.intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.evosuite.intellij.util.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Iterator;
import java.util.Set;

/**
 * This class will contain all of the functionality for adding icons to the left gutter.
 *
 * Created by webby on 10/07/17.
 */
public class GutterIconController {
    private TestedMethodLocator testedMethodLocator;


    //constructor
    public GutterIconController(TestedMethodLocator testedMethodLocator){
        this.testedMethodLocator = testedMethodLocator;
    }


    /**
     * Will mark all tested methods in a given editor.
     *
     * Asynchronous with main thread (via. markTestedMethodsInClass)
     *
     * @param editor
     * @param project
     */
    public void markTestedMethodsInEditor(Editor editor, Project project){
        if (editor == null) return;

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile psiFile = psiDocumentManager.getPsiFile(editor.getDocument());

        //todo: check if psiFile is java

        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        PsiClass[] psiClasses = psiJavaFile.getClasses();
        for (int i = 0; i < psiClasses.length; i++){
            PsiClass psiClass = psiClasses[i];
            //todo: check if the class is testable and if so...
            markTestedMethodsInClass(psiClass, editor);
        }

    }

    /**
     * Will mark all tested methods in a given class.
     *
     * Asynchronous with main thread
     *
     * @param testedClass
     * @param editor
     */
    public void markTestedMethodsInClass(PsiClass testedClass, Editor editor){
        try{
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    doMarkTestedMethods(testedClass, editor);
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }


    private void doMarkTestedMethods(PsiClass testedClass, Editor editor){
        TestedMethodLocator testedMethodLocator = new TestedMethodLocator();
        Document document = editor.getDocument();
        PsiMethod[] testedMethods = testedMethodLocator.getTestedMethods(testedClass);

        //remove all prior changes and find tested methods
        unmark(editor);

        for (int i = 0; i < testedMethods.length; i++){
            markTestedMethod(testedMethods[i], document, editor);
        }
    }


    private void unmark(Editor editor){
        if (editor == null){
            return;
        }

        MarkupModel markupModel = editor.getMarkupModel();
        markupModel.removeAllHighlighters();
    }


    private void markTestedMethod(PsiMethod method, Document document, Editor selectedEditor){
        int line = PsiController.getMethodStartLineNumber(method, document);
        MarkupModel markupModel = selectedEditor.getMarkupModel();
        RangeHighlighter rangeHighlighter = markupModel.addLineHighlighter(line, HighlighterLayer.FIRST, null);
        addTestedMethodGutterIcon(rangeHighlighter, method);
    }


    private void addTestedMethodGutterIcon(RangeHighlighter rangeHighlighter, final PsiMethod method){
        rangeHighlighter.setGutterIconRenderer(new GutterIconRenderer() {
            @NotNull
            @Override
            public Icon getIcon() {
                return Icons.getEvoSuiteIcon();
            }

            public String getToolTipText(){
                return "Go to tests for " + method.getName();
            }

            public boolean isNavigateAction(){
                return true;
            }

            public AnAction getClickAction(){
                return new GoToTestsAction(method);
            }

            @Override
            public boolean equals(Object o) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }
        });
    }
}
