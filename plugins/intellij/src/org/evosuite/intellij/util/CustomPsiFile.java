package org.evosuite.intellij.util;

import com.intellij.psi.PsiFile;

/**
 * This was created as a work around for psiFiles not having file paths.
 *
 * Created by webby on 25/07/17.
 */
public class CustomPsiFile {
    private PsiFile psiFile;
    private String filePath;

    public CustomPsiFile(PsiFile psiFile, String filePath) {
        this.psiFile = psiFile;
        this.filePath = filePath;
    }

    public PsiFile getPsiFile() {
        return psiFile;
    }

    public void setPsiFile(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
