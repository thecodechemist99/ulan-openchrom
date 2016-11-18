package org.chromulan.system.control.device.setting;

import java.io.Externalizable;
import java.util.HashMap;

public interface IDeviceSetting extends Externalizable {

	String getDeviceID();

	String getDeviceType();

	String getName();

	String getPluginID();

	HashMap<String, IValue<?>> getValues();

	void setDeviceID(String id);

	void setDeviceType(String type);

	void setName(String name);

	void setPlugnID(String id);
}
