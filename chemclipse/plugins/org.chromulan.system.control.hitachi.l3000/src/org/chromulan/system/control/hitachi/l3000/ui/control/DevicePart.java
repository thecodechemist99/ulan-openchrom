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

	private static final Logger logger = Logger.getLogger(DevicePart.class);
	private Button buttonResetParamets;
	private Button buttonSendStart;
	private Button buttonSendStop;
	private Button buttonSetParametrs;
	private Combo comboOutputType;
	@Inject
	private Composite composite;
	final private DataBindingContext dbc = new DataBindingContext();
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

	private Combo getOutputType(Composite composite) {

		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.add(ControlDevice.SETTING_DATA_OUTPUT_ANALOG);
		combo.add(ControlDevice.SETTING_DATA_OUTPUT_DATA_COMMUNICATION);
		return combo;
	}

	@PostConstruct
	public void postConstruct() {

		device = deviceInterface.getControlDevice();
		IObservableValue<Integer> propertyOutputType = BeanProperties.value(ControlDevice.PROPERTY_OUTPUT_TYPE, Integer.class).observe(device);
		IObservableValue<Float> propertyTimeInterval = BeanProperties.value(ControlDevice.PROPERTY_TIME_INTERVAL, Float.class).observe(device);
		IObservableValue<Float> propertyWavelenghtInterval = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_INTERVA, Float.class).observe(device);
		IObservableValue<Integer> propertyWavelenghtRangeFrom = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_RANGE_FROM, Integer.class).observe(device);
		IObservableValue<Integer> propertyWavelenghtRangeTo = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_RANGE_TO, Integer.class).observe(device);
		IObservableValue<Boolean> propertySendStart = BeanProperties.value(ControlDevice.PROPERTY_SEND_START, Boolean.class).observe(device);
		IObservableValue<Boolean> propertySendStop = BeanProperties.value(ControlDevice.PROPERTY_SEND_STOP, Boolean.class).observe(device);
		BeanProperties.value(ControlDevice.PROPERTY_AUTOSET_VALUE, Boolean.class).observe(device);
		Label label = new Label(composite, SWT.None);
		label.setText("Device connected");
		labelConnection = new Label(composite, SWT.None);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_DATA_OUTPUT_TYPE);
		comboOutputType = getOutputType(composite);
		dbc.bindValue(WidgetProperties.selection().observe(comboOutputType), propertyOutputType, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setConverter(new ConvertoDataComunicationTypeToNumber()), new UpdateValueStrategy().setConverter(new ConvertoNumberToDataComunicationType()));
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_TIME_INTERVAL);
		textTimeInterval = new Text(composite, SWT.BORDER);
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textTimeInterval), propertyTimeInterval, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setAfterConvertValidator(new ValidatorTimeInterval(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_INTERVAL);
		textWaveLenghtInterva = new Text(composite, SWT.BORDER);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textWaveLenghtInterva), propertyWavelenghtInterval, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setAfterConvertValidator(new ValidatorWavelenghtInterval(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_RANGE_FROM);
		textWaveRangeFrom = new Text(composite, SWT.BORDER);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textWaveRangeFrom), propertyWavelenghtRangeFrom, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setAfterConvertValidator(new ValidatorWavelenghtRangeFrom(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_RANGE_TO);
		textWaveRangeTo = new Text(composite, SWT.BORDER);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(textWaveRangeTo), propertyWavelenghtRangeTo, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST).setAfterConvertValidator(new ValidatorWavelenghtRangeTo(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_SEND_START);
		buttonSendStart = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(buttonSendStart), propertySendStart, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_SEND_STOP);
		buttonSendStop = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(buttonSendStop), propertySendStop, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);
		buttonSetParametrs = new Button(composite, SWT.PUSH);
		buttonSetParametrs.setText("Set Parametrs");
		buttonSetParametrs.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					dbc.updateModels();
					if(device.isConnected()) {
						device.sendDataOutputType();
						device.sendTimeInterval();
						device.sendWavelenghtInterval();
						device.sendWaveLenghtRange();
						device.resetChromatogram();
					}
					device.resetChromatogram();
				} catch(IOException e1) {
					device.closeSerialPort();
				}
			}
		});
		buttonResetParamets = new Button(composite, SWT.PUSH);
		buttonResetParamets.setText("get defautl parametrs");
		buttonResetParamets.addListener(SWT.Selection, (event) -> {
			dbc.updateTargets();
		});
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
		} else {
			labelConnection.setText("NO");
		}
		labelConnection.getParent().update();
	}

	private void setWidgeStartRecording() {

		buttonResetParamets.setEnabled(false);
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

		buttonResetParamets.setEnabled(true);
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

	@Inject
	@Optional
	public void updateConnection(@UIEventTopic(value = IControlDevicesEvents.TOPIC_CONTROL_DEVICES_CONTROL) ControlDevice controlDevice) {

		if(controlDevice != null) {
			setWidgeConnection();
		}
	}
}
