package pige.dataLayer;

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Create DataLayerWriter object
 * @param DataLayer object containing net to save
 * @author Ben Kirby
 * @author Pere Bonet (minor changes)
 * 
 * @Author Dianxiang Xu, August 2011
 */
public class GraphDataLayerWriter {

   /** DataLayer object passed in to save */
   private GraphDataLayerInterface graphModel;
   
   /** Create a writer with the DataLayer object to save*/
   public GraphDataLayerWriter(GraphDataLayerInterface currentGraph) {
      graphModel = currentGraph;
   }

   /**
    * @param filename URI location to save file
    * @throws ParserConfigurationException
    * @throws DOMException
    * @throws TransformerConfigurationException
    * @throws TransformerException
    */
   public void saveXML(File file) throws NullPointerException, IOException,
           ParserConfigurationException, DOMException,
           TransformerConfigurationException, TransformerException {

      // Error checking
      if (file == null) {
         throw new NullPointerException("Null file in saveXML");
      }

      Document graphDOM = null;

      StreamSource xsltSource = null;
      Transformer transformer = null;
      try {
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = builderFactory.newDocumentBuilder();
         graphDOM = builder.newDocument();

         Element GXML = graphDOM.createElement("gxml"); // GXML Top Level Element
         graphDOM.appendChild(GXML);

         Attr xmlAttr = graphDOM.createAttribute("xmlns"); // XML "xmlns" Attribute
         xmlAttr.setValue("");
         GXML.setAttributeNode(xmlAttr);

         Element graph = graphDOM.createElement("graph"); 
         GXML.appendChild(graph);
         Attr graphAttrId = graphDOM.createAttribute("id"); // "id" Attribute
         graphAttrId.setValue("Graph-One");
         graph.setAttributeNode(graphAttrId);
         Attr graphAttrType = graphDOM.createAttribute("type"); // "type" Attribute
         graphAttrType.setValue("General Graph");
         graph.setAttributeNode(graphAttrType);
      
         GraphAnnotationNote[] labels = graphModel.getLabels();
         for (int i = 0; i < labels.length; i++) {
            graph.appendChild(createAnnotationNoteElement(labels[i], graphDOM));
         }

         GraphNode[] graphNodes = graphModel.getGraphNodes();
         for (int i = 0; i < graphNodes.length; i++) {
            graph.appendChild(createNodeElement(graphNodes[i], graphDOM));
         }

         GraphArc[] arcs = graphModel.getArcs();
         for (int i = 0; i < arcs.length; i++) {
            Element newArc = createArcElement(arcs[i], graphDOM);

            int arcPoints = arcs[i].getArcPath().getArcPathDetails().length;
            String[][] point = arcs[i].getArcPath().getArcPathDetails();
            for (int j = 0; j < arcPoints; j++) {
               newArc.appendChild(createArcPoint(point[j][0], point[j][1], point[j][2], graphDOM, j));
            }
            graph.appendChild(newArc);
         //newArc = null;
         }

         graphDOM.normalize();
         // Create Transformer with XSL Source File
         xsltSource = new StreamSource(Thread.currentThread().
                 getContextClassLoader().getResourceAsStream("xslt" +
//               System.getProperty("file.separator") 
                 "/" 
                 + "GenerateGraphXML.xsl"));

         transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
         // Write file and do XSLT transformation to generate correct PNML
         File outputObjectArrayList = file;//new File(filename); // Output for XSLT Transformation
         DOMSource source = new DOMSource(graphDOM);
         StreamResult result = new StreamResult(outputObjectArrayList);
         transformer.transform(source, result);
      } catch (ParserConfigurationException e) {
         // System.out.println("=====================================================================================");
         System.out.println("ParserConfigurationException thrown in saveXML() " +
                 ": dataLayerWriter Class : dataLayer Package: filename=\"" +
                 file.getCanonicalPath() + "\" xslt=\"" +
                 xsltSource.getSystemId() + "\" transformer=\"" +
                 transformer.getURIResolver() + "\"");
      // System.out.println("=====================================================================================");
      // e.printStackTrace(System.err);
      } catch (DOMException e) {
         // System.out.println("=====================================================================");
         System.out.println("DOMException thrown in saveXML() " +
                 ": dataLayerWriter Class : dataLayer Package: filename=\"" +
                 file.getCanonicalPath() + "\" xslt=\"" +
                 xsltSource.getSystemId() + "\" transformer=\"" +
                 transformer.getURIResolver() + "\"");
      // System.out.println("=====================================================================");
      // e.printStackTrace(System.err);
      } catch (TransformerConfigurationException e) {
         // System.out.println("==========================================================================================");
         System.out.println("TransformerConfigurationException thrown in savePNML() " +
                 ": dataLayerWriter Class : dataLayer Package: filename=\"" + file.getCanonicalPath() + "\" xslt=\"" + xsltSource.getSystemId() + "\" transformer=\"" + transformer.getURIResolver() + "\"");
      // System.out.println("==========================================================================================");
      // e.printStackTrace(System.err);
      } catch (TransformerException e) {
         // System.out.println("=============================================================================");
         System.out.println("TransformerException thrown in saveXML() : dataLayerWriter Class : dataLayer Package: filename=\"" + file.getCanonicalPath() + "\" xslt=\"" + xsltSource.getSystemId() + "\" transformer=\"" + transformer.getURIResolver() + "\"" + e);
      // System.out.println("=============================================================================");
      // e.printStackTrace(System.err);
      }
   }

   private Element createNodeElement(GraphNode inputNode, Document document) {
      Element nodeElement = null;

      if (document != null) {
         nodeElement = document.createElement("node");
      }

      if (inputNode != null) {
         Double positionXInput = inputNode.getPositionXObject();
         Double positionYInput = inputNode.getPositionYObject();
         String idInput = inputNode.getId();
         String nameInput = inputNode.getName();
         Double nameOffsetXInput = inputNode.getNameOffsetXObject();
         Double nameOffsetYInput = inputNode.getNameOffsetYObject();
         nodeElement.setAttribute("positionX", (positionXInput != null ? String.valueOf(positionXInput) : ""));
         nodeElement.setAttribute("positionY", (positionYInput != null ? String.valueOf(positionYInput) : ""));
         nodeElement.setAttribute("name", (nameInput != null ? nameInput : (idInput != null && idInput.length() > 0 ? idInput : "")));
         nodeElement.setAttribute("id", (idInput != null ? idInput : "error"));
         nodeElement.setAttribute("nameOffsetX", (nameOffsetXInput != null ? String.valueOf(nameOffsetXInput) : ""));
         nodeElement.setAttribute("nameOffsetY", (nameOffsetYInput != null ? String.valueOf(nameOffsetYInput) : ""));
      }
      return nodeElement;
   }

   private Element createAnnotationNoteElement(GraphAnnotationNote inputLabel, Document document) {
      Element labelElement = null;

      if (document != null) {
         labelElement = document.createElement("labels");
      }

      if (inputLabel != null) {
         int positionXInput = inputLabel.getOriginalX();
         int positionYInput = inputLabel.getOriginalY();
         int widthInput = inputLabel.getNoteWidth();
         int heightInput = inputLabel.getNoteHeight();
         String nameInput = inputLabel.getNoteText();
         boolean borderInput = inputLabel.isShowingBorder();

         labelElement.setAttribute("positionX",
                 (positionXInput >= 0.0 ? String.valueOf(positionXInput) : ""));
         labelElement.setAttribute("positionY",
                 (positionYInput >= 0.0 ? String.valueOf(positionYInput) : ""));
         labelElement.setAttribute("width",
                 (widthInput >= 0.0 ? String.valueOf(widthInput) : ""));
         labelElement.setAttribute("height",
                 (heightInput >= 0.0 ? String.valueOf(heightInput) : ""));
         labelElement.setAttribute("border", String.valueOf(borderInput));
         labelElement.setAttribute("text", (nameInput != null ? nameInput : ""));
      }
      return labelElement;
   }


   private Element createArcElement(GraphArc inputArc, Document document) {
      Element arcElement = null;

      if (document != null) {
         arcElement = document.createElement("arc");
      }

      if (inputArc != null) {
    	 try { 
         String idInput = inputArc.getId();
         String sourceInput = inputArc.getSource().getId();
         String targetInput = inputArc.getTarget().getId();

         // for debugging purposes: sometimes saved arcs have empty id/source/target
         try {
        	 if (sourceInput == null || sourceInput.equals(""))
        		 System.out.print("Arc "+idInput+" source null or empty");
        	 if (targetInput == null || targetInput.equals(""))
        		 System.out.print("Arc "+idInput+" target null or empty");
         	}
         catch (Exception e){}
         
         // Double inscriptionPositionXInput = inputArc.getInscriptionOffsetXObject();        
         // Double inscriptionPositionYInput = inputArc.getInscriptionOffsetYObject();
         arcElement.setAttribute("id", (idInput != null ? idInput : "error"));
         arcElement.setAttribute("source", (sourceInput != null ? sourceInput : ""));
         arcElement.setAttribute("target", (targetInput != null ? targetInput : ""));
         arcElement.setAttribute("event", inputArc.getName());
         arcElement.setAttribute("precondition", inputArc.getPrecondition());
         arcElement.setAttribute("postcondition", inputArc.getPostcondition());
    	 }
    	 catch (Exception e){
    		 // source and target can be null?
    	 }
      }
      return arcElement;
   }

   private Element createArcPoint(String x, String y, String type,
           Document document, int id) {
      Element arcPoint = null;

      if (document != null) {
         arcPoint = document.createElement("arcpath");
      }
      String pointId = String.valueOf(id);
      if (pointId.length() < 3) {
         pointId = "0" + pointId;
      }
      if (pointId.length() < 3) {
         pointId = "0" + pointId;
      }
      arcPoint.setAttribute("id", pointId);
      arcPoint.setAttribute("xCoord", x);
      arcPoint.setAttribute("yCoord", y);
      arcPoint.setAttribute("arcPointType", type);

      return arcPoint;
   }

   public static void saveTemporaryFile(GraphDataLayerInterface data, String className) {
      // desar la xarxa a un arxiu temporal per si hi ha cap problema
      try {
         //current working dir?
         File dir = new File(Thread.currentThread().getContextClassLoader().
                 getResource("").toURI());

         // temporary file
         File tempFile = File.createTempFile("graph[" + className + "][" +
                 System.nanoTime() + "]", ".xml", dir);

         GraphDataLayerWriter saveModel = new GraphDataLayerWriter(data);

         saveModel.saveXML(tempFile);

         // Delete temp file when program exits. If PIPE crashes,
         // tempFile won't be deleted.
         tempFile.deleteOnExit();

         // If the directory does not exists, IOException will be thrown
         // and temporary file will not be created.
         System.out.println("Temporary file created at : " + tempFile.getPath());
      } catch (URISyntaxException ex) {
         Logger.getLogger(className).log(Level.SEVERE, null, ex);
      } catch (IOException ioe) {
         System.out.println("Exception creating temporary file : " + ioe);
      } catch (NullPointerException ex) {
         System.out.println("Exception creating temporary file : " + ex);
         Logger.getLogger(className).log(Level.SEVERE, null, ex);
      } catch (ParserConfigurationException ex) {
         System.out.println("Exception creating temporary file : " + ex);
         Logger.getLogger(className).log(Level.SEVERE, null, ex);
      } catch (DOMException ex) {
         System.out.println("Exception creating temporary file : " + ex);
         Logger.getLogger(className).log(Level.SEVERE, null, ex);
      } catch (TransformerConfigurationException ex) {
         System.out.println("Exception creating temporary file : " + ex);
         Logger.getLogger(className).log(Level.SEVERE, null, ex);
      } catch (TransformerException ex) {
         System.out.println("Exception creating temporary file : " + ex);
         Logger.getLogger(className).log(Level.SEVERE, null, ex);
      }
   }

}
