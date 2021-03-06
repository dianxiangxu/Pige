package pige.gui.widgets;

import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;

import pige.dataLayer.GraphDataLayerInterface;
import pige.dataLayer.GraphNode;
import pige.gui.CreateGraphGui;
import pige.gui.GuiView;
import pigelocales.PigeLocales;

/**
 *
 * @author  pere
 */
public class GraphNodeEditorPanel 
        extends javax.swing.JPanel {
   
 	private static final long serialVersionUID = 1L;
   
	private GraphNode graphNode;
	private String name;	
	private String siblingRelationString;
	private GraphDataLayerInterface xmlData;
	private GuiView view;
	private JRootPane rootPane;
   
	public GraphNodeEditorPanel(JRootPane _rootPane, GraphNode _graphNode, 
           GraphDataLayerInterface _pnmlData, GuiView _view) {
      graphNode = _graphNode;
      xmlData = _pnmlData;
      view = _view;
      name = graphNode.getName();
      siblingRelationString = graphNode.getSiblingRelationString();

      rootPane = _rootPane;
      initComponents();
      rootPane.setDefaultButton(okButton);

   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   private void initComponents() {
	   java.awt.GridBagConstraints gridBagConstraints;
		
      graphNodeEditorPanel = new javax.swing.JPanel();
//      nameLabel = new javax.swing.JLabel();
      nameTextArea = new javax.swing.JTextArea(5,40);
      
//      tokensLabel = new javax.swing.JLabel();
//      tokensTextArea = new javax.swing.JTextArea(3, 40);
      
      buttonPanel = new javax.swing.JPanel();
      okButton = new javax.swing.JButton();
      cancelButton = new javax.swing.JButton();

      setLayout(new java.awt.GridBagLayout());

      graphNodeEditorPanel.setLayout(new java.awt.GridBagLayout());

      graphNodeEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(PigeLocales.bundleString("Edit Node")));

      nameLabel= new JLabel(PigeLocales.bundleString(CreateGraphGui.graphType.getNodeTitle()+" Name")+":");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      graphNodeEditorPanel.add(nameLabel, gridBagConstraints);

      nameTextArea.setText(graphNode.getName());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      graphNodeEditorPanel.add(new JScrollPane(nameTextArea), gridBagConstraints);
if (CreateGraphGui.graphType.hasNodeProperty()){
     siblingRelationLabel= new JLabel(PigeLocales.bundleString("Sibling Relation")+":");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      graphNodeEditorPanel.add(siblingRelationLabel, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      graphNodeEditorPanel.add(createSiblingRelationPanel(), gridBagConstraints);
}      
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
      add(graphNodeEditorPanel, gridBagConstraints);

      buttonPanel.setLayout(new java.awt.GridBagLayout());

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
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 9);
      buttonPanel.add(okButton, gridBagConstraints);

      cancelButton.setText(PigeLocales.bundleString("Cancel"));
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelButtonHandler(evt);
         }
      });

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints.insets = new java.awt.Insets(8, 0, 8, 10);
      buttonPanel.add(cancelButton, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      add(buttonPanel, gridBagConstraints);

   }// </editor-fold>//GEN-END:initComponents

	private JPanel createSiblingRelationPanel() {
		andButton = new JRadioButton("AND");
		andButton.setSelected(siblingRelationString.equals("AND"));

		orButton = new JRadioButton("OR");
		orButton.setSelected(siblingRelationString.equals("OR"));

		priorityAndButton = new JRadioButton("Priority AND");
		priorityAndButton.setSelected(siblingRelationString.equals("Priority AND"));

		ButtonGroup group = new ButtonGroup();
		group.add(andButton);
		group.add(orButton);
		group.add(priorityAndButton);

		JPanel panel = new JPanel();
		panel.add(priorityAndButton);
		panel.add(orButton);
		panel.add(andButton);

		return panel;
	}

   ChangeListener changeListener = new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
         JSpinner spinner = (JSpinner)evt.getSource();
         JSpinner.NumberEditor numberEditor =
                 ((JSpinner.NumberEditor)spinner.getEditor());
         numberEditor.getTextField().setBackground(new Color(255,255,255));
         spinner.removeChangeListener(this);
      }
   };   
   
   private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_okButtonKeyPressed
      if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
         doOK();
      }
   }//GEN-LAST:event_okButtonKeyPressed

   private void doOK(){

      view.getUndoManager().newEdit(); // new "transaction""
      
      String newName = nameTextArea.getText();
      if (!newName.equals(name)){
         if (xmlData.checkGraphNodeIDAvailability(newName)){
            view.getUndoManager().addEdit(graphNode.setGraphObjectName(newName));
         } else{
            // aquest nom no est� disponible...
            JOptionPane.showMessageDialog(null,
                    PigeLocales.bundleString("Duplicate node name")+": " + newName+".", PigeLocales.bundleString("Error"),
                                JOptionPane.WARNING_MESSAGE);
            return;
         }
      }
      if (CreateGraphGui.graphType.hasNodeProperty()){
    	  if (andButton.isSelected())
    		  graphNode.setSiblingRelation(GraphNode.SiblingRelation.AND);
    	  else if (orButton.isSelected())
    		  graphNode.setSiblingRelation(GraphNode.SiblingRelation.OR);
    	  else
    		  graphNode.setSiblingRelation(GraphNode.SiblingRelation.PRIORITYAND);
      }
      graphNode.update();
      graphNode.repaint();
      exit();
   }
   
   
   private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler
      doOK();
   }//GEN-LAST:event_okButtonHandler

   
   private void exit() {
      rootPane.getParent().setVisible(false);
   }
   
   
   private void cancelButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonHandler
      exit();
   }//GEN-LAST:event_cancelButtonHandler

   
   private javax.swing.JPanel buttonPanel;
   private javax.swing.JButton cancelButton;

   private javax.swing.JLabel nameLabel;
   private javax.swing.JTextArea nameTextArea;
   private javax.swing.JButton okButton;
   private javax.swing.JPanel graphNodeEditorPanel;

   private javax.swing.JLabel siblingRelationLabel;
   private javax.swing.JRadioButton andButton;
   private javax.swing.JRadioButton orButton;
   private javax.swing.JRadioButton priorityAndButton;

}
