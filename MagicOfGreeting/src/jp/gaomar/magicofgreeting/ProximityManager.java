package jp.gaomar.magicofgreeting;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ProximityManager implements SensorEventListener {

    // �ߐڃZ���T�[�̕ω������m���郊�X�i�[�ł��B
    public interface OnProximityListener {
        // �Z���T�[�̒l���ς������Ăяo����܂��B
        void onSensorChanged(SensorEvent event);
        // ������������Ăяo����܂��B
        void onFar(float value);
        // �߂Â�����Ăяo����܂��B
        void onNear(float value);
    }

    private SensorManager mSensorManager;
    private OnProximityListener mListener;

    private float mPreValue = -1;

    public ProximityManager(Context context) {
        mSensorManager = (SensorManager)context.getSystemService(
                                        Context.SENSOR_SERVICE);
    }

    public void setOnProximityListener(OnProximityListener listener) {
        mListener = listener;
    }

    public void onResume() {
        // �ߐڃZ���T�[���擾
        List<Sensor> list =
            mSensorManager.getSensorList(Sensor.TYPE_PROXIMITY);

        // �擾�ł��Ȃ���Ή������Ȃ�
        if (list.size() < 1) return;

        // �ߐڃZ���T�[���擾
        Sensor sensor = list.get(0);

        // �ߐڃZ���T�[�Ƀ��X�i�[��o�^
        // ��3�����Ŋ��x���w��ł��܂��B
        mSensorManager.registerListener(this, sensor,
                                   SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        // ���X�i�[������
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        // �ߐڃZ���T�[�ȊO�͉������Ȃ��B
        if (event.sensor.getType() != Sensor.TYPE_PROXIMITY) {
            return;
        }
        // ����͉������Ȃ�
        if (mPreValue == -1) {
            mPreValue = event.values[0];
            return;
        }
        float value = event.values[0];
        if (value < mPreValue) {
            // �O����l����������΋߂Â����Ƃ������ƂȂ̂�
            // ���X�i�[��onNear���\�b�h���Ăяo���B
            if (mListener != null) mListener.onNear(value);
        } else if (value > mPreValue) {
            // �O����l���傫����Ή����������Ƃ������ƂȂ̂�
            // ���X�i�[��onFar���\�b�h���Ăяo���B
            if (mListener != null) mListener.onFar(value);
        }
        // ���X�i�[���Z�b�g����Ă��
        // onSensorChanged���\�b�h���Ăяo��
        if (mListener != null) mListener.onSensorChanged(event);
        // ����̒l��ۑ�
        mPreValue = value;
    }
}
