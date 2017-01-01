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
package org.chromulan.system.control.hitachi.l3000.ui.control;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.events.IAcquisitionEvents;
import org.chromulan.system.control.events.IAcquisitionUIEvents;
import org.chromulan.system.control.hitachi.l3000.model.ControlDevice;
import org.chromulan.system.control.hitachi.l3000.model.DeviceInterface;
import org.chromulan.system.control.hitachi.l3000.model.evens.DeviceEvents;
import org.chromulan.system.control.hitachi.l3000.ui.control.setting.ConnectionPreferencePage;
import org.chromulan.system.control.hitachi.l3000.ui.support.ConvertoDataComunicationTypeToNumber;
import org.chromulan.system.control.hitachi.l3000.ui.support.ConvertoNumberToDataComunicationType;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorTimeInterval;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorWavelenghtInterval;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorWavelenghtRangeFrom;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorWavelenghtRangeTo;
import org.chromulan.system.control.model.IAcquisition;
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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DevicePart {

	private Button buttonSendStart;
	private Button buttonSendStop;
	private Button buttonSetParametrs;
	private Combo comboOutputType;
	@Inject
	private Composite composite;
	private ControlDevice device;
	@Inject
	private DeviceInterface deviceInterface;
	@Inject
	private IEventBroker eventBroker;
	private Label labelConnection;
	private Text textTimeInterval;
	private Text textWaveLenghtInterva;
	private Text textWaveRangeFrom;
	private Text textWaveRangeTo;

	public DevicePart() {
	}

	@Inject
	@Optional
	public void closeConnection(@UIEventTopic(value = DeviceEvents.TOPIC_HITACHI_L3000_CONNECTION_CLOSE) DeviceInterface deviceInterface) {

		if(deviceInterface != null) {
			setWidgeConnection();
		}
	}

	private Combo getOutputType(Composite composite) {

		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.add(ControlDevice.SETTING_DATA_OUTPUT_ANALOG);
		combo.add(ControlDevice.SETTING_DATA_OUTPUT_DATA_COMMUNICATION);
		return combo;
	}

	@Inject
	@Optional
	public void openConnection(@UIEventTopic(value = DeviceEvents.TOPIC_HITACHI_L3000_CONNECTION_OPEN) DeviceInterface deviceInterface) {

		if(deviceInterface != null) {
			setWidgeConnection();
		}
	}

	@PostConstruct
	public void postConstruct() {

		device = deviceInterface.getControlDevice();
		DataBindingContext dbc = new DataBindingContext();
		IObservableValue propertyOutputType = BeanProperties.value(ControlDevice.PROPERTY_OUTPUT_TYPE, Integer.class).observe(device);
		IObservableValue propertyTimeInterval = BeanProperties.value(ControlDevice.PROPERTY_TIME_INTERVAL, Float.class).observe(device);
		IObservableValue propertyWavelenghtInterval = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_INTERVA, Float.class).observe(device);
		IObservableValue propertyWavelenghtRangeFrom = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_RANGE_FROM, Integer.class).observe(device);
		IObservableValue propertyWavelenghtRangeTo = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_RANGE_TO, Integer.class).observe(device);
		IObservableValue propertySendStart = BeanProperties.value(ControlDevice.PROPERTY_SEND_START, Boolean.class).observe(device);
		IObservableValue propertySendStop = BeanProperties.value(ControlDevice.PROPERTY_SEND_STOP, Boolean.class).observe(device);
		BeanProperties.value(ControlDevice.PROPERTY_AUTOSET_VALUE, Boolean.class).observe(device);
		Label label = new Label(composite, SWT.None);
		label.setText("Device connected");
		labelConnection = new Label(composite, SWT.None);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_DATA_OUTPUT_TYPE);
		comboOutputType = getOutputType(composite);
		dbc.bindValue(WidgetProperties.selection().observe(comboOutputType), propertyOutputType, new UpdateValueStrategy().setConverter(new ConvertoDataComunicationTypeToNumber()), new UpdateValueStrategy().setConverter(new ConvertoNumberToDataComunicationType()));
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_TIME_INTERVAL);
		textTimeInterval = new Text(composite, SWT.BORDER);
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textTimeInterval), propertyTimeInterval, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorTimeInterval(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_INTERVAL);
		textWaveLenghtInterva = new Text(composite, SWT.BORDER);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textWaveLenghtInterva), propertyWavelenghtInterval, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorWavelenghtInterval(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_RANGE_FROM);
		textWaveRangeFrom = new Text(composite, SWT.BORDER);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textWaveRangeFrom), propertyWavelenghtRangeFrom, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorWavelenghtRangeFrom(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_RANGE_TO);
		textWaveRangeTo = new Text(composite, SWT.BORDER);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textWaveRangeTo), propertyWavelenghtRangeTo, new UpdateValueStrategy().setAfterConvertValidator(new ValidatorWavelenghtRangeTo(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_SEND_START);
		buttonSendStart = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(buttonSendStart), propertySendStart, new UpdateValueStrategy(), null);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_SEND_STOP);
		buttonSendStop = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(buttonSendStop), propertySendStop, new UpdateValueStrategy(), null);
		buttonSetParametrs = new Button(composite, SWT.PUSH);
		buttonSetParametrs.setText("Set Device");
		buttonSetParametrs.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				deviceInterface.sendTimeInterval();
				deviceInterface.sendWavelenghtInterval();
				deviceInterface.sendWaveLenghtRange();
			}
		});
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Data Monitor");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				eventBroker.post(IAcquisitionUIEvents.TOPIC_ACQUISITION_CHROMULAN_UI_CHROMATOGRAM_DISPLAY, device.getDatareceive().getChromatogram());
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

				PreferenceManager manager = new PreferenceManager();
				ConnectionPreferencePage settings = new ConnectionPreferencePage(deviceInterface);
				PreferenceNode nodeBase = new PreferenceNode("Connection", settings);
				manager.addToRoot(nodeBase);
				PreferenceDialog dialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), manager);
				dialog.open();
				setWidgeConnection();
			}
		});
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
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
			buttonSetParametrs.setEnabled(true);
		} else {
			labelConnection.setText("NO");
			buttonSetParametrs.setEnabled(false);
		}
		labelConnection.getParent().update();
	}

	private void setWidgeStartRecording() {

		buttonSendStart.setEnabled(false);
		buttonSendStop.setEnabled(false);
		buttonSetParametrs.setEnabled(false);
		comboOutputType.setEnabled(false);
		textTimeInterval.setEnabled(false);
		textWaveRangeFrom.setEnabled(false);
		textWaveRangeTo.setEnabled(false);
		textWaveLenghtInterva.setEnabled(false);
	}

	private void setWidgeStopRecording() {

		buttonSendStart.setEnabled(true);
		buttonSendStop.setEnabled(true);
		buttonSetParametrs.setEnabled(true);
		comboOutputType.setEnabled(true);
		textTimeInterval.setEnabled(true);
		textWaveRangeFrom.setEnabled(true);
		textWaveRangeTo.setEnabled(true);
		textWaveLenghtInterva.setEnabled(true);
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
}
