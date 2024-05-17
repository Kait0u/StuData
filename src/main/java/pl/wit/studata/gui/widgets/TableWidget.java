/**
 * 
 */
package pl.wit.studata.gui.widgets;

import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Klasa poszerzająca możliwości zwykłego obiektu JTable.
 * @author Jakub Jaworski
 */
public class TableWidget extends JTable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Model tabeli.
	 */
	private DefaultTableModel model = null;

	/**
	 * Konstruktor parametryczny przyjmujący nagłówki kolumn oraz informacje o tym, czy komórki mają być edytowalne, czy nie.
	 * @param headers Tablica nagłówków
	 * @param editable Czy komórki mają dać się edytować.
	 */
	public TableWidget(String[] headers, boolean editable) {
		model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return editable;
			}
		};
		
		if (headers != null)
			for (String headerName: headers)
				model.addColumn(headerName);
		
		setModel(model);
	}
	
	/**
	 * Konstruktor parametryczny przyjmujący nagłówki kolumn. Komórki będą nieedytowalne przez użytkownika GUI.
	 * @param headers Tablica nagłówków
	 */
	public TableWidget(String[] headers) {
		this(headers, false);
	}
	
	/**
	 * Usuwa wszystkie elementy z tabeli.
	 */
	public void clear() {
		model.setRowCount(0);
	}
	
	/**
	 * Dodaje nowy wiersz do tabeli korzystając z danych przekazanych. Jeśli szerokość tabeli jest mniejsza niż danych, wówczas wpisane są tylko te dane, które się mieszczą. Jeżeli szerokość tabeli jest większa niż danego wiersza danych, wpisane jest tylko tyle, ile podano - reszta komórek pozostanie pusta.
	 * @param dataRow Wiersz do dopisania.
	 */
	public void addDataRow(Object[] dataRow) {
		if (dataRow == null) return;
		
		int iMax = Math.min(getColumnCount(), dataRow.length);
		dataRow = Arrays.copyOf(dataRow, iMax);
		
		model.addRow(dataRow);
	}
	
	/**
	 * Metoda dodająca wiele wierszy jednocześnie.
	 * @param dataRows Tablica 2D składająca się z wierszy, które składają się z kolumn.
	 */
	public void addData(Object[][] dataRows) {
		if (dataRows == null) return;
		
		for (Object[] row: dataRows) {
			addDataRow(row);
		}
	}
	
	/**
	 * Kasuje wiersz o podanym ineksie z tabeli.
	 * @param rowIdx Indeks wiersza do skasowania.
	 */
	public void deleteRow(int rowIdx) {
		model.removeRow(rowIdx);
	}
	
	/**
	 * Kasuje wiele wierszy w ramach jednej operacji.
	 * @param rowsToDelete Lista indeksów wierszy do usunięcia.
	 */
	public void deleteMultipleRows(List<Integer> rowsToDelete) {
		if (rowsToDelete == null) return;
		
		// Offset służy do rozwiązania problemu, w którym po usunięciu pojedynczego wiersza zmienia się numeracja wierszy pod nim.
		int offset = 0;
		for (int rowIdx: rowsToDelete) {
			deleteRow(rowIdx - offset);
			++offset;
		}
	}

	/**
	 * Dodaje wiersz do tabeli w miejsce starego korzystając z danych przekazanych. Jeśli szerokość tabeli jest mniejsza niż danych, wówczas wpisane są tylko te dane, które się mieszczą. Jeżeli szerokość tabeli jest większa niż danego wiersza danych, wpisane jest tylko tyle, ile podano - reszta komórek pozostanie pusta.
	 * @param rowIdx Indeks wiersza
	 * @param dataRow Tablica danych
	 */
	public void updateRow(int rowIdx, Object[] dataRow) {
		if (dataRow == null) return;
		
		int iMax = Math.min(getColumnCount(), dataRow.length);
		for (int i = 0; i < iMax; ++i) {
			model.setValueAt(dataRow[i], rowIdx, i);
		}
	}
	
	/**
	 * Pobiera wiersz z tabeli o danym indeksie.
	 * @param rowIdx Nr indeksu wiersza
	 * @return Wiersz jako tablica obiektów, lub null jeśli wiersz nie istnieje.
	 */
	public Object[] getRow(int rowIdx) {
		if (rowIdx < 0 || rowIdx >= getRowCount()) 
			return null;
		
		int colCount = getColumnCount();
		Object[] result = new Object[colCount];
		
		for (int i = 0; i < colCount; ++i) {
			result[i] = getValueAt(rowIdx, i);
		}
		
		return result;
	}
	
	/**
	 * Metoda poszukująca wiersza według zadanej wartości poszukiwanej i indeksu kolumny.
	 * @param value Wartość poszukiwana
	 * @param colIdx Indeks kolumny
	 * @return Indeks znalezionego wiersza lub -1 przy niepowodzeniu.
	 */
	public int findRowByCellValue(Object value, int colIdx) {
		if (value == null) return -1;
		
		int result = -1;
		
		int rowCount = getRowCount();
		for (int rowIdx = 0; rowIdx < rowCount; ++rowIdx) {
			Object foundVal = getValueAt(rowIdx, colIdx);
			if (value.equals(foundVal)) {
				result = rowIdx;
				break;
			}
		}
		
		return result;
	}
}
