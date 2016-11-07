/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.model;

import java.beans.PropertyChangeListener;
import java.util.Date;

import org.chromulan.system.control.device.IDevicesProfile;

public interface IAcquisition {

	final String PROPERTY_AUTO_STOP = "autoStop";
	final String PROPERTY_DESCRIPTION = "description";
	final String PROPERTY_DURATION = "duration";
	final String PROPERTY_NAME = "name";

	void addPropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	IAcquisitionSaver getAcquisitionSaver();

	boolean getAutoStop();

	String getDescription();

	IDevicesProfile getDevicesProfile();

	long getDuration();

	String getName();

	Date getStartDate();

	boolean isCompleted();

	boolean isRunning();

	void removePropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	void setAcquisitionSaver(IAcquisitionSaver saver);

	void setAutoStop(boolean b);

	void setDescription(String description);

	void setDevicesProfile(IDevicesProfile devicesProfile);

	void setDuration(long duration);

	void setName(String name);

	void start();

	void stop(boolean changeTime);
}
