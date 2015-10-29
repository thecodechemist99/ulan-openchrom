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
import javax.inject.Named;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.model.AnalysesCSD;
import org.chromulan.system.control.model.AnalysisCSD;
import org.chromulan.system.control.model.IAnalyses;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.model.IAnalysisCSD;
import org.chromulan.system.control.ui.analysis.support.AnalysesTable;
import org.chromulan.system.control.ui.analysis.support.AnalysisSettingsPreferencePage;
import org.chromulan.system.control.ui.wizard.WizardNewAnalysis;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class AnalysesPart {

	private IAnalyses analyses;
	private AnalysesTable analysesTable;
	@Inject
	Display diplay;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	EHandlerService handlerService;
	@Inject
	Composite parent;
	@Inject
	MPart part;
	@Inject
	private MPerspective perspective;

	public AnalysesPart() {

		analyses = new AnalysesCSD();
	}

	private void addAnalysis(IAnalysis analysis) {

		if(analyses.getActualAnalysis() == null) {
			analyses.addAnalysis(analysis);
			selectAnalysis();
		} else {
			analyses.addAnalysis(analysis);
		}
		redrawTable();
	}

	private void createAnalysis() {

		WizardNewAnalysis newAnalysisWizard = new WizardNewAnalysis();
		WizardDialog wizardDialog = new WizardDialog(parent.getShell(), newAnalysisWizard);
		if(wizardDialog.open() == Window.OK) {
			int numberAnalysis = (Integer)newAnalysisWizard.getModel().numberAnalyses.getValue();
			for(int i = 1; i <= numberAnalysis; i++) {
				IAnalysisCSD analysis = new AnalysisCSD();
				String name = getNameAnalysis((String)newAnalysisWizard.getModel().name.getValue(), i, numberAnalysis);
				analysis.setName(name);
				analysis.setAutoContinue((Boolean)newAnalysisWizard.getModel().autoContinue.getValue());
				analysis.setAutoStop((Boolean)newAnalysisWizard.getModel().autoStop.getValue());
				analysis.setInterval((Long)newAnalysisWizard.getModel().interval.getValue());
				addAnalysis(analysis);
			}
		}
	}

	@PostConstruct
	void createComposite() {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		GridData gridData;
		parent.setLayout(gridLayout);
		final Composite tableComposite = new Composite(parent, SWT.None);
		analysesTable = new AnalysesTable(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER, analyses);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 35;
		tableComposite.setLayout(new FillLayout());
		tableComposite.setLayoutData(gridData);
		final Button buttonAddAnalysis = new Button(parent, SWT.PUSH);
		buttonAddAnalysis.setText("Add");
		buttonAddAnalysis.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				createAnalysis();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonAddAnalysis.setLayoutData(gridData);
		final Button buttonRemoveAnalysis = new Button(parent, SWT.PUSH);
		buttonRemoveAnalysis.setText("Remove");
		buttonRemoveAnalysis.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int i = analysesTable.getViewer().getTable().getSelectionIndex();
				if(i != -1) {
					removeAnalysis(i);
				}
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonRemoveAnalysis.setLayoutData(gridData);
		final Button buttonActualAnalysis = new Button(parent, SWT.PUSH);
		buttonActualAnalysis.setText("Select Actual");
		buttonActualAnalysis.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				analysesTable.selectActualAnalysis();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonActualAnalysis.setLayoutData(gridData);
		analysesTable.getViewer().addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				selectAnalysis();
			}
		});
		initializationHandler();
	}

	@Inject
	@Optional
	public void endAnalysis(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END) IAnalysis analysis) {

		if(analyses.isActualAnalysis(analysis)) {
			setAnalysis(analyses.setNextAnalysisActual());
			redrawTable();
		}
	}

	private String getNameAnalysis(String name, int numberOfAnalysis, int maxNumber) {

		if(name != null && !name.isEmpty() && maxNumber > 0) {
			if(maxNumber == 1) {
				return name;
			} else {
				int maxNumDigits = (int)(Math.log10(maxNumber) + 1);
				int actulNumDigits = (int)(Math.log10(numberOfAnalysis) + 1);
				StringBuilder builder = new StringBuilder(name);
				for(int i = 0; i < maxNumDigits - actulNumDigits; i++) {
					builder.append('0');
				}
				builder.append(numberOfAnalysis);
				return builder.toString();
			}
		} else {
			return name;
		}
	}

	@SuppressWarnings("restriction")
	private void initializationHandler() {

		handlerService.activateHandler(AnalysesSearchToolItem.ID_COMMAND_SEARCH, new Object() {

			@Execute
			public void execute(@Named(AnalysesSearchToolItem.ID_PARAMETER_NAME) final String nameSearch) {

				if(nameSearch == null || nameSearch.isEmpty()) {
					analysesTable.removeFilterName();
				} else {
					analysesTable.addFilterName(nameSearch);
				}
			}
		});
	}

	private void redrawTable() {

		analysesTable.getViewer().refresh();
	}

	private void removeAnalysis(int number) {

		IAnalysis analysis = analyses.getAnalysis(number);
		if(analyses.isActualAnalysis(analysis)) {
			if(!analysis.isBeingRecorded()) {
				setAnalysis(analyses.setNextAnalysisActual());
				analyses.removeAnalysis(number);
			} else {
				// TODO: alert
			}
		} else {
			analyses.removeAnalysis(number);
		}
		redrawTable();
	}

	private void selectAnalysis() {

		int index = analysesTable.getViewer().getTable().getSelectionIndex();
		if(index != -1) {
			if(index < analyses.getNumberAnalysis()) {
				PreferenceManager manager = new PreferenceManager();
				AnalysisSettingsPreferencePage settings = new AnalysisSettingsPreferencePage(analyses.getAnalysis(index), true);
				PreferenceNode nodeBase = new PreferenceNode("base", settings);
				manager.addToRoot(nodeBase);
				PreferenceDialog dialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), manager);
				if(Window.OK == dialog.open()) {
					redrawTable();
				}
			}
		}
	}

	private void setAnalysis(IAnalysis analysis) {

		perspective.getContext().remove(IAnalysis.class);
		eventBroker.send(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_SET, analysis);
		if(analysis != null) {
			perspective.getContext().set(IAnalysis.class, analysis);
		}
	}
}
