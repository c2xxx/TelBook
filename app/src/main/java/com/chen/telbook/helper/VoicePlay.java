package com.chen.telbook.helper;

import com.chen.libchen.Logger;

/**
 * Created by ChenHui on 2016/12/15.
 */

public class VoicePlay {

    public static void playNetError() {
        Logger.d("播放网络错误语音");
        playAssets("mp3_net_error.mp3");
    }

    public static void playNewMissedCall() {
        Logger.d("播放您有新的未接电话语音");
        playAssets("mp3_new_missed_call.mp3");
    }

    private static void playAssets(String url) {
        MediaPlayerHelper.getInstance().playSoundAssets(url, new MediaPlayerHelper.MediaPlayerListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean isPlayComplete) {

            }
        });
    }

}
