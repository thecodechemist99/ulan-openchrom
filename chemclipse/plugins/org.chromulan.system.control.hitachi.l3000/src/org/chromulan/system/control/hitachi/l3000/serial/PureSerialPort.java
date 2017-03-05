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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.chromulan.system.control.hitachi.l3000.model.DataReceive;

import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;
import purejavacomm.UnsupportedCommOperationException;

public class PureSerialPort extends AbstractSerialPort {

	private boolean addDataread;
	private BaudRate baudRate;
	private String dataControl;
	private String DataControlAnable = "data control enable";
	private String DataControlDisable = "data control disable";
	private Delimiter delimiter;
	private OutputStream inputStream;
	private String name;
	private Parity parity;
	private CommPortIdentifier portId;
	private SerialPort serialPort;

	protected PureSerialPort(DataReceive dataReceive) {
		super(dataReceive);
		dataControl = DataControlAnable;
		this.baudRate = BaudRate.BOUD_RATE_4800;
		this.delimiter = Delimiter.DELIMITER_CR;
		this.parity = Parity.PARITY_EVEN;
		this.name = "";
	}

	@Override
	public void addDataEvent() throws IOException {

		if(serialPort == null || addDataread) {
			return;
		}
		final InputStream inputStream = serialPort.getInputStream();
		try {
			serialPort.addEventListener(new SerialPortEventListener() {

				@Override
				public void serialEvent(SerialPortEvent arg0) {

					try {
						int ch;
						while((ch = inputStream.read()) != -1) {
							addData((char)ch);
						}
					} catch(IOException e) {
						serialPort.close();
					}
				}
			});
			addDataread = true;
		} catch(TooManyListenersException e) {
		}
	}

	@Override
	public void close() throws IOException {

		if(serialPort != null) {
			serialPort.close();
		}
	}

	@Override
	public BaudRate getBaudeRate() {

		return baudRate;
	}

	@Override
	public boolean getCTS() {

		if(serialPort != null) {
			return serialPort.isCTS();
		} else {
			return false;
		}
	}

	@Override
	public String getDataContol() {

		return dataControl;
	}

	@Override
	public String[] getDataControlTypes() {

		return new String[]{DataControlAnable, DataControlDisable};
	}

	@Override
	public Delimiter getDelimiter() {

		return delimiter;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String[] getNames() {

		ArrayList<String> names = new ArrayList<>();
		Enumeration<CommPortIdentifier> enumerations = CommPortIdentifier.getPortIdentifiers();
		while(enumerations.hasMoreElements()) {
			CommPortIdentifier commPortIdentifier = enumerations.nextElement();
			String name = commPortIdentifier.getName();
			int portType = commPortIdentifier.getPortType();
			if(portType == CommPortIdentifier.PORT_SERIAL) {
				names.add(name);
			}
		}
		String[] array = new String[names.size()];
		return names.toArray(array);
	}

	@Override
	public Parity getParity() {

		return parity;
	}

	@Override
	public boolean isOpen() {

		if(serialPort == null) {
			return false;
		}
		return portId.isCurrentlyOwned();
	}

	@Override
	public boolean open(String name, BaudRate baudRate, Parity parity, Delimiter delimiter, String dataControl) throws IOException {

		if(!isOpen() && name != null && baudRate != null && delimiter != null && dataControl != null && (dataControl.equals(DataControlAnable) || dataControl.equals(DataControlDisable))) {
			try {
				portId = CommPortIdentifier.getPortIdentifier(name);
				if(portId == null) {
					return false;
				}
				serialPort = (purejavacomm.SerialPort)portId.open("OpenChrom-Hitachi-L3000", 1000);
				this.name = name;
				if(parity.equals(Parity.PARITY_EVEN)) {
					serialPort.setSerialPortParams(getBaudeRate().getBaudRate(), dataBits, stopBits, SerialPort.PARITY_EVEN);
				} else {
					serialPort.setSerialPortParams(getBaudeRate().getBaudRate(), dataBits, stopBits, SerialPort.PARITY_NONE);
				}
				if(dataControl.equals(DataControlAnable)) {
					serialPort.setRTS(true);
				} else {
					serialPort.setRTS(false);
				}
				serialPort.notifyOnDataAvailable(true);
				inputStream = serialPort.getOutputStream();
				this.baudRate = baudRate;
				this.delimiter = delimiter;
				this.parity = parity;
				this.dataControl = dataControl;
				this.addDataread = false;
				return true;
			} catch(NoSuchPortException e) {
				throw new IOException("No Such Port Exception");
			} catch(PortInUseException e) {
				throw new IOException("Port In use Exception");
			} catch(UnsupportedCommOperationException e) {
				throw new IOException("Unsupported Comm Operation Exception");
			}
		}
		return false;
	}

	@Override
	protected boolean sendMsg(byte[] msg) throws IOException {

		if(!isOpen()) {
			return false;
		}
		inputStream.write(msg);
		inputStream.flush();
		return true;
	}

	@Override
	public boolean setParametrs(BaudRate baudRate, Parity parity, Delimiter delimiter, String dataControl) throws IOException {

		if(isOpen() && baudRate != null && delimiter != null && dataControl != null && (dataControl.equals(DataControlAnable) || dataControl.equals(DataControlDisable))) {
			try {
				if(parity.equals(Parity.PARITY_EVEN)) {
					serialPort.setSerialPortParams(getBaudeRate().getBaudRate(), dataBits, stopBits, SerialPort.PARITY_EVEN);
				} else {
					serialPort.setSerialPortParams(getBaudeRate().getBaudRate(), dataBits, stopBits, SerialPort.PARITY_NONE);
				}
				if(dataControl.equals(DataControlAnable)) {
					serialPort.setRTS(true);
					serialPort.getFlowControlMode();
				} else {
					serialPort.setRTS(false);
				}
				this.baudRate = baudRate;
				this.delimiter = delimiter;
				this.parity = parity;
				this.dataControl = dataControl;
				return true;
			} catch(UnsupportedCommOperationException e) {
				throw new IOException("Unsupported Comm Operation Exception");
			}
		}
		return false;
	}
}
