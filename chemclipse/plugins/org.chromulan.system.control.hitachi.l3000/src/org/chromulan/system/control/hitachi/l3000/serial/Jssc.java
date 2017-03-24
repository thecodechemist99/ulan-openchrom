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

import org.chromulan.system.control.hitachi.l3000.model.DataReceive;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Jssc extends AbstractSerialPort {

	private boolean addDataread;
	private BaudRate baudRate;
	private String dataControl;
	private String DataControlAnable = "data control enable";
	private String DataControlDisable = "data control disable";
	private Delimiter delimiter;
	private String name;
	private Parity parity;
	private SerialPort serialPort;

	protected Jssc(DataReceive dataReceive) {
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
		try {
			serialPort.addEventListener(new SerialPortEventListener() {

				@Override
				public void serialEvent(SerialPortEvent event) {

					if(event.getEventValue() > 0) {
						try {
							byte[] bb = serialPort.readBytes();
							if(bb != null) {
								for(int i = 0; i < bb.length; i++) {
									addData((char)bb[i]);
								}
							}
						} catch(SerialPortException e) {
							try {
								close();
							} catch(IOException e1) {
							}
						}
					}
				}
			}, SerialPort.MASK_RXCHAR);
		} catch(SerialPortException e) {
			throw new IOException();
		}
		addDataread = true;
	}

	@Override
	public void close() throws IOException {

		if(serialPort != null) {
			try {
				serialPort.closePort();
			} catch(SerialPortException e) {
				throw new IOException();
			}
		}
	}

	@Override
	public BaudRate getBaudeRate() {

		return baudRate;
	}

	@Override
	public boolean getCTS() {

		if(serialPort != null) {
			try {
				boolean b = serialPort.isCTS();
				return b;
			} catch(SerialPortException e) {
			}
		}
		return false;
	}

	@Override
	public String getDataContol() {

		return dataControl;
	}

	@Override
	public String[] getDataControlTypes() {

		return new String[]{this.DataControlAnable, this.DataControlDisable};
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

		return SerialPortList.getPortNames();
	}

	@Override
	public Parity getParity() {

		return parity;
	}

	@Override
	public boolean isOpen() {

		if(serialPort != null) {
			return serialPort.isOpened();
		}
		return false;
	}

	@Override
	public boolean open(String name, BaudRate baudRate, Parity parity, Delimiter delimiter, String dataControl) throws IOException {

		if(serialPort == null || !serialPort.isOpened()) {
			boolean setRTS = false;
			if(dataControl.equals(this.DataControlAnable)) {
				setRTS = true;
			}
			int setParity;
			if(parity.equals(Parity.PARITY_EVEN)) {
				setParity = SerialPort.PARITY_EVEN;
			} else {
				setParity = SerialPort.PARITY_NONE;
			}
			serialPort = new SerialPort(name);
			try {
				serialPort.openPort();
				serialPort.setParams(baudRate.getBaudRate(), AbstractSerialPort.dataBits, AbstractSerialPort.stopBits, setParity, setRTS, false);
				this.delimiter = delimiter;
				this.name = name;
				this.parity = parity;
				this.dataControl = dataControl;
				this.baudRate = baudRate;
				return true;
			} catch(SerialPortException e) {
				throw new IOException();
			}
		}
		return false;
	}

	@Override
	protected boolean sendMsg(byte[] msg) throws IOException {

		if(serialPort != null && serialPort.isOpened()) {
			try {
				serialPort.writeBytes(msg);
				return true;
			} catch(SerialPortException e) {
				throw new IOException();
			}
		}
		return false;
	}

	@Override
	public boolean setParametrs(BaudRate baudRate, Parity parity, Delimiter delimiter, String dataControl) throws IOException {

		if(serialPort != null || serialPort.isOpened()) {
			boolean setRTS = false;
			if(dataControl.equals(this.DataControlAnable)) {
				setRTS = true;
			}
			int setParity;
			if(parity.equals(Parity.PARITY_EVEN)) {
				setParity = SerialPort.PARITY_EVEN;
			} else {
				setParity = SerialPort.PARITY_NONE;
			}
			try {
				serialPort.setParams(baudRate.getBaudRate(), AbstractSerialPort.dataBits, AbstractSerialPort.stopBits, setParity, setRTS, false);
				this.baudRate = baudRate;
				this.delimiter = delimiter;
				this.parity = parity;
				this.dataControl = dataControl;
				return true;
			} catch(SerialPortException e) {
				throw new IOException();
			}
		}
		return false;
	}
}
