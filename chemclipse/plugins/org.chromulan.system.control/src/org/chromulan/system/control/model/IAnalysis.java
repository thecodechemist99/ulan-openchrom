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

	final String PROPERTY_NAME = "name";
	final String PROPERTY_INTERVAL = "interval";
	final String PROPERTY_AUTO_STOP = "autoStop";
	final String PROPERTY_AUTO_CONTINUE = "autoContinue";
	final String PROPERTY_DIRECTORY = "directory";

	void setName(String name);

	String getName();

	void startRecording();

	void stopRecording();

	void setAutoStop(boolean b);

	boolean getAutoStop();

	void setAutoContinue(boolean b);

	boolean getAutoContinue();

	boolean isRecording();

	boolean isRecored();

	Date getStartDate();

	long getInterval();

	void setInterval(long interval);

	void setDirectory(File directory);

	File getDirectory();

	void addPropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);
}
