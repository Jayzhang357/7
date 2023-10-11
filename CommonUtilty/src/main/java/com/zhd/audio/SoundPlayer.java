package com.zhd.audio;

import com.zhd.commonutilty.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer {

    private static SoundPlayer SOUND_PLAYER_INSTANCE = null;
    private SoundPool mSoundPool;
    private Context mContext;

    public static SoundPlayer getInstance(Context context) {
        if (SOUND_PLAYER_INSTANCE == null)
        {
            SOUND_PLAYER_INSTANCE = new SoundPlayer(context);
        }

        return SOUND_PLAYER_INSTANCE;
    }

    public SoundPlayer(Context context) {
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 6);

        //mSoundPool.play(1, 1, 1, 0, 0, 1);//第一个参数为id，id即为放入到soundPool中的顺序，。第二个和第三个参数为左右声道的音量控制。第四个参数为优先级，由于只有这一个声音，因此优先级在这里并不重要。第五个参数为是否循环播放，0为不循环，-1为循环。最后一个参数为播放比率，从0.5到2，一般为1，表示正常播放。
    }

    public void playNormalButton(){
        mSoundPool.play(1, 1, 1, 0, 0, 1);
    }

    public void playWarnming(){
        mSoundPool.play(3, 1, 1, 0, 0, 1);
    }

    public void playSteerReady(){
        mSoundPool.play(5, 1, 1, 0, 0, 1);
    }

    public void playSteerInterrupt(){
        mSoundPool.play(6, 1, 1, 0, 0, 1);
    }

    public void playSteerReject(){
        mSoundPool.play(4, 1, 1, 0, 0, 1);
    }
}
