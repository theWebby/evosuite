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

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import org.evosuite.intellij.util.Utils;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * Created by arcuri on 9/29/14.
 */
public class EvoParameters {

    public static final String CORES_EVOSUITE_PARAM = "cores_evosuite_param";
    public static final String TIME_EVOSUITE_PARAM = "time_evosuite_param";
    public static final String MEMORY_EVOSUITE_PARAM = "memory_evosuite_param";
    public static final String TARGET_FOLDER_EVOSUITE_PARAM = "target_folder_evosuite_param";
    public static final String MVN_LOCATION = "mvn_location";
    public static final String JAVA_HOME = "JAVA_HOME";
    public static final String EVOSUITE_JAR_LOCATION = "evosuite_jar_location";
    public static final String EXECUTION_MODE = "execution_mode";
    public static final String GUI_DIALOG_WIDTH = "evosuite_gui_dialog_width";
    public static final String GUI_DIALOG_HEIGHT = "evosuite_gui_dialog_height";
    public static final String NUMBER_OF_ADVANCED_PARAMS = "number_of_advanced_params";
    public static final String NUMBER_OF_CONFIGURATIONS = "number_of_configurations";
    public static final String ADVANCED_PARAM = "advanced_param_";  //number appended
    public static final String CONFIGURATION = "configuration_";    //number appended
    public static final String CURRENT_CONFIGURATION = "current_configuration_";

    public static final String EXECUTION_MODE_MVN = "JAR";
    public static final String EXECUTION_MODE_JAR = "MVN";

    private static final EvoParameters singleton = new EvoParameters();

    private int cores;
    private int memory;
    private int time;
    private String folder;
    private String mvnLocation;
    private String javaHome;
    private String evosuiteJarLocation;
    private String executionMode;
    private DefaultListModel<String> advancedParams;
    private DefaultListModel<String> configs;
    private String currentConfigName;
    private int guiWidth;
    private int guiHeight;

    private final int MIN_GUI_WIDTH = 500;
    private final int MIN_GUI_HEIGHT = 340;


    public static EvoParameters getInstance(){
        return singleton;
    }

    private EvoParameters(){
    }

    public boolean usesMaven(){
        return executionMode.equals(EXECUTION_MODE_MVN);
    }

    public void load(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        configs = new DefaultListModel<String>();

        int numberOfConfigs = properties.getInt(NUMBER_OF_CONFIGURATIONS, -1);
        if (numberOfConfigs == -1){
            // no configurations found
            numberOfConfigs = 1;
            configs.addElement("Configuration 1");
        }
        else{
            // load all of the configuration names
            for (int i = 0; i < numberOfConfigs; i++){
                configs.addElement(properties.getValue(CONFIGURATION + i));
            }
        }

        currentConfigName = properties.getValue(CURRENT_CONFIGURATION, "");

        if (currentConfigName.equals("")){
            //load the first configuration
            loadConfiguration(project, configs.get(0));
        }
        else{
            loadConfiguration(project, currentConfigName);
        }


    }

    public void loadConfiguration(Project project, String configName){
        this.currentConfigName = configName;

        PropertiesComponent p = PropertiesComponent.getInstance(project);
        cores = p.getInt(configName + CORES_EVOSUITE_PARAM,1);
        memory = p.getInt(configName + MEMORY_EVOSUITE_PARAM,2000);
        time = p.getInt(configName + TIME_EVOSUITE_PARAM,3);
        folder = p.getValue(configName + TARGET_FOLDER_EVOSUITE_PARAM, "src/evo");

        String envJavaHome = System.getenv("JAVA_HOME");
        javaHome = p.getValue(configName + JAVA_HOME, envJavaHome!=null ? envJavaHome : "");
        mvnLocation = p.getValue(configName + MVN_LOCATION,"");
        evosuiteJarLocation = p.getValue(configName + EVOSUITE_JAR_LOCATION,"");
        executionMode = p.getValue(configName + EXECUTION_MODE,EXECUTION_MODE_MVN);

            advancedParams = new DefaultListModel<String>();

        //loading advanced parameters
        for (int i = 0; i < p.getInt(configName + "_" + NUMBER_OF_ADVANCED_PARAMS, 0); i++){
            advancedParams.addElement(p.getValue(configName + "_" + ADVANCED_PARAM + i));
        }

        guiWidth = p.getInt(configName + GUI_DIALOG_WIDTH, MIN_GUI_WIDTH);       //default is minimum
        guiHeight = p.getInt(configName + GUI_DIALOG_HEIGHT, MIN_GUI_HEIGHT);    //default is minimum

        setGUIMinimums();
    }

    public void save(Project project){
        PropertiesComponent p = PropertiesComponent.getInstance(project);
        saveConfigurationNames(project, 0);

        p.setValue(currentConfigName + CORES_EVOSUITE_PARAM,""+cores);
        p.setValue(currentConfigName + TIME_EVOSUITE_PARAM,""+time);
        p.setValue(currentConfigName + MEMORY_EVOSUITE_PARAM,""+memory);
        p.setValue(currentConfigName + TARGET_FOLDER_EVOSUITE_PARAM,folder);
        p.setValue(currentConfigName + JAVA_HOME,javaHome);
        p.setValue(currentConfigName + MVN_LOCATION,getPossibleLocationForMvn());
        p.setValue(currentConfigName + EVOSUITE_JAR_LOCATION,evosuiteJarLocation);
        p.setValue(currentConfigName + EXECUTION_MODE,executionMode);
        p.setValue(currentConfigName + GUI_DIALOG_WIDTH,""+guiWidth);
        p.setValue(currentConfigName + GUI_DIALOG_HEIGHT,""+guiHeight);

        //saving advanced parameters
        p.setValue(currentConfigName + "_" + NUMBER_OF_ADVANCED_PARAMS, ""+advancedParams.size());
        for (int i = 0; i < advancedParams.size(); i++){
            p.setValue(currentConfigName + "_" + ADVANCED_PARAM + i, advancedParams.get(i));
        }
    }

    /**
     * Will save all of the configuration names that exist.
     * If removeLastXElements is 0, all configuration names will be saved.
     * If removeLastXElements is 1, all but the last configuration name will be saved.
     *
     * Note, this method will also set the current configuration name
     *
     * @param project
     * @param removeLastXElements
     */
    public void saveConfigurationNames(Project project, int removeLastXElements){
        PropertiesComponent p = PropertiesComponent.getInstance(project);

        int numberOfConfigs = configs.size() - removeLastXElements;

        p.setValue(NUMBER_OF_CONFIGURATIONS, ""+numberOfConfigs);
        for (int i = 0; i < numberOfConfigs; i++){
            p.setValue(CONFIGURATION+i, configs.get(i));
        }

        p.setValue(CURRENT_CONFIGURATION, currentConfigName);
    }

    private String getPossibleLocationForMvn(){

        List<String> mvnExeList = Utils.getMvnExecutableNames();

        String mvnHome = System.getenv("MAVEN_HOME");
        if(mvnHome==null || mvnHome.isEmpty()){
            mvnHome = System.getenv("M2_HOME");
        }
        if(mvnHome==null || mvnHome.isEmpty()){
            mvnHome = System.getenv("MVN_HOME");
        }

        if(mvnHome==null || mvnHome.isEmpty()){
            //check in PATH
            String path = System.getenv("PATH");
            String[] tokens = path.split(File.pathSeparator);
            for(String location : tokens){
                if(! (location.contains("maven") || location.contains("mvn"))){
                    continue;
                }
                for(String mvnExe : mvnExeList) {
                    File exe = new File(location, mvnExe);
                    if (exe.exists()) {
                        return exe.getAbsolutePath();
                    }
                }
            }
            return "";

        } else {
            for(String mvnExe : mvnExeList) {
                File exe = new File(mvnHome, "bin");
                exe = new File(exe, mvnExe);
                if (exe.exists()) {
                    return exe.getAbsolutePath();
                }
            }
            return "";
        }
    }

    private void setGUIMinimums(){
        if (guiWidth < MIN_GUI_WIDTH) {
            guiWidth = MIN_GUI_WIDTH;
        }

        if (guiHeight < MIN_GUI_HEIGHT) {
            guiHeight = MIN_GUI_HEIGHT;
        }
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getFolder() {
        return folder;
    }

    // TODO: Could check if that folder exists and is set as a source folder
    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getMvnLocation() {
        return mvnLocation;
    }

    public void setMvnLocation(String mvnLocation) {
        this.mvnLocation = mvnLocation;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String getEvosuiteJarLocation() {
        return evosuiteJarLocation;
    }

    public void setEvosuiteJarLocation(String evosuiteJarLocation) {
        this.evosuiteJarLocation = evosuiteJarLocation;
    }

    public String getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(String executionMode) {
        this.executionMode = executionMode;
    }

    public int getGuiWidth() {
        return guiWidth;
    }

    public void setGuiWidth(int guiWidth) {
        this.guiWidth = guiWidth;
    }

    public int getGuiHeight() {
        return guiHeight;
    }

    public void setGuiHeight(int guiHeight) {
        this.guiHeight = guiHeight;
    }

    public void addAdvancedParameter(String param){
        advancedParams.addElement(param);
    }

    public DefaultListModel<String> getAdvancedParams(){
        if (advancedParams == null){
            return new DefaultListModel<String>();
        }
        else{
            return advancedParams;
        }
    }

    public String getAdvancedParam(int index){
        return this.advancedParams.get(index);
    }

    public void setAdvancedParams(DefaultListModel<String> advancedParams){
        this.advancedParams = advancedParams;
    }

    public DefaultListModel<String> getConfigs(){
        return this.configs;
    }

    public String getConfig(int index){
        return this.configs.get(index);
    }

    public void addConfiguration(String configName){
        configs.addElement(configName);
    }

    public String getCurrentConfigName() {
        return currentConfigName;
    }

    public void removeConfig(Project project, String configName){
        int configToRemoveIndex = configs.indexOf(configName);

        // config to be removed is replaced with the last element
        configs.set(configToRemoveIndex, (configs.lastElement()));

        //load the first configuration
        loadConfiguration(project, configs.get(0));
        saveConfigurationNames(project, 1);
        load(project);
    }



}





















































