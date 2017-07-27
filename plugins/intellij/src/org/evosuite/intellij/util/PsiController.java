package org.evosuite.intellij.util;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Useful:
 *      - String[] allFileNames = FilenameIndex.getAllFilenames(project);
 *
 * Created by webby on 15/07/17.
 */
public class PsiController {
    private Project project;
    private PsiDirectory psiDirectory;

    public PsiController(Project project){
        this.project = project;
        psiDirectory = getCacheDir(project);

    }


    /**
     * Ok so we know that the methodName = println
     * we also need methodParams = (int) / (String) / ect.
     *
     * @param qualifiedMethodName (i.e. java.io.PrintStream.println(int))
     * @return
     */
    public PsiMethod getPsiMethod(String qualifiedMethodName){
        String qualifiedClassName = getClassName(qualifiedMethodName);
        PsiMethod[] classPsiMethods = getClassPsiMethods(project, qualifiedClassName);

        //create the target method signiture
        String[] methodParamSplit = qualifiedMethodName.split("\\("); //{[java.io.PrintStream.println],["int)"]}
        String[] methodNameSplit = methodParamSplit[0].split("\\."); //{[java],[io],[PrintStream],[println]}
        String methodSigniture = methodNameSplit[methodNameSplit.length - 1] + "(" + methodParamSplit[1]; //i.e. println + ( + int) = println(int)

        StringBuilder thisMethodSigniture;

        //for each method in the class, create a method signiture (i.e. println(int))
        for (PsiMethod psiMethod : classPsiMethods) {
            thisMethodSigniture = new StringBuilder();

            //get all the params for the method
            PsiParameterList psiMethodParamsList = psiMethod.getParameterList();
            PsiParameter[] psiMethodParamsArray = psiMethodParamsList.getParameters();

            //add the name to the string
            thisMethodSigniture.append(psiMethod.getName());
            thisMethodSigniture.append("(");

            //add params to the string
            for (int i = 0; i < psiMethodParamsArray.length; i++) {
                thisMethodSigniture.append(psiMethodParamsArray[i].getType().getCanonicalText());

                if (i != psiMethodParamsArray.length - 1) thisMethodSigniture.append(", ");
            }
            thisMethodSigniture.append(")");
            if (thisMethodSigniture.toString().equals(methodSigniture)) {
                return psiMethod;
            }
        }

        return null;
    }


//    public PsiMethod getPsiMethod(TestDef testDef){
//        MethodDeclaration md = testDef.getMethodDeclaration();
//        System.out.println("Method Declaration: " + md);
//    }

    /**
     * Method to get all PsiMethods from a qualified class name (ie. System.out.println() == java.io.PrintStream.println())
     *
     * @param qualifiedClassName
     * @return List of PsiMethods in a given P
     */
    public static PsiMethod[] getClassPsiMethods(Project project, String qualifiedClassName){
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(qualifiedClassName, GlobalSearchScope.allScope(project));
        return psiClass.getAllMethods();
    }

    //todo return list of psiMehtod[]
    public void getClassPsiMethodsByFilePath(String filePath){
        PsiFile psiFile = PsiManager.getInstance(project).findFile(LocalFileSystem.getInstance().findFileByPath(filePath));

        System.out.println(psiFile);

    }



    private static PsiDirectory getCacheDir(Project project) {
        File dirName = new File(project.getBasePath(), ".idea/gits");
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(project.getBasePath(), ".idea/gits"));
        if (directory == null) {
            try {
                //LOG.info("dir is null" + dirName.getPath());
                directory = VfsUtil.createDirectories(dirName.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return PsiManager.getInstance(project).findDirectory(directory);
    }

    /**
     * Returns a collection of all PsiFiles that contain tests from a given project.
     *
     * Note: this method will only look for tests ending in ESTest, excluding _scaffolding
     *
     * @return
     */
    public CustomPsiFile[] getTestPsiFiles(){
        /**
         *  todo getting all the psiFiles takes some time, try stepping over it to see how long it takes-
         *  maybe run asyncro?
         */


        Collection<VirtualFile> allPsiFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME,
                JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        List<VirtualFile> testVirtualFiles = new ArrayList<VirtualFile>();

        Iterator<VirtualFile> iterator = allPsiFiles.iterator();
        VirtualFile thisVirtualFile;
        while(iterator.hasNext()){
            thisVirtualFile = iterator.next();

            if (thisVirtualFile.getName().contains("ESTest.java")){
                //if the tests contains ESTest.java not ESTest_scaffolding,java, then it's an Evo generated test
                testVirtualFiles.add(thisVirtualFile);

            }
        }

        CustomPsiFile[] psiTestFiles = new CustomPsiFile[testVirtualFiles.size()];
        PsiFile thisPsiFile;
        
        for (int i = 0; i < testVirtualFiles.size(); i++) {
            thisPsiFile = PsiManager.getInstance(project).findFile(testVirtualFiles.get(i));
            psiTestFiles[i] = new CustomPsiFile(thisPsiFile, testVirtualFiles.get(i).getPath());
        }
        
        return psiTestFiles;
    }

    //todo: is static? an removed project param
    public static int getMethodStartLineNumber(PsiMethod method, Document document){
        //todo: for some reason finding the start line number from the psiMethod gives random numbers

        int offset = method.getNameIdentifier().getStartOffsetInParent() + method.getStartOffsetInParent();
        return document.getLineNumber(offset);
    }


    public static String dropLastElement(String s, String delimiter){

        StringBuilder s2 = new StringBuilder();
        String[] dirs = s.split(delimiter.equals(".") ? "\\." : delimiter);

        //todo: tidy the whole escaped params thing with this delimiter
        if (delimiter.equals("\\/")) delimiter = "/";

        //assuming the last element is a file not a directory
        for (int i = 0; i < dirs.length - 1; i++) {
            s2.append(dirs[i]);

            //if we aren't on the last
            if (i != dirs.length - 2) s2.append(delimiter);
        }

        return s2.toString();
    }

    /**
     * Assuming the class name is in the form of a qualified signiture:
     *
     * java.io.PrintStream.println(java.lang.Object)
     *
     * @param s
     */
    private static String getClassName(String s){
        String[] split = s.split("\\(");
        return dropLastElement(split[0], ".");
    }
}
