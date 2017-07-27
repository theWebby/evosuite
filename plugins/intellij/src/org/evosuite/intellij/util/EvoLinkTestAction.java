package org.evosuite.intellij.util;

import com.github.javaparser.ast.visitor.VoidVisitor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import org.evosuite.intellij.GutterIconController;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by webby on 16/07/17.
 */
public class EvoLinkTestAction extends AnEvoAction {
    private ArrayList<PsiMethodPair> methodPairs;

    public EvoLinkTestAction(){
        super("Add gutter icons to methods that link to their evosuite tests.", "Test");
        methodPairs = new ArrayList<PsiMethodPair>();
    }

    public void actionPerformed(AnActionEvent actionEvent){
        try {
            linkTests(actionEvent.getProject(), actionEvent.getData(PlatformDataKeys.EDITOR));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void linkTests(Project project, Editor editor) throws Exception{
//        PsiController psiController = new PsiController(project);
//        PsiMethod[] psiMethods = psiController.getClassPsiMethods("java.io.PrintStream.println");
//        PsiMethod printlnMethod = psiController.getPsiMethod("java.io.PrintStream.println(java.lang.String)");


        GutterIconController gutterIconController = new GutterIconController(project);
        gutterIconController.markTestedMethodsInEditor(editor, project);


//        PsiController psiController = new PsiController(project);
//        List<PsiMethod> testMethods = new ArrayList<PsiMethod>();
//
//        JavaParserController jpc = new JavaParserController(project);
//        List<TestDef> testDefs = jpc.getTestDefs(testDir);
//
//        //finds the psi method for the test and its method calls
//        for (int i = 0; i < testDefs.size(); i++) {
//            testDefs.get(i).findPsiMethods(psiController);
//            //System.out.println(testDefs.get(i).toString());
//        }






    }
}
