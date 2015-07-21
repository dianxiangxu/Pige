
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import pige.dataLayer.GraphType;
import pige.gui.CreateGraphGui;
import pige.gui.GraphPanel;

/**
 * Pige - Platform Independent Graph Editor
 * created from Pipe3.0 - Platform Independent Petri net Editor
 * http://pipe2.sourceforge.net/
 * 
 * @author Dianxiang Xu
 *
 */

public class PigeDemo extends JFrame {

	public static File file = new File("tree.xml");

   public PigeDemo(){
		super("Pige - Platform Independent Graph Editor");
		GraphPanel graphPanel = CreateGraphGui.createGraphPanel(this,file, true,
				GraphType.FiniteStateMachine);
//		GraphPanel graphPanel = CreateGraphGui.createGraphPanel(this,file, true,
//				GraphType.ThreatTree);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(graphPanel.getPaletteToolBar(), BorderLayout.NORTH);
		panel.add(graphPanel, BorderLayout.CENTER);
		setContentPane(panel);
   }
   
	public static void setLookAndFeel() {
		if (System.getProperty("os.name").contains("Windows")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
			}
		}
	}

    private static void startGUI() {
    	setLookAndFeel();
		PigeDemo window = new PigeDemo();
		window.setPreferredSize(new Dimension(800, 500));
		window.pack();
		window.setVisible(true);
   }

   public static void main(String args[]) {
      SwingUtilities.invokeLater(new Runnable() {

         public void run() {
        	 startGUI();
         }

      });
   }

}
