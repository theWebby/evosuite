package org.evosuite.intellij.util;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.evosuite.intellij.EvoSettingsAction;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by webby on 26/06/17.
 *
 * For shared methods between actions, ie. loadIcon
 */
public class AnEvoAction extends AnAction {
    public AnEvoAction(String name, String description){
        super(name, description, Icons.getEvoSuiteIcon());
    }

    public void actionPerformed(AnActionEvent event){

    }
}
