package pfc.rs232;

import gnu.io.NoSuchPortException;

import java.io.IOException;
import java.util.Formatter;
import java.util.logging.Logger;

import com.fazecast.jSerialComm.SerialPort;

/**
 * This implements the connection with the serial port.
 */
public class SerialCommunication {

	private String port;
	private SerialPort serialPort;
	private Logger log = Logger.getLogger("pfc.rs323");
	private final Object writeLock = new Object();

	public SerialCommunication(String port) throws NoSuchPortException, Exception
	{
		super();
		this.port = port;
		try {
			this.connect();
		} catch (NoSuchPortException e) {
			log.severe("Port " + port  + " can't be opened.");
			throw e;
		} catch (Exception e) {
			if (serialPort != null) {
				serialPort.closePort();
				log.info("Port " + port  + " closed.");
			}
			throw e;
		}
	}

	public void connect () throws Exception
	{
		serialPort = SerialPort.getCommPort(port);
		if (serialPort == null || serialPort.getSystemPortName() == null || serialPort.getSystemPortName().length() == 0) {
			throw new NoSuchPortException("Port " + port + " was not found.");
		}

		serialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

		if (!serialPort.openPort(300)) {
			log.severe("Error: Port is currently in use or cannot be opened.");
			throw new NoSuchPortException("Port " + port + " cannot be opened.");
		}

		log.info("Serial port " + port + " opened.");
	}

	public void close() throws IOException {
		if (serialPort != null) {
			serialPort.closePort();
			log.info("Port " + port  + " closed.");
		}
	}

	public void write(byte bytes[]) throws IOException {
		synchronized (writeLock) {
			int offset = 0;
			while (offset < bytes.length) {
				int written = serialPort.writeBytes(bytes, bytes.length - offset, offset);
				if (written < 0) {
					throw new IOException("Error writing to serial port " + port);
				}
				offset += written;
			}
		}
	}

	public int read(byte[] buffer, int length) throws IOException {
		int read = serialPort.readBytes(buffer, length);
		if (read < 0) {
			throw new IOException("Error reading from serial port " + port);
		}
		return read;
	}

	public int bytesAvailable() {
		return serialPort.bytesAvailable();
	}

	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);

		Formatter formatter = new Formatter(sb);
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}

		return sb.toString();
	}
	public boolean isOpen() {
		return serialPort != null && serialPort.isOpen();
	}
}
