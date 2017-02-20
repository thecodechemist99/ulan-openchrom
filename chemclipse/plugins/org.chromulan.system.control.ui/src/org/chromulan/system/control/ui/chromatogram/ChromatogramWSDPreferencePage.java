/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.chromatogram;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.preference.PreferencePageSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ChromatogramWSDPreferencePage extends PreferencePage {

	private IObservableMap<Double, Boolean> attributesMap;
	final private DataBindingContext dbc = new DataBindingContext();
	private ChromatogramWSDOverviewUI chromatogramWSDOverviewUI;

	public ChromatogramWSDPreferencePage(ChromatogramWSDOverviewUI chromatogramWSDOverviewUI) {
		super("Select wavelenght");
		this.chromatogramWSDOverviewUI = chromatogramWSDOverviewUI;
		this.attributesMap = new WritableMap<>();
		Map<Double, Boolean> map = chromatogramWSDOverviewUI.getChromatogramWSD().getSelectedWaveLengths();
		attributesMap.putAll(map);
	}

	@Override
	protected Control createContents(Composite parent) {

		PreferencePageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.NONE);
		Iterator<Map.Entry<Double, Boolean>> iterator = attributesMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Button button = new Button(composite, SWT.CHECK);
			Entry<Double, Boolean> entry = iterator.next();
			button.setText(Double.toString(entry.getKey()));
			IObservableValue<Boolean> observeValue = Observables.observeMapEntry(attributesMap, entry.getKey());
			dbc.bindValue(WidgetProperties.selection().observe(button), observeValue);
		}
		Button button = new Button(composite, SWT.PUSH);
		button.setText("select all");
		button.addListener(SWT.Selection, even -> {
			attributesMap.forEach((k, v) -> attributesMap.put(k, true));
		});
		button = new Button(composite, SWT.PUSH);
		button.setText("deselect all");
		button.addListener(SWT.Selection, even -> {
			attributesMap.forEach((k, v) -> attributesMap.put(k, false));
		});
		MultiValidator validator = new MultiValidator() {

			@Override
			protected IStatus validate() {

				if(attributesMap.isEmpty() || attributesMap.containsValue(true)) {
					return Status.OK_STATUS;
				}
				return ValidationStatus.error("Select at least one wavelenght");
			}
		};
		dbc.addValidationStatusProvider(validator);
		GridLayoutFactory.swtDefaults().numColumns(1).generateLayout(composite);
		return parent;
	}

	@Override
	protected void performDefaults() {

		Map<Double, Boolean> map = chromatogramWSDOverviewUI.getChromatogramWSD().getSelectedWaveLengths();
		attributesMap.putAll(map);
		super.performDefaults();
	}

	@Override
	public boolean performOk() {

		Map<Double, Boolean> map = chromatogramWSDOverviewUI.getChromatogramWSD().getSelectedWaveLengths();
		map.putAll(attributesMap);
		return super.performOk();
	}
}
