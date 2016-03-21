package org.chromulan.system.control.devices.handlers;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.data.DataSupplier;
import org.chromulan.system.control.events.IControlDeviceEvents;
import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.model.ULanConnection;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.widgets.Display;

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.IULanCommunication.IFilt;
import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.ULanMsg;
import net.sourceforge.ulan.base.exceptions.HandleHasNotBeenInitializedException;

public class CloseConnection {

	public static final String HANDLER_ID = "org.chromulan.system.control.devices.command.closeconnection";
	private ULanCommunicationInterface communication;
	@Inject
	private ULanConnection connection;
	@Inject
	private DataSupplier dataSupplier;
	private IControlDevices devices;
	@Inject
	private Display display;
	@Inject
	private IEventBroker eventBroker;
	private IFilt<Void> filtCloseConnection;

	public CloseConnection() {
		communication = new ULanCommunicationInterface();
	}

	@Inject
	@Optional
	public void addFilt(@UIEventTopic(value = IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN) ULanConnection connection) {

		try {
			filtCloseConnection.activateFilt();
		} catch(IOException e) {
		}
	}

	private void closeConnection() {

		for(IControlDevice device : devices.getControlDevices()) {
			device.setConnected(false);
			eventBroker.send(IControlDeviceEvents.TOPIC_CONTROL_DEVICE_ULAN_DISCONNECT, device);
		}
		dataSupplier.updateControlDevices();
		try {
			connection.close();
		} catch(HandleHasNotBeenInitializedException | IOException e) {
			// TODO Auto-generated catch block
		} finally {
			eventBroker.post(IULanConnectionEvents.TOPIC_CONNECTION_ULAN_CLOSE, connection);
		}
	}

	@Execute
	public void execute() {

		closeConnection();
	}

	@PostConstruct
	public void postConstruct() {

		devices = dataSupplier.getControlDevices();
		filtCloseConnection = communication.addFilt(0, null, new CompletionHandler<ULanMsg, Void>() {

			@Override
			public void completed(ULanMsg arg0, Void arg1) {

			}

			@Override
			public void failed(Exception arg0, Void arg1) {

				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						closeConnection();
					}
				});
			}
		});
	}
}
