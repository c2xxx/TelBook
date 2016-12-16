package com.chen.telbook.helper;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.chen.libchen.Logger;
import com.chen.telbook.MyApplication;

/**
 * 音频播放
 * Created by hui on 2016/1/18.
 */
public class MediaPlayerHelper {
    private MediaPlayer mPlayer;
    private MediaPlayerListener mListener;

    //单例
    private static MediaPlayerHelper instance = null;

    private MediaPlayerHelper() {

    }

    public static MediaPlayerHelper getInstance() {
        if (instance == null) {
            synchronized (MediaPlayerHelper.class) {
                if (instance == null) {
                    instance = new MediaPlayerHelper();
                }
            }
        }
        return instance;
    }


    public void playSound(String filePathString, final MediaPlayerListener listener) {
        release();
        mPlayer = new MediaPlayer();
        //保险起见，设置报错监听
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mPlayer.reset();
                return false;
            }
        });

        mListener = listener;
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    listener.onEnd(true);
                }
            });
            mPlayer.setDataSource(filePathString);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                    listener.onStart();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSoundAssets(String filePathString, final MediaPlayerListener listener) {
        release();
        mPlayer = new MediaPlayer();
        //保险起见，设置报错监听
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mPlayer.reset();
                return false;
            }
        });

        mListener = listener;
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    listener.onEnd(true);
                }
            });
            AssetFileDescriptor fileDescriptor = MyApplication.getContext().getAssets().openFd(filePathString);
            mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                    listener.onStart();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e);
        }
    }


    public void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;

            if (mListener != null) {
                mListener.onEnd(false);
            }
        }
    }

    public interface MediaPlayerListener {
        void onStart();

        /**
         * @param isPlayComplete 是否播放完成，而不是继续播放下一段音频(区别于未播放完成被终止)
         */
        void onEnd(boolean isPlayComplete);
    }
}
