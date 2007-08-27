package org.jahia.loganalyzer.gui.swing;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import org.jahia.loganalyzer.LogParser;

public class LogAnalyzerMainDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton browseButton;
    private JTextField inputLogFile;
    private JTextField csvOutputFile;
    private JTextField csvSeparatorCharField;
    private JComboBox analysisTypeSelection;
    private JFileChooser fileChooser;

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

        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(LogAnalyzerMainDialog.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    //This is where a real application would open the file.
                    inputLogFile.setText(file.getAbsoluteFile().toString());
                } else {
                }
            }
        });
    }

    private void disableUI() {
        buttonOK.setEnabled(false);
        buttonCancel.setEnabled(false);
        browseButton.setEnabled(false);
        setEnabled(false);
    }

    private void enableUI() {
        buttonOK.setEnabled(true);
        buttonCancel.setEnabled(true);
        browseButton.setEnabled(true);
        setEnabled(true);
    }

    private void onOK() {
        disableUI();
        AnalysisWorker worker = new AnalysisWorker(inputLogFile.getText(),
        csvOutputFile.getText(),
        csvSeparatorCharField.getText().charAt(0));
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

        public AnalysisWorker(String inputFileName, String outputFileName, char csvSeparatorChar) {
            this.inputFileName = inputFileName;
            this.outputFileName = outputFileName;
            this.csvSeparatorChar = csvSeparatorChar;
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
                logParser.parse(reader, writer, new ArrayList());
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
