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





import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.model.Analysis;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.ui.analysis.support.MillisecondsToMinutes;
import org.chromulan.system.control.ui.analysis.support.MinutesToMilliseconds;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
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

public class AnalysisPart {

	@Inject
	private Composite parent;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private Display display;
	
	@Inject
	protected MPerspective perspective;
	
	
	private DataBindingContext dbc;
	
	private IAnalysis analysis;
	
	private Label lableNameAnalysis;
	private Label labelInterval;
	private Label labelTimeRecording;

	
	private Button buttonStart;
	private Button buttonStop;
	private Button buttonEnd;
	private Button buttonSave;
	
	private Button buttonAutoSave;
	private Button buttonAutoContinue;
		
	private Timer timer;
	private ActualyationTimeRecording timeRecording;
	private boolean stopRecording;
	private AutoStop autoStop;

	
	public AnalysisPart() {
		analysis = null;
		dbc = new DataBindingContext();
		timer = new Timer();
		timeRecording = new ActualyationTimeRecording();
		stopRecording = true;
		autoStop = new AutoStop();
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
		buttonEnd = new Button(composite, SWT.PUSH);
		buttonEnd.setText("End Analysis");
		buttonEnd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				endAnalysis();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonEnd.setLayoutData(gridData);
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
		
		
		
		buttonAutoContinue = new Button(composite, SWT.CHECK);
		buttonAutoContinue.setText("Auto continue");
		buttonAutoContinue.setEnabled(false);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonAutoContinue.setLayoutData(gridData);
		
		
		
		buttonAutoSave = new Button(composite, SWT.CHECK);
		buttonAutoSave.setText("Auto continue");
		buttonAutoSave.setEnabled(false);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonAutoSave.setLayoutData(gridData);
		
		
		labelInterval = new Label(composite, SWT.LEFT);
		labelInterval.setText("");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		labelInterval.setLayoutData(gridData);
		
		labelTimeRecording = new Label(composite, SWT.RIGHT);
		labelTimeRecording.setText("0");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		labelTimeRecording.setLayoutData(gridData);
		
		initializationButtons();
	}

	

	private void initializationButtons() {
		buttonStart.setEnabled(false);
		buttonStop.setEnabled(false);
		buttonEnd.setEnabled(false);
		buttonSave.setEnabled(false);
	}



	@Inject
	@Optional
	public void setAnalysis(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_SET) IAnalysis analysis) {
		
		if(this.analysis != null && this.analysis.getAutoContinue())
		{
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
			if(analysis == null)
			{
				dbc.dispose();
				
				lableNameAnalysis.setText("");
				labelTimeRecording.setText("0");
				//TODO: hide interval labelInterval.setText("");
				//TODO: hide time recording labelTimeRecording.setText("");
				
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(false);
				buttonSave.setEnabled(false);
				perspective.getContext().remove(IAnalysis.class);
				buttonAutoContinue.setSelection(false);
				buttonAutoSave.setSelection(false);
			}
		}
	}
	
	private void dataBinding()
	{
		dbc.dispose();
		dbc.bindValue(WidgetProperties.text().observe(lableNameAnalysis),BeanProperties.value(IAnalysis.PROPERTY_NAME).observe(analysis));
		dbc.bindValue(WidgetProperties.text().observe(labelInterval),BeanProperties.value(IAnalysis.PROPERTY_INTERVAL).observe(analysis),new UpdateValueStrategy().setConverter(new MinutesToMilliseconds()),new UpdateValueStrategy().setConverter(new MillisecondsToMinutes()));
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoSave), BeanProperties.value(IAnalysis.PROPERTY_AUTO_STOP).observe(analysis));
		dbc.bindValue(WidgetProperties.selection().observe(buttonAutoContinue), BeanProperties.value(IAnalysis.PROPERTY_AUTO_CONTINUE).observe(analysis));
		
	
	}
	
	@Inject
	@Optional
	public void startRecording(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START_RECORDING) IAnalysis analysis) {
		if((analysis != null) && (analysis==this.analysis)) {
			analysis.startRecording();
			stopRecording = false;
			display.timerExec(1000, timeRecording);			
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			buttonEnd.setEnabled(false);
			buttonSave.setEnabled(false);
			
			if(analysis.getAutoStop())
			{
				display.timerExec((int)analysis.getInterval(), autoStop);
			}
		}
	}

	@Inject
	@Optional
	public void stopRecording(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_STOP_RECORDING) IAnalysis analysis) {

		if(!stopRecording && (analysis != null) && (analysis==this.analysis)) {
			stopRecording = true;
			analysis.stopRecording();
			display.timerExec(-1, timeRecording);
			display.timerExec(-1, autoStop);
			timer.purge();
			if(analysis.getAutoContinue())
			{
				endAnalysis();
			}
			else
			{
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(true);
				buttonSave.setEnabled(true);
			}
		}
	}

	protected void exportChromatogram() {

	}

	protected void endAnalysis() {

		if((analysis != null) && (!analysis.isRecording())) {
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(false);
				buttonSave.setEnabled(false);
				perspective.getContext().remove(IAnalysis.class);
				eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END, analysis);
			}	
	}
	
	private class ActualyationTimeRecording implements Runnable
	{
		@Override
		public void run() {
			
				if(analysis.getStartDate() != null)
				{
					labelTimeRecording.setText(Long.toString((System.currentTimeMillis()-analysis.getStartDate().getTime())/(1000)));
				}
			display.timerExec(1000, this);
		}
		
			
		
	}
	
	private class AutoStop implements Runnable
	{
		@Override
		public void run() {
			eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_STOP_RECORDING, analysis);
		}
	}

}
