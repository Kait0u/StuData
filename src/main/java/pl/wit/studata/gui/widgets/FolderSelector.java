package pl.wit.studata.gui.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * 
 * @author Jakub Jaworski
 */
public class FolderSelector extends JPanel {
    private JTextField textField;
    private JButton browseButton;
    private String selectedPath;

    public FolderSelector() {
        setLayout(new BorderLayout(0, 0));

        textField = new JTextField();
        textField.setEditable(false);
        add(textField, BorderLayout.CENTER);

        browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFolder();
            }
        });
        add(browseButton, BorderLayout.EAST);
    }
    
    public FolderSelector(String defaultPath) {
    	this();
    	selectedPath = defaultPath;
    	textField.setText(selectedPath);
    }

    private void chooseFolder() {
        JFileChooser fileChooser = selectedPath == null ? new JFileChooser() : new JFileChooser(new File(selectedPath).getParent());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedPath = selectedFile.getPath();
            textField.setText(selectedPath);
        }
    }

    public String getPath() {
        return selectedPath;
    }
    
    public void setPath() {
    	
    }
}
