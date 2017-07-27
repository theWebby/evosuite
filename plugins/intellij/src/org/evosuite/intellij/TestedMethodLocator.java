package org.evosuite.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.evosuite.intellij.util.JavaParserController;
import org.evosuite.intellij.util.PsiController;
import org.evosuite.intellij.util.TestDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by webby on 10/07/17.
 */
public class TestedMethodLocator {

    private Project project;

    public TestedMethodLocator(Project project){
        this.project = project;
    }


    /**
     * Returns a list of tested methods (psiMethods) from a given test PsiClass
     *
     * @param psiClass
     * @return
     */
    public List<TestDef> getTestDefs(PsiClass psiClass, String classFilePath) throws Exception{
        PsiController psiController = new PsiController(project);
        JavaParserController jpc = new JavaParserController(project);

        List<TestDef> testDefs = jpc.getTestDefs(classFilePath);
        for (TestDef testDef : testDefs) {
            //System.out.println(testDef.toString());
            testDef.findPsiMethods(psiController);
        }

        return testDefs;
    }
}


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