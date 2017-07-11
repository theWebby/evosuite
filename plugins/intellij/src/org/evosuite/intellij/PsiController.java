package org.evosuite.intellij;

import com.intellij.psi.PsiMethod;
import com.intellij.openapi.editor.Document;

/**
 * Created by webby on 10/07/17.
 */
public class PsiController {

    public static int getMethodStartLineNumber(PsiMethod method, Document document){
        int offset = method.getNameIdentifier().getTextOffset();
        return document.getLineNumber(offset);
    }
}
