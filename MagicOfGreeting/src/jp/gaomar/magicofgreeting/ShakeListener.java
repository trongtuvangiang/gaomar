package jp.gaomar.magicofgreeting;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeListener
implements SensorEventListener {

	private SensorManager mSensorManager;
	private OnShakeListener mListener;
	private long mPreTime;
	private float mLastX;
	private float mLastY;
	private float mLastZ;
	private int mShakeCount;

	// �V�F�C�N�����m��������onShake���\�b�h���Ăяo���܂��B
	// setOnShakeListener���\�b�h�ŃZ�b�g���Ă��������B
	public interface OnShakeListener {
	    void onShake();
	}

	// OnShakeListener���Z�b�g
	public void setOnShakeListener(OnShakeListener listener) {
	    mListener = listener;
	}

	public ShakeListener(Context context) {
	    // SensorManager�̃C���X�^���X���擾
	    mSensorManager = (SensorManager)context.getSystemService(
	                                          Context.SENSOR_SERVICE);
	}

	public void onResume() {
	    // �����x�Z���T�[���擾
	    List<Sensor> list =
	        mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

	    // �擾�ł��Ȃ���Ή������Ȃ�
	    if (list.size() < 1) return;

	    // �����x�Z���T�[�Ƀ��X�i�[��o�^
	    // ��3�����Ŋ��x���w��ł��܂��B
	    // �����UI�Ɏg���z���SENSOR_DELAY_UI�ɂ��܂����B
	    mSensorManager.registerListener(this,
	                                   list.get(0),
	                                   SensorManager.SENSOR_DELAY_UI);
	}

	public void onPause() {
	    // ���X�i�[������
	    mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	// �Z���T�[�̒l���ς������Ăяo�����
	public void onSensorChanged(SensorEvent event) {
	    // �Z���T�[�̃^�C�v�������x�Z���T�[����Ȃ������牽�����Ȃ�
	    if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
	        return;
	    }

	    long curTime = System.currentTimeMillis();
	    long diffTime = curTime - mPreTime;
	    // �������p�x�ŃC�x���g����������̂�
	    // 100ms��1��v�Z����悤�ɊԈ���
	    if (diffTime > 100) {
	        // ���݂̒l���Ƃ���
	        float x = event.values[0];
	        float y = event.values[1];
	        float z = event.values[2];
	        // �O��̒l�Ƃ̍�����X�s�[�h���v�Z
	        float speed = Math.abs(x+y+z - mLastX-mLastY-mLastZ)
	                      / diffTime * 10000;
	        // �X�s�[�h��300�ȏ�Ȃ�i���D�݂ŕς��Ă��������j
	        if (speed > 300) {
	            // �V�F�C�N�J�E���g���C���N�������g
	            mShakeCount++;
	            // 4��A���X�s�[�h��300�ȏ�Ȃ�
	            // �V�F�C�N�ƔF��i���D�݂ŕς��Ă��������j
	            if (mShakeCount > 3) {
	                mShakeCount = 0;
	                // ���X�i�[���Z�b�g����Ă����
	                if (mListener != null) {
	                    // onShake���\�b�h���Ăяo��
	                    mListener.onShake();
	                }
	            }
	        } else {
	            // 300�ȉ��Ȃ烊�Z�b�g
	            mShakeCount = 0;
	        }
	        // �O��l�Ƃ��ĕۑ�
	        mPreTime = curTime;
	        mLastX = x;
	        mLastY = y;
	        mLastZ = z;
	    }
	}
}
