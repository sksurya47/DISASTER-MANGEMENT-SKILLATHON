/*
 * Copyright 2018 Nazmul Idris. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.floodalert.app.fcm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.floodalert.app.common.CustomLog;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Provides access to a MediaPlayer object which is used to play a single MP3 file from the
 * <code>res/raw</code> folder.
 */
public class SkMediaPlayerHolder {

    public static final int SEEKBAR_REFRESH_INTERVAL_MS = 1000;

    private int mResourceId;
    private final MediaPlayer mMediaPlayer;
    private Context mContext;
    private ArrayList<String> mLogMessages = new ArrayList<>();
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekbarProgressUpdateTask;

    public SkMediaPlayerHolder(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopUpdatingSeekbarWithPlaybackProgress(true);
                logToUI("MediaPlayer playback completed");

            }
        });
        logToUI("mMediaPlayer = new MediaPlayer()");
    }

    // MediaPlayer orchestration.

    public void release() {
        logToUI("release() and mMediaPlayer = null");
        mMediaPlayer.release();
    }

    public void play() {
        CustomLog.trace("SkMediaPlayerHolder: play: ");
        if (!mMediaPlayer.isPlaying()) {
            logToUI(String.format("start() %s",
                    mContext.getResources().getResourceEntryName(mResourceId)));
            mMediaPlayer.start();
            startUpdatingSeekbarWithPlaybackProgress();
        }
    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            logToUI("pause()");
        }
    }

    public void reset() {
        logToUI("reset()");
        mMediaPlayer.reset();
        load(mResourceId);
        stopUpdatingSeekbarWithPlaybackProgress(true);
    }

    @SuppressLint("NewApi")
    public void load(int resourceId) {
        mResourceId = resourceId;
        AssetFileDescriptor assetFileDescriptor =
                mContext.getResources().openRawResourceFd(mResourceId);
        try {
            logToUI("load() {1. setDataSource}");
            mMediaPlayer.setDataSource(assetFileDescriptor);
        } catch (NoSuchMethodError|Exception e) {
            logToUI(e.toString());
        }

        try {
            logToUI("load() {2. prepare}");
            mMediaPlayer.prepare();
        } catch (Exception e) {
            logToUI(e.toString());
        }

    }

    // Reporting media playback position to Seekbar in MainActivity.

    private void stopUpdatingSeekbarWithPlaybackProgress(boolean resetUIPlaybackPosition) {
        mExecutor.shutdownNow();
        mExecutor = null;
        mSeekbarProgressUpdateTask = null;

    }

    private void startUpdatingSeekbarWithPlaybackProgress() {
        // Setup a recurring task to sync the mMediaPlayer position with the Seekbar.
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarProgressUpdateTask == null) {
            mSeekbarProgressUpdateTask = new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        int currentPosition = mMediaPlayer.getCurrentPosition();

                    }
                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekbarProgressUpdateTask,
                0,
                SEEKBAR_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    // Logging to UI methods.

    public void logToUI(String msg) {
        mLogMessages.add(msg);
        fireLogUpdate();
    }

    /**
     * update the MainActivity's UI with the debug log messages
     */
    public void fireLogUpdate() {
        StringBuffer formattedLogMessages = new StringBuffer();
        for (int i = 0; i < mLogMessages.size(); i++) {
            formattedLogMessages.append(i)
                    .append(" - ")
                    .append(mLogMessages.get(i));
            if (i != mLogMessages.size() - 1) {
                formattedLogMessages.append("\n");
            }
        }
    }

}
