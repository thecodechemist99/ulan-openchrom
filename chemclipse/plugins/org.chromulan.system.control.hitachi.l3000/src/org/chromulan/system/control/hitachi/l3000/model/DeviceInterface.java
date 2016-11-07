/*******************************************************************************
 * Copyright (c) 2016 Jan Holy.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.chromulan.system.control.device.IControlDevice;
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

import jssc.SerialPortException;

@Creatable
@Singleton
public class DeviceInterface {

	private ControlDevice controlDevice;
	@Inject
	private DataSupplier dataSupplier;
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
					IChromatogramWSD chromatogramWSD = controlDevice.getDatareceive().getChromatogram().geChromatogramWSD();
					if(acquisition instanceof IAcquisitionCSD) {
						HashMap<Integer, IChromatogramCSD> newChrom = IChromatogramWSDAcquisition.chromatogramWSDtoCSD(chromatogramWSD);
						for(Entry<Integer, IChromatogramCSD> chromSet : newChrom.entrySet()) {
							IChromatogramCSD chromatogramCSD = chromSet.getValue();
							chromatograms.add(new SaveChromatogram(chromatogramCSD, acquisition.getName() + " " + chromSet.getKey().toString()));
						}
					}
					if(acquisition instanceof IChromatogramWSD) {
						chromatograms.add(new SaveChromatogram(chromatogramWSD, acquisition.getName()));
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
						try {
							controlDevice.sendStart();
							controlDevice.getDatareceive().reset(true);
						} catch(SerialPortException e) {
							// logger.warn(e);
						}
					}
					controlDevice.setPrepare(false);
				}
			}

			@Override
			public void stopAcquisition(IAcquisition acquisition) {

				if(acquisition == setAcqiusition) {
					controlDevice.getDatareceive().setSaveData(false);
				}
			}
		};
	}

	@PreDestroy
	public void destroy() {

		manager.removeChangeListener(changeListener);
	}

	public IAcquisition getAcqiusition() {

		return setAcqiusition;
	}

	public ControlDevice getControlDevice() {

		return controlDevice;
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
}
