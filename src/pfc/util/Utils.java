package pfc.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import pfc.control.configuration.SavedConfiguration;
import pfc.control.constants.Operations;

/**
 * Utilitis
 * @author rodrigo
 *
 */
public class Utils {


    	/**
    	 * get actual String timestamp with format 
    	 * @param date Date to format
    	 * @return String 
    	 */
	public static String getStringDate(Date date){
		String result = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss");
		result = formatter.format(date);
		return result;
	}

	
	/**
	 * Transform char move operation to string
	 * @param move char
	 * @return String
	 */
	public static String getStringMove(char move){
		String result = null;
		if (move == Operations.FORWARD_MOVE) {
			result = "FORWARD_MOVE";
		}
		else if (move == Operations.BACKWARD_MOVE) {
			result = "BACKWARD_MOVE";
		}
		else if (move == Operations.LEFT_MOVE) {
			result = "LEFT_MOVE";
		}
		else if (move == Operations.RIGHT_MOVE) {
			result = "RIGHT_MOVE";
		}
		return result;
	}


	/**
	 * Send email
	 * @param properties properties to get some configuration
	 * @param subject email subject
	 * @param hosts email receivers hosts
	 * @param content email content
	 * @throws Exception Email send exception
	 */
	public static void sendMail(SavedConfiguration properties, String subject, String hosts[], String content) throws Exception{
		Logger log = Logger.getLogger("pcf.util");
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", properties.getParameter(SavedConfiguration.MAIL_TRANSPORT_PROTOCOL));
		props.setProperty("mail.host", properties.getParameter(SavedConfiguration.MAIL_HOST));
		props.setProperty("mail.user", properties.getParameter(SavedConfiguration.MAIL_USER));
		props.setProperty("mail.password", properties.getParameter(SavedConfiguration.MAIL_PASSWORD));
		props.setProperty("mail.auth", "true");
		
        Session mailSession = Session.getDefaultInstance(props);
		Transport transport = mailSession.getTransport();

		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(subject);
		message.setFrom(new InternetAddress("rodrigojimen@gmail.com"));
		
		message.setContent(content, "text/plain");
		for (int i = 0; i < hosts.length;  i++)
		message.addRecipient(Message.RecipientType.TO,
				new InternetAddress(hosts[i]));

		log.info("Send email: " + message.getSubject() + " Destiny: " + hosts.toString());
		transport.connect(properties.getParameter(SavedConfiguration.MAIL_USER), properties.getParameter(SavedConfiguration.MAIL_PASSWORD));
		transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}
	

}
