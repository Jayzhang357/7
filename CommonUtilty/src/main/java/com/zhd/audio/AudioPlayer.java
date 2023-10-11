package com.zhd.audio;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class AudioPlayer {
    private SoundPool mSoundPool;
    private int mCurrentPaly; //当前播放音频ID
    private Context mContext;
    private final int mInterval = 2000; //播放音频间隔时间
    private TimerTask mTask;
    private Timer mTimer;
    private int [] mSoundArray;
    private ConcurrentLinkedQueue<Integer> mPlayerQueue;
    //声音播放优先级
    public static final int SPEEDING = 0;
    public static final int EXCEED = 1;
    public static final int FORBIDEN = 2;

    public AudioPlayer(Context context) {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        mCurrentPaly = -1;
        mContext = context;
        mPlayerQueue = new ConcurrentLinkedQueue<Integer>();
        mSoundArray = new int [3];
        mTask = new TimerTask() {
            @Override
            public void run() {
                Integer soundID = getPlayeStream();
                if(soundID != null) {
                    mCurrentPaly = mSoundPool.play(mSoundArray[soundID], 1, 1, soundID, 0, 1);
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 0, mInterval);
    }

    public void addPlayStream(int type) {
        if(type < SPEEDING || type > FORBIDEN)
            return;
        synchronized (mPlayerQueue) {
            if(!mPlayerQueue.contains(Integer.valueOf(type))) {
                mPlayerQueue.add(Integer.valueOf(type));
            }
        }
    }

    public Integer getPlayeStream() {
        Integer maxPriority = -1;
        synchronized (mPlayerQueue) {
            if(mPlayerQueue.size() >= 2) {
                Integer id = null;
                while((id = mPlayerQueue.poll()) != null) {
                    maxPriority = id > maxPriority ? id : maxPriority;
                }
            } else {
                maxPriority = mPlayerQueue.poll();
            }
        }

        return maxPriority;
    }

    public void load() {
//		mSoundArray[SPEEDING] = mSoundPool.load(mContext, R.raw.speeding, SPEEDING);
//		mSoundArray[EXCEED] = mSoundPool.load(mContext, R.raw.exceed, EXCEED);
//		mSoundArray[FORBIDEN] = mSoundPool.load(mContext, R.raw.forbiden, FORBIDEN);
    }

    public void unload() {
        mSoundPool.pause(mCurrentPaly);
        mSoundPool.unload(SPEEDING);
        mSoundPool.unload(EXCEED);
        mSoundPool.unload(FORBIDEN);
    }

    public void release() {
        mSoundPool.autoPause();
        mSoundPool.release();
    }
}
