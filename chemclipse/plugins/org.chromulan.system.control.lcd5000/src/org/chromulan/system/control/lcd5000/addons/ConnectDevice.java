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

import org.chromulan.system.control.events.IControlDeviceEvents;
import org.chromulan.system.control.model.DeviceType;
import org.chromulan.system.control.model.IControlDevice;
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
	public void connectDevice(@UIEventTopic(value = IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_CONNECT) IControlDevice device) {

		if(device == null) {
			return;
		}
		for(String nameDevice : supportedDevices) {
			if(device.getDeviceDescription().getModulType().toLowerCase().equals(nameDevice)) {
				device.setDeviceType(DeviceType.DETECTOR);
				if(modelService.find(device.getID(), application) == null) {
					MPart part = MBasicFactory.INSTANCE.createPart();
					part.setLabel(device.getID());
					part.setObject(device);
					part.setElementId(device.getID());
					part.setCloseable(false);
					part.setContributionURI("bundleclass://org.chromulan.system.control.lcd5000/org.chromulan.system.control.lcd5000.parts.Lcd5000Part");
					MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.ui.partstack.devicesSetting", application);
					stack.getChildren().add(part);
					partService.showPart(part, PartState.CREATE);
				}
				break;
			}
		}
	}
}
