/*******************************************************************************
 * Copyright (c) 2015, 2017 Jan Holy.
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

import org.chromulan.system.control.device.IDevicesProfile;

public abstract class AbstractAcquisition implements IAcquisition {

	private IAcquisitionSaver acquisitionSaver;
	private Float amount;
	private String analysis;
	private boolean autoStop;
	private String column;
	private Date date;
	private String description;
	private String detection;
	private IDevicesProfile devicesProfile;
	private long duration;
	private Float flowRate;
	private String flowRateUnit;
	private Float injectionVolume;
	private Float ISTDAmount;
	private String mobilPhase;
	private String name;
	final protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private boolean record;
	private boolean recording;
	private Float temperature;
	private String temperatureUnit;

	public AbstractAcquisition() {
		name = "";
		recording = false;
		record = false;
		autoStop = false;
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
	public IAcquisitionSaver getAcquisitionSaver() {

		return acquisitionSaver;
	}

	@Override
	public Float getAmount() {

		return amount;
	}

	@Override
	public String getAnalysis() {

		return analysis;
	}

	@Override
	public boolean getAutoStop() {

		return this.autoStop;
	}

	@Override
	public String getColumn() {

		return column;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public String getDetection() {

		return detection;
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
	public Float getFlowRate() {

		return flowRate;
	}

	@Override
	public String getFlowRateUnit() {

		return flowRateUnit;
	}

	@Override
	public Float getInjectionVolume() {

		return injectionVolume;
	}

	@Override
	public Float getISTDAmount() {

		return ISTDAmount;
	}

	@Override
	public String getMobilPhase() {

		return mobilPhase;
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
	public Float getTemperature() {

		return temperature;
	}

	@Override
	public String getTemperatureUnit() {

		return temperatureUnit;
	}

	@Override
	public boolean isCompleted() {

		return record;
	}

	@Override
	public boolean isRunning() {

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
	public void setAcquisitionSaver(IAcquisitionSaver saver) {

		this.acquisitionSaver = saver;
	}

	@Override
	public void setAmount(Float amount) {

		propertyChangeSupport.firePropertyChange(PROPERTY_AMOUNT, this.amount, this.amount = amount);
	}

	@Override
	public void setAnalysis(String analysis) {

		propertyChangeSupport.firePropertyChange(PROPERTY_ANALYSIS, this.analysis, this.analysis = analysis);
	}

	@Override
	public void setAutoStop(boolean b) {

		synchronized(this) {
			if(!isRunning()) {
				propertyChangeSupport.firePropertyChange(PROPERTY_AUTO_STOP, this.autoStop, this.autoStop = b);
			}
		}
	}

	@Override
	public void setColumn(String column) {

		propertyChangeSupport.firePropertyChange(PROPERTY_COLUMN, this.column, this.column = column);
	}

	@Override
	public void setDescription(String description) {

		propertyChangeSupport.firePropertyChange(PROPERTY_DESCRIPTION, this.description, this.description = description);
	}

	@Override
	public void setDetection(String detection) {

		propertyChangeSupport.firePropertyChange(PROPERTY_DETECTION, this.detection, this.detection = detection);
	}

	@Override
	public void setDevicesProfile(IDevicesProfile devicesProfile) {

		if(this.devicesProfile != null) {
			this.devicesProfile.removeAcqusition(this);
		}
		this.devicesProfile = devicesProfile;
		this.devicesProfile.addAcquisition(this);
	}

	@Override
	public void setDuration(long duration) {

		synchronized(this) {
			if(!isRunning()) {
				propertyChangeSupport.firePropertyChange(PROPERTY_DURATION, this.duration, this.duration = duration);
			}
		}
	}

	@Override
	public void setFlowRate(Float flowRate) {

		propertyChangeSupport.firePropertyChange(PROPERTY_FLOW_RATE, this.flowRate, this.flowRate = flowRate);
	}

	@Override
	public void setFlowRateUnit(String flowRateUnit) {

		propertyChangeSupport.firePropertyChange(PROPERTY_FLOW_RATE_UNIT, this.flowRateUnit, this.flowRateUnit = flowRateUnit);
	}

	@Override
	public void setInjectionVolume(Float injectionVolume) {

		propertyChangeSupport.firePropertyChange(PROPERTY_INJECTION_VOLUME, this.injectionVolume, this.injectionVolume = injectionVolume);
	}

	@Override
	public void setISTDAmount(Float ISTDAmount) {

		propertyChangeSupport.firePropertyChange(PROPERTY_ISTD_AMOUNT, this.ISTDAmount, this.ISTDAmount = ISTDAmount);
	}

	@Override
	public void setMobilPhase(String mobilPhase) {

		propertyChangeSupport.firePropertyChange(PROPERTY_MOBIL_PHASE, this.mobilPhase, this.mobilPhase = mobilPhase);
	}

	@Override
	public void setName(String name) {

		propertyChangeSupport.firePropertyChange(PROPERTY_NAME, this.name, this.name = name);
	}

	@Override
	public void setTemperature(Float temperature) {

		propertyChangeSupport.firePropertyChange(PROPERTY_TEMPERATURE, this.temperature, this.temperature = temperature);
	}

	@Override
	public void setTemperatureUnit(String temperatureUnit) {

		propertyChangeSupport.firePropertyChange(PROPERTY_TEMPERATURE_UNIT, this.temperatureUnit, this.temperatureUnit = temperatureUnit);
	}

	@Override
	public void start() {

		synchronized(this) {
			if(!recording && !record) {
				this.date = new Date();
				this.recording = true;
			}
		}
	}

	@Override
	public void stop(boolean changeDuration) {

		synchronized(this) {
			if(recording) {
				record = true;
				recording = false;
				Date date = new Date();
				if(changeDuration) {
					setDuration(date.getTime() - date.getTime());
				}
			}
		}
	}
}
