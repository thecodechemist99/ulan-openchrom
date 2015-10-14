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
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.ProgressMonitor;
import javax.swing.UnsupportedLookAndFeelException;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.model.ULanConnection;
import org.chromulan.system.control.ui.analysis.support.UlanScanNetRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.ICompatibleDevices;
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
	private Composite parent;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;
	@Inject
	private MApplication application;
	@Inject
	private Display display;
	@Inject
	private IEventBroker eventBroker;
	private Table table;
	private Button buttonRefreshDevices;
	private Button buttonAddDevice;
	private List<DeviceDescription> devices;
	private Label labelConnection;
	private ULanCommunicationInterface communication;
	private IFilt filtCloseConnection;
	private ULanConnection connection;

	public AvailableDevicesPart() {

		if(ULanDrv.isLibraryLoaded()) {
			communication = new ULanCommunicationInterface();
		}
		connection = new ULanConnection();
	}

	@PostConstruct
	public void createPartControl() {

		GridLayout gridLayout = new GridLayout(2, false);
		parent.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 2;
		labelConnection = new Label(parent, SWT.LEFT);
		labelConnection.setText("Connection is closed");
		labelConnection.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		table = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		TableColumn columnName = new TableColumn(table, SWT.NULL);
		columnName.setText("Name of devices");
		columnName.setWidth(150);
		TableColumn columnDescription = new TableColumn(table, SWT.None);
		columnDescription.setText("Description");
		columnDescription.setWidth(400);
		buttonRefreshDevices = new Button(parent, SWT.PUSH);
		buttonRefreshDevices.setText("Scan Net");
		buttonRefreshDevices.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						openConection();
						loadDevice();
					}
				});
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonRefreshDevices.setLayoutData(gridData);
		buttonAddDevice = new Button(parent, SWT.PUSH);
		buttonAddDevice.setText("Add device");
		buttonAddDevice.setEnabled(true);
		buttonAddDevice.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				openNewDeviceSettingsPart(table.getSelectionIndex());
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonAddDevice.setLayoutData(gridData);
		MPerspective perspectiveChromulan = (MPerspective)modelService.find("org.chromulan.system.control.ui.perspective.chromulan", application);
		if(perspectiveChromulan.getContext().containsKey(IAnalysis.class)) {
			buttonAddDevice.setEnabled(false);
		} else {
			buttonAddDevice.setEnabled(true);
		}
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

	private void openConection() {

		if(ULanDrv.isLibraryLoaded()) {
			try {
				if(!ULanCommunicationInterface.isOpen()) {
					ULanCommunicationInterface.open();
					if(!filtCloseConnection.isFiltActive()) {
						filtCloseConnection.activateFilt();
					}
					labelConnection.setText("Connection is opened");
					eventBroker.post(IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_OPEN, connection);
				}
			} catch(IOException e) {
				// TODO:Exception
			}
		}
	}

	private void closeConnection() {

		table.clearAll();
		if(ULanDrv.isLibraryLoaded()) {
			try {
				if(!ULanCommunicationInterface.isOpen()) {
					ULanCommunicationInterface.close();
				}
			} catch(IOException e) {
				// logger.warn(e);
			} finally {
				labelConnection.setText("Connection is closed");
				eventBroker.post(IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_CLOSE, connection);
			}
		}
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

	private void rewriteTable() {

		table.removeAll();
		for(DeviceDescription deviceDescription : devices) {
			TableItem item = new TableItem(table, SWT.None);
			item.setText(0, deviceDescription.getModulType());
			item.setText(1, deviceDescription.getDescription());
		}
	}

	@Inject
	@Optional
	public void disableButtons(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START_RECORDING) IAnalysis analysis) {

		buttonAddDevice.setEnabled(false);
	}

	@Inject
	@Optional
	public void enableButtons(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START) IAnalysis analysis) {

		buttonAddDevice.setEnabled(true);
	}

	private void openNewDeviceSettingsPart(int numberDevice) {

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
}
