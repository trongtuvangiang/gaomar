package jp.gaomar.osakacitybus;

public class BusDestination {

	private String destination;
	private String url;
	private String route;
	private String html;
	private int routeNo;
	
	public BusDestination(String destination, String url, String route) {
		super();
		this.destination = destination;
		this.url = url;
		this.route = route;
	}
	
	public BusDestination(String destination, String url, String route,
			String html) {
		super();
		this.destination = destination;
		this.url = url;
		this.route = route;
		this.html = html;
	}



	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public int getRouteNo() {
		return routeNo;
	}

	public void setRouteNo(int routeNo) {
		this.routeNo = routeNo;
	}
	
	
}
