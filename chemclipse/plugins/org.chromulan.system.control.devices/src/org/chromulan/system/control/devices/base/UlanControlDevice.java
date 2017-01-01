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
package org.chromulan.system.control.devices.base;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.channels.ClosedChannelException;

import org.chromulan.system.control.device.setting.DeviceSetting;
import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.devices.Activator;

import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.IULanDevice;
import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.ULanDevice;

public abstract class UlanControlDevice implements IUlanControlDevice {

	public static String getDeviceID(DeviceDescription description) {

		return description.getModulType() + "_" + Long.toString(description.getAdr());
	}

	private final String COMPANY = "PiKRON";
	private DeviceDescription description;
	private IULanDevice device;
	private IDeviceSetting deviceSetting;
	private DeviceType deviceType;
	private boolean isPrepared;
	private String model;
	private String name;
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public UlanControlDevice() {
	}

	public UlanControlDevice(DeviceType deviceType, DeviceDescription description, boolean isConnected) {
		this.device = new ULanDevice(description);
		this.description = description;
		this.name = description.getModulType();
		this.isPrepared = false;
		this.deviceType = deviceType;
		this.model = description.getModulType();
		this.deviceSetting = new DeviceSetting(Activator.PLUGIN_ID, description.getModulType(), description.getModulType(), getDeviceID(description));
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {

		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public abstract void connect() throws ClosedChannelException, IOException;

	public abstract void disConnect();

	@Override
	public String getCompany() {

		return COMPANY;
	}

	@Override
	public String getDescription() {

		return getDeviceDescription().getAdr() + " " + getDeviceDescription().getDescription();
	}

	@Override
	public DeviceDescription getDeviceDescription() {

		return description;
	}

	@Override
	public String getDeviceID() {

		return getDeviceID(description);
	}

	@Override
	public IDeviceSetting getDeviceSetting() {

		return deviceSetting;
	}

	@Override
	public DeviceType getDeviceType() {

		return deviceType;
	}

	@Override
	public long getFlg() {

		return FLG_SUPPORT_CSD_CHROMATOGRAM;
	}

	@Override
	public String getModel() {

		return model;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String getPluginID() {

		return Activator.PLUGIN_ID;
	}

	@Override
	public IULanDevice getUlanDevice() {

		return device;
	}

	@Override
	public boolean isConnected() {

		return ULanCommunicationInterface.isOpen();
	}

	@Override
	public boolean isPrepared() {

		return this.isPrepared;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		this.name = (String)in.readObject();
		this.deviceType = DeviceType.valueOf((String)in.readObject());
		long adr = in.readLong();
		String description = (String)in.readObject();
		this.deviceSetting = (IDeviceSetting)in.readObject();
		this.description = new DeviceDescription(adr, description);
		this.device = new ULanDevice(this.description);
		this.isPrepared = false;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {

		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public void setDeviceType(DeviceType deviceType) {

		propertyChangeSupport.firePropertyChange(PROPERTY_DEVICE_TYPE, this.deviceType, this.deviceType = deviceType);
	}

	@Override
	public void setModel(String model) {

		this.model = model;
	}

	@Override
	public void setName(String name) {

		propertyChangeSupport.firePropertyChange(PROPERTY_NAME, this.name, this.name = name);
	}

	@Override
	public void setPrepared(boolean b) {

		this.isPrepared = b;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeObject(name);
		out.writeObject(deviceType.name());
		out.writeLong(description.getAdr());
		out.writeObject(description.getDescription());
		out.writeObject(deviceSetting);
	}
}
