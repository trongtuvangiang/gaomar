package jp.gaomar.magicofgreeting;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class SoundSwitch implements Runnable{
	private OnReachedVolumeListener mListener;

	private boolean isRecording = true;

	private static final int SAMPLE_RATE = 8000;

	public void stop(){
		isRecording = false;
	}

	public void setOnVolumeReachedListener(OnReachedVolumeListener listener){
		mListener = listener;
	}

	public interface OnReachedVolumeListener {
		void OnReachedVolum( short volume );
	}

	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
		SAMPLE_RATE,
		AudioFormat.CHANNEL_CONFIGURATION_MONO,
		AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		short[] buffer = new short[bufferSize];

		audioRecord.startRecording();

		while(isRecording){
			audioRecord.read(buffer, 0, bufferSize);
			short max = 0;
			for ( int i=0; i<bufferSize; i++){
				max = (short)Math.max(max, buffer[i]);
				if( mListener != null){
					if( max > 10000 ){
						mListener.OnReachedVolum(max);
						break;
					}

				}
			}
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		audioRecord.stop();
		audioRecord.release();
	}
}
