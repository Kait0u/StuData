/**
 * 
 */
package pl.wit.studata.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import pl.wit.studata.AppData;
import pl.wit.studata.Config;
import pl.wit.studata.InternalData;
import pl.wit.studata.gui.enums.ConfigSettings;
import pl.wit.studata.gui.widgets.FolderSelector;
import pl.wit.studata.gui.widgets.FormWidget;

/**
 * 
 */
public class ConfigDialog extends JDialog {
	private static final String TITLE = AppData.APP_TITLE.concat(" - Config");
	
	private JSpinner spnThreadPool = null;
	private FolderSelector folselDbPath = null;
	private JButton btnApply = null;
	private JButton btnCancel = null;
			
	
	/**
	 * Konstruktor przyjmujący okno rodzica.
	 * 
	 * @param owner Okno-rodzic.
	 */
	public ConfigDialog(Window owner) {
		super(owner);
		setModal(true);
		setTitle(TITLE);
		setMinimumSize(new Dimension(475, 250));
		setResizable(false);

		setLayout(new BorderLayout());

		FormWidget form = new FormWidget();
		for (ConfigSettings setting : ConfigSettings.values()) {
			JComponent comp = null;
			switch (setting) {
			case THREADPOOL_SIZE:
				spnThreadPool = new JSpinner(new SpinnerNumberModel(Config.THREADPOOL_SIZE, 1, Runtime.getRuntime().availableProcessors(), 1));
				comp = spnThreadPool;
				break;
			case DB_PATH:
				folselDbPath = new FolderSelector(InternalData.DATABASE_FILE.getParentFile().getPath());
				folselDbPath.setMinimumSize(new Dimension(250, 30));
				comp = folselDbPath;
				break;
			}
			
			form.addField(setting.getSettingName().concat(": "), comp);
		}
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				submit(false);
			}
		});
		
		
		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				submit(true);
				
			}
		});
		
		form.addWidget(btnCancel);
		form.addWidget(btnApply);
		form.setPreferredSize(new Dimension(410, 240));
		add(form, BorderLayout.CENTER);
		
		setLocationRelativeTo(null); // Centruj okno na ekranie
		setVisible(true);
	}
	
	private void submit(boolean shouldApply) {
		if (shouldApply) {
			int threadPoolSize = (Integer) spnThreadPool.getValue();
			String dbPath = folselDbPath.getPath();
			
			// Validate
			if (!new File(dbPath).exists()) {
				MessageBoxes.showErrorBox("Error!", "Invalid database path!");
				return;
			}
			
			Config.THREADPOOL_SIZE = threadPoolSize;
			Config.DB_PATH = dbPath.concat("/data.studata");
			
			Config.saveToFile();
			MessageBoxes.showInfoBox("Success!", "Restart the application for changes to take effect.");
		}
		
		dispose();
	}

}
