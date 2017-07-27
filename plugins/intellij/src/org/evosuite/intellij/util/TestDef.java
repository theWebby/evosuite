package org.evosuite.intellij.util;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.intellij.psi.PsiMethod;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the information we need from tests
 *
 * Created by webby on 12/07/17.
 */
public class TestDef {
    private MethodDeclaration md;
    private String filePath;
    private int lineNumber;
    private String testSignature;

    private List<ExpressionStmt> statements;
    private List<MethodCallExpr> methodCallExprs;
    private List<PsiMethod> psiMethodCalls;



    public TestDef(MethodDeclaration md, final String FILE_PATH){
        this.md = md;
        this.filePath = FILE_PATH;
        this.psiMethodCalls = new ArrayList<PsiMethod>();
    }


    //      accessors and mutators

    public void setStatements(List<ExpressionStmt> stmts){
        this.statements = new ArrayList<ExpressionStmt>(stmts);
    }


    public void setMethodCallExprs(List<MethodCallExpr> methodCallExprs){
        this.methodCallExprs = new ArrayList<MethodCallExpr>(methodCallExprs);
    }

    public void setTestSignature(String signature){
        this.testSignature = signature;
    }

    public String getTestName(){
        return this.md.getNameAsString();
    }

    public MethodDeclaration getMethodDeclaration(){
        return this.md;
    }

    public List<PsiMethod> getTestedPsiMethods(){
        return this.psiMethodCalls;
    }

    public String getFilePath(){
        return this.filePath;
    }

    public void setTestLineNumber(int lineNumber){
        this.lineNumber = lineNumber;
    }

    public int getTestLineNumber(){
        return this.lineNumber;
    }






    //todo this is repeated
//    private String filePath2Directory(String filePath){
//        StringBuilder directoryPath = new StringBuilder();
//        String[] dirs = filePath.split("/");
//
//        //assuming the last element is a file not a directory
//        for (int i = 0; i < dirs.length - 1; i++) {
//            directoryPath.append("/");
//            directoryPath.append(dirs[i]);
//        }
//
//        return directoryPath.toString();
//    }


    //------------------------------------------------------------------------------------------------------------------
    //      HELPER METHODS

    /**
     * Will find the PsiMethod for the test and the PsiMethods for all the method calls
     *
     * This must be called after visiting to get TestDef as .
     */
    public void findPsiMethods(PsiController psiController) throws Exception{
        //System.out.println(this.getMethodDeclaration());
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        //todo: URGENT! MUST MAKE THIS WORK AUTOMATICALLY
        typeSolver.add(new JarTypeSolver("/opt/idea-IU-171.4694.23/lib/junit.jar"));
        typeSolver.add(new JarTypeSolver("/opt/idea-IU-171.4694.23/lib/junit-4.12.jar"));

        //System.out.println(psiController.dropLastElement(PsiController.dropLastElement(filePath, "/"), "/"));
        typeSolver.add(new JavaParserTypeSolver(new File(psiController.dropLastElement(filePath, "/"))));
        //typeSolver.add(new JavaParserTypeSolver(new File("/home/webby/IdeaProjects/project00/src/test")));
        typeSolver.add(new JavaParserTypeSolver(new File("/home/webby/IdeaProjects/project00/src/")));

        //todo get the psiMethod of the test
        psiController.getClassPsiMethodsByFilePath(filePath); //PsiMethod[] tests =


        makeMethodCallsDistinct();



        //_____method calls
        //for each method call, solve it, find the psiMethod
        for (MethodCallExpr methodCallExpr : methodCallExprs) {
            String qualifiedSignature = JavaParserFacade.get(typeSolver).solve(methodCallExpr).getCorrespondingDeclaration().getQualifiedSignature();
            PsiMethod thisMethodCall = psiController.getPsiMethod(qualifiedSignature);
            psiMethodCalls.add(thisMethodCall);
        }
    }

    private void makeMethodCallsDistinct(){
        if (methodCallExprs.size() > 0){
            List<MethodCallExpr> distinctMethodCallExprs = new ArrayList<MethodCallExpr>();
            distinctMethodCallExprs.add(methodCallExprs.get(0));

            //only add elements that are not already in the list
            for (int i = 1; i < methodCallExprs.size(); i++) {
                if (!distinctMethodCallExprs.contains(methodCallExprs.get(i))) distinctMethodCallExprs.add(methodCallExprs.get(i));
            }

            this.methodCallExprs = distinctMethodCallExprs;
        }
    }


    public String toString(){
        StringBuilder s = new StringBuilder();

        s.append("\n_____________\n############\n");
        s.append("Test Name: " + this.getTestName() + " - (" + filePath + ")\n");
        s.append("Test Sig: " + this.testSignature + "\n");

        s.append("\n ### STATEMENTS ###\n");
        statements.forEach(statement -> {
            s.append(statement.toString() + "\n");
        });

        s.append("\n ### METHOD CALLS ###\n");
        methodCallExprs.forEach(methodCall -> {
            s.append(methodCall.toString() + "\n");
        });

        s.append("\n ### METHOD CALL PSI's ###\n");
        psiMethodCalls.forEach( psiMethodCall ->{
            s.append(psiMethodCall + "\n");
        });


        s.append("#########################");

        return s.toString();
    }
}
