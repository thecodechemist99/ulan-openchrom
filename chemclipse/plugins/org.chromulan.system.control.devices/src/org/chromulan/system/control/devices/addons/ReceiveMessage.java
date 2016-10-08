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
package org.chromulan.system.control.devices.addons;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.devices.connection.ULanConnection;
import org.chromulan.system.control.devices.events.IULanConnectionEvents;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.IULanCommunication;
import net.sourceforge.ulan.base.IULanCommunication.IFilt;
import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.ULanHandle;
import net.sourceforge.ulan.base.ULanMsg;

public class ReceiveMessage {

	private IFilt<Void> filtStartRecording;

	public ReceiveMessage() {
		IULanCommunication com = new ULanCommunicationInterface();
		filtStartRecording = com.addFilt(ULanHandle.CMD_LCDMRK, null, new CompletionHandler<ULanMsg, Void>() {

			@Override
			public void completed(ULanMsg arg0, Void arg1) {

				// TODO : start recording
			}

			@Override
			public void failed(Exception arg0, Void arg1) {

			}
		});
	}

	@Inject
	@Optional
	public void openCommunicationEvent(
			@UIEventTopic(value = IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN) ULanConnection connection) {

		try {
			filtStartRecording.activateFilt();
		} catch (IOException e) {
			// TODO: exception
		}
	}

	@PostConstruct
	public void postConstruct() {

		if (ULanCommunicationInterface.isOpen()) {
			try {
				filtStartRecording.activateFilt();
			} catch (IOException e1) {
				// TODO: exception logger.warn(e1);
			}
		}
	}

	@PreDestroy
	public void preDestroy() {

		filtStartRecording.deactivateFilt();
	}
}
