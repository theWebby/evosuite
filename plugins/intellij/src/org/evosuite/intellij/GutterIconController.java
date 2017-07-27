package org.evosuite.intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.evosuite.intellij.util.CustomPsiFile;
import org.evosuite.intellij.util.Icons;
import org.evosuite.intellij.util.PsiController;
import org.evosuite.intellij.util.TestDef;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class will contain all of the functionality for adding icons to the left gutter.
 *
 * Created by webby on 10/07/17.
 */
public class GutterIconController {

    private Project project;

    //constructor
    public GutterIconController(Project project){
        this.project = project;
    }


    /**
     * Will mark all tested methods in a given editor.
     *
     * Asynchronous with main thread (via. markTestedMethodsInClass)
     *
     * @param editor
     * @param project
     */
    public void markTestedMethodsInEditor(Editor editor, Project project) throws Exception {
        if (editor == null) return;

        //todo make psiFiles a list of all psiTestFiles
        PsiController psiController = new PsiController(project);
        CustomPsiFile[] customPsiFiles = psiController.getTestPsiFiles();

        //todo: check if psiFile is java

        //for each psiFile and then each class within the psiFile, mark the testedMethods
        for (CustomPsiFile customPsiFile : customPsiFiles) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) customPsiFile.getPsiFile();
            PsiClass[] psiClasses = psiJavaFile.getClasses();

            //for each class in the psi file
            for (int i = 0; i < psiClasses.length; i++){
                PsiClass psiClass = psiClasses[i];
                //todo: check if the class is testable and if so...
                markTestedMethodsInClass(psiClass, editor, customPsiFiles[0].getFilePath());
            }
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
    public void markTestedMethodsInClass(PsiClass testedClass, Editor editor, String classFilePath) throws Exception{
        try{
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try{
                        doMarkTestedMethods(testedClass, editor, classFilePath);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }


    private void doMarkTestedMethods(PsiClass testedClass, Editor editor, String classFilePath) throws Exception{
        TestedMethodLocator testedMethodLocator = new TestedMethodLocator(project);
        Document document = editor.getDocument();
        List<TestDef> testDefs = testedMethodLocator.getTestDefs(testedClass, classFilePath);

        //remove all prior changes and find tested methods
        unmark(editor);



        //todo: pass through a marked test and a liked test through testDefs maybe?
//        for (int i = 0; i < testedMethods.length; i++){
//            markTestedMethod(testedMethods[i], document, editor);
//        }




        for (TestDef testDef : testDefs) {

            List<PsiMethod> testedPsiMethods = testDef.getTestedPsiMethods();
            //System.out.println(testedPsiMethods);

            for (PsiMethod testedMethod : testDef.getTestedPsiMethods()) {
                markTestedMethod(testedMethod, document, editor, testDef.getFilePath(), testDef.getTestLineNumber());
            }
        }
    }


    private void markTestedMethod(PsiMethod method, Document document, Editor selectedEditor, String filePath, int testLineNo){
        int line = PsiController.getMethodStartLineNumber(method, document);
        MarkupModel markupModel = selectedEditor.getMarkupModel();
        RangeHighlighter rangeHighlighter = markupModel.addLineHighlighter(line, HighlighterLayer.FIRST, null);
        addTestedMethodGutterIcon(rangeHighlighter, method, filePath, testLineNo);
    }


    private void addTestedMethodGutterIcon(RangeHighlighter rangeHighlighter, final PsiMethod linkedTest, String filePath, int testLineNo){
        rangeHighlighter.setGutterIconRenderer(new GutterIconRenderer() {
            @NotNull
            @Override
            public Icon getIcon() {
                return Icons.getEvoSuiteIcon();
            }

            public String getToolTipText(){
                return "Go to tests for this method";
            }

            public boolean isNavigateAction(){
                return true;
            }

            public AnAction getClickAction(){
                return new GoToTestsAction(filePath, project, testLineNo);
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

    private void unmark(Editor editor){
        if (editor == null){
            return;
        }

        MarkupModel markupModel = editor.getMarkupModel();
        markupModel.removeAllHighlighters();
    }
}
