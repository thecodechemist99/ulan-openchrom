package org.chromulan.system.control.devices.handlers;

import java.io.IOException;

import javax.inject.Inject;

import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.ULanConnection;
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
			// TODO Auto-generated catch block
		}
	}

	private void openConection() throws HandleHasNotBeenInitializedException, IOException {

		connection.open();
		eventBroker.send(IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN, connection);
	}
}
