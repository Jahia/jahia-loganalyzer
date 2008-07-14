package org.jahia.loganalyzer.gui.swing;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.jahia.loganalyzer.LogParser;
import org.jahia.loganalyzer.LogParserConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogAnalyzerMainDialog extends JDialog {

    private static final Log log =
            LogFactory.getLog(LogAnalyzerMainDialog.class);

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton browseInputLogFile;
    private JTextField inputLogFile;
    private JTextField perfDetailsCSVOutputFile;
    private JTextField csvSeparatorCharField;
    private JButton browsePerfDetailsCSVOutputFile;
    private JTabbedPane typeTabbedPane;
    private JCheckBox performanceActivated;
    private JComboBox regexpPatternField;
    private JTextField dateFormatField;
    private JCheckBox threadDumpsActivated;
    private JTextField threadDetailsCSVOutputFile;
    private JCheckBox exceptionsActivated;
    private JTextField exceptionDetailsCSVOutputFile;
    private JTextField threadSummaryCSVOutputFile;
    private JTextField servletMapping;
    private JLabel servletMappingLabel;
    private JTextField contextMapping;
    private JTextField perfSummaryCSVOutputFile;
    private JTextField exceptionSummaryCSVOutputFile;
    private JComboBox standardMinimumLogLevel;
    private JTextField standardDetailsCSVOutputFile;
    private JTextField standardSummaryCSVOutputFile;
    private JButton browsePerfSummaryCSVOutputFile;
    private JButton browseExceptionDetailsCSVOutputFile;
    private JButton browseExceptionSummaryCSVOutputFile;
    private JButton browseThreadDetailsCSVOutputFile;
    private JButton browseThreadSummaryCSVOutputFile;
    private JButton browseStandardDetailsCSVOutputFile;
    private JButton browseStandardSummaryCSVOutputFile;
    private JFileChooser fileChooser;
    private LogParserConfiguration logParserConfiguration = new LogParserConfiguration();

    public LogAnalyzerMainDialog() {
        setTitle("Jahia Log Analysis Tool");
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

        inputLogFile.setText(logParserConfiguration.getInputFileName());
        perfDetailsCSVOutputFile.setText(logParserConfiguration.getPerfDetailsOutputFileName());
        perfSummaryCSVOutputFile.setText(logParserConfiguration.getPerfSummaryOutputFileName());
        threadDetailsCSVOutputFile.setText(logParserConfiguration.getThreadDetailsOutputFileName());
        threadSummaryCSVOutputFile.setText(logParserConfiguration.getThreadSummaryOutputFileName());
        exceptionDetailsCSVOutputFile.setText(logParserConfiguration.getExceptionDetailsOutputFileName());
        exceptionSummaryCSVOutputFile.setText(logParserConfiguration.getExceptionSummaryOutputFileName());
        standardDetailsCSVOutputFile.setText(logParserConfiguration.getStandardDetailsOutputFileName());
        standardSummaryCSVOutputFile.setText(logParserConfiguration.getStandardSummaryOutputFileName());
        dateFormatField.setText(logParserConfiguration.getDateFormatString());
        contextMapping.setText(logParserConfiguration.getContextMapping());
        servletMapping.setText(logParserConfiguration.getServletMapping());
        standardMinimumLogLevel.setSelectedIndex(logParserConfiguration.getStandardMinimumLogLevel());

        //Create a file chooser
        fileChooser = new JFileChooser();

        //Uncomment one of the following lines to try a different
        //file selection mode.  The first allows just directories
        //to be selected (and, at least in the Java look and feel,
        //shown).  The second allows both files and directories
        //to be selected.  If you leave these lines commented out,
        //then the default mode (FILES_ONLY) will be used.
        //
        //fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        browseInputLogFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setSelectedFile(new File(inputLogFile.getText()));
                int returnVal = fileChooser.showOpenDialog(LogAnalyzerMainDialog.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    inputLogFile.setText(file.getAbsoluteFile().toString());

                    // now we use the same parent directory for all output files
                    File newPerfDetails = new File(file.getParent(), LogParserConfiguration.DEFAULT_PERF_DETAILS_OUTPUTFILENAME_STRING);
                    perfDetailsCSVOutputFile.setText(newPerfDetails.getAbsoluteFile().toString());
                    File newPerfSummary = new File(file.getParent(), LogParserConfiguration.DEFAULT_PERF_SUMMARY_OUTPUTFILENAME_STRING);
                    perfSummaryCSVOutputFile.setText(newPerfSummary.getAbsoluteFile().toString());
                    File newThreadDetails = new File(file.getParent(), LogParserConfiguration.DEFAULT_THREAD_DETAILS_OUTPUTFILENAME_STRING);
                    threadDetailsCSVOutputFile.setText(newThreadDetails.getAbsoluteFile().toString());
                    File newThreadSummary = new File(file.getParent(), LogParserConfiguration.DEFAULT_THREAD_SUMMARY_OUTPUTFILENAME_STRING);
                    threadSummaryCSVOutputFile.setText(newThreadSummary.getAbsoluteFile().toString());
                    File newExceptionDetails = new File(file.getParent(), LogParserConfiguration.DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME_STRING);
                    exceptionDetailsCSVOutputFile.setText(newExceptionDetails.getAbsoluteFile().toString());
                    File newExceptionSummary = new File(file.getParent(), LogParserConfiguration.DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME_STRING);
                    exceptionSummaryCSVOutputFile.setText(newExceptionSummary.getAbsoluteFile().toString());
                    File newStandardDetails = new File(file.getParent(), LogParserConfiguration.DEFAULT_STANDARD_DETAILS_OUTPUTFILENAME_STRING);
                    standardDetailsCSVOutputFile.setText(newStandardDetails.getAbsoluteFile().toString());
                    File newStandardSummary = new File(file.getParent(), LogParserConfiguration.DEFAULT_STANDARD_SUMMARY_OUTPUTFILENAME_STRING);
                    standardSummaryCSVOutputFile.setText(newStandardSummary.getAbsoluteFile().toString());
                } else {
                }
            }
        });
        browsePerfDetailsCSVOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFileChooser(perfDetailsCSVOutputFile);
            }
        });
        browsePerfSummaryCSVOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFileChooser(perfSummaryCSVOutputFile);
            }
        });
        browseExceptionDetailsCSVOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFileChooser(exceptionDetailsCSVOutputFile);
            }
        });
        browseExceptionSummaryCSVOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFileChooser(exceptionSummaryCSVOutputFile);
            }
        });
        browseThreadDetailsCSVOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFileChooser(threadDetailsCSVOutputFile);
            }
        });
        browseThreadSummaryCSVOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFileChooser(threadSummaryCSVOutputFile);
            }
        });
        browseStandardDetailsCSVOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFileChooser(standardDetailsCSVOutputFile);
            }
        });
        browseStandardSummaryCSVOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFileChooser(standardSummaryCSVOutputFile);
            }
        });
    }

    private void processFileChooser(JTextField csvOutputFile) {
        fileChooser.setSelectedFile(new File(csvOutputFile.getText()));
        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            csvOutputFile.setText(file.getAbsoluteFile().toString());
        } else {
        }
    }

    private void disableUI() {
        buttonOK.setEnabled(false);
        buttonCancel.setEnabled(false);
        browseInputLogFile.setEnabled(false);
        setEnabled(false);
    }

    private void enableUI() {
        buttonOK.setEnabled(true);
        buttonCancel.setEnabled(true);
        browseInputLogFile.setEnabled(true);
        setEnabled(true);
    }

    private void onOK() {
        disableUI();
        List<String> patternList = new ArrayList<String>();
        patternList.add(regexpPatternField.getSelectedItem().toString());
        logParserConfiguration.setInputFileName(inputLogFile.getText());
        logParserConfiguration.setPerformanceAnalyzerActivated(performanceActivated.isSelected());
        logParserConfiguration.setPerfDetailsOutputFileName(perfDetailsCSVOutputFile.getText());
        logParserConfiguration.setPerfSummaryOutputFileName(perfSummaryCSVOutputFile.getText());
        logParserConfiguration.setThreadDumpAnalyzerActivated(threadDumpsActivated.isSelected());
        logParserConfiguration.setThreadDetailsOutputFileName(threadDetailsCSVOutputFile.getText());
        logParserConfiguration.setThreadSummaryOutputFileName(threadSummaryCSVOutputFile.getText());
        logParserConfiguration.setExceptionAnalyzerActivated(exceptionsActivated.isSelected());
        logParserConfiguration.setExceptionDetailsOutputFileName(exceptionDetailsCSVOutputFile.getText());
        logParserConfiguration.setExceptionSummaryOutputFileName(exceptionSummaryCSVOutputFile.getText());
        logParserConfiguration.setStandardDetailsOutputFileName(standardDetailsCSVOutputFile.getText());
        logParserConfiguration.setStandardSummaryOutputFileName(standardSummaryCSVOutputFile.getText());
        logParserConfiguration.setCsvSeparatorChar(csvSeparatorCharField.getText().charAt(0));
        logParserConfiguration.setPatternList(patternList);
        logParserConfiguration.setDateFormatString(dateFormatField.getText());
        logParserConfiguration.setServletMapping(servletMapping.getText());
        logParserConfiguration.setStandardMinimumLogLevel(standardMinimumLogLevel.getSelectedIndex());
        AnalysisWorker worker = new AnalysisWorker(logParserConfiguration);
        worker.start();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new FormLayout("fill:d:grow", "center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        contentPane.setToolTipText("");
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:grow"));
        CellConstraints cc = new CellConstraints();
        contentPane.add(panel1, cc.xy(1, 3));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:d:grow"));
        panel1.add(panel2, cc.xy(3, 1));
        buttonOK = new JButton();
        this.$$$loadButtonText$$$(buttonOK, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.button.analyzeButton"));
        panel2.add(buttonOK, cc.xy(1, 1));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, cc.xy(3, 1));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow"));
        contentPane.add(panel3, cc.xy(1, 1));
        browseInputLogFile = new JButton();
        this.$$$loadButtonText$$$(browseInputLogFile, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.selectInputLogFileButton"));
        panel3.add(browseInputLogFile, cc.xy(5, 1));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.selectInputLogFile"));
        panel3.add(label1, cc.xy(1, 1));
        inputLogFile = new JTextField();
        panel3.add(inputLogFile, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        typeTabbedPane = new JTabbedPane();
        typeTabbedPane.setTabPlacement(1);
        panel3.add(typeTabbedPane, cc.xyw(1, 9, 5));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:grow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:grow"));
        typeTabbedPane.addTab(ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.tab.performance"), panel4);
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.enterPerfDetailsCSVOutputFile"));
        panel4.add(label2, cc.xy(1, 3));
        perfDetailsCSVOutputFile = new JTextField();
        panel4.add(perfDetailsCSVOutputFile, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        browsePerfDetailsCSVOutputFile = new JButton();
        this.$$$loadButtonText$$$(browsePerfDetailsCSVOutputFile, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.button.selectCSVOutputFile"));
        panel4.add(browsePerfDetailsCSVOutputFile, cc.xy(5, 3));
        performanceActivated = new JCheckBox();
        performanceActivated.setEnabled(true);
        performanceActivated.setSelected(true);
        this.$$$loadButtonText$$$(performanceActivated, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.radioButton.activated"));
        panel4.add(performanceActivated, cc.xy(1, 1));
        regexpPatternField = new JComboBox();
        regexpPatternField.setEditable(true);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("(.*?): .*\\[org\\.jahia\\.bin\\.Jahia\\].*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*");
        defaultComboBoxModel1.addElement(".*?\\[(.*?)\\].*org\\.jahia\\.bin\\.Jahia.*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*");
        regexpPatternField.setModel(defaultComboBoxModel1);
        panel4.add(regexpPatternField, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.regexpPattern"));
        panel4.add(label3, cc.xy(1, 7));
        dateFormatField = new JTextField();
        panel4.add(dateFormatField, cc.xy(3, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label4 = new JLabel();
        this.$$$loadLabelText$$$(label4, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.dateFormat"));
        panel4.add(label4, cc.xy(1, 9));
        final JLabel label5 = new JLabel();
        this.$$$loadLabelText$$$(label5, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.enterPerfSummaryCSVOutputFile"));
        panel4.add(label5, cc.xy(1, 5));
        perfSummaryCSVOutputFile = new JTextField();
        panel4.add(perfSummaryCSVOutputFile, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        browsePerfSummaryCSVOutputFile = new JButton();
        this.$$$loadButtonText$$$(browsePerfSummaryCSVOutputFile, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.button.selectCSVOutputFile"));
        panel4.add(browsePerfSummaryCSVOutputFile, cc.xy(5, 5));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:grow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:grow"));
        typeTabbedPane.addTab(ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.tab.exceptions"), panel5);
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        exceptionsActivated = new JCheckBox();
        exceptionsActivated.setSelected(true);
        exceptionsActivated.setText("Activated");
        panel5.add(exceptionsActivated, cc.xy(1, 1));
        exceptionDetailsCSVOutputFile = new JTextField();
        panel5.add(exceptionDetailsCSVOutputFile, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label6 = new JLabel();
        this.$$$loadLabelText$$$(label6, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.enterExceptionDetailsCSVOutputFile"));
        panel5.add(label6, cc.xy(1, 3));
        final JLabel label7 = new JLabel();
        this.$$$loadLabelText$$$(label7, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.enterExceptionSummaryCSVOutputFile"));
        panel5.add(label7, cc.xy(1, 5));
        exceptionSummaryCSVOutputFile = new JTextField();
        panel5.add(exceptionSummaryCSVOutputFile, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        browseExceptionDetailsCSVOutputFile = new JButton();
        this.$$$loadButtonText$$$(browseExceptionDetailsCSVOutputFile, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.button.selectCSVOutputFile"));
        panel5.add(browseExceptionDetailsCSVOutputFile, cc.xy(5, 3));
        browseExceptionSummaryCSVOutputFile = new JButton();
        this.$$$loadButtonText$$$(browseExceptionSummaryCSVOutputFile, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.button.selectCSVOutputFile"));
        panel5.add(browseExceptionSummaryCSVOutputFile, cc.xy(5, 5));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:grow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:grow"));
        typeTabbedPane.addTab(ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.tab.threadDumps"), panel6);
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        threadDumpsActivated = new JCheckBox();
        threadDumpsActivated.setSelected(true);
        threadDumpsActivated.setText("Activated");
        panel6.add(threadDumpsActivated, cc.xy(1, 1));
        final JLabel label8 = new JLabel();
        label8.setText("Details output file");
        panel6.add(label8, cc.xy(1, 3));
        threadDetailsCSVOutputFile = new JTextField();
        panel6.add(threadDetailsCSVOutputFile, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        threadSummaryCSVOutputFile = new JTextField();
        panel6.add(threadSummaryCSVOutputFile, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label9 = new JLabel();
        label9.setText("Summary output file");
        panel6.add(label9, cc.xy(1, 5));
        browseThreadDetailsCSVOutputFile = new JButton();
        this.$$$loadButtonText$$$(browseThreadDetailsCSVOutputFile, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.button.selectCSVOutputFile"));
        panel6.add(browseThreadDetailsCSVOutputFile, cc.xy(5, 3));
        browseThreadSummaryCSVOutputFile = new JButton();
        this.$$$loadButtonText$$$(browseThreadSummaryCSVOutputFile, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.button.selectCSVOutputFile"));
        panel6.add(browseThreadSummaryCSVOutputFile, cc.xy(5, 5));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:grow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:grow"));
        typeTabbedPane.addTab(ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.tab.standard"), panel7);
        panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JLabel label10 = new JLabel();
        this.$$$loadLabelText$$$(label10, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.standard.minimumLogLevel"));
        panel7.add(label10, cc.xy(1, 1));
        standardMinimumLogLevel = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("TRACE");
        defaultComboBoxModel2.addElement("DEBUG");
        defaultComboBoxModel2.addElement("INFO");
        defaultComboBoxModel2.addElement("WARN");
        defaultComboBoxModel2.addElement("ERROR");
        defaultComboBoxModel2.addElement("FATAL");
        standardMinimumLogLevel.setModel(defaultComboBoxModel2);
        panel7.add(standardMinimumLogLevel, cc.xy(3, 1));
        final JLabel label11 = new JLabel();
        this.$$$loadLabelText$$$(label11, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.standard.enterDetailsCSVOutputFile"));
        panel7.add(label11, cc.xy(1, 3));
        standardDetailsCSVOutputFile = new JTextField();
        panel7.add(standardDetailsCSVOutputFile, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label12 = new JLabel();
        this.$$$loadLabelText$$$(label12, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.standard.enterSummaryCSVOutputFile"));
        panel7.add(label12, cc.xy(1, 5));
        standardSummaryCSVOutputFile = new JTextField();
        standardSummaryCSVOutputFile.setText("");
        panel7.add(standardSummaryCSVOutputFile, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        browseStandardDetailsCSVOutputFile = new JButton();
        this.$$$loadButtonText$$$(browseStandardDetailsCSVOutputFile, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.button.selectCSVOutputFile"));
        panel7.add(browseStandardDetailsCSVOutputFile, cc.xy(5, 3));
        browseStandardSummaryCSVOutputFile = new JButton();
        this.$$$loadButtonText$$$(browseStandardSummaryCSVOutputFile, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.button.selectCSVOutputFile"));
        panel7.add(browseStandardSummaryCSVOutputFile, cc.xy(5, 5));
        final JLabel label13 = new JLabel();
        this.$$$loadLabelText$$$(label13, ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.label.separatorChar"));
        panel3.add(label13, cc.xy(1, 3));
        csvSeparatorCharField = new JTextField();
        csvSeparatorCharField.setText(ResourceBundle.getBundle("loganalyzer_messages").getString("org.jahia.loganalyzer.gui.swing.defaultSeparatorChat"));
        panel3.add(csvSeparatorCharField, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        servletMappingLabel = new JLabel();
        servletMappingLabel.setText("Servlet mapping");
        panel3.add(servletMappingLabel, cc.xy(1, 7));
        servletMapping = new JTextField();
        servletMapping.setText("");
        panel3.add(servletMapping, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label14 = new JLabel();
        label14.setText("Context mapping");
        panel3.add(label14, cc.xy(1, 5));
        contextMapping = new JTextField();
        panel3.add(contextMapping, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    class AnalysisWorker extends Thread {

        LogParserConfiguration logParserConfiguration;

        public AnalysisWorker(LogParserConfiguration logParserConfiguration) {
            this.logParserConfiguration = logParserConfiguration;
        }

        public void run() {
            try {
                InputStream in = new BufferedInputStream(
                        new ProgressMonitorInputStream(
                                LogAnalyzerMainDialog.this,
                                "Reading " + logParserConfiguration.getInputFileName(),
                                new FileInputStream(logParserConfiguration.getInputFileName())));
                InputStreamReader reader = new InputStreamReader(in);
                LogParser logParser = new LogParser();
                logParser.setLogParserConfiguration(logParserConfiguration);
                logParser.parse(reader);
            } catch (InterruptedIOException iioe) {
                JOptionPane.showMessageDialog(LogAnalyzerMainDialog.this, "Analysis cancelled by user", "Warning", JOptionPane.WARNING_MESSAGE);
                enableUI();
                return;
            } catch (IOException ioe) {
                ioe.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                JOptionPane.showMessageDialog(LogAnalyzerMainDialog.this, ioe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                LogAnalyzerMainDialog.this.setEnabled(true);
                enableUI();
                return;
            }
            JOptionPane.showMessageDialog(LogAnalyzerMainDialog.this, "Analysis completed.", "Status", JOptionPane.INFORMATION_MESSAGE);
            enableUI();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
        } catch (Throwable t) {
            log.error("Error setting look and feel", t);
        }
        LogAnalyzerMainDialog dialog = new LogAnalyzerMainDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
