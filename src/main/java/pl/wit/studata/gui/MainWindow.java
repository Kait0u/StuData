/**
 * 
 */
package pl.wit.studata.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pl.wit.studata.AppData;
import pl.wit.studata.Config;
import pl.wit.studata.InternalData;
import pl.wit.studata.backend.UniDB;
import pl.wit.studata.gui.dialogs.ConfigDialog;
import pl.wit.studata.gui.dialogs.MessageBoxes;
import pl.wit.studata.gui.enums.MainMenuItems;
import pl.wit.studata.gui.enums.MainTabs;
import pl.wit.studata.gui.interfaces.IDatabaseInteractor;
import pl.wit.studata.gui.tabs.ClassTab;
import pl.wit.studata.gui.tabs.GradingTab;
import pl.wit.studata.gui.tabs.GroupTab;
import pl.wit.studata.gui.tabs.StudentTab;

/**
 * Klasa reprezentująca okno główne programu
 * 
 * @author Jakub Jaworski
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Pasek menu górnego.
	 */
	private JMenuBar menuBar = null;

	/**
	 * Menu "Menu" w którym zawarte są funkcjonalności zapisu, konfiguracji, etc.
	 */
	private JMenu mnMenu = null;
	/**
	 * Przycisk-menu "About", które wywoła informacje o programie.
	 */
	private JMenuItem mnAbout = null;

	/**
	 * Mapa zbieraąca w jednym miejscu opcję menu "Menu" i zespolony z nią obiekt
	 * JMenuItem.
	 */
	private Map<MainMenuItems, JMenuItem> menuItems = null;

	/**
	 * Kontener z zakładkami, dzięki któremu możliwe będzie przełączanie się między
	 * formularzami.
	 */
	private JTabbedPane tabbedPane = null;

	/**
	 * Mapa w której kluczem jest wyliczenie zakładki, a wartością jest zakładka.
	 */
	private Map<MainTabs, JPanel> tabs = null;

	/**
	 * Aktualnie aktywna zakładka
	 */
	private IDatabaseInteractor currentTab = null;

	/**
	 * Konstruktor bezparametryczny.
	 * 
	 * @throws HeadlessException
	 */
	public MainWindow() throws HeadlessException {
		super();

		setTitle(AppData.APP_TITLE);
		setMinimumSize(new Dimension(AppData.MIN_WIDTH, AppData.MIN_HEIGHT));
		getContentPane().setLayout(new BorderLayout());

		// Konfiguracja paska menu
		menuBar = new JMenuBar();
		menuBar.add(mnMenu = new JMenu("Menu"));
		menuBar.add(mnAbout = new JMenuItem("About") {

			private static final long serialVersionUID = 1L;

			// Aby nie zajmował całego miejsca.
			@Override
			public Dimension getMaximumSize() {
				Dimension d1 = super.getPreferredSize();
				Dimension d2 = super.getMaximumSize();
				d2.width = d1.width;
				return d2;
			}

			// Konfiguruj wygląd i zachowanie
			{
				setOpaque(false);
				addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						MessageBoxes.showProgramInfoBox();
					}
				});
			}
		});

		setJMenuBar(menuBar);

		// Konfiguracja menu "Menu"
		menuItems = new HashMap<MainMenuItems, JMenuItem>();
		for (MainMenuItems item : MainMenuItems.values()) {
			String name = item.getOptionName();
			JMenuItem menuItem = new JMenuItem(name);

			switch (item) {
			case SAVE:
				menuItem.setAccelerator(KeyStroke.getKeyStroke('S', java.awt.event.InputEvent.CTRL_DOWN_MASK));
				menuItem.addActionListener((e) -> saveFromCurrentTab());
				break;
			case REFRESH:
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
				menuItem.addActionListener((e) -> updateCurrentTab());
				break;
			case CONFIG:
				menuItem.addActionListener((e) -> new ConfigDialog(this));
				break;
			case QUIT:
				menuItem.setAccelerator(KeyStroke.getKeyStroke('Q', java.awt.event.InputEvent.CTRL_DOWN_MASK));
				menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						quit();
					}
				});
				break;
			default:
				break;
			}

			mnMenu.add(menuItem);

			menuItems.put(item, menuItem);
		}

		// Konfiguracja zakładek
		tabbedPane = new JTabbedPane();
		tabs = new LinkedHashMap<MainTabs, JPanel>();

		for (MainTabs t : MainTabs.values()) {
			String name = t.getTabName();
			JPanel tabPanel;
			switch (t) {
			case STUDENTS:
				tabPanel = new StudentTab();
				break;
			case GROUPS:
				tabPanel = new GroupTab();
				break;
			case CLASSES:
				tabPanel = new ClassTab();
				break;
			case GRADING:
				tabPanel = new GradingTab();
				break;
			default:
				tabPanel = new JPanel();
				tabPanel.add(new JLabel("Welcome to the tab: ".concat(name).concat("! You probably got lost and that's how you got here!")));
				break;
			}

			if (tabPanel instanceof IDatabaseInteractor) tabbedPane.addTab(name, tabPanel);

			tabs.put(t, tabPanel);
		}

		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// -----------------------
		updateCurrentTabRef();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Centruj okno na ekranie
		InternalData.registerWindow(this);
		setVisible(true);
		configureListeners();
	}

	private void configureListeners() {
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (currentTab.hasUnsavedChanges()) {
					savePreviousTab();
				}
				updateCurrentTabRef();
				currentTab.update();
			}
		});
	}

	private void updateCurrentTabRef() {
		int idx = tabbedPane.getSelectedIndex();
		Object retrieved = tabs.values().toArray()[idx];
		currentTab = retrieved instanceof IDatabaseInteractor ? (IDatabaseInteractor) retrieved : null;
	}
	
	private void updateCurrentTab() {
		if (currentTab.hasUnsavedChanges()) {
			if (!MessageBoxes.showConfirmationBox("Are you sure?", "Unsaved changes detected! If you refresh now, you will lose your progress. Are you sure you want to refresh?")) 
				return;
		}
		setEnabled(false);
		currentTab.update();
		setEnabled(true);
	}
	
	/**
	 * Metoda zapisująca obecną kartę (za uprzednią zgodą użytkownika)
	 */
	private void saveFromCurrentTab() {
		if (currentTab.hasUnsavedChanges()) {
			boolean shouldSave = MessageBoxes.showConfirmationBox("Unsaved Changes!",
					"The current tab has got some unsaved changes. Would you like to save them?");
			
			if (shouldSave) {
				currentTab.pushToDB();
			}
		} else MessageBoxes.showInfoBox("Good to go!", "No changes to save!");
		
	}
	
	/**
	 * Metoda zapisująca kartę przy zmianie karty (za uprzednią zgodą użytkownika), cofająca zmiany na karcie poprzedniej w przypadku odmowy.
	 */
	private void savePreviousTab() {
		boolean shouldSave = MessageBoxes.showConfirmationBox("Unsaved Changes!",
				"The previous tab has got some unsaved changes. Would you like to save them?");
		
		if (shouldSave) {
			currentTab.pushToDB();
			
		} else {
			currentTab.nullifyChanges();
			currentTab.update();
		}
		
	}

	private void quit() {
		boolean canQuit = false;

		if (currentTab.hasUnsavedChanges()) {
			boolean shouldSave = MessageBoxes.showConfirmationBox("Unsaved Changes!",
					"The current tab has got some unsaved changes. Would you like to save them before you quit?");
			if (shouldSave)
				saveFromCurrentTab();
		}

		setEnabled(false);

		try {
			// TODO Zapis bazy do pliku.
			UniDB db = InternalData.DATABASE;
			synchronized (db) {
				db.saveToFile(InternalData.DATABASE_PATH);
			}
		} catch (Exception e) {
			MessageBoxes.showErrorBox("Error!", e.getMessage());
		}

		InternalData.EXECUTOR.shutdown();

		try {
			InternalData.EXECUTOR.awaitTermination(1, TimeUnit.MINUTES);
			canQuit = true;
		} catch (InterruptedException ex) {
			canQuit = MessageBoxes.showConfirmationBox("Termination Error!",
					"There was an error trying to quit the program. Do you wish to force-quit it?\nWARNING: This poses a risk of losing or corrupting the data!");
		}

		if (canQuit) {
			InternalData.destroyAllWindows();
			System.exit(0);
		} else {
			setEnabled(true);
		}
	}

}
