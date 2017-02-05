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
package org.chromulan.system.control.devices.base;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.chromulan.system.control.device.ControlDevices;
import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.devices.events.IControlDeviceEvents;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.manager.acquisitions.IAcquisitionChangeListener;
import org.chromulan.system.control.manager.acquisitions.IAcquisitionManager;
import org.chromulan.system.control.manager.devices.DataSupplier;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.model.SaveChromatogram;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.ULanCommunicationInterface;

@Creatable
@Singleton
public class UlanDevicesManager {

	@Inject
	private MApplication application;
	@Inject
	private IEclipseContext context;
	private IAcquisitionChangeListener createChromatogram;
	@Inject
	private DataSupplier dataSupplier;
	private List<IUlanControlDevice> devices;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private IAcquisitionManager manager;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;

	public UlanDevicesManager() {
		devices = new ArrayList<>();
		createChromatogram = new IAcquisitionChangeListener() {

			@Override
			public List<SaveChromatogram> getChromatograms(IAcquisition acquisition) {

				List<SaveChromatogram> saveChromatograms = new ArrayList<>();
				for(IUlanControlDevice iUlanControlDevice : devices) {
					if(IUlanControlDevices.contains(acquisition.getDevicesProfile().getControlDevices(), iUlanControlDevice.getDeviceID())) {
						if(iUlanControlDevice instanceof DetectorULanControlDevice) {
							DetectorULanControlDevice detectorULanControlDevice = (DetectorULanControlDevice)iUlanControlDevice;
							saveChromatograms.add(new SaveChromatogram(detectorULanControlDevice.getChromatogramRecording().getChromatogram(), detectorULanControlDevice.getName(), detectorULanControlDevice.getName()));
						}
					}
				}
				return saveChromatograms;
			}
		};
	}

	public void addSaveDevices() {

		List<IControlDevice> device = dataSupplier.getControlDevices();
		for(IControlDevice iControlDevice : device) {
			if((iControlDevice instanceof IUlanControlDevice)) {
				if(!IUlanControlDevices.contains(devices, iControlDevice.getDeviceID())) {
					if(iControlDevice instanceof ULand3xControlDevice) {
						ULand3xControlDevice uLand3xControlDevice = (ULand3xControlDevice)iControlDevice;
						connectULad3x(uLand3xControlDevice);
						devices.add(uLand3xControlDevice);
					} else if(iControlDevice instanceof Lcd5000ControlDevice) {
						Lcd5000ControlDevice lcd5000ControlDevice = (Lcd5000ControlDevice)iControlDevice;
						connectLcd5000(lcd5000ControlDevice);
						devices.add(lcd5000ControlDevice);
					}
				}
			}
		}
		dataSupplier.updateControlDevices();
	}

	private void connectDevice(DeviceDescription description) {

		IUlanControlDevice controlDevice;
		if((controlDevice = IUlanControlDevices.getControlDevice(devices, UlanControlDevice.getDeviceID(description))) == null) {
			connectNewDevice(description);
		} else {
			connectDevice(controlDevice);
		}
	}

	private void connectDevice(IUlanControlDevice controlDevice) {

		eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, controlDevice);
	}

	private void connectLcd5000(Lcd5000ControlDevice lcd5000ControlDevice) {

		MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.devices.partstack.devicesSetting", application);
		if(stack == null) {
			return;
		}
		MPart part = partService.createPart("org.chromulan.system.control.devices.partdescriptor.lcd5000");
		part.setLabel(lcd5000ControlDevice.getName());
		DetectorControler detectorControler = new DetectorControler(lcd5000ControlDevice);
		ContextInjectionFactory.inject(detectorControler, context);
		part.setObject(detectorControler);
		stack.getChildren().add(part);
		partService.activate(part);
	}

	private void connectNewDevice(DeviceDescription description) {

		IUlanControlDevice controlDevice = null;
		if(description.getModulType().toLowerCase().equals("lcd5000") || description.getModulType().toLowerCase().equals("lcd5004")) {
			Lcd5000ControlDevice lcd5000ControlDevice = new Lcd5000ControlDevice(description, true);
			connectLcd5000(lcd5000ControlDevice);
			controlDevice = lcd5000ControlDevice;
		} else if(description.getModulType().toLowerCase().equals("ulad32") || description.getModulType().toLowerCase().equals("ulad31")) {
			ULand3xControlDevice uLand3xControlDevice = new ULand3xControlDevice(description, true);
			connectULad3x(uLand3xControlDevice);
			controlDevice = uLand3xControlDevice;
		}
		if(controlDevice != null) {
			dataSupplier.getControlDevices().add(controlDevice);
			devices.add(controlDevice);
			connectDevice(controlDevice);
		}
	}

	@Inject
	@Optional
	public void connectReqiredDevice(@UIEventTopic(value = IControlDevicesEvents.TOPIC_CONTROL_DEVICES_CONTROL) ControlDevices devices) {

		if(devices == null) {
			return;
		}
		for(IControlDevice deviceCon : devices.getControlDevices()) {
			if(deviceCon instanceof IUlanControlDevice) {
				IUlanControlDevice device = (IUlanControlDevice)deviceCon;
				IUlanControlDevice controlDevice = IUlanControlDevices.getControlDevice(this.dataSupplier.getControlDevices(), device.getDeviceID());
				if(controlDevice == null) {
					try {
						DeviceDescription description = ULanCommunicationInterface.getDevice(device.getDeviceDescription().getAdr());
						if(description != null) {
							IUlanControlDevices.add(this.dataSupplier.getControlDevices(), device);
							eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, IUlanControlDevices.getControlDevice(this.dataSupplier.getControlDevices(), device.getDeviceID()));
						}
					} catch(Exception e) {
					}
				} else {
					try {
						DeviceDescription description = ULanCommunicationInterface.getDevice(device.getDeviceDescription().getAdr());
						if(description != null) {
							eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT, controlDevice);
						} else {
							eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, controlDevice);
						}
					} catch(Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
			}
		}
		dataSupplier.updateControlDevices();
	}

	private void connectULad3x(ULand3xControlDevice uLand3xControlDevice) {

		MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.devices.partstack.devicesSetting", application);
		if(stack == null) {
			return;
		}
		MPart part = partService.createPart("org.chromulan.system.control.devices.partdescriptor.ulad3x");
		part.setLabel(uLand3xControlDevice.getName());
		DetectorControler detectorControler = new DetectorControler(uLand3xControlDevice);
		ContextInjectionFactory.inject(detectorControler, context);
		part.setObject(detectorControler);
		stack.getChildren().add(part);
		partService.activate(part);
	}

	private void disconnectDevice(IUlanControlDevice controlDevice) {

		eventBroker.post(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, controlDevice);
	}

	public List<IUlanControlDevice> getDevices() {

		return devices;
	}

	@PostConstruct
	private void postConstruct() {

		manager.addChangeListener(createChromatogram);
	}

	@PreDestroy
	private void preDestroy() {

		manager.removeChangeListener(createChromatogram);
	}

	public void updateConnection(List<DeviceDescription> descriptions) {

		for(DeviceDescription description : descriptions) {
			connectDevice(description);
		}
		for(IControlDevice device : this.dataSupplier.getControlDevices()) {
			if(device instanceof IUlanControlDevice) {
				IUlanControlDevice ulanDevice = (IUlanControlDevice)device;
				if(!IUlanControlDevices.contains(devices, ulanDevice.getDeviceID())) {
					disconnectDevice(ulanDevice);
				}
			}
		}
		dataSupplier.updateControlDevices();
	}
}
