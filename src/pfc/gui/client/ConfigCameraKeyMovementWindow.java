package pfc.gui.client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

/**
 * @author rodrigo
 *
 */
public class ConfigCameraKeyMovementWindow extends JFrame{
	
	
	/**
	 * Serialization Id
	 */
	private static final long serialVersionUID = 5502718567411290661L;
	
	
	/**
	 * The Logger instance. All log messages from this class are routed through
	 * this member. The Logger namespace is <code>suncertify.gui</code>.
	 */
	private Logger log = Logger.getLogger("pfc.gui.client");
	
	private Language lang;
	private JTextField cameraKeyUpField = null;
	private JTextField cameraKeyDownField = null;
	private JTextField cameraKeyLeftField = null;
	private JTextField cameraKeyRightField = null;
	private JTextField cameraKeyCenterField = null;
	private GridBagLayout gridbag;
	private GridBagConstraints constraints;
	private SavedConfiguration config;
	private JFrame parent; 
	
	/**
	 * Configuration for the camera control keys window
	 * @param lang language
	 * @param parent parent window
	 */
	public ConfigCameraKeyMovementWindow(Language lang, JFrame parent){
		super();
		
		/* load saved configuration */
		this.config = SavedConfiguration.getSavedConfiguration();
		this.parent = parent;
		
		this.lang = lang;
		this.setResizable(false);
		this.setTitle(lang.KEY_CAMERA_MOVE_CONFIG_WINDOW_NAME);
		this.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent arg0) {setVisible(false);}});
		
		gridbag = new GridBagLayout();
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		this.setLayout(gridbag);
		
		
		
		/* Configure The Camera keys movement */
		
		
		/* Camera Keys Movement Panel */
		JPanel cameraKeysContolPanel = new JPanel();
		cameraKeysContolPanel.setBorder(BorderFactory.createTitledBorder(this.lang.KEY_CAMERA_MOVE_CONFIG_PANEL_NAME));
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(cameraKeysContolPanel, constraints);
		cameraKeysContolPanel.setLayout(gridbag);
		
		/* Up Movement */ 
		JLabel upKeyLabel = new JLabel(this.lang.KEY_UP_SEL_LABEL);
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridwidth = 1;
		gridbag.setConstraints(upKeyLabel, constraints);
		cameraKeysContolPanel.add(upKeyLabel);
		
		cameraKeyUpField = new JTextField(2);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(cameraKeyUpField, constraints);
		cameraKeysContolPanel.add(cameraKeyUpField);
		
		/* Down Movement */ 
		JLabel downKeyLabel = new JLabel(this.lang.KEY_DOWN_SEL_LABEL);
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridwidth = 1;
		gridbag.setConstraints(downKeyLabel, constraints);
		cameraKeysContolPanel.add(downKeyLabel);
		
		cameraKeyDownField = new JTextField(2);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(cameraKeyDownField, constraints);
		cameraKeysContolPanel.add(cameraKeyDownField);
		
		/* Right Movement */ 
		JLabel rightKeyLabel = new JLabel(this.lang.KEY_RIGHT_SEL_LABEL);
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridwidth = 1;
		gridbag.setConstraints(rightKeyLabel, constraints);
		cameraKeysContolPanel.add(rightKeyLabel);
		
		cameraKeyRightField = new JTextField(2);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(cameraKeyRightField, constraints);
		cameraKeysContolPanel.add(cameraKeyRightField);
		
		/* Left Movement */ 
		JLabel leftKeyLabel = new JLabel(this.lang.KEY_LEFT_SEL_LABEL);
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridwidth = 1;
		gridbag.setConstraints(leftKeyLabel, constraints);
		cameraKeysContolPanel.add(leftKeyLabel);
		
		cameraKeyLeftField = new JTextField(2);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(cameraKeyLeftField, constraints);
		cameraKeysContolPanel.add(cameraKeyLeftField);
		
		
		/* Left Movement */ 
		JLabel centerKeyLabel = new JLabel(this.lang.KEY_CENTER_SEL_LABEL);
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridwidth = 1;
		gridbag.setConstraints(centerKeyLabel, constraints);
		cameraKeysContolPanel.add(centerKeyLabel);
		
		cameraKeyCenterField = new JTextField(2);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(cameraKeyCenterField, constraints);
		cameraKeysContolPanel.add(cameraKeyCenterField);
		
		this.add(cameraKeysContolPanel);
		
		
		/* Button Configuration */
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonsPanel.setLayout(gridbag);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
		gridbag.setConstraints(buttonsPanel, constraints);

		JButton saveButton = new JButton(lang.SAVE);
		saveButton.addActionListener(new Save());
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridwidth = 1; //end row
		gridbag.setConstraints(saveButton, constraints);
		buttonsPanel.add(saveButton);
		
		JButton cancelButton = new JButton(lang.CANCEL);
		cancelButton.addActionListener(new Cancel());
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
		gridbag.setConstraints(cancelButton, constraints);
		buttonsPanel.add(cancelButton);

		this.add(buttonsPanel);
		
		
		
		/* Center on the screen of the computer */
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((d.getWidth() - this.getWidth()) / 2);
		int y = (int) ((d.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);
		
		loadKeysConf();
		
		this.pack();
		this.setVisible(true);
	}
	
	
	
	private void loadKeysConf(){
		if (config.getParameter(SavedConfiguration.KEY_CAMERA_UP).length() != 1)
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_UP + " is not a character");
		else	cameraKeyUpField.setText(config.getParameter(SavedConfiguration.KEY_CAMERA_UP));
		
		if (config.getParameter(SavedConfiguration.KEY_CAMERA_DOWN).length() != 1)
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_DOWN + " is not a character");
		else cameraKeyDownField.setText(config.getParameter(SavedConfiguration.KEY_CAMERA_DOWN));
		
		if (config.getParameter(SavedConfiguration.KEY_CAMERA_LEFT).length() != 1)
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_LEFT + " is not a character");
		else cameraKeyRightField.setText(config.getParameter(SavedConfiguration.KEY_CAMERA_RIGHT));
		
		if (config.getParameter(SavedConfiguration.KEY_CAMERA_RIGHT).length() != 1) 
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_RIGHT + " is not a character");
		else	cameraKeyLeftField.setText(config.getParameter(SavedConfiguration.KEY_CAMERA_LEFT));
		
		if (config.getParameter(SavedConfiguration.KEY_CAMERA_CENTER).length() != 1) 
			log.severe("The property: " + SavedConfiguration.KEY_CAMERA_CENTER + " is not a character");
		else 	cameraKeyCenterField.setText(config.getParameter(SavedConfiguration.KEY_CAMERA_CENTER));
	}
	
	private class Save implements ActionListener {

		/**
		 * Save Button Handler.
		 *
		 * @param ae The event triggering the save Button Click.
		 */
		public void actionPerformed(ActionEvent ae) {
			
			if (cameraKeyUpField.getText().length() != 1 || 
					cameraKeyDownField.getText().length() != 1 ||
					cameraKeyLeftField.getText().length() != 1 ||
					cameraKeyRightField.getText().length() != 1 || 
					cameraKeyCenterField.getText().length() != 1){
				log.severe("A Value for a Key is not a character");
				ApplicationRunner.handleAlert(lang.ERROR_KEY_MOVE_CAMERA_VALUES_MESSAGE);
			}
			else {
				config.setParameter(SavedConfiguration.KEY_CAMERA_UP, cameraKeyUpField.getText());
				config.setParameter(SavedConfiguration.KEY_CAMERA_DOWN, cameraKeyDownField.getText());
				config.setParameter(SavedConfiguration.KEY_CAMERA_RIGHT, cameraKeyRightField.getText());
				config.setParameter(SavedConfiguration.KEY_CAMERA_LEFT, cameraKeyLeftField.getText());
				config.setParameter(SavedConfiguration.KEY_CAMERA_CENTER, cameraKeyCenterField.getText());
				
				ManualVigilanceWindow.setKeyCameraUp(cameraKeyUpField.getText());
				ManualVigilanceWindow.setKeyCameraDown(cameraKeyDownField.getText());
				ManualVigilanceWindow.setKeyCameraRight(cameraKeyRightField.getText());
				ManualVigilanceWindow.setKeyCameraLeft(cameraKeyLeftField.getText());
				ManualVigilanceWindow.setKeyCameraCenter(cameraKeyCenterField.getText());
				
			}	
			setVisible(false);
			parent.requestFocus();
		}
	}
	
	
	private class Cancel implements ActionListener {

		/**
		 * Cancel Button Handler.
		 *
		 * @param ae The event triggering the cancel Button Click.
		 */
		public void actionPerformed(ActionEvent ae) {
			setVisible(false);
			parent.requestFocus();
		}
	}

}
