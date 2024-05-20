/**
 * 
 */
package pl.wit.studata.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.javatuples.Pair;

import pl.wit.studata.AppData;
import pl.wit.studata.gui.widgets.FormWidget;



/**
 * Klasa reprezentująca okno dialogowe tworzenia / aktualizowania kryterium dla przedmiotu.
 */
public class CriterionCreationDialog extends JDialog {
	private static final String TITLE = AppData.APP_TITLE.concat(" - Criterion Creator");
	
	private JTextField tfName = null;
	private JSpinner spnPoints = null;
	private JButton btnCreateUpdate = null;
	private JButton btnCancel = null;
	
	// [Dane]:
	private Pair<String, Integer> result = null;
	
	/**
	 * Konstruktor parametryczny przyjmujący referencję do okna powołującego.
	 * @param owner Referencja do okna powołującego okienko dialogowe.
	 */
	public CriterionCreationDialog(Window owner) {
		super(owner);
		setModal(true);
		setTitle(TITLE);
		setMinimumSize(new Dimension(300, 150));
		setResizable(false);
		
		// ----------
		setLayout(new BorderLayout(10, 0));
		
		FormWidget form = new FormWidget();
		
		tfName = new JTextField(20);
		form.addField("Name: ", tfName);
		
		spnPoints = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		form.addField("Points: ", spnPoints);
		
		add(form, BorderLayout.CENTER);
		
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				submit(false);
			}
		});
		
		
		btnCreateUpdate = new JButton("Create");
		btnCreateUpdate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				submit(true);
				
			}
		});
		
		pnlButtons.add(btnCancel, BorderLayout.LINE_START);
		pnlButtons.add(btnCreateUpdate, BorderLayout.LINE_END);
		
		add(pnlButtons, BorderLayout.PAGE_END);
	}
	
	private void submit(boolean shouldApply) {
		if (shouldApply) {
			String critName = tfName.getText();
			Integer maxPoints = (Integer) spnPoints.getValue();
			
			if (critName.isBlank()) {
				MessageBoxes.showErrorBox("Invalid Data!", "Criterion Name cannot be empty or blank!");
				return;
			}
			
			result = Pair.with(critName, maxPoints);
		}
		
		dispose();
	}

	public Pair<String, Integer> getResult() {
		return result;
	}
	
	public static Pair<String, Integer> showDialog(Window owner) {
		CriterionCreationDialog dialog = new CriterionCreationDialog(owner);
		dialog.setVisible(true);
		return dialog.getResult();
	}
	
	public static Pair<String, Integer> showDialog(Window owner, String name, Integer maxPoints) {
		CriterionCreationDialog dialog = new CriterionCreationDialog(owner);
		
		dialog.btnCreateUpdate.setText("Update");
		if (name != null) dialog.tfName.setText(name);
		if (maxPoints != null && maxPoints > 0) dialog.spnPoints.setValue(maxPoints);
		
		dialog.setVisible(true);
		return dialog.getResult();
	}
	
	
}
