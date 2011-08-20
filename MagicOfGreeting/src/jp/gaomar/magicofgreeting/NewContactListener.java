package jp.gaomar.magicofgreeting;

import android.app.Activity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class NewContactListener implements ContactListener{

	private Activity uiActivity;

	public NewContactListener(Activity activity) {
		super();
		this.uiActivity = activity;

	}

	@Override
	public void beginContact(Contact contact) {
        String objAName = "";
        String objBName = "";

        try {
			// �����Ԃ������̂�
			Fixture fixtureA = contact.getFixtureA();
			Fixture fixtureB = contact.getFixtureB();

			Body bodyA = fixtureA.getBody();
			Object objA = bodyA.getUserData();
			if ( ((BodyInfo)objA) instanceof BodyInfo ) {
			    BodyInfo buf = (BodyInfo)objA;
			    objAName = buf.getName();
			}

			Body bodyB = fixtureB.getBody();
			Object objB = bodyB.getUserData();
			if ( ((BodyInfo)objB) instanceof BodyInfo ) {
			    BodyInfo buf = (BodyInfo)objB;
			    objBName = buf.getName();
			}

			// �n�ʁ@�Ɓ@�^�}�S�@���ڐG�����Ƃ��̂ݍ폜�ΏۂƂ���
			if ( (uiActivity.getString(R.string.pg_char).equals(objAName) |
					uiActivity.getString(R.string.pg_char).equals(objBName)) ) {

			    Vector2 contactPos    = contact.getWorldManifold().getPoints()[0];

			    Vector2 va = contact.getFixtureA().getBody().getLinearVelocityFromWorldPoint(contactPos);
			    va.sub(contact.getFixtureB().getBody().getLinearVelocityFromWorldPoint(contactPos));

			    float iForce = va.len();
			    if (iForce > 20) {
			    	if (((BodyInfo)objA).getJointFlag()) {
			    		((BodyInfo)objB).setAliveFlag( false );

			    	} else if (((BodyInfo)objB).getJointFlag()){
			    		((BodyInfo)objA).setAliveFlag( false );
			    	} else {
			    		((BodyInfo)objA).setAliveFlag( false );
			    		((BodyInfo)objB).setAliveFlag( false );
			    	}
			    }
			}
		} catch (Exception e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}

	@Override
	public void endContact(Contact contact) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

}
