package org.jahia.loganalyzer.gui.swing;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.jahia.loganalyzer.LogParser;

public class LogAnalyzerMainDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton browseInputLogFile;
    private JTextField inputLogFile;
    private JTextField csvOutputFile;
    private JTextField csvSeparatorCharField;
    private JButton browseCSVOutputFile;
    private JTabbedPane typeTabbedPane;
    private JCheckBox activatedCheckBox;
    private JTextField regexpPatternField;
    private JTextField dateFormatField;
    private JFileChooser fileChooser;
    private static final String DEFAULT_REGEXP_PATTERN = ".*?\\[(.*?)\\].*org\\.jahia\\.bin\\.Jahia.*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*";
    private static final String DEFAULT_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss,SSS";

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
        File defaultOutputFile = new File("jahia-log-analyzer.csv");
        csvOutputFile.setText(defaultOutputFile.getAbsoluteFile().toString());
        regexpPatternField.setText(DEFAULT_REGEXP_PATTERN);
        dateFormatField.setText(DEFAULT_DATE_FORMAT_STRING);

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
                fileChooser.setSelectedFile(new File(csvOutputFile.getText()));
                int returnVal = fileChooser.showOpenDialog(LogAnalyzerMainDialog.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    //This is where a real application would open the file.
                    csvOutputFile.setText(file.getAbsoluteFile().toString());
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
        patternList.add(regexpPatternField.getText());
        AnalysisWorker worker = new AnalysisWorker(inputLogFile.getText(),
        csvOutputFile.getText(),
        csvSeparatorCharField.getText().charAt(0),
        patternList, dateFormatField.getText());
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

        private String inputFileName;
        private String outputFileName;
        private char csvSeparatorChar;
        private List patternList;
        private String dateFormatString;

        public AnalysisWorker(String inputFileName, String outputFileName, char csvSeparatorChar, List patternList, String dateFormatString) {
            this.inputFileName = inputFileName;
            this.outputFileName = outputFileName;
            this.csvSeparatorChar = csvSeparatorChar;
            this.patternList = patternList;
            this.dateFormatString = dateFormatString;
        }

        public void run() {
            try {
                InputStream in = new BufferedInputStream(
                        new ProgressMonitorInputStream(
                                LogAnalyzerMainDialog.this,
                                "Reading " + inputFileName,
                                new FileInputStream(inputFileName)));
                InputStreamReader reader = new InputStreamReader(in);
                FileWriter writer = new FileWriter(outputFileName);
                LogParser logParser = new LogParser();
                logParser.setCsvOutputSeparatorChar(csvSeparatorChar);
                logParser.parse(reader, writer, patternList, dateFormatString);
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
