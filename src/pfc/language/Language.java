package pfc.language;

/**
 * Represent a abstract with all the languages phrases and words used by the application
 * @author rodrigo
 *
 */
public class Language {
	
	
	/**
	 * Default constructor
	 */
	public Language(){
		
		MOVE_CONTROL_KIND_SELECTION = new String[2];
		MOVE_CONTROL_KIND_SELECTION[0] = FOLLOW_LINES_MOVE_NAME; 
		MOVE_CONTROL_KIND_SELECTION[1] = ROUTE_MOVE_NAME;
	}
    
    
    public static String SCHEDULE_ENABLE_TEXT;
    
    public String SCHEDULE_ENABLE_NAME;
    
    public String ONCE_ENABLE_TEXT;
    public String ONCE_ENABLE_NAME;
    
    public String DIARY_ENABLE_TEXT;
    public String DIARY_ENABLE_NAME;
    
    public String WEEKLY_ENABLE_TEXT;
    public String WEEKLY_ENABLE_NAME;
    
    public String MONTHLY_ENABLE_TEXT;
    public String MONTHLY_ENABLE_NAME;
    
    public String ANUAL_ENABLE_TEXT;
    public String ANUAL_ENABLE_NAME;
    
    
    
    public String SCHEDULE_HOUR_NAME;
    public String SCHEDULE_MINUTE_NAME;
    public String SCHEDULE_WEEKDAY_NAME;
    public String SCHEDULE_MONTHDAY_NAME;
    public String SCHEDULE_MONTH_NAME;
    public String SCHEDULE_YEAR_NAME;
    public String SCHEDULE_NO_ENABLE_WARNING;
    
    public String[] SCHEDULE_START_STOP_COMBO = new String[2];
    
    
    public String CONFIG_OPTIONS_PANEL_TITLE;
    
   
    public String LANGUAGE_LABEL;
    
    public String MONTH_JANUARY;
    public String MONTH_FEBRERY;
    public String MONTH_MARCH;
    public String MONTH_APRIL;
    public String MONTH_MAY;
    public String MONTH_JUNE;
    public String MONTH_JULY;
    public String MONTH_AGOST;
    public String MONTH_SEPTEMBER;
    public String MONTH_OCTOBER;
    public String MONTH_NOVEMBER;
    public String MONTH_DECEMBER;
    public String[] MONTHS = new String[12];
    
    public String MONDAY;
    public String TUESDAY;
    public String WEDNESDAY;
    public String THURSDAY;
    public String FRIDAY;
    public String SATURDAY;
    public String SUNDAY;
    public String[] WEEKDAYS = new String[7];
    

    public String SERVER_LOCATION_LABEL;
    public String SERVER_PORT_LABEL;
    public String SERVER_PORT_TOOL_TIP;
    public String SERVER_COMM_PORT_LABEL;
    public String SERVER_COMM_PORT_TOOL_TIP;
    public String SERVER_RTP_PORT_LABEL;
    public String SERVER_RTP_PORT_TOOL_TIP;
    public String SERVER_RTP_CLIENTS_IP_MASK_LABEL;
    public String SERVER_RTP_CLIENTS_IP_MASK_TOOL_TIP;
    public String SERVER_VIDEO_SOURCE_LABEL;
    public String SERVER_VIDEO_DEVICES_LABEL;
    public String SERVER_VIDEO_DEVICE_LABEL_TOOL_TIP;
    public String SERVER_VIDEO_STREAM_URL_LABEL;
    public String SERVER_VIDEO_STREAM_URL_TOOL_TIP;
    public String SERVER_RMI_SERVER_TEXT;
    public String SERVER_CLIENT_SAVE_VIDEO_LOCATION_LABEL;
    public String VIDEO_SOURCE_LOCAL_OPTION;
    public String VIDEO_SOURCE_STREAM_OPTION;
    
    public String CAM_ENABLE_LABEL;
    public String CAM_ENABLE_TEXT;
    
    public String HOST_IP_LOCATION_TOOL_TIP;
    
    public String CLIENT_PLAYER_IN_USE_MESSAGE;
    public String VIGILANCIE_RUNNING_MESSAGE;
    public String ROUTE_DEFINE_NAME_MESSAGE;
    public String ROUTE_START_BEFORE_MESSAGE;
    public String ROUTE_EXISTS_MESSAGE;
    public String ROUTES_NO_CREATED_MESSAGE;
    public String ROUTES_SEL_LABEL;
    public String PROX_DISTANCE_LABEL;
    public String LOW_BATERY_LABEL;
    public String CHARGE_BATERY_LABEL;

    
    public String START;
    public String STOP;
    public String EXIT;
    public String EXIT_TOOL_TIP;
    public String QUIT;
    public String CANCEL;
    public String SAVE;
    public String LOAD;
    public String CONNECT;
    public String CLOSE;
    public String DEVICE;
    public String CHECK_MOVE_POINT;
    public String CHECK_TERMAL_POINT;
    
    public String FOLLOW_LINES_MOVE_NAME;
    public String ROUTE_MOVE_NAME;
    public String STATIC_MOVE_NAME;
    public String[] MOVE_CONTROL_KIND_SELECTION;
    
    
    public String ROUTE_CONFIGURATION_PANEL_NAME;
    
    
    public String SCAN_PERIOD_LABEL;
    public String SCAN_ORIENTATION_PERIOD_LABEL;
    public String KILL_SCAN_BY_OPERATION_LABEL;
    public String ROUTE_NAME_LABEL;
    public String ENABLE_EMAIL_SEND_LABEL;
    public String EMAIL_RECEIVERS_LABEL;
    
    
    public String ENABLE;
    public String DISABLE;
    public String[] ENABLE_SELECTION;
    
    public String INITIAL_STATUS;
    public String VIGILANCE_STOPPED_LABEL;
    public String VIGILANCE_STARTED_LABEL;
    
    public String MAIN_WINDOW_NAME;
    public String SERVER_WINDOW_NAME;
    public String HOSTLOCATIONDIALOG_WINDOW_NAME;
    public String SCHEDULE_WINDOW_NAME;
    public String MOVE_CONTROLLER_WINDOW_NAME;
    public String KEY_CAMERA_MOVE_CONFIG_WINDOW_NAME;
    public String KEY_CAMERA_MOVE_CONFIG_PANEL_NAME;
    
    
    public String FILE_MENU_NAME;
    public String FILE_MENU_START;
    public String FILE_MENU_STOP;
    public String FILE_MENU_CONFIG;
    public String FILE_MENU_SCHEDULE;
    
 
    public String OPTIONS_MENU_NAME;
    public String OPTIONS_MANUAL_VIGILANCE_START;
    public String OPTIONS_MENU_STOP;
    public String OPTIONS_PROGRAM_ROUTE;
    public String OPTIONS_SHOW_VIDEO;
    public String OPTIONS_PLAYER_PANEL_TITLE;
    public String OPTIONS_SHOW_REAL_TIME_VIDEO_NAME;
    public String OPTIONS_MANUAL_CONTROL_WINDOW_NAME;
    public String OPTIONS_MANUAL_CONTROL_SEL_PANEL_TITLE;
    
    public String HELP_MENU_NAME;
    public String HELP_MENU_ABOUT;
    
    public String CONFIG_WINDOW_NAME;
    
    public String CONFIG_MOVE_SENSOR_PANEL_NAME;
    public String CONFIG_TERMAL_SENSOR_PANEL_NAME;
    public String CONFIG_SCAN_CONF_PANEL_NAME;
    public String CONFIG_HEARTBEAT_PANEL_NAME;
    public String CONFIG_EMAIL_PANEL_NAME;
    public String CONFIG_BATERY_PANEL_NAME;
    public String CONFIG_VIDEO_PANEL_NAME;
    public String CONFIG_CONTROL_MOVE_PANEL_NAME;
    public String CONFIG_CHECK_ALARM_PANEL_NAME;
    public String CONFIG_HEARTBEAT_LABEL;
    public String CONFIG_SAVE_VIDEO_ENABLE;
    public String CONFIG_SAVE_VIDEO_ALL_TIME_ENABLE;
    public String CONFIG_SAVE_VIDEO_TIME_LABEL;
    public String CONFIG_HEARTBEAT_TOOL_TIP;
    public String CONFIG_SCHEDULE_NOT_PROGRAM_SELECTED;
    public String CONFIG_SCHEDULE_TITLE;
    public String CONFIG_SCHEDULE_PANEL_TITLE;
    public String CONFIG_SCHEDULE_START_PANEL_TITLE;
    public String CONFIG_SCHEDULE_STOP_PANEL_TITLE;
    public String SCHEDULE_ERROR_VALUES_WARNING;
    public String SCHEDULE_ERROR_DATES_WARNING;
    
    public String ABOUT_WINDOW_NAME;
    
    
    public String MOVE_CONTROL_LOCKED;
    public String CAMERA_NO_ENABLE_MESSAGE;
    public String ERROR_KEY_MOVE_CAMERA_VALUES_MESSAGE;
    public String KEY_CAMERA_CONF;
    public String KEY_UP_SEL_LABEL;
    public String KEY_DOWN_SEL_LABEL;
    public String KEY_RIGHT_SEL_LABEL;
    public String KEY_LEFT_SEL_LABEL;
    public String KEY_CENTER_SEL_LABEL;
    public String CAMERA_KEYS_NOT_OK_MESSAGE;
    public String ERROR_CONFIG_VALUES_MESSAGE;
    public String ERROR_SAVE_VIEO_ALARM_MESSAGE;
    
    public String HEARTBEAT_ALARM_MESSAGE;
    public String MOVE_ALARM_MESSAGE;
    public String TERMAL_ALARM_MESSAGE;
    public String DISTANCE_ALARM_MESSAGE;
    public String LOW_VOLTAGE_ALARM_MESSAGE;
    public String STOP_VIGILANCE_MESSAGE;
    public String START_VIGILANCE_MESSAGE;
    public String LOAD_CONF_MESSAGE;
    public String SAVE_CONF_MESSAGE;
    public String LOAD_SCHEDULE_CONF_MESSAGE;
    public String SAVE_SCHEDULE_CONF_MESSAGE;
    
}
