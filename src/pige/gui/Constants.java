package pige.gui;
import java.awt.Color;

public class Constants {
   
   public static final String TOOL = "PIGE";
   public static final String VERSION = "2.5";
   
   public static final String PROPERTY_FILE_EXTENSION = ".properties";
   public static final String PROPERTY_FILE_DESC = "PIGE Properties file";
   public static final String CLASS_FILE_EXTENSION = ".class";
   public static final String CLASS_FILE_DESC = "Java Class File";   
      
   public static final int GRAPHNODE        = 105;
   public static final int GRAPHANNOTATION   = 109;
   public static final int SELECT       = 110;
   public static final int DELETE       = 111;
   public static final int GRAPHARC          = 112;
   public static final int GRID         = 113;
      
   public static final int DRAW         = 115;
   
   public static final int DRAG         = 120;
   
   
   public static final int FAST_PLACE      = 150;
   public static final int FAST_TRANSITION = 151;
            
   // Special:  Parsing in a XML file - creating components
   public static final int CREATING     = 200;   
      
   public static final int GRAPHNODE_HEIGHT=30;
   
   public static final Color ELEMENT_LINE_COLOUR = Color.BLACK;
   public static final Color ELEMENT_FILL_COLOUR = Color.WHITE;
   public static final Color SELECTION_LINE_COLOUR = new Color(0,0,192);
   public static final Color SELECTION_FILL_COLOUR = new Color(192,192,255);
   
   // For ArcPath:
   public static final int ARC_CONTROL_POINT_CONSTANT = 3;
   public static final int ARC_PATH_SELECTION_WIDTH = 6;
   public static final int ARC_PATH_PROXIMITY_WIDTH = 10;
   
   // For Snap-To behaviour:
   public static int GRAPHNODE_PROXIMITY_RADIUS = 25;
   public static final int MIN_SELFARC_POINTS = 4;
   
   // Object layer positions for GuiView:
   public static final int WHITE_LAYER_OFFSET = 80;
   public static final int ARC_POINT_LAYER_OFFSET = 50;
   public static final int ARC_LAYER_OFFSET = 20;
   public static final int GRAPHNODE_LAYER_OFFSET = 30;
   public static final int NOTE_LAYER_OFFSET = 10;
   public static final int SELECTION_LAYER_OFFSET = 90;
   public static final int LOWEST_LAYER_OFFSET = 0;
   public static final int ANNOTATION_LAYER_OFFSET = 10;
   
   // For AnnotationNote appearance:
   public static final int RESERVED_BORDER = 12;
   public static final int ANNOTATION_SIZE_OFFSET = 4;
   public static final int ANNOTATION_MIN_WIDTH = 40;
   public static final Color NOTE_DISABLED_COLOUR = Color.BLACK;
   public static final Color NOTE_EDITING_COLOUR = Color.BLACK;
   public static final Color RESIZE_POINT_DOWN_COLOUR = new Color(220,220,255);
   public static final String ANNOTATION_DEFAULT_FONT = "Helvetica";
   public static final int ANNOTATION_DEFAULT_FONT_SIZE = 12;
   
   
   public static final String LABEL_FONT = "Dialog";
   public static final int    LABEL_DEFAULT_FONT_SIZE = 11;
   
   
   public static final int DEFAULT_OFFSET_X = -5;
   public static final int DEFAULT_OFFSET_Y = 35;
   
      /** X-Axis Scale Value */
   public static final int DISPLAY_SCALE_FACTORX = 7; 
   /** Y-Axis Scale Value */
   public static final int DISPLAY_SCALE_FACTORY = 7; 
   /** X-Axis Shift Value */
   public static final int DISPLAY_SHIFT_FACTORX = 270; 
   /** Y-Axis Shift Value */
   public static final int DISPLAY_SHIFT_FACTORY = 120; 
   
   
   public static final int NAMELABEL_OFFSET = 12;

   public static int DEFAULT_BUFFER_SIZE = 50;
   
   public static boolean JOIN_ARCS = false;

   public static final int ZOOM_DELTA   = 10;
   public static final int ZOOM_MAX     = 300;
   public static final int ZOOM_MIN     = 40;
   public static final int ZOOM_DEFAULT = 100;

   public static Color BACKGROUND_COLOR = new Color(255, 255, 255, 200);
//REVISAR-LIMITS
   public static final int MAX_NODES = 10000;
   // TODO: find a better value for MAX_NODES
   
   
}
