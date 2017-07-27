package org.evosuite.intellij;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.OpenSourceUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.evosuite.intellij.util.TestDef;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by webby on 10/07/17.
 */
public class GoToTestsAction extends AnAction {

    private Project project;
    private String filePath;
    private int lineNumber;
    private Document document;


    public GoToTestsAction(String filePath, Project project, int lineNumber){
        this.filePath = filePath;
        this.project = project;
        this.lineNumber = lineNumber;
    }


    /**
     * Finds the virtual file from the filepath in testDef
     * Opens the virtual file at the offset of the test in testDef
     *
     * @param anActionEvent
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        System.out.println("fp: " + filePath);

        //find the file from the filePath
//        Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(project, filePath,
//                GlobalSearchScope.allScope(project));

//        Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME,
//                JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));

        VirtualFile file = VirtualFileManager.getInstance().getFileSystem(StandardFileSystems.FILE_PROTOCOL).findFileByPath(filePath);
        this.document = FileDocumentManager.getInstance().getDocument(file);
        System.out.println(file);




        //todo: find the offset of the test we need to link to and lob it in OpenFileDescriptor

        OpenFileDescriptor testFile = new OpenFileDescriptor(project, file, document.getLineStartOffset(lineNumber)); //offset goes here
        OpenSourceUtil.navigate(true, testFile);

    }

//    private int lineNumberToOffset(int lineNumber, VirtualFile file){
//        OpenFileDescriptor fileDescriptor;
//
//        for (long offset = 0; offset < file.getLength(); offset += 5) {
//            fileDescriptor = new OpenFileDescriptor(project, file, (int) offset);
//
//            System.out.println(fileDescriptor.getLine());
//
//            if (fileDescriptor.getLine() >= lineNumber){
//                while(fileDescriptor.getLine() != lineNumber){
//                    offset--;
//                }
//
//                return (int) offset;
//            }
//        }
//        return 0;
//    }
}
