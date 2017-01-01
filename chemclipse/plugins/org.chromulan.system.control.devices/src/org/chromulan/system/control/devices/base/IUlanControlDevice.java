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

import org.chromulan.system.control.device.IControlDevice;

import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.IULanDevice;

public interface IUlanControlDevice extends IControlDevice {

	final String PROPERTY_DEVICE_TYPE = "deviceType";
	final String PROPERTY_NAME = "name";

	void addPropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	DeviceDescription getDeviceDescription();

	@Override
	String getDeviceID();

	DeviceType getDeviceType();

	@Override
	String getName();

	IULanDevice getUlanDevice();

	void removePropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	void setDeviceType(DeviceType deviceType);

	void setModel(String model);

	void setName(String name);

	void setPrepared(boolean b);
}
