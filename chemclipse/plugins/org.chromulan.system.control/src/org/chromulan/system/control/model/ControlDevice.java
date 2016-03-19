/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.sourceforge.ulan.base.DeviceDescription;

public class ControlDevice implements IControlDevice {

	private DeviceDescription description;
	private DeviceType deviceType;
	private boolean isConnected;
	private String name;
	protected PropertyChangeSupport propertyChangeSupport;

	public ControlDevice() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.deviceType = DeviceType.UNKNOWEN;
	}

	public ControlDevice(DeviceDescription description, boolean isConnected) {
		this();
		this.description = description;
		this.name = description.getModulType();
		this.isConnected = isConnected;
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
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		this.name = (String)in.readObject();
		long adr = in.readLong();
		String description = (String)in.readObject();
		this.description = new DeviceDescription(adr, description);
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
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeObject(name);
		out.writeLong(description.getAdr());
		out.writeObject(description.getDescription());
	}
}
