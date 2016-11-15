package org.chromulan.system.control.device.setting;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

public abstract class AbstractDeviceSetting implements IDeviceSetting {

	private String deviceID;
	private String deviceType;
	private String name;
	private String pluginID;
	private HashMap<String, IValue<?>> values;

	public AbstractDeviceSetting() {
		this.name = "";
		this.values = new HashMap<>();
	}

	@Override
	public String getDeviceID() {

		return deviceID;
	}

	@Override
	public String getDeviceType() {

		return deviceType;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String getPluginID() {

		return pluginID;
	}

	@Override
	public HashMap<String, IValue<?>> getValues() {

		return values;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		this.name = (String)in.readObject();
		this.deviceType = (String)in.readObject();
		this.pluginID = (String)in.readObject();
		this.values = (HashMap<String, IValue<?>>)in.readObject();
	}

	@Override
	public void setDeviceID(String id) {

		this.deviceID = id;
	}

	@Override
	public void setDeviceType(String type) {

		this.deviceType = type;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public void setPlugnID(String id) {

		this.pluginID = id;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeObject(name);
		out.writeObject(deviceType);
		out.writeObject(pluginID);
		out.writeObject(values);
	}
}
