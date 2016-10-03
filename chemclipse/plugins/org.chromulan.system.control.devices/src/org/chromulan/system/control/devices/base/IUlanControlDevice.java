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

import org.chromulan.system.control.device.IControlDevice;

import net.sourceforge.ulan.base.DeviceDescription;

public interface IUlanControlDevice extends IControlDevice {

	final String PROPERTY_DEVICE_TYPE = "deviceType";
	final String PROPERTY_NAME = "name";

	void addPropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	DeviceDescription getDeviceDescription();

	DeviceType getDeviceType();

	@Override
	String getName();

	void removePropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	void setDeviceType(DeviceType deviceType);

	void setType(String type);
	
	void setName(String name);
	
	void setConnected(boolean b);
	
	void setPrepared(boolean b);
	
	
}
