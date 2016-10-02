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
package org.chromulan.system.control.devices.base;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.devices.Activator;

import net.sourceforge.ulan.base.DeviceDescription;

public class UlanControlDevice implements IUlanControlDevice {

	private DeviceDescription description;
	private DeviceType deviceType;
	private boolean isConnected;
	private boolean isPrepared;
	private String name;
	private IDeviceSetting deviceSetting;
	protected PropertyChangeSupport propertyChangeSupport;
	

	public UlanControlDevice() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.deviceType = DeviceType.UNKNOWEN;
		this.deviceSetting = new UlanDeviceSetting();
	}

	public UlanControlDevice(DeviceDescription description, boolean isConnected) {
		this();
		this.description = description;
		this.name = description.getModulType();
		this.isConnected = isConnected;
		this.isPrepared = true;
	}

	public UlanControlDevice(DeviceDescription description, boolean isConnected, boolean isPrepared) {
		this(description, isConnected);
		this.isPrepared = isPrepared;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {

		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public DeviceDescription getDeviceDescription() {

		return description;
	}

	@Override
	public DeviceType getDeviceType() {

		return deviceType;
	}

	@Override
	public int getFlg() {

		return FLG_SUPPORT_CSD_CHROMATOGRAM;
	}

	@Override
	public String getID() {

		return description.getModulType() + "_" + Long.toString(description.getAdr());
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public boolean isConnected() {

		return isConnected;
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
		this.description = new DeviceDescription(adr, description);
		this.deviceSetting = (IDeviceSetting) in.readObject();
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
	public void setConnected(boolean b) {

		isConnected = b;
	}

	@Override
	public void setDeviceType(DeviceType deviceType) {

		propertyChangeSupport.firePropertyChange(PROPERTY_DEVICE_TYPE, this.deviceType, this.deviceType = deviceType);
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
	public String getDescription() {

		return getDeviceType() + " " + getDeviceDescription().getAdr() + " " + getDeviceDescription().getDescription();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeObject(name);
		out.writeObject(deviceType.name());
		out.writeLong(description.getAdr());
		out.writeObject(description.getDescription());
		out.writeObject(deviceSetting);
	}
	
	@Override
	public String getPluginID() {
		
		return Activator.PLUGIN_ID;
	}

	@Override
	public void setDeviceSetting(IDeviceSetting deviceSetting) {
		this.deviceSetting = deviceSetting;
		
	}

	@Override
	public IDeviceSetting getDeviceSetting() {	
		return deviceSetting;
	}
}
