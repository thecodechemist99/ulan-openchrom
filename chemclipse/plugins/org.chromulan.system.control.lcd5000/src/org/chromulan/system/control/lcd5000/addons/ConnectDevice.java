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
package org.chromulan.system.control.lcd5000.addons;

import javax.inject.Inject;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.devices.base.DeviceType;
import org.chromulan.system.control.devices.base.IUlanControlDevice;
import org.chromulan.system.control.devices.events.IControlDeviceEvents;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class ConnectDevice {

	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;
	private String[] supportedDevices = new String[]{"lcd5000", "lcd5004"};

	@Inject
	@Optional
	public void connectDevice(@UIEventTopic(value = IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT) IControlDevice controlDevice) {

		IUlanControlDevice device;
		if(controlDevice instanceof IUlanControlDevice) {
			device = (IUlanControlDevice)controlDevice;
		} else {
			return;
		}
		MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.devices.partstack.devicesSetting", application);
		if(stack == null) {
			return;
		}
		for(String nameDevice : supportedDevices) {
			if(device.getDeviceDescription().getModulType().toLowerCase().equals(nameDevice)) {
				device.setDeviceType(DeviceType.DETECTOR);
				if(modelService.find(device.getDeviceID(), application) == null) {
					MPart part = MBasicFactory.INSTANCE.createPart();
					part.setLabel(device.getDeviceID());
					part.setObject(device);
					part.setElementId(device.getDeviceID());
					part.setCloseable(false);
					part.setIconURI("platform:/plugin/org.chromulan.system.control.lcd5000/icons/16x16/devices.gif");
					part.setContributionURI("bundleclass://org.chromulan.system.control.lcd5000/org.chromulan.system.control.lcd5000.parts.Lcd5000Part");
					stack.getChildren().add(part);
					partService.showPart(part, PartState.CREATE);
				}
				break;
			}
		}
	}
}
