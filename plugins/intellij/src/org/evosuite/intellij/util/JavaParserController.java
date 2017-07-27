package org.evosuite.intellij.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to handle all Java Parse visitors functionality
 *
 * Created by webby on 18/07/17.
 */
public class JavaParserController {
    private Project project;

    public JavaParserController(Project project){
        this.project = project;
    }

    /**
     *  Returns a list of TestDefinitions
     *
     * @return
     */
    public List<TestDef> getTestDefs(String testDir) throws Exception{
        CompilationUnit cu = JavaParser.parse(new FileInputStream(testDir));
        List<TestDef> testDefs = new ArrayList<>();
        VoidVisitor<List<TestDef>> testVisitor = new TestVisitor(testDir);
        testVisitor.visit(cu, testDefs);

        return testDefs;
    }
}
