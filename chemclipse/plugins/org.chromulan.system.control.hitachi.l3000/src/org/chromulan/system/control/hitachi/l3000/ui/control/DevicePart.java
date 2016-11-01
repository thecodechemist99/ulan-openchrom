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
package org.chromulan.system.control.hitachi.l3000.ui.control;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.hitachi.l3000.model.ControlDevice;
import org.chromulan.system.control.hitachi.l3000.model.DeviceInterface;
import org.chromulan.system.control.hitachi.l3000.ui.support.ConvertoDataComunicationTypeToNumber;
import org.chromulan.system.control.hitachi.l3000.ui.support.ConvertoNumberToDataComunicationType;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorTimeInterval;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorWavelenghtInterval;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorWavelenghtRangeFrom;
import org.chromulan.system.control.hitachi.l3000.ui.support.ValidatorWavelenghtRangeTo;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DevicePart {

	@Inject
	private Composite composite;
	private ControlDevice device;
	@Inject
	private DeviceInterface deviceInterface;

	public DevicePart() {
	}

	private Combo getOutputType(Composite composite) {

		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.add(ControlDevice.SETTING_DATA_OUTPUT_ANALOG);
		combo.add(ControlDevice.SETTING_DATA_OUTPUT_DATA_COMMUNICATION);
		return combo;
	}

	private Combo getSelectPortName(Composite composite) {

		Combo combo = new Combo(composite, SWT.READ_ONLY);
		return combo;
	}

	@PostConstruct
	public void postConstruct() {

		device = deviceInterface.getControlDevice();
		DataBindingContext dbc = new DataBindingContext();
		IObservableValue propertyPortName = BeanProperties.value(ControlDevice.PROPERTY_PORT_NAME, String.class).observe(device);
		IObservableValue propertyOutputType = BeanProperties.value(ControlDevice.PROPERTY_OUTPUT_TYPE, Integer.class).observe(device);
		IObservableValue propertyTimeInterval = BeanProperties.value(ControlDevice.PROPERTY_TIME_INTERVAL, Float.class).observe(device);
		IObservableValue propertyWavelenghtInterval = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_INTERVA, Float.class).observe(device);
		IObservableValue propertyWavelenghtRangeFrom = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_RANGE_FROM, Integer.class).observe(device);
		IObservableValue propertyWavelenghtRangeTo = BeanProperties.value(ControlDevice.PROPERTY_WAVELENGHT_RANGE_TO, Integer.class).observe(device);
		IObservableValue propertySendStart = BeanProperties.value(ControlDevice.PROPERTY_SEND_START, Boolean.class).observe(device);
		IObservableValue propertySendStop = BeanProperties.value(ControlDevice.PROPERTY_SEND_STOP, Boolean.class).observe(device);
		BeanProperties.value(ControlDevice.PROPERTY_AUTOSET_VALUE, Boolean.class).observe(device);
		Label label = new Label(composite, SWT.None);
		label.setText("Device");
		label = new Label(composite, SWT.None);
		label.setText(device.getName());
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_NAME_PORT);
		Combo combo = getSelectPortName(composite);
		dbc.bindValue(WidgetProperties.selection().observe(combo), propertyPortName);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_DATA_OUTPUT_TYPE);
		combo = getOutputType(composite);
		dbc.bindValue(WidgetProperties.selection().observe(combo), propertyOutputType, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setConverter(new ConvertoDataComunicationTypeToNumber()), new UpdateValueStrategy().setConverter(new ConvertoNumberToDataComunicationType()));
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_TIME_INTERVAL);
		Text text = new Text(composite, SWT.BORDER);
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), propertyTimeInterval, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new ValidatorTimeInterval(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_INTERVAL);
		text = new Text(composite, SWT.BORDER);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), propertyWavelenghtInterval, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new ValidatorWavelenghtInterval(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_RANGE_FROM);
		text = new Text(composite, SWT.BORDER);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), propertyWavelenghtRangeFrom, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new ValidatorWavelenghtRangeFrom(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_WAVELENGHT_RANGE_TO);
		text = new Text(composite, SWT.BORDER);
		binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), propertyWavelenghtRangeTo, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new ValidatorWavelenghtRangeTo(device)), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_SEND_START);
		Button button = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(button), propertySendStart, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);
		label = new Label(composite, SWT.None);
		label.setText(ControlDevice.SETTING_SEND_STOP);
		button = new Button(composite, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(button), propertySendStop, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);
		final Button buttonSetParametr = new Button(composite, SWT.PUSH);
		buttonSetParametr.setText("Set Paramets");
		buttonSetParametr.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				dbc.updateModels();
			}
		});
		button = new Button(composite, SWT.PUSH);
		button.setText("Reset Parametrs");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				dbc.updateTargets();
			}
		});
		button = new Button(composite, SWT.PUSH);
		button.setText("Reset Parametrs");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
	}
}
