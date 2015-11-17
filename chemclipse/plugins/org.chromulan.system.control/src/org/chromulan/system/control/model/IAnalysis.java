/*******************************************************************************
 * Copyright (c) 2015 Jan Holy.
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
import java.io.File;
import java.util.Date;

public interface IAnalysis {

	final String PROPERTY_AUTO_CONTINUE = "autoContinue";
	final String PROPERTY_AUTO_STOP = "autoStop";
	final String PROPERTY_DESCRIPTION = "description";
	final String PROPERTY_DIRECTORY = "directory";
	final String PROPERTY_INTERVAL = "interval";
	final String PROPERTY_NAME = "name";

	void addPropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	boolean getAutoContinue();

	boolean getAutoStop();

	String getDescription();

	IDevicesProfile getDevicesProfile();

	File getDirectory();

	long getInterval();

	String getName();

	Date getStartDate();

	boolean hasBeenRecorded();

	boolean isBeingRecorded();

	void removePropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	void setAutoContinue(boolean b);

	void setAutoStop(boolean b);

	void setDescription(String description);

	void setDevicesProfile(IDevicesProfile devicesProfile);

	void setDirectory(File directory);

	void setInterval(long interval);

	void setName(String name);

	void startRecording();

	void stopRecording();
}
