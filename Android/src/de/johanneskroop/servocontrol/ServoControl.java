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
	private final int duration = 3; // seconds
    private final int sampleRate = 20000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double sample2[] = new double[numSamples];
    private final double freqOfTone = 20000; // hz

    private final byte generatedSnd[] = new byte[4 * numSamples];

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
    	String s = Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50)+Integer.toBinaryString(50);
    	s = s + s + s + s + s + s;
    	s = s + s + s + s + s + s;
    	System.out.println("submit: "+s);
    	char[] ar =  s.toCharArray();
    	int ari = 0;
    	// fill out the array
    	sample[0] = 0;
    	sample2[0] = 0;
    	sample[1] = 1;
    	if(ari < ar.length && ar[ari++] == '1')
    	{
    		sample2[1] = 1;
    		sample2[2] = -1;
    	}
    	else
    	{
    		sample2[1] = 0;
    		sample2[2] = 0;
    	}
    	sample[2] = 1;
        for (int i = 3; i < numSamples; i++) 
        {
            sample[i] = sample[i-1] * -1;
            if(ari < ar.length && ar[ari++] == '1')
        	{
        		sample2[i] = 1;
        		if(i+1 < numSamples)
        		{
        			sample2[i+1] = -1;
        		}
        	}
        	else
        	{
        		sample2[i] = 0;
        		if(i+1 < numSamples)
        		{
        			sample2[i+1] = 0;
        		}
        	}
            if( i < 30)
            {
            	System.out.println("("+ sample[i] + ") - ("+ sample2[i]+")");
            }
            i++;
            if( i < numSamples)
            {
            	sample[i] = sample[i-1];
            	if( i < 30)
                {
            		System.out.println("("+ sample[i] + ") - ("+ sample2[i]+")");
                }
            }
            
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        int oidx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            final short val2 = (short) ((sample2[oidx++] * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (val2 & 0x00ff);
            generatedSnd[idx++] = (byte) ((val2 & 0xff00) >>> 8);
        }
    }

    void playSound()
    {
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
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