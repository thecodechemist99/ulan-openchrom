/*******************************************************************************
 * Copyright (c) 2015 Jan Holy, Dr. Philip Wenig.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.parts;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.model.ULanConnection;
import org.chromulan.system.control.model.chromatogram.ChromatogramRecordingCSD;
import org.chromulan.system.control.model.chromatogram.IChromatogramRecordingCSD;
import org.chromulan.system.control.ui.events.IAnalysisUIEvents;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.IULanCommunication.IFilt;
import net.sourceforge.ulan.base.IULanDevice;
import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.ULanDevice;
import net.sourceforge.ulan.base.ULanHandle;
import net.sourceforge.ulan.base.ULanMsg;

public class DeviceSettingsPart {

	private IULanDevice device;
	@Inject
	private IEventBroker eventBroker;
	private IFilt filtGetData;
	private IFilt filtStartRecording;
	private boolean getData;
	private IChromatogramRecordingCSD chromatogramRecording;
	@Inject
	private Composite parent;
	@Inject
	MPart part;
	private boolean startRecording;
	private boolean stopRecording;

	public DeviceSettingsPart() {

		chromatogramRecording = new ChromatogramRecordingCSD();
		IChromatogramCSD chromatogramCSD = new ChromatogramCSD();
		chromatogramCSD.setScanInterval(100);
		chromatogramCSD.setScanDelay(100);
		chromatogramRecording.setChromatogram(chromatogramCSD);
		startRecording = false;
		stopRecording = false;
		getData = true;
	}

	@PostConstruct
	public void createPartControl() {

		parent.setLayout(new FillLayout());
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add Chromatogtam");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				eventBroker.post(IAnalysisUIEvents.TOPIC_ANALYSIS_CHROMULAN_UI_CHROMATOGRAM_DISPLAY, chromatogramRecording);
			}
		});
		loadDevice();
	}

	private void loadDevice() {

		DeviceDescription description = (DeviceDescription)part.getObject();
		device = new ULanDevice(description);
		filtGetData = device.addFiltAdr(0x4f, null, new CompletionHandler<ULanMsg, Void>() {

			@Override
			public void completed(ULanMsg arg0, Void arg1) {

				ByteBuffer buffer = arg0.getMsg();
				if(getData && !stopRecording) {
					while(buffer.hasRemaining()) {
						chromatogramRecording.addScanAutoSet(new ScanCSD(buffer.getFloat()));
					}
				}
			}

			@Override
			public void failed(Exception arg0, Void arg1) {

			}
		});
		filtStartRecording = device.addFiltAdr(ULanHandle.CMD_LCDMRK, null, new CompletionHandler<ULanMsg, Void>() {

			@Override
			public void completed(ULanMsg arg0, Void arg1) {

				startRecording();
			}

			@Override
			public void failed(Exception arg0, Void arg1) {

			}
		});
		if(ULanCommunicationInterface.isOpen()) {
			try {
				filtGetData.activateFilt();
				filtStartRecording.activateFilt();
			} catch(IOException e) {
				// TODO: excetption logger.warn(e);
			}
		}
	}

	public void openConnection(@UIEventTopic(value = IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_OPEN) ULanConnection connection) {

		if(ULanCommunicationInterface.isOpen()) {
			try {
				filtGetData.activateFilt();
				filtStartRecording.activateFilt();
			} catch(IOException e) {
				// TODO: excetption logger.warn(e);
			}
		}
	}

	private void resetRecording() {

		startRecording = false;
		stopRecording = false;
		chromatogramRecording.resetRecording();
	}

	public void start(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START) IAnalysis analisis) {

		resetRecording();
	}

	private void startRecording() {

		if(!startRecording && getData) {
			startRecording = true;
			chromatogramRecording.resetRecording();
		}
	}

	public void startRecording(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START_RECORDING) IAnalysis analisis) {

		startRecording();
	}

	private void stopRecording() {

		stopRecording = true;
	}

	public void stopRecording(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START_RECORDING) IAnalysis analysis) {

		stopRecording();
	}
}
