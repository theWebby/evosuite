package org.evosuite.intellij.util;

import org.evosuite.intellij.EvoSettingsAction;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by webby on 10/07/17.
 */
public class Icons {
    public static Icon getEvoSuiteIcon(){
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
