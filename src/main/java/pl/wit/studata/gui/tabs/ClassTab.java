package pl.wit.studata.gui.tabs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.javatuples.Pair;

import pl.wit.studata.InternalData;
import pl.wit.studata.backend.UniDB;
import pl.wit.studata.backend.models.ClassCriterion;
import pl.wit.studata.backend.models.UniClass;
import pl.wit.studata.gui.dialogs.CriterionCreationDialog;
import pl.wit.studata.gui.dialogs.MessageBoxes;
import pl.wit.studata.gui.enums.ClassTableHeaders;
import pl.wit.studata.gui.interfaces.IDatabaseInteractor;
import pl.wit.studata.gui.widgets.CRUDList;
import pl.wit.studata.gui.widgets.FormWidget;
import pl.wit.studata.gui.widgets.TableWidget;

/**
 * Klasa opisująca zakładkę "Classes"
 * @author Jakub Jaworski
 */
public class ClassTab extends JPanel implements IDatabaseInteractor, ActionListener {

	private static final long serialVersionUID = 1L;

	/**
	 * Panel górny. 
	 * (Wyświetlanie danych)
	 */
	private JPanel pnlTop = null;
	
	/**
	 * Panel dolny.
	 * (Wprowadzanie danych i filtrowanie)
	 */
	private JPanel pnlBot = null;
	
	/**
	 * Podpanel panelu dolnego przechowujący jego karty.
	 */
	private JPanel pnlBotCards = null;
	
	/**
	 * Karta w panelu dolnym, skupiająca się na dodawaniu, aktualizacji i usuwaniu danych.
	 */
	private JPanel pnlCrUpDel = null;
	
	/**
	 * Karta w panelu dolnym, skupiająca się na wyszukiwaniu danych.
	 */
	private JPanel pnlQuery = null;

	/**
	 * Tabela na dane o grupach
	 */
	private TableWidget tblData = null;
	
	/**
	 * Widok z suwakiem, który zwierać będzie tabelę.
	 * @see tblData
	 */
	private JScrollPane scrlTblData = null;
	
	/**
	 * Mapa pola formularza do komponentów formularzowych (formularz tworzenia)
	 */
	private Map<ClassTableHeaders, Component> formMap = null;
	
	/**
	 * Mapa pola formularza do komponentów formularzowych (formularz wyszukiwania
	 */
	private Map<ClassTableHeaders, Component> formQueryMap = null;
	
	/**
	 * Przycisk, którego funkcją jest zatwierdzenie formularza
	 */
	private JButton btnSubmit = null;
	
	/**
	 * Przycisk, którego funkcją jest wczytanie zaznaczonego wiersza z tabeli do formularza.
	 */
	private JButton btnEdit = null;

	/**
	 * Przycisk, którego funkcją jest odznaczenie zaznaczonego wiersza w tabeli.
	 */
	private JButton btnDeselect = null;
	
	/**
	 * Przycisk, którego funkcją jest usunięcie zaznaczonego wiersza z tabeli oraz danych, które reprezentuje ten wiersz z kolekcji w których występuje.
	 */
	private JButton btnDelete = null;
	
	/**
	 * Przycisk, którego funkcją jest dokonanie wyszukiwania według podanych kryteriów.
	 */
	private JButton btnSearch = null;
	
	/**
	 * Przycisk, którego funkcją jest przywrócenie domyślnych wartości kryteriom.
	 */
	private JButton btnClearCriteria = null;
	
	// [Dane]
	
	/**
	 * Lista do przechowywania zajęć pobranych z bazy danych w danym momencie.
	 */
	private List<UniClass> classes = null;
	
	/**
	 * Mapa pozwalająca na pilnowanie i porównywanie zmian przedmiotów, celem późniejszego wprowadzenia ich do bazy.
	 */
	private Map<UniClass, UniClass> copyToOriginalClass = null;
	
	/**
	 * Mapa pozwalająca na pilnowanie i porównywanie zmian przedmiotów, celem późniejszego wprowadzenia ich do bazy.
	 */
	private Map<UniClass, UniClass> originalToCopyClass = null;
	
	/**
	 * Mapa pozwalająca na pilnowanie i porównywanie zmian przedmiotów, celem późniejszego wprowadzenia ich do bazy.
	 */
	private Map<ClassCriterion, ClassCriterion> copyToOriginalCriterion = null;
	
	/**
	 * Mapa pozwalająca na pilnowanie i porównywanie zmian przedmiotów, celem późniejszego wprowadzenia ich do bazy.
	 */
	private Map<ClassCriterion, ClassCriterion> originalToCopyCriterion = null;
	
	/**
	 * Zmienna do przechowywania, czy istnieją jakieś modyfikacje, które nie zostały ani zatwierdzone, ani odrzucone.
	 */
	private boolean unsavedChanges = false;
	
	// [Różne]
	
	// Zmienne wewnętrzne
	private static final String CREATE_FORM_STR = "Create";
	private static final String SEARCH_FORM_STR = "Search";

	
	/**
	 * Konstruktor bezparametryczny.
	 */
	@SuppressWarnings("serial")
	public ClassTab() {
		super();
		
		setLayout(new GridLayout(2, 1));
		
		// Panel górny (wyświetlanie)
		pnlTop = new JPanel();
		pnlTop.setBorder(BorderFactory.createTitledBorder("Data"));
		pnlTop.setLayout(new BorderLayout());
		
		String[] headers = Arrays.asList(ClassTableHeaders.values())
				.stream()
				.map(new Function<ClassTableHeaders, String>() {
					@Override
					public String apply(ClassTableHeaders h) {
						return h.getHeaderName();
					}
				}).toArray(String[]::new);
		
		tblData = new TableWidget(headers);
		tblData.setAutoCreateRowSorter(true);
		tblData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrlTblData = new JScrollPane(tblData);
		
		pnlTop.add(scrlTblData, BorderLayout.CENTER);
		
		
		add(pnlTop);
		
		// ---------------------- 
		
		// Panel dolny (wprowadzanie danych i wyszukiwanie danych)
		pnlBot = new JPanel();
		pnlBot.setBorder(BorderFactory.createTitledBorder("Form"));
		pnlBot.setLayout(new BorderLayout());
		
		JComboBox<String> cmbForm = new JComboBox<String>(new String[] {CREATE_FORM_STR, SEARCH_FORM_STR});
		cmbForm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> source = (JComboBox<String>) e.getSource();
				String cardName = (String) source.getSelectedItem();
				CardLayout cl = (CardLayout) pnlBotCards.getLayout();
				cl.show(pnlBotCards, cardName);
			}
		});
		
		pnlBotCards = new JPanel();
		pnlBotCards.setLayout(new CardLayout());
		
		pnlBot.add(cmbForm, BorderLayout.PAGE_START);
		pnlBot.add(pnlBotCards, BorderLayout.CENTER);
		
		pnlCrUpDel = new JPanel();
		pnlCrUpDel.setLayout(new GridBagLayout());
		GridBagConstraints cPnlCrRupDel = new GridBagConstraints();
		cPnlCrRupDel.insets = new Insets(0, 20, 0, 20);
		
		FormWidget formCreateUpdate = new FormWidget();
		formMap = new LinkedHashMap<>();
		for (ClassTableHeaders header: ClassTableHeaders.values()) {
			String label = header.getHeaderName().concat(": ");
			Component comp = null;
			
			switch (header) {
				case CODE:
				case NAME:
					comp = new JTextField(20);
					break;
				case CRITERIA:
					comp = new CRUDList() {{
						Dimension minSize = getMinimumSize();
						minSize.height = 80;
						minSize.width = 300;
						setMinimumSize(minSize);
						setAddButtonAction((e) -> {
							Pair<String, Integer> pair = CriterionCreationDialog.showDialog(null);
							if (pair == null) return;
							
							String critName = pair.getValue0();
							Integer maxPoints = pair.getValue1();
							
							if (getListItems().stream()
									.filter((item) -> ((ClassCriterion) item).getCriterionName().equals(critName))
									.count() > 0) {
								MessageBoxes.showErrorBox("Error!", "The name \"".concat(critName).concat("\" is already in use!"));
								return;
							}
							
							ClassCriterion criterion = new ClassCriterion(critName, maxPoints);
							addItem(criterion);
						});
						setEditButtonAction((e) -> {
							ClassCriterion selected = (ClassCriterion) getSelectedItem();
							if (selected != null) {
								String critName = selected.getCriterionName();
								Integer maxPoints = selected.getMaxPoints();
								
								Pair<String, Integer> pair = CriterionCreationDialog.showDialog(null, critName, maxPoints);
								if (pair == null) return;
								
								String newCritName = pair.getValue0();
								Integer newMaxPoints = pair.getValue1();
								
								if (getListItems().stream()
										.filter((item) -> item != selected && ((ClassCriterion) item).getCriterionName().equals(newCritName))
										.count() > 0) {
									MessageBoxes.showErrorBox("Error!", "The name \"".concat(newCritName).concat("\" is already in use!"));
									return;
								}
								
								selected.setCriterionName(newCritName);
								selected.setMaxPoints(newMaxPoints);
								
								updateSelectedItem(selected);
							}
						});
					}};
					break;
			}
			
			if (comp != null) {
				formCreateUpdate.addField(label, comp);
				formMap.put(header, comp);
			}
		}
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(this);
		formCreateUpdate.addWidget(btnSubmit);
		
		pnlCrUpDel.add(formCreateUpdate, cPnlCrRupDel);
		
		// Update-delete frame
		JPanel pnlUpdateDelete = new JPanel();
		pnlUpdateDelete.setLayout(new GridLayout(3, 1));
		pnlUpdateDelete.setBorder(BorderFactory.createTitledBorder("Selection Options"));
		pnlUpdateDelete.setMinimumSize(new Dimension(150, 120));
		
		
		btnEdit = new JButton("Load & Edit");
		btnEdit.addActionListener(this);
		pnlUpdateDelete.add(btnEdit);
		
		btnDeselect = new JButton("Deselect");
		btnDeselect.addActionListener(this);
		pnlUpdateDelete.add(btnDeselect);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(this);
		btnDelete.setBackground(Color.RED);
		btnDelete.setForeground(Color.WHITE);
		pnlUpdateDelete.add(btnDelete);
		
		++cPnlCrRupDel.gridy;
		pnlCrUpDel.add(pnlUpdateDelete, cPnlCrRupDel);
		pnlBotCards.add(pnlCrUpDel, CREATE_FORM_STR);
		
		// Panel do poszukiwań
		pnlQuery = new JPanel(new GridLayout(1, 1));
		FormWidget formQuery = new FormWidget();
		
		formQueryMap = new LinkedHashMap<>();
		for (ClassTableHeaders header: ClassTableHeaders.values()) {
			String label = header.getHeaderName().concat(": ");
			Component comp = null;
			
			switch (header) {
				case CODE:
				case NAME:
					comp = new JTextField(20);
					break;
			default:
				break;
		}
			
			if (comp != null) {
				formQuery.addField(label, comp);
				formQueryMap.put(header, comp);
			}
		}
		
		btnSearch = new JButton("Filter");
		btnSearch.addActionListener(this);
		
		btnClearCriteria = new JButton("Reset Criteria");
		btnClearCriteria.addActionListener(this);
		
		formQuery.addWidget(btnSearch);
		formQuery.addWidget(btnClearCriteria);
		
		Dimension pSize = formQuery.getPreferredSize();
		pSize.height = 200;
		pSize.width = 300;
		formQuery.setPreferredSize(pSize);
	
		pnlQuery.add(formQuery);
		
		pnlBotCards.add(pnlQuery, SEARCH_FORM_STR);
		
		add(pnlBot);
		
		// Czynności po zbudowaniu interfejsu
		update();
		
	}
	
	private void updateTable() {
		if (tblData == null) return;
		
		tblData.clear();
		
		if (classes != null) {
			for (UniClass c: classes) {
				addTableRow(c);
			}
		} 	
	}
	
	/**
	 * Metoda dodająca wiersz do tabeli na podstawie istniejącej Grupy.
	 * @param g Grupa do dodania.
	 */
	private void addTableRow(UniClass c) {
		if (c == null) return;
		
		Object[] rowData = new Object[] {
			c.getCode(),
			c.getClassName(),
			c.getCriteriaList()
		};
		
		tblData.addDataRow(rowData);
	}
	
	/**
	 * Aktualizuje wiersz w tabeli o zadanym indeksie informacjami o przedmiocie.
	 * @param rowIdx Indeks wiersza.
	 * @param c Przedmiot.
	 */
	private void updateTableRow(int rowIdx, UniClass c) {
		if (c == null) return;
		
		Object[] rowData = new Object[] {
			c.getCode(),
			c.getClassName(),
			c.getCriteriaList()
		};
		
		tblData.updateRow(rowIdx, rowData);
	}
	
	/**
	 * Metoda szukająca wiersza, który reprezentuje dany przedmiot, po jego kodzie.
	 * @param c Przedmot.
	 * @return Indeks wiersza w tabeli, który reprezentuje przedmiot lub -1, jeżeli nic nie znaleziono.
	 */
	private int getRowIdx(UniClass c) {
		if (c == null)
			return -1;
		
		String classCode = c.getCode();
		
		return tblData.findRowByCellValue(classCode, ClassTableHeaders.CODE.ordinal());
	}
	
	/**
	 * Metoda dodająca przedmiot.
	 * @param code Kod przedmiotu.
	 * @param name Nazwa przedmiotu.
	 * @param criteriaList Lista kryteriów przedmiotu.
	 * @return true, jeżeli udało się dodać przedmiot, false jeśli nie.
	 */
	private boolean addClass(String code, String name, List<ClassCriterion> criteria) {
		if (code == null || name == null) return false;
		
		UniClass c = new UniClass(name, code, criteria);
		classes.add(c);
		addTableRow(c);
		return true;
	}
	
	/**
	 * Metoda usuwa przedmiot ze wszystkich struktur danych tej klasy.
	 * @param toDelete Przedmiot do usunięcia
	 * @return true jeżeli przedmiot został usunięty, false jeśli żadne usunięcie nie nastąpiło.
	 */
	private boolean deleteClass(UniClass toDelete) {
		if (toDelete == null)
			return false;
		
		
		if (!classes.contains(toDelete))
			return false;
		
		classes.remove(toDelete);
		return true;
	}
	
	/**
	 * Metoda aktualizuje obiekt grupy w lokalnych strukturach danych.
	 * @param toUpdate Aktualizowany przedmiot.
	 * @param name Nowa nazwa.
	 * @param newCriteria Nowe kryteria oceniania.
	 * @return true, jeżeli aktualizacja się dokonała, false jeśli nie
	 */
	private boolean updateClass(UniClass toUpdate, String name, List<ClassCriterion> newCriteria) {
		if (toUpdate == null)
			return false;
		
		toUpdate.setClassName(name);
		toUpdate.setCriteriaList(newCriteria);
		
		int rowIdx = getRowIdx(toUpdate);
		
		updateTableRow(rowIdx, toUpdate);
		
		return true;
	}
	
	/**
	 * Metoda zwracająca przedmiot o danym kodzie w lokalnych danych, o ile takowy istnieje.
	 * @param code Kod przedmiotu.
	 * @return UniClass (jeśli istnieje) lub null
	 */
	private UniClass findByCode(String code) {
		return classes.stream().filter((c) -> (c.getCode().equals(code))).findFirst().orElse(null);
	}
	
	private void filterTable() {
		JTextField tfCode = (JTextField) formQueryMap.get(ClassTableHeaders.CODE);
		JTextField tfName = (JTextField) formQueryMap.get(ClassTableHeaders.NAME);
		
		String queryCode = tfCode.getText().trim();
		queryCode = queryCode.isEmpty() ? null : queryCode;
		
		String queryName = tfName.getText().trim();
		queryName = queryName.isEmpty() ? null : queryName;
		
		updateTable();
		
		List<Integer> rowsToDelete = new ArrayList<Integer>(tblData.getRowCount());
		
		for (int rowIdx = 0; rowIdx < tblData.getRowCount(); ++rowIdx) {
			boolean criteriaMet = true;
			
			Object[] row = tblData.getRow(rowIdx);
			
			if (criteriaMet && queryCode != null) {
				String val = ((String) row[ClassTableHeaders.CODE.ordinal()]).toUpperCase();
				criteriaMet &= val.contains(queryCode.toUpperCase());
			}
			if (criteriaMet && queryName != null) {
				String val = ((String) row[ClassTableHeaders.NAME.ordinal()]).toUpperCase();
				criteriaMet &= val.contains(queryName.toUpperCase());
			}
			
			// Oznacz do usunięcia jeśli koniunkcja niespełniona
			if (!criteriaMet) {
				rowsToDelete.add(rowIdx);
			}
		}
		
		tblData.deleteMultipleRows(rowsToDelete);
	}
	
	private void resetFilterCriteria() {
		JTextField tfCode = (JTextField) formQueryMap.get(ClassTableHeaders.CODE);
		JTextField tfName = (JTextField) formQueryMap.get(ClassTableHeaders.NAME);
		
		tfCode.setText("");
		tfName.setText("");
		
		updateTable();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == btnSubmit) {
			JTextField tfCode = (JTextField) formMap.get(ClassTableHeaders.CODE);
			JTextField tfName = (JTextField) formMap.get(ClassTableHeaders.NAME);
			CRUDList crlCriteria = (CRUDList) formMap.get(ClassTableHeaders.CRITERIA);
			
			String code = tfCode.getText();
			String name = tfName.getText();
			List<ClassCriterion> criteria = crlCriteria.getListItems()
					.stream().map((item) -> (ClassCriterion) item)
					.collect(Collectors.toCollection(LinkedList::new));
			
			UniClass queryClass = findByCode(code);
			
			if (queryClass == null)
				unsavedChanges |= addClass(code, name, criteria);
			else if (MessageBoxes.showConfirmationBox("Are you sure?", "Are you sure that you want to update class ".concat(queryClass.toString()).concat("?")))
				unsavedChanges |= updateClass(queryClass, name, criteria);
		} else if (source == btnDeselect) {
			tblData.clearSelection();
		} else if (source == btnDelete) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) {
				MessageBoxes.showInfoBox("No selection!", "Please select a row first!");
				return;
			}
			
			String codeVal = (String) tblData.getValueAt(selectedIdx, ClassTableHeaders.CODE.ordinal());
			
			UniClass toDelete = findByCode(codeVal);
			if (toDelete == null) return;
			
			if (MessageBoxes.showConfirmationBox("Are you sure?", "Are you sure that you want to DELETE class ".concat(toDelete.toString()).concat("?"))) {
				unsavedChanges |= deleteClass(toDelete);
				tblData.deleteRow(selectedIdx);
				tblData.clearSelection();
			} 
		} else if (source == btnEdit) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) {
				MessageBoxes.showInfoBox("No selection!", "Please select a row first!");
				return;
			}
			
			String codeVal = (String) tblData.getValueAt(selectedIdx, ClassTableHeaders.CODE.ordinal());
			
			UniClass toUpdate = findByCode(codeVal); 
			
			if (toUpdate != null) {
				String code = toUpdate.getCode();
				String name = toUpdate.getClassName();
				List<ClassCriterion> criteria = toUpdate.getCriteriaList();
				
				JTextField tfCode = (JTextField) formMap.get(ClassTableHeaders.CODE);
				JTextField tfName = (JTextField) formMap.get(ClassTableHeaders.NAME);
				CRUDList crlCriteria = (CRUDList) formMap.get(ClassTableHeaders.CRITERIA);
				
				tfCode.setText(code);
				tfName.setText(name);
				crlCriteria.clearList();
				criteria.stream().forEach((item) -> crlCriteria.addItem(item));
			}
		} else if (source == btnSearch) {
			filterTable();
		} else if (source == btnClearCriteria) {
			resetFilterCriteria();
			updateTable();
		}

	}
	
	@Override
	public void pullFromDB() {
		copyToOriginalClass = new HashMap<>();
		originalToCopyClass = new HashMap<>();
		copyToOriginalCriterion = new HashMap<>();
		originalToCopyCriterion = new HashMap<>();
		classes = new LinkedList<>();
		
		UniDB db = InternalData.DATABASE;
		
		List<UniClass> dbClasses = null;
		
		synchronized (db) {
			dbClasses = db.getClassList();
		}
		
		synchronized (dbClasses) {
			for (UniClass clOrig: dbClasses) {
				UniClass clCopy = clOrig.deepCopy();
				
				classes.add(clCopy);
				copyToOriginalClass.put(clCopy, clOrig);
				originalToCopyClass.put(clOrig, clCopy);
				
				// Kryteria
				List<ClassCriterion> critListOrig = clOrig.getCriteriaList();
				List<ClassCriterion> critListCopy = clCopy.getCriteriaList();
				int criterionCount = critListOrig.size();
				for (int critIdx = 0; critIdx < criterionCount; ++critIdx) {
					ClassCriterion critOrig = critListOrig.get(critIdx);
					ClassCriterion critCopy = critListCopy.get(critIdx);
					
					copyToOriginalCriterion.put(critCopy, critOrig);
					originalToCopyCriterion.put(critOrig, critCopy);
				}
			}
		}
	}
	
	@Override
	public void merge() {
		List<UniClass> tempClasses = new ArrayList<>();
		
		for (UniClass clCopy: classes) {
			if (copyToOriginalClass.containsKey(clCopy)) {
				UniClass clOrig = copyToOriginalClass.get(clCopy);
				clOrig.setClassName(clCopy.getClassName());
				
				// Kryteria
				@SuppressWarnings("unused")
				List<ClassCriterion> critListOrig = clOrig.getCriteriaList();
				List<ClassCriterion> critListCopy = clCopy.getCriteriaList();
				List<ClassCriterion> newCritList = new LinkedList<>();
				
				for (ClassCriterion critCopy: critListCopy) {
					if (copyToOriginalCriterion.containsKey(critCopy)) {
						ClassCriterion critOrig = copyToOriginalCriterion.get(critCopy);
						critOrig.setCriterionName(critCopy.getCriterionName());
						critOrig.setMaxPoints(critCopy.getMaxPoints());
						newCritList.add(critOrig);
					} else newCritList.add(critCopy);
				}
				
				clOrig.setCriteriaList(newCritList);
				
				tempClasses.add(clOrig);
			} else tempClasses.add(clCopy);
		}
		
		classes = tempClasses;
	}

	@Override
	public void pushToDB() {
		merge();
		
		List<UniClass> classesToDB = new ArrayList<UniClass>(classes);
		
		Thread tClasses = new Thread(() -> {
			UniDB db = InternalData.DATABASE;
			synchronized(db) {
				db.updateClasses(classesToDB);
			}
		});
		
		InternalData.EXECUTOR.execute(tClasses);
		
		try {
			tClasses.join();
			unsavedChanges = false;
			update();
		} catch (InterruptedException ex) {
			StringBuilder sb = new StringBuilder("Something went wrong saving changes to the database!");
			sb.append('\n').append(ex.getMessage());
			MessageBoxes.showErrorBox("Error!", sb.toString());
		}
	}

	@Override
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	@Override
	public void nullifyChanges() {
		unsavedChanges = false;
		update();

	}

	@Override
	public void update() {
		pullFromDB();
		updateTable();
	}

	

}
