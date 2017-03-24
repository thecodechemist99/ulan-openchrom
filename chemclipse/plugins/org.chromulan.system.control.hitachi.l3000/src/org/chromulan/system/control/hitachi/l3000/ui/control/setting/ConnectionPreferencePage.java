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
package org.chromulan.system.control.hitachi.l3000.ui.control.setting;

import java.io.IOException;

import org.chromulan.system.control.hitachi.l3000.model.ControlDevice;
import org.chromulan.system.control.hitachi.l3000.serial.AbstractSerialPort.BaudRate;
import org.chromulan.system.control.hitachi.l3000.serial.AbstractSerialPort.Delimiter;
import org.chromulan.system.control.hitachi.l3000.serial.AbstractSerialPort.Parity;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ConnectionPreferencePage extends PreferencePage {

	private static final Logger logger = Logger.getLogger(ConnectionPreferencePage.class);
	private IObservableValue<String> boudRate;
	private Button buttonClose;
	private Combo comboName;
	private ControlDevice controlDevice;
	private IObservableValue<String> controlSignal;
	final private DataBindingContext dbc = new DataBindingContext();
	private IObservableValue<String> delimiter;
	private Label labelCTSsignal;
	private IObservableValue<String> name;
	private IObservableValue<String> parity;

	public ConnectionPreferencePage(ControlDevice controlDevice) {
		super("Connection");
		try {
			logger.info("create object ConnectionPreferencePage");
			this.controlDevice = controlDevice;
			String name = this.controlDevice.getPortName();
			BaudRate baudeRate = controlDevice.getPortBaudRate();
			Delimiter delimiter = controlDevice.getPortDelimeter();
			Parity parity = controlDevice.getPortParity();
			String controlSignal = controlDevice.getPortDataControlSignal();
			this.name = new WritableValue<>(name, String.class);
			if(baudeRate != null) {
				this.boudRate = new WritableValue<>(Integer.toString(baudeRate.getBaudRate()), String.class);
			} else {
				this.boudRate = new WritableValue<>(Integer.toString(BaudRate.BOUD_RATE_4800.getBaudRate()), String.class);
			}
			if(delimiter != null) {
				this.delimiter = new WritableValue<>(delimiter.name(), String.class);
			} else {
				this.delimiter = new WritableValue<>(Delimiter.DELIMITER_CR.name(), String.class);
			}
			if(parity != null) {
				this.parity = new WritableValue<>(parity.name(), String.class);
			} else {
				this.parity = new WritableValue<>(Parity.PARITY_EVEN.name(), String.class);
			}
			this.controlSignal = new WritableValue<>(controlSignal, String.class);
		} catch(Throwable e) {
			logger.error("Exception in constor", e);
			throw e;
		}
	}

	@Override
	protected Control createContents(Composite composite) {

		try {
			logger.info("run method createContents");
			Composite parent = new Composite(composite, SWT.None);
			Label label = new Label(parent, SWT.None);
			label.setText("Select Port Name");
			comboName = getNames(parent);
			dbc.bindValue(WidgetProperties.selection().observe(comboName), name, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);
			label = new Label(parent, SWT.None);
			label.setText("Select Boud rate");
			Combo combo = getBaudRate(parent);
			dbc.bindValue(WidgetProperties.selection().observe(combo), boudRate, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);
			label = new Label(parent, SWT.None);
			label.setText("Select Delimiter");
			combo = getDelimiter(parent);
			dbc.bindValue(WidgetProperties.selection().observe(combo), delimiter, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);
			label = new Label(parent, SWT.None);
			label.setText("Parity even");
			combo = getParity(parent);
			dbc.bindValue(WidgetProperties.selection().observe(combo), parity, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);
			label = new Label(parent, SWT.None);
			label.setText("Data control signal");
			combo = getControlSignal(parent);
			dbc.bindValue(WidgetProperties.selection().observe(combo), controlSignal, new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);
			label = new Label(parent, SWT.None);
			label.setText("CTS signal");
			labelCTSsignal = new Label(parent, SWT.None);
			buttonClose = new Button(parent, SWT.PUSH);
			buttonClose.setText("close");
			buttonClose.addListener(SWT.Selection, (event) -> {
				controlDevice.closeSerialPort();
				updateWidget();
			});
			GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(parent);
			updateWidget();
			return composite;
		} catch(Throwable e) {
			logger.error("Exception in create Control", e);
			throw e;
		}
	}

	private Combo getBaudRate(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		for(BaudRate baudRate : BaudRate.values()) {
			combo.add(Integer.toString(baudRate.getBaudRate()));
		}
		return combo;
	}

	private Combo getControlSignal(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		for(String controlSignal : controlDevice.getDataControlTypes()) {
			combo.add(controlSignal);
		}
		return combo;
	}

	private Combo getDelimiter(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		for(Delimiter delimiter : Delimiter.values()) {
			combo.add(delimiter.name());
		}
		return combo;
	}

	private Combo getNames(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		for(String name : controlDevice.getSerialPortNames()) {
			combo.add(name);
		}
		return combo;
	}

	private Combo getParity(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		for(Parity parity : Parity.values()) {
			combo.add(parity.name());
		}
		return combo;
	}

	@Override
	protected void performDefaults() {

		dbc.updateTargets();
	}

	@Override
	public boolean performOk() {

		dbc.updateModels();
		setErrorMessage(null);
		String name = this.name.getValue();
		String parity = this.parity.getValue();
		String controlSignal = this.controlSignal.getValue();
		String delimiter = this.delimiter.getValue();
		String baudeRate = this.boudRate.getValue();
		int boudrate = Integer.valueOf(baudeRate);
		if(name == null || name.isEmpty() || controlSignal == null || controlSignal.isEmpty()) {
			return false;
		}
		if(!controlDevice.isConnected()) {
			try {
				return controlDevice.openSerialPort(name, BaudRate.getBaudeRate(boudrate), Parity.valueOf(parity), controlSignal, Delimiter.valueOf(delimiter));
			} catch(IOException e) {
				setErrorMessage(e.getMessage());
				controlDevice.closeSerialPort();
			} finally {
				updateWidget();
			}
		} else {
			try {
				return controlDevice.setParametrsSerialPort(BaudRate.getBaudeRate(boudrate), Parity.valueOf(parity), controlSignal, Delimiter.valueOf(delimiter));
			} catch(IOException e) {
				setErrorMessage(e.getMessage());
				controlDevice.closeSerialPort();
			} finally {
				updateWidget();
			}
		}
		return false;
	}

	private void updateWidget() {

		if(controlDevice.isConnected()) {
			comboName.setEnabled(false);
			buttonClose.setEnabled(true);
			if(controlDevice.getCTS()) {
				labelCTSsignal.setText("Yes");
			} else {
				labelCTSsignal.setText("No");
			}
		} else {
			comboName.setEnabled(true);
			buttonClose.setEnabled(false);
			labelCTSsignal.setText("No");
		}
		labelCTSsignal.getParent().update();
	}
}
