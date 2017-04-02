/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.hitachi.l3000.serial;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.chromulan.system.control.hitachi.l3000.model.DataReceive;

public abstract class AbstractSerialPort {

	public static enum BaudRate {
		BOUD_RATE_1200(1200), BOUD_RATE_150(150), BOUD_RATE_2400(2400), BOUD_RATE_300(300), BOUD_RATE_4800(4800), BOUD_RATE_600(600), BOUD_RATE_75(75);

		public static BaudRate getBaudeRate(int baud) {

			for(BaudRate baudRate : BaudRate.values()) {
				if(baudRate.getBaudRate() == baud) {
					return baudRate;
				}
			}
			return null;
		}

		private int baudrate;

		private BaudRate(int baudrate) {
			this.baudrate = baudrate;
		}

		public int getBaudRate() {

			return baudrate;
		}
	}

	public static enum Delimiter {
		DELIMITER_CR("\r"), DELIMITER_CRLF("\r\n");

		private String delimiter;

		private Delimiter(String delimiter) {
			this.delimiter = delimiter;
		}

		public String getDelimiter() {

			return delimiter;
		}
	}

	public static enum Parity {
		PARITY_EVEN(), PARITY_NONE();
	}

	static protected int dataBits = 7;
	static protected int stopBits = 2;

	public static AbstractSerialPort getSerialPort(DataReceive dataReceive) {

		return new PureSerialPort(dataReceive);
	}

	private StringBuilder builder = new StringBuilder();
	private DataReceive dataReceive;

	protected AbstractSerialPort(DataReceive dataReceive) {
		this.dataReceive = dataReceive;
	}

	protected void addData(char ch) {

		if((ch == 'D') && (builder.length() > 0)) {
			dataReceive.addScan(builder.toString());
			builder.setLength(0);
		}
		builder.append(ch);
	}

	abstract public void addDataEvent() throws IOException;

	abstract public void close() throws IOException;

	abstract public BaudRate getBaudeRate();

	abstract public boolean getCTS();

	abstract public String getDataContol();

	abstract public String[] getDataControlTypes();

	public DataReceive getDataReceive() {

		return dataReceive;
	}

	abstract public Delimiter getDelimiter();

	abstract public String getName();

	abstract public String[] getNames();

	abstract public Parity getParity();

	abstract public boolean isOpen();

	abstract public boolean open(String name, BaudRate baudRate, Parity parity, Delimiter delimiter, String dataControl) throws IOException;

	public boolean sendDataOutputType(int outputType) throws IOException {

		if(outputType == 0) {
			return sendMessagge("SEND", "0");
		} else if(outputType == 1) {
			return sendMessagge("SEND", "1");
		}
		return false;
	}

	private boolean sendMessagge(String command, String messagge) throws IOException {

		String msg;
		if(messagge != null) {
			msg = new String(command + " " + messagge + getDelimiter().getDelimiter());
		} else {
			msg = new String(command + getDelimiter().getDelimiter());
		}
		return sendMsg(msg.getBytes(StandardCharsets.UTF_8));
	}

	abstract protected boolean sendMsg(byte[] msg) throws IOException;

	public boolean sendStart() throws IOException {

		return sendMessagge("START", null);
	}

	public boolean sendStop() throws IOException {

		return sendMessagge("STOP", null);
	}

	public boolean sendTimeInterval(float timeInterval) throws IOException {

		String StimeInterval = String.format(Locale.US, "%.1f", timeInterval);
		return sendMessagge("TIME", StimeInterval);
	}

	public boolean sendWavelenghtInterval(float wavelenghtInterval) throws IOException {

		String waveLenghtInterval = String.format(Locale.US, "%.1f", wavelenghtInterval);
		return sendMessagge("WL", waveLenghtInterval);
	}

	public boolean sendWaveLenghtRange() throws IOException {

		// TODO:body of function
		return false;
	}

	abstract public boolean setParametrs(BaudRate baudRate, Parity parity, Delimiter delimiter, String dataControl) throws IOException;
}
