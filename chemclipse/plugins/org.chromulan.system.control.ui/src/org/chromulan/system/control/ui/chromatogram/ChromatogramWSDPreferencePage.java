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
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ChromatogramWSDPreferencePage extends PreferencePage {

	private IObservableMap<Double, Boolean> attributesMap;
	private ChromatogramWSDOverviewUI chromatogramWSDOverviewUI;

	public ChromatogramWSDPreferencePage(ChromatogramWSDOverviewUI chromatogramWSDOverviewUI) {
		super("Select Wave lenght");
		this.chromatogramWSDOverviewUI = chromatogramWSDOverviewUI;
		this.attributesMap = new WritableMap<>();
	}

	@Override
	protected Control createContents(Composite parent) {

		DataBindingContext dbc = new DataBindingContext();
		Composite composite = new Composite(parent, SWT.NONE);
		Map<Double, Boolean> map = chromatogramWSDOverviewUI.getChromatogramWSD().getSelectedWaveLengths();
		attributesMap.clear();
		attributesMap.putAll(map);
		Iterator<Map.Entry<Double, Boolean>> iterator = attributesMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Button button = new Button(composite, SWT.CHECK);
			Entry<Double, Boolean> entry = iterator.next();
			button.setText(Double.toString(entry.getKey()));
			IObservableValue<Boolean> observeValue = Observables.observeMapEntry(attributesMap, entry.getKey());
			dbc.bindValue(WidgetProperties.selection().observe(button), observeValue);
		}
		GridLayoutFactory.swtDefaults().numColumns(1).generateLayout(composite);
		return parent;
	}

	@Override
	public boolean performOk() {

		if(!attributesMap.containsValue(true)) {
			return false;
		}
		Iterator<Entry<Double, Boolean>> iterator = attributesMap.entrySet().iterator();
		Map<Double, Boolean> map = chromatogramWSDOverviewUI.getChromatogramWSD().getSelectedWaveLengths();
		while(iterator.hasNext()) {
			Entry<Double, Boolean> entry = iterator.next();
			map.replace(entry.getKey(), entry.getValue());
		}
		return super.performOk();
	}
}
