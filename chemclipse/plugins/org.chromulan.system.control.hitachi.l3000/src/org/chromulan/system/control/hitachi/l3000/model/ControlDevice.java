/*******************************************************************************
 * Copyright (c) 2016, 2017 Jan Holy.
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
import java.util.Map.Entry;

import javax.inject.Inject;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.setting.DeviceSetting;
import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.device.setting.IValue;
import org.chromulan.system.control.device.setting.ValueBoolean;
import org.chromulan.system.control.device.setting.ValueEnumeration;
import org.chromulan.system.control.device.setting.ValueFloat;
import org.chromulan.system.control.device.setting.ValueInt;
import org.chromulan.system.control.hitachi.l3000.Activator;
import org.chromulan.system.control.hitachi.l3000.serial.AbstractSerialPort;
import org.chromulan.system.control.hitachi.l3000.serial.AbstractSerialPort.BaudRate;
import org.chromulan.system.control.hitachi.l3000.serial.AbstractSerialPort.Delimiter;
import org.chromulan.system.control.hitachi.l3000.serial.AbstractSerialPort.Parity;
import org.chromulan.system.control.manager.devices.DataSupplier;
import org.chromulan.system.control.model.IChromatogramWSDAcquisition;

public class ControlDevice implements IControlDevice {

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
	private DataReceive dataReceive;
	@Inject
	private DataSupplier dataSupplier;
	private IDeviceSetting deviceSetting;
	private boolean isPrepare;
	private final String MODEL = "L3000";
	private String name;
	private int outputType;
	private BaudRate portBaudRate;
	private String portDataControlSignal;
	private Delimiter portDelimeter;
	private String portName;
	private Parity portParity;
	final private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private boolean sendStart;
	private boolean sendStop;
	private AbstractSerialPort serialPort;
	private float timeInterval;
	private float wavelenghtInterval;
	private int wavelenghtRangeFrom;
	private int wavelenghtRangeTo;

	public ControlDevice() {
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
		this.dataReceive = new DataReceive("hitachi l3000", 1 * 1000, 5, 200, 360);
		this.serialPort = AbstractSerialPort.getSerialPort(dataReceive);
		portParity = serialPort.getParity();
		portBaudRate = serialPort.getBaudeRate();
		portName = serialPort.getName();
		portDelimeter = serialPort.getDelimiter();
		portDataControlSignal = serialPort.getDataContol();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {

		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {

		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void closeSerialPort() {

		try {
			serialPort.close();
		} catch(IOException e) {
		}
		if(dataSupplier != null) {
			dataSupplier.updateControlDevices();
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
		IValue<?>[] outPutType = new IValue<?>[]{new ValueInt(deviceSetting, PROPERTY_DATA_OUTPUT_ANALOG, SETTING_DATA_OUTPUT_ANALOG, OUTPUT_ANALOG, "").setChangeable(false).setPrintable(false), new ValueInt(deviceSetting, PROPERTY_DATA_OUTPUT_DATA_COMMUNICATION, SETTING_DATA_OUTPUT_ANALOG, OUTPUT_DIGITAL, "").setChangeable(false).setPrintable(false)};
		values.put(PROPERTY_OUTPUT_TYPE, new ValueEnumeration(deviceSetting, PROPERTY_OUTPUT_TYPE, SETTING_DATA_OUTPUT_TYPE, outPutType, 1));
		return deviceSetting;
	}

	@Override
	public String getCompany() {

		return COMPANY;
	}

	public boolean getCTS() {

		return serialPort.getCTS();
	}

	public String[] getDataControlTypes() {

		return serialPort.getDataControlTypes();
	}

	@Override
	public String getDescription() {

		if(serialPort != null) {
			return "Port Name: " + serialPort.getName();
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
	public long getFlg() {

		return FLG_SUPPORT_CSD_CHROMATOGRAM;
	}

	public IChromatogramWSDAcquisition getChromatogram() {

		return dataReceive.getChromatogram();
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

	public BaudRate getPortBaudRate() {

		return portBaudRate;
	}

	public String getPortDataControlSignal() {

		return portDataControlSignal;
	}

	public Delimiter getPortDelimeter() {

		return portDelimeter;
	}

	public String getPortName() {

		return portName;
	}

	public Parity getPortParity() {

		return portParity;
	}

	public String[] getSerialPortNames() {

		return serialPort.getNames();
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

		return serialPort.isOpen();
	}

	@Override
	public boolean isPrepared() {

		return isPrepare;
	}

	public boolean isSavaData() {

		return this.dataReceive.isSaveData();
	}

	public boolean isSendStart() {

		return sendStart;
	}

	public boolean isSendStop() {

		return sendStop;
	}

	public boolean openSerialPort(String portName, BaudRate portBaudRate, Parity portParity, String portDataControlSignal, Delimiter portDelimiter) throws IOException {

		boolean b = serialPort.open(portName, portBaudRate, portParity, portDelimiter, portDataControlSignal);
		if(b) {
			serialPort.addDataEvent();
			this.portName = portName;
			this.portBaudRate = portBaudRate;
			this.portParity = portParity;
			this.portDataControlSignal = portDataControlSignal;
			this.portDelimeter = portDelimiter;
			if(dataSupplier != null) {
				dataSupplier.updateControlDevices();
			}
		}
		return b;
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
		BaudRate portBaudRate = (BaudRate)in.readObject();
		Delimiter portDelimeter = (Delimiter)in.readObject();
		String portDataControlSignal = (String)in.readObject();
		Parity portParity = (Parity)in.readObject();
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
		this.portParity = portParity;
		this.dataReceive.reset(true, (int)(timeInterval * 1000), wavelenghtInterval, wavelenghtRangeFrom, wavelenghtRangeTo);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {

		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {

		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void resetChromatogram() {

		this.dataReceive.reset((int)(timeInterval * 1000), wavelenghtInterval, wavelenghtRangeFrom, wavelenghtRangeTo);
	}

	public void resetChromatogram(boolean savadata) {

		this.dataReceive.reset(savadata, (int)(timeInterval * 1000), wavelenghtInterval, wavelenghtRangeFrom, wavelenghtRangeTo);
	}

	public void saveData(boolean save) {

		this.dataReceive.setSaveData(save);
	}

	public boolean sendDataOutputType() throws IOException {

		return serialPort.sendDataOutputType(outputType);
	}

	public boolean sendStart() throws IOException {

		return serialPort.sendStart();
	}

	public boolean sendStop() throws IOException {

		return serialPort.sendStop();
	}

	public boolean sendTimeInterval() throws IOException {

		return serialPort.sendTimeInterval(timeInterval);
	}

	public boolean sendWavelenghtInterval() throws IOException {

		return serialPort.sendWavelenghtInterval(wavelenghtInterval);
	}

	public boolean sendWaveLenghtRange() throws IOException {

		// TODO:body of function
		return false;
	}

	public void setAutoSetValue(boolean autoSetValue) {

		propertyChangeSupport.firePropertyChange(PROPERTY_AUTOSET_VALUE, this.autoSetValue, autoSetValue);
		this.autoSetValue = autoSetValue;
	}

	@Override
	public void setDeviceSetting(IDeviceSetting deviceSetting) {

		if(deviceSetting.getDeviceID() == getDeviceID() && deviceSetting.getPluginID() == deviceSetting.getPluginID()) {
			HashMap<String, IValue<?>> values = deviceSetting.getValues();
			for(Entry<String, IValue<?>> entry : values.entrySet()) {
				Object value = entry.getValue().getValue();
				String key = entry.getKey();
				switch(key) {
					case PROPERTY_AUTOSET_VALUE:
						if(value instanceof Boolean) {
							setAutoSetValue((boolean)value);
						}
						break;
					case PROPERTY_DATA_OUTPUT_ANALOG:
						if(value instanceof String) {
							if(entry.getValue() instanceof ValueEnumeration) {
								ValueEnumeration enumeration = (ValueEnumeration)entry.getValue();
								if(enumeration.getValue() instanceof ValueInt) {
									ValueInt outputType = (ValueInt)enumeration.getValue();
									setOutputType(outputType.getValue());
								}
							}
						}
						break;
					case PROPERTY_NAME:
						if(value instanceof String) {
							setName((String)value);
						}
						break;
					case PROPERTY_OUTPUT_TYPE:
						if(value instanceof Integer) {
							setOutputType((int)value);
						}
						break;
					case PROPERTY_SEND_START:
						if(value instanceof Boolean) {
							setSendStart((boolean)value);
						}
						break;
					case PROPERTY_SEND_STOP:
						if(value instanceof Boolean) {
							setSendStop((boolean)value);
						}
						break;
					case PROPERTY_TIME_INTERVAL:
						if(value instanceof Float) {
							setTimeInterval((float)value);
						}
						break;
					case PROPERTY_WAVELENGHT_INTERVA:
						if(value instanceof Float) {
							setWavelenghtInterval((float)value);
						}
						break;
					case PROPERTY_WAVELENGHT_RANGE_FROM:
						if(value instanceof Integer) {
							setWavelenghtRangeFrom((int)value);
						}
						break;
					case PROPERTY_WAVELENGHT_RANGE_TO:
						if(value instanceof Integer) {
							setWavelenghtRangeTo((int)value);
						}
						break;
				}
			}
		}
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

	public boolean setParametrsSerialPort(BaudRate portBaudRate, Parity portParity, String portDataControlSignal, Delimiter portDelimiter) throws IOException {

		boolean b = serialPort.setParametrs(portBaudRate, portParity, portDelimiter, portDataControlSignal);
		if(b) {
			this.portBaudRate = portBaudRate;
			this.portParity = portParity;
			this.portDataControlSignal = portDataControlSignal;
			this.portDelimeter = portDelimiter;
		}
		return b;
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
		out.writeObject(portBaudRate);
		out.writeObject(portDelimeter);
		out.writeObject(portDataControlSignal);
		out.writeObject(portParity);
	}
}
