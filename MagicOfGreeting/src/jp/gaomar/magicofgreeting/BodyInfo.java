package jp.gaomar.magicofgreeting;

import org.anddev.andengine.entity.shape.Shape;


public class BodyInfo {
	private String name;
	private int id;
    private Shape shape;
    private boolean aliveFlag;
    private boolean jointFlag;

    public BodyInfo(String name, int id, Shape shape) {
    	this.name = name;
    	this.id = id;
        this.shape = shape;
        this.aliveFlag = true;
        this.jointFlag = false;
    }

	public String getName() {
		return name;
	}
	public boolean getAliveFlag() {
        return this.aliveFlag;
    }
    public void setAliveFlag( boolean flag ) {
        this.aliveFlag = flag;
    }
    public Shape getShape() {
        return this.shape;
    }
	public boolean getJointFlag() {
        return this.jointFlag;
    }
    public void setJointFlag( boolean flag ) {
        this.jointFlag = flag;
    }
	public int getId() {
		return id;
	}


}
