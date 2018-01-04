/*******************************************************************************
 * Copyright (c) 2016, 2018 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.hitachi.l3000.ui.control;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.events.IAcquisitionEvents;
import org.chromulan.system.control.events.IAcquisitionUIEvents;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.hitachi.l3000.model.ControlDevice;
import org.chromulan.system.control.hitachi.l3000.model.DeviceInterface;
import org.chromulan.system.control.hitachi.l3000.ui.control.setting.ConnectionPreferencePage;
import org.chromulan.system.control.hitachi.l3000.ui.support.ConvertoDataComunicationTypeToNumber;
import org.chromulan.system.control.hitachi.l3000.ui.support.ConvertoNumberToDataComunicationType;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorTimeInterval;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorWavelenghtInterval;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorWavelenghtRangeFrom;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorWavelenghtRangeTo;
import org.chromulan.system.control.model.IAcquisition;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DevicePart {

	private static final Logger logger = Logger.getLogger(DevicePart.class);
	private Combo comboOutputType;
	final private DataBindingContext dbc = new DataBindingContext();
	private ControlDevice device;
	@Inject
	private DeviceInterface deviceInterface;
	@Inject
	private IEventBroker eventBroker;
	private Label labelConnection;

	public DevicePart() {
	}

	private void createControlDevice(Composite parent, Object layoutData) {

		Group groupControlDevce = new Group(parent, SWT.None);
		groupControlDevce.setLayoutData(layoutData);
		groupControlDevce.setLayout(new GridLayout(2, true));
		groupControlDevce.setText("Control Device");
		IObservableValue<Integer> propertyOutputType = BeanProperties.value(ControlDevice.PROPERTY_OUTPUT_TYPE, Integer.class).observe(device);
		IObservableValue<Float> propertyTimeInterval = BeanProperties.value(ControlDevice.PROPERTY_TIME_INTERVAL, Float.class).observe(device);
		IObservableValue<Float> propertyWavelenghtInterval = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_INTERVA, Float.class).observe(device);
		IObservableValue<Integer> propertyWavelenghtRangeFrom = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_RANGE_FROM, Integer.class).observe(device);
		IObservableValue<Integer> propertyWavelenghtRangeTo = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_RANGE_TO, Integer.class).observe(device);
		BeanProperties.value(ControlDevice.PROPERTY_AUTOSET_VALUE, Boolean.class).observe(device);
		Label label = new Label(groupControlDevce, SWT.None);
		label.setText("Device connected");
		labelConnection = new Label(groupControlDevce, SWT.None);
		label = new Label(groupControlDevce, SWT.None);
		label.setText(ControlDevice.SETTING_DATA_OUTPUT_TYPE);
		comboOutputType = getOutputType(groupControlDevce);
		dbc.bindValue(WidgetProperties.selection().observe(comboOutputType), propertyOutputType, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setConverter(new ConvertoDataComunicationTypeToNumber()), new UpdateValueStrategy().setConverter(new ConvertoNumberToDataComunicationType()));
		label = new Label(groupControlDevce, SWT.None);
		label.setText(ControlDevice.SETTING_TIME_INTERVAL);
		Text textTimeInterval = new Text(groupControlDevce, SWT.BORDER);
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textTimeInterval), propertyTimeInterval, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setAfterConvertValidator(new ValidatorTimeInterval(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(textTimeInterval);
		label = new Label(groupControlDevce, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_INTERVAL);
		Text textWaveLenghtInterva = new Text(groupControlDevce, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(textWaveLenghtInterva);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textWaveLenghtInterva), propertyWavelenghtInterval, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setAfterConvertValidator(new ValidatorWavelenghtInterval(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(groupControlDevce, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_RANGE_FROM);
		Text textWaveRangeFrom = new Text(groupControlDevce, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(textWaveRangeFrom);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textWaveRangeFrom), propertyWavelenghtRangeFrom, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setAfterConvertValidator(new ValidatorWavelenghtRangeFrom(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(groupControlDevce, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_RANGE_TO);
		Text textWaveRangeTo = new Text(groupControlDevce, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(textWaveRangeTo);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textWaveRangeTo), propertyWavelenghtRangeTo, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setAfterConvertValidator(new ValidatorWavelenghtRangeTo(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		Button button = new Button(groupControlDevce, SWT.PUSH);
		button.setText("Set Parametrs");
		button.addListener(SWT.Selection, e -> {
			try {
				if(device.isConnected()) {
					dbc.updateModels();
					device.sendDataOutputType();
					device.sendTimeInterval();
					device.sendWavelenghtInterval();
					device.sendWaveLenghtRange();
					device.resetChromatogram();
				}
			} catch(IOException e1) {
				device.closeSerialPort();
			}
		});
		GridDataFactory.swtDefaults().span(2, 1).applyTo(button);
		button = new Button(groupControlDevce, SWT.PUSH);
		button.setText("Start");
		button.addListener(SWT.Selection, e -> {
			try {
				device.sendStart();
			} catch(IOException e1) {
			}
		});
		button = new Button(groupControlDevce, SWT.PUSH);
		button.setText("Stop");
		button.addListener(SWT.Selection, e -> {
			try {
				device.sendStop();
			} catch(IOException e1) {
			}
		});
	}

	private Combo getOutputType(Composite composite) {

		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.add(ControlDevice.SETTING_DATA_OUTPUT_ANALOG);
		combo.add(ControlDevice.SETTING_DATA_OUTPUT_DATA_COMMUNICATION);
		return combo;
	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		device = deviceInterface.getControlDevice();
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		createControlDevice(composite, new GridData(GridData.FILL_HORIZONTAL));
		IObservableValue<Boolean> propertyStartOnDataInput = BeanProperties.value(ControlDevice.PROPERTY_START_ON_DATA_INPUT, Boolean.class).observe(device);
		IObservableValue<Boolean> propertyStopOnDataInput = BeanProperties.value(ControlDevice.PROPERTY_STOP_ON_DATA_INPUT, Boolean.class).observe(device);
		Button startOnDataInput = new Button(composite, SWT.CHECK);
		startOnDataInput.setText(ControlDevice.SETTING_START_ON_DATA_INPUT);
		dbc.bindValue(WidgetProperties.selection().observe(startOnDataInput), propertyStartOnDataInput);
		Button stopOnDataInput = new Button(composite, SWT.CHECK);
		stopOnDataInput.setText(ControlDevice.SETTING_STOP_ON_DATA_INPUT);
		dbc.bindValue(WidgetProperties.selection().observe(stopOnDataInput), propertyStopOnDataInput);
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Data Monitor");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				eventBroker.post(IAcquisitionUIEvents.TOPIC_ACQUISITION_CHROMULAN_UI_CHROMATOGRAM_DISPLAY, device.getChromatogram());
			}
		});
		button = new Button(composite, SWT.PUSH);
		button.setText("Connection");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					logger.info("press button connection");
					PreferenceManager manager = new PreferenceManager();
					ConnectionPreferencePage settings = new ConnectionPreferencePage(device);
					PreferenceNode nodeBase = new PreferenceNode("Connection", settings);
					manager.addToRoot(nodeBase);
					PreferenceDialog dialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), manager);
					dialog.open();
					setWidgeConnection();
				} catch(Throwable e1) {
					logger.error("Exception in button", e1);
					throw e1;
				}
			}
		});
		setWidge();
	}

	private void setWidge() {

		IAcquisition acquisition = deviceInterface.getAcqiusition();
		if(acquisition == null || !acquisition.isRunning()) {
			setWidgeStopRecording();
		} else {
			setWidgeStartRecording();
		}
		setWidgeConnection();
	}

	private void setWidgeConnection() {

		if(device.isConnected()) {
			labelConnection.setText("YES");
		} else {
			labelConnection.setText("NO");
		}
		labelConnection.getParent().update();
	}

	private void setWidgeStartRecording() {

		comboOutputType.setEnabled(false);
	}

	private void setWidgeStopRecording() {

		comboOutputType.setEnabled(true);
	}

	@Inject
	@Optional
	public void startAcqusition(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_START_RECORDING) IAcquisition acquisition) {

		if(acquisition != null && acquisition == deviceInterface.getAcqiusition()) {
			setWidgeStartRecording();
		}
	}

	@Inject
	@Optional
	public void stopAcqusition(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_START_RECORDING) IAcquisition acquisition) {

		if(acquisition != null && acquisition == deviceInterface.getAcqiusition()) {
			setWidgeStopRecording();
		}
	}

	@Inject
	@Optional
	public void updateConnection(@UIEventTopic(value = IControlDevicesEvents.TOPIC_CONTROL_DEVICES_CONTROL) ControlDevice controlDevice) {

		if(controlDevice != null) {
			setWidgeConnection();
		}
	}
}
