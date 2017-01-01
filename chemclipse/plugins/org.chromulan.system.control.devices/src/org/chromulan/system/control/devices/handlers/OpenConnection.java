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

import javax.inject.Inject;

import org.chromulan.system.control.devices.connection.ULanConnection;
import org.chromulan.system.control.devices.events.IULanConnectionEvents;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import net.sourceforge.ulan.base.exceptions.HandleHasNotBeenInitializedException;

public class OpenConnection {

	public static final String HANDLER_ID = "org.chromulan.system.control.devices.command.openconnection";
	@Inject
	private ULanConnection connection;
	@Inject
	private IEventBroker eventBroker;

	@Execute
	public void execute() {

		try {
			openConection();
		} catch(HandleHasNotBeenInitializedException | IOException e) {
		}
	}

	private void openConection() throws HandleHasNotBeenInitializedException, IOException {

		connection.open();
		eventBroker.send(IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN, connection);
	}
}
