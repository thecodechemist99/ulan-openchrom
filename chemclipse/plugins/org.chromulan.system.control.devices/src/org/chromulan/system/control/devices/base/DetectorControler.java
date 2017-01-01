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

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.devices.connection.ULanConnection;
import org.chromulan.system.control.devices.events.IULanConnectionEvents;
import org.chromulan.system.control.manager.acquisitions.IAcquisitionChangeListener;
import org.chromulan.system.control.manager.acquisitions.IAcquisitionManager;
import org.chromulan.system.control.model.IAcquisition;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.IULanCommunication.IFilt;
import net.sourceforge.ulan.base.IULanDevice;
import net.sourceforge.ulan.base.ULanHandle;
import net.sourceforge.ulan.base.ULanMsg;

public class DetectorControler {

	@Inject
	private ULanConnection connection;
	private DetectorULanControlDevice controlDevice;
	private IFilt<Void> filtStartRecording;
	@Inject
	private IAcquisitionManager manager;
	private IAcquisition setAcquisition;
	private IULanDevice ulanDevice;

	public DetectorControler(DetectorULanControlDevice controlDevice) {
		this.controlDevice = controlDevice;
		ulanDevice = controlDevice.getUlanDevice();
	}

	@Inject
	@Optional
	public void closeConnection(@UIEventTopic(value = IULanConnectionEvents.TOPIC_CONNECTION_ULAN_CLOSE) ULanConnection connection) {

		controlDevice.disConnect();
		filtStartRecording.deactivateFilt();
	}

	public DetectorULanControlDevice getControlDevice() {

		return controlDevice;
	}

	@Inject
	@Optional
	public void openConnection(@UIEventTopic(value = IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN) ULanConnection connection) {

		try {
			if(connection != null && connection.isOpen()) {
				if(!(setAcquisition != null && setAcquisition.isRunning())) {
					resetChromatogramData();
				}
				controlDevice.connect();
				filtStartRecording.activateFilt();
			}
		} catch(IOException e) {
			// TODO: logger.warn(e);
		}
	}

	@PostConstruct
	public void postConstruct() {

		filtStartRecording = ulanDevice.addFiltAdr(ULanHandle.CMD_LCDMRK, null, new CompletionHandler<ULanMsg, Void>() {

			@Override
			public void completed(ULanMsg arg0, Void arg1) {

				if(setAcquisition != null) {
					manager.start(setAcquisition);
				}
			}

			@Override
			public void failed(Exception arg0, Void arg1) {

			}
		});
		manager.addChangeListener(new IAcquisitionChangeListener() {

			@Override
			public void endAcquisition(IAcquisition acquisition) {

				if(setAcquisition == acquisition) {
					setAcquisition = null;
					controlDevice.setPrepared(true);
					controlDevice.start(true);
				}
			}

			@Override
			public void setAcquisition(IAcquisition acquisition) {

				List<IControlDevice> controlDevices = acquisition.getDevicesProfile().getControlDevices();
				if(setAcquisition == null && IUlanControlDevices.contains(controlDevices, controlDevice.getDeviceID())) {
					setAcquisition = acquisition;
					controlDevice.setPrepared(false);
				}
			}

			@Override
			public void startAcquisition(IAcquisition acquisition) {

				if(setAcquisition == acquisition) {
					controlDevice.start(true);
				}
			}

			@Override
			public void stopAcquisition(IAcquisition acquisition) {

				if(setAcquisition == acquisition) {
					controlDevice.stop();
				}
			}
		});
		controlDevice.setPrepared(true);
		if(connection.isOpen()) {
			openConnection(connection);
		}
	}

	public void resetChromatogramData() {

		controlDevice.getChromatogramRecording().newAcquisition(controlDevice.getScanDelay(), controlDevice.getScanInterval());
	}
}
