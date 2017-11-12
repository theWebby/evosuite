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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.evosuite.intellij.util.EvoVersion;
import org.evosuite.intellij.util.Utils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvoStartDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JFormattedTextField memoryField;
    private JFormattedTextField coreField;
    private JFormattedTextField timeField;
    private JTextField folderField;
    private JTextField mavenField;
    private JTextField javaHomeField;
    private JButton selectMavenButton;
    private JButton selectJavaHomeButton;
    private JTextField evosuiteLocationTesxField;
    private JButton evosuiteSelectionButton;
    private JRadioButton mavenRadioButton;
    private JRadioButton evosuiteRadioButton;
    private JPanel defaultSettingsTab;
    private JTabbedPane tabbedPane1;
    private JPanel okPanel;
    private JButton addParamButton;
    private JTextField paramTextField;
    private JList advancedParamsList; //UI ONLY - changes to be made to advancedParamsListModel
    private JButton removeParamButton;
    private JLabel warningLabel;
    private JComboBox configurationBox;
    private JButton addConfigurationButton;
    private JButton removeConfigButton;
    private DefaultListModel<String> advancedParamsListModel;
    //private DefaultListModel<String> configurationsListModel;

    private boolean configurationsAdded;

    private volatile boolean wasOK = false;
    private volatile EvoParameters params;
    private volatile Project project;

    public void initFields(Project project, EvoParameters params) {
        this.project = project;
        this.params = params;
        this.configurationsAdded = false;
        //configurationsListModel = params.getConfigs();


        setFields();

        //advancedParamsListModel = new DefaultListModel<String>();


        checkExecution();

        addSavedConfiguraions();
        configurationBox.setSelectedIndex(params.getConfigs().indexOf(params.getCurrentConfigName()));
        this.configurationsAdded = true;

        this.configurationBox.setEditable(false);
        try {
            this.configurationBox.setSelectedIndex(0);
        } catch (Exception e) {
            this.configurationBox.setSelectedIndex(-1);
        }

    }


    public EvoStartDialog() {

        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        addParamButton.addActionListener(e -> onAddParam());
        paramTextField.addActionListener(e -> onAddParam()); //action is enter button
        removeParamButton.addActionListener(e -> onRemoveParam());
        addConfigurationButton.addActionListener(e -> onAddConfig(project));
        configurationBox.addActionListener(e -> onChangeConfiguration());
        removeConfigButton.addActionListener(e -> onRemoveConfig());

        advancedParamsList.addListSelectionListener(e -> onSelectList());
        advancedParamsList.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemoveParam();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        selectMavenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onSelectMvn();
            }
        });

        selectJavaHomeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onSelectJavaHome();
            }
        });

        selectJavaHomeButton.setToolTipText("Choose a valid JDK home for Java " + EvoVersion.JAVA_VERSION);

        mavenRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                checkExecution();
            }
        });

        evosuiteRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                checkExecution();
            }
        });

        evosuiteSelectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onSelectEvosuite();
            }
        });

        //setPreferredSize(new Dimension(EvoParameters.getInstance().getGuiWidth(), EvoParameters.getInstance().getGuiHeight()));
    }

    private void setFields() {
        coreField.setValue(params.getCores());
        memoryField.setValue(params.getMemory());
        timeField.setValue(params.getTime());

        folderField.setText(params.getFolder());
        mavenField.setText(params.getMvnLocation());
        evosuiteLocationTesxField.setText(params.getEvosuiteJarLocation());
        javaHomeField.setText(params.getJavaHome());

        if (!Utils.isMavenProject(project)) {
            //disable Maven options
            selectMavenButton.setEnabled(false);
            mavenRadioButton.setEnabled(false);
            params.setExecutionMode(EvoParameters.EXECUTION_MODE_JAR);
        }

        if (params.usesMaven()) {
            mavenRadioButton.setSelected(true);
        } else {
            evosuiteRadioButton.setSelected(true);
        }

        checkExecution();
        addAdvancedParams();
    }

    private void onSelectEvosuite() {
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true; // need to be able to navigate through folders
                }
                return checkIfValidEvoSuiteJar(file);
            }

            @Override
            public String getDescription() {
                return "EvoSuite executable jar";
            }
        });

        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            params.setEvosuiteJarLocation(path);
            evosuiteLocationTesxField.setText(path);
        }
    }

    private void checkExecution() {
        if (mavenRadioButton.isSelected()) {
            params.setExecutionMode(EvoParameters.EXECUTION_MODE_MVN);
        } else if (evosuiteRadioButton.isSelected()) {
            params.setExecutionMode(EvoParameters.EXECUTION_MODE_JAR);
        }

        evosuiteLocationTesxField.setEnabled(evosuiteRadioButton.isSelected());
        evosuiteSelectionButton.setEnabled(evosuiteRadioButton.isSelected());
        mavenField.setEnabled(mavenRadioButton.isSelected());
        selectMavenButton.setEnabled(mavenRadioButton.isSelected());
    }

    private void onSelectMvn() {
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true; // need to be able to navigate through folders
                }
                return checkIfValidMaven(file);
            }

            @Override
            public String getDescription() {
                return "Maven executable";
            }
        });

        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            params.setMvnLocation(path);
            mavenField.setText(path);
        }
    }

    private void onSelectJavaHome() {

        String jdkStartLocation = getJDKStartLocation();

        JFileChooser fc = new JFileChooser(jdkStartLocation);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                //return checkIfValidJavaHome(file);
                return true; //otherwise all folders will be greyed out
            }

            @Override
            public String getDescription() {
                return "Java Home (containing bin/javac)";
            }
        });
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();

            if (!checkIfValidJavaHome(new File(path))) {
                Messages.showMessageDialog(project, "Invalid JDK home: choose a correct one that contains bin/javac",
                        "EvoSuite Plugin", Messages.getErrorIcon());
                return;
            }

            params.setJavaHome(path);
            javaHomeField.setText(path);
        }
    }

    private String getJDKStartLocation() {
        String start = params.getJavaHome(); //TODO check for null
        if (start == null || start.isEmpty() && ProjectJdkTable.getInstance().getAllJdks().length > 0) {
            //try to check the JDK used by the project

            for (Sdk sdk : ProjectJdkTable.getInstance().getAllJdks()) {
                if (sdk.getVersionString().contains("" + EvoVersion.JAVA_VERSION)) {
                    start = sdk.getHomePath();
                    break;
                }
            }
            if (start == null) {
                //just take something as starting point
                start = ProjectJdkTable.getInstance().getAllJdks()[0].getHomePath();
            }
        }

        if (start == null || start.isEmpty()) {
            return ""; //if still empty, return default ""
        }

        File file = new File(start);
        while (file != null) {
            if (file.getName().toLowerCase().contains("jdk")) {
                file = file.getParentFile(); //go to the parent, which might have several JDKs
                break;
            }
            file = file.getParentFile();
        }

        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }

    private boolean checkIfValidJavaHome(File file) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return false;
        }

        String javac = Utils.isWindows() ? "javac.exe" : "javac";
        File jf = new File(new File(file, "bin"), javac);
        return jf.exists();
    }

    private boolean checkIfValidEvoSuiteJar(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        }
        String name = file.getName().toLowerCase();

        if (Arrays.asList("runtime", "standalone", "client", "plugin", "test", "generated").stream()
                .anyMatch(k -> name.contains(k))) {
            return false;
        }

        return name.startsWith("evosuite") && name.endsWith(".jar");
    }

    private boolean checkIfValidMaven(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        }
        List<String> names = Utils.getMvnExecutableNames();
        for (String name : names) {
            if (file.getName().toLowerCase().equals(name)) {
                return true;
            }
        }
        return false;
    }


    private void onOK() {
// add your code here

        dispose();

        wasOK = saveParameters(true);
    }

    private void onCancel() {
// add your code here if necessary


        dispose();

        saveParameters(false);
    }


    private boolean saveParameters(boolean validate) {

        int cores = ((Number) coreField.getValue()).intValue();
        int memory = ((Number) memoryField.getValue()).intValue();
        int time = ((Number) timeField.getValue()).intValue();
        String dir = folderField.getText();
        String mvn = mavenField.getText();
        String javaHome = javaHomeField.getText();
        String evosuiteJar = evosuiteLocationTesxField.getText();


        List<String> errors = new ArrayList<>();

        if (cores < 1) {
            errors.add("Number of cores needs to be positive value");
        } else {
            params.setCores(cores);
        }

        if (memory < 1) {
            errors.add("Memory needs to be a positive value");
        } else {
            params.setMemory(memory);
        }

        if (time < 1) {
            errors.add("Duration needs to be a positive value");
        } else {
            params.setTime(time);
        }

        if (params.usesMaven() && !checkIfValidMaven(new File(mvn))) {
            errors.add("Invalid Maven executable: choose a correct one");
        } else {
            params.setMvnLocation(mvn);
        }

        if (!params.usesMaven() && !checkIfValidEvoSuiteJar(new File(evosuiteJar))) {
            errors.add("Invalid EvoSuite executable jar: choose a correct evosuite*.jar one");
        } else {
            params.setEvosuiteJarLocation(evosuiteJar);
        }

        if (!checkIfValidJavaHome(new File(javaHome))) {
            errors.add("Invalid JDK home: choose a correct one that contains bin/javac");
        } else {
            params.setJavaHome(javaHome);
        }

        params.setFolder(dir);
        params.setAdvancedParams(advancedParamsListModel);
        params.setGuiWidth(this.getWidth());
        params.setGuiHeight(this.getHeight());


        if (validate && !errors.isEmpty()) {
            String title = "ERROR: EvoSuite Plugin";
            String msg = String.join("\n", errors);
            Messages.showMessageDialog(project, msg, title, Messages.getErrorIcon());
            return false;
        }

        return true;
    }

    private boolean isFieldsModified() {

        if (!coreField.getValue().equals(params.getCores())) return true;
        if (!memoryField.getValue().equals(params.getMemory())) return true;
        if (!timeField.getValue().equals(params.getTime())) return true;
        if (!folderField.getText().equals(params.getFolder())) return true;
        if (!mavenField.getText().equals(params.getMvnLocation())) return true;
        if (!evosuiteLocationTesxField.getText().equals(params.getEvosuiteJarLocation())) return true;
        if (!javaHomeField.getText().equals(params.getJavaHome())) return true;

        if (advancedParamsListModel.size() != params.getAdvancedParams().size()) {
            return true;
        } else {
            // check if all advanced params are the same
            for (int i = 0; i < advancedParamsListModel.size(); i++) {
                if (!advancedParamsListModel.get(i).equals(params.getAdvancedParam(i))) {
                    return true;
                }
            }
        }


        return false;
    }


    public static void main(String[] args) {
        EvoStartDialog dialog = new EvoStartDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public boolean isWasOK() {
        return wasOK;
    }


    //add a custom param from the advanced tab to the list of params
    private void onAddParam() {
        String newParam = paramTextField.getText();
        newParam = newParam.replace(" ", "");


        int duplicateParamIndex = findDuplicateAdvancedParamIndex(newParam);

        if (duplicateParamIndex != -1) {
            // update param
            advancedParamsListModel.set(duplicateParamIndex, newParam);
            advancedParamsList.setSelectedIndex(duplicateParamIndex);
        } else {
            // add new param
            advancedParamsListModel.addElement(paramTextField.getText());
            paramTextField.setText("");
            //addAdvancedParams();
        }
    }


    private void onRemoveParam() {
        if (advancedParamsList.isSelectionEmpty()) {
            warn("You have not selected a parameter to be removed.");
        } else {
            advancedParamsListModel.remove(advancedParamsList.getSelectedIndex());
            //setFields();
            //addSavedConfiguraions();
        }
    }


    private void warn(String message) {
        warningLabel.setText(message);
        warningLabel.setVisible(true);
    }


    private void hideWarn() {
        warningLabel.setVisible(false);
    }

    /**
     * add the params from EvoParameters to the DefaultListModel
     */
    private void addAdvancedParams() {
        //advancedParamsListModel = new DefaultListModel<String>();

        advancedParamsListModel.removeAllElements();

        for (int i = 0; i < params.getAdvancedParams().size(); i++) {
            advancedParamsListModel.addElement(params.getAdvancedParam(i));
        }
    }

    private void addSavedConfiguraions() {
        configurationBox.removeAllItems();

        for (int i = 0; i < params.getConfigs().size(); i++) {
            //configurationBox.addItem(params.getConfig(i));
            configurationBox.insertItemAt(params.getConfig(i), i);
        }
    }


    /**
     * @param newParam
     * @return Index of duplicate parameter (-1 if none exist)
     */
    private int findDuplicateAdvancedParamIndex(String newParam) {
        for (int i = 0; i < advancedParamsListModel.size(); i++) {
            if (isDuplicateParams(newParam, advancedParamsListModel.get(i))) {
                return i;
            }
        }

        return -1; //no duplicates
    }

    private boolean isDuplicateParams(String p1, String p2) {
        String[] p1Parts = p1.split("=");
        String[] p2Parts = p2.split("=");

        // if the property is the same
        if (p1Parts[0].equals(p2Parts[0])) {
            return true;
        }

        return false;
    }


    private void onSelectList() {
        int selectedIndex = advancedParamsList.getSelectedIndex();

        if (selectedIndex != -1) {
            paramTextField.setText(advancedParamsListModel.get(selectedIndex));
        } else {
            paramTextField.setText("");
        }
    }


    private void onAddConfig(Project project) {


        if (isFieldsModified()) {
            warn("Please save the current configuration first.");
        } else {
            String configName = JOptionPane.showInputDialog(new JFrame(), "New configuration name:");

            if (configName != null && !duplicateConfigName(configName)) {
                params.addConfiguration(configName);
                params.saveConfigurationNames(project, 0);
                params.loadConfiguration(project, configName);
                configurationsAdded = false;
                addSavedConfiguraions();
                configurationsAdded = true;
                setFields();
                configurationBox.setSelectedIndex(configurationBox.getItemCount() - 1);
            }
        }
    }

    private void onRemoveConfig() {
        configurationsAdded = false;

        if (params.getConfigs().size() == 1) {
            warn("You cannot remove the last configuration!");
        } else {
            //are you sure
            int response = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you wish to remove the current configuration?");
            if (response == JOptionPane.YES_OPTION) {
                params.removeConfig(project, params.getCurrentConfigName());
            }

            setFields();
            addSavedConfiguraions();
            configurationBox.setSelectedIndex(0);
        }


        configurationsAdded = true;
    }


    /**
     * Checks if a new config name exists.
     * <p>
     * Note: When duplicate names exist, only the first instance is loaded
     *
     * @param newName
     * @return true is the new name is a duplicate
     */
    private boolean duplicateConfigName(String newName) {
        for (int i = 0; i < params.getConfigs().size(); i++) {
            if (params.getConfigs().get(i).equals(newName)) {
                return true;
            }
        }
        return false;
    }

    private void onChangeConfiguration() {
        // stops it trying to load each configuration after adding it to the combo box
        if (configurationsAdded) {
            boolean change = false;

            if (isFieldsModified()) {
                int response = JOptionPane.showConfirmDialog(new JFrame(), "Unsaved changes to current configuration will be lost! Are you sure?");
                if (response != JOptionPane.YES_OPTION) {
                    //dont change and set the configuration combobox to the current configuration
                    configurationsAdded = false;
                    configurationBox.setSelectedIndex(params.getConfigs().indexOf(params.getCurrentConfigName()));
                    configurationsAdded = true;
                } else {
                    change = true;
                }
            } else {
                change = true;
            }


            if (change) {
                String configName = configurationBox.getSelectedItem().toString();
                params.loadConfiguration(project, configName);
                setFields();
            }
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setParseIntegerOnly(true);
        coreField = new JFormattedTextField(nf);
        memoryField = new JFormattedTextField(nf);
        timeField = new JFormattedTextField(nf);
        advancedParamsListModel = new DefaultListModel<String>();
        advancedParamsList = new JBList(advancedParamsListModel);
    }


    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new FormLayout("fill:391px:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        contentPane.setMinimumSize(new Dimension(800, 400));
        contentPane.setPreferredSize(new Dimension(800, 400));
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "EvoSuite Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(contentPane.getFont().getName(), contentPane.getFont().getStyle(), 20)));
        tabbedPane1 = new JTabbedPane();
        CellConstraints cc = new CellConstraints();
        contentPane.add(tabbedPane1, cc.xy(1, 5));
        defaultSettingsTab = new JPanel();
        defaultSettingsTab.setLayout(new FormLayout("fill:170px:grow,fill:13px:noGrow,fill:200px:grow(5.0),left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:37px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:15dlu:noGrow,top:3dlu:noGrow,center:15dlu:noGrow,top:3dlu:noGrow,center:15dlu:noGrow,top:3dlu:noGrow,center:15dlu:noGrow,top:3dlu:noGrow,center:15dlu:noGrow,top:3dlu:noGrow,center:15dlu:noGrow,top:3dlu:noGrow,center:15dlu:noGrow"));
        tabbedPane1.addTab("Default", defaultSettingsTab);
        final JLabel label1 = new JLabel();
        label1.setText("Number of cores:");
        label1.setToolTipText("How many cores will be used by EvoSuite in parallel. Note: this is used only when more than one class is selected at the same time");
        defaultSettingsTab.add(label1, cc.xy(1, 1));
        defaultSettingsTab.add(coreField, cc.xy(3, 1));
        final JLabel label2 = new JLabel();
        label2.setText("Memory per core (MB):");
        label2.setToolTipText("Max memory per core used by EvoSuite. We recommend at least 1GB per core, where 2GB per core should be more than enough. ");
        defaultSettingsTab.add(label2, cc.xy(1, 3));
        defaultSettingsTab.add(memoryField, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        label3.setText("Time per class (minutes):");
        label3.setToolTipText("Max time used by EvoSuite to generate test cases on each class. At least half of this time will be used to generate test data. The rest is used for other optimizations like assertion generation and minimizing the test suites. The more time, the better results you will get. However, more than 10 minutes per class will unlikely bring any major benefit. ");
        defaultSettingsTab.add(label3, cc.xy(1, 5));
        defaultSettingsTab.add(timeField, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label4 = new JLabel();
        label4.setText("Export folder:");
        label4.setToolTipText("Folder where the generated tests will be copied to.  To run the generated tests, such folder needs to be on the classpath.");
        defaultSettingsTab.add(label4, cc.xy(1, 7));
        folderField = new JTextField();
        defaultSettingsTab.add(folderField, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label5 = new JLabel();
        label5.setText("Maven location:");
        label5.setToolTipText("Location of the executable file for Maven");
        defaultSettingsTab.add(label5, cc.xy(1, 9));
        final JLabel label6 = new JLabel();
        label6.setText("JAVA_HOME:");
        label6.setToolTipText("The JDK with which EvoSuite will be run. Note: this might have a different version (e.g., 7) from the one the project is compiled to (e.g., 6). It can be an higher version, but not lower.  ");
        defaultSettingsTab.add(label6, cc.xy(1, 13));
        mavenField = new JTextField();
        mavenField.setEditable(false);
        mavenField.setHorizontalAlignment(2);
        defaultSettingsTab.add(mavenField, cc.xy(3, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        javaHomeField = new JTextField();
        javaHomeField.setEditable(false);
        javaHomeField.setHorizontalAlignment(2);
        defaultSettingsTab.add(javaHomeField, cc.xy(3, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        selectMavenButton = new JButton();
        selectMavenButton.setText("Select");
        defaultSettingsTab.add(selectMavenButton, cc.xy(5, 9));
        selectJavaHomeButton = new JButton();
        selectJavaHomeButton.setText("Select");
        defaultSettingsTab.add(selectJavaHomeButton, cc.xy(5, 13));
        final JLabel label7 = new JLabel();
        label7.setText("EvoSuite location:");
        label7.setToolTipText("Location of the standalone jar file of EvoSuite");
        defaultSettingsTab.add(label7, cc.xy(1, 11));
        evosuiteLocationTesxField = new JTextField();
        evosuiteLocationTesxField.setEditable(false);
        evosuiteLocationTesxField.setHorizontalAlignment(2);
        defaultSettingsTab.add(evosuiteLocationTesxField, cc.xy(3, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        evosuiteSelectionButton = new JButton();
        evosuiteSelectionButton.setText("Select");
        defaultSettingsTab.add(evosuiteSelectionButton, cc.xy(5, 11));
        mavenRadioButton = new JRadioButton();
        mavenRadioButton.setText("");
        defaultSettingsTab.add(mavenRadioButton, cc.xy(7, 9));
        evosuiteRadioButton = new JRadioButton();
        evosuiteRadioButton.setText("");
        defaultSettingsTab.add(evosuiteRadioButton, cc.xy(7, 11));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Advanced", panel1);
        addParamButton = new JButton();
        addParamButton.setText("Add");
        panel1.add(addParamButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        paramTextField = new JTextField();
        panel1.add(paramTextField, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panel1.add(advancedParamsList, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        removeParamButton = new JButton();
        removeParamButton.setText("Remove");
        panel1.add(removeParamButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        okPanel = new JPanel();
        okPanel.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:161dlu:grow,fill:100px:noGrow,left:13dlu:noGrow,fill:100px:noGrow", "center:35px:grow"));
        contentPane.add(okPanel, cc.xy(1, 9));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        okPanel.add(buttonCancel, cc.xy(5, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        buttonOK = new JButton();
        buttonOK.setMaximumSize(new Dimension(66, 27));
        buttonOK.setMinimumSize(new Dimension(66, 27));
        buttonOK.setPreferredSize(new Dimension(66, 27));
        buttonOK.setText("OK");
        buttonOK.setVerticalAlignment(0);
        buttonOK.setVerticalTextPosition(0);
        okPanel.add(buttonOK, cc.xy(3, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        warningLabel = new JLabel();
        warningLabel.setEnabled(true);
        warningLabel.setForeground(new Color(-65536));
        warningLabel.setText("");
        okPanel.add(warningLabel, cc.xy(2, 1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel2, cc.xy(1, 1));
        removeConfigButton = new JButton();
        removeConfigButton.setText("Remove");
        panel2.add(removeConfigButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        configurationBox = new JComboBox();
        configurationBox.setEditable(false);
        configurationBox.setEnabled(true);
        panel3.add(configurationBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addConfigurationButton = new JButton();
        addConfigurationButton.setText("Add");
        panel2.add(addConfigurationButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Configuration:");
        panel2.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        contentPane.add(separator1, cc.xy(1, 3, CellConstraints.FILL, CellConstraints.FILL));
        final JSeparator separator2 = new JSeparator();
        contentPane.add(separator2, cc.xy(1, 7, CellConstraints.FILL, CellConstraints.FILL));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(mavenRadioButton);
        buttonGroup.add(evosuiteRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
