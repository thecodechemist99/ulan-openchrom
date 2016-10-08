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
package org.chromulan.system.control.lcd5000.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.devices.base.IUlanControlDevice;
import org.chromulan.system.control.devices.base.IUlanControlDevices;
import org.chromulan.system.control.devices.base.data.IDetectorData;
import org.chromulan.system.control.devices.connection.ULanConnection;
import org.chromulan.system.control.devices.events.IULanConnectionEvents;
import org.chromulan.system.control.events.IAcquisitionEvents;
import org.chromulan.system.control.lcd5000.model.Lcd5000;
import org.chromulan.system.control.lcd5000.model.Lcd5000Data;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.ui.events.IAcquisitionUIEvents;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Lcd5000Part {

	private IAcquisition acquisition;
	private Button buttonReadData;
	private Button buttonReset;
	private IUlanControlDevice controlDevice;
	@Inject
	private IEventBroker eventBroker;
	private Lcd5000 lcd5000;
	private PropertyChangeListener listener;
	@Inject
	private MPart part;
	private Table table;

	public Lcd5000Part() {
		listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				redrawTable();
			}
		};
	}

	@PostConstruct
	public void createPartControl(Composite parent) {

		parent.setLayout(new GridLayout(3, false));
		controlDevice = (IUlanControlDevice) part.getObject();
		controlDevice.addPropertyChangeListener(listener);
		lcd5000 = new Lcd5000(controlDevice);
		try {
			lcd5000.connect();
			lcd5000.start(false);
		} catch (IOException e1) {
			// logger.warn(e1);
		}
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 0);
		table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setLayoutData(gridData);
		TableColumn tableColumn = new TableColumn(table, SWT.None);
		tableColumn.setText("variable");
		tableColumn.setWidth(150);
		tableColumn = new TableColumn(table, SWT.None);
		tableColumn.setText("value");
		tableColumn.setWidth(500);
		Label name = new Label(parent, SWT.BEGINNING);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 0);
		name.setText("Description");
		name.setLayoutData(gridData);
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Signal Monitor");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				lcd5000.getChromatogramRecording().setName(controlDevice.getName());
				eventBroker.post(IAcquisitionUIEvents.TOPIC_ACQUISITION_CHROMULAN_UI_CHROMATOGRAM_DISPLAY,
						lcd5000.getChromatogramRecording());
			}
		});
		buttonReadData = new Button(parent, SWT.CHECK);
		buttonReadData.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (buttonReadData.getSelection()) {
					lcd5000.start(false);
				} else {
					lcd5000.stop();
				}
			}
		});
		buttonReadData.setText("read data");
		if (lcd5000.isBeeingRecored()) {
			buttonReadData.setSelection(true);
		} else {
			buttonReadData.setSelection(false);
		}
		buttonReset = new Button(parent, SWT.PUSH);
		buttonReset.setText("reset");
		buttonReset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				lcd5000.newAcquisition();
			}
		});
		redrawTable();
	}

	private void enableButton(boolean enabled) {

		buttonReadData.setEnabled(enabled);
		buttonReset.setEnabled(enabled);
	}

	@Inject
	@Optional
	public void openConnection(
			@UIEventTopic(value = IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN) ULanConnection connection) {

		try {
			lcd5000.connect();
		} catch (IOException e) {
			// TODO: logger.warn(e);
		}
	}

	@PreDestroy
	public void preDestroy() {

		controlDevice.removePropertyChangeListener(listener);
	}

	private void redrawTable() {

		table.removeAll();
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Name");
		tableItem.setText(1, controlDevice.getName());
		tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Device type");
		tableItem.setText(1, controlDevice.getDeviceType().toString());
		tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Device description");
		tableItem.setText(1, controlDevice.getDeviceDescription().getDescription());
		tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Scan Interval (milliseconds)");
		tableItem.setText(1, Integer.toString(lcd5000.getScanInterva()));
		tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Scan Delay (milliseconds)");
		tableItem.setText(1, Integer.toString(lcd5000.getScanDelay()));
	}

	@Inject
	@Optional
	public void removeAcquisition(
			@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_END) IAcquisition analisis) {

		if (this.acquisition == analisis) {
			part.getTransientData().remove(IDetectorData.DETECTORS_DATA);
			enableButton(true);
			this.acquisition = null;
			if (analisis.isCompleted()) {
				lcd5000.newAcquisition();
				lcd5000.start(false);
			}
		}
	}

	@Inject
	@Optional
	public void setAcquisition(
			@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_SET) IAcquisition analisis) {

		if (this.acquisition == null && analisis != null && analisis.getDevicesProfile() != null
				&& IUlanControlDevices.contains(analisis.getDevicesProfile(), controlDevice.getDeviceID())) {
			this.acquisition = analisis;
		}
	}

	@Inject
	@Optional
	public void startRecording(
			@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_START_RECORDING) IAcquisition acquisition) {

		if (this.acquisition != null && this.acquisition == acquisition) {
			lcd5000.start(true);
			buttonReadData.setSelection(true);
			enableButton(false);
		}
	}

	@Inject
	@Optional
	public void stopRecording(
			@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_STOP_RECORDING) IAcquisition acquisition) {

		if (this.acquisition != null && this.acquisition == acquisition) {
			lcd5000.stop();
			Lcd5000Data data = new Lcd5000Data(controlDevice);
			data.setDescription("");
			data.setChromatogram(lcd5000.getChromatogramRecording().getChromatogram());
			List<IDetectorData> list = new LinkedList<IDetectorData>();
			list.add(data);
			part.getTransientData().put(IDetectorData.DETECTORS_DATA, list);
		}
	}
}
