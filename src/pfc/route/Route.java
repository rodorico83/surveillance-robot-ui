package pfc.route;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pfc.control.constants.Operations;

/**
 * Route definition implementation
 * @author rodrigo
 *
 */
public class Route implements Serializable {

	/**
	 *  Serial ID for serialization 
	 */
	private static final long serialVersionUID = 7368025616380336590L;
	private Vector<Movement> movements;
	private String name;
	private int position;

	/**
	 * Constructor
	 */
	public Route(){
		movements = new Vector<Movement>();
		position = 0;
	}

	/**
	 * Add a new move to the route
	 * @param movement Movement
	 */
	public void addMove(Movement movement){
		movements.add(movement);
	} 

	/**
	 * Get all the route moves in a vector
	 * @return Vector<Movement>
	 */
	public Vector<Movement> getMovements() {
		return movements;
	}

	/**
	 * Get the name of the route 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the route
	 * @param name String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Save a route by name
	 * @param name Route name
	 * @param file File, xml file
	 */
	static public void saveRouteName (String name,File file) {
		StreamResult stream;
		Document doc = null;

		try {
			if (!file.exists()){
				file.createNewFile();
				DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
				doc = docBuilder.newDocument();

				/*create the root element and add it to the document */
				Element rootElem = doc.createElement("routes");
				doc.appendChild(rootElem);

			}
			else {
				DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = dbfac.newDocumentBuilder();
				doc = docBuilder.parse(file);
			}

			Element root = doc.getDocumentElement();
			Element nameElem = doc.createElement("route");
			nameElem.setAttribute("name", name);
			root.appendChild(nameElem);


			/* Prepare the DOM document for writing */
			Source source = new DOMSource(doc);
			stream = new StreamResult(file);

			/* Write the DOM document to the file */
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");

			xformer.transform(source, stream);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Save a route by route instance
	 * @param route Route to save
	 * @param file File, xml file
	 */
	static public void saveRoute (Route route, File file) {
		Document doc = null;
		StreamResult stream;

		try {


			/* We need a Document */
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();

			/* create the root element and add it to the document */
			Element rootElem = doc.createElement("route");
			rootElem.setAttribute("name", route.getName());
			doc.appendChild(rootElem);
			Vector<Movement> vectorMoves = route.getMovements();
			for (int i= 0; vectorMoves != null && i < vectorMoves.size(); i++){
				Movement move = vectorMoves.get(i);
				Element moveElem = doc.createElement("move");
				moveElem.setAttribute("direction", Operations.getOperation(move.getMove()));
				moveElem.setAttribute("time", String.valueOf(move.getMoveDuration()));
				moveElem.setAttribute("checkPoint", String.valueOf(move.getCheckMove()));
				rootElem.appendChild(moveElem);
			}


			/* Prepare the DOM document for writing */
			Source source = new DOMSource(doc);
			stream = new StreamResult(file);

			/* Write the DOM document to the file */
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");

			xformer.transform(source, stream);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get a Route By xml file
	 * @param file XML to load
	 * @return Route
	 * @throws Exception Exception happened 
	 */
	static public Route getRoute(File file) throws Exception {
		Route result = null;
		Document doc;
		if (file.exists()) {
			result = new Route();
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.parse(file);
			Element root = doc.getDocumentElement();
			result.setName(root.getAttribute("name"));
			NodeList moveList = root.getChildNodes();
			for (int i = 0; moveList != null && i < moveList.getLength(); i++) {
				if (moveList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element moveElem = (Element) moveList.item(i); 
					int checkMove = Integer.parseInt(moveElem.getAttribute("checkPoint"));
					char moveChar = Operations.getOperation(moveElem.getAttribute("direction"));
					long time = Long.parseLong(moveElem.getAttribute("time"));
					Movement move = result.new Movement(moveChar, time, checkMove);
					result.addMove(move);
				}
			}
		}

		return result;

	}

	/**
	 * Get Route names from  a xml file
	 * @param file XML file
	 * @return String[], All the route names 
	 */
	static public String[] getRouteNames(File file) {
		String[] result = null;
		Document doc;
		try {
			if (file.exists()){
				DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
				doc = docBuilder.parse(file);

				Element root = doc.getDocumentElement();
				NodeList nameList = root.getChildNodes();
				result = new String[nameList.getLength()];
				for (int i = 0; i < nameList.getLength(); i++) {
					if (nameList.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element nameElem = (Element) nameList.item(i);

						if (nameElem.hasAttribute("name")) {
							String name = nameElem.getAttribute("name");
							result[i] = name;
						}
					}
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Movement implementation
	 * @author rodrigo
	 *
	 */
	public class Movement {
		private char move;
		private long moveDuration;
		private int checkMove;


		/**
		 * Constructor
		 * @param move char move
		 * @param moveDuration move duration
		 * @param checkMove check move
		 */
		public Movement(char move, long moveDuration,int checkMove){
			this.move = move;
			this.moveDuration = moveDuration;
			this.checkMove = checkMove;
		}

		/**
		 * Get the king move
		 * @return char
		 */
		public char getMove() {
			return move;
		}
		
		/**
		 * Set the move kind
		 * @param move char
		 */
		public void setMove(char move) {
			this.move = move;
		}
		
		/**
		 * Get Move Duration
		 * @return long
		 */
		public long getMoveDuration() {
			return moveDuration;
		}
		
		/**
		 * Set move duration 
		 * @param moveTime long
		 */
		public void setMoveDuration(long moveTime) {
			this.moveDuration = moveTime;
		}
		
		
		/**
		 * Get Check move or not
		 * @return boolean
		 */
		public int getCheckMove() {
			return checkMove;
		}

		/**
		 * Set a check move or not 
		 * @param checkMove boolean
		 */
		public void setCheckMove(int checkMove) {
			this.checkMove = checkMove;
		}	
	}

	/**
	 * Get the position of the actual move pointer
	 * @return int
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Set the position of the actual move pointer
	 * @param position int
	 */
	public void setPosition(int position) {
		this.position = position;
	}

}
