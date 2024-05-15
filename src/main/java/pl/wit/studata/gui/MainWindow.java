/**
 * 
 */
package pl.wit.studata.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import pl.wit.studata.AppData;
import pl.wit.studata.gui.enums.MainMenuItems;
import pl.wit.studata.gui.enums.MainTabs;
import pl.wit.studata.gui.tabs.StudentTab;

/**
 * Klasa reprezentująca okno główne programu
 * @author Jakub Jaworski
 */
public class MainWindow extends JFrame {
	/**
	 * Pasek menu górnego.
	 */
	private JMenuBar menuBar = null;
	
	/**
	 * Menu "Menu" w którym zawarte są funkcjonalności zapisu, konfiguracji, etc.
	 */
	private JMenu mnMenu = null;
	/**
	 * Menu "About", które wywoła informacje o programie.
	 */
	private JMenu mnAbout = null;
	
	/**
	 * Mapa zbieraąca w jednym miejscu opcję menu "Menu" i zespolony z nią obiekt JMenuItem.
	 */
	private Map<MainMenuItems, JMenuItem> menuItems = null;
	
	/**
	 * Kontener z zakładkami, dzięki któremu możliwe będzie przełączanie się między formularzami.
	 */
	private JTabbedPane tabbedPane = null;
	
	/**
	 * Mapa w której kluczem jest wyliczenie zakładki, a wartością jest zakładka.
	 */
	private Map<MainTabs, JPanel> tabs = null;
	

	/**
	 * Konstruktor bezparametryczny.
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
		menuBar.add(mnAbout = new JMenu("About"));
		setJMenuBar(menuBar);
		
		// Konfiguracja menu "Menu"
		menuItems = new HashMap<MainMenuItems, JMenuItem>();
		for (MainMenuItems item: MainMenuItems.values()) {
			String name = item.getOptionName();
			JMenuItem menuItem = new JMenuItem(name);
			mnMenu.add(menuItem);
			
			menuItems.put(item, menuItem);
		}
		
		// Konfiguracja zakładek
		tabbedPane = new JTabbedPane();
		tabs = new LinkedHashMap<MainTabs, JPanel>();
		
		for (MainTabs t: MainTabs.values()) {
			String name = t.getTabName();
			JPanel tabPanel;
			switch (t) {
				case STUDENTS:
					tabPanel = new StudentTab();
					break;
				default:
					tabPanel = new JPanel();
					tabPanel.add(new JLabel("Welcome to the tab: ".concat(name).concat("!")));
					break;
			}
			
			tabbedPane.addTab(name, tabPanel);
			
			tabs.put(t, tabPanel);
		}
		
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		// -----------------------
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Centruj okno na ekranie
		setVisible(true);
	}
}
