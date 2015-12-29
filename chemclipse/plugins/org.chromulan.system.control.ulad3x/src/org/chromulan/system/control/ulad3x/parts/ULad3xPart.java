/*******************************************************************************
 * Copyright (c) 2015 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ulad3x.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.ULanConnection;
import org.chromulan.system.control.model.data.IChromatogramCSDData;
import org.chromulan.system.control.ui.events.IAnalysisUIEvents;
import org.chromulan.system.control.ulad3x.model.ULad3x;
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
import org.eclipse.swt.widgets.Text;

public class ULad3xPart {

	private IAnalysis analysis;
	private Button buttonReadData;
	private Button buttonReset;
	private IControlDevice controlDevice;
	@Inject
	private IEventBroker eventBroker;
	private PropertyChangeListener listener;
	@Inject
	private MPart part;
	private Table table;
	private Text textDescription;
	private ULad3x uLad3x;

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
		controlDevice = (IControlDevice)part.getObject();
		controlDevice.addPropertyChangeListener(listener);
		uLad3x = new ULad3x(controlDevice);
		try {
			uLad3x.connect();
			uLad3x.start(false);
		} catch(IOException e1) {
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
		textDescription = new Text(parent, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 0);
		textDescription.setLayoutData(gridData);
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Signal Monitor");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				eventBroker.post(IAnalysisUIEvents.TOPIC_ANALYSIS_CHROMULAN_UI_CHROMATOGRAM_DISPLAY, uLad3x.getChromatogramRecording());
			}
		});
		buttonReadData = new Button(parent, SWT.CHECK);
		buttonReadData.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(buttonReadData.getSelection()) {
					uLad3x.start(false);
				} else {
					uLad3x.stop();
				}
			}
		});
		buttonReadData.setText("read data");
		if(uLad3x.isBeeingRecored()) {
			buttonReadData.setSelection(true);
		} else {
			buttonReadData.setSelection(false);
		}
		buttonReset = new Button(parent, SWT.PUSH);
		buttonReset.setText("reset");
		buttonReset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				uLad3x.reset();
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
	public void openConnection(@UIEventTopic(value = IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_OPEN) ULanConnection connection) {

		try {
			uLad3x.connect();
		} catch(IOException e) {
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
		tableItem.setText(0, "Scan Interval");
		tableItem.setText(1, Integer.toString(uLad3x.getScanInterva()));
		tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Scan Delay");
		tableItem.setText(1, Integer.toString(uLad3x.getScanDelay()));
	}

	@Inject
	@Optional
	public void removeAnalysis(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END) IAnalysis analisis) {

		if(this.analysis == analisis) {
			part.getContext().remove(IChromatogramCSDData.class);
			enableButton(true);
			this.analysis = null;
			if(analisis.hasBeenRecorded()) {
				uLad3x.newRecord();
				uLad3x.start(false);
			}
		}
	}

	@Inject
	@Optional
	public void setAnalysis(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_SET) IAnalysis analisis) {

		if(this.analysis == null && analisis.getDevicesProfile() != null && analisis.getDevicesProfile().getControlDevices().contains(controlDevice.getID())) {
			this.analysis = analisis;
		}
	}

	@Inject
	@Optional
	public void startRecording(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START_RECORDING) IAnalysis analysis) {

		if(this.analysis != null && this.analysis == analysis) {
			uLad3x.start(true);
			buttonReadData.setSelection(true);
			enableButton(false);
		}
	}

	@Inject
	@Optional
	public void stopRecording(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_STOP_RECORDING) IAnalysis analysis) {

		if(this.analysis != null && this.analysis == analysis) {
			uLad3x.stop();
			uLad3x.getChromatogramRecording().setDescription(textDescription.getText());
			if(uLad3x.getChromatogramRecording() instanceof IChromatogramCSDData) {
				IChromatogramCSDData chromatogramCSDData = (IChromatogramCSDData)uLad3x.getChromatogramRecording();
				part.getContext().set(IChromatogramCSDData.class, chromatogramCSDData);
			}
		}
	}
}
