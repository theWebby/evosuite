package org.evosuite.intellij.util;

import com.github.javaparser.Position;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by webby on 12/07/17.
 */
public class TestVisitor extends VoidVisitorAdapter<List<TestDef>> {
    private String filePath;
    private List<ExpressionStmt> statementBuffer;
    private List<MethodCallExpr> methodCallBuffer;


    public TestVisitor(final String FILE_PATH){
        this.filePath = FILE_PATH;
        this.statementBuffer = new ArrayList<ExpressionStmt>();
        this.methodCallBuffer = new ArrayList<MethodCallExpr>();

    }


    //      VISITS

    /**
     *  Visit Method
     *  Also adds everything in the buffer to a new TestDef
     *
     * @param md
     * @param testDefs
     */
    public void visit(MethodDeclaration md, List<TestDef> testDefs){
        super.visit(md, testDefs);
        TestDef thisTest = new TestDef(md, filePath);

        thisTest.setStatements(this.statementBuffer);
        this.statementBuffer.clear();

        thisTest.setMethodCallExprs(this.methodCallBuffer);
        this.methodCallBuffer.clear();



        thisTest.setTestLineNumber(getMDPosition(md).line);

        testDefs.add(thisTest);
    }


    /**
     * Visit Expression Statement
     *
     * @param stmt
     * @param testDefs
     */
    public void visit(ExpressionStmt stmt, List<TestDef> testDefs){
        super.visit(stmt, testDefs);
        this.statementBuffer.add(stmt);
    }


    public void visit(MethodCallExpr mc, List<TestDef> testDefs){
        super.visit(mc, testDefs);
        this.methodCallBuffer.add(mc);
    }


    //_______HELPERS

    private Position getMDPosition(MethodDeclaration md){
        Optional<Position> positionOptional = md.getBegin();

        if (positionOptional.isPresent()){
            Position pos = positionOptional.get();
            return pos;
        }

        return null;
    }
}