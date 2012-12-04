package jp.gaomar.nagoyacitybus;

import java.io.Serializable;

public class BusTimeTable implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String wHour;				// ()	•½“ú
	private String wMinute;			// (•ª)	•½“ú
	private String stHour;				// ()	“y—j“ú
	private String stMinute;			// (•ª)	“y—j“ú
	private String suHour;				// ()	“új“ú
	private String suMinute;			// (•ª)	“új“ú
	
	private String busRoutes;		// Œn“
	private String destination;		// s‚«æ
	private String note;			// ”õl
	private String html;

	/**
	 * ƒRƒ“ƒXƒgƒ‰ƒNƒ^
	 * @param html
	 */
	public BusTimeTable() {
		super();
	}
	
	public BusTimeTable(String html) {
		this.html = html;
	}

	public String getwHour() {
		return wHour;
	}

	public void setwHour(String wHour) {
		this.wHour = wHour;
	}

	public String getwMinute() {
		return wMinute;
	}

	public void setwMinute(String wMinute) {
		this.wMinute = wMinute;
	}

	public String getStHour() {
		return stHour;
	}

	public void setStHour(String stHour) {
		this.stHour = stHour;
	}

	public String getStMinute() {
		return stMinute;
	}

	public void setStMinute(String stMinute) {
		this.stMinute = stMinute;
	}

	public String getSuHour() {
		return suHour;
	}

	public void setSuHour(String suHour) {
		this.suHour = suHour;
	}

	public String getSuMinute() {
		return suMinute;
	}

	public void setSuMinute(String suMinute) {
		this.suMinute = suMinute;
	}

	public String getBusRoutes() {
		return busRoutes;
	}

	public void setBusRoutes(String busRoutes) {
		this.busRoutes = busRoutes;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	
	
}
