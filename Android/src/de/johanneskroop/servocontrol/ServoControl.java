package de.johanneskroop.servocontrol;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;


public class ServoControl extends Activity implements OnCheckedChangeListener{
    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private final double duration = 0.0025; // seconds
    private final int sampleRate = 44000;
    private final int numSamples = (int)(duration * (double)sampleRate);
    private final double sample[] = new double[numSamples];
    private final double freqOfTone = 440; // hz

    private final byte generatedSnd[] = new byte[2 * numSamples];

    Handler handler = new Handler();

    
    boolean play = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servo_control);
        
        ToggleButton btn1 = (ToggleButton)findViewById(R.id.toggleButton1);
        btn1.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        
       
    }

 // Use a new tread as this can take a while
    Thread thread = new Thread(new Runnable() 
    {
        public void run() 
        {
            genTone();
            handler.post(new Runnable() 
            {

                public void run() 
                {
                    playSound();
                }
            });
        }
    });
    
  
    
    void genTone(){
    	int ms = sampleRate/1000;
    	int x = 2;
    	int num = numSamples/2;
        // fill out the array
    	for (int j = 0; j < numSamples;) {
    		for (int i = 0; i < x && j < numSamples; ++i) {
    			sample[j++] = 1;//Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
    		}
    		for (int i = 0; i < x && j < numSamples; ++i) {
    			sample[j++] = 0;//Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
    		}
    		for (int i = 0; i < x && j < numSamples; ++i) {
    			sample[j++] = 1;//Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
    		}
    		for (int i = 0; i < x && j < numSamples; ++i) {
    			sample[j++] = 0;//Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
    		}
    	}

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound()
    {
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
    
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		// Use a new tread as this can take a while
        final Thread thread = new Thread(new Runnable() 
        {
            public void run() 
            {
                genTone();
                handler.post(new Runnable() 
                {

                    public void run() 
                    {
                        playSound();
                    }
                });
            }
        });
        thread.start();
		
	}
}