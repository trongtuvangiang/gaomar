package jp.gaomar.keihan;

public class BusStation {
	private String stationId;			// �o�X��ID
	private String stationName;		// �o�X�▼
	
	/**
	 * �R���X�g���N�^
	 * @param stationId
	 * @param stationName
	 */
	public BusStation(String stationId, String stationName) {
		this.stationId = stationId;
		this.stationName = stationName;
	}


	public String getStationId() {
		return stationId;
	}


	public void setStationId(String stationId) {
		this.stationId = stationId;
	}


	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
	
}
