package org.chromulan.system.control.model;

public class AcquisitionMSD extends AbstractAcquisition implements IAcquisitionMSD {

	public AcquisitionMSD() {
		super();
	}

	@Override
	public IAcquisitionMSDSaver getAcquisitionMSDSaver() {

		IAcquisitionSaver acquisitionSaver = getAcquisitionSaver();
		if(acquisitionSaver instanceof IAcquisitionMSDSaver) {
			return (IAcquisitionMSDSaver)acquisitionSaver;
		}
		return null;
	}
}
