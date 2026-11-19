package pfc.gui.client;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.*;
import gnu.io.NoSuchPortException;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pfc.control.ScheduleProgram;
import pfc.control.client.Controller;
import pfc.control.configuration.Configuration;
import pfc.control.configuration.SavedConfiguration;
import pfc.gui.ApplicationMode;
import pfc.gui.ApplicationRunner;
import pfc.video.SelectDevice;
import pfc.video.LocalVideoReceive;
import pfc.video.VideoController;
import pfc.video.VideoReceive;
import pfc.video.VideoSession;
import pfc.language.Language;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * The main application window
 *
 * @author Rodrigo Jimenez
 * @version 1.0
 */
public class MainWindow extends JFrame {
	/**
	 * A version number for this class so that serialization can occur
	 * without worrying about the underlying class changing between
	 * serialization and deserialization.<p>
	 *
	 * Not that we ever serialize this class of course, but JFrame implements
	 * Serializable, so therefore by default we do as well.
	 */
	private static final long serialVersionUID = 5165L;

	/**
	 * The internal reference to the GUI controller.
	 */
	private GuiController controller;


	/**
	 * Config windows for introduce the configuration  
	 * for the robot 
	 */
	private JFrame configFrame = null;

	/**
	 * Configuration for a schedule function programming 
	 */
	private JFrame scheduleFrame = null;


	/**
	 * About windows for introduce see the version 
	 * and author of the proyect 
	 */
	private JFrame aboutFrame = null;

	/**
	 * Show real Video Frame
	 * 
	 */
	private ShowRealTimeVideoWindow showRealTimeFrame = null;


	/**
	 * The language selected
	 */
	private Language lang;

	/**
	 * Status of the vigilance Label
	 */
	private JLabel status = null;

	private ApplicationMode connectionType;
	private String selectedVideoSource;


	/**
	 * Boolean values to know if any Playe Window is running
	 * 
	 */
	private static boolean runningPlayer = false;


	/**
	 * Holds a reference to the controller and configuration of the robot.
	 */
	private Controller updateRunner;


	/**
	 * Holds a reference to the Thread wich reads the configuration and alarms from 
	 * the server every x Seconds
	 */
	private Thread updateThread;

	/**
	 * TextArea to save all the alarms 
	 */
	private JTextArea logArea = null;

	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.gui</code>.
	 */
	private Logger log = Logger.getLogger("pfc.gui.client");


	/**
	 * Builds and displays the main application window. The constructor begins
	 * by building the connection selection dialog box. After the user selects
	 * a connection type, the method creates a <code>MainWindow</code>
	 * instance.
	 *
	 * @param args an argument specifying whether we are starting a networked
	 * client (argument missing) or a standalone client (argument = "alone").
	 */
	public MainWindow(String[] args) {
		super();

		connectionType = (args.length == 0)
		? ApplicationMode.NETWORK_CLIENT
				: ApplicationMode.STANDALONE_CLIENT;

		// find out where our server is
		HostLocationDialog hostLocation =
			new HostLocationDialog(this, connectionType);
		// if the user cancelled the location window
		if (hostLocation.userCanceled()) {
			System.exit(0);
		}

		// try to connect to the server, we create a new controller for the instance
		try {
			selectedVideoSource = hostLocation.getSelectedVideoSource();
			if (hostLocation.isCamEnable())
				controller = new GuiController(hostLocation.getNetworkType(),
						hostLocation.getLocation(),
						hostLocation.getPort(),
						hostLocation.getCommport(),
						selectedVideoSource,
						hostLocation.getRtpport());
			else
				controller = new GuiController(hostLocation.getNetworkType(),
						hostLocation.getLocation(),
						hostLocation.getPort(),
						hostLocation.getCommport(),
						null,
						null);
		} 
		catch (NoSuchPortException e) {
			log.severe(e.getMessage());
			ApplicationRunner.handleAlert("The Comm Port can't not be found or is in use");
			System.exit(-1);
		}catch (IOException e) {
			log.severe(e.getMessage());
			ApplicationRunner.handleAlert(e.getMessage());
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			log.severe(e.getMessage());
			ApplicationRunner.handleAlert(e.getMessage());
			System.exit(-1);
		}


		this.lang = hostLocation.getLang();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				if (connectionType == ApplicationMode.STANDALONE_CLIENT){
					VideoController.stopVideoTransmitter();
					if (controller != null){
						try {
							controller.getConnection().stop();
						} catch (IOException e) {
							log.severe(e.getMessage());
							ApplicationRunner.handleAlert(e.getMessage());
							e.printStackTrace();
						}
					}
				}
				System.exit(0);
			}}
		);
		this.setName(lang.MAIN_WINDOW_NAME);
		this.setTitle(lang.MAIN_WINDOW_NAME);
		this.setResizable(true);

		/**
		 *  Add the menu bar
		 */
		JMenuBar menuBar = new JMenuBar();

		/** 
		 * File Menu option
		 */
		JMenu fileMenu = new JMenu(lang.FILE_MENU_NAME);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);


		JMenuItem startMenuItem = new JMenuItem(lang.FILE_MENU_START);
		startMenuItem.addActionListener(new StartVigilance());
		startMenuItem.setMnemonic(KeyEvent.VK_V);
		fileMenu.add(startMenuItem);


		JMenuItem stopMenuItem = new JMenuItem(lang.FILE_MENU_STOP);
		stopMenuItem.addActionListener(new StopVigilance());
		stopMenuItem.setMnemonic(KeyEvent.VK_T);
		fileMenu.add(stopMenuItem);

		JMenuItem configMenuItem = new JMenuItem(lang.FILE_MENU_CONFIG);
		configMenuItem.addActionListener(new ConfigApplication());
		configMenuItem.setMnemonic(KeyEvent.VK_C);
		fileMenu.add(configMenuItem);

		JMenuItem scheduleMenuItem = new JMenuItem(lang.FILE_MENU_SCHEDULE);
		scheduleMenuItem.addActionListener(new ScheduleApplication());
		scheduleMenuItem.setMnemonic(KeyEvent.VK_S);
		fileMenu.add(scheduleMenuItem);

		JMenuItem quitMenuItem = new JMenuItem(lang.QUIT);
		quitMenuItem.addActionListener(new QuitApplication());
		quitMenuItem.setMnemonic(KeyEvent.VK_Q);
		fileMenu.add(quitMenuItem);



		/**
		 * Config menu option
		 */
		JMenu optionsMenu = new JMenu(lang.OPTIONS_MENU_NAME);
		optionsMenu.setMnemonic(KeyEvent.VK_O);


		JMenuItem showRealVideoItem = new JMenuItem(lang.OPTIONS_SHOW_VIDEO);
		showRealVideoItem.addActionListener(new ShowRealTimeVideoApplication(hostLocation.getLocation(), hostLocation.getRtpport()));
		showRealVideoItem.setMnemonic(KeyEvent.VK_S);
		optionsMenu.add(showRealVideoItem);

		JMenuItem manualVigilanceItem = new JMenuItem(lang.OPTIONS_MANUAL_VIGILANCE_START);
		manualVigilanceItem.addActionListener(new ManualVigilanceApplication(hostLocation.getLocation(), hostLocation.getRtpport(), false));
		manualVigilanceItem.setMnemonic(KeyEvent.VK_V);
		optionsMenu.add(manualVigilanceItem);

		JMenuItem programRouteItem = new JMenuItem(lang.OPTIONS_PROGRAM_ROUTE);
		programRouteItem.addActionListener(new ManualVigilanceApplication(hostLocation.getLocation(), hostLocation.getRtpport(), true));
		programRouteItem.setMnemonic(KeyEvent.VK_P);
		optionsMenu.add(programRouteItem);


		try {
			if (connectionType == ApplicationMode.STANDALONE_CLIENT
					&& (!controller.getConnection().isCamEnabled() || !hostLocation.isCamEnable())) {
				if (hostLocation.isCamEnable())
					ApplicationRunner.handleWarning(lang.CAMERA_NO_ENABLE_MESSAGE);
				manualVigilanceItem.setEnabled(false);
				showRealVideoItem.setEnabled(false);
				programRouteItem.setEnabled(false);
			}
		} catch (RemoteException e) {
			log.severe(e.getMessage());
			ApplicationRunner.handleAlert(e.getMessage());
			System.exit(-1);
		}

		menuBar.add(optionsMenu);


		/**
		 * Help menu option
		 */
		JMenu helpMenu = new JMenu(lang.HELP_MENU_NAME);
		helpMenu.setMnemonic(KeyEvent.VK_H);
		JMenuItem aboutMenuItem = new JMenuItem(lang.HELP_MENU_ABOUT);
		aboutMenuItem.addActionListener(new AboutApplication());
		aboutMenuItem.setMnemonic(KeyEvent.VK_A);
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);
		this.setJMenuBar(menuBar);



		/**
		 * TextArea loggin
		 */
		JPanel loggerPanel = new JPanel(new BorderLayout());

		logArea = new JTextArea(8,75);
		JScrollPane scrollPane = new JScrollPane(logArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		logArea.setLineWrap(false);
		logArea.setPreferredSize(new Dimension(100, 100));
		logArea.setEnabled(false);
		logArea.append("INFO: " + lang.MAIN_WINDOW_NAME + " " + (new Date()).toString() + "\n");
		scrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		loggerPanel.add(scrollPane,BorderLayout.CENTER);
		this.add(loggerPanel);

		/**
		 * Status Panel
		 */
		JPanel statusPanel = new JPanel(new BorderLayout());
		status = new JLabel();
		status.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		status.setText(lang.VIGILANCE_STOPPED_LABEL);
		statusPanel.add(status, BorderLayout.CENTER);
		this.add(statusPanel, BorderLayout.SOUTH);


		// Center on the screen of the computer
		this.pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((d.getWidth() - this.getWidth()) / 2);
		int y = (int) ((d.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);

		try {
			updateRunner = new Controller(controller.getConnection(),lang,status,logArea);
		} catch (RemoteException e) {
			log.severe(e.getMessage());
			ApplicationRunner.handleAlert(e.getMessage());
			System.exit(-1);
		}
		updateThread = new Thread(updateRunner);
		updateThread.start();


		this.setVisible(true);

	}



	/**
	 * is running any window video player
	 * @return boolean
	 */
	public static boolean isRunningPlayer() {
		return runningPlayer;
	}


	/**
	 * Set if is running a video player
	 * @param runningPlayer boolean
	 */
	public static void setRunningPlayer(boolean runningPlayer) {
		MainWindow.runningPlayer = runningPlayer;
	}


	/**
	 * Handles all application quit events.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	private class QuitApplication implements ActionListener {

		/**
		 * Quits the application when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {
			if (connectionType == ApplicationMode.STANDALONE_CLIENT){
				VideoController.stopVideoTransmitter();

				if (controller != null){
					try {
						controller.getConnection().stop();
					} catch (IOException e) {
						log.severe(e.getMessage());
						ApplicationRunner.handleAlert(e.getMessage());
						e.printStackTrace();
					}
				}

			}
			System.exit(0);
		}
	}


	private class ShowRealTimeVideoApplication implements ActionListener {

		private String hostLocation;
		private String rtpPort;
		ShowRealTimeVideoApplication (String hostLocation, String rtpPort){
			super();
			this.hostLocation = hostLocation;
			this.rtpPort = rtpPort;
		}

		public void actionPerformed(ActionEvent e) {


			if (runningPlayer){
				ApplicationRunner.handleWarning(lang.CLIENT_PLAYER_IN_USE_MESSAGE);
				return;
			}
			if (showRealTimeFrame == null) {
				showRealTimeFrame = new ShowRealTimeVideoWindow(lang,controller);
			}
			showRealTimeFrame.initialize();
			showRealTimeFrame.setVisible(true);
			startVideoReceiveAsync((PlayerWindow) showRealTimeFrame, hostLocation, rtpPort, true);
		}
	}

	private class ManualVigilanceApplication implements ActionListener {

		private String hostLocation;
		private String rtpPort;
		private ManualVigilanceWindow manualVigilanceFrame;
		private boolean programmingRoute;


		ManualVigilanceApplication (String hostLocation, String rtpPort, boolean programmingRoute){
			super();
			this.hostLocation = hostLocation;
			this.rtpPort = rtpPort;
			this.programmingRoute = programmingRoute;
		}

		public void actionPerformed(ActionEvent e) {

			if (runningPlayer){
				ApplicationRunner.handleWarning(lang.CLIENT_PLAYER_IN_USE_MESSAGE);
				return;
			}
			try {
				if (controller.getConnection().isStarted()){
					ApplicationRunner.handleWarning(lang.VIGILANCIE_RUNNING_MESSAGE);
					return;
				}
				if (controller.takeRobotControl() != 0) {
					if (manualVigilanceFrame == null) {
						manualVigilanceFrame = new ManualVigilanceWindow(lang, controller, programmingRoute);
					}
					manualVigilanceFrame.initialize();
					manualVigilanceFrame.setVisible(true);
					startVideoReceiveAsync((PlayerWindow) manualVigilanceFrame, hostLocation, rtpPort, false);
				}
				else
					ApplicationRunner.handleAlert(lang.MOVE_CONTROL_LOCKED);
			}catch (RemoteException ex){
				log.severe(ex.getMessage());
				ApplicationRunner.handleAlert(ex.getMessage());
				System.exit(-1);
			}catch (IOException ex){
				log.severe(ex.getMessage());
				ApplicationRunner.handleAlert(ex.getMessage());
				System.exit(-1);
			}
		}
	}

	private void startVideoReceiveAsync(final PlayerWindow playerWindow,
			final String hostLocation,
			final String rtpPort,
			final boolean enableRobotCam) {
		Thread videoStartThread = new Thread(new Runnable() {
			public void run() {
				try {
					if (enableRobotCam) {
						controller.getConnection().enableRobotCam(true);
					}
					VideoSession receive = createVideoSession(playerWindow, hostLocation, rtpPort);
					playerWindow.setVideoSession(receive);

					if (!initializeVideoSession(receive)) {
						throw new IOException("Failed to initialize the video session.");
					}
				} catch (IllegalArgumentException e) {
					handleVideoStartupFailure(playerWindow, e);
				} catch (RemoteException e) {
					handleVideoStartupFailure(playerWindow, e);
				} catch (IOException e) {
					handleVideoStartupFailure(playerWindow, e);
				}
			}
		}, "pfc-video-start");
		videoStartThread.start();
	}

	private VideoSession createVideoSession(PlayerWindow playerWindow,
			String hostLocation,
			String rtpPort) {
		if (connectionType == ApplicationMode.STANDALONE_CLIENT) {
			return new LocalVideoReceive(playerWindow, selectedVideoSource);
		}
		return new VideoReceive(hostLocation,rtpPort, playerWindow);
	}

	private boolean initializeVideoSession(VideoSession videoSession) throws IOException {
		if (videoSession instanceof LocalVideoReceive) {
			return ((LocalVideoReceive) videoSession).initialize();
		}
		return ((VideoReceive) videoSession).initialize();
	}

	private void handleVideoStartupFailure(final PlayerWindow playerWindow, final Exception e) {
		log.severe(e.getMessage());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				playerWindow.closeRTP();
				ApplicationRunner.handleAlert(e.getMessage());
			}
		});
	}

	/**
	 * Handles all application quit events.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	private class ConfigApplication implements ActionListener {



		/**
		 * Combo for the move sensor configuration
		 */
		private JComboBox moveSensorBox;

		/**
		 *  TextFiled Configure the period for the move scan
		 */
		private JTextField moveSensorPeriodScanField;

		/**
		 *  Configure the period for the termal scan horizontal orientation
		 */
		private JTextField moveSensorPeriodOrientationField;


		/**
		 * Combo for the termal sensor configuration
		 */
		private JComboBox termalSensorBox;

		/**
		 *  Configure the period for the termal scan
		 */
		private JTextField termalSensorPeriodScanField;

		/**
		 *  Configure the period for the termal scan horizontal orientation
		 */
		private JTextField termalSensorPeriodOrientationField;

		/**
		 * Field for the time heartbeat period
		 */
		private JTextField heartbeatTimeField ;

		/**
		 * Enable the save video option when alarm detected 
		 */
		private JCheckBox saveVideoAlarmCheck;


		/**
		 * Enable the save video all the time 
		 */
		private JCheckBox saveVideoAllTimeCheck;

		/**
		 *  Configure the time of video
		 */
		private JTextField saveVideoPeriodField;

		/**
		 *  Time for scan alarms at the server
		 */
		private JTextField checkAlarmsPeriodField;


		/**
		 * Enable the auto movement/manual movement for the robot 
		 */
		private JComboBox controlMoveTypeBox;

		/**
		 * Selection for the programmed route 
		 */
		private JComboBox routeSelBox;

		/**
		 *  Enable Send Emal At Alarm
		 */
		private JCheckBox enableEmailBox;

		/**
		 * Email Receivers Field
		 */
		private JTextField emailReceiversField;
		
		/**
		 *  Configure the distance for proximity alarm
		 */
		private JTextField proxDistanceAlarmField;
		
		/**
		 *  Configure the low voltage for alarm
		 */
		private JTextField lowVoltageField;

		/**
		 *  Configure the voltage for solar charge
		 */
		private JTextField chargeVoltageField;
		
		/**
		 *  To kill the active scans by Stop operation
		 */
		private JCheckBox killScansByStopBox;

		private JComboBox videoSourceTypeBox;

		private JComboBox videoDeviceBox;

		private JTextField videoStreamUrlField;
		

		private Configuration conf;


		public void actionPerformed(ActionEvent ae) {

			if (configFrame == null) {

				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints constraints = new GridBagConstraints();

				configFrame = new JFrame();
				configFrame.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent arg0) {configFrame.setVisible(false);}});
				configFrame.setName(lang.CONFIG_WINDOW_NAME);
				configFrame.setTitle(lang.CONFIG_WINDOW_NAME);
				configFrame.setResizable(false);

				JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				configPanel.setBorder(BorderFactory.createTitledBorder(lang.FILE_MENU_CONFIG));
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(configPanel, constraints);
				configPanel.setLayout(gridbag);

				
				/* Scan configuration*/
				JPanel scanConfPanel = new JPanel();
				scanConfPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_SCAN_CONF_PANEL_NAME));
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(scanConfPanel, constraints);
				scanConfPanel.setLayout(gridbag);
				
				
				/* Kill Scan configuration */
				JLabel killScansLabel = new JLabel(lang.KILL_SCAN_BY_OPERATION_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(killScansLabel, constraints);
				scanConfPanel.add(killScansLabel);

				killScansByStopBox = new JCheckBox();
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(killScansByStopBox, constraints);
				scanConfPanel.add(killScansByStopBox);
				
				
				/* Move sensor configuration */
				/* Move Sensor Configuration Panel */
				JPanel moveSensorPanel = new JPanel();
				moveSensorPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_MOVE_SENSOR_PANEL_NAME));
				moveSensorPanel.setLayout(gridbag);

				/* Move Sensor Enabled  */
				moveSensorBox = new JComboBox(lang.ENABLE_SELECTION);
				moveSensorBox.setName(lang.CONFIG_MOVE_SENSOR_PANEL_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(moveSensorBox, constraints);
				moveSensorPanel.add(moveSensorBox);

				/* Move Sensor Period Label and TextFiled */
				JLabel moveSensorPeriodLabel = new JLabel(lang.SCAN_PERIOD_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(moveSensorPeriodLabel, constraints);
				moveSensorPanel.add(moveSensorPeriodLabel);

				moveSensorPeriodScanField = new JTextField(5);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(moveSensorPeriodScanField, constraints);
				moveSensorPanel.add(moveSensorPeriodScanField);


				JLabel moveSensorPeriodOrientationLabel = new JLabel(lang.SCAN_ORIENTATION_PERIOD_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(moveSensorPeriodOrientationLabel, constraints);
				moveSensorPanel.add(moveSensorPeriodOrientationLabel);

				moveSensorPeriodOrientationField = new JTextField(5);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(moveSensorPeriodOrientationField, constraints);
				moveSensorPanel.add(moveSensorPeriodOrientationField);



				/* Add The Move Sensor Panel to the Config Panel */
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(moveSensorPanel, constraints);
				scanConfPanel.add(moveSensorPanel);

				/* Termal sensor configuration */

				/* Termal Sensor Configuration Panel */
				JPanel termalSensorPanel = new JPanel();
				termalSensorPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_TERMAL_SENSOR_PANEL_NAME));
				termalSensorPanel.setLayout(gridbag);

				/* Termal Sensor Enabled  */
				termalSensorBox = new JComboBox(lang.ENABLE_SELECTION);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(termalSensorBox, constraints);
				termalSensorPanel.add(termalSensorBox);

				/* Termal Sensor Period Label and TextFiled */
				JLabel termalSensorPeriodLabel = new JLabel(lang.SCAN_PERIOD_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(termalSensorPeriodLabel, constraints);
				termalSensorPanel.add(termalSensorPeriodLabel);

				termalSensorPeriodScanField = new JTextField(5);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(termalSensorPeriodScanField, constraints);
				termalSensorPanel.add(termalSensorPeriodScanField);


				JLabel termalSensorPeriodOrientationLabel = new JLabel(lang.SCAN_ORIENTATION_PERIOD_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(termalSensorPeriodOrientationLabel, constraints);
				termalSensorPanel.add(termalSensorPeriodOrientationLabel);

				termalSensorPeriodOrientationField = new JTextField(5);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(termalSensorPeriodOrientationField, constraints);
				termalSensorPanel.add(termalSensorPeriodOrientationField);

				/* Add The Termal Sensor Panel to the Config Panel */
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(termalSensorPanel, constraints);
				scanConfPanel.add(termalSensorPanel);
				
				/* Add the scan configuration panel to the config panel*/
				configPanel.add(scanConfPanel);


				/* Mail Configuration */
				JPanel emailPanel = new JPanel();
				emailPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_EMAIL_PANEL_NAME));
				emailPanel.setLayout(gridbag);


				JLabel enableEmailLabel = new JLabel(lang.ENABLE_EMAIL_SEND_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(enableEmailLabel, constraints);
				emailPanel.add(enableEmailLabel);

				enableEmailBox = new JCheckBox();
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(enableEmailBox, constraints);
				emailPanel.add(enableEmailBox);

				JLabel emailReceiversLabel = new JLabel(lang.EMAIL_RECEIVERS_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(emailReceiversLabel, constraints);
				emailPanel.add(emailReceiversLabel);

				emailReceiversField = new JTextField(25);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(emailReceiversField, constraints);
				emailPanel.add(emailReceiversField);
				
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(emailPanel, constraints);
				configPanel.add(emailPanel);


				/* Heartbeat configuration */

				/* Heartbeat Configuration Panel */
				JPanel heartbeatPanel = new JPanel();
				heartbeatPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_HEARTBEAT_PANEL_NAME));
				heartbeatPanel.setLayout(gridbag);

				JLabel heartbeatTimeLabel = new JLabel(lang.CONFIG_HEARTBEAT_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(heartbeatTimeLabel, constraints);
				heartbeatPanel.add(heartbeatTimeLabel);

				heartbeatTimeField = new JTextField(8);
				heartbeatTimeField.setName(lang.CONFIG_HEARTBEAT_LABEL);
				heartbeatTimeField.setToolTipText(lang.CONFIG_HEARTBEAT_TOOL_TIP);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(heartbeatTimeField, constraints);
				heartbeatPanel.add(heartbeatTimeField);

				/* Add The HeartBeat Panel to the Config Panel */
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(heartbeatPanel, constraints);
				configPanel.add(heartbeatPanel);

				/* Video Alarm Configuration */
				JPanel videoPanel = new JPanel();
				videoPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_VIDEO_PANEL_NAME));
				videoPanel.setLayout(gridbag);

				saveVideoAllTimeCheck = new JCheckBox(lang.CONFIG_SAVE_VIDEO_ALL_TIME_ENABLE);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(saveVideoAllTimeCheck, constraints);
				videoPanel.add(saveVideoAllTimeCheck);

				saveVideoAlarmCheck = new JCheckBox(lang.CONFIG_SAVE_VIDEO_ENABLE);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(saveVideoAlarmCheck, constraints);
				videoPanel.add(saveVideoAlarmCheck);

				/* video Time Value for saving video */

				JLabel videoTimeLabel = new JLabel(lang.CONFIG_SAVE_VIDEO_TIME_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(videoTimeLabel, constraints);
				videoPanel.add(videoTimeLabel);

				saveVideoPeriodField = new JTextField(8);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(saveVideoPeriodField, constraints);
				videoPanel.add(saveVideoPeriodField);

				if (connectionType == ApplicationMode.STANDALONE_CLIENT) {
					JLabel videoSourceLabel = new JLabel(lang.SERVER_VIDEO_SOURCE_LABEL);
					constraints.gridwidth = 1;
					constraints.anchor = GridBagConstraints.EAST;
					gridbag.setConstraints(videoSourceLabel, constraints);
					videoPanel.add(videoSourceLabel);

					videoSourceTypeBox = new JComboBox(new String[] {
							lang.VIDEO_SOURCE_LOCAL_OPTION,
							lang.VIDEO_SOURCE_STREAM_OPTION
					});
					videoSourceTypeBox.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							updateStandaloneVideoSourceControls();
						}
					});
					constraints.gridwidth = GridBagConstraints.REMAINDER;
					constraints.anchor = GridBagConstraints.WEST;
					gridbag.setConstraints(videoSourceTypeBox, constraints);
					videoPanel.add(videoSourceTypeBox);

					JLabel deviceLabel = new JLabel(lang.DEVICE + ":");
					constraints.gridwidth = 1;
					constraints.anchor = GridBagConstraints.EAST;
					gridbag.setConstraints(deviceLabel, constraints);
					videoPanel.add(deviceLabel);

					Vector<String> videoDevices = SelectDevice.lookUpDevices();
					if (videoDevices == null) {
						videoDevices = new Vector<String>();
					}
					if (videoDevices.isEmpty()) {
						videoDevices.add("No device found");
					}
					videoDeviceBox = new JComboBox(videoDevices);
					videoDeviceBox.setToolTipText(lang.SERVER_VIDEO_DEVICE_LABEL_TOOL_TIP);
					constraints.gridwidth = GridBagConstraints.REMAINDER;
					constraints.anchor = GridBagConstraints.WEST;
					gridbag.setConstraints(videoDeviceBox, constraints);
					videoPanel.add(videoDeviceBox);

					JLabel streamUrlLabel = new JLabel(lang.SERVER_VIDEO_STREAM_URL_LABEL);
					constraints.gridwidth = 1;
					constraints.anchor = GridBagConstraints.EAST;
					gridbag.setConstraints(streamUrlLabel, constraints);
					videoPanel.add(streamUrlLabel);

					videoStreamUrlField = new JTextField(25);
					videoStreamUrlField.setToolTipText(lang.SERVER_VIDEO_STREAM_URL_TOOL_TIP);
					constraints.gridwidth = GridBagConstraints.REMAINDER;
					constraints.anchor = GridBagConstraints.WEST;
					gridbag.setConstraints(videoStreamUrlField, constraints);
					videoPanel.add(videoStreamUrlField);
				}

				/* Add The Video Panel to the Config Panel */
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(videoPanel, constraints);
				configPanel.add(videoPanel);

				try {
					if (!controller.getConnection().isCamEnabled()) {
						saveVideoAlarmCheck.setEnabled(false);
						saveVideoAlarmCheck.setSelected(false);
						saveVideoAllTimeCheck.setEnabled(false);
						saveVideoAllTimeCheck.setSelected(false);
						saveVideoPeriodField.setEnabled(false);
						videoTimeLabel.setEnabled(false);
						if (connectionType == ApplicationMode.STANDALONE_CLIENT) {
							setStandaloneVideoSourceControlsEnabled(false);
						}
					}
					else {
						saveVideoAlarmCheck.addChangeListener(new ChangeListenerVideoConfig());
						saveVideoAllTimeCheck.addChangeListener(new ChangeListenerVideoConfig());
						if (connectionType == ApplicationMode.STANDALONE_CLIENT) {
							setStandaloneVideoSourceControlsEnabled(true);
						}
					}
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}


				/* Configure Kind of move control */

				/* Control Move Panel*/
				JPanel moveContolPanel = new JPanel();
				moveContolPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_CONTROL_MOVE_PANEL_NAME));
				moveContolPanel.setLayout(gridbag);

				controlMoveTypeBox = new JComboBox(lang.MOVE_CONTROL_KIND_SELECTION);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(controlMoveTypeBox, constraints);
				moveContolPanel.add(controlMoveTypeBox);

				JLabel routeSelLabel = new JLabel(lang.ROUTES_SEL_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(routeSelLabel, constraints);
				moveContolPanel.add(routeSelLabel);

				routeSelBox = new JComboBox();
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(routeSelBox, constraints);
				moveContolPanel.add(routeSelBox);
				
				JLabel proximityLabel = new JLabel(lang.PROX_DISTANCE_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(proximityLabel, constraints);
				moveContolPanel.add(proximityLabel);
				
				proxDistanceAlarmField = new JTextField(5);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(proxDistanceAlarmField, constraints);
				moveContolPanel.add(proxDistanceAlarmField);

				/* Add The Move Control Panel to the Config Panel */
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(moveContolPanel, constraints);
				configPanel.add(moveContolPanel);
				
				
				/* Mail Configuration */
				JPanel bateryPanel = new JPanel();
				bateryPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_BATERY_PANEL_NAME));
				bateryPanel.setLayout(gridbag);
				
				JLabel lowVoltageLabel = new JLabel(lang.LOW_BATERY_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(lowVoltageLabel, constraints);
				bateryPanel.add(lowVoltageLabel);
				
				lowVoltageField = new JTextField(5);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(lowVoltageField, constraints);
				bateryPanel.add(lowVoltageField);
						
				JLabel chargeVoltageLabel = new JLabel(lang.CHARGE_BATERY_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(chargeVoltageLabel, constraints);
				bateryPanel.add(chargeVoltageLabel);
				
				chargeVoltageField = new JTextField(5);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(chargeVoltageField, constraints);
				bateryPanel.add(chargeVoltageField);
				

				/* Add The Move Control Panel to the Config Panel */
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(bateryPanel, constraints);
				configPanel.add(bateryPanel);
				
				

				/* Configure Period for Alarm Check */

				/* Check Alarm Panel*/
				JPanel alarmCheckPanel = new JPanel();
				alarmCheckPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_CHECK_ALARM_PANEL_NAME));
				alarmCheckPanel.setLayout(gridbag);

				JLabel checkAlarmLabel = new JLabel(lang.SCAN_PERIOD_LABEL);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(videoTimeLabel, constraints);
				alarmCheckPanel.add(checkAlarmLabel);

				checkAlarmsPeriodField = new JTextField(8);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(checkAlarmsPeriodField, constraints);
				alarmCheckPanel.add(checkAlarmsPeriodField);


				/* Add The Check Alarm Panel to the Config Panel */
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(alarmCheckPanel, constraints);
				configPanel.add(alarmCheckPanel);


				/* Configure Buttons */

				JPanel configButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				configButtonsPanel.setLayout(gridbag);

				/* add the savebutton */
				JButton saveButton = new JButton(lang.SAVE);
				constraints.gridwidth = 1; 
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(saveButton, constraints);
				saveButton.addActionListener(new SaveConfig());
				configButtonsPanel.add(saveButton);

				/* add the loadbutton */
				JButton loadButton = new JButton(lang.LOAD);
				constraints.gridwidth = 1; 
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(loadButton, constraints);
				loadButton.addActionListener(new LoadConfig());
				configButtonsPanel.add(loadButton);

				/* add the cancel button */
				JButton closeButton = new JButton(lang.CLOSE);
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(closeButton, constraints);
				closeButton.addActionListener(new CancelConfig());
				configButtonsPanel.add(closeButton);

				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(configButtonsPanel, constraints);
				configPanel.add(configButtonsPanel);

				configFrame.add(configPanel);
				configFrame.pack();

				// Center on the screen of the computer
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				int x = (int) ((d.getWidth() - configFrame.getWidth()) / 2);
				int y = (int) ((d.getHeight() - configFrame.getHeight()) / 2);
				configFrame.setLocation(x, y);
			}

			loadConfiguration();
			configFrame.setVisible(true);

		}


		private class ChangeListenerVideoConfig implements ChangeListener{


			public void stateChanged(ChangeEvent arg0) {
				if (saveVideoAlarmCheck.isEnabled() && saveVideoAlarmCheck.isSelected()){
					saveVideoAllTimeCheck.setEnabled(false);
					saveVideoAllTimeCheck.setSelected(false);
				}
				else if (saveVideoAllTimeCheck.isEnabled() && saveVideoAllTimeCheck.isSelected()){
					saveVideoAlarmCheck.setEnabled(false);
					saveVideoAlarmCheck.setSelected(false);
					saveVideoPeriodField.setEditable(false);
				}
				else if (saveVideoAllTimeCheck.isEnabled()) {
					saveVideoAllTimeCheck.setEnabled(true);
					saveVideoAlarmCheck.setEnabled(true);
					saveVideoPeriodField.setEditable(true);
				}

			}}

		private void loadConfiguration() {
			try {
				conf = controller.getConnection().getConf();
				SavedConfiguration properties = SavedConfiguration.getSavedConfiguration();
				String emailReceivers = "";

				if (controller.getConnection().isCamEnabled()) {
					/* Load the Save Video Option */
					saveVideoAlarmCheck.setSelected(conf.isEnableSaveVideoAlarm());
					if (conf.isEnableSaveVideoAlarm())
						saveVideoPeriodField.setText(String.valueOf(conf.getTimeSaveVideo() / 1000));
					else
						saveVideoPeriodField.setEditable(false);

					saveVideoAllTimeCheck.setSelected(conf.isEnableSaveVideoAllTime());
				}

				if (connectionType == ApplicationMode.STANDALONE_CLIENT && videoSourceTypeBox != null) {
					String sourceType = properties.getParameter(SavedConfiguration.VIDEO_SOURCE_TYPE);
					if ("stream".equals(sourceType)) {
						videoSourceTypeBox.setSelectedItem(lang.VIDEO_SOURCE_STREAM_OPTION);
					}
					else {
						videoSourceTypeBox.setSelectedItem(lang.VIDEO_SOURCE_LOCAL_OPTION);
					}

					String videoDevice = properties.getParameter(SavedConfiguration.VIDEO_DEVICE);
					if (videoDevice != null && videoDeviceBox != null) {
						videoDeviceBox.setSelectedItem(videoDevice);
					}

					String streamUrl = properties.getParameter(SavedConfiguration.VIDEO_STREAM_URL);
					videoStreamUrlField.setText(streamUrl == null ? "" : streamUrl);
					updateStandaloneVideoSourceControls();
				}

				/* Load Move Control Option */
				if (conf.isEnableRouteMovement())
					controlMoveTypeBox.setSelectedItem(lang.ROUTE_MOVE_NAME);
				else if (conf.isEnableFollowLinesMovement())
					controlMoveTypeBox.setSelectedItem(lang.FOLLOW_LINES_MOVE_NAME);
				else if (conf.isEnableStaticMovement())
					controlMoveTypeBox.setSelectedItem(lang.STATIC_MOVE_NAME);

				try {
					String[] routes = controller.getConnection().getRouteNames();
					routeSelBox.removeAllItems();
					for (int i = 0; i < routes.length; i++){
						routeSelBox.addItem((String) routes[i]);
					}
					if (conf.isEnableRouteMovement()) {
						String activeRoute = conf.getActiveRoute();
						if (activeRoute != null && activeRoute.compareTo("") != 0) 
							routeSelBox.setSelectedItem(conf.getActiveRoute());
					}
				} catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
				}
				
				proxDistanceAlarmField.setText(String.valueOf(conf.getProximityDistanceAlarm()));
				lowVoltageField.setText(String.valueOf(conf.getLowVoltageValueAlarm()));
				chargeVoltageField.setText(String.valueOf(conf.getChargeVoltageValue()));
				killScansByStopBox.setSelected(conf.getKillsScansByStop());
				
				/* Load the move sensor selection */
				if (conf.isEnableMovementSensor()) { 
					moveSensorBox.setSelectedItem(lang.ENABLE);
					moveSensorPeriodScanField.setText(String.valueOf(conf.getPeriodMovementSensor() / 1000));
					moveSensorPeriodOrientationField.setText(String.valueOf(conf.getPeriodOrientationMovementSensor() / 1000));
				}
				else {
					moveSensorBox.setSelectedItem(lang.DISABLE);
				}

				/* Load the termal sensor selection */
				if (conf.isEnableTermalSensor()) { 
					termalSensorBox.setSelectedItem(lang.ENABLE);
					termalSensorPeriodScanField.setText(String.valueOf(conf.getPeriodTermalSensor() / 1000));
					termalSensorPeriodOrientationField.setText(String.valueOf(conf.getPeriodOrientationTermalSensor() / 1000));
				}
				else {
					termalSensorBox.setSelectedItem(lang.DISABLE);
				}

				/* Load the heartbeat period */
				heartbeatTimeField.setText(String.valueOf(String.valueOf(conf.getPeriodHeartbeat() / 1000)));

				/* Load the client alarm update */
				checkAlarmsPeriodField.setText(String.valueOf(String.valueOf(updateRunner.getUpdateAlarmTime() / 1000)));

				if (conf.isEnableEmail()) {
					enableEmailBox.setSelected(true);
					for (int i  = 0; i < conf.getEmailReceivers().length ; i++){
						if (i != 0 && i != conf.getEmailReceivers().length)
							emailReceivers = emailReceivers + ";" + conf.getEmailReceivers()[i];
						else 
							emailReceivers = emailReceivers + conf.getEmailReceivers()[i];
					}
					emailReceiversField.setText(emailReceivers);
				}
				else 
					enableEmailBox.setSelected(false);

				logArea.append("INFO: " +  lang.LOAD_CONF_MESSAGE + ((Date)new Date()).toString() + "\n");

			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
			}
		}


		/**
		 * Handles Load Button Event Action.
		 *
		 * @author Rodrigo Jimenez
		 * @version 1.0
		 */
		private class LoadConfig implements ActionListener {

			public void actionPerformed(ActionEvent arg0) {
				loadConfiguration();
			}
		}


		/**
		 * Handles Save Button Event Action.
		 *
		 * @author Rodrigo Jimenez
		 * @version 1.0
		 */
		private class SaveConfig implements ActionListener {

			/**
			 * Save the configuration when invoked.
			 *
			 * @param ae The event triggering the quit operation.
			 */
			public void actionPerformed(ActionEvent ae) {
				Configuration newConf = new Configuration();
				try {

					newConf = (Configuration) conf.clone();

					if (controller.getConnection().isCamEnabled()) { 
						/* Save the Save Video Option */
						if (conf.isEnableSaveVideoAllTime() && controller.getConnection().isStarted() && saveVideoAlarmCheck.isSelected()) {
							ApplicationRunner.handleWarning(lang.ERROR_SAVE_VIEO_ALARM_MESSAGE);
							return;
						}
						else {
							newConf.setEnableSaveVideoAlarm(saveVideoAlarmCheck.isSelected());
							if (saveVideoAlarmCheck.isSelected()){
								newConf.setTimeSaveVideo(Long.parseLong(saveVideoPeriodField.getText()) * 1000);
							}
						}
						newConf.setEnableSaveVideoAllTime(saveVideoAllTimeCheck.isSelected());
					}

					if (connectionType == ApplicationMode.STANDALONE_CLIENT && videoSourceTypeBox != null) {
						applyStandaloneVideoSourceChanges();
					}

					/* Save Move Control Option */
					if (((String) controlMoveTypeBox.getSelectedItem()).compareTo(lang.FOLLOW_LINES_MOVE_NAME) == 0){
						newConf.setEnableFollowLinesMovement(true);
						newConf.setEnableRouteMovement(false);
						newConf.setEnableStaticMovement(false);
					}
					else if (((String) controlMoveTypeBox.getSelectedItem()).compareTo(lang.STATIC_MOVE_NAME) == 0){
						newConf.setEnableStaticMovement(true);
						newConf.setEnableRouteMovement(false);
						newConf.setEnableFollowLinesMovement(false);
					}
					else {
						newConf.setEnableStaticMovement(false);
						newConf.setEnableRouteMovement(true);
						newConf.setEnableFollowLinesMovement(false);
						if (routeSelBox.getItemCount() == 0) {
							ApplicationRunner.handleWarning(lang.ROUTES_NO_CREATED_MESSAGE);
							return;
						}
						newConf.setActiveRoute((String) routeSelBox.getSelectedItem());
					} 
					
					newConf.setProximityDistanceAlarm(Float.parseFloat(proxDistanceAlarmField.getText()));
					newConf.setLowVoltageValueAlarm(Float.parseFloat(lowVoltageField.getText()));
					if (newConf.getLowVoltageValueAlarm() > 15.0){
						throw new NumberFormatException("Low Voltage Max Value are 15 Volts");
					}
					
					newConf.setChargeVoltageValue(Float.parseFloat(chargeVoltageField.getText()));

					newConf.setKillsScansByStop( killScansByStopBox.isSelected());
					
					/* Save the move sensor selection */
					String sel = (String) moveSensorBox.getSelectedItem();
					if (sel.compareTo(lang.ENABLE) == 0) {
						newConf.setEnableMovementSensor(true);
						newConf.setPeriodMovementSensor(Long.parseLong((String) moveSensorPeriodScanField.getText()) * 1000);
						newConf.setPeriodOrientationMovementSensor(Long.parseLong((String) moveSensorPeriodOrientationField.getText()) * 1000);
						
					}
					else newConf.setEnableMovementSensor(false);

					/* Save the termal sensor selection */
					sel = (String) termalSensorBox.getSelectedItem();
					if (sel.compareTo(lang.ENABLE) == 0) {
						newConf.setEnableTermalSensor(true);
						if (termalSensorPeriodScanField.getText().compareTo("") != 0) {
							newConf.setPeriodTermalSensor(Long.parseLong((String) termalSensorPeriodScanField.getText()) * 1000);
							newConf.setPeriodOrientationTermalSensor(Long.parseLong((String) termalSensorPeriodOrientationField.getText()) * 1000);
						}
						else {
							newConf.setPeriodTermalSensor(0);
							newConf.setPeriodOrientationTermalSensor(0);
						}
					}
					else newConf.setEnableTermalSensor(false);

					/* Save the heartbeat period */
					newConf.setPeriodHeartbeat(Long.parseLong(heartbeatTimeField.getText()) * 1000);

					/* Save the client alarm update */
					updateRunner.setUpdateAlarmTime(Long.parseLong(checkAlarmsPeriodField.getText()) * 1000);
					
					/*Save the email configuration */
					if (enableEmailBox.isSelected()) {
						newConf.setEnableEmail(true);
						newConf.setEmailReceivers(emailReceiversField.getText().split(";"));
					}
					else newConf.setEnableEmail(false);
					

					/* Save the configuration to the server and to the robot */
					controller.getConnection().setConf(newConf);

					logArea.append("INFO: " + lang.SAVE_CONF_MESSAGE + ((Date)new Date()).toString() + "\n");
					
					configFrame.setVisible(false);

				}catch (RemoteException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (IOException e) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				} catch (NumberFormatException e){
					log.severe(lang.ERROR_CONFIG_VALUES_MESSAGE);
					ApplicationRunner.handleWarning(lang.ERROR_CONFIG_VALUES_MESSAGE);
				}
			}
		}

		private void setStandaloneVideoSourceControlsEnabled(boolean enabled) {
			if (videoSourceTypeBox != null) {
				videoSourceTypeBox.setEnabled(enabled);
			}
			if (videoDeviceBox != null) {
				videoDeviceBox.setEnabled(enabled);
			}
			if (videoStreamUrlField != null) {
				videoStreamUrlField.setEnabled(enabled);
			}
			if (enabled) {
				updateStandaloneVideoSourceControls();
			}
		}

		private void updateStandaloneVideoSourceControls() {
			if (videoSourceTypeBox == null) {
				return;
			}

			boolean streamSource = lang.VIDEO_SOURCE_STREAM_OPTION.equals(videoSourceTypeBox.getSelectedItem());
			boolean controlsEnabled = videoSourceTypeBox.isEnabled();
			boolean cameraAvailable = videoDeviceBox != null
					&& videoDeviceBox.getSelectedItem() != null
					&& !"No device found".equals(videoDeviceBox.getSelectedItem());

			if (videoDeviceBox != null) {
				videoDeviceBox.setEnabled(controlsEnabled && !streamSource && cameraAvailable);
			}
			if (videoStreamUrlField != null) {
				videoStreamUrlField.setEnabled(controlsEnabled && streamSource);
			}
		}

		private void applyStandaloneVideoSourceChanges() throws IOException, RemoteException {
			SavedConfiguration properties = SavedConfiguration.getSavedConfiguration();
			String oldSourceType = properties.getParameter(SavedConfiguration.VIDEO_SOURCE_TYPE);
			String oldVideoDevice = properties.getParameter(SavedConfiguration.VIDEO_DEVICE);
			String oldVideoStreamUrl = properties.getParameter(SavedConfiguration.VIDEO_STREAM_URL);

			String newSourceType = lang.VIDEO_SOURCE_STREAM_OPTION.equals(videoSourceTypeBox.getSelectedItem())
					? "stream"
					: "local";
			String newVideoDevice = videoDeviceBox == null || videoDeviceBox.getSelectedItem() == null
					? ""
					: (String) videoDeviceBox.getSelectedItem();
			String newVideoStreamUrl = videoStreamUrlField == null
					? ""
					: videoStreamUrlField.getText().trim();

			if ("stream".equals(newSourceType) && newVideoStreamUrl.length() == 0) {
				throw new IOException("The video stream URL is empty.");
			}

			if ("local".equals(newSourceType) && ("".equals(newVideoDevice) || "No device found".equals(newVideoDevice))) {
				throw new IOException("No local video device is available.");
			}

			properties.setParameter(SavedConfiguration.VIDEO_SOURCE_TYPE, newSourceType);
			properties.setParameter(SavedConfiguration.VIDEO_DEVICE, newVideoDevice);
			properties.setParameter(SavedConfiguration.VIDEO_STREAM_URL, newVideoStreamUrl);

			boolean sourceChanged =
					!safeEquals(oldSourceType, newSourceType)
					|| !safeEquals(oldVideoDevice, newVideoDevice)
					|| !safeEquals(oldVideoStreamUrl, newVideoStreamUrl);

			if (sourceChanged && controller.getConnection().isCamEnabled()) {
				String selectedSource = "stream".equals(newSourceType) ? newVideoStreamUrl : newVideoDevice;
				String rtpPort = properties.getParameter(SavedConfiguration.SERVER_RTP_PORT);
				VideoController.stopVideoTransmitter();
				new VideoController(selectedSource, rtpPort);
			}
		}

		private boolean safeEquals(String left, String right) {
			if (left == null) {
				return right == null;
			}
			return left.equals(right);
		}

		/**
		 * Handles all application quit events.
		 *
		 * @author Rodrigo Jimenez
		 * @version 1.0
		 */
		private class CancelConfig implements ActionListener {

			/**
			 * Quits the application when invoked.
			 *
			 * @param ae The event triggering the quit operation.
			 */
			public void actionPerformed(ActionEvent ae) {
				configFrame.setVisible(false);
			}
		}
	}


	/**
	 * Handles all application quit events.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	private class ScheduleApplication implements ActionListener, ItemListener, ChangeListener {


		/* Fields for the Schedule Configuration */

		/* Panels */
		private JPanel schedulePanel;
		private JPanel scheduleConfigPanel;
		private JPanel scheduleButtonsPanel;
		private JPanel scheduleStartPanel;
		private JPanel scheduleStopPanel;

		/* Schedule Types Declarations */
		private JCheckBox enableScheduleProgram;
		private JCheckBox enableOnceSchedule;
		private JCheckBox enableDiarySchedule;
		private JCheckBox enableWeeklySchedule;
		private JCheckBox enableMonthlySchedule;
		private JCheckBox enableAnualSchedule;


		/* Schedule Start Declarations */
		private JTextField hourStart;
		private JTextField minuteStart;
		private JComboBox weekDayStart;
		private JTextField monthDayStart;
		private JComboBox monthStart;
		private JTextField yearStart;

		/* Schedule Stop Declaration */
		private JTextField hourStop;
		private JTextField minuteStop;
		private JComboBox weekDayStop;
		private JTextField monthDayStop;
		private JComboBox monthStop;
		private JTextField yearStop;


		private ScheduleProgram scheduleProgram;

		/**
		 * Quits the application when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {

			if (scheduleFrame == null) {
				scheduleFrame = new JFrame();
				scheduleFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
				scheduleFrame.setName(lang.SCHEDULE_WINDOW_NAME);
				scheduleFrame.setTitle(lang.SCHEDULE_WINDOW_NAME);
				scheduleFrame.setResizable(false);

				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.insets = new Insets(2, 2, 2, 2);
				constraints.anchor = GridBagConstraints.WEST;
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row


				schedulePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				schedulePanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_SCHEDULE_TITLE));
				schedulePanel.setLayout(gridbag);

				enableScheduleProgram = new  JCheckBox(lang.SCHEDULE_ENABLE_TEXT);
				enableScheduleProgram.setName(lang.SCHEDULE_ENABLE_NAME);
				enableScheduleProgram.addChangeListener(this);
				gridbag.setConstraints(enableScheduleProgram, constraints);
				schedulePanel.add(enableScheduleProgram);

				enableOnceSchedule = new  JCheckBox(lang.ONCE_ENABLE_TEXT);
				enableOnceSchedule.setName(lang.ONCE_ENABLE_NAME);
				enableOnceSchedule.addChangeListener(this);
				gridbag.setConstraints(enableOnceSchedule, constraints);
				schedulePanel.add(enableOnceSchedule);

				enableDiarySchedule = new  JCheckBox(lang.DIARY_ENABLE_TEXT);
				enableDiarySchedule.setName(lang.DIARY_ENABLE_NAME);
				enableDiarySchedule.addChangeListener(this);
				gridbag.setConstraints(enableDiarySchedule, constraints);
				schedulePanel.add(enableDiarySchedule);

				enableWeeklySchedule = new  JCheckBox(lang.WEEKLY_ENABLE_TEXT);
				enableWeeklySchedule.setName(lang.WEEKLY_ENABLE_NAME);
				enableWeeklySchedule.addChangeListener(this);
				gridbag.setConstraints(enableWeeklySchedule, constraints);
				schedulePanel.add(enableWeeklySchedule);


				enableMonthlySchedule = new  JCheckBox(lang.MONTHLY_ENABLE_TEXT);
				enableMonthlySchedule.setName(lang.MONTHLY_ENABLE_NAME);
				enableMonthlySchedule.addChangeListener(this);
				gridbag.setConstraints(enableMonthlySchedule, constraints);
				schedulePanel.add(enableMonthlySchedule);

				enableAnualSchedule = new  JCheckBox(lang.ANUAL_ENABLE_TEXT);
				enableAnualSchedule.setName(lang.ANUAL_ENABLE_NAME);
				enableAnualSchedule.addChangeListener(this);
				gridbag.setConstraints(enableAnualSchedule, constraints);
				schedulePanel.add(enableAnualSchedule);

				scheduleFrame.add(schedulePanel);

				scheduleConfigPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				scheduleConfigPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_SCHEDULE_PANEL_TITLE));
				scheduleConfigPanel.setLayout(gridbag);

				/* Add the schedule configuration for a specific program */


				/* Starts Configuration */

				/* Declare Starts Panel */
				scheduleStartPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				scheduleStartPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_SCHEDULE_START_PANEL_TITLE));
				scheduleStartPanel.setLayout(gridbag);

				/* Hour Start Label */
				JLabel hourStartLabel = new JLabel(lang.SCHEDULE_HOUR_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(hourStartLabel, constraints);
				scheduleStartPanel.add(hourStartLabel);

				/* Hour Start TextField */
				hourStart = new JTextField(5);
				hourStart.setName(lang.SCHEDULE_HOUR_NAME);
				constraints.gridwidth = 2;
				gridbag.setConstraints(hourStart, constraints);
				scheduleStartPanel.add(hourStart);

				/* Minute Start Label */
				JLabel minuteLabel = new JLabel(lang.SCHEDULE_MINUTE_NAME);
				constraints.gridwidth = 3;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(minuteLabel, constraints);
				scheduleStartPanel.add(minuteLabel);


				/* Minute Start TextField */
				minuteStart = new JTextField(5);
				minuteStart.setName(lang.SCHEDULE_MINUTE_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(minuteStart, constraints);
				scheduleStartPanel.add(minuteStart);


				/* WeekDay Label */
				JLabel weekDayLabel = new JLabel(lang.SCHEDULE_WEEKDAY_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(weekDayLabel, constraints);
				scheduleStartPanel.add(weekDayLabel);


				/* WeekDay TextField */
				weekDayStart = new JComboBox(lang.WEEKDAYS);
				weekDayStart.setName(lang.SCHEDULE_WEEKDAY_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(weekDayStart, constraints);
				scheduleStartPanel.add(weekDayStart);

				/* MonthDay Label */
				JLabel monthDayLabel = new JLabel(lang.SCHEDULE_MONTHDAY_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(monthDayLabel, constraints);
				scheduleStartPanel.add(monthDayLabel);

				/* MontDay TextField */
				monthDayStart = new JTextField(5);
				monthDayStart.setName(lang.SCHEDULE_MONTHDAY_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(monthDayStart, constraints);
				scheduleStartPanel.add(monthDayStart);

				/* Month Label */
				JLabel monthLabel = new JLabel(lang.SCHEDULE_MONTH_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(monthLabel, constraints);
				scheduleStartPanel.add(monthLabel);

				/* Month Combo */
				monthStart = new JComboBox(lang.MONTHS);
				monthStart.setName(lang.SCHEDULE_MONTH_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(monthStart, constraints);
				scheduleStartPanel.add(monthStart);

				/* Year Label */
				JLabel yearLabel = new JLabel(lang.SCHEDULE_YEAR_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(yearLabel,  constraints);
				scheduleStartPanel.add(yearLabel);

				/* Year TexField*/
				yearStart = new JTextField(5);
				yearStart.setName(lang.SCHEDULE_YEAR_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(yearStart, constraints);
				scheduleStartPanel.add(yearStart);

				/* Add scheduleStartPanel To scheduleConfigPanel */
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.CENTER;
				scheduleConfigPanel.add(scheduleStartPanel);

				/* Stops Configuration */

				/* Declare Stop Panel */
				scheduleStopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				scheduleStopPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_SCHEDULE_STOP_PANEL_TITLE));
				scheduleStopPanel.setLayout(gridbag);


				/* Hour Stop Label */
				JLabel hourStopLabel = new JLabel(lang.SCHEDULE_HOUR_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(hourStopLabel, constraints);
				scheduleStopPanel.add(hourStopLabel);

				/* Hour Stop TextField */ 
				hourStop = new JTextField(5);
				hourStop.setName(lang.SCHEDULE_HOUR_NAME);
				constraints.gridwidth = 2;
				gridbag.setConstraints(hourStop, constraints);
				scheduleStopPanel.add(hourStop);

				/* Minute Stop Label */
				JLabel minuteStopLabel = new JLabel(lang.SCHEDULE_MINUTE_NAME);
				constraints.gridwidth = 3;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(minuteStopLabel, constraints);
				scheduleStopPanel.add(minuteStopLabel);

				/* Minute Stop TextField */
				minuteStop = new JTextField(5);
				minuteStop.setName(lang.SCHEDULE_MINUTE_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(minuteStop, constraints);
				scheduleStopPanel.add(minuteStop);


				/* WeekDay Label */
				weekDayLabel = new JLabel(lang.SCHEDULE_WEEKDAY_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(weekDayLabel, constraints);
				scheduleStopPanel.add(weekDayLabel);


				/* WeekDay TextField */
				weekDayStop = new JComboBox(lang.WEEKDAYS);
				weekDayStop.setName(lang.SCHEDULE_WEEKDAY_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(weekDayStop, constraints);
				scheduleStopPanel.add(weekDayStop);

				/* MonthDay Label */
				monthDayLabel = new JLabel(lang.SCHEDULE_MONTHDAY_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(monthDayLabel, constraints);
				scheduleStopPanel.add(monthDayLabel);

				/* MontDay TextField */
				monthDayStop = new JTextField(5);
				monthDayStop.setName(lang.SCHEDULE_MONTHDAY_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(monthDayStop, constraints);
				scheduleStopPanel.add(monthDayStop);

				/* Month Label */
				monthLabel = new JLabel(lang.SCHEDULE_MONTH_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(monthLabel, constraints);
				scheduleStopPanel.add(monthLabel);

				/* Month Combo */
				monthStop = new JComboBox(lang.MONTHS);
				monthStop.setName(lang.SCHEDULE_MONTH_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(monthStop, constraints);
				scheduleStopPanel.add(monthStop);

				/* Year Label */
				yearLabel = new JLabel(lang.SCHEDULE_YEAR_NAME);
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(yearLabel,  constraints);
				scheduleStopPanel.add(yearLabel);

				/* Year TexField*/
				yearStop = new JTextField(5);
				yearStop.setName(lang.SCHEDULE_YEAR_NAME);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(yearStop, constraints);
				scheduleStopPanel.add(yearStop);

				/* Add scheduleStartPanel To scheduleConfigPanel */
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.CENTER;
				scheduleConfigPanel.add(scheduleStopPanel);


				constraints.anchor = GridBagConstraints.CENTER;
				constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
				schedulePanel.add(scheduleConfigPanel, constraints);

				scheduleButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				scheduleButtonsPanel.setLayout(gridbag);


				/* Save button */
				JButton saveButton = new JButton(lang.SAVE);
				saveButton.addActionListener(new SaveConfig());
				constraints.gridwidth = 1;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(saveButton, constraints);
				scheduleButtonsPanel.add(saveButton);


				/* Load button */
				JButton loadButton = new JButton(lang.LOAD);
				loadButton.addActionListener(new LoadConfig());
				constraints.gridwidth = 2;
				constraints.anchor = GridBagConstraints.EAST;
				gridbag.setConstraints(loadButton, constraints);
				scheduleButtonsPanel.add(loadButton);

				/* Close button */
				JButton closeButton = new JButton(lang.CLOSE);
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.WEST;
				gridbag.setConstraints(closeButton, constraints);
				closeButton.addActionListener(new CloseConfig());
				scheduleButtonsPanel.add(closeButton);

				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.anchor = GridBagConstraints.CENTER;
				gridbag.setConstraints(scheduleButtonsPanel, constraints);

				schedulePanel.add(scheduleButtonsPanel);
				scheduleFrame.pack();

				// Center on the screen of the computer
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				int x = (int) ((d.getWidth() - scheduleFrame.getWidth()) / 2);
				int y = (int) ((d.getHeight() - scheduleFrame.getHeight()) / 2);
				scheduleFrame.setLocation(x, y);
			}

			/* Charge the Schedule Program */
			loadScheduleProgram();

			/* Load the schedule from the server configuration */
			scheduleFrame.setVisible(true);
		}


		/**
		 * Load a Schedule Program from the server
		 *
		 */
		private void loadScheduleProgram(){

			try {
				scheduleProgram = controller.getConnection().getSchedule();

				if (scheduleProgram.isEnabled()) {

					hourStart.setEnabled(true);
					minuteStart.setEnabled(true);
					hourStop.setEnabled(true);
					minuteStop.setEnabled(true);

					hourStart.setText(String.valueOf(scheduleProgram.getCalendar(true).get(Calendar.HOUR_OF_DAY)));
					minuteStart.setText(String.valueOf(scheduleProgram.getCalendar(true).get(Calendar.MINUTE)));
					hourStop.setText(String.valueOf(scheduleProgram.getCalendar(false).get(Calendar.HOUR_OF_DAY)));
					minuteStop.setText(String.valueOf(scheduleProgram.getCalendar(false).get(Calendar.MINUTE)));	

					enableScheduleProgram.setSelected(true);

					if (scheduleProgram.isOnce()){
						enableOnceSchedule.setSelected(true);
						enableDiarySchedule.setSelected(false);
						enableWeeklySchedule.setEnabled(false);
						enableMonthlySchedule.setEnabled(false);
						enableAnualSchedule.setEnabled(false);
						monthDayStart.setEnabled(true);
						monthStart.setEnabled(true);
						yearStart.setEnabled(true);
						weekDayStart.setEnabled(false);
						monthDayStop.setEnabled(true);
						monthStop.setEnabled(true);
						yearStop.setEnabled(true);
						weekDayStop.setEnabled(false);


						monthDayStart.setText(String.valueOf(scheduleProgram.getCalendar(true).get(Calendar.DAY_OF_MONTH)));
						monthStart.setSelectedIndex(scheduleProgram.getCalendar(true).get(Calendar.MONTH));
						yearStart.setText(String.valueOf(scheduleProgram.getCalendar(true).get(Calendar.YEAR)));
						monthDayStop.setText(String.valueOf(scheduleProgram.getCalendar(false).get(Calendar.DAY_OF_MONTH)));
						monthStop.setSelectedIndex(scheduleProgram.getCalendar(false).get(Calendar.MONTH));
						yearStop.setText(String.valueOf(scheduleProgram.getCalendar(false).get(Calendar.YEAR)));

					}

					else if (scheduleProgram.isDiary()){
						enableOnceSchedule.setSelected(false);
						enableDiarySchedule.setSelected(true);
						enableWeeklySchedule.setEnabled(false);
						enableMonthlySchedule.setEnabled(false);
						enableAnualSchedule.setEnabled(false);
						weekDayStart.setEnabled(false);
						monthDayStart.setEnabled(false);
						monthStart.setEnabled(false);
						yearStart.setEnabled(false);
						weekDayStop.setEnabled(false);
						monthDayStop.setEnabled(false);
						monthStop.setEnabled(false);
						yearStop.setEnabled(false);

					}
					else if (scheduleProgram.isWeekly()){
						enableOnceSchedule.setSelected(false);
						enableDiarySchedule.setEnabled(false);
						enableWeeklySchedule.setSelected(true);
						enableMonthlySchedule.setEnabled(false);
						enableAnualSchedule.setEnabled(false);
						weekDayStart.setEnabled(true);
						monthDayStart.setEnabled(false);
						monthStart.setEnabled(false);
						yearStart.setEnabled(false);
						weekDayStop.setEnabled(true);
						monthDayStop.setEnabled(false);
						monthStop.setEnabled(false);
						yearStop.setEnabled(false);

						weekDayStart.setSelectedIndex(scheduleProgram.getCalendar(true).get(Calendar.DAY_OF_WEEK) - 1);
						weekDayStop.setSelectedIndex(scheduleProgram.getCalendar(false).get(Calendar.DAY_OF_WEEK) - 1);

					}
					else if (scheduleProgram.isMonthly()){
						enableOnceSchedule.setSelected(false);
						enableDiarySchedule.setEnabled(false);
						enableWeeklySchedule.setEnabled(false);
						enableMonthlySchedule.setSelected(true);
						enableAnualSchedule.setEnabled(false);
						monthDayStart.setEnabled(true);
						weekDayStart.setEnabled(false);
						monthStart.setEnabled(false);
						yearStart.setEnabled(false);
						monthDayStop.setEnabled(true);
						weekDayStop.setEnabled(false);
						monthStop.setEnabled(false);
						yearStop.setEnabled(false);

						monthDayStart.setText(String.valueOf(scheduleProgram.getCalendar(true).get(Calendar.DAY_OF_MONTH)));
						monthDayStop.setText(String.valueOf(scheduleProgram.getCalendar(false).get(Calendar.DAY_OF_MONTH)));

					}
					else if (scheduleProgram.isAnual()){
						enableOnceSchedule.setSelected(false);
						enableDiarySchedule.setEnabled(false);
						enableWeeklySchedule.setEnabled(false);
						enableMonthlySchedule.setEnabled(false);
						enableAnualSchedule.setSelected(true);
						monthDayStart.setEnabled(true);
						monthStart.setEnabled(true);
						yearStart.setEnabled(false);
						weekDayStart.setEnabled(false);
						monthDayStop.setEnabled(true);
						monthStop.setEnabled(true);
						yearStop.setEnabled(false);
						weekDayStop.setEnabled(false);

						monthDayStart.setText(String.valueOf(scheduleProgram.getCalendar(true).get(Calendar.DAY_OF_MONTH)));
						monthStart.setSelectedIndex(scheduleProgram.getCalendar(true).get(Calendar.MONTH));
						monthDayStop.setText(String.valueOf(scheduleProgram.getCalendar(false).get(Calendar.DAY_OF_MONTH)));
						monthStop.setSelectedIndex(scheduleProgram.getCalendar(false).get(Calendar.MONTH));
					}
				}
				else {

					ApplicationRunner.handleWarning(lang.SCHEDULE_NO_ENABLE_WARNING);
					enableScheduleProgram.setSelected(false);
					enableOnceSchedule.setSelected(false);
					enableDiarySchedule.setEnabled(false);
					enableWeeklySchedule.setEnabled(false);
					enableMonthlySchedule.setEnabled(false);
					enableAnualSchedule.setEnabled(false);
					hourStart.setEnabled(false);
					minuteStart.setEnabled(false);
					weekDayStart.setEnabled(false);
					monthDayStart.setEnabled(false);
					monthStart.setEnabled(false);
					yearStart.setEnabled(false);
					hourStop.setEnabled(false);
					minuteStop.setEnabled(false);
					weekDayStop.setEnabled(false);
					monthDayStop.setEnabled(false);
					monthStop.setEnabled(false);
					yearStop.setEnabled(false);
				}

				logArea.append("INFO: " + lang.LOAD_SCHEDULE_CONF_MESSAGE + ((Date)new Date()).toString() + "\n");

			}catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			}
		}

		/**
		 * Save a Schedule Program from the client to the server
		 *
		 */
		private void saveScheduleProgram(){

			ScheduleProgram scheduleProgramOld = (ScheduleProgram) scheduleProgram.getClon();
			try {
				if (enableScheduleProgram.isSelected()){
					Calendar calendarStart = Calendar.getInstance();
					Calendar calendarStop = Calendar.getInstance();

					scheduleProgram.setEnabled(true);

					if (!ScheduleProgram.validateField(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStart.getText())) ||
							!ScheduleProgram.validateField(Calendar.MINUTE, Integer.parseInt(minuteStart.getText())) ||
							!ScheduleProgram.validateField(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStop.getText())) ||
							!ScheduleProgram.validateField(Calendar.MINUTE, Integer.parseInt(minuteStop.getText()))) {
						controller.getConnection().setSchedule(scheduleProgramOld);
						ApplicationRunner.handleWarning(lang.SCHEDULE_ERROR_VALUES_WARNING);
						return; 
					}

					calendarStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStart.getText()));
					calendarStart.set(Calendar.MINUTE, Integer.parseInt(minuteStart.getText()));
					calendarStop.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStop.getText()));
					calendarStop.set(Calendar.MINUTE, Integer.parseInt(minuteStop.getText()));

					if (enableOnceSchedule.isSelected()) {
						scheduleProgram.setOnce(true);
						scheduleProgram.setDiary(false);
						scheduleProgram.setWeekly(false);
						scheduleProgram.setMonthly(false);
						scheduleProgram.setAnual(false);


						if (!ScheduleProgram.validateField(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStart.getText())) ||
								!ScheduleProgram.validateField(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStop.getText()))) {
							controller.getConnection().setSchedule(scheduleProgramOld);
							ApplicationRunner.handleWarning(lang.SCHEDULE_ERROR_VALUES_WARNING);
							return; 
						}

						calendarStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStart.getText()));
						calendarStart.set(Calendar.MONTH, monthStart.getSelectedIndex());
						calendarStop.set(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStop.getText()));
						calendarStop.set(Calendar.MONTH, monthStop.getSelectedIndex());
						calendarStart.set(Calendar.YEAR, Integer.parseInt(yearStart.getText()));
						calendarStop.set(Calendar.YEAR, Integer.parseInt(yearStop.getText()));
					}
					else if (enableDiarySchedule.isSelected()) {
						scheduleProgram.setDiary(true);
						scheduleProgram.setOnce(false);
						scheduleProgram.setWeekly(false);
						scheduleProgram.setMonthly(false);
						scheduleProgram.setAnual(false);
					}
					else if (enableWeeklySchedule.isSelected()) {
						scheduleProgram.setWeekly(true);
						scheduleProgram.setDiary(false);
						scheduleProgram.setOnce(false);
						scheduleProgram.setMonthly(false);
						scheduleProgram.setAnual(false);



						calendarStart.set(Calendar.DAY_OF_WEEK, weekDayStart.getSelectedIndex() + 1);
						calendarStop.set(Calendar.DAY_OF_WEEK, weekDayStop.getSelectedIndex() + 1);

					}
					else if (enableMonthlySchedule.isSelected()) {
						scheduleProgram.setMonthly(true);
						scheduleProgram.setWeekly(false);
						scheduleProgram.setDiary(false);
						scheduleProgram.setOnce(false);
						scheduleProgram.setAnual(false);

						if (!ScheduleProgram.validateField(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStart.getText())) ||
								!ScheduleProgram.validateField(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStop.getText()))) {
							controller.getConnection().setSchedule(scheduleProgramOld);
							ApplicationRunner.handleWarning(lang.SCHEDULE_ERROR_VALUES_WARNING);
							return; 
						}

						calendarStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStart.getText()));
						calendarStop.set(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStop.getText()));

					}
					else if (enableAnualSchedule.isSelected()) {
						scheduleProgram.setAnual(true);
						scheduleProgram.setMonthly(false);
						scheduleProgram.setWeekly(false);
						scheduleProgram.setDiary(false);
						scheduleProgram.setOnce(false);

						if (!ScheduleProgram.validateField(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStart.getText())) ||
								!ScheduleProgram.validateField(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStop.getText()))) {
							controller.getConnection().setSchedule(scheduleProgramOld);
							ApplicationRunner.handleWarning(lang.SCHEDULE_ERROR_VALUES_WARNING);
							return; 
						}

						calendarStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStart.getText()));
						calendarStart.set(Calendar.MONTH, monthStart.getSelectedIndex() + 1);
						calendarStop.set(Calendar.DAY_OF_MONTH, Integer.parseInt(monthDayStop.getText()));
						calendarStop.set(Calendar.MONTH, monthStop.getSelectedIndex() + 1);

					}
					else {
						ApplicationRunner.handleAlert(lang.CONFIG_SCHEDULE_NOT_PROGRAM_SELECTED);
					}

					scheduleProgram.setCalendar(calendarStart, true);
					scheduleProgram.setCalendar(calendarStop,false);

					if (calendarStart.after(calendarStop)) {
						controller.getConnection().setSchedule(scheduleProgramOld);
						ApplicationRunner.handleWarning(lang.SCHEDULE_ERROR_DATES_WARNING);
						return;
					}

				} else {
					scheduleProgram.setEnabled(false);
					scheduleProgram.setCalendar(null, true);
					scheduleProgram.setCalendar(null, false);
				}
				controller.getConnection().setSchedule(scheduleProgram);
				logArea.append("INFO: " + lang.SAVE_SCHEDULE_CONF_MESSAGE + ((Date)new Date()).toString() + "\n");

			}catch (RemoteException e){
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
				System.exit(-1);
			} catch (NumberFormatException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				ApplicationRunner.handleWarning(lang.SCHEDULE_ERROR_VALUES_WARNING);
				try {
					controller.getConnection().setSchedule(scheduleProgramOld);
				} catch (RemoteException e1) {
					log.severe(e.getMessage());
					ApplicationRunner.handleAlert(e.getMessage());
					System.exit(-1);
				}	
			}


		}



		/**
		 * Handles all application quit events.
		 *
		 * @author Rodrigo Jimenez
		 * @version 1.0
		 */
		private class SaveConfig implements ActionListener {

			/**
			 * Save the configuration when invoked.
			 *
			 * @param ae The event triggering the quit operation.
			 */
			public void actionPerformed(ActionEvent ae) {
				saveScheduleProgram();
			}
		}


		/**
		 * Handles all application quit events.
		 *
		 * @author Rodrigo Jimenez
		 * @version 1.0
		 */
		private class LoadConfig implements ActionListener {

			/**
			 * Save the configuration when invoked.
			 *
			 * @param ae The event triggering the quit operation.
			 */
			public void actionPerformed(ActionEvent ae) {
				loadScheduleProgram();
			}
		}

		/**
		 * Handles all application quit events.
		 *
		 * @author Rodrigo Jimenez
		 * @version 1.0
		 */
		private class CloseConfig implements ActionListener {

			/**
			 * Quits the application when invoked.
			 *
			 * @param ae The event triggering the quit operation.
			 */
			public void actionPerformed(ActionEvent ae) {
				scheduleFrame.setVisible(false);
			}
		}

		public void itemStateChanged(ItemEvent e) {}

		public void stateChanged(ChangeEvent e) {

			enableScheduleProgram.removeChangeListener(this);
			enableOnceSchedule.removeChangeListener(this);
			enableDiarySchedule.removeChangeListener(this);
			enableWeeklySchedule.removeChangeListener(this);
			enableMonthlySchedule.removeChangeListener(this);
			enableAnualSchedule.removeChangeListener(this);

			if (enableScheduleProgram.isSelected()){
				//scheduleProgram.setEnabled(true);
				enableOnceSchedule.setEnabled(true);
				enableDiarySchedule.setEnabled(true);
				enableWeeklySchedule.setEnabled(true);
				enableMonthlySchedule.setEnabled(true);
				enableAnualSchedule.setEnabled(true);

				hourStart.setEnabled(true);
				minuteStart.setEnabled(true);
				hourStop.setEnabled(true);
				minuteStop.setEnabled(true);

				if (enableOnceSchedule.isSelected()) {
					//scheduleProgram.setOnce(true);
					//scheduleProgram.setDiary(false);
					//scheduleProgram.setWeekly(false);
					//scheduleProgram.setMonthly(false);
					//scheduleProgram.setAnual(false);

					enableDiarySchedule.setEnabled(false);
					enableWeeklySchedule.setEnabled(false);
					enableMonthlySchedule.setEnabled(false);
					enableAnualSchedule.setEnabled(false);
					enableDiarySchedule.setSelected(false);
					enableWeeklySchedule.setSelected(false);
					enableMonthlySchedule.setSelected(false);
					enableAnualSchedule.setSelected(false);

					weekDayStart.setEnabled(false);
					monthDayStart.setEnabled(true);
					monthStart.setEnabled(true);
					yearStart.setEnabled(true);
					weekDayStop.setEnabled(false);
					monthDayStop.setEnabled(true);
					monthStop.setEnabled(true);
					yearStop.setEnabled(true);
				}

				else if (enableDiarySchedule.isSelected()) {
					//scheduleProgram.setOnce(false);
					//scheduleProgram.setDiary(true);
					//scheduleProgram.setWeekly(false);
					//scheduleProgram.setMonthly(false);
					//scheduleProgram.setAnual(false);

					enableOnceSchedule.setEnabled(false);
					enableWeeklySchedule.setEnabled(false);
					enableMonthlySchedule.setEnabled(false);
					enableAnualSchedule.setEnabled(false);
					enableOnceSchedule.setSelected(false);
					enableWeeklySchedule.setSelected(false);
					enableMonthlySchedule.setSelected(false);
					enableAnualSchedule.setSelected(false);

					weekDayStart.setEnabled(false);
					monthDayStart.setEnabled(false);
					monthStart.setEnabled(false);
					yearStart.setEnabled(false);
					weekDayStop.setEnabled(false);
					monthDayStop.setEnabled(false);
					monthStop.setEnabled(false);
					yearStop.setEnabled(false);
				}
				else if (enableWeeklySchedule.isSelected()) {
					//scheduleProgram.setOnce(false);
					//scheduleProgram.setDiary(false);
					//scheduleProgram.setWeekly(true);
					//scheduleProgram.setMonthly(false);
					//scheduleProgram.setAnual(false);

					enableOnceSchedule.setEnabled(false);
					enableDiarySchedule.setEnabled(false);
					enableMonthlySchedule.setEnabled(false);
					enableAnualSchedule.setEnabled(false);
					enableOnceSchedule.setSelected(false);
					enableDiarySchedule.setSelected(false);
					enableMonthlySchedule.setSelected(false);
					enableAnualSchedule.setSelected(false);

					weekDayStart.setEnabled(true);
					monthDayStart.setEnabled(false);
					monthStart.setEnabled(false);
					yearStart.setEnabled(false);
					weekDayStop.setEnabled(true);
					monthDayStop.setEnabled(false);
					monthStop.setEnabled(false);
					yearStop.setEnabled(false);

				}
				else if (enableMonthlySchedule.isSelected()) {
					//scheduleProgram.setOnce(false);
					//scheduleProgram.setDiary(false);
					//scheduleProgram.setWeekly(false);
					//scheduleProgram.setMonthly(true);
					//scheduleProgram.setAnual(false);

					enableOnceSchedule.setEnabled(false);
					enableWeeklySchedule.setEnabled(false);
					enableDiarySchedule.setEnabled(false);
					enableAnualSchedule.setEnabled(false);
					enableOnceSchedule.setSelected(false);
					enableWeeklySchedule.setSelected(false);
					enableDiarySchedule.setSelected(false);
					enableAnualSchedule.setSelected(false);


					weekDayStart.setEnabled(false);
					monthDayStart.setEnabled(true);
					monthStart.setEnabled(false);
					yearStart.setEnabled(false);
					weekDayStop.setEnabled(false);
					monthDayStop.setEnabled(true);
					monthStop.setEnabled(false);
					yearStop.setEnabled(false);
				}
				else if (enableAnualSchedule.isSelected()) {
					//scheduleProgram.setOnce(false);
					//scheduleProgram.setDiary(false);
					//scheduleProgram.setWeekly(false);
					//scheduleProgram.setMonthly(false);
					//scheduleProgram.setAnual(true);

					enableOnceSchedule.setEnabled(false);
					enableWeeklySchedule.setEnabled(false);
					enableMonthlySchedule.setEnabled(false);
					enableDiarySchedule.setEnabled(false);

					weekDayStart.setEnabled(false);
					monthDayStart.setEnabled(true);
					monthStart.setEnabled(true);
					yearStart.setEnabled(false);
					weekDayStop.setEnabled(false);
					monthDayStop.setEnabled(true);
					monthStop.setEnabled(true);
					yearStop.setEnabled(false);

				}
				else {
					hourStart.setEnabled(false);
					minuteStart.setEnabled(false);
					weekDayStart.setEnabled(false);
					monthDayStart.setEnabled(false);
					monthStart.setEnabled(false);
					yearStart.setEnabled(false);
					hourStop.setEnabled(false);
					minuteStop.setEnabled(false);
					weekDayStop.setEnabled(false);
					monthDayStop.setEnabled(false);
					monthStop.setEnabled(false);
					yearStop.setEnabled(false);
				}
			} 
			else {

				//scheduleProgram.setOnce(false);
				//scheduleProgram.setDiary(false);
				//scheduleProgram.setWeekly(false);
				//scheduleProgram.setMonthly(false);
				//scheduleProgram.setAnual(false);

				enableOnceSchedule.setSelected(false);
				enableDiarySchedule.setSelected(false);
				enableWeeklySchedule.setSelected(false);
				enableMonthlySchedule.setSelected(false);
				enableAnualSchedule.setSelected(false);
				enableOnceSchedule.setEnabled(false);
				enableDiarySchedule.setEnabled(false);
				enableWeeklySchedule.setEnabled(false);
				enableMonthlySchedule.setEnabled(false);
				enableAnualSchedule.setEnabled(false);

				weekDayStart.setEnabled(false);
				monthDayStart.setEnabled(false);
				monthStart.setEnabled(false);
				yearStart.setEnabled(false);
				weekDayStop.setEnabled(false);
				monthDayStop.setEnabled(false);
				monthStop.setEnabled(false);
				yearStop.setEnabled(false);
			}

			enableScheduleProgram.addChangeListener(this);
			enableOnceSchedule.addChangeListener(this);
			enableDiarySchedule.addChangeListener(this);
			enableWeeklySchedule.addChangeListener(this);
			enableMonthlySchedule.addChangeListener(this);
			enableAnualSchedule.addChangeListener(this);
		}
	}


	/**
	 * Handles all application quit events.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	private class StartVigilance implements ActionListener {



		/**
		 * Start the vigilance when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {
			try {
				controller.getConnection().start();
			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
			}
		}
	}


	/**
	 * Handles all application quit events.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	private class StopVigilance implements ActionListener {

		/**
		 * Start the vigilance when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {
			try {
				controller.getConnection().stop();
			} catch (RemoteException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
			} catch (IOException e) {
				log.severe(e.getMessage());
				ApplicationRunner.handleAlert(e.getMessage());
			}
		}
	}


	/**
	 * Handles all application quit events.
	 *
	 * @author Rodrigo Jimenez
	 * @version 1.0
	 */
	private class AboutApplication implements ActionListener {

		/**
		 * Quits the application when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {

			if (aboutFrame == null) {
				aboutFrame = new JFrame();
				aboutFrame.setName(lang.ABOUT_WINDOW_NAME);
				aboutFrame.setTitle(lang.ABOUT_WINDOW_NAME);
				aboutFrame.setResizable(false);
				aboutFrame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent arg0) {aboutFrame.setVisible(false);}}
				);

				JPanel aboutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				JLabel name = new JLabel("PFC Rodrigo Jimenez Rodriguez");
				JLabel copy = new JLabel("(c) Copyright Rodrigo 2011.");
				JLabel version = new JLabel("Version: 1.0");
				JLabel contact = new JLabel("contact: rodrigoj@telefonica.net");
				aboutPanel.add(name);
				aboutPanel.add(version);
				aboutPanel.add(copy);
				aboutPanel.add(contact);
				JButton okButton = new JButton("Ok");
				okButton.addActionListener(new OkAbout());
				aboutPanel.add(okButton);
				aboutFrame.add(aboutPanel);
				aboutFrame.pack();
				aboutFrame.setSize(200, 150);
				// Center on the screen of the computer
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				int x = (int) ((d.getWidth() - aboutFrame.getWidth()) / 2);
				int y = (int) ((d.getHeight() - aboutFrame.getHeight()) / 2);
				aboutFrame.setLocation(x, y);
			}
			aboutFrame.setVisible(true);
		}

		/**
		 * Handles all application quit events.
		 *
		 * @author Rodrigo Jimenez
		 * @version 1.0
		 */
		private class OkAbout implements ActionListener {

			/**
			 * Quits the application when invoked.
			 *
			 * @param ae The event triggering the quit operation.
			 */
			public void actionPerformed(ActionEvent ae) {
				aboutFrame.setVisible(false);
			}
		}

	}

}



