/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
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

import net.sourceforge.ulan.base.DeviceDescription;

public interface IControlDevice {

	final String PROPERTY_DEVICE_TYPE = "deviceType";
	final String PROPERTY_NAME = "name";

	void addPropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	DeviceDescription getDeviceDescription();

	DeviceType getDeviceType();

	String getID();

	String getName();

	boolean isConnected();

	void removePropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	void setConnected(boolean b);

	void setDeviceType(DeviceType deviceType);

	void setName(String name);
}