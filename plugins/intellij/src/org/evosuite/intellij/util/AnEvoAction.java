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
        super(name, description, loadIcon());
    }

    public void actionPerformed(AnActionEvent event){

    }

    private static Icon loadIcon(){
        try {
            Image image = ImageIO.read( EvoSettingsAction.class.getClassLoader().getResourceAsStream( "evosuite.png" ));
            image = image.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon icon =  new ImageIcon(image);

            return icon;
        } catch (IOException e) {
            e.printStackTrace(); //should not really happen
        }
        return null;
    }
}
