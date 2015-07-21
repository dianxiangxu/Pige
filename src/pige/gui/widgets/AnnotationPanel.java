package pige.gui.widgets;

import pige.dataLayer.GraphAnnotationNote;
import pigelocales.PigeLocales;

/**
 * @author  Pere Bonet
 */
public class AnnotationPanel extends javax.swing.JPanel {
   
   private GraphAnnotationNote annotation;
   
   
   /**
    * Creates new form ParameterPanel
    */
   public AnnotationPanel(GraphAnnotationNote _annotation) {
      annotation = _annotation;
      initComponents();
      textArea.setText(annotation.getText());
   }
   
   private void initComponents() {
	      java.awt.GridBagConstraints gridBagConstraints;

	      panel = new javax.swing.JPanel();
	      textArea = new javax.swing.JTextArea(10, 100);
	      buttonPanel = new javax.swing.JPanel();
	      okButton = new javax.swing.JButton();
	      cancelButton = new javax.swing.JButton();

	      setLayout(new java.awt.BorderLayout());

	      setMaximumSize(new java.awt.Dimension(239, 208));
	      setMinimumSize(new java.awt.Dimension(239, 208));
	      panel.setLayout(new java.awt.GridLayout(1, 0));

	      panel.setBorder(javax.swing.BorderFactory.createTitledBorder(PigeLocales.bundleString("Edit Annotation")));
	      textArea.setLineWrap(true);
	      textArea.setWrapStyleWord(true);

	      panel.add(new javax.swing.JScrollPane(textArea));

	      add(panel, java.awt.BorderLayout.CENTER);

	      buttonPanel.setLayout(new java.awt.GridBagLayout());

	      okButton.setText(PigeLocales.bundleString("OK"));
	      okButton.setMaximumSize(new java.awt.Dimension(75, 25));
	      okButton.setMinimumSize(new java.awt.Dimension(75, 25));
	      okButton.setPreferredSize(new java.awt.Dimension(75, 25));
	      okButton.addActionListener(new java.awt.event.ActionListener() {
	         public void actionPerformed(java.awt.event.ActionEvent evt) {
	            okButtonActionPerformed(evt);
	         }
	      });

	      gridBagConstraints = new java.awt.GridBagConstraints();
	      gridBagConstraints.gridx = 6;
	      gridBagConstraints.gridy = 0;
	      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
	      gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
	      buttonPanel.add(okButton, gridBagConstraints);

	      cancelButton.setText(PigeLocales.bundleString("Cancel"));
	      cancelButton.addActionListener(new java.awt.event.ActionListener() {
	         public void actionPerformed(java.awt.event.ActionEvent evt) {
	            cancelButtonActionPerformed(evt);
	         }
	      });

	      gridBagConstraints = new java.awt.GridBagConstraints();
	      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
	      gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
	      buttonPanel.add(cancelButton, gridBagConstraints);

	      add(buttonPanel, java.awt.BorderLayout.SOUTH);

	   }

   private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
      exit();
   }//GEN-LAST:event_cancelButtonActionPerformed

   
   private void focusGained(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
      textField.moveCaretPosition(textField.getText().length());
   }
   
   private void focusLost(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
   }   
   
   private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
      annotation.setText(textArea.getText());
      annotation.repaint();
      exit();   
   }//GEN-LAST:event_okButtonActionPerformed
   
   
   private void exit() {
      //Provisional!
      this.getParent().getParent().getParent().getParent().setVisible(false);
   }      
   
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JPanel buttonPanel;
   private javax.swing.JButton cancelButton;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JButton okButton;
   private javax.swing.JPanel panel;
   private javax.swing.JTextArea textArea;
   // End of variables declaration//GEN-END:variables
   
}
