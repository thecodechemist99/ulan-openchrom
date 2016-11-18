package org.chromulan.system.control.model;

public class AcquisitionWSD extends AbstractAcquisition implements IAcquisitionWSD {

	public AcquisitionWSD() {
		super();
	}

	@Override
	public IAcquisitionWSDSaver getAcquisitionWSDSaver() {

		IAcquisitionSaver acquisitionSaver = getAcquisitionSaver();
		if(acquisitionSaver instanceof IAcquisitionWSDSaver) {
			return (IAcquisitionWSDSaver)acquisitionSaver;
		}
		return null;
	}
}
