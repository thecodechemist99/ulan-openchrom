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
import java.util.Date;

import org.chromulan.system.control.model.data.AbstractAnalysisData;

public abstract class AbstractAnalysis extends AbstractAnalysisData implements IAnalysis {

	private IAnalysisSaver analysisSaver;
	private boolean autoContinue;
	private boolean autoStop;
	private Date date;
	private String description;
	private IDevicesProfile devicesProfile;
	private long duration;
	private String name;
	protected PropertyChangeSupport propertyChangeSupport;
	private boolean recording;
	private boolean recored;

	public AbstractAnalysis() {
		name = "";
		recording = false;
		recored = false;
		autoStop = false;
		autoContinue = false;
		propertyChangeSupport = new PropertyChangeSupport(this);
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
	public IAnalysisSaver getAnalysisSaver() {

		return analysisSaver;
	}

	@Override
	public boolean getAutoContinue() {

		return autoContinue;
	}

	@Override
	public boolean getAutoStop() {

		return this.autoStop;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public IDevicesProfile getDevicesProfile() {

		return devicesProfile;
	}

	@Override
	public long getDuration() {

		return duration;
	}

	@Override
	public String getName() {

		return this.name;
	}

	@Override
	public Date getStartDate() {

		return date;
	}

	@Override
	public boolean hasBeenRecorded() {

		return recored;
	}

	@Override
	public boolean isBeingRecorded() {

		return recording;
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
	public void setAnalysisSaver(IAnalysisSaver saver) {

		this.analysisSaver = saver;
	}

	@Override
	public void setAutoContinue(boolean b) {

		propertyChangeSupport.firePropertyChange(PROPERTY_AUTO_CONTINUE, this.autoContinue, this.autoContinue = b);
	}

	@Override
	public void setAutoStop(boolean b) {

		propertyChangeSupport.firePropertyChange(PROPERTY_AUTO_STOP, this.autoStop, this.autoStop = b);
	}

	@Override
	public void setDescription(String description) {

		propertyChangeSupport.firePropertyChange(PROPERTY_DESCRIPTION, this.description, this.description = description);
	}

	@Override
	public void setDevicesProfile(IDevicesProfile devicesProfile) {

		this.devicesProfile = devicesProfile;
	}

	@Override
	public void setDuration(long duration) {

		propertyChangeSupport.firePropertyChange(PROPERTY_DURATION, this.duration, this.duration = duration);
	}

	@Override
	public void setName(String name) {

		propertyChangeSupport.firePropertyChange(PROPERTY_NAME, this.name, this.name = name);
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
}
