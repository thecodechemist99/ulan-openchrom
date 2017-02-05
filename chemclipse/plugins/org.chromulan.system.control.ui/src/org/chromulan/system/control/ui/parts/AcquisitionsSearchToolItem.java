/*******************************************************************************
 * Copyright (c) 2015, 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.parts;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class AcquisitionsSearchToolItem {

	@Inject
	private ECommandService commandService;
	@Inject
	private EHandlerService handlerService;

	@PostConstruct
	public void createPartControl(Composite parent) {

		final Composite comp = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		comp.setLayout(rowLayout);
		final Text text = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH | SWT.BORDER);
		final Button button = new Button(comp, SWT.PUSH);
		text.setMessage("Search press enter");
		text.setLayoutData(new RowData(150, SWT.DEFAULT));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String search = text.getText();
				if(search.isEmpty()) {
					Map<String, Object> parameters = new HashMap<>();
					parameters.put(AcquisitionsPart.ID_PARAMETER_SEARCH_NAME, search);
					ParameterizedCommand com = commandService.createCommand(AcquisitionsPart.ID_COMMAND_SEARCH, parameters);
					if(handlerService.canExecute(com)) {
						handlerService.executeHandler(com);
					}
					return;
				}
				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					Map<String, Object> parameters = new HashMap<>();
					parameters.put(AcquisitionsPart.ID_PARAMETER_SEARCH_NAME, search);
					ParameterizedCommand com = commandService.createCommand(AcquisitionsPart.ID_COMMAND_SEARCH, parameters);
					if(handlerService.canExecute(com)) {
						handlerService.executeHandler(com);
					}
				}
			}
		});
		button.setText("X");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				text.setText("");
				Map<String, Object> parameters = new HashMap<>();
				parameters.put(AcquisitionsPart.ID_PARAMETER_SEARCH_NAME, "");
				ParameterizedCommand com = commandService.createCommand(AcquisitionsPart.ID_COMMAND_SEARCH, parameters);
				if(handlerService.canExecute(com)) {
					handlerService.executeHandler(com);
				}
			}
		});
	}
}
