/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ulad3x.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.devices.base.IUlanControlDevice;
import org.chromulan.system.control.model.ChromatogramCSDAcquisition;
import org.chromulan.system.control.model.IChromatogramAcquisition;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.IULanCommunication.IFilt;
import net.sourceforge.ulan.base.IULanDevice;
import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.ULanDevice;
import net.sourceforge.ulan.base.ULanMsg;

public class ULad3x {

	private IControlDevice controlDevice;
	public final int DEFAULT_SCAN_DELAY = 0;
	public final int DEFAULT_SCAN_INTERVAL = 100;
	private DeviceDescription description;
	private IULanDevice device;
	private IFilt<Void> filtGetData;
	private IChromatogramAcquisition chromatogramAcquisition;
	private Boolean isBeeingRecored;
	private int scanDelay;
	private int scanInterval;

	public ULad3x(IUlanControlDevice controlDevice) {
		super();
		this.controlDevice = controlDevice;
		device = new ULanDevice(controlDevice.getDeviceDescription());
		chromatogramAcquisition = new ChromatogramCSDAcquisition(DEFAULT_SCAN_INTERVAL, DEFAULT_SCAN_DELAY);
		filtGetData = device.addFiltAdr(0x4f, null, new CompletionHandler<ULanMsg, Void>() {

			@Override
			public void completed(ULanMsg arg0, Void arg1) {

				ByteBuffer buffer = arg0.getMsg();
				synchronized (isBeeingRecored) {
					if (isBeeingRecored) {
						while (buffer.hasRemaining()) {
							chromatogramAcquisition.addScanAutoSet(new ScanCSD(buffer.getFloat()));
						}
					}
				}
			}

			@Override
			public void failed(Exception arg0, Void arg1) {

			}
		});
		scanDelay = DEFAULT_SCAN_DELAY;
		scanInterval = DEFAULT_SCAN_INTERVAL;
	}

	public ULad3x(IUlanControlDevice controlDevice, int scanDelay, int scanInterval) {
		this(controlDevice);
		this.scanDelay = scanDelay;
		this.scanInterval = scanInterval;
	}

	public void connect() throws ClosedChannelException, IOException {

		if (ULanCommunicationInterface.isOpen()) {
			filtGetData.activateFilt();
		}
	}

	public void disconnect() {

		filtGetData.deactivateFilt();
	}

	@Override
	protected void finalize() throws Throwable {

		try {
			super.finalize();
		} finally {
			filtGetData.deactivateFilt();
		}
	}

	public IControlDevice getControlDevice() {

		return controlDevice;
	}

	public DeviceDescription getDeviceDescription() {

		return description;
	}

	public IChromatogramAcquisition getChromatogramRecording() {

		return chromatogramAcquisition;
	}

	public int getScanDelay() {

		return scanDelay;
	}

	public int getScanInterva() {

		return scanInterval;
	}

	public boolean isBeeingRecored() {

		return isBeeingRecored;
	}

	public boolean isConnect() {

		return filtGetData.isFiltActive();
	}

	public void newAcquisition() {

		chromatogramAcquisition.newAcquisition(DEFAULT_SCAN_INTERVAL, scanDelay);
	}

	public void setScanDelay(int milliseconds) {

		scanDelay = milliseconds;
		chromatogramAcquisition.setScanDelay(milliseconds);
	}

	public void start(boolean reset) {

		isBeeingRecored = true;
		if (reset) {
			chromatogramAcquisition.newAcquisition(DEFAULT_SCAN_INTERVAL, scanDelay);
		}
	}

	public void stop() {

		synchronized (isBeeingRecored) {
			isBeeingRecored = false;
		}
	}
}
