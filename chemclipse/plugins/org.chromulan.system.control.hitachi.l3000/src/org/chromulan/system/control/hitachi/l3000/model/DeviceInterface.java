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
package org.chromulan.system.control.hitachi.l3000.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.hitachi.l3000.model.evens.DeviceEvents;
import org.chromulan.system.control.manager.acquisitions.IAcquisitionChangeListener;
import org.chromulan.system.control.manager.acquisitions.IAcquisitionManager;
import org.chromulan.system.control.manager.devices.DataSupplier;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.model.IAcquisitionCSD;
import org.chromulan.system.control.model.IChromatogramWSDAcquisition;
import org.chromulan.system.control.model.SaveChromatogram;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

@Creatable
@Singleton
public class DeviceInterface {

	private ControlDevice controlDevice;
	@Inject
	private DataSupplier dataSupplier;
	@Inject
	private IEventBroker eventBroker;
	private IAcquisitionChangeListener changeListener;
	@Inject
	private IAcquisitionManager manager;
	private IAcquisition setAcqiusition;

	public DeviceInterface() {
		changeListener = new IAcquisitionChangeListener() {

			@Override
			public void endAcquisition(IAcquisition acquisition) {

				if(acquisition == setAcqiusition) {
					setAcqiusition = null;
					controlDevice.setPrepare(true);
				}
			}

			@Override
			public List<SaveChromatogram> getChromatograms(IAcquisition acquisition) {

				List<SaveChromatogram> chromatograms = new ArrayList<>();
				if(acquisition == setAcqiusition) {
					IChromatogramWSD chromatogramWSD = controlDevice.getDatareceive().getChromatogram().getChromatogramWSD();
					if(acquisition instanceof IAcquisitionCSD) {
						HashMap<Double, IChromatogramCSD> newChrom = IChromatogramWSDAcquisition.chromatogramWSDtoCSD(chromatogramWSD);
						for(Entry<Double, IChromatogramCSD> chromSet : newChrom.entrySet()) {
							IChromatogramCSD chromatogramCSD = chromSet.getValue();
							SaveChromatogram saveChromatogram =new SaveChromatogram(chromatogramCSD, acquisition.getName() + " " + chromSet.getKey().toString(),controlDevice.getName());
							saveChromatogram.addDevicePropertie(ControlDevice.SETTING_TIME_INTERVAL, Integer.toString(controlDevice.getTimeIntervalMill()) + "ms");
							saveChromatogram.addDevicePropertie("wave lenght",chromSet.getKey().toString()+"nm");
							chromatograms.add(saveChromatogram);
						}
					}
					if(acquisition instanceof IChromatogramWSD) {
						SaveChromatogram saveChromatogram = new SaveChromatogram(chromatogramWSD, acquisition.getName(),controlDevice.getName());
						saveChromatogram.addDevicePropertie(ControlDevice.SETTING_TIME_INTERVAL, Integer.toString(controlDevice.getTimeIntervalMill()) + "ms");
						saveChromatogram.addDevicePropertie(ControlDevice.SETTING_WAVELENGHT_INTERVAL, Float.toString(controlDevice.getWavelenghtInterval())+"nm");
						saveChromatogram.addDevicePropertie(ControlDevice.SETTING_WAVELENGHT_RANGE_FROM, Float.toString(controlDevice.getWavelenghtRangeFrom())+"nm");
						saveChromatogram.addDevicePropertie(ControlDevice.SETTING_WAVELENGHT_RANGE_TO, Float.toString(controlDevice.getWavelenghtRangeTo())+"nm");
						chromatograms.add(saveChromatogram);
					}
				
				}
				return chromatograms;
			}

			@Override
			public void setAcquisition(IAcquisition acquisition) {

				if(setAcqiusition != null) {
					return;
				}
				List<IControlDevice> controlDevices = acquisition.getDevicesProfile().getControlDevices();
				for(IControlDevice controlDevice : controlDevices) {
					if(controlDevice instanceof ControlDevice) {
						setAcqiusition = acquisition;
						return;
					}
				}
			}

			@Override
			public void startAcquisition(IAcquisition acquisition) {

				if(acquisition == setAcqiusition) {
					if(controlDevice.isSendStart()) {
						sendStart();
					}
					controlDevice.setPrepare(false);
					controlDevice.getDatareceive().reset(true);
				}
			}

			@Override
			public void stopAcquisition(IAcquisition acquisition) {

				if(acquisition == setAcqiusition) {
					if(controlDevice.isSendStop()) {
						sendStop();
					}
					controlDevice.getDatareceive().setSaveData(false);
				}
			}
		};
	}

	public boolean closeConnection() {

		try {
			controlDevice.closeSerialPort();
			dataSupplier.updateControlDevices();
			return true;
		} catch(Exception e) {
		}
		return false;
	}

	@PreDestroy
	public void destroy() {

		closeConnection();
		manager.removeChangeListener(changeListener);
	}

	public IAcquisition getAcqiusition() {

		return setAcqiusition;
	}

	public ControlDevice getControlDevice() {

		return controlDevice;
	}

	public boolean openConnection(String portName, int portBaudRate, boolean portEventParity, boolean portDataControlSignal, String portDelimiter, boolean addFilter) {

		try {
			boolean b = controlDevice.openSerialPort(portName, portBaudRate, portEventParity, portDataControlSignal, portDelimiter);
			if(b) {
				eventBroker.post(DeviceEvents.TOPIC_HITACHI_L3000_CONNECTION_OPEN, this);
				dataSupplier.updateControlDevices();
			}
			return b;
		} catch(Exception e) {
			controlDevice.closeSerialPort();
		}
		return false;
	}

	@PostConstruct
	public void postConstruct() {

		for(IControlDevice device : dataSupplier.getControlDevices()) {
			if(device instanceof ControlDevice) {
				this.controlDevice = (ControlDevice)device;
			}
		}
		if(controlDevice == null) {
			controlDevice = new ControlDevice();
			dataSupplier.getControlDevices().add(controlDevice);
		}
		manager.addChangeListener(changeListener);
	}

	public boolean sendDataOutputType() {

		try {
			return controlDevice.sendDataOutputType();
		} catch(Exception e) {
			closeConnection();
		}
		return false;
	}

	private boolean sendStart() {

		try {
			return controlDevice.sendStart();
		} catch(IOException e) {
			closeConnection();
		}
		return false;
	}

	private boolean sendStop() {

		try {
			return controlDevice.sendStop();
		} catch(IOException e) {
			closeConnection();
		}
		return false;
	}

	public boolean sendTimeInterval() {

		try {
			return controlDevice.sendTimeInterval();
		} catch(IOException e) {
			closeConnection();
		}
		return false;
	}

	public boolean sendWavelenghtInterval() {

		try {
			return controlDevice.sendWavelenghtInterval();
		} catch(IOException e) {
			closeConnection();
		}
		return false;
	}

	public boolean sendWaveLenghtRange() {

		try {
			return controlDevice.sendWaveLenghtRange();
		} catch(IOException e) {
			closeConnection();
		}
		return false;
	}

	public boolean setParameters(int portBaudRate, boolean portEventParity, boolean portDataControlSignal, String portDelimiter) {

		try {
			boolean b = controlDevice.setPortParameters(portBaudRate, portEventParity, portDataControlSignal, portDelimiter);
			if(b) {
				eventBroker.post(DeviceEvents.TOPIC_HITACHI_L3000_CONNECTION_SET_PARAMETERS, this);
			}
			return b;
		} catch(Exception e) {
			closeConnection();
		}
		return false;
	}
}
