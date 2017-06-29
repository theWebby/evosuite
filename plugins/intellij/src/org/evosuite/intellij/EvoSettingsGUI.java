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
import javafx.stage.Stage;
import org.evosuite.intellij.stage.*;

/**
 * Created by webby on 28/06/17.
 */
public class EvoSettingsGUI extends Application{
    DefaultSettingsPane defaultSettingsPane;
    AdvancedSettingsPane advancedSettingsPane;

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
    private void initComponents(Stage primaryStage){
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

        setupTabs(primaryStage);
    }


    private void setupTabs(Stage primaryStage){
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
        tabPane.prefWidthProperty().bind(primaryStage.widthProperty());
        tabPane.setTabMinWidth(225);
    }

    private void setupGrid(Stage primaryStage) {
        contentBox.getChildren().addAll(tabPane, separator, bottomPanel);
    }

    public EvoSettingsGUI(){

    }


    public void start(Stage primaryStage) throws Exception {
        initComponents(primaryStage);
        setupGrid(primaryStage);
        primaryStage.setResizable(false);

        evoWindow = primaryStage;
        evoWindow.setTitle("EvoSuite Settings");





        settingsScene = new Scene(contentBox, 530, 360);
        evoWindow.setScene(settingsScene);

        evoWindow.show();
    }


    public void onButtonOK(){
        wasOK = true;
    }

    public void onButtonCancel(){
        wasOK = false;
    }
}
