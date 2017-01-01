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
package org.chromulan.system.control.devices.base;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.model.ChromatogramCSDAcquisition;
import org.chromulan.system.control.model.IChromatogramAcquisition;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.DeviceDescription;
import net.sourceforge.ulan.base.IULanCommunication.IFilt;
import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.ULanMsg;

public abstract class DetectorULanControlDevice extends UlanControlDevice {

	private Boolean beeingRecored = new Boolean(true);
	private IFilt<Void> filtGetData;
	private IChromatogramAcquisition chromatogramAcquisition;
	private int scanDelay;
	private int scanInterval;

	public DetectorULanControlDevice() {
		super();
	}

	public DetectorULanControlDevice(DeviceDescription description, boolean isConnected, int scanInterval) {
		super(DeviceType.DETECTOR, description, isConnected);
		this.scanDelay = 0;
		this.scanInterval = scanInterval;
		setObject(scanInterval, 0);
	}

	@Override
	public void connect() throws ClosedChannelException, IOException {

		if(ULanCommunicationInterface.isOpen()) {
			filtGetData.activateFilt();
		}
	}

	@Override
	public void disConnect() {

		filtGetData.deactivateFilt();
	}

	public IChromatogramAcquisition getChromatogramRecording() {

		return chromatogramAcquisition;
	}

	public int getScanDelay() {

		return scanDelay;
	}

	public int getScanInterval() {

		return scanInterval;
	}

	public boolean isBeeingRecored() {

		return beeingRecored;
	}

	public void newAcquisition() {

		chromatogramAcquisition.newAcquisition(scanInterval, scanDelay);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		super.readExternal(in);
		scanDelay = in.readInt();
		scanInterval = in.readInt();
		setObject(scanInterval, scanDelay);
	}

	public void setBeeingRecored(boolean beeingRecored) {

		this.beeingRecored = beeingRecored;
	}

	@Override
	public void setDeviceSetting(IDeviceSetting deviceSetting) {

	}

	private void setObject(int scanInterval, int scanDelay) {

		chromatogramAcquisition = new ChromatogramCSDAcquisition(scanInterval, scanDelay);
		filtGetData = getUlanDevice().addFiltAdr(0x4f, null, new CompletionHandler<ULanMsg, Void>() {

			@Override
			public void completed(ULanMsg arg0, Void arg1) {

				ByteBuffer buffer = arg0.getMsg();
				synchronized(beeingRecored) {
					if(beeingRecored) {
						while(buffer.hasRemaining()) {
							chromatogramAcquisition.addScanAutoSet(new ScanCSD(buffer.getFloat()));
						}
					}
				}
			}

			@Override
			public void failed(Exception arg0, Void arg1) {

			}
		});
	}

	public void setScanDelay(int milliseconds) {

		scanDelay = milliseconds;
		chromatogramAcquisition.setScanDelay(milliseconds);
	}

	public void setScanInterval(int milliseconds) {

		scanInterval = milliseconds;
		chromatogramAcquisition.setScanInterval(milliseconds, true);
	}

	public void start(boolean reset) {

		synchronized(beeingRecored) {
			beeingRecored = true;
			if(reset) {
				chromatogramAcquisition.newAcquisition(scanInterval, scanDelay);
			}
		}
	}

	public void stop() {

		synchronized(beeingRecored) {
			beeingRecored = false;
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		super.writeExternal(out);
		out.writeInt(scanDelay);
		out.writeInt(scanInterval);
	}
}
