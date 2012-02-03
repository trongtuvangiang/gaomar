package jp.gaomar.astronumbertouch;

public class Position {
	private int x;
	private int y;
	private int no;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
		this.no = -1;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}
	
	
}
