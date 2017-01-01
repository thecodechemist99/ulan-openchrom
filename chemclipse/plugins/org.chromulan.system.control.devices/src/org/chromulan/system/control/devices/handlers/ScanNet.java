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
package org.chromulan.system.control.devices.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.inject.Inject;

import org.chromulan.system.control.devices.base.UlanDevicesManager;
import org.chromulan.system.control.devices.connection.ULanConnection;
import org.chromulan.system.control.devices.events.IULanConnectionEvents;
import org.chromulan.system.control.devices.supports.UlanScanNetRunnable;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;

import net.sourceforge.ulan.base.DeviceDescription;

public class ScanNet {

	public static final String HANDLER_ID = "org.chromulan.system.control.devices.command.scannet";
	@Inject
	private ULanConnection connection;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private UlanDevicesManager manager;

	@Execute
	private void execute() {

		try {
			connection.open();
			eventBroker.post(IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN, connection);
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			UlanScanNetRunnable runnable = new UlanScanNetRunnable();
			try {
				dialog.run(true, false, runnable);
				List<DeviceDescription> descriptions = runnable.getDevices();
				manager.updateConnection(descriptions);
			} catch(InterruptedException e) {
				// logger.warn(e);
			}
		} catch(IOException | InvocationTargetException e) {
			try {
				connection.close();
			} catch(Exception e2) {
			}
		}
	}
}
