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

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

/**
 *  Entry point for the IntelliJ plugin for when IntelliJ starts
 *
 * Created by arcuri on 11/1/14.
 */
public class ApplicationRegistration implements ApplicationComponent {
    @Override
    public void initComponent() {
        // Gets an instance of the WindowMenu action group.
        //DefaultActionGroup windowM = (DefaultActionGroup) am.getAction("WindowMenu");
        //this in the file editor, not the left-pane file selection
        //DefaultActionGroup editorM = (DefaultActionGroup) am.getAction("EditorPopupMenu");

        ActionManager am = ActionManager.getInstance();
        EvoSettingsAction evoSettingsAction = new EvoSettingsAction();
        am.registerAction("Evo Options", evoSettingsAction);

        DefaultActionGroup evoGroup = new DefaultActionGroup("EvoSuite", true);
        evoGroup.add(evoSettingsAction);



        //get the menu's and add a separator
        DefaultActionGroup pvM = (DefaultActionGroup) am.getAction("ProjectViewPopupMenu");
        DefaultActionGroup epM = (DefaultActionGroup) am.getAction("EditorPopupMenu");
        pvM.addSeparator();
        epM.addSeparator();

        pvM.add(evoGroup);
        epM.add(evoGroup);
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "EvoSuite Plugin";
    }
}
