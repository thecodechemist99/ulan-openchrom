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

	String PROPERTY_AMOUNT = "amount";
	String PROPERTY_ANALYSIS = "analysis";
	String PROPERTY_AUTO_STOP = "autoStop";
	String PROPERTY_COLUMN = "column";
	String PROPERTY_DESCRIPTION = "description";
	String PROPERTY_DETECTION = "detection";
	String PROPERTY_DURATION = "duration";
	String PROPERTY_FLOW_RATE = "flowRate";
	String PROPERTY_FLOW_RATE_UNIT = "flowRateUnit";
	String PROPERTY_MOBIL_PHASE = "mobilPhase";
	String PROPERTY_NAME = "name";
	String PROPERTY_TEMPERATURE_UNIT = "temperatureUnit";
	String PROPERTY_TEMPERATYRE = "temperature";

	void addPropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	IAcquisitionSaver getAcquisitionSaver();

	Float getAmount();

	String getAnalysis();

	boolean getAutoStop();

	String getColunm();

	String getDescription();

	String getDetection();

	IDevicesProfile getDevicesProfile();

	long getDuration();

	float getFlowRate();

	String getFlowRateUnit();

	String getMobilPhase();

	String getName();

	Date getStartDate();

	float getTemperature();

	String getTemperatureUnit();

	boolean isCompleted();

	boolean isRunning();

	void removePropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	void setAcquisitionSaver(IAcquisitionSaver saver);

	void setAmount(Float amount);

	void setAnalysis(String analysis);

	void setAutoStop(boolean b);

	void setColumn(String column);

	void setDescription(String description);

	void setDetection(String detection);

	void setDevicesProfile(IDevicesProfile devicesProfile);

	void setDuration(long duration);

	void setFlowRate(float flowRate);

	void setFlowRateUnit(String flowRateUnit);

	void setMobilPhase(String mobilPhase);

	void setName(String name);

	void setTemperature(float temperature);

	void setTemperatureUnit(String unit);

	void start();

	void stop(boolean changeTime);
}
