package pfc.language;

/**
 * Represent the Spanish language class
 * @author rodrigo 
 *
 */
public class LanguageSpanish extends Language{

    /**
     * Default onstructor
     */
    public LanguageSpanish(){
	super();

	SCHEDULE_ENABLE_TEXT = "Habilitar Funcionamiento Programado";
	SCHEDULE_ENABLE_NAME = "Habilitar Funcionamiento Programado";

	ONCE_ENABLE_TEXT = "Una vez";
	ONCE_ENABLE_NAME = "Una vez";

	DIARY_ENABLE_TEXT = "Diariamente";
	DIARY_ENABLE_NAME = "Diariamente";

	WEEKLY_ENABLE_TEXT = "Semanalmente";
	WEEKLY_ENABLE_NAME = "Semanalmente";

	MONTHLY_ENABLE_TEXT = "Mensualmente";
	MONTHLY_ENABLE_NAME = "Mensualmente";

	ANUAL_ENABLE_TEXT = "Anualmente";
	ANUAL_ENABLE_NAME = "Anualmente";

	SCHEDULE_HOUR_NAME = "Hora";
	SCHEDULE_MINUTE_NAME = "Minuto";
	SCHEDULE_WEEKDAY_NAME = "Dia de la semana";
	SCHEDULE_MONTHDAY_NAME = "Dia del mes";
	SCHEDULE_MONTH_NAME = "Mes";
	SCHEDULE_YEAR_NAME = "Año";
	SCHEDULE_NO_ENABLE_WARNING = "No ha sido establecida ninguna configuracion programada";
	SCHEDULE_ERROR_VALUES_WARNING = "Algun parametro esta vacio o es incorrecto reviselo";
	SCHEDULE_ERROR_DATES_WARNING = "La Fecha de arranque es posterior a la de parada, por favor reviselo";

	CONFIG_SCHEDULE_NOT_PROGRAM_SELECTED = "Advertencia: Programa no seleccionado";
	CONFIG_SCHEDULE_TITLE = "Configuracion Programa";
	CONFIG_SCHEDULE_PANEL_TITLE = "Configuracion Programacion seleccionada";
	CONFIG_SCHEDULE_START_PANEL_TITLE = "Configuracion Arranque";
	CONFIG_SCHEDULE_STOP_PANEL_TITLE = "Configuracion Parada";
    
    

	MONTH_JANUARY = "Enero";
	MONTH_FEBRERY = "Febrero";
	MONTH_MARCH = "Marzo";
	MONTH_APRIL = "Abril";
	MONTH_MAY = "Mayo";
	MONTH_JUNE = "Junio";
	MONTH_JULY = "Julio";
	MONTH_AGOST = "Agosto";
	MONTH_SEPTEMBER = "Septiembre";
	MONTH_OCTOBER = "Octubre";
	MONTH_NOVEMBER = "Noviembre";
	MONTH_DECEMBER = "Diciembre";

	MONDAY = "Lunes";
	TUESDAY = "Martes";
	WEDNESDAY = "Miercoles";
	THURSDAY = "Jueves";
	FRIDAY = "Viernes";
	SATURDAY = "Sabado";
	SUNDAY = "Domingo";

	LANGUAGE_LABEL = "Escoga Idioma:";
	
	CONFIG_OPTIONS_PANEL_TITLE = "Configuracion";
	
	
	MAIN_WINDOW_NAME = "PFC Aplicacion Cliente Robot Vigilante";
	SERVER_WINDOW_NAME = "PFC Aplicacion Servidor Robot Vigilante";
	ABOUT_WINDOW_NAME = "Sobre PFC Aplicacion de control de un Robot Vigilante";
	HOSTLOCATIONDIALOG_WINDOW_NAME = "PFC Aplicacion de Configuracion autonoma";
	SCHEDULE_WINDOW_NAME = "PFC Aplicacion Cliente Funcionamiento programado";
	MOVE_CONTROLLER_WINDOW_NAME = "PFC Aplicacion Control de Movimento";
	KEY_CAMERA_MOVE_CONFIG_WINDOW_NAME = "Seleccion de Teclas Para el movimiento de camara";
	KEY_CAMERA_MOVE_CONFIG_PANEL_NAME = "Seleccion de Teclas Camara";

	SERVER_LOCATION_LABEL = "IP servidor: ";
	SERVER_PORT_LABEL = "Puerto servidor: ";
	SERVER_COMM_PORT_LABEL = "Pueto COMM Servidor: ";
	SERVER_RTP_CLIENTS_IP_MASK_LABEL = "Mascara Clientes RTP: ";
	SERVER_RTP_PORT_LABEL = "Puerto RTP Servidor: ";
	SERVER_VIDEO_SOURCE_LABEL = "Fuente de video: ";
	SERVER_VIDEO_DEVICES_LABEL = "Dispositivos de camara: ";
	CAM_ENABLE_LABEL = "Habilitar camara: ";

	HOST_IP_LOCATION_TOOL_TIP = "Localizacion del servidor (direccion IP)";
	SERVER_PORT_TOOL_TIP = "Puerto usado por el servidor para las peticiones";
	SERVER_COMM_PORT_TOOL_TIP = "Puerto COMM usado por el servidor para comunicarse con el robot vigilante";
	SERVER_RTP_PORT_TOOL_TIP = "Puerto RTP usado por el servidor para transmitir el video a los clientes";
	SERVER_RTP_CLIENTS_IP_MASK_TOOL_TIP = "Mascara Clientes RTP";
	SERVER_VIDEO_DEVICE_LABEL_TOOL_TIP = "Dispositivo de video conectado al robot";
	SERVER_VIDEO_STREAM_URL_LABEL = "URL del stream: ";
	SERVER_VIDEO_STREAM_URL_TOOL_TIP = "URL RTSP/HTTP usada como fuente de video";
	SERVER_RMI_SERVER_TEXT = "Servidor RMI";
	SERVER_CLIENT_SAVE_VIDEO_LOCATION_LABEL = "Ruta Descarga de Videos: ";
	VIDEO_SOURCE_LOCAL_OPTION = "Camara local";
	VIDEO_SOURCE_STREAM_OPTION = "Stream RTSP/HTTP";

	CAM_ENABLE_TEXT = "Habilitar Camara";
	CLIENT_PLAYER_IN_USE_MESSAGE = "Por favor antes cierre la ventana del reproductor";
	ROUTE_DEFINE_NAME_MESSAGE = "Por favor antes de guardar defina el nombre la ruta";
	ROUTE_START_BEFORE_MESSAGE = "Por favor antes de  gaurdar una ruta debes iniciarla";
	ROUTE_EXISTS_MESSAGE = "Esta ruta existe sera sobreescrita";
	ROUTES_NO_CREATED_MESSAGE = "No existen rutas creadas, por favor cambie el tipo de movimiento antes de guardar";
	VIGILANCIE_RUNNING_MESSAGE = "El Robot esta actualmente vigilando,\n si desea realizar vigilancia manual primero debera pararlo a traves del menu de configuracion";
	
	
	ROUTES_SEL_LABEL ="Selecion Ruta: ";

	ROUTE_CONFIGURATION_PANEL_NAME = "Configuracion Ruta";
	
	
	
	EXIT = "Salir";
	START = "Lanzar";
	STOP = "Parar";
	QUIT = "Salir";
	CANCEL = "Cancelar";
	SAVE = "Guardar";
	LOAD = "Cargar";
	CONNECT = "Conectar"; 
	CLOSE = "Cerrar";
	DEVICE = "Dispositivo";
	CHECK_MOVE_POINT = "Movimiento";
	CHECK_TERMAL_POINT = "Termico";
	
	FOLLOW_LINES_MOVE_NAME = "Sigue Lineas";
	ROUTE_MOVE_NAME = "Ruta Programada";
	STATIC_MOVE_NAME = "Estatico";
	MOVE_CONTROL_KIND_SELECTION = new String[3];
	MOVE_CONTROL_KIND_SELECTION[0] = FOLLOW_LINES_MOVE_NAME; 
	MOVE_CONTROL_KIND_SELECTION[1] = ROUTE_MOVE_NAME;
	MOVE_CONTROL_KIND_SELECTION[2] = STATIC_MOVE_NAME; 
	
	PROX_DISTANCE_LABEL = "Distancia proximidad para alarma: ";
	LOW_BATERY_LABEL = "Voltage Bajo para alarma (Max 15V): ";
	CHARGE_BATERY_LABEL = " Valor Voltaje para carga bateria: ";
		
	SCAN_PERIOD_LABEL = "Periodo de Escaneo (Segundos): ";
	SCAN_ORIENTATION_PERIOD_LABEL = "Periodo Orientacion (Segundos): ";
	KILL_SCAN_BY_OPERATION_LABEL = "Cortar scaneos activos al parar vigilancia";
	ROUTE_NAME_LABEL = "Nombre Ruta: ";
	ENABLE_EMAIL_SEND_LABEL = "Habilitar Envio de Correo en Alarmas :";
	EMAIL_RECEIVERS_LABEL = "Destinatarios Envio Correos :";
    
	
	ENABLE = "Habilitar";
	DISABLE = "Deshabilitar";
	ENABLE_SELECTION = new String[2];
	
	EXIT_TOOL_TIP = "Parar el servidor tan pronto como sea seguro";
	INITIAL_STATUS = "Establezca parametros de configuracion y haga click en \"" + START + "\"";
	
	
	FILE_MENU_NAME = "Archivo";
	FILE_MENU_START = "Comenzar Vigilancia";
	FILE_MENU_STOP = "Parar Vigilancia";
	FILE_MENU_CONFIG = "Configuracion";
	FILE_MENU_SCHEDULE = "Funcionamiento Programado";
	
	
	
	OPTIONS_MENU_NAME = "Opciones";
	OPTIONS_MANUAL_VIGILANCE_START = "Empezar Vigilancia Manual";
	OPTIONS_MENU_STOP = "Parar Vigilancia Manual";
	OPTIONS_PROGRAM_ROUTE  = "Programar una nueva ruta";
	OPTIONS_SHOW_VIDEO = "Camara a Tiempo real";
	OPTIONS_SHOW_REAL_TIME_VIDEO_NAME = "Video a Tiempo Real PFC Aplicacion Cliente";
	OPTIONS_MANUAL_CONTROL_WINDOW_NAME = "Control Manual PFC Aplicacion Cliente";
	OPTIONS_PLAYER_PANEL_TITLE = "Reproductor";
	OPTIONS_MANUAL_CONTROL_SEL_PANEL_TITLE = "Seleccion de Control";

	
	HELP_MENU_NAME = "Ayuda";
	HELP_MENU_ABOUT = "Sobre";
	
	CONFIG_WINDOW_NAME = "Configuracion PFC Control Aplicacion Cliente";
	
	CONFIG_MOVE_SENSOR_PANEL_NAME = "Sensor Movimiento";
	CONFIG_TERMAL_SENSOR_PANEL_NAME = "Sensor Termico";
	CONFIG_SCAN_CONF_PANEL_NAME = "Configuracion scaneo sensores";
	CONFIG_HEARTBEAT_PANEL_NAME = "Pulsaciones";
	CONFIG_EMAIL_PANEL_NAME = "Configuracion Envio Correos";
	CONFIG_BATERY_PANEL_NAME = "Configuration De Bateria";
	CONFIG_VIDEO_PANEL_NAME = "Configuracion de Video";
	CONFIG_CONTROL_MOVE_PANEL_NAME = "Control de Movimiento";
	CONFIG_CHECK_ALARM_PANEL_NAME ="Comprobacion de Alarmas Cliente";
	CONFIG_HEARTBEAT_LABEL = "Tiempo para pulsacion (segundos): ";
	CONFIG_HEARTBEAT_TOOL_TIP = "Declara el intervalo de tiempo para la pulsacion entre el servidor y el robot";
	CONFIG_SAVE_VIDEO_ENABLE = "Habilitar Grabacion en Alarmas";
	CONFIG_SAVE_VIDEO_ALL_TIME_ENABLE = "Habilitar Grabacion todo el tiempo";
	CONFIG_SAVE_VIDEO_TIME_LABEL = "Tiempo de grabacion (segundos): ";
	MOVE_CONTROL_LOCKED = "El Control del Robot esta bloqueado por otro Cliente, intentelo mas tarde";
	
	
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
	
	WEEKDAYS[0] = SUNDAY;
	WEEKDAYS[1] = MONDAY;
	WEEKDAYS[2] = TUESDAY;
	WEEKDAYS[3] = WEDNESDAY;
	WEEKDAYS[4] = THURSDAY;
	WEEKDAYS[5] = FRIDAY;
	WEEKDAYS[6] = SATURDAY;
	
	
	SCHEDULE_START_STOP_COMBO[0] = this.START;
	SCHEDULE_START_STOP_COMBO[1] = this.STOP;
	
	ENABLE_SELECTION[0] = this.ENABLE;
	ENABLE_SELECTION[1] = this.DISABLE;
	
	CAMERA_NO_ENABLE_MESSAGE = "El Servidor no tiene habilitado el servicio de camara, las opciones referentes a dicho servicio seran deshabilitadas";
	ERROR_KEY_MOVE_CAMERA_VALUES_MESSAGE = "Algn valor de tecla no es un caracter, Configuracion no guardada";
	ERROR_CONFIG_VALUES_MESSAGE = "Algun valor es incorrecto o no esta rellenada, por favor comprueba antes de guardar";
	ERROR_SAVE_VIEO_ALARM_MESSAGE = "La vigilancia esta corriendo y esta configurada para grabarlo todo, por favor para la vigilancia antes de guardar";
	
	KEY_CAMERA_CONF = "Teclas";
	KEY_UP_SEL_LABEL = "Tecla Movimiento Arriba: ";
    KEY_DOWN_SEL_LABEL = "Tecla Movimiento Abajo: ";
    KEY_RIGHT_SEL_LABEL = "Tecla Movimiento Derecha: ";
    KEY_LEFT_SEL_LABEL = "Tecla Movimiento Izquierda: ";
    KEY_CENTER_SEL_LABEL = "Tecla Movimiento Centro: ";
    CAMERA_KEYS_NOT_OK_MESSAGE = "Existen algunas teclas para el movimiento de la camara no bien definidas, por favor definelas antes de usarla";
	
    VIGILANCE_STOPPED_LABEL = "Vigilancia Parada...";
    VIGILANCE_STARTED_LABEL = "Vigilancia Arrancada...";
    
    HEARTBEAT_ALARM_MESSAGE = "Alarma Pulsacion Detectada ";
    MOVE_ALARM_MESSAGE = "Alarma de Movimiento Detectada ";
    TERMAL_ALARM_MESSAGE = "Alarma Termica Detectada ";
    DISTANCE_ALARM_MESSAGE = "Alarma de proximidad detectada ";
    LOW_VOLTAGE_ALARM_MESSAGE = "Alarma de bajo voltage detectada ";
    STOP_VIGILANCE_MESSAGE = "Vigilancia Parada ";
    START_VIGILANCE_MESSAGE = "Vigilancia Arrancada ";
    LOAD_CONF_MESSAGE = "Cargada Configuracion Servidor ";
    SAVE_CONF_MESSAGE = "Guardada Configuracion Servidor ";
    LOAD_SCHEDULE_CONF_MESSAGE = "Cargada Configuracion Funcionamiento Programado ";
    SAVE_SCHEDULE_CONF_MESSAGE = "Guardada Configuracion Funcionamiento Programado ";

    
    }
}


