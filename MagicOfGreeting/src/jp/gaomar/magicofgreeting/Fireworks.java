package jp.gaomar.magicofgreeting;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.particle.Particle;
import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.anddev.andengine.entity.particle.initializer.ColorInitializer;
import org.anddev.andengine.entity.particle.initializer.IParticleInitializer;
import org.anddev.andengine.entity.particle.modifier.AlphaModifier;
import org.anddev.andengine.entity.particle.modifier.ExpireModifier;
import org.anddev.andengine.entity.particle.modifier.ScaleModifier;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class Fireworks extends ParticleSystem{

    private double mInitVel;
    private float mDecelPercent;
    private double vel;
    private Random mGenerator;
    private float r, g, b;
    
	public Fireworks(CircleOutlineParticleEmitter particleEmitter,  TextureRegion textureRegion, int iNumParticles, int iCameraWidth, int iCameraHeight) {
		super(particleEmitter,(float)iNumParticles*8, (float)iNumParticles*8, iNumParticles, textureRegion);
		
        mDecelPercent = 75/100.0f;
        mInitVel = 180.0;
        mGenerator = new Random();
        vel = mInitVel;

        this.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
        setFireWorkColor((int)(Math.random()* 254)+1,(int)(Math.random()* 254)+1, (int)(Math.random()* 254)+1);
        this.addParticleInitializer(new ColorInitializer(r, g, b));
        this.addParticleModifier(new ScaleModifier(2.0f, 0.5f, 0.0f, 1.0f));
        this.addParticleModifier(new ExpireModifier(4.0f));
        this.addParticleModifier(new AlphaModifier(1.0f, 0.0f, 0.0f, 1.5f));
        
        this.addParticleInitializer(new IParticleInitializer()
        {
        	@Override
    	    public void onInitializeParticle(Particle pParticle) {
    		int ang = mGenerator.nextInt(359);
    		float fVelocityX = (float) (Math.cos(Math.toRadians(ang)) * vel);
    		float fVelocityY = (float) (Math.sin(Math.toRadians(ang)) * vel);
    		pParticle.getPhysicsHandler().setVelocity(fVelocityX, fVelocityY);
    		// calculate air resistance that acts opposite to particle
    		// velocity
    		float fVelXopposite = TogglePosNeg(fVelocityX);
    		float fVelYopposite = TogglePosNeg(fVelocityY);
    		// x% of deceleration is applied (that is opposite to velocity)
    		pParticle.getPhysicsHandler().setAcceleration(fVelXopposite * mDecelPercent, fVelYopposite * mDecelPercent);
    	    }
        	//private float TogglePosNeg(float fInputNumber)
        	// what it does: makes a +ve number -ve, and -ve number +ve
        	private float TogglePosNeg(float fInputNumber)
        	{
        		return(fInputNumber - (2*fInputNumber));
        	}
        	
        });

	}

	  protected void setFireWorkColor(float paramFloat1, float paramFloat2, float paramFloat3)
	  {
	    float f1 = paramFloat1 / 255.0F;
	    this.r = f1;
	    float f2 = paramFloat2 / 255.0F;
	    this.g = f2;
	    float f3 = paramFloat3 / 255.0F;
	    this.b = f3;
	  }

}
