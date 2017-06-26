package org.evosuite.intellij.util;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.evosuite.intellij.EvoParameters;
import org.evosuite.intellij.EvoSettingsAction;
import org.evosuite.intellij.IntelliJNotifier;
import org.evosuite.intellij.ModulesInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by webby on 26/06/17.
 */
public class EvoRunAction extends AnEvoAction {
    public EvoRunAction(){
        super("Run", "Start running EvoSuite to generate JUnit tests automatically");
    }


    public void actionPerformed(AnActionEvent event){
        String title = "EvoSuite Plugin";
        Project project = event.getData(PlatformDataKeys.PROJECT);
        final AsyncGUINotifier notifier = IntelliJNotifier.getNotifier(project);
        Map<String,Set<String>> map = getCUTsToTest(event);
        if(map==null || map.isEmpty() || map.values().stream().mapToInt(Set::size).sum() == 0){
            Messages.showMessageDialog(project, "No '.java' file or non-empty source folder was selected in a valid module",
                    title, Messages.getErrorIcon());
            return;
        }

        if (EvoSuiteExecutor.getInstance().isAlreadyRunning()) {
            Messages.showMessageDialog(project, "An instance of EvoSuite is already running",
                    title, Messages.getErrorIcon());
            return;
        }

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("EvoSuite");

        toolWindow.show(() -> notifier.clearConsole());
        EvoSuiteExecutor.getInstance().run(project, EvoParameters.getInstance(),map,notifier);
    }




    /**
     *
     *
     * @return a map where key is a maven module root path, and value a list of full class names of CUTs
     */
    private Map<String, Set<String>> getCUTsToTest(AnActionEvent event){

        Map<String,Set<String>> map = new LinkedHashMap<>();

        Project project = event.getData(PlatformDataKeys.PROJECT);

        ModulesInfo modulesInfo = new ModulesInfo(project);

        if (! modulesInfo.hasRoots()){
            return null;
        }

        Set<String> alreadyHandled = new LinkedHashSet<>();

        for(VirtualFile virtualFile : event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)){
            String selectedFilePath = new File(virtualFile.getCanonicalPath()).getAbsolutePath();
            recursiveHandle(map, modulesInfo, alreadyHandled, selectedFilePath);
        }

        return map;
    }


    private void recursiveHandle(Map<String,Set<String>> map, ModulesInfo modulesInfo, Set<String> alreadyHandled, String path){

        if(alreadyHandled.contains(path)){
            return;
        }

        Set<String> skip = handleSelectedPath(map, modulesInfo, path);
        alreadyHandled.add(path);

        for(String s : skip){
            recursiveHandle(map, modulesInfo, alreadyHandled, s);
        }
    }


    private Set<String> handleSelectedPath(Map<String, Set<String>> map, ModulesInfo modulesInfo, String selectedFilePath) {

         /*
                if Module A includes sub-module B, the source roots in B should
                not be marked for A
             */
        Set<String> skip = new LinkedHashSet<>();

        String module = modulesInfo.getModuleFolder(selectedFilePath);
        File selectedFile = new File(selectedFilePath);

        if(module == null){
            return skip;
        }

        Set<String> classes = map.getOrDefault(module, new LinkedHashSet<>());

        String root = modulesInfo.getSourceRootForFile(selectedFilePath);

        if(root == null){
            /*
                the chosen file is not in a source folder.
                Need to check if its parent of any of them
             */
            Set<String> included = modulesInfo.getIncludedSourceRoots(selectedFilePath);
            if(included==null || included.isEmpty()){
                return skip;
            }

            for(String otherModule : modulesInfo.getModulePathsView()) {

                if(otherModule.length() > module.length() && otherModule.startsWith(module)) {
                    //the considered module has a sub-module
                    included.stream().filter(inc -> inc.startsWith(otherModule)).forEach(skip::add);
                }
            }

            for(String sourceFolder : included){
                if(skip.contains(sourceFolder)){
                    continue;
                }
                scanFolder(new File(sourceFolder),classes,sourceFolder);
            }

        } else {
            if(!selectedFile.isDirectory()){
                if(!selectedFilePath.endsWith(".java")){
                    // likely a resource file
                    return skip;
                }

                String name = getCUTName(selectedFilePath, root);
                classes.add(name);
            } else {
                scanFolder(selectedFile,classes,root);
            }

        }

        if(! classes.isEmpty()) {
            map.put(module, classes);
        }

        return skip;
    }


    private void scanFolder(File file, Set<String> classes, String root) {
        for(File child : file.listFiles()){
            if(child.isDirectory()){
                scanFolder(child, classes, root);
            } else {
                String path = child.getAbsolutePath();
                if(path.endsWith(".java")){
                    String name = getCUTName(path,root);
                    classes.add(name);
                }
            }
        }
    }


    private String getCUTName(String path, String root) {
        String name = path.substring(root.length()+1, path.length() - ".java".length());
        name = name.replace('/','.'); //posix
        name = name.replace("\\", ".");  // windows
        return name;
    }
}
