package pige.gui.widgets;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;

import pige.dataLayer.GraphArc;
import pige.dataLayer.GraphDataLayerInterface;
import pige.gui.CreateGraphGui;
import pige.gui.GuiView;
import pigelocales.PigeLocales;

public class ArcPropertyEditorPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;
	private GraphArc arc;
	
	private String name;
	private String precond;
	private String postcond; 
	
	private GuiView view;
	JRootPane rootPane;

	public ArcPropertyEditorPanel(JRootPane _rootPane, GraphArc _arc,
			GraphDataLayerInterface _pnmlData, GuiView _view) {
		arc = _arc;
		view = _view;
		name = arc.getName();
		precond = arc.getPrecondition();
		postcond = arc.getPostcondition();
		rootPane = _rootPane;

		initComponents();

		rootPane.setDefaultButton(okButton);
	}

	private void initComponents() {

		java.awt.GridBagConstraints gridBagConstraints;

		arcEditorPanel = new javax.swing.JPanel();
		buttonPanel = new javax.swing.JPanel();
		cancelButton = new javax.swing.JButton();
		okButton = new javax.swing.JButton();

		nameTextField = new javax.swing.JTextField(name,40);
		precondTextField = new javax.swing.JTextField(precond, 40);
		postcondTextField = new javax.swing.JTextField(postcond, 40);
		
		setLayout(new java.awt.GridBagLayout());

		arcEditorPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(PigeLocales.bundleString("Edit "+CreateGraphGui.graphType.getArcTitle())));
		arcEditorPanel.setLayout(new java.awt.GridBagLayout());

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		arcEditorPanel.add(new JLabel(PigeLocales.bundleString("Event name")+" "), gridBagConstraints);
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		arcEditorPanel.add(nameTextField, gridBagConstraints);
		
		nameTextField
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusGained(
							java.awt.event.FocusEvent evt) {
						nameTextFieldFocusGained(evt);
					}

					public void focusLost(java.awt.event.FocusEvent evt) {
						nameTextFieldFocusLost(evt);
					}
				});		

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		arcEditorPanel.add(new JLabel(PigeLocales.bundleString("Precondition")+" "), gridBagConstraints);
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		arcEditorPanel.add(precondTextField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		arcEditorPanel.add(new JLabel(PigeLocales.bundleString("Postcondition")+" "), gridBagConstraints);
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		arcEditorPanel.add(postcondTextField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		add(arcEditorPanel, gridBagConstraints);
		buttonPanel.setLayout(new java.awt.GridBagLayout());

		cancelButton.setText(PigeLocales.bundleString("Cancel"));
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonHandler(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		buttonPanel.add(cancelButton, gridBagConstraints);

		okButton.setText(PigeLocales.bundleString("OK"));
		okButton.setMaximumSize(new java.awt.Dimension(75, 25));
		okButton.setMinimumSize(new java.awt.Dimension(75, 25));
		okButton.setPreferredSize(new java.awt.Dimension(75, 25));
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonHandler(evt);
			}
		});
		okButton.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				okButtonKeyPressed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		buttonPanel.add(okButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 8, 3);
		add(buttonPanel, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	
	private void nameTextFieldFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_nameTextFieldFocusLost
		// focusLost(nameTextField);
	}// GEN-LAST:event_nameTextFieldFocusLost

	private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_nameTextFieldFocusGained
		// focusGained(nameTextField);
	}// GEN-LAST:event_nameTextFieldFocusGained

	private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_okButtonKeyPressed
		if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
			okButtonHandler(new java.awt.event.ActionEvent(this, 0, ""));
		}
	}// GEN-LAST:event_okButtonKeyPressed

	CaretListener caretListener = new javax.swing.event.CaretListener() {
		public void caretUpdate(javax.swing.event.CaretEvent evt) {
			JTextField textField = (JTextField) evt.getSource();
			textField.setBackground(new Color(255, 255, 255));
			// textField.removeChangeListener(this);
		}
	};

	private void okButtonHandler(java.awt.event.ActionEvent evt) {
	    view.getUndoManager().newEdit(); // new "transaction""
		String newName = nameTextField.getText();
		String newPrecond = precondTextField.getText();
		String newPostcond = postcondTextField.getText();
		
		if (!newName.equals(name) || !newPrecond.equals(precond) || !newPostcond.equals(postcond)){
		   view.getUndoManager().addEdit(arc.setArcProperties(newName, newPrecond, newPostcond));
		}
		arc.update();
		arc.getSource().update();
		exit();
	}

	private void exit() {
		rootPane.getParent().setVisible(false);
	}

	private void cancelButtonHandler(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonHandler
		exit();
	}// GEN-LAST:event_cancelButtonHandler

	private javax.swing.JPanel buttonPanel;
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton okButton;
	private javax.swing.JPanel arcEditorPanel;
	
	private javax.swing.JTextField nameTextField;
	private javax.swing.JTextField precondTextField;
	private javax.swing.JTextField postcondTextField;


}
