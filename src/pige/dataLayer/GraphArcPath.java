/**
 * Created on 12-Feb-2004
 */
package pige.dataLayer;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;

import pige.gui.Constants;
import pige.gui.GuiView;
import pige.gui.ZoomController;
import pige.gui.handler.ArcPathPointHandler;
import pige.gui.undo.AddArcPathPointEdit;
import pige.gui.undo.UndoableEdit;


/**
 * @author Peter Kyme, Tom Barnwell and Michael Camacho
 *
 * @author Edwin Chung 16 Mar 2007: modified the constructor and several other 
 * functions so that DataLayer objects can be created outside the GUI
 *
 * @author Pere Bonet: modified the weight label placement
 */
public class GraphArcPath 
        implements Shape, Cloneable {
   
   private GeneralPath path = new GeneralPath();
   private List<GraphArcPathPoint> pathPoints = new ArrayList<GraphArcPathPoint>();
   private GraphArc myArc;
   protected GraphArcPathPoint currentPoint;
   private boolean pointLock = false;
   private static Stroke proximityStroke = 
            new BasicStroke(Constants.ARC_PATH_PROXIMITY_WIDTH/*, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER*/);
   private static Stroke stroke = new BasicStroke(Constants.ARC_PATH_SELECTION_WIDTH);
   private Shape shape, proximityShape;
   private boolean showControlPoints = false;
   
   public Point2D.Float midPoint  = new Point2D.Float();
   
   
   public GraphArcPath(GraphArc a) {
      myArc = a;
   }
   

   public GraphArcPathPoint getArcPathPoint(int i) {
      return (GraphArcPathPoint)(pathPoints.get(i));
   }
   
   
   public void createPath() {
      setControlPoints();
      
      path = new GeneralPath();
      currentPoint = (GraphArcPathPoint)pathPoints.get(0);
      path.moveTo(currentPoint.getPoint().x, currentPoint.getPoint().y);
      
      currentPoint.setPointType(GraphArcPathPoint.STRAIGHT);
      
      double length = 0;
      for (int c = 1; c <= getEndIndex(); c++) {
         GraphArcPathPoint previousPoint = currentPoint;
         currentPoint = (GraphArcPathPoint)pathPoints.get(c);
         
         if (currentPoint.getPointType() == GraphArcPathPoint.STRAIGHT) {
            path.lineTo(currentPoint.getPoint().x, currentPoint.getPoint().y);
         } else if (currentPoint.getPointType() == GraphArcPathPoint.CURVED) {
            if (showControlPoints) {
               //draw control lines for illustrative purposes
               path.lineTo(currentPoint.getControl1().x,
                           currentPoint.getControl1().y);
               path.lineTo(currentPoint.getControl2().x,
                           currentPoint.getControl2().y);
               path.lineTo(currentPoint.getPoint().x,
                           currentPoint.getPoint().y);
               path.moveTo(previousPoint.getPoint().x, 
                           previousPoint.getPoint().y);
            }
            path.curveTo(currentPoint.getControl1().x,
                         currentPoint.getControl1().y,
                         currentPoint.getControl2().x,
                         currentPoint.getControl2().y,
                         currentPoint.getPoint().x,
                         currentPoint.getPoint().y);
         }
         length += getMod(currentPoint.getPoint(), previousPoint.getPoint());
      }

      length /= 2;
      int c = 0;
      currentPoint = (GraphArcPathPoint)pathPoints.get(c++);
      
      if (getEndIndex() < 2) {
         midPoint.x = (float)((((GraphArcPathPoint)pathPoints.get(0)).getPoint().x 
                       + ((GraphArcPathPoint)pathPoints.get(1)).getPoint().x)*0.5);
         midPoint.y = (float)((((GraphArcPathPoint)pathPoints.get(0)).getPoint().y 
                       + ((GraphArcPathPoint)pathPoints.get(1)).getPoint().y)*0.5);
      } else {
         double acc = 0;
         double percent = 0;
         for (c = 1; c <= getEndIndex(); c++) {
            GraphArcPathPoint previousPoint = currentPoint;
            currentPoint = (GraphArcPathPoint)pathPoints.get(c);

            double inc = getMod(currentPoint.getPoint(), previousPoint.getPoint());
            if ((acc + inc > length)) {
               percent = (length - acc)/inc;
               break; 
            }
            acc += inc;
         }

         GraphArcPathPoint previousPoint = (GraphArcPathPoint)pathPoints.get(c-1);
         midPoint.x = previousPoint.getPoint().x + 
                 (float)((currentPoint.getPoint().x - 
                          previousPoint.getPoint().x) * percent);
         midPoint.y = previousPoint.getPoint().y + 
                 (float)((currentPoint.getPoint().y -
                          previousPoint.getPoint().y) * percent);
      }

      shape = stroke.createStrokedShape(this);
      getArc().setArcLabelPosition();
      proximityShape = proximityStroke.createStrokedShape(this);
   }
   
   
   private void setControlPoints() {
      setCurveControlPoints(); //must be in this order
      setStraightControlPoints();
      setEndControlPoints();
   }
   
   
   /* returns a control point for curve CD with incoming vector AB*/
   private Point2D.Float getControlPoint(Point2D.Float A, Point2D.Float B, 
            Point2D.Float C, Point2D.Float D) {
      Point2D.Float p = new Point2D.Float(0, 0);
      
      double modAB = getMod(A, B);
      double modCD = getMod(C, D);
      
      double ABx = (B.x - A.x) / modAB;
      double ABy = (B.y - A.y) / modAB;
      
      if (modAB < 7) {
         // hack, stops division by zero, modAB can only be this low if the 
         // points are virtually superimposed anyway
         p = (Point2D.Float)C.clone();
      } else {
         p.x = C.x + (float) (ABx * modCD / Constants.ARC_CONTROL_POINT_CONSTANT);
         p.y = C.y + (float) (ABy * modCD / Constants.ARC_CONTROL_POINT_CONSTANT);
      }
      return p;
   }
   
   
   private double getMod(Point2D.Float A, Point2D.Float B) {
      double ABx = A.x - B.x;
      double ABy = A.y - B.y;
      
      return Math.sqrt(ABx * ABx + ABy * ABy);
   }
   
   
   /* function sets control points for any curved sections of the path */
   private void setCurveControlPoints() {
      if (pathPoints.size() < 1) {
         return;
      }
      GraphArcPathPoint myCurrentPoint = (GraphArcPathPoint)pathPoints.get(0);
      myCurrentPoint.setPointType(GraphArcPathPoint.STRAIGHT);
      
      Cubic[] X,Y;
      
      int endIndex = getEndIndex();
      
      for (int c = 1; c <= endIndex;) {
         int curveStartIndex = 0;
         int curveEndIndex = 0;
         myCurrentPoint = (GraphArcPathPoint)pathPoints.get(c);
         
         if (myCurrentPoint.getPointType() == true) {
            curveStartIndex = c-1;
            
            for(; c <= endIndex && myCurrentPoint.getPointType() == true; c++) {
               myCurrentPoint = (GraphArcPathPoint)pathPoints.get(c);
               curveEndIndex = c;
            }
            /* calculate a cubic for each section of the curve */
            int lengthOfCurve = curveEndIndex - curveStartIndex;
            int k1;
            int x[] = new int[lengthOfCurve + 2];
            int y[] = new int[lengthOfCurve + 2];
            X = new Cubic[lengthOfCurve + 2];
            Y = new Cubic[lengthOfCurve + 2];
            
            for (k1= 0; k1 <= (curveEndIndex - curveStartIndex); k1++) {
               x[k1] = (int)((GraphArcPathPoint)pathPoints.get(curveStartIndex + k1)).getPoint().x;
               y[k1] = (int)((GraphArcPathPoint)pathPoints.get(curveStartIndex + k1)).getPoint().y;
            }
            x[k1] = x[k1-1];
            y[k1] = y[k1-1];
            
            X = calcNaturalCubic(k1,x);
            Y = calcNaturalCubic(k1,y);
            
            for (int k2 = 1; k2 <= lengthOfCurve; k2++) {
               myCurrentPoint = (GraphArcPathPoint)pathPoints.get(k2 + curveStartIndex);
               myCurrentPoint.setControl1(X[k2-1].getX1(),Y[k2-1].getX1());
               myCurrentPoint.setControl2(X[k2-1].getX2(),Y[k2-1].getX2());               
            }            
         } else {
            c++;
         }
      }
   }
   
   
   /* fuction sets the control points for any straight sections and for smooth
    * intersection between straight and curved sections */
   private void setStraightControlPoints() {
      
      GraphArcPathPoint myCurrentPoint = (GraphArcPathPoint)pathPoints.get(0);
      GraphArcPathPoint myPreviousButOnePoint = null;
      GraphArcPathPoint myNextPoint = null;
      GraphArcPathPoint myPreviousPoint = null;
      
      for (int c = 1; c <= getEndIndex(); c++) {
         myPreviousPoint = (GraphArcPathPoint)pathPoints.get(c-1);
         myCurrentPoint = (GraphArcPathPoint)pathPoints.get(c);
         
         if (myCurrentPoint.getPointType() == false) {
            myCurrentPoint.setControl1(getControlPoint(myPreviousPoint.getPoint(),
                     myCurrentPoint.getPoint(),
                     myPreviousPoint.getPoint(),
                     myCurrentPoint.getPoint()));
            myCurrentPoint.setControl2(getControlPoint(myCurrentPoint.getPoint(),
                     myPreviousPoint.getPoint(),
                     myCurrentPoint.getPoint(),
                     myPreviousPoint.getPoint()));
         } else {
            if (c > 1 && myPreviousPoint.getPointType() == false) {
               myPreviousButOnePoint = (GraphArcPathPoint)pathPoints.get(c-2);
               myCurrentPoint.setControl1(getControlPoint(
                        myPreviousButOnePoint.getPoint(),
                        myPreviousPoint.getPoint(),
                        myPreviousPoint.getPoint(),
                        myCurrentPoint.getPoint()));
            }
            if (c < getEndIndex()) {
               myNextPoint = (GraphArcPathPoint)pathPoints.get(c+1);
               if (myNextPoint.getPointType() == false ) {
                  myCurrentPoint.setControl2(getControlPoint(myNextPoint.getPoint(),
                           myCurrentPoint.getPoint(),
                           myCurrentPoint.getPoint(),
                           myPreviousPoint.getPoint()));
               }
            }
         }
      }
   }
   
   
   private void setEndControlPoints() {
      GraphAbstractNode source = getArc().getSource();
      GraphAbstractNode target = getArc().getTarget();
//      double anAngle = Math.toRadians(transitionAngle);

    double anAngle = Math.toRadians(0);

      if  (!(getEndIndex() > 0)) {
         return;
      } else if (target != null && source instanceof GraphNode&&
               ((GraphArcPathPoint)pathPoints.get(getEndIndex())).getPointType() == true) {
         GraphArcPathPoint myPoint = (GraphArcPathPoint)pathPoints.get(getEndIndex());
         GraphArcPathPoint myLastPoint = (GraphArcPathPoint)pathPoints.get(getEndIndex()-1);
         float distance = (float)getMod(myPoint.getPoint(), myLastPoint.getPoint())
                          / Constants.ARC_CONTROL_POINT_CONSTANT;
         myPoint.setControl2(
                 (float)(myPoint.getPoint().x + Math.cos(anAngle)*distance),
                 (float)(myPoint.getPoint().y + Math.sin(anAngle)*distance));
         
         myPoint = (GraphArcPathPoint)pathPoints.get(1);
         myPoint.setControl1(getControlPoint(
                 ((GraphArcPathPoint)pathPoints.get(0)).getPoint(),
                 myPoint.getControl2(),
                 ((GraphArcPathPoint)pathPoints.get(0)).getPoint(),
                 myPoint.getControl2()));
      }
   }
   
   
   public void addPoint(double x, double y, boolean type) {
      pathPoints.add(new GraphArcPathPoint((float)x, (float)y, type, this));
   }
   

   public void addPoint() {
      pathPoints.add(new GraphArcPathPoint(this));
   }

   
   public void deletePoint(GraphArcPathPoint a) {
      pathPoints.remove(a);
   }
   
   
   public void updateArc() {
      myArc.updateArcPosition();
   }
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#contains(double, double)
    */
   public boolean contains(double arg0, double arg1) {
      return false;
   }
   
   
   public int getEndIndex() {
      return pathPoints.size()-1;
   }
   
   
   public void setPointLocation(int index, double x, double y) {
      if (index < pathPoints.size() && index >= 0) {
         ((GraphArcPathPoint)pathPoints.get(index)).setPointLocation((float)x,
                                                                (float)y);
      }
   }
   
   
   public void setPointType(int index, boolean type) {
      ((GraphArcPathPoint)pathPoints.get(index)).setPointType(type);
   }
   
   
   public void setFinalPointType(boolean type) {
      ((GraphArcPathPoint)pathPoints.get(getEndIndex())).setPointType(type);
   }
   
   
   public void togglePointType(int index) {
      ((GraphArcPathPoint)pathPoints.get(index)).togglePointType();
   }
   
   
   public boolean getPointType(int index) {
      return ((GraphArcPathPoint)pathPoints.get(index)).getPointType();
   }
   
   
   public void selectPoint(int index) {
      ((GraphArcPathPoint)pathPoints.get(index)).select();
   }
   
      
   public int getNumPoints() {
      return pathPoints.size();
   }
   
   
   public Point2D.Float getPoint(int index) {
      return ((GraphArcPathPoint)pathPoints.get(index)).getPoint();
   }
   
   
   public GraphArcPathPoint getPathPoint(int index) {
      return ((GraphArcPathPoint)pathPoints.get(index));
   }
   
   
   public GraphArc getArc() {
      return myArc;
   }
   
   
   public void showPoints() {
      if (!pointLock) {
         for (int i = 0; i < pathPoints.size(); i++) {
            ((GraphArcPathPoint)pathPoints.get(i)).setVisible(true);
         }
      }
   }
   
   
   public void hidePoints() {
      if (!pointLock) {
         for (int i = 0; i < pathPoints.size(); i++) {
            currentPoint = ((GraphArcPathPoint)pathPoints.get(i));
            if (!currentPoint.isSelected()) {
               currentPoint.setVisible(false);
            }
         }
      }
   }
   
   
   //
   public void forceHidePoints() {
      for (int i = 0; i < pathPoints.size(); i++) {
         ((GraphArcPathPoint)pathPoints.get(i)).hidePoint();
      }
   }   
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
    */
   public void setPointVisibilityLock(boolean lock) {
      pointLock = lock;
   }
      
   
   /* modified to use control points, ensures a curve hits a place tangetially */
   public double getEndAngle() {
      if (getEndIndex() > 0) {
            return ((GraphArcPathPoint)pathPoints.get(getEndIndex())).getAngle(((GraphArcPathPoint)(pathPoints.get(getEndIndex()))).getControl1());
      }
      return 0;
   }
   
   
   public double getStartAngle() {
      if (getEndIndex()>0) {
         return ((GraphArcPathPoint)pathPoints.get(0)).getAngle(((GraphArcPathPoint)(pathPoints.get(1))).getControl2());
      }
      return 0;
   }
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#contains(double, double, double, double)
    */
   public boolean contains(double arg0, double arg1, double arg2, double arg3) {
      return false;
   }
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#intersects(double, double, double, double)
    */
   public boolean intersects(double arg0, double arg1, double arg2, double arg3) {
      return false;
   }

   
   /* (non-Javadoc)
    * @see java.awt.Shape#getBounds()
    */
   public Rectangle getBounds() {
      return path.getBounds();
   }
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#contains(java.awt.geom.Point2D)
    */
   public boolean contains(Point2D p) {
      return shape.contains(p);
   }
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#contains(java.awt.geom.Point2D)
    */
   public boolean proximityContains(Point2D p) {
	   return proximityShape.contains(p);
   }
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#getBounds2D()
    */
   public Rectangle2D getBounds2D() {
      return null;
   }
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#contains(java.awt.geom.Rectangle2D)
    */
   public boolean contains(Rectangle2D arg0) {
      return false;
   }
   

   /* (non-Javadoc)
    * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
    */
   public boolean intersects(Rectangle2D r) {
      return shape.intersects(r);
   }
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
    */
   public boolean proximityIntersects(Rectangle2D r) {
      return proximityShape.intersects(r);
   }
   
   
   /* (non-Javadoc)
    * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform)
    */
   public PathIterator getPathIterator(AffineTransform arg0) {
      return path.getPathIterator(arg0);
   }
   
      
   /* (non-Javadoc)
    * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform, double)
    */
   public PathIterator getPathIterator(AffineTransform arg0, double arg1) {
      return path.getPathIterator(arg0, arg1);
   }
   
   
   private Cubic[] calcNaturalCubic(int n, int[] x) {
      float[] gamma = new float[n+1];
      float[] delta = new float[n+1];
      float[] D = new float[n+1];
   
      /* We solve the equation
         [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
         |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
         |  1 4 1   | | .  | = |      .         |
         |    ..... | | .  |   |      .         |
         |     1 4 1| | .  |   |3(x[n] - x[n-2])|
         [       1 2] [D[n]]   [3(x[n] - x[n-1])]
                 
         by using row operations to convert the matrix to upper triangular
         and then back sustitution.  The D[i] are the derivatives at the knots.
       */
      
      gamma[0] = 1.0f/2.0f;
      for (int i = 1; i < n; i++) {
         gamma[i] = 1/(4-gamma[i-1]);
      }
      gamma[n] = 1/(2-gamma[n-1]);
      
      delta[0] = 3*(x[1]-x[0])*gamma[0];
      for (int i = 1; i < n; i++) {
         delta[i] = (3*(x[i+1]-x[i-1])-delta[i-1])*gamma[i];
      }
      delta[n] = (3*(x[n]-x[n-1])-delta[n-1])*gamma[n];
      
      D[n] = delta[n];
      for (int i = n-1; i >= 0; i--) {
         D[i] = delta[i] - gamma[i]*D[i+1];
      }
      
      /* now compute the coefficients of the cubics */
      Cubic[] C = new Cubic[n];
      for (int i = 0; i < n; i++) {
         C[i] = new Cubic(x[i], 
                          D[i], 
                          3*(x[i+1] - x[i]) - 2*D[i] - D[i+1],
                          2*(x[i] - x[i+1]) + D[i] + D[i+1]);
      }
      return C;
   }
   
   
   public void addPointsToGui(GuiView editWindow) {
      GraphArcPathPoint pathPoint;
      ArcPathPointHandler pointHandler;
      
      ((GraphArcPathPoint)pathPoints.get(0)).setDraggable(false);
      ((GraphArcPathPoint)pathPoints.get(pathPoints.size()-1)).setDraggable(false);
      
      for (int i = 0; i < pathPoints.size(); i++) {
         pathPoint = (GraphArcPathPoint)pathPoints.get(i);
         pathPoint.setVisible(false);
         
         // Check whether the point has already been added to the gui
         // as addPointsToGui() may have been called after the user
         // split an existing point. If this is the case, we don't want
         // to add all the points again along with new action listeners,
         // we just want to add the new point.
         // Nadeem 21/06/2005
         if (editWindow.getIndexOf(pathPoint) < 0) {
            editWindow.add(pathPoint);
            
            pointHandler = new ArcPathPointHandler(editWindow, pathPoint);
            
            if (pathPoint.getMouseListeners().length == 0) {
               pathPoint.addMouseListener(pointHandler);
            }
            
            if (pathPoint.getMouseMotionListeners().length == 0) {
               pathPoint.addMouseMotionListener(pointHandler);
            }
            
            if (pathPoint.getMouseWheelListeners().length == 0) {
               pathPoint.addMouseWheelListener((MouseWheelListener)pointHandler);
            }              
            pathPoint.updatePointLocation();
         }
      }
   }   
   
   
   public void addPointsToGui(JLayeredPane editWindow) {
      GraphArcPathPoint pathPoint;
      ArcPathPointHandler pointHandler;
      
      ((GraphArcPathPoint)pathPoints.get(0)).setDraggable(false);
      ((GraphArcPathPoint)pathPoints.get(pathPoints.size() - 1)).setDraggable(false);
      
      for (int i = 0; i < pathPoints.size(); i++) {
         pathPoint = (GraphArcPathPoint)pathPoints.get(i);
         pathPoint.setVisible(false);
         
         // Check whether the point has already been added to the gui
         // as addPointsToGui() may have been called after the user
         // split an existing point. If this is the case, we don't want
         // to add all the points again along with new action listeners,
         // we just want to add the new point.
         // Nadeem 21/06/2005
         if (editWindow.getIndexOf(pathPoint) < 0) {
            editWindow.add(pathPoint);
            pointHandler = new ArcPathPointHandler(editWindow, pathPoint);
            
            if (pathPoint.getMouseListeners().length == 0) {
               pathPoint.addMouseListener(pointHandler);
            }
            
            if (pathPoint.getMouseMotionListeners().length == 0) {
               pathPoint.addMouseMotionListener(pointHandler);
            }
            
            if (pathPoint.getMouseWheelListeners().length == 0) {
               pathPoint.addMouseWheelListener((MouseWheelListener)pointHandler);
            }            
            pathPoint.updatePointLocation();
         }
      }
   }


   public void delete() {  // Michael: Tells the arc points to remove themselves
      while (!pathPoints.isEmpty()) {
         ((GraphArcPathPoint)pathPoints.get(0)).kill(); // force delete of ALL points
      }
   }
   
   
   public String[][] getArcPathDetails() {
      int length = getEndIndex() + 1;
      String[][] details = new String[length][3];

      int zoom = this.getArc().getZoom();
      int x, y;
      for (int c = 0; c < length; c++) {         
         x = ((GraphArcPathPoint)pathPoints.get(c)).getX();
         details[c][0] = String.valueOf(ZoomController.getUnzoomedValue(x, zoom));
         y = ((GraphArcPathPoint)pathPoints.get(c)).getY();        
         details[c][1] = String.valueOf(ZoomController.getUnzoomedValue(y, zoom));
         details[c][2] = 
                 String.valueOf(((GraphArcPathPoint)pathPoints.get(c)).getPointType());
      }
      return details;
   }
   
   
   public void purgePathPoints() {	
      // Dangerous! Only called from DataLayer when loading ArcPaths
      pathPoints.clear();
   }
   
   /**
    * insertPoint()
    * Inserts a new point into the Array List of path points
    * at the specified index and shifts all the following points along
    * @param index
    * @param newpoint
    * @author Nadeem
    */
   public void insertPoint(int index, GraphArcPathPoint newpoint) {
      pathPoints.add(index, newpoint);
      if (myArc.getParent() instanceof GuiView) {
         addPointsToGui((GuiView)myArc.getParent());
      } else {
         addPointsToGui((JLayeredPane)myArc.getParent());
      }
   }
   
   
   /**
    * splitSegment()
    * Goes through neighbouring pairs of ArcPathPoints determining the midpoint
    * between them. Then calculates the distance from midpoint to the point 
    * passed as an argument. The pair of ArcPathPoints resulting in the shortest
    * distance then have an extra point added between them at the midpoint 
    * effectively splitting that segment into two.
    * @param mouseposition
    */
   public GraphArcPathPoint splitSegment(Point2D.Float mouseposition) {
      int wantedpoint = findPoint(mouseposition);
      
      GraphArcPathPoint first = (GraphArcPathPoint)pathPoints.get(wantedpoint);
      GraphArcPathPoint second = (GraphArcPathPoint)pathPoints.get(wantedpoint + 1);
      GraphArcPathPoint newpoint = 
              new GraphArcPathPoint(second.getMidPoint(first), first.getPointType(), this);
      insertPoint(wantedpoint+1, newpoint);
      createPath();
      myArc.updateArcPosition();

      return newpoint;
   }
   

   public UndoableEdit insertPointAt(Point2D.Float mouseposition, boolean flag) {
      int wantedpoint = findPoint(mouseposition);
      
      GraphArcPathPoint newPoint = new GraphArcPathPoint(mouseposition, flag, this);
      insertPoint(wantedpoint+1, newPoint);
      createPath();
      myArc.updateArcPosition();

      return new AddArcPathPointEdit(this.getArc(), newPoint);
   }

   
   private int findPoint(final Point2D.Float mouseposition) {
      // An array to store all the distances from the midpoints
      double[] distances = new double[pathPoints.size() - 1];
      
      // Calculate the midpoints and distances to them
      for (int index = 0; index < (pathPoints.size() - 1); index++) {
         GraphArcPathPoint first = (GraphArcPathPoint)pathPoints.get(index);
         GraphArcPathPoint second = (GraphArcPathPoint)pathPoints.get(index+1);
         Point2D.Float midpoint = first.getMidPoint(second);
         distances[index] = midpoint.distance(mouseposition);
      }
      
      // Now determine the shortest midpoint
      double shortest = distances[0];
      int wantedpoint = 0;
      for (int index = 0; index < pathPoints.size() - 1; index++) {
         if (distances[index] < shortest) {
            shortest = distances[index];
            wantedpoint = index;
         }
      }
      return wantedpoint;
   }

   
   public boolean isPointSelected(int j) {
      return ((GraphArcPathPoint)pathPoints.get(j)).isSelected();
   }
   
}



class Cubic {
   
   float a,b,c,d;         /* a + b*u + c*u^2 + d*u^3 */
   
   
   public Cubic(float _a, float _b, float _c, float _d) {
      a = _a;
      b = _b;
      c = _c;
      d = _d;
   }
   
   
   // Return first control point coordinate (calculated from coefficients)
   public float getX1() {
      return ((b + 3*a)/3);
   }
   
   
   // Return second control point coordinate (calculated from coefficients)
   public float getX2() {
      return ((c + 2*b + 3*a)/3);
   }
   
   
   /** evaluate cubic */
   public float eval(float u) {
      return (((d*u) + c)*u + b)*u + a;
   }
   
}