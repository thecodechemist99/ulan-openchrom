/*******************************************************************************
 * Copyright (c) 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.hitachi.l3000.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.setting.DeviceSetting;
import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.device.setting.IValue;
import org.chromulan.system.control.device.setting.ValueBoolean;
import org.chromulan.system.control.device.setting.ValueEnumeration;
import org.chromulan.system.control.device.setting.ValueFloat;
import org.chromulan.system.control.device.setting.ValueInt;
import org.chromulan.system.control.hitachi.l3000.Activator;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class ControlDevice implements IControlDevice {

	public static final int BOUD_RATE_1200 = 1200;
	public static final int BOUD_RATE_150 = 150;
	public static final int BOUD_RATE_2400 = 2400;
	public static final int BOUD_RATE_300 = 300;
	public static final int BOUD_RATE_4800 = 4800;
	public static final int BOUD_RATE_600 = 600;
	public static final int BOUD_RATE_75 = 75;
	public static final String DELIMITER_CR = "\r";
	public static final String DELIMITER_CRLF = "\r\n";
	public final static int OUTPUT_ANALOG = 0;
	public final static int OUTPUT_DIGITAL = 1;
	public final static String PROPERTY_AUTOSET_VALUE = "autoSetValue";
	public final static String PROPERTY_DATA_OUTPUT_ANALOG = "analogOutput";
	public final static String PROPERTY_DATA_OUTPUT_DATA_COMMUNICATION = "dataCommunication";
	public final static String PROPERTY_NAME = "name";
	public final static String PROPERTY_OUTPUT_TYPE = "outputType";
	public final static String PROPERTY_SEND_START = "sendStart";
	public final static String PROPERTY_SEND_STOP = "sendStop";
	public final static String PROPERTY_TIME_INTERVAL = "timeInterval";
	public final static String PROPERTY_WAVELENGHT_INTERVA = "wavelenghtInterval";
	public final static String PROPERTY_WAVELENGHT_RANGE_FROM = "wavelenghtRangeFrom";
	public final static String PROPERTY_WAVELENGHT_RANGE_TO = "wavelenghtRangeTo";
	public static final String SETTING_DATA_OUTPUT_ANALOG = "analog output";
	public static final String SETTING_DATA_OUTPUT_DATA_COMMUNICATION = "data communication output";
	public static final String SETTING_DATA_OUTPUT_TYPE = "output type";
	public static final String SETTING_SEND_START = "send start";
	public static final String SETTING_SEND_STOP = "send stop";
	public static final String SETTING_TIME_INTERVAL = "time interval";
	public static final String SETTING_WAVELENGHT_INTERVAL = "wavelenght interval";
	public static final String SETTING_WAVELENGHT_RANGE_FROM = "wavelenght range from";
	public static final String SETTING_WAVELENGHT_RANGE_TO = "wavelenght range to";
	private boolean autoSetValue;
	private final String COMPANY = "HITACHI";
	private final int DATA_OUTPUT_ANALOG_VALUE = 0;
	private final int DATA_OUTPUT_DATA_COMMUNICATION_VALUE = 1;
	private DataReceive dataReceive;
	private IDeviceSetting deviceSetting;
	private boolean isPrepare;
	private final String MODEL = "L3000";
	private String name;
	private int outputType;
	private int portBaudRate;
	private boolean portDataControlSignal;
	private String portDelimeter;
	private boolean portEventParity;
	private String portName;
	private PropertyChangeSupport propertyChangeSupport;
	private boolean sendStart;
	private boolean sendStop;
	private SerialPort serialPort;
	private float timeInterval;
	private float wavelenghtInterval;
	private int wavelenghtRangeFrom;
	private int wavelenghtRangeTo;

	public ControlDevice() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.isPrepare = true;
		deviceSetting = create();
		setName(COMPANY + " " + MODEL);
		setOutputType(OUTPUT_DIGITAL);
		setTimeInterval(1);
		setWavelenghtInterval(5);
		setWavelenghtRangeFrom(200);
		setWavelenghtRangeTo(360);
		setSendStart(false);
		setSendStop(false);
		portName = "";
		portBaudRate = BOUD_RATE_4800;
		portDelimeter = DELIMITER_CR;
		portDataControlSignal = true;
		portEventParity = true;
		dataReceive = new DataReceive(this);
	}

	private void addDataEvent() throws SerialPortException {

		final StringBuilder builder = new StringBuilder();
		serialPort.addEventListener(new SerialPortEventListener() {

			@Override
			public void serialEvent(SerialPortEvent arg) {

				if(arg.isRXCHAR() && arg.getEventValue() > 0) {
					byte[] messagge;
					try {
						messagge = serialPort.readBytes();
						for(int i = 0; i < messagge.length; i++) {
							if(messagge[i] == 'D') {
								builder.append((char)messagge[i]);
							} else {
								if(builder.length() != 0) {
									if(messagge[i] == '\r') {
										dataReceive.addScan(builder.toString());
										builder.setLength(0);
									} else {
										builder.append((char)messagge[i]);
									}
								}
							}
						}
					} catch(SerialPortException e) {
						// TODO:logger.warn(e);
					}
				}
			}
		}, SerialPort.MASK_RXCHAR);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {

		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {

		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void closeSerialPort() throws SerialPortException {

		if(serialPort != null) {
			serialPort.closePort();
		}
	}

	private IDeviceSetting create() {

		IDeviceSetting deviceSetting = new DeviceSetting(getPluginID(), "settings", COMPANY + " " + MODEL, getDeviceID());
		HashMap<String, IValue<?>> values = deviceSetting.getValues();
		values.put(PROPERTY_TIME_INTERVAL, new ValueFloat(deviceSetting, PROPERTY_TIME_INTERVAL, SETTING_TIME_INTERVAL, 0.1f, "sec", 1).setPrintable(false));
		values.put(PROPERTY_WAVELENGHT_INTERVA, new ValueFloat(deviceSetting, PROPERTY_WAVELENGHT_INTERVA, SETTING_WAVELENGHT_INTERVAL, 5f, "nm", 1));
		values.put(PROPERTY_WAVELENGHT_RANGE_FROM, new ValueInt(deviceSetting, PROPERTY_WAVELENGHT_RANGE_FROM, SETTING_WAVELENGHT_RANGE_FROM, 200, "nm"));
		values.put(PROPERTY_WAVELENGHT_RANGE_TO, new ValueInt(deviceSetting, PROPERTY_WAVELENGHT_RANGE_TO, SETTING_WAVELENGHT_RANGE_TO, 360, "nm"));
		values.put(PROPERTY_SEND_START, new ValueBoolean(deviceSetting, PROPERTY_SEND_START, SETTING_SEND_START, false));
		values.put(PROPERTY_SEND_STOP, new ValueBoolean(deviceSetting, PROPERTY_SEND_STOP, SETTING_SEND_STOP, false));
		IValue<?>[] outPutType = new IValue<?>[]{new ValueInt(deviceSetting, PROPERTY_DATA_OUTPUT_ANALOG, SETTING_DATA_OUTPUT_ANALOG, DATA_OUTPUT_ANALOG_VALUE, "").setChangeable(false).setPrintable(false), new ValueInt(deviceSetting, PROPERTY_DATA_OUTPUT_DATA_COMMUNICATION, SETTING_DATA_OUTPUT_ANALOG, DATA_OUTPUT_DATA_COMMUNICATION_VALUE, "").setChangeable(false).setPrintable(false)};
		values.put(PROPERTY_OUTPUT_TYPE, new ValueEnumeration(deviceSetting, PROPERTY_OUTPUT_TYPE, SETTING_DATA_OUTPUT_TYPE, outPutType, 1));
		return deviceSetting;
	}

	@Override
	protected void finalize() throws Throwable {

		try {
			closeSerialPort();
		} finally {
			super.finalize();
		}
	}

	@Override
	public String getCompany() {

		return COMPANY;
	}

	public DataReceive getDatareceive() {

		return dataReceive;
	}

	@Override
	public String getDescription() {

		if(serialPort != null) {
			return "Port Name: " + serialPort.getPortName();
		}
		return "";
	}

	@Override
	public String getDeviceID() {

		return COMPANY + " " + MODEL;
	}

	@Override
	public IDeviceSetting getDeviceSetting() {

		return deviceSetting;
	}

	@Override
	public int getFlg() {

		return FLG_SUPPORT_CSD_CHROMATOGRAM;
	}

	@Override
	public String getModel() {

		return MODEL;
	}

	@Override
	public String getName() {

		return name;
	}

	public int getOutputType() {

		return outputType;
	}

	@Override
	public String getPluginID() {

		return Activator.PLUGIN_ID;
	}

	public int getPortBaudRate() {

		return portBaudRate;
	}

	public String getPortDelimeter() {

		return portDelimeter;
	}

	public String getPortName() {

		return portName;
	}

	public float getTimeInterval() {

		return timeInterval;
	}

	public int getTimeIntervalMill() {

		return (int)(timeInterval * 1000);
	}

	public float getWavelenghtInterval() {

		return wavelenghtInterval;
	}

	public int getWavelenghtRangeFrom() {

		return wavelenghtRangeFrom;
	}

	public int getWavelenghtRangeTo() {

		return wavelenghtRangeTo;
	}

	public boolean isAutoSetValue() {

		return autoSetValue;
	}

	@Override
	public boolean isConnected() {

		if(serialPort == null) {
			return false;
		}
		return serialPort.isOpened();
	}

	public boolean isPortDataControlSignal() {

		return portDataControlSignal;
	}

	public boolean isPortEventParity() {

		return portEventParity;
	}

	@Override
	public boolean isPrepared() {

		return isPrepare;
	}

	public boolean isSendStart() {

		return sendStart;
	}

	public boolean isSendStop() {

		return sendStop;
	}

	public void openSerialPort(String portName, int portBaudRate, boolean portEventParity, boolean portDataControlSignal, String portDelimiter) throws SerialPortException {

		if(serialPort == null) {
			serialPort = new SerialPort(portName);
		} else {
			if(!serialPort.isOpened() && !serialPort.getPortName().equals(portName)) {
				serialPort = new SerialPort(portName);
			}
		}
		this.portName = portName;
		if(!serialPort.isOpened()) {
			serialPort.openPort();
			setPortParameters(portBaudRate, portEventParity, portDataControlSignal, portDelimiter);
			addDataEvent();
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		String name = (String)in.readObject();
		int outPutType = in.readInt();
		float timeInterval = in.readFloat();
		float wavelenghtInterval = in.readFloat();
		int wavelenghtRangeFrom = in.readInt();
		int wavelenghtRangeTo = in.readInt();
		boolean sendStart = in.readBoolean();
		boolean sendStop = in.readBoolean();
		boolean autosetValue = in.readBoolean();
		String portName = (String)in.readObject();
		int portBaudRate = in.readInt();
		String portDelimeter = (String)in.readObject();
		boolean portDataControlSignal = in.readBoolean();
		boolean portEventParity = in.readBoolean();
		setName(name);
		setOutputType(outPutType);
		setTimeInterval(timeInterval);
		setWavelenghtInterval(wavelenghtInterval);
		setWavelenghtRangeFrom(wavelenghtRangeFrom);
		setWavelenghtRangeTo(wavelenghtRangeTo);
		setSendStart(sendStart);
		setSendStop(sendStop);
		setAutoSetValue(autosetValue);
		this.portName = portName;
		this.portBaudRate = portBaudRate;
		this.portDelimeter = portDelimeter;
		this.portDataControlSignal = portDataControlSignal;
		this.portEventParity = portEventParity;
		this.dataReceive = new DataReceive(this);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {

		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {

		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void sendDataOutputType() throws SerialPortException {

		if(outputType == OUTPUT_ANALOG) {
			sendMessagge("SEND", " 0");
		} else if(outputType == OUTPUT_DIGITAL) {
			sendMessagge("SEND", " 1");
		}
	}

	private boolean sendMessagge(String command, String messagge) throws SerialPortException {

		if(serialPort != null && serialPort.isOpened()) {
			if(messagge != null) {
				return serialPort.writeString(command + " " + messagge + portDelimeter);
			} else {
				return serialPort.writeString(command + portDelimeter);
			}
		}
		return false;
	}

	public void sendStart() throws SerialPortException {

		sendMessagge("START", null);
	}

	public void sendStop() throws SerialPortException {

		sendMessagge("STOP", null);
	}

	public void sendTimeInterval() throws SerialPortException {

		String timeInterval = String.format("%.1f", this.timeInterval);
		sendMessagge("TIME", timeInterval);
	}

	public void sendWavelenghtInterval() throws SerialPortException {

		String waveLenghtInterval = String.format("%.1f", this.wavelenghtInterval);
		sendMessagge("WL", waveLenghtInterval);
	}

	public void sendWaveLenghtRange() {

		// TODO:body of function
	}

	public void setAutoSetValue(boolean autoSetValue) {

		propertyChangeSupport.firePropertyChange(PROPERTY_AUTOSET_VALUE, this.autoSetValue, autoSetValue);
		this.autoSetValue = autoSetValue;
	}

	@Override
	public void setDeviceSetting(IDeviceSetting deviceSetting) {

		this.deviceSetting = deviceSetting;
	}

	public void setName(String name) {

		propertyChangeSupport.firePropertyChange(PROPERTY_NAME, this.name, name);
		this.name = name;
	}

	public void setOutputType(int outputType) {

		propertyChangeSupport.firePropertyChange(PROPERTY_OUTPUT_TYPE, this.outputType, outputType);
		ValueEnumeration enumeration = (ValueEnumeration)deviceSetting.getValues().get(PROPERTY_OUTPUT_TYPE);
		enumeration.setValueByOrder(outputType);
		this.outputType = outputType;
	}

	public void setPortParameters(int portBaudRate, boolean portEventParity, boolean portDataControlSignal, String portDelimiter) throws SerialPortException {

		if(serialPort != null && serialPort.isOpened()) {
			if(portEventParity) {
				serialPort.setParams(portBaudRate, 7, 2, SerialPort.PARITY_EVEN, portDataControlSignal, false);
			} else {
				serialPort.setParams(portBaudRate, 7, 2, SerialPort.PARITY_NONE, portDataControlSignal, false);
			}
			this.portBaudRate = portBaudRate;
			this.portEventParity = portEventParity;
			this.portDataControlSignal = portDataControlSignal;
			this.portDelimeter = portDelimiter;
		}
	}

	public void setPrepare(boolean isPrepare) {

		this.isPrepare = isPrepare;
	}

	public void setSendStart(boolean sendStart) {

		propertyChangeSupport.firePropertyChange(PROPERTY_SEND_START, this.sendStart, sendStart);
		ValueBoolean valuse = (ValueBoolean)deviceSetting.getValues().get(PROPERTY_SEND_START);
		valuse.setValue(sendStart);
		this.sendStart = sendStart;
	}

	public void setSendStop(boolean sendStop) {

		propertyChangeSupport.firePropertyChange(PROPERTY_SEND_STOP, this.sendStop, sendStop);
		ValueBoolean value = (ValueBoolean)deviceSetting.getValues().get(PROPERTY_SEND_STOP);
		value.setValue(sendStop);
		this.sendStop = sendStop;
	}

	public void setTimeInterval(float timeInterval) {

		propertyChangeSupport.firePropertyChange(PROPERTY_TIME_INTERVAL, this.timeInterval, timeInterval);
		ValueFloat value = (ValueFloat)deviceSetting.getValues().get(PROPERTY_TIME_INTERVAL);
		value.setValue(timeInterval);
		this.timeInterval = timeInterval;
	}

	public void setWavelenghtInterval(float wavelenghtInterval) {

		propertyChangeSupport.firePropertyChange(PROPERTY_WAVELENGHT_INTERVA, this.wavelenghtInterval, wavelenghtInterval);
		ValueFloat value = (ValueFloat)deviceSetting.getValues().get(PROPERTY_WAVELENGHT_INTERVA);
		value.setValue(wavelenghtInterval);
		this.wavelenghtInterval = wavelenghtInterval;
	}

	public void setWavelenghtRangeFrom(int wavelenghtRangeFrom) {

		propertyChangeSupport.firePropertyChange(PROPERTY_WAVELENGHT_RANGE_FROM, this.wavelenghtRangeFrom, wavelenghtRangeFrom);
		ValueInt value = (ValueInt)deviceSetting.getValues().get(PROPERTY_WAVELENGHT_RANGE_FROM);
		value.setValue(wavelenghtRangeFrom);
		this.wavelenghtRangeFrom = wavelenghtRangeFrom;
	}

	public void setWavelenghtRangeTo(int wavelenghtRangeTo) {

		propertyChangeSupport.firePropertyChange(PROPERTY_WAVELENGHT_RANGE_TO, this.wavelenghtRangeTo, wavelenghtRangeTo);
		ValueInt value = (ValueInt)deviceSetting.getValues().get(PROPERTY_WAVELENGHT_RANGE_TO);
		value.setValue(wavelenghtRangeTo);
		this.wavelenghtRangeTo = wavelenghtRangeTo;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeObject(name);
		out.writeInt(outputType);
		out.writeFloat(timeInterval);
		out.writeFloat(wavelenghtInterval);
		out.writeInt(wavelenghtRangeFrom);
		out.writeInt(wavelenghtRangeTo);
		out.writeBoolean(sendStart);
		out.writeBoolean(sendStop);
		out.writeBoolean(autoSetValue);
		out.writeObject(portName);
		out.writeInt(portBaudRate);
		out.writeObject(portDelimeter);
		out.writeBoolean(portDataControlSignal);
		out.writeBoolean(portEventParity);
	}
}
