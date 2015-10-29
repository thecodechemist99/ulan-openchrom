/*******************************************************************************
 * Copyright (c) 2015 Jan Holy, Dr. Philip Wenig.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.parts;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.ULanConnection;
import org.chromulan.system.control.ui.analysis.support.UlanScanNetRunnable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.IULanCommunication.IFilt;
import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.ULanDrv;
import net.sourceforge.ulan.base.ULanMsg;

public class AvailableDevicesPart {

	static {
		if(ULanDrv.isLibraryLoaded()) {
			ULanCommunicationInterface.setHandle(new ULanDrv());
		}
	}
	@Inject
	private MApplication application;
	private Button buttonConnection;
	private Button buttonRefreshDevices;
	private ULanCommunicationInterface communication;
	private ULanConnection connection;
	private List<DeviceDescription> devices;
	@Inject
	private Display display;
	@Inject
	private IEventBroker eventBroker;
	private IFilt filtCloseConnection;
	private Label labelConnection;
	@Inject
	private EModelService modelService;
	@Inject
	private Composite parent;
	@Inject
	private EPartService partService;
	private Table table;

	public AvailableDevicesPart() {

		if(ULanDrv.isLibraryLoaded()) {
			communication = new ULanCommunicationInterface();
		}
		connection = new ULanConnection();
	}

	private void closeConnection() {

		table.clearAll();
		if(ULanDrv.isLibraryLoaded()) {
			try {
				if(ULanCommunicationInterface.isOpen()) {
					ULanCommunicationInterface.close();
				}
			} catch(IOException e) {
				// logger.warn(e);
			} finally {
				labelConnection.setText("Connection is closed");
				labelConnection.setBackground(display.getSystemColor(SWT.COLOR_RED));
				labelConnection.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				buttonRefreshDevices.setEnabled(false);
				buttonConnection.setText("Open Conection");
				eventBroker.post(IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_CLOSE, connection);
				parent.layout();
			}
		}
	}

	@PostConstruct
	public void createPartControl() {

		GridLayout gridLayout = new GridLayout(2, false);
		parent.setLayout(gridLayout);
		labelConnection = new Label(parent, SWT.LEFT | SWT.BORDER);
		labelConnection.setText("Connection is closed");
		labelConnection.setBackground(display.getSystemColor(SWT.COLOR_RED));
		labelConnection.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		GridData gridData = new GridData(GridData.END, GridData.FILL, true, false);
		gridData.horizontalSpan = 2;
		labelConnection.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		table = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		final TableColumn columnAdr = new TableColumn(table, SWT.NULL);
		columnAdr.setText("Adr");
		columnAdr.setWidth(40);
		final TableColumn columnName = new TableColumn(table, SWT.NULL);
		columnName.setText("Name of devices");
		columnName.setWidth(150);
		final TableColumn columnDescription = new TableColumn(table, SWT.None);
		columnDescription.setText("Description");
		columnDescription.setWidth(400);
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				int i = table.getSelectionIndex();
				if(i != -1) {
					openDeviceSettingsPart(i);
				}
			}
		});
		buttonRefreshDevices = new Button(parent, SWT.PUSH);
		buttonRefreshDevices.setText("Scan Net");
		buttonRefreshDevices.setEnabled(false);
		buttonRefreshDevices.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						loadDevice();
					}
				});
			}
		});
		gridData = new GridData(GridData.BEGINNING, GridData.FILL, false, false);
		buttonRefreshDevices.setLayoutData(gridData);
		buttonConnection = new Button(parent, SWT.PUSH);
		buttonConnection.setText("Open Conection");
		buttonConnection.setEnabled(true);
		buttonConnection.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(ULanCommunicationInterface.isOpen()) {
					closeConnection();
				} else {
					openConection();
				}
			}
		});
		gridData = new GridData(GridData.END, GridData.FILL, true, false);
		buttonConnection.setLayoutData(gridData);
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
		display.asyncExec(new Runnable() {

			@Override
			public void run() {

				openConection();
				loadDevice();
			}
		});
	}

	private void loadDevice() {

		if(ULanCommunicationInterface.isOpen()) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			UlanScanNetRunnable runnable = new UlanScanNetRunnable();
			try {
				dialog.run(true, true, runnable);
				this.devices = runnable.getDevices();
				rewriteTable();
			} catch(InvocationTargetException e) {
				// /logger.warn(e);
			} catch(InterruptedException e) {
				// logger.warn(e);
			}
		} else {
			MessageDialog.openWarning(parent.getShell(), "Net warning", "Net has not been founded, please control net conection.");
			closeConnection();
		}
	}

	private void openConection() {

		if(ULanDrv.isLibraryLoaded()) {
			try {
				if(!ULanCommunicationInterface.isOpen()) {
					ULanCommunicationInterface.open();
					if(!filtCloseConnection.isFiltActive()) {
						filtCloseConnection.activateFilt();
					}
					labelConnection.setText("Connection is opened");
					labelConnection.redraw();
					labelConnection.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
					labelConnection.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
					eventBroker.post(IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_OPEN, connection);
					buttonConnection.setText("Close conection");
					buttonRefreshDevices.setEnabled(true);
					parent.layout();
				}
			} catch(IOException e) {
				// TODO:Exception
			}
		}
	}

	private void openDeviceSettingsPart(int numberDevice) {

		if(ULanCommunicationInterface.isOpen() && this.devices != null && numberDevice < this.devices.size()) {
			DeviceDescription device = this.devices.get(numberDevice);
			MPart part = MBasicFactory.INSTANCE.createPart();
			part.setLabel(device.getModulType());
			part.setObject(device);
			part.setElementId("Device");
			part.setCloseable(true);
			part.setContributionURI("bundleclass://org.chromulan.system.control.ui/org.chromulan.system.control.ui.parts.DeviceSettingsPart");
			MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.ui.partstack.devicesSetting", application);
			stack.getChildren().add(part);
			partService.activate(part);
		}
	}

	private void rewriteTable() {

		table.removeAll();
		for(DeviceDescription deviceDescription : devices) {
			TableItem item = new TableItem(table, SWT.None);
			item.setText(0, Long.toString(deviceDescription.getAdr()));
			item.setText(1, deviceDescription.getModulType());
			item.setText(2, deviceDescription.getDescription());
		}
	}
}
