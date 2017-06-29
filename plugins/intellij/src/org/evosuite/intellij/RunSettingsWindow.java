package org.evosuite.intellij;

import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

import static javafx.application.Application.launch;

/**
 * THIS IS A TEMP CLASS
 *
 * this was built to run the gui without starting IDEA IDE
 *
 * Created by webby on 29/06/17.
 */
public class RunSettingsWindow {
    //this is the EvoSettingsAction.actionPerformed method
    public static void main(String[] args){
        EvoSettingsGUI gui = new EvoSettingsGUI();

        String title = "EvoSuite Plugin";
        //Project project = (Project) PlatformDataKeys.PROJECT;

        EvoSettingsGUI settingsWindow = new EvoSettingsGUI();
        launch(settingsWindow.getClass());

        if (EvoParameters.getInstance().getGuiWasOK()){
            System.out.println("Yeah");
        }
        else{
            System.out.println("Nah bruv");
        }
    }
}
