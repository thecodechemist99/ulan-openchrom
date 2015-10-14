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
package org.chromulan.system.control.ui.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.model.Analyses;
import org.chromulan.system.control.model.Analysis;
import org.chromulan.system.control.model.IAnalyses;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.ui.wizard.WizardNewAnalysis;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class AnalysesPart {

	private IAnalyses analyses;
	private Table tableAnalyses;
	private TableColumn columnName;
	private TableColumn columnState;
	private TableColumn columnAutoContinue;
	private TableColumn columnAutoStop;
	private TableColumn columnInterval;
	private Button buttonAddAnalysis;
	private Button buttonRemoveAnalysis;
	@Inject
	Composite parent;
	@Inject
	MPart part;
	@Inject
	Display diplay;
	@Inject
	private IEventBroker eventBroker;

	public AnalysesPart() {

		analyses = new Analyses();
	}

	@PostConstruct
	void createComposite() {

		Composite composite = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout(2, false);
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		tableAnalyses = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		tableAnalyses.setLayoutData(gridData);
		tableAnalyses.setHeaderVisible(true);
		columnName = new TableColumn(tableAnalyses, SWT.None);
		columnName.setText("Name");
		columnName.setWidth(150);
		columnState = new TableColumn(tableAnalyses, SWT.None);
		columnState.setText("State");
		columnState.setWidth(70);
		columnAutoStop = new TableColumn(tableAnalyses, SWT.None);
		columnAutoStop.setText("Auto Stop");
		columnAutoStop.setWidth(80);
		columnInterval = new TableColumn(tableAnalyses, SWT.None);
		columnInterval.setText("Interval");
		columnInterval.setWidth(80);
		columnAutoContinue = new TableColumn(tableAnalyses, SWT.None);
		columnAutoContinue.setText("Auto continue");
		columnAutoContinue.setWidth(100);
		buttonAddAnalysis = new Button(composite, SWT.PUSH);
		buttonAddAnalysis.setText("Add analysis");
		buttonAddAnalysis.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				createAnalysis();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonAddAnalysis.setLayoutData(gridData);
		buttonRemoveAnalysis = new Button(composite, SWT.PUSH);
		buttonRemoveAnalysis.setText("Remove analysis");
		buttonRemoveAnalysis.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int i = tableAnalyses.getSelectionIndex();
				if(i != -1) {
					removeAnalysis(i);
				}
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonRemoveAnalysis.setLayoutData(gridData);
	}

	private void createAnalysis() {

		WizardNewAnalysis newAnalysisWizard = new WizardNewAnalysis();
		WizardDialog wizardDialog = new WizardDialog(parent.getShell(), newAnalysisWizard);
		if(wizardDialog.open() == Window.OK) {
			IAnalysis analysis = new Analysis();
			analysis.setName((String)newAnalysisWizard.getModel().name.getValue());
			analysis.setAutoContinue((Boolean)newAnalysisWizard.getModel().autoContinue.getValue());
			analysis.setAutoStop((Boolean)newAnalysisWizard.getModel().autoStop.getValue());
			analysis.setInterval((Long)newAnalysisWizard.getModel().interval.getValue());
			// TODO: add directory analysis.setDirectory((File)newAnalysisWizard.getModel().folder.getValue());
			addAnalysis(analysis);
		}
	}

	private void addAnalysis(IAnalysis analysis) {

		if(analyses.getActualAnalysis() == null) {
			analyses.addAnalysis(analysis);
			eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_SET, analysis);
		} else {
			analyses.addAnalysis(analysis);
		}
		redrawTable();
	}

	private void removeAnalysis(int number) {

		IAnalysis analysis = analyses.getAnalysis(number);
		if(analyses.isActualAnalysis(analysis)) {
			if(!analysis.isRecording()) {
				eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_SET, analyses.setNextAnalysisActual());
				analyses.removeAnalysis(number);
			} else {
				// TODO: alert
			}
		} else {
			analyses.removeAnalysis(number);
		}
		redrawTable();
	}

	@Inject
	@Optional
	public void endAnalysis(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END) IAnalysis analysis) {

		if(analyses.isActualAnalysis(analysis)) {
			eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_SET, analyses.setNextAnalysisActual());
			redrawTable();
		}
	}

	private void redrawTable() {

		tableAnalyses.removeAll();
		boolean finshed = true;
		for(int i = 0; i < analyses.getNumberAnalysis(); i++) {
			TableItem item = new TableItem(tableAnalyses, SWT.None);
			IAnalysis analysis = analyses.getAnalysis(i);
			item.setText(0, analysis.getName());
			if(analyses.isActualAnalysis(analysis)) {
				item.setText(1, "actual");
				finshed = false;
			} else if(finshed) {
				item.setText(1, "finished");
			} else {
				item.setText(1, "waiting");
			}
			item.setText(4, Boolean.toString(analysis.getAutoContinue()));
			item.setText(2, Boolean.toString(analysis.getAutoStop()));
			item.setText(3, Long.toString(analysis.getInterval() / (1000 * 60)));
		}
	}
}
