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
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.model.ULanConnection;
import org.chromulan.system.control.ui.analysis.support.MillisecondsToMinutes;
import org.chromulan.system.control.ui.analysis.support.MinutesToMilliseconds;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import net.sourceforge.ulan.base.IULanCommunication.IFilt;

public class AnalysisPart {

	private class ActualyationTimeRecording implements Runnable {

		@Override
		public void run() {

			if(analysis.getStartDate() != null) {
				labelTimeRecording.setText(Long.toString((System.currentTimeMillis() - analysis.getStartDate().getTime()) / (1000)));
			}
			display.timerExec(1000, this);
		}
	}

	private class AutoStop implements Runnable {

		@Override
		public void run() {

			eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_STOP_RECORDING, analysis);
		}
	}

	private IAnalysis analysis;
	private AutoStop autoStop;
	private Button buttonAutoContinue;
	private Button buttonAutoStop;
	private Button buttonEnd;
	private Button buttonSave;
	private Button buttonStart;
	private Button buttonStop;
	private DataBindingContext dbc;
	@Inject
	private Display display;
	@Inject
	private IEventBroker eventBroker;
	// private IULanCommunication commucation;
	private IFilt filtStartRecording;
	private Label labelInterval;
	private Label labelTimeRecording;
	private Label lableNameAnalysis;
	@Inject
	private Composite parent;
	@Inject
	protected MPerspective perspective;
	private boolean stopRecording;
	private ActualyationTimeRecording timeRecording;

	public AnalysisPart() {

		analysis = null;
		dbc = new DataBindingContext();
		timeRecording = new ActualyationTimeRecording();
		stopRecording = true;
		autoStop = new AutoStop();
		// commucation = new ULanCommunicationInterface();
	}

	@Inject
	@Optional
	public void activateFiltStartRecording(@UIEventTopic(value = IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_OPEN) ULanConnection connection) {

		/*
		 * try {
		 * filtStartRecording.activateFilt();
		 * } catch(IOException e) {
		 * // TODO: exception logger.warn(e);
		 * }
		 */
	}

	@PostConstruct
	public void createPartControl() {

		Composite composite = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout(2, false);
		composite.setLayout(gridLayout);
		lableNameAnalysis = new Label(composite, SWT.LEFT);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 2;
		lableNameAnalysis.setLayoutData(gridData);
		buttonStart = new Button(composite, SWT.PUSH);
		buttonStart.setText("Start recording");
		buttonStart.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START_RECORDING, analysis);
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonStart.setLayoutData(gridData);
		buttonStop = new Button(composite, SWT.PUSH);
		buttonStop.setText("Stop Recording");
		buttonStop.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_STOP_RECORDING, analysis);
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonStop.setLayoutData(gridData);
		buttonSave = new Button(composite, SWT.PUSH);
		buttonSave.setText("Save");
		buttonSave.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				exportChromatogram();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonSave.setLayoutData(gridData);
		buttonEnd = new Button(composite, SWT.PUSH);
		buttonEnd.setText("Next Analysis");
		buttonEnd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				endAnalysis();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonEnd.setLayoutData(gridData);
		buttonAutoContinue = new Button(composite, SWT.CHECK);
		buttonAutoContinue.setText("Auto continue");
		buttonAutoContinue.setEnabled(false);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonAutoContinue.setLayoutData(gridData);
		buttonAutoStop = new Button(composite, SWT.CHECK);
		buttonAutoStop.setText("Auto stop");
		buttonAutoStop.setEnabled(false);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonAutoStop.setLayoutData(gridData);
		labelInterval = new Label(composite, SWT.LEFT);
		labelInterval.setText("");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		labelInterval.setLayoutData(gridData);
		labelTimeRecording = new Label(composite, SWT.RIGHT);
		labelTimeRecording.setText("0");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		labelTimeRecording.setLayoutData(gridData);
		initializationButtons();
		/*
		 * filtStartRecording = commucation.addFilt(ULanDrv.CMD_LCDMRK, null, new CompletionHandler<ULanMsg, Void>() {
		 * @Override
		 * public void completed(ULanMsg arg0, Void arg1) {
		 * if(analysis != null) {
		 * eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START_RECORDING, analysis);
		 * }
		 * }
		 * @Override
		 * public void failed(Exception arg0, Void arg1) {
		 * }
		 * });
		 */
	}

	private void dataBinding() {

		dbc.dispose();
		dbc.bindValue(WidgetProperties.text().observe(lableNameAnalysis), BeanProperties.value(IAnalysis.PROPERTY_NAME).observe(analysis));
		dbc.bindValue(WidgetProperties.text().observe(labelInterval), BeanProperties.value(IAnalysis.PROPERTY_INTERVAL).observe(analysis), new UpdateValueStrategy().setConverter(new MinutesToMilliseconds()), new UpdateValueStrategy().setConverter(new MillisecondsToMinutes()));
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoStop), BeanProperties.value(IAnalysis.PROPERTY_AUTO_STOP).observe(analysis));
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoContinue), BeanProperties.value(IAnalysis.PROPERTY_AUTO_CONTINUE).observe(analysis));
	}

	protected void endAnalysis() {

		if((analysis != null) && (!analysis.isBeingRecorded())) {
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(false);
			buttonEnd.setEnabled(false);
			buttonSave.setEnabled(false);
			perspective.getContext().remove(IAnalysis.class);
			eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END, analysis);
		}
	}

	protected void exportChromatogram() {

	}

	private void initializationButtons() {

		buttonStart.setEnabled(false);
		buttonStop.setEnabled(false);
		buttonEnd.setEnabled(false);
		buttonSave.setEnabled(false);
	}

	@PreDestroy
	void preDestroy() {

		filtStartRecording.deactivateFilt();
		display.timerExec(-1, timeRecording);
		display.timerExec(-1, autoStop);
	}

	@Inject
	@Optional
	public void setAnalysis(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_SET) IAnalysis analysis) {

		if(this.analysis != null && this.analysis.getAutoContinue()) {
			exportChromatogram();
		}
		if((analysis != null) && this.analysis != analysis) {
			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			buttonEnd.setEnabled(true);
			buttonSave.setEnabled(false);
			labelTimeRecording.setText("0");
			perspective.getContext().set(IAnalysis.class, analysis);
			this.analysis = analysis;
			dataBinding();
			eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START, analysis);
		} else {
			if(analysis == null) {
				dbc.dispose();
				lableNameAnalysis.setText("");
				labelTimeRecording.setText("0");
				// TODO: hide interval labelInterval.setText("");
				// TODO: hide time recording labelTimeRecording.setText("");
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(false);
				buttonSave.setEnabled(false);
				perspective.getContext().remove(IAnalysis.class);
				buttonAutoContinue.setSelection(false);
				buttonAutoStop.setSelection(false);
			}
		}
	}

	@Inject
	@Optional
	public void startRecording(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START_RECORDING) IAnalysis analysis) {

		if((analysis != null) && (analysis == this.analysis)) {
			analysis.startRecording();
			stopRecording = false;
			display.timerExec(1000, timeRecording);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			buttonEnd.setEnabled(false);
			buttonSave.setEnabled(false);
			if(analysis.getAutoStop()) {
				display.timerExec((int)analysis.getInterval(), autoStop);
			}
		}
	}

	@Inject
	@Optional
	public void stopRecording(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_STOP_RECORDING) IAnalysis analysis) {

		if(!stopRecording && (analysis != null) && (analysis == this.analysis)) {
			stopRecording = true;
			analysis.stopRecording();
			display.timerExec(-1, timeRecording);
			display.timerExec(-1, autoStop);
			if(analysis.getAutoContinue()) {
				endAnalysis();
			} else {
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(true);
				buttonSave.setEnabled(true);
			}
		}
	}
}
