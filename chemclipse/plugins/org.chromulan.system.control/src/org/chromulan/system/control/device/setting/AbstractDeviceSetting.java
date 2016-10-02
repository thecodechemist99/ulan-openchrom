package org.chromulan.system.control.device.setting;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractDeviceSetting implements IDeviceSetting{
	private String name;
	private List<IValueChangeListener> listeners;
	private HashMap<String, IValue<?>> values;
	
	
	public AbstractDeviceSetting() {
		this.name ="";
		this.values = new HashMap<>();
		listeners = new ArrayList<>();
	}
	
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.name = (String) in.readObject();	
		this.values = (HashMap<String, IValue<?>>) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(name);
		out.writeObject(values);
	}

	@Override
	public String getName() {
		return name;
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public HashMap<String, IValue<?>> getValues() {
		return values;
	}

	
	@Override
	public void addUpdateValueListener(IValueChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void updateValueListeners() {
		for (IValueChangeListener listener : listeners) {
			listener.update();
		}	
	}
	

}
