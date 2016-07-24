package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chaya.bontact.R;

import java.io.IOException;

/**
 * Created by chaya on 7/24/2016.
 */
public class AudioPlayerInnerHelper {

    TextView playBtn, pauseBtn;
    AppCompatSeekBar seekBar;
    MediaPlayer mediaPlayer;
    double startTime = 0;
    double finalTime = 0;
    Handler seekHandler;
    Uri recordUrl = null;
    Context context;

    public void setRecordUrl(Uri recordUrl) {
        this.recordUrl = recordUrl;
    }

    public AudioPlayerInnerHelper(View itemView) {
        this.context = itemView.getContext();
        initPlayerComponent(itemView);
    }

    public boolean preparePlayer(Uri recordUrl) {
        setRecordUrl(recordUrl);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(completionListener);
        try {
            mediaPlayer.setDataSource(context, recordUrl);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void preparePlayer(int resourcePlayer) {
        mediaPlayer = MediaPlayer.create(context, resourcePlayer);
        mediaPlayer.setOnCompletionListener(completionListener);

    }

    public void initPlayerComponent(View itemView) {
        playBtn = (TextView) itemView.findViewById(R.id.play_btn);
        pauseBtn = (TextView) itemView.findViewById(R.id.pause_btn);
        seekBar = (AppCompatSeekBar) itemView.findViewById(R.id.seekbar_visitor_record);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        playBtn.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
        playBtn.setOnClickListener(playListener);
        pauseBtn.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
        seekHandler = new Handler();
    }

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            startTime = 0;
            stopRecord();
        }
    };
    View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                stopRecord();
            } else {
                if (mediaPlayer == null) {
                    Toast.makeText(context, "the player is being ready", Toast.LENGTH_SHORT).show();
                    return;
                }
                playRecord();
            }
        }
    };
    AppCompatSeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser == true) {
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                    playRecord(progress);
            }
            //todo:this method don't work !
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public void playRecord() {
      playRecord(0);
    }
    public void playRecord(int newStartTime) {
        if (mediaPlayer == null)
            return;
        playBtn.setText(R.string.pause_btn_icon);
        mediaPlayer.start();
        seekHandler.postDelayed(UpdateSongTime, 100);
        finalTime = mediaPlayer.getDuration();
        if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration())
            startTime = 0;
        else
        if(newStartTime==0)
            startTime = mediaPlayer.getCurrentPosition();
        else
        startTime=newStartTime;
        seekBar.setMax((int) finalTime);
        seekBar.setProgress((int) startTime);
    }

    public void stopRecord() {
        playBtn.setText(R.string.play_btn_icon);
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            seekBar.setProgress((int) startTime);
            seekHandler.postDelayed(this, 100);
        }
    };

    public void audioPlayerProblematicPrepare(int issue) {
        switch (issue) {
            case R.string.account_not_allow:
                preparePlayer(R.raw.callrecord);
            case R.string.short_record:
                preparePlayer(R.raw.recorder);
        }
    }

}
