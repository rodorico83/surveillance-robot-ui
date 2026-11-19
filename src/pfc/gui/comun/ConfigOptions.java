package pfc.gui.comun;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;
import java.util.Observable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pfc.gui.ApplicationMode;
import pfc.gui.PositiveIntegerField;
import pfc.gui.client.ConnectionType;
import pfc.language.*;
import pfc.video.*;


/**
 * A common panel used by both the client and server application to specify the
 * configuration options. For the most part these configuration options are the
 * same for both client and server (even if used in different ways). For example
 * the port the server will listen on must be the same port the client will use
 * to connect to the server. So it makes sense to have a common panel so that
 * users can be presented with a familiar layout for all 3 application modes.
 *
 * @author Rodrigo Jimenez
 * @version 1.0
 */
public class ConfigOptions extends JPanel implements ItemListener, ChangeListener {


    /**
     * A version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     * <p>
     * Not that we ever serialize this class of course, but JPanel implements
     * Serializable, so therefore by default we do as well.
     */
    private static final long serialVersionUID = 5165L;

    /**
     * An Observable class so interested users of this class can receive
     * automatic updates whenever user options change.
     */
    private ConfigObservable observerConfigOptions = new ConfigObservable();

    // All user modifiable fields are defined here, along with all buttons.
    // This makes it easy to disable the fields and buttons once the
    // configuration is complete

    private Language lang;

    private JComboBox languageBox;
    private JTextField locationField = new JTextField(15);
    private JTextField portNumber = new PositiveIntegerField(5);
    private JTextField commportNumber = new JTextField(5);
    private JTextField rtpportNumber = new JTextField(5);
    private JComboBox sourceTypeBox;
    private JTextField streamUrlField;

    private JLabel serverPortLabel;


    private JRadioButton rmiOption;
    private JCheckBox enableCamCheck;
    private JComboBox camerasBox;
    private JTextField saveVideoPathField;

    private ActionHandler actionHandler = null;

    private String language = null;
    private String location = null;
    private String port = null;
    private String commport = null;
    private String rtpport = null;
    private String videoSourceType = "local";
    private String videoDevice = null;
    private String videoStreamUrl = "";
    private boolean enableCam = true;
    private String saveVideoPath;

    private ApplicationMode applicationMode = ApplicationMode.STANDALONE_CLIENT;

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger namespace is <code>suncertify.gui</code>.
     */
    private Logger log = Logger.getLogger("pfc.gui");

    /**
     * Creates a new instance of ConfigOptions - the panel for configuring
     * database connectivity.
     *
     * @param applicationMode one of <code>STANDALONE_CLIENT_MODE</code>,
     *                        <code>NETWORK_CLIENT_MODE</code>, or <code>SERVER_MODE</code>.
     * @param language        Language language to use
     * @see ApplicationMode
     */
    public ConfigOptions(ApplicationMode applicationMode, Language language) {
        super();

        this.applicationMode = applicationMode;
        this.lang = language;

        actionHandler = new ActionHandler();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridbag);
        this.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_OPTIONS_PANEL_TITLE));

        // Standard options
        // ensure there is always a gap between components
        constraints.insets = new Insets(2, 2, 2, 2);


        JLabel languageLabel = new JLabel(lang.LANGUAGE_LABEL);
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(languageLabel, constraints);
        this.add(languageLabel);


        languageBox = new JComboBox();
        languageBox.addItemListener(actionHandler);
        languageBox.setName(lang.LANGUAGE_LABEL);
        constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
        constraints.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(languageBox, constraints);
        this.add(languageBox);


        if (applicationMode == ApplicationMode.NETWORK_CLIENT
                || applicationMode == ApplicationMode.SERVER) {

            JLabel hostLocationLabel = new JLabel(lang.SERVER_LOCATION_LABEL);
            constraints.gridwidth = 1;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(hostLocationLabel, constraints);
            this.add(hostLocationLabel);

            constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            constraints.anchor = GridBagConstraints.WEST;
            locationField.setToolTipText(lang.HOST_IP_LOCATION_TOOL_TIP);
            locationField.addFocusListener(actionHandler);
            locationField.setName(lang.SERVER_LOCATION_LABEL);
            gridbag.setConstraints(locationField, constraints);
            this.add(locationField);

        }

        JPanel camerasPanel = new JPanel();
        camerasPanel.setBorder(BorderFactory.createTitledBorder(lang.CONFIG_VIDEO_PANEL_NAME));
        camerasPanel.setLayout(gridbag);
        constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
        constraints.anchor = GridBagConstraints.CENTER;
        gridbag.setConstraints(camerasPanel, constraints);

        enableCamCheck = new JCheckBox(lang.CAM_ENABLE_TEXT);
        enableCamCheck.setMnemonic(KeyEvent.VK_C);
        enableCamCheck.setName(lang.CAM_ENABLE_LABEL);
        enableCamCheck.addChangeListener(this);
        constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
        constraints.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(enableCamCheck, constraints);
        camerasPanel.add(enableCamCheck);

        JLabel serverRTPPortLabel = new JLabel(lang.SERVER_RTP_PORT_LABEL);
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(serverRTPPortLabel, constraints);
        camerasPanel.add(serverRTPPortLabel);

        rtpportNumber.addFocusListener(actionHandler);
        rtpportNumber.setToolTipText(lang.SERVER_RTP_PORT_TOOL_TIP);
        rtpportNumber.setName(lang.SERVER_RTP_PORT_LABEL);
        constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
        constraints.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(rtpportNumber, constraints);
        camerasPanel.add(rtpportNumber);

        if ((applicationMode == ApplicationMode.SERVER)
                || (applicationMode == ApplicationMode.STANDALONE_CLIENT)) {

            JLabel saveVideoPathLabel = new JLabel(lang.SERVER_CLIENT_SAVE_VIDEO_LOCATION_LABEL);
            constraints.gridwidth = 1;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(saveVideoPathLabel, constraints);
            camerasPanel.add(saveVideoPathLabel);

            saveVideoPathField = new JTextField(30);
            saveVideoPathField.setName(lang.SERVER_CLIENT_SAVE_VIDEO_LOCATION_LABEL);
            saveVideoPathField.addFocusListener(actionHandler);
            constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            constraints.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(saveVideoPathField, constraints);
            camerasPanel.add(saveVideoPathField);

            JLabel videoSourceLabel = new JLabel(lang.SERVER_VIDEO_SOURCE_LABEL);
            constraints.gridwidth = 1;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(videoSourceLabel, constraints);
            camerasPanel.add(videoSourceLabel);

            sourceTypeBox = new JComboBox(new String[] {
                    lang.VIDEO_SOURCE_LOCAL_OPTION,
                    lang.VIDEO_SOURCE_STREAM_OPTION
            });
            sourceTypeBox.addItemListener(actionHandler);
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(sourceTypeBox, constraints);
            camerasPanel.add(sourceTypeBox);


            JLabel cameraDeviceLabel = new JLabel(lang.DEVICE + ":");
            constraints.gridwidth = 1;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(cameraDeviceLabel, constraints);
            camerasPanel.add(cameraDeviceLabel);

            // Configuration for the camera
            Vector<String> videoDevices = (Vector<String>) SelectDevice.lookUpDevices();
            if (videoDevices == null) {
                videoDevices = new Vector<String>();
            }
            if (videoDevices.isEmpty()) {
                videoDevices.add("No device found");
            }
            String[] items = (String[]) (String[]) videoDevices.toArray(new String[videoDevices.size()]);

            // add the camera device configuration
            camerasBox = new JComboBox(items);
            camerasBox.addItemListener(actionHandler);
            camerasBox.setToolTipText(lang.SERVER_VIDEO_DEVICE_LABEL_TOOL_TIP);
            camerasBox.setName(lang.SERVER_VIDEO_DEVICES_LABEL);
            constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            constraints.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(camerasBox, constraints);
            camerasPanel.add(camerasBox);

            JLabel streamUrlLabel = new JLabel(lang.SERVER_VIDEO_STREAM_URL_LABEL);
            constraints.gridwidth = 1;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(streamUrlLabel, constraints);
            camerasPanel.add(streamUrlLabel);

            streamUrlField = new JTextField(30);
            streamUrlField.setName(lang.SERVER_VIDEO_STREAM_URL_LABEL);
            streamUrlField.setToolTipText(lang.SERVER_VIDEO_STREAM_URL_TOOL_TIP);
            streamUrlField.addFocusListener(actionHandler);
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(streamUrlField, constraints);
            camerasPanel.add(streamUrlField);

            serverPortLabel = new JLabel(lang.SERVER_COMM_PORT_LABEL);
            constraints.gridwidth = 1;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(serverPortLabel, constraints);
            this.add(serverPortLabel);

            commportNumber.addFocusListener(actionHandler);
            commportNumber.setToolTipText(lang.SERVER_COMM_PORT_TOOL_TIP);
            commportNumber.setName(lang.SERVER_COMM_PORT_LABEL);
            constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            constraints.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(commportNumber, constraints);
            this.add(commportNumber);
        }

        this.add(camerasPanel);

        if ((applicationMode == ApplicationMode.SERVER)
                || (applicationMode == ApplicationMode.NETWORK_CLIENT)) {
            // Build the Server port row if applicable
            constraints.weightx = 0.0;

            JLabel serverPortLabel = new JLabel(lang.SERVER_PORT_LABEL);
            constraints.gridwidth = 1;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(serverPortLabel, constraints);
            this.add(serverPortLabel);

            portNumber.addFocusListener(actionHandler);
            portNumber.setToolTipText(lang.SERVER_PORT_TOOL_TIP);
            portNumber.setName(lang.SERVER_PORT_LABEL);
            constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            constraints.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(portNumber, constraints);
            this.add(portNumber);

            // Build the Server type option row 1 if applicable
            constraints.weightx = 0.0;

            JLabel serverTypeLabel = new JLabel("Server Type: ");
            constraints.gridwidth = 1;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(serverTypeLabel, constraints);
            this.add(serverTypeLabel);
            constraints.weightx = 0.0;
            constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 1;
            rmiOption = new JRadioButton(lang.SERVER_RMI_SERVER_TEXT);
            gridbag.setConstraints(rmiOption, constraints);
            rmiOption.addActionListener(actionHandler);
            rmiOption.setActionCommand(lang.SERVER_RMI_SERVER_TEXT);
            this.add(rmiOption);

            ButtonGroup serverTypesGroup = new ButtonGroup();
            serverTypesGroup.add(rmiOption);
        }
    }

    /**
     * Utility method to inform our observers of any changes the user makes to
     * the parameters on screen.
     *
     * @param updateType Enum specify which field has changed
     * @param payLoad    the new data the user just entered.
     */
    private void updateObservers(OptionUpdate.Updates updateType,
                                 Object payLoad) {
        OptionUpdate update = new OptionUpdate(updateType, payLoad);
        observerConfigOptions.setChanged();
        observerConfigOptions.notifyObservers(update);
    }

    /**
     * A utility class to handle user interactions with the panel. These are
     * not processed by the user of this panel, rather they are processed
     * internally, and an update is then sent to any observers.
     */
    private class ActionHandler implements ActionListener, FocusListener, ItemListener {
        /**
         * {@inheritDoc}
         */

        public void actionPerformed(ActionEvent ae) {
            if (lang.SERVER_RMI_SERVER_TEXT.equals(ae.getActionCommand())) {
                updateObservers(OptionUpdate.Updates.NETWORK_CHOICE_MADE,
                        ConnectionType.RMI);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void focusGained(FocusEvent e) {
            // ignored - we don't do anything special when users enter a field
        }

        /**
         * {@inheritDoc}
         */
        public void focusLost(FocusEvent e) {


            if (lang.SERVER_CLIENT_SAVE_VIDEO_LOCATION_LABEL.equals(e.getComponent().getName())
                    && (!saveVideoPathField.getText().equals(saveVideoPath))) {
                saveVideoPath = saveVideoPathField.getText();
                updateObservers(OptionUpdate.Updates.SAVE_VIDEO_PATH_CHANGED, saveVideoPath.trim());
            }

            if (lang.SERVER_LOCATION_LABEL.equals(e.getComponent().getName())
                    && (!locationField.getText().equals(location))) {
                location = locationField.getText();
                updateObservers(OptionUpdate.Updates.HOST_LOCATION_CHANGED, location.trim());
            }

            if (lang.SERVER_COMM_PORT_LABEL.equals(e.getComponent().getName())
                    && (!commportNumber.getText().equals(commport))) {
                commport = commportNumber.getText();
                updateObservers(OptionUpdate.Updates.COMM_PORT_CHANGED, commport.trim());
            }

            if (lang.SERVER_PORT_LABEL.equals(e.getComponent().getName())
                    && (!portNumber.getText().equals(port))) {
                port = portNumber.getText();
                updateObservers(OptionUpdate.Updates.PORT_CHANGED, port.trim());
            }

            if (lang.SERVER_RTP_PORT_LABEL.equals(e.getComponent().getName())
                    && (!rtpportNumber.getText().equals(rtpport))) {
                rtpport = rtpportNumber.getText();
                updateObservers(OptionUpdate.Updates.RTP_PORT_CHANGED, rtpport.trim());
            }

            if (lang.SERVER_VIDEO_DEVICES_LABEL.equals(e.getComponent().getName())
                    && (!camerasBox.getSelectedItem().equals(videoDevice))) {
                videoDevice = (String) camerasBox.getSelectedItem();
                updateObservers(OptionUpdate.Updates.VIDEO_DEVICE_CHANGED, videoDevice.trim());
            }

            if (streamUrlField != null
                    && lang.SERVER_VIDEO_STREAM_URL_LABEL.equals(e.getComponent().getName())
                    && (!streamUrlField.getText().equals(videoStreamUrl))) {
                videoStreamUrl = streamUrlField.getText();
                updateObservers(OptionUpdate.Updates.VIDEO_STREAM_URL_CHANGED, videoStreamUrl.trim());
            }
        }


        public void itemStateChanged(ItemEvent arg0) {
            if (arg0.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            if (arg0.getSource() == sourceTypeBox) {
                String sourceType = getSelectedSourceTypeValue();
                if (!sourceType.equals(videoSourceType)) {
                    videoSourceType = sourceType;
                    updateVideoSourceControls();
                    updateObservers(OptionUpdate.Updates.VIDEO_SOURCE_TYPE_CHANGED, videoSourceType);
                }
                return;
            }

            if (arg0.getSource() == camerasBox) {
                Object selectedItem = camerasBox.getSelectedItem();
                if (selectedItem != null && !selectedItem.equals(videoDevice)) {
                    videoDevice = (String) selectedItem;
                    updateObservers(OptionUpdate.Updates.VIDEO_DEVICE_CHANGED, videoDevice.trim());
                }
                return;
            }

            if (arg0.getSource() == languageBox && !languageBox.getSelectedItem().equals(language)) {
                language = (String) languageBox.getSelectedItem();
                updateObservers(OptionUpdate.Updates.LANGUAGE_CHANGED, language.trim());
            }
        }
    }


    /**
     * Returns the mode the application will be running in
     * (networked, standalone, etc).
     *
     * @return the mode the application will be running in
     * (networked, standalone, etc).
     * @see ApplicationMode
     */
    public ApplicationMode getApplicationMode() {
        return this.applicationMode;
    }


    /**
     * Sets the contents of the languages field.
     *
     * @param languages String, String with all the permited languages separated by commas
     */
    public void setLanguages(String languages) {
        String[] languagesArray = languages.split(",");
        languageBox.removeItemListener(actionHandler);
        for (int i = 0; i < languagesArray.length; i++) {
            languageBox.addItem(languagesArray[i]);
        }
        languageBox.addItemListener(actionHandler);
    }

    /**
     * Enable the Select language box
     *
     * @param enable boolean
     */
    public void setLanguageBoxEnabled(boolean enable) {
        languageBox.setEnabled(enable);
    }

    /**
     * Sets the contents of the languages field.
     *
     * @param language String, String with all the permited languages separated by commas
     */
    public void setLanguage(String language) {
        languageBox.setSelectedItem(language);
        this.language = language;
        try {
            this.lang = (Language) Class.forName("pfc.language.Language" + language).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the contents of the port number text field.
     *
     * @return the contents of the port number text field.
     */
    public String getPortCommNumberText() {
        return commportNumber.getText();
    }

    /**
     * Sets the contents of the comm port number text field.
     *
     * @param portCommNumber the contents of the port number text field.
     */
    public void setPortCommNumberText(String portCommNumber) {
        commport = portCommNumber;
        this.commportNumber.setText(portCommNumber);
    }

    /**
     * Configures whether the comm port number field is enabled or not.
     *
     * @param enabled true if the port number field is enabled.
     */
    public void setPortCommNumberEnabled(boolean enabled) {
        this.commportNumber.setEnabled(enabled);
    }


    /**
     * @return boolean
     */
    public boolean getCamEnableValue() {
        return enableCamCheck.isSelected();
    }


    public void stateChanged(ChangeEvent e) {

        if (enableCamCheck.isSelected()) {
            setPortRTPNumberEnabled(true);
            if ((this.applicationMode == ApplicationMode.STANDALONE_CLIENT)
                    || (this.applicationMode == ApplicationMode.SERVER)) {
                updateVideoSourceControls();
                setSaveVideoPathEnabled(true);
            }
            updateObservers(OptionUpdate.Updates.CAM_ENABLE_CHANGED, "true");
        } else {
            setPortRTPNumberEnabled(false);
            if ((this.applicationMode == ApplicationMode.STANDALONE_CLIENT)
                    || (this.applicationMode == ApplicationMode.SERVER)) {
                setSaveVideoPathEnabled(false);
                setCameraDevicesEnabled(false);
                setVideoSourceTypeEnabled(false);
                setStreamUrlEnabled(false);
            }
            updateObservers(OptionUpdate.Updates.CAM_ENABLE_CHANGED, "false");
        }
    }


    /**
     * @param enableCam String boolean value in string
     */
    public void setCamEnable(String enableCam) {
        if (enableCam.equals("true")) {
            enableCamCheck.setSelected(true);
            this.enableCam = true;
            setPortRTPNumberEnabled(true);
            updateVideoSourceControls();
        } else {
            enableCamCheck.setSelected(false);
            this.enableCam = false;
            setPortRTPNumberEnabled(false);
            setCameraDevicesEnabled(false);
            setVideoSourceTypeEnabled(false);
            setStreamUrlEnabled(false);
        }
    }

    /**
     * Configures whether the comm port number field is enabled or not.
     *
     * @param enabled true if the port number field is enabled.
     */
    public void setCamEnableCheck(boolean enabled) {
        this.enableCamCheck.setEnabled(enabled);
    }

    /**
     * Returns the contents of the port number text field.
     *
     * @return the contents of the port number text field.
     */
    public String getPortRTPNumberText() {
        return rtpportNumber.getText();
    }

    /**
     * Sets the contents of the save video location text field.
     *
     * @param saveVideoPath String path to save all the video records
     */
    public void setSaveVideoPathText(String saveVideoPath) {
        this.saveVideoPath = saveVideoPath;
        this.saveVideoPathField.setText(saveVideoPath);
    }

    /**
     * Returns the contents of the save video location text field.
     *
     * @return the contents of the port number text field.
     */
    public String getSaveVideoPathText() {
        return saveVideoPathField.getText();
    }

    /**
     * Configures whether the save video path field is enabled or not.
     *
     * @param enabled true if the port number field is enabled.
     */
    public void setSaveVideoPathEnabled(boolean enabled) {
        this.saveVideoPathField.setEnabled(enabled);
    }


    /**
     * Sets the contents of the RTP port number text field.
     *
     * @param portRTPNumber the contents of the rtp number text field.
     */
    public void setPortRTPNumberText(String portRTPNumber) {
        rtpport = portRTPNumber;
        this.rtpportNumber.setText(portRTPNumber);
    }


    /**
     * Configures whether the RTP port number field is enabled or not.
     *
     * @param enabled true if the RTP number field is enabled.
     */
    public void setPortRTPNumberEnabled(boolean enabled) {
        this.rtpportNumber.setEnabled(enabled);
    }

    /**
     * Returns the contents of the camera device text field.
     *
     * @return the contents of the camera device text field.
     */
    public String getCameraDeviceSelection() {
        return (String) camerasBox.getSelectedItem();
    }

    public String getSelectedVideoSource() {
        if ("stream".equals(videoSourceType)) {
            if (streamUrlField == null) {
                return null;
            }
            String streamUrl = streamUrlField.getText().trim();
            return streamUrl.length() == 0 ? null : streamUrl;
        }

        if (camerasBox == null || camerasBox.getSelectedItem() == null) {
            return null;
        }

        String selectedItem = (String) camerasBox.getSelectedItem();
        if ("No device found".equals(selectedItem)) {
            return null;
        }
        return selectedItem;
    }

    /**
     * Returns the contents of the camera device text field.
     *
     * @param device String
     */
    public void setCameraDeviceSelection(String device) {
        if (camerasBox != null && device != null) {
            camerasBox.setSelectedItem(device);
            videoDevice = (String) camerasBox.getSelectedItem();
        }
    }

    /**
     * Configures whether the camera device field is enabled or not.
     *
     * @param enabled true if the camera device field is enabled.
     */
    public void setCameraDevicesEnabled(boolean enabled) {
        if (this.camerasBox != null)
            this.camerasBox.setEnabled(enabled);
    }

    public String getVideoSourceType() {
        return videoSourceType;
    }

    public void setVideoSourceType(String sourceType) {
        if (sourceType == null) {
            sourceType = "local";
        }

        videoSourceType = sourceType;
        if (sourceTypeBox != null) {
            if ("stream".equals(sourceType)) {
                sourceTypeBox.setSelectedItem(lang.VIDEO_SOURCE_STREAM_OPTION);
            } else {
                sourceTypeBox.setSelectedItem(lang.VIDEO_SOURCE_LOCAL_OPTION);
            }
        }
        updateVideoSourceControls();
    }

    public String getStreamUrlText() {
        return streamUrlField == null ? "" : streamUrlField.getText();
    }

    public void setStreamUrlText(String streamUrl) {
        videoStreamUrl = streamUrl == null ? "" : streamUrl;
        if (streamUrlField != null) {
            streamUrlField.setText(videoStreamUrl);
        }
    }

    public void setVideoSourceTypeEnabled(boolean enabled) {
        if (sourceTypeBox != null) {
            sourceTypeBox.setEnabled(enabled);
        }
    }

    public void setStreamUrlEnabled(boolean enabled) {
        if (streamUrlField != null) {
            streamUrlField.setEnabled(enabled);
        }
    }

    /**
     * Returns the contents of the port number text field.
     *
     * @return the contents of the port number text field.
     */
    public String getPortNumberText() {
        return portNumber.getText();
    }

    /**
     * Sets the contents of the port number text field.
     *
     * @param portNumber the contents of the port number text field.
     */
    public void setPortNumberText(String portNumber) {
        port = portNumber;
        this.portNumber.setText(portNumber);
    }

    /**
     * Configures whether the port number field is enabled or not.
     *
     * @param enabled true if the port number field is enabled.
     */
    public void setPortNumberEnabled(boolean enabled) {
        this.portNumber.setEnabled(enabled);
    }

    /**
     * Returns the contents of the server location field.
     *
     * @return the contents of the server location field.
     */
    public String getLocationFieldText() {
        return locationField.getText();
    }

    /**
     * Sets the contents of the server location field.
     *
     * @param locationField the contents of the server location field.
     */
    public void setLocationFieldText(String locationField) {
        location = locationField;
        this.locationField.setText(locationField);
    }

    /**
     * Configures whether the location field is enabled or not.
     *
     * @param enabled true if the location field is enabled.
     */
    public void setLocationFieldEnabled(boolean enabled) {
        this.locationField.setEnabled(enabled);
    }

    /**
     * Sets the network connection type selected for the user
     * in this case is only available the rmi option
     *
     * @param connectionType ConnectionType
     */
    public void setNetworkConnection(ConnectionType connectionType) {
        switch (connectionType) {
            case RMI:
                rmiOption.setSelected(true);
                break;
            default:
                log.warning("Trying to set unknown connection type: "
                        + connectionType);
                break;
        }
    }

    /**
     * Configures whether the RMI option radio button is enabled or not.
     *
     * @param enabled true if the RMI option radio button is enabled.
     */
    public void setRmiOptionEnabled(boolean enabled) {
        this.rmiOption.setEnabled(enabled);
    }


    /**
     * Our Observabe class - a class that Observers can register themselves
     * with in order to recieve updates as things change within this panel.
     */
    private class ConfigObservable extends Observable {
        /**
         * {@inheritDoc}
         */
        public void setChanged() {
            super.setChanged();
        }
    }

    /**
     * Returns an instance of the <code>Observable</code> class. Observers can
     * register themselves with this class in order to recieve updates as
     * things change within this panel.
     *
     * @return an instance of the <code>Observable</code> class.
     */
    public Observable getObservable() {
        return observerConfigOptions;
    }

    private void updateVideoSourceControls() {
        boolean videoEnabled = enableCamCheck.isSelected();
        boolean streamSource = "stream".equals(videoSourceType);
        boolean cameraAvailable = camerasBox != null
                && camerasBox.getSelectedItem() != null
                && !"No device found".equals(camerasBox.getSelectedItem());

        setVideoSourceTypeEnabled(videoEnabled);
        setCameraDevicesEnabled(videoEnabled && !streamSource && cameraAvailable);
        setStreamUrlEnabled(videoEnabled && streamSource);
    }

    private String getSelectedSourceTypeValue() {
        if (sourceTypeBox == null
                || lang.VIDEO_SOURCE_LOCAL_OPTION.equals(sourceTypeBox.getSelectedItem())) {
            return "local";
        }
        return "stream";
    }

    public void itemStateChanged(ItemEvent e) {
    }


}
