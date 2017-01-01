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
package org.chromulan.system.control.devices.addons;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.devices.base.UlanDevicesManager;
import org.chromulan.system.control.devices.handlers.ScanNet;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.osgi.service.event.Event;

@SuppressWarnings("restriction")
public class AutoScanNet {

	@Inject
	private ECommandService commandService;
	@Inject
	private EHandlerService handlerService;
	@Inject
	private UlanDevicesManager manager;
	private boolean startScan;

	@PostConstruct
	public void postConstruct() {

		startScan = true;
	}

	private void startScan() {

		if(startScan) {
			ParameterizedCommand com = commandService.createCommand(ScanNet.HANDLER_ID, new HashMap<String, Object>());
			if(handlerService.canExecute(com)) {
				handlerService.executeHandler(com);
			}
			startScan = false;
		}
	}

	@Inject
	@Optional
	public void subscribeTopicSelectedElement(@EventTopic(UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT) Event event) {

		if(event == null) {
			return;
		}
		Object newValue = event.getProperty(EventTags.NEW_VALUE);
		if((newValue instanceof MPerspective)) {
			MPerspective perspective = (MPerspective)newValue;
			if(perspective.getElementId().startsWith("org.chromulan.system.control.ui.perspective.chromulan")) {
				manager.addSaveDevices();
				startScan();
			}
		}
	}
}
