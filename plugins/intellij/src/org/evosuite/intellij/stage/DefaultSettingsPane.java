package org.evosuite.intellij.stage;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Created by webby on 28/06/17.
 */
public class DefaultSettingsPane extends GridPane {
    // Components
    Button buttonSelectExport;
    Button buttonSelectMaven;
    Button buttonSelectEvoSuite;
    Button buttonSelectJava;
    TextField textFieldNumberOfCores;
    TextField textFieldMemoryPerCore;
    TextField textFieldTimePerClass;
    TextField textFieldExportFolder;
    TextField textFieldMavenLocation;
    TextField textFieldEvoSuiteLocation;
    TextField textFieldJavaHomeLocation;
    Label labelNoCores;
    Label labelMemoryCores;
    Label labelTime;
    Label labelExportFolder;
    Label labelMaven;
    Label labelEvo;
    Label labelJava;
    RadioButton radioButtonMaven;
    RadioButton radioButtonEvoSuite;

    private void initComponents(){
        // Grid Layout
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setHgap(10);
        this.setVgap(8);

        // Initialize Components and create action listeners
        labelNoCores = new Label("Number of Cores: ");
        labelMemoryCores = new Label("Memory per Core: ");
        labelTime = new Label("Time per Class: ");
        labelExportFolder = new Label("Export Folder: ");
        labelMaven = new Label("Maven Location: ");
        labelEvo = new Label("EvoSuite Location: ");
        labelJava = new Label("JAVA_HOME: ");
        textFieldEvoSuiteLocation = new TextField();
        textFieldExportFolder = new TextField();
        textFieldJavaHomeLocation = new TextField();
        textFieldMavenLocation = new TextField();
        textFieldMemoryPerCore = new TextField();
        textFieldNumberOfCores = new TextField();
        textFieldTimePerClass = new TextField();
        radioButtonEvoSuite = new RadioButton();
        radioButtonMaven = new RadioButton();
        buttonSelectEvoSuite = new Button("Select");
        buttonSelectExport = new Button("Select");
        buttonSelectJava = new Button("Select");
        buttonSelectMaven = new Button("Select");
        buttonSelectMaven.setOnAction(e -> onButtonSelectMaven());
        buttonSelectJava.setOnAction(e -> onButtonSelectJava());
        buttonSelectExport.setOnAction(e -> onButtonSelectExport());
        buttonSelectEvoSuite.setOnAction(e -> onButtonEvoSuite());

        // pushes the text boxes out (can't find what is making them small)
        textFieldEvoSuiteLocation.setMinWidth(250);
    }

    public void setupGrid(){
        GridPane.setConstraints(labelNoCores,               0, 1);
        GridPane.setConstraints(textFieldNumberOfCores,     1, 1);

        GridPane.setConstraints(labelMemoryCores,           0, 2);
        GridPane.setConstraints(textFieldMemoryPerCore,     1, 2);

        GridPane.setConstraints(labelTime,                  0, 3);
        GridPane.setConstraints(textFieldTimePerClass,      1, 3);

        GridPane.setConstraints(labelExportFolder,          0, 4);
        GridPane.setConstraints(textFieldExportFolder,      1, 4);
        GridPane.setConstraints(buttonSelectExport,         2, 4);

        GridPane.setConstraints(labelMaven,                 0, 5);
        GridPane.setConstraints(textFieldMavenLocation,     1, 5);
        GridPane.setConstraints(buttonSelectMaven,          2, 5);
        GridPane.setConstraints(radioButtonMaven,           3, 5);

        GridPane.setConstraints(labelEvo,                   0, 6);
        GridPane.setConstraints(textFieldEvoSuiteLocation,  1, 6);
        GridPane.setConstraints(buttonSelectEvoSuite,       2, 6);
        GridPane.setConstraints(radioButtonEvoSuite,        3, 6);

        GridPane.setConstraints(labelJava,                  0, 7);
        GridPane.setConstraints(textFieldJavaHomeLocation,  1, 7);
        GridPane.setConstraints(buttonSelectJava,           2, 7);

        this.getChildren().addAll(labelNoCores, textFieldNumberOfCores, labelMemoryCores,
                textFieldMemoryPerCore, labelTime, textFieldTimePerClass, labelExportFolder, textFieldExportFolder,
                buttonSelectExport, labelMaven, textFieldMavenLocation, buttonSelectMaven, radioButtonMaven, labelEvo,
                textFieldEvoSuiteLocation, buttonSelectEvoSuite, radioButtonEvoSuite, labelJava, textFieldJavaHomeLocation, buttonSelectJava);

    }

    public DefaultSettingsPane(){
        initComponents();
        setupGrid();
    }

    public void onButtonSelectMaven(){}

    public void onButtonSelectJava(){}

    public void onButtonSelectExport(){}

    public void onButtonEvoSuite(){}
}
