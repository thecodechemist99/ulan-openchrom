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
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Date;

public class Analysis implements IAnalysis {

	private String name;
	private boolean recording;
	private boolean recored;
	private boolean autoStop;
	private long interval;
	private Date date;
	private File directory;
	private boolean autoContinue;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public Analysis() {

		name = "";
		recording = false;
		recored = false;
		autoStop = false;
		autoContinue = false;
	}

	@Override
	public void setName(String name) {

		propertyChangeSupport.firePropertyChange(PROPERTY_NAME, this.name, this.name = name);
	}

	@Override
	public String getName() {

		return this.name;
	}

	@Override
	public void startRecording() {

		if(!recording && !recored) {
			this.date = new Date();
			this.recording = true;
		}
	}

	@Override
	public void stopRecording() {

		if(recording) {
			recored = true;
			recording = false;
		}
	}

	@Override
	public void setAutoStop(boolean b) {

		propertyChangeSupport.firePropertyChange(PROPERTY_AUTO_STOP, this.autoStop, this.autoStop = b);
	}

	@Override
	public boolean getAutoStop() {

		return this.autoStop;
	}

	@Override
	public boolean isRecording() {

		return recording;
	}

	@Override
	public boolean isRecored() {

		return recored;
	}

	@Override
	public Date getStartDate() {

		return date;
	}

	@Override
	public long getInterval() {

		return interval;
	}

	@Override
	public void setInterval(long interval) {

		propertyChangeSupport.firePropertyChange(PROPERTY_INTERVAL, this.interval, this.interval = interval);
	}

	@Override
	public void setDirectory(File directory) {

		if(directory.isDirectory()) {
			propertyChangeSupport.firePropertyChange(PROPERTY_DIRECTORY, this.directory, this.directory = directory);
		}
	}

	@Override
	public File getDirectory() {

		return this.directory;
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
	public void removePropertyChangeListener(PropertyChangeListener listener) {

		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {

		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public void setAutoContinue(boolean b) {

		propertyChangeSupport.firePropertyChange(PROPERTY_AUTO_CONTINUE, this.autoContinue, this.autoContinue = b);
	}

	@Override
	public boolean getAutoContinue() {

		return autoContinue;
	}
}
