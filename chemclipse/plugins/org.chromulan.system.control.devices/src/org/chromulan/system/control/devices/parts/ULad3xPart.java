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
package org.chromulan.system.control.devices.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.devices.base.DetectorControler;
import org.chromulan.system.control.devices.base.IUlanControlDevices;
import org.chromulan.system.control.events.IAcquisitionEvents;
import org.chromulan.system.control.events.IAcquisitionUIEvents;
import org.chromulan.system.control.model.IAcquisition;
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

public class ULad3xPart {

	private IAcquisition acquisition;
	private Button buttonReadData;
	private Button buttonReset;
	private DetectorControler controlDevice;
	@Inject
	private IEventBroker eventBroker;
	private PropertyChangeListener listener;
	@Inject
	private MPart part;
	private Table table;

	public ULad3xPart() {
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
		controlDevice = (DetectorControler)part.getObject();
		controlDevice.getControlDevice().addPropertyChangeListener(listener);
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

				eventBroker.post(IAcquisitionUIEvents.TOPIC_ACQUISITION_CHROMULAN_UI_CHROMATOGRAM_DISPLAY, controlDevice.getControlDevice().getChromatogramRecording());
			}
		});
		buttonReadData = new Button(parent, SWT.CHECK);
		buttonReadData.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(buttonReadData.getSelection()) {
					controlDevice.getControlDevice().setBeeingRecored(true);
				} else {
					controlDevice.getControlDevice().setBeeingRecored(false);
				}
			}
		});
		buttonReadData.setText("read data");
		if(controlDevice.getControlDevice().isBeeingRecored()) {
			buttonReadData.setSelection(true);
		} else {
			buttonReadData.setSelection(false);
		}
		buttonReset = new Button(parent, SWT.PUSH);
		buttonReset.setText("reset");
		buttonReset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				controlDevice.resetChromatogramData();
			}
		});
		redrawTable();
	}

	private void enableButton(boolean enabled) {

		buttonReadData.setEnabled(enabled);
		buttonReset.setEnabled(enabled);
	}

	@PreDestroy
	public void preDestroy() {

		controlDevice.getControlDevice().removePropertyChangeListener(listener);
	}

	private void redrawTable() {

		table.removeAll();
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Name");
		tableItem.setText(1, controlDevice.getControlDevice().getName());
		tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Device type");
		tableItem.setText(1, controlDevice.getControlDevice().getDeviceType().toString());
		tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Device description");
		tableItem.setText(1, controlDevice.getControlDevice().getDescription());
		tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Scan Interval (milliseconds)");
		tableItem.setText(1, Integer.toString(controlDevice.getControlDevice().getScanInterval()));
		tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Scan Delay (milliseconds)");
		tableItem.setText(1, Integer.toString(controlDevice.getControlDevice().getScanDelay()));
	}

	@Inject
	@Optional
	public void removeAcquisition(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_END) IAcquisition analisis) {

		if(this.acquisition == analisis) {
			enableButton(true);
			this.acquisition = null;
		}
	}

	@Inject
	@Optional
	public void setAcquisition(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_SET) IAcquisition analisis) {

		if(this.acquisition == null && analisis != null && analisis.getDevicesProfile() != null && IUlanControlDevices.contains(analisis.getDevicesProfile().getControlDevices(), controlDevice.getControlDevice().getDeviceID())) {
			this.acquisition = analisis;
		}
	}

	@Inject
	@Optional
	public void startRecording(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_START_RECORDING) IAcquisition acquisition) {

		if(this.acquisition != null && this.acquisition == acquisition) {
			buttonReadData.setSelection(true);
			enableButton(false);
		}
	}
}
