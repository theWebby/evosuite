package org.evosuite.intellij;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.evosuite.intellij.stage.*;

import javax.swing.*;

/**
 * Created by webby on 28/06/17.
 */
public class EvoSettingsGUI extends Application{
    private EvoParameters params = EvoParameters.getInstance();

    private DefaultSettingsPane defaultSettingsPane;
    private AdvancedSettingsPane advancedSettingsPane;

    private Stage evoWindow;
    private Scene settingsScene;
    private VBox contentBox;
    private TabPane tabPane;
    private HBox bottomPanel;
    private HBox topPanel;
    private Tab defaultTab;
    private Tab advancedTab;
    private Separator separator;

    private Button buttonOK;
    private Button buttonCancel;
    private ImageView imageEvoSuite;
    private Label labelTitle;

    private volatile boolean wasOK;

    // Initialises components needed on the gui ie. text and action listeners
    private void initComponents(Stage stage){
        wasOK = false;

        //create the structure
        contentBox = new VBox();
        bottomPanel = new HBox();
        separator = new Separator();


        // Bottom Box
        buttonOK = new Button("OK");
        buttonCancel = new Button("Cancel");
        buttonOK.setOnAction(e -> onButtonOK());
        buttonCancel.setOnAction(e -> onButtonCancel());
        buttonOK.setMinWidth(100);
        buttonCancel.setMinWidth(100);
        imageEvoSuite = new ImageView("http://www.evosuite.org/wp-content/themes/twentytwelvecustom/images/site-logo.png");
        imageEvoSuite.setFitWidth(160);
        imageEvoSuite.setPreserveRatio(true);
        Pane splitter = new Pane();
        splitter.setMinSize(115, 1);
        bottomPanel = new HBox();
        bottomPanel.getChildren().addAll(imageEvoSuite, splitter, buttonOK, buttonCancel);
        bottomPanel.setPadding(new Insets(15,10,10,10));
        bottomPanel.setSpacing(10);

        setupTabs(stage);
    }


    private void setupTabs(Stage stage){
        //creating a new tab pane and adding instances of the default and advanced settings as tabs
        tabPane = new TabPane();
        defaultTab = new Tab("Default Settings");
        advancedTab = new Tab("Advanced Settings");
        defaultSettingsPane = new DefaultSettingsPane();
        advancedSettingsPane = new AdvancedSettingsPane();
        defaultTab.setContent(defaultSettingsPane);
        advancedTab.setContent(advancedSettingsPane);
        tabPane.getTabs().setAll(defaultTab, advancedTab);

        //defaultSettingsPane.prefWidthProperty().bind(primaryStage.widthProperty());

        //tab settings
        defaultTab.setClosable(false);
        advancedTab.setClosable(false);
        tabPane.prefWidthProperty().bind(stage.widthProperty());
        tabPane.setTabMinWidth(225);
    }

    private void setupGrid(Stage stage) {
        contentBox.getChildren().addAll(tabPane, separator, bottomPanel);
    }

    public EvoSettingsGUI(){

    }


    public void start(Stage primaryStage) throws Exception {
        // creating the stage (window) with some default settings
        evoWindow = new Stage();
        evoWindow.setResizable(false);
        evoWindow.setTitle("EvoSuite Settings");
        evoWindow.initModality(Modality.APPLICATION_MODAL);
        evoWindow.setOnCloseRequest(e -> onClose());

        //inits everything and adds to contentBox
        initComponents(evoWindow);
        setupGrid(evoWindow);

        //creating the scene and adding it to the stage
        settingsScene = new Scene(contentBox, 530, 360);
        evoWindow.setScene(settingsScene);

        // show the settings stage and pause.
        evoWindow.showAndWait();
    }


    //accessor and mutator methods
    public boolean isWasOK(){
        return wasOK;
    }


    //      Event handler methods

    public void onButtonOK(){
        wasOK = true;
        onClose();
    }

    public void onButtonCancel(){
        wasOK = false;
        onClose();
    }

    //update this to only ask if they are sure if they have made changes to the settings. If no changes have been made
    //then you need not ask the message.
    public void onClose(){
        if (wasOK){
            saveParams();
        }
        else{
            //if the user does not click 'OK' to close then this will be shown
            int answer = JOptionPane.showConfirmDialog(null, "About to close without saving, are you sure?", "Warning", 0);
            if (answer == JOptionPane.NO_OPTION){
                wasOK = true;
                saveParams();
            }

        }
        evoWindow.hide();
    }


    // custom

    private void saveParams(){
        params.setGuiWasOK(wasOK);
    }
}
