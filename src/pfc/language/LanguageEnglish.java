package pfc.language;

/**
 * Represents the English language class
 * @author rodrigo
 */
public class LanguageEnglish extends Language{

	
	

	/**
	 * Default constructor
	 */
	public LanguageEnglish(){
		

		SCHEDULE_ENABLE_TEXT = "Enable Schedule Program";

		SCHEDULE_ENABLE_NAME = "Enable Schedule Program";
		ONCE_ENABLE_TEXT = "Once time";
		ONCE_ENABLE_NAME = "Once time";
		DIARY_ENABLE_TEXT = "Diary";
		DIARY_ENABLE_NAME = "Diary";
		WEEKLY_ENABLE_TEXT = "Weekly";
		WEEKLY_ENABLE_NAME = "Weekly";
		MONTHLY_ENABLE_TEXT = "Monthly";
		MONTHLY_ENABLE_NAME = "Monthly";
		ANUAL_ENABLE_TEXT = "Anual";
		ANUAL_ENABLE_NAME = "Anual";

		SCHEDULE_HOUR_NAME = "Hour";
		SCHEDULE_MINUTE_NAME = "Minute";
		SCHEDULE_WEEKDAY_NAME = "Day of the week";
		SCHEDULE_MONTHDAY_NAME = "Day of The Month";
		SCHEDULE_MONTH_NAME = "Month";
		SCHEDULE_YEAR_NAME = "Year";
		SCHEDULE_NO_ENABLE_WARNING = "A Schedule Configuration is no Programmed";
		SCHEDULE_ERROR_VALUES_WARNING = "Check Parameters, there are some erroneos";
		SCHEDULE_ERROR_DATES_WARNING = "The Start Date is after the Stop Date, Please Check Dates";



		CONFIG_SCHEDULE_NOT_PROGRAM_SELECTED = "Warning: A Schedle Program is not selected";
		CONFIG_SCHEDULE_TITLE = "Schedule Configuration";
		CONFIG_SCHEDULE_PANEL_TITLE = "Configure the Schedule Program Selected";
		CONFIG_SCHEDULE_START_PANEL_TITLE = "Start Configuration";
		CONFIG_SCHEDULE_STOP_PANEL_TITLE = "Stop Configuration";

		MONTH_JANUARY = "January";
		MONTH_FEBRERY = "Febrery";
		MONTH_MARCH = "March";
		MONTH_APRIL = "April";
		MONTH_MAY = "May";
		MONTH_JUNE = "June";
		MONTH_JULY = "July";
		MONTH_AGOST = "Agost";
		MONTH_SEPTEMBER = "September";
		MONTH_OCTOBER = "October";
		MONTH_NOVEMBER = "November";
		MONTH_DECEMBER = "December";

		MONDAY = "Monday";
		TUESDAY = "Tuesday";
		WEDNESDAY = "Wednesday";
		THURSDAY = "Thursday";
		FRIDAY = "Friday";
		SATURDAY = "Saturday";
		SUNDAY = "Sunday";

		LANGUAGE_LABEL = "Select Language: ";

		CONFIG_OPTIONS_PANEL_TITLE = "Configuration";

		SERVER_LOCATION_LABEL = "Server location: ";
		SERVER_PORT_LABEL = "Server port: ";
		SERVER_COMM_PORT_LABEL = "Server COMM port: ";
		SERVER_RTP_CLIENTS_IP_MASK_LABEL = "RTP Clients : ";
		SERVER_RTP_PORT_LABEL = "Server RTP port: ";
		SERVER_VIDEO_SOURCE_LABEL = "Video source: ";
		SERVER_VIDEO_DEVICES_LABEL = "Camera Devices: ";
		CAM_ENABLE_LABEL = "Use Cam Device: ";

		HOST_IP_LOCATION_TOOL_TIP = "The server where is located (IP address)";
		SERVER_PORT_TOOL_TIP = "The port number the Server uses to listens for requests";
		SERVER_COMM_PORT_TOOL_TIP = "The COMM port number the Server uses to listens the vigilance robot";
		SERVER_RTP_PORT_TOOL_TIP = "The RTP port number the Server uses to transmit the cam to the clients";
		SERVER_RTP_CLIENTS_IP_MASK_TOOL_TIP = "The RTP clients mask or ips or broadcast ip address";
		SERVER_VIDEO_DEVICE_LABEL_TOOL_TIP = "The Video Device that is connecter to the robot";
		SERVER_VIDEO_STREAM_URL_LABEL = "Stream URL: ";
		SERVER_VIDEO_STREAM_URL_TOOL_TIP = "The RTSP/HTTP URL used as the video source";
		SERVER_RMI_SERVER_TEXT = "RMI server";
		SERVER_CLIENT_SAVE_VIDEO_LOCATION_LABEL = "Save Video Recording Path: ";
		VIDEO_SOURCE_LOCAL_OPTION = "Local camera";
		VIDEO_SOURCE_STREAM_OPTION = "RTSP/HTTP stream";

		CAM_ENABLE_TEXT = "Enable Camera";


		CLIENT_PLAYER_IN_USE_MESSAGE = "Please before Close The player Window";
		ROUTE_DEFINE_NAME_MESSAGE = "Please before save define a Route Name";
		ROUTE_START_BEFORE_MESSAGE = "Please before save a Route you Have to Start It";
		ROUTE_EXISTS_MESSAGE = "This route exits Will be overwrited";
		ROUTES_NO_CREATED_MESSAGE = "No Exists Routes Created, please change the move kind before save";
		VIGILANCIE_RUNNING_MESSAGE = "Actualy the Robot is Working,\n If you want Manual VIgilance you have to stop the Robot before by the menu configuration";
		
		ROUTES_SEL_LABEL ="Route Select: ";

		ROUTE_CONFIGURATION_PANEL_NAME = "Route Configuration";

		MAIN_WINDOW_NAME = "PFC Robot Vigilance Control Client Application";
		SERVER_WINDOW_NAME = "PFC Vigilance Robot Control Server Application";
		HOSTLOCATIONDIALOG_WINDOW_NAME = "PFC Vigilance Robot Standalone Configuration";
		SCHEDULE_WINDOW_NAME = "Schudule Program PFC Vigilance Control Client Application";
		MOVE_CONTROLLER_WINDOW_NAME = "Move Control PFC Vigilance Robot Control Application";
		ABOUT_WINDOW_NAME = "About PFC Vigilance Robot Control Application";
		KEY_CAMERA_MOVE_CONFIG_WINDOW_NAME = "Key Camera Move Selection";
		KEY_CAMERA_MOVE_CONFIG_PANEL_NAME = "Key Camera Selections";

		FILE_MENU_NAME = "File";
		FILE_MENU_START = "Start Vigilance";
		FILE_MENU_STOP = "Stop Vigilance";
		FILE_MENU_CONFIG = "Configuration";
		FILE_MENU_SCHEDULE = "Schedule Configuration";

		EXIT = "Exit";
		START = "Start";
		STOP = "Stop";
		QUIT = "Quit";
		CANCEL = "Cancel";
		SAVE = "Save";
		LOAD = "Load";
		CONNECT = "Connect";
		CLOSE = "Close";
		DEVICE = "device";
		CHECK_MOVE_POINT = "Move Scan";
		CHECK_TERMAL_POINT = "Termal Scan";

		FOLLOW_LINES_MOVE_NAME = "Follow Lines";
		ROUTE_MOVE_NAME = "Programed Route";
		STATIC_MOVE_NAME = "Static";
		MOVE_CONTROL_KIND_SELECTION = new String[3];
		MOVE_CONTROL_KIND_SELECTION[0] = FOLLOW_LINES_MOVE_NAME; 
		MOVE_CONTROL_KIND_SELECTION[1] = ROUTE_MOVE_NAME;
		MOVE_CONTROL_KIND_SELECTION[2] = STATIC_MOVE_NAME;
		
		PROX_DISTANCE_LABEL = "Proximity distance alarm: ";
		LOW_BATERY_LABEL = "Low Voltage Value for Alarm (Max 15V): ";
		CHARGE_BATERY_LABEL = " Voltage Value for batery Charge: ";

		
		SCAN_PERIOD_LABEL = "Scan Period (Seconds): ";
		SCAN_ORIENTATION_PERIOD_LABEL = "Orientation Period (Seconds): ";
		KILL_SCAN_BY_OPERATION_LABEL = "Kill Active Scans When Stop Vigilance";
		ROUTE_NAME_LABEL = "Route name: ";
		ENABLE_EMAIL_SEND_LABEL = "Enable Email Send on Alarm :";
		EMAIL_RECEIVERS_LABEL = "Email Send Receivers :";

		ENABLE = "Enable";
		DISABLE = "Disable";
		ENABLE_SELECTION = new String[2];

		EXIT_TOOL_TIP = "Stops the server as soon as it is safe to do so";
		INITIAL_STATUS = "Enter configuration parameters and click \"" + START + "\"";

		OPTIONS_MENU_NAME = "Options";
		OPTIONS_MANUAL_VIGILANCE_START = "Start Manual Vigilance";
		OPTIONS_MENU_STOP = "Stop Manual Vigilance";
		OPTIONS_PROGRAM_ROUTE  = "Program a new Route";
		OPTIONS_SHOW_VIDEO = "Real Time Camera";
		OPTIONS_SHOW_REAL_TIME_VIDEO_NAME = "Real Time Video PFC Client Application";
		OPTIONS_MANUAL_CONTROL_WINDOW_NAME = "Manual Control PFC Client Application";
		OPTIONS_PLAYER_PANEL_TITLE = "Player";
		OPTIONS_MANUAL_CONTROL_SEL_PANEL_TITLE = "Control Selection";


		HELP_MENU_NAME = "Help";
		HELP_MENU_ABOUT = "About";

		CONFIG_WINDOW_NAME = "Config PFC Vigilance Control Client Application";
		
		CONFIG_MOVE_SENSOR_PANEL_NAME = "Move Sensor";
		CONFIG_TERMAL_SENSOR_PANEL_NAME = "Termal Sensor";
		CONFIG_SCAN_CONF_PANEL_NAME = "Sensor Scan Configuration";
		CONFIG_HEARTBEAT_PANEL_NAME = "Heartbeat";
		CONFIG_EMAIL_PANEL_NAME = "Email Configuration";
		CONFIG_BATERY_PANEL_NAME = "Batery Configuration";
		CONFIG_VIDEO_PANEL_NAME = "Video Configuration";
		CONFIG_CONTROL_MOVE_PANEL_NAME = "Move Control";
		CONFIG_CHECK_ALARM_PANEL_NAME ="Client Alarms Check";
		CONFIG_HEARTBEAT_LABEL = "Time for heartbeat (seconds): ";
		CONFIG_SAVE_VIDEO_ENABLE = "Enable Alarm Recording";
		CONFIG_SAVE_VIDEO_ALL_TIME_ENABLE = "Record All The Time";
		CONFIG_SAVE_VIDEO_TIME_LABEL = "Time for recording (seconds): ";
		CONFIG_HEARTBEAT_TOOL_TIP = "Declare the time for the heartbeat between the server and the robot";



		MOVE_CONTROL_LOCKED = "The Control of The Robot is locked by another Client, try again later";

		MONTHS[0] = MONTH_JANUARY;
		MONTHS[1] = MONTH_FEBRERY;
		MONTHS[2] = MONTH_MARCH;
		MONTHS[3] = MONTH_APRIL;
		MONTHS[4] = MONTH_MAY ;
		MONTHS[5] = MONTH_JUNE;
		MONTHS[6] = MONTH_JULY;
		MONTHS[7] = MONTH_AGOST;
		MONTHS[8] = MONTH_SEPTEMBER;
		MONTHS[9] = MONTH_OCTOBER;
		MONTHS[10] = MONTH_NOVEMBER;
		MONTHS[11] = MONTH_DECEMBER;

		
		WEEKDAYS[0] = MONDAY;
		WEEKDAYS[1] = TUESDAY;
		WEEKDAYS[2] = WEDNESDAY;
		WEEKDAYS[3] = THURSDAY;
		WEEKDAYS[4] = FRIDAY;
		WEEKDAYS[5] = SATURDAY;
		WEEKDAYS[6] = SUNDAY;


		SCHEDULE_START_STOP_COMBO[0] = this.START;
		SCHEDULE_START_STOP_COMBO[1] = this.STOP;

		ENABLE_SELECTION[0] = this.ENABLE;
		ENABLE_SELECTION[1] = this.DISABLE;
		
		CAMERA_NO_ENABLE_MESSAGE = "The serve has no enabled the camera service, the Camera Options will be disabled";
		ERROR_KEY_MOVE_CAMERA_VALUES_MESSAGE = "There a Key Value that is not a Characted, Configuration not saved";
		ERROR_CONFIG_VALUES_MESSAGE = "There are some values incorrect or not filled, please check before save";
		ERROR_SAVE_VIEO_ALARM_MESSAGE = "The Vigilance is running and is configured to save all video Time, please stop before make this change";
		
		KEY_CAMERA_CONF = "Keys";
		KEY_UP_SEL_LABEL = "Up Key Move: ";
	    KEY_DOWN_SEL_LABEL = "Down Key Move: ";
	    KEY_RIGHT_SEL_LABEL = "Right Key Move: ";
	    KEY_LEFT_SEL_LABEL = "Left Key Move: ";
	    KEY_CENTER_SEL_LABEL = "Center Key Move: ";
	    CAMERA_KEYS_NOT_OK_MESSAGE = "There are some Key Values for the Camera Movement wrong, please Select before use";
	    
	    
	    VIGILANCE_STOPPED_LABEL = "Vigilance Stopped...";
	    VIGILANCE_STARTED_LABEL = "Vigilance Started...";
	    
	    
	    HEARTBEAT_ALARM_MESSAGE = "Heartbeat Alarm Detected ";
	    MOVE_ALARM_MESSAGE = "Move Alarm Detected ";
	    TERMAL_ALARM_MESSAGE = "Termal Alarm Detected ";
	    DISTANCE_ALARM_MESSAGE = "Proximity Distance Alarm Detected ";
	    LOW_VOLTAGE_ALARM_MESSAGE = "Low Voltage Alarm Detected ";
	    STOP_VIGILANCE_MESSAGE = "Vigilance Stopped ";
	    START_VIGILANCE_MESSAGE = "Vigilance Started ";
	    
	    LOAD_CONF_MESSAGE = "Loaded Server Configuration";
	    SAVE_CONF_MESSAGE = "Saved Server Configuration ";
	    LOAD_SCHEDULE_CONF_MESSAGE = "Loaded Schedule Configuration ";
	    SAVE_SCHEDULE_CONF_MESSAGE = "Saved Schedule Configuration ";


	}


}
