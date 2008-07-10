package org.jahia.loganalyzer.gui.swing;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.jahia.loganalyzer.LogParser;
import org.jahia.loganalyzer.LogParserConfiguration;

public class LogAnalyzerMainDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton browseInputLogFile;
    private JTextField inputLogFile;
    private JTextField performanceDetailsCSVOutputFile;
    private JTextField csvSeparatorCharField;
    private JButton browseCSVOutputFile;
    private JTabbedPane typeTabbedPane;
    private JCheckBox performanceActivated;
    private JComboBox regexpPatternField;
    private JTextField dateFormatField;
    private JCheckBox threadDumpsActivated;
    private JTextField threadDetailsCSVOutputFile;
    private JCheckBox exceptionsActivated;
    private JTextField exceptionCSVOutputFile;
    private JTextField threadSummaryCSVOutputFile;
    private JTextField servletMapping;
    private JLabel servletMappingLabel;
    private JTextField contextMapping;
    private JTextField performanceSummaryCSVOutputFile;
    private JFileChooser fileChooser;
    private static final String DEFAULT_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss,SSS";
    private static final String DEFAULT_CONTEXTMAPPING_STRING = "/jahia";
    private static final String DEFAULT_SERVLETMAPPING_STRING = "/Jahia";
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

        File defaultInputLogFile = new File("catalina.out");
        inputLogFile.setText(defaultInputLogFile.getAbsoluteFile().toString());
        File defaultPerfOutputFile = new File("jahia-perf-analysis.csv");
        performanceDetailsCSVOutputFile.setText(defaultPerfOutputFile.getAbsoluteFile().toString());
        File defaultThreadDetailsOutputFile = new File("jahia-threads-details.csv");
        threadDetailsCSVOutputFile.setText(defaultThreadDetailsOutputFile.getAbsoluteFile().toString());
        File defaultThreadSummaryOutputFile = new File("jahia-threads-summary.csv");
        threadSummaryCSVOutputFile.setText(defaultThreadSummaryOutputFile.getAbsoluteFile().toString());
        File defaultExceptionsOutputFile = new File("jahia-exceptions-analysis.csv");
        exceptionCSVOutputFile.setText(defaultExceptionsOutputFile.getAbsoluteFile().toString());
        dateFormatField.setText(DEFAULT_DATE_FORMAT_STRING);
        contextMapping.setText(DEFAULT_CONTEXTMAPPING_STRING);
        servletMapping.setText(DEFAULT_SERVLETMAPPING_STRING);

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
                    //This is where a real application would open the file.
                    inputLogFile.setText(file.getAbsoluteFile().toString());
                } else {
                }
            }
        });
        browseCSVOutputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setSelectedFile(new File(performanceDetailsCSVOutputFile.getText()));
                int returnVal = fileChooser.showOpenDialog(LogAnalyzerMainDialog.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    //This is where a real application would open the file.
                    performanceDetailsCSVOutputFile.setText(file.getAbsoluteFile().toString());
                } else {
                }
            }
        });
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
        List patternList = new ArrayList();
        patternList.add(regexpPatternField.getSelectedItem().toString());
        logParserConfiguration.setInputFileName(inputLogFile.getText());
        logParserConfiguration.setPerformanceAnalyzerActivated(performanceActivated.isSelected());
        logParserConfiguration.setPerfOutputFileName(performanceDetailsCSVOutputFile.getText());
        logParserConfiguration.setThreadDumpAnalyzerActivated(threadDumpsActivated.isSelected());
        logParserConfiguration.setThreadDetailsOutputFileName(threadDetailsCSVOutputFile.getText());
        logParserConfiguration.setThreadSummaryOutputFileName(threadSummaryCSVOutputFile.getText());
        logParserConfiguration.setExceptionAnalyzerActivated(exceptionsActivated.isSelected());
        logParserConfiguration.setExceptionsOutputFileName(exceptionCSVOutputFile.getText());
        logParserConfiguration.setCsvSeparatorChar(csvSeparatorCharField.getText().charAt(0));
        logParserConfiguration.setPatternList(patternList);
        logParserConfiguration.setDateFormatString(dateFormatField.getText());
        logParserConfiguration.setServletMapping(servletMapping.getText());
        AnalysisWorker worker = new AnalysisWorker(logParserConfiguration);
        worker.start();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        try {
              UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
           } catch (Exception e) {}
        LogAnalyzerMainDialog dialog = new LogAnalyzerMainDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
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
}
