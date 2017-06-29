/**
 * Copyright (C) 2010-2017 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.evosuite.intellij.util.AnEvoAction;
import org.evosuite.intellij.util.AsyncGUINotifier;
import org.evosuite.intellij.util.EvoSuiteExecutor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static javafx.application.Application.launch;

/**
 * Created by arcuri on 9/24/14.
 */
public class EvoSettingsAction extends AnEvoAction {

    public EvoSettingsAction() {
        super("Settings", "Open GUI dialog to configure EvoSuite.");
    }

    /**
     * Creates a new instance of the Settings window.
     * If the user clicks ok, the settings are saved.
     *
     * @param event
     */
    public void actionPerformed(AnActionEvent event) {

//        String title = "EvoSuite Plugin";
//        Project project = event.getData(PlatformDataKeys.PROJECT);
//
//        EvoStartDialog dialog = new EvoStartDialog();
//        dialog.initFields(project, EvoParameters.getInstance());
//        dialog.setModal(true);
//        dialog.setLocationRelativeTo(null);
//        //dialog.setLocationByPlatform(true);
//        dialog.pack();
//        dialog.setVisible(true);
//
//        if (dialog.isWasOK()) {
//            EvoParameters.getInstance().save(project);
//        }

        String title = "EvoSuite Plugin";
        Project project = event.getData(PlatformDataKeys.PROJECT);

        EvoSettingsGUI settingsWindow = new EvoSettingsGUI();
        launch(settingsWindow.getClass());

        if (settingsWindow.isWasOK()) {
            System.out.println("OK");
        }
        else
        {
            System.out.println("Nah dont save it bruv");
        }


    }
}
