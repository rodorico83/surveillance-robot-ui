package pfc.gui.client;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import pfc.control.configuration.SavedConfiguration;
import pfc.gui.ApplicationRunner;
import pfc.language.Language;
import pfc.route.Route;
import pfc.control.constants.Operations;

/**
 * Manual Vigilance / Programming Route Window
 * @author rodrigo
 *
 */
public class ManualVigilanceWindow extends PlayerWindow implements KeyListener{


	/**
	 * 
	 */
	private static final long serialVersionUID = -6788960408862217884L;


	private Language lang;


	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.gui</code>.
	 */
	private Logger log = Logger.getLogger("pfc.gui.client");


	boolean forward = false;
	boolean backward = false;
	boolean left = false;
	boolean right = false;
	boolean upCamera = false;
	boolean downCamera = false;
	boolean leftCamera = false;
	boolean rightCamera = false;
	boolean centerCamera = false;
	private static String keyCameraUp;
	private static String keyCameraDown;
	private static String keyCameraLeft;
	private static String keyCameraRight;
	private static String keyCameraCenter;
	private static boolean keysCameraOks = false;
	private Route route = null;
	boolean programRoute = false;
	private GuiController controller;
	private GridBagLayout gridbag;
	private GridBagConstraints constraints;
	private JPanel buttonsPanel;
	private JPanel routeConfPanel;
	private JTextField routeNameField;
	private SavedConfiguration config;
	private boolean programmingRoute;
	private boolean programRouteWindow;
	private ArrayList<String> movements = null;
	private char lastOperation; 

	
	/**
	 * Constructor
	 * @param lang language
	 * @param controller Service controller
	 * @param programRouteWindow boolean true (program route window) false (manual vigilance window)
	 */
	public ManualVigilanceWindow(Language lang, GuiController controller, boolean programRouteWindow) {
		super(lang);

		boolean okCameraKeys = true;

		movements = new ArrayList<String>();

		this.lang = lang;
		this.controller = controller;
		this.programRouteWindow = programRouteWindow;
		this.programmingRoute = false;

		// load saved configuration
		this.config = SavedConfiguration.getSavedConfiguration();

		this.setName(this.lang.OPTIONS_MANUAL_CONTROL_WINDOW_NAME);
		this.setTitle(this.lang.OPTIONS_MANUAL_CONTROL_WINDOW_NAME);
		this.setFocusable(true);
		this.addKeyListener(this);

		this.addWindowListener(new ManualVigilanceWindowAdapter());

		gridbag = new GridBagLayout();
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);

		if (config.getParameter(SavedConfiguration.KEY_CAMERA_UP).length() != 1) {
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_UP + " is not a character");
			okCameraKeys = false;
		}
		else keyCameraUp = config.getParameter(SavedConfiguration.KEY_CAMERA_UP);

		if (config.getParameter(SavedConfiguration.KEY_CAMERA_DOWN).length() != 1) {
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_DOWN + " is not a character");
			okCameraKeys = false;
		}
		else keyCameraDown = config.getParameter(SavedConfiguration.KEY_CAMERA_DOWN);

		if (config.getParameter(SavedConfiguration.KEY_CAMERA_LEFT).length() != 1) {
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_LEFT + " is not a character");
			okCameraKeys = false;
		}
		else keyCameraLeft = config.getParameter(SavedConfiguration.KEY_CAMERA_LEFT);

		if (config.getParameter(SavedConfiguration.KEY_CAMERA_RIGHT).length() != 1) { 
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_RIGHT + " is not a character");
			okCameraKeys = false;
		}
		else keyCameraRight = config.getParameter(SavedConfiguration.KEY_CAMERA_RIGHT);

		if (config.getParameter(SavedConfiguration.KEY_CAMERA_CENTER).length() != 1) { 
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_CENTER + " is not a character");
			okCameraKeys = false;
		}
		else keyCameraCenter = config.getParameter(SavedConfiguration.KEY_CAMERA_CENTER);
		
		
		
		if (okCameraKeys) ManualVigilanceWindow.keysCameraOks = true;
		else ApplicationRunner.handleWarning(lang.CAMERA_KEYS_NOT_OK_MESSAGE);

	}




	public void initializeComponents(){

		if (programRouteWindow) {

			/* Route Configuration */
			routeConfPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			routeConfPanel.setBorder(BorderFactory.createTitledBorder(lang.ROUTE_CONFIGURATION_PANEL_NAME));
			routeConfPanel.setLayout(gridbag);
			constraints.anchor = GridBagConstraints.CENTER;
			constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
			gridbag.setConstraints(routeConfPanel, constraints);

			JLabel routeNameLabel = new JLabel(lang.ROUTE_NAME_LABEL);
			constraints.gridwidth = 1;
			constraints.anchor = GridBagConstraints.EAST;
			gridbag.setConstraints(routeNameLabel, constraints);
			routeConfPanel.add(routeNameLabel);

			routeNameField = new JTextField(10);
			constraints.anchor = GridBagConstraints.WEST;
			constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
			gridbag.setConstraints(routeNameField, constraints);
			routeConfPanel.add(routeNameField);

			constraints.anchor = GridBagConstraints.CENTER;
			constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
			this.add(routeConfPanel, constraints);
		}


		/* Button Configuration */
		buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonsPanel.setLayout(gridbag);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
		gridbag.setConstraints(buttonsPanel, constraints);

		JButton keyCameraConfButton = new JButton(lang.KEY_CAMERA_CONF);
		keyCameraConfButton.addActionListener(new KeyCameraConf(this));
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridwidth = 1; //end row
		gridbag.setConstraints(keyCameraConfButton, constraints);
		buttonsPanel.add(keyCameraConfButton);

		if (programRouteWindow) {


			JButton startButton = new JButton(lang.START);
			startButton.addActionListener(new StartRoute());
			constraints.anchor = GridBagConstraints.CENTER;
			constraints.gridwidth = 3; //end row
			gridbag.setConstraints(startButton, constraints);
			buttonsPanel.add(startButton);


			JButton saveButton = new JButton(lang.SAVE);
			saveButton.addActionListener(new SaveRoute());
			constraints.anchor = GridBagConstraints.CENTER;
			constraints.gridwidth = 4; //end row
			gridbag.setConstraints(saveButton, constraints);
			buttonsPanel.add(saveButton);
		}

		JButton endButton = new JButton(lang.QUIT);
		endButton.addActionListener(new ControlMoveEnd());
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
		gridbag.setConstraints(endButton, constraints);
		buttonsPanel.add(endButton);

		this.add(buttonsPanel);
		this.requestFocus(true);
	}


	/**
	 * @return String
	 */
	public static String getKeyCameraUp() {
		return keyCameraUp;
	}
	
	/**
	 * 
	 * @param keyCameraUp String
	 */
	public static void setKeyCameraUp(String keyCameraUp) {
		ManualVigilanceWindow.keyCameraUp = keyCameraUp;
	}
	
	/**
	 * 
	 * @return String
	 */
	public static String getKeyCameraDown() {
		return keyCameraDown;
	}
	
	/**
	 * 
	 * @param keyCameraDown String 
	 */
	public static void setKeyCameraDown(String keyCameraDown) {
		ManualVigilanceWindow.keyCameraDown = keyCameraDown;
	}
	
	/**
	 * 
	 * @return String
	 */
	public static String getKeyCameraLeft() {
		return keyCameraLeft;
	}
	
	/**
	 * 
	 * @param keyCameraLeft String
	 */
	public static void setKeyCameraLeft(String keyCameraLeft) {
		ManualVigilanceWindow.keyCameraLeft = keyCameraLeft;
	}
	
	/**
	 * 
	 * @return String
	 */
	public static String getKeyCameraRight() {
		return keyCameraRight;
	}
	
	/**
	 * 
	 * @param keyCameraRight Strin 
	 */
	public static void setKeyCameraRight(String keyCameraRight) {
		ManualVigilanceWindow.keyCameraRight = keyCameraRight;
	}
	
	
	/**
	 * @return String
	 */
	public static String getKeyCameraCenter() {
		return keyCameraCenter;
	}

	/**
	 * @param keyCameraCenter String
	 */
	public static void setKeyCameraCenter(String keyCameraCenter) {
		ManualVigilanceWindow.keyCameraCenter = keyCameraCenter;
	}
	
	/**
	 * Indicates if all the camera control keys are correct
	 * @return boolean
	 */
	public static boolean isKeysCameraOks() {
		return keysCameraOks;
	}
	
	/**
	 * 
	 * @param keysCameraOks boolean
	 */
	public static void setKeysCameraOks(boolean keysCameraOks) {
		ManualVigilanceWindow.keysCameraOks = keysCameraOks;
	}

	public void keyPressed(KeyEvent ke) {
		int key = ke.getKeyCode();


		if (key == KeyEvent.VK_UP && !this.forward) {
			try {
				controller.getConnection().forwardRobotMovement(true);
				forward = true;
				movements.add(0,String.valueOf(Operations.FORWARD_MOVE));
				lastOperation = Operations.FORWARD_MOVE;
			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
		}
		else if (key == KeyEvent.VK_DOWN && !this.backward){
			try {
				controller.getConnection().backwardRobotMovement(true);
				backward = true;
				movements.add(0,String.valueOf(Operations.BACKWARD_MOVE));
				lastOperation = Operations.BACKWARD_MOVE;
			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
		}
		else if (key == KeyEvent.VK_LEFT && !this.left) {
			try {
				controller.getConnection().leftRobotMovement(true);
				left = true;
				movements.add(0,String.valueOf(Operations.LEFT_MOVE));
				lastOperation = Operations.LEFT_MOVE;
			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
		}
		else if (key == KeyEvent.VK_RIGHT && !this.right) {
			try {
				controller.getConnection().rightRobotMovement(true);
				right = true;
				movements.add(0,String.valueOf(Operations.RIGHT_MOVE));
				lastOperation = Operations.RIGHT_MOVE;
			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
		}

		if (ManualVigilanceWindow.keysCameraOks == true) {
			String keyString = KeyEvent.getKeyText(key);
			
			if (keyString.compareToIgnoreCase(keyCameraCenter) == 0&& !this.centerCamera){
				try {
					controller.getConnection().centerCameraMovement();
					centerCamera = true;
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
			else if (keyString.compareToIgnoreCase(keyCameraUp) == 0 && !this.upCamera){
				try {
					controller.getConnection().upCameraMovement(true);
					upCamera = true;
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
			else if (keyString.compareToIgnoreCase(keyCameraDown) == 0 && !this.downCamera){
				try {
					controller.getConnection().downCameraMovement(true);
					downCamera = true;
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
			else if (keyString.compareToIgnoreCase(keyCameraRight) == 0 && !this.rightCamera){
				try {
					controller.getConnection().rightCameraMovement(true);
					rightCamera = true;
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
			else if (keyString.compareToIgnoreCase(keyCameraLeft) == 0 && !this.leftCamera){
				try {
					controller.getConnection().leftCameraMovement(true);
					leftCamera = true;
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
		}

	}

	/**
	 * Send a new route move when a key is released
	 * @throws RemoteException RMI Exception
	 * @throws IOException Serial Port communication exception
	 */
	public void sendMoveAfterReleaseKey() throws RemoteException, IOException{
	
			if (!movements.isEmpty()){
				if (movements.get(0).compareTo(String.valueOf(Operations.FORWARD_MOVE)) == 0 && lastOperation != Operations.FORWARD_MOVE){
					controller.getConnection().forwardRobotMovement(true);
				}
				else if (movements.get(0).compareTo(String.valueOf(Operations.BACKWARD_MOVE)) == 0 && lastOperation != Operations.BACKWARD_MOVE){
					controller.getConnection().backwardRobotMovement(true);
				}
				else if (movements.get(0).compareTo(String.valueOf(Operations.LEFT_MOVE)) == 0 && lastOperation != Operations.LEFT_MOVE){
					controller.getConnection().leftRobotMovement(true);
				}
				else if (movements.get(0).compareTo(String.valueOf(Operations.RIGHT_MOVE)) == 0 && lastOperation != Operations.RIGHT_MOVE){
					controller.getConnection().rightRobotMovement(true);
				}
			}
	}



	public void keyReleased(KeyEvent ke) {
		int key = ke.getKeyCode();

		/* Seleccion de control para el robot */
		if (key == KeyEvent.VK_UP) {
			try {
				forward = false;
				if (movements.get(0).compareTo(String.valueOf(Operations.FORWARD_MOVE)) == 0) {
					if (movements.size() == 1)
						controller.getConnection().forwardRobotMovement(false);
				}
				movements.remove(String.valueOf(Operations.FORWARD_MOVE));
				sendMoveAfterReleaseKey();

			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
		}
		else if (key == KeyEvent.VK_DOWN){
			try {
				backward = false;
				if (movements.get(0).compareTo(String.valueOf(Operations.BACKWARD_MOVE)) == 0) {
					if (movements.size() == 1)
						controller.getConnection().backwardRobotMovement(false);
				}
				movements.remove(String.valueOf(Operations.BACKWARD_MOVE));
				sendMoveAfterReleaseKey();


			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
		}
		else if (key == KeyEvent.VK_LEFT) {
			try {
				left = false;
				if (movements.get(0).compareTo(String.valueOf(Operations.LEFT_MOVE)) == 0) {
					if (movements.size() == 1)
						controller.getConnection().leftRobotMovement(false);
				}
				movements.remove(String.valueOf(Operations.LEFT_MOVE));
				sendMoveAfterReleaseKey();


			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
		}
		else if (key == KeyEvent.VK_RIGHT) {
			try {
				right = false;
				if (movements.get(0).compareTo(String.valueOf(Operations.RIGHT_MOVE)) == 0) {
					if (movements.size() == 1)
						controller.getConnection().rightRobotMovement(false);
				}
				movements.remove(String.valueOf(Operations.RIGHT_MOVE));
				sendMoveAfterReleaseKey();


			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
		}
		if (ManualVigilanceWindow.keysCameraOks == true){
			String keyString = KeyEvent.getKeyText(key);
			if (keyString.compareToIgnoreCase(keyCameraUp) == 0){
				try {
					controller.getConnection().upCameraMovement(false);
					upCamera = false;
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
			else if (keyString.compareToIgnoreCase(keyCameraDown) == 0){
				try {
					controller.getConnection().downCameraMovement(false);
					downCamera = false;
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
			else if (keyString.compareToIgnoreCase(keyCameraRight) == 0){
				try {
					controller.getConnection().rightCameraMovement(false);
					rightCamera = false;
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
			else if (keyString.compareToIgnoreCase(keyCameraLeft) == 0){
				try {
					controller.getConnection().leftCameraMovement(false);
					leftCamera = false;
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
			else if (keyString.compareToIgnoreCase(keyCameraCenter) == 0){
				centerCamera = false;
			}
		}

	}

	public void keyTyped(KeyEvent ke) {}




	/**
	 * Handles all application quit events.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	class ManualVigilanceWindowAdapter extends WindowAdapter {

		public void windowClosing(WindowEvent we) {
			try {
				controller.releaseRobotControl();
				programmingRoute = false;
				movements.clear();
			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}catch (IOException ex){
				log.severe(ex.getMessage());
				ApplicationRunner.handleAlert(ex.getMessage());
				System.exit(-1);
			}
			closeRTP();
			remove(buttonsPanel);
			buttonsPanel.removeAll();
			if (programRouteWindow) {
				remove(routeConfPanel);
				routeConfPanel.removeAll();
			}
			setVisible(false);
		}
	}



	/**
	 * Handles button exit.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	class StartRoute implements ActionListener {

		/**
		 * Quits the application when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {
			if (keysCameraOks){
				route = new Route();
				programmingRoute = true;
				requestFocus(true);
				try {
					controller.getConnection().startRouteDefinition();
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}
			}
			else ApplicationRunner.handleWarning(lang.CAMERA_KEYS_NOT_OK_MESSAGE);

		}
	}


	/**
	 * Handles button exit.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	class SaveRoute implements ActionListener {


		private String[] routeNames;
		private boolean routeExist = false;
		/**
		 * Quits the application when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {
			if (programmingRoute) {
				if (routeNameField.getText().compareTo("") != 0){
					route.setName(routeNameField.getText());
					try {
						routeNames = controller.getConnection().getRouteNames();
						for (int i = 0; i < routeNames.length; i++){
							if  (routeNameField.getText().compareTo(routeNames[i]) == 0) routeExist = true;
						}
						if (routeExist) 
							ApplicationRunner.handleWarning(lang.ROUTE_EXISTS_MESSAGE);

						controller.getConnection().addRoute(route);
						log.info("Route " + route.getName() + " added");
						route = null;
						programmingRoute = false;

					} catch (RemoteException e) {
						log.severe(e.getMessage());
						ApplicationRunner.handleAlert(e.getMessage());
						System.exit(-1);
					} catch (IOException e) {
						log.severe(e.getMessage());
						ApplicationRunner.handleAlert(e.getMessage());
						System.exit(-1);
					}
				}
				else ApplicationRunner.handleWarning(lang.ROUTE_DEFINE_NAME_MESSAGE);
			}
			else ApplicationRunner.handleWarning(lang.ROUTE_START_BEFORE_MESSAGE);
			requestFocus(true);
		}
	}

	/**
	 * Handles button exit.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	class KeyCameraConf implements ActionListener {

		private JFrame parent; 
		KeyCameraConf (JFrame parent){
			this.parent = parent;
		}

		/**
		 * Quits the application when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {
			new ConfigCameraKeyMovementWindow(lang,parent);
		}
	}

	/**
	 * Handles button exit.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	private class ControlMoveEnd implements ActionListener {

		/**
		 * Quits the application when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {
			try {
				controller.releaseRobotControl();
				programmingRoute = false;
				movements.clear();
			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}catch (IOException ex){
				log.severe(ex.getMessage());
				ApplicationRunner.handleAlert(ex.getMessage());
				System.exit(-1);
			}
			closeRTP();
			remove(buttonsPanel);
			buttonsPanel.removeAll();
			if (programRouteWindow) {
				remove(routeConfPanel);	
				routeConfPanel.removeAll();
			}
			setVisible(false);
		}
	}
	
}
