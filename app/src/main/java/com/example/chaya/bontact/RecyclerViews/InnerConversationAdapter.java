package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.AudioPlayerInnerHelper;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.SpecialFontsHelper;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.R;

import java.io.IOException;


/**
 * Created by chaya on 6/26/2016.
 */

public class InnerConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VISITOR_TEXT_VH = 0;
    public static final int AGENT_TEXT_VH = 1;
    public static final int SYSTEM_MSG_VH = 2;
    public static final int VISITOR_RECORD_VH = 3;
    Cursor cursor;
    Context context;

    public InnerConversationAdapter(Context context, Cursor cursor) {
        this.context = context;
        setCursor(cursor);
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VISITOR_TEXT_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case AGENT_TEXT_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case VISITOR_RECORD_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_record, null);
                viewHolder = new InnerConversationVisitorRecordHolder(view);
                break;
            case SYSTEM_MSG_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_system_item, null);
                viewHolder = new InnerConversationMsgHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {

            int idSurfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            InnerConversationDataManager dataManager = new InnerConversationDataManager(context, idSurfer);
            InnerConversation innerConversation = dataManager.convertCursorToInnerConversation(cursor);

            InnerConversationHolder innerConversationHolder;
            InnerConversationMsgHolder innerConversationMsgHolder;
            InnerConversationVisitorRecordHolder visitorRecordHolder;

            switch (getItemViewType(position)) {
                case SYSTEM_MSG_VH:
                    innerConversationMsgHolder = (InnerConversationMsgHolder) holder;
                    if (innerConversation.actionType == ChanelsTypes.webCall)
                        innerConversationMsgHolder.msg.setText(R.string.webcall_cancled);
                    else
                        innerConversationMsgHolder.msg.setText(setMsgConcatWithDate(innerConversation));
                    innerConversationMsgHolder.msg.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    break;
                case AGENT_TEXT_VH:
                    innerConversationHolder = (InnerConversationHolder) holder;
                    innerConversationHolder.msg.setText(setMsgConcatWithDate(innerConversation));
                    innerConversationHolder.name.setText(getNameForAgent(innerConversation));
                    innerConversationHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));
                    break;
                case VISITOR_TEXT_VH:
                    innerConversationHolder = (InnerConversationHolder) holder;
                    innerConversationHolder.msg.setText(setMsgConcatWithDate(innerConversation));
                    innerConversationHolder.name.setText(getNameForVisitor(innerConversation));
                    innerConversationHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));
                    break;
                case VISITOR_RECORD_VH:
                    visitorRecordHolder = ((InnerConversationVisitorRecordHolder) holder);
                    visitorRecordHolder.name.setText(getNameForVisitor(innerConversation));
                    visitorRecordHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));
                    setRecordPlayer(visitorRecordHolder, innerConversation);
                    break;
            }
        }
    }

    private boolean setRecordPlayer(InnerConversationVisitorRecordHolder visitorRecordHolder, InnerConversation innerConversation) {

        String url = null;
        if (innerConversation.record == true && innerConversation.mess != null && Integer.parseInt(innerConversation.mess) > 5 &&
                (innerConversation.actionType == ChanelsTypes.callback || innerConversation.actionType == ChanelsTypes.webCall)) {
            AgentDataManager agentDataManager = new AgentDataManager();
            url = context.getResources().getString(R.string.domain_api);
            url += "record/" + agentDataManager.getAgentToken(context) + "/" + innerConversation.req_id + "/" + innerConversation.req_id + ".mp3";
            Uri recordUri = Uri.parse(url);

            if (visitorRecordHolder.player.preparePlayer(recordUri) == false)
                return false;
           /* visitorRecordHolder.setPlayer(recordUri);
            visitorRecordHolder.mediaPlayer = new MediaPlayer();
            try {
                visitorRecordHolder.mediaPlayer.setDataSource(context, recordUri);
                visitorRecordHolder.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                visitorRecordHolder.mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }*/
        } else if (innerConversation.actionType == ChanelsTypes.callback && innerConversation.record == true)
            visitorRecordHolder.player.audioPlayerProblematicPrepare(R.string.short_record);
        else if (innerConversation.actionType == ChanelsTypes.callback)
            visitorRecordHolder.player.audioPlayerProblematicPrepare(R.string.account_not_allow);


                    /*String url = null;
                    if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_RECORD)) == 1 &&
                            innerConversation.mess != null && Integer.parseInt(innerConversation.mess) > 5 &&
                            (innerConversation.actionType == ChanelsTypes.callback || innerConversation.actionType == ChanelsTypes.webCall)) {
                        AgentDataManager agentDataManager = new AgentDataManager();
                        url = context.getResources().getString(R.string.domain_api);
                        url += "record/" + agentDataManager.getAgentToken(context) + "/" + innerConversation.req_id + "/" + innerConversation.req_id + ".mp3";
                        Uri recordUri = Uri.parse(url);
                        // visitorRecordHolder.setPlayer(recordUri);
                        visitorRecordHolder.mediaPlayer = new MediaPlayer();
                        visitorRecordHolder.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                        try {
                            visitorRecordHolder.mediaPlayer.setDataSource(context, recordUri);
                            visitorRecordHolder.mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (innerConversation.actionType == ChanelsTypes.callback
                            && cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_RECORD)) == 1)
                        setShortCall(visitorRecordHolder);
                    else if (innerConversation.actionType == ChanelsTypes.callback)
                        setCallNotActive(visitorRecordHolder);*/
                    /*  visitorRecordHolder.msg.setText(url);
                    visitorRecordHolder.msg.setVisibility(View.VISIBLE);*/
        return true;
    }

    private Spannable setMsgConcatWithDate(InnerConversation innerConversation) {

        String msg = null;
        Spannable span = null;
        msg = innerConversation.getMess();
        String date = DateTimeHelper.getTimeFromDateToDisplay(innerConversation.getTimeRequest());
        if (msg != null && date != null) {
            msg = msg.concat("   " + date);
            int index = msg.indexOf(date);
            span = new SpannableString(msg);
            span.setSpan(new RelativeSizeSpan(0.7f), index, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return span;
        }
        return null;
    }

    private String getNameForAgent(InnerConversation innerConversation) {
        if (innerConversation.agentName == null)
            return "agent";
        else
            return innerConversation.agentName;
    }

    private String getNameForVisitor(InnerConversation innerConversation) {
        if (innerConversation.from_s == null)
            return "visitor";
        else
            return innerConversation.from_s;
    }

    public AudioPlayerInnerHelper getPlayer(InnerConversationVisitorRecordHolder visitorRecordHolder)
    {
        return visitorRecordHolder.player;
    }

    @Override
    public int getItemViewType(int position) {

        if (cursor != null && cursor.moveToPosition(position)) {
            if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_SYSTEM_MSG)) == 0)//not a system msg
            {
                if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_REP_REQUEST)) == 1) {
                    return AGENT_TEXT_VH;
                } else {
                    if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.callback ||
                            (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.webCall &&
                                    cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS)) != null))
                        return VISITOR_RECORD_VH;
                    if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.webCall)
                        return SYSTEM_MSG_VH;
                    return VISITOR_TEXT_VH;
                }
            }
        }
        return SYSTEM_MSG_VH;
    }

    @Override
    public int getItemCount() {
        if (cursor != null)
            return cursor.getCount();
        return 0;
    }

    public Cursor swapCursor(Cursor cursor) {
        if (getCursor() == cursor) {
            return null;
        }
        Cursor oldCursor = getCursor();
        setCursor(cursor);
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    //********************View Holder************************************

    public class InnerConversationVisitorRecordHolder extends RecyclerView.ViewHolder {

        TextView chanelIcon, name;
        AudioPlayerInnerHelper player;
       /* TextView playBtn, pauseBtn;
        AppCompatSeekBar seekBar;
        MediaPlayer mediaPlayer;
        double startTime = 0;
        double finalTime = 0;
        Handler seekHandler;
        Uri recordUrl = null;*/

        public InnerConversationVisitorRecordHolder(View itemView) {
            super(itemView);
            chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
            chanelIcon.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            name = (TextView) itemView.findViewById(R.id.displayName);
            name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            player = new AudioPlayerInnerHelper(itemView);
          /*  playBtn = (TextView) itemView.findViewById(R.id.play_btn);
            pauseBtn = (TextView) itemView.findViewById(R.id.pause_btn);
            seekBar = (AppCompatSeekBar) itemView.findViewById(R.id.seekbar_visitor_record);
            playBtn.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            playBtn.setOnClickListener(playListener);
            pauseBtn.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            seekHandler = new Handler();*/
        }

       /*public void setPlayer(Uri uri) {
            recordUrl = uri;
        }


        View.OnClickListener playListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    stopRecord();
                    playBtn.setText(R.string.play_btn_icon);
                } else {
                    if (mediaPlayer == null)
                        return;
                    playBtn.setText(R.string.pause_btn_icon);
                    playRecord();
                    finalTime = mediaPlayer.getDuration();
                    if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration())
                        startTime = 0;
                    else
                        startTime = mediaPlayer.getCurrentPosition();
                    seekBar.setMax((int) finalTime);
                    seekBar.setProgress((int) startTime);
                }
            }
        };

        public void playRecord() {
            if (mediaPlayer == null)
                return;
            mediaPlayer.start();
            seekHandler.postDelayed(UpdateSongTime, 100);

        }

        public void stopRecord() {
            if (mediaPlayer != null)
                mediaPlayer.pause();
        }

        Runnable UpdateSongTime = new Runnable() {
            public void run() {
                startTime = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) startTime);
                seekHandler.postDelayed(this, 100);
            }
        };*/


    }

    class InnerConversationHolder extends InnerConversationMsgHolder {
        TextView chanelIcon, name;

        public InnerConversationHolder(View itemView) {
            super(itemView);
            chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
            chanelIcon.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            name = (TextView) itemView.findViewById(R.id.displayName);
            name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));

            itemView.setOnClickListener(this);
        }

    }

    class InnerConversationMsgHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView msg;

        public InnerConversationMsgHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.mess);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}


/*
public class InnerConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VISITOR_TEXT_VH = 0;
    public static final int AGENT_TEXT_VH = 1;
    public static final int SYSTEM_MSG_VH = 2;
    public static final int VISITOR_RECORD_VH = 3;
    Cursor cursor;
    Context context;

    public InnerConversationAdapter(Context context, Cursor cursor) {
        this.context = context;
        setCursor(cursor);
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VISITOR_TEXT_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case AGENT_TEXT_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case VISITOR_RECORD_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_record, null);
                viewHolder = new InnerConversationVisitorRecordHolder(view);
                break;
            case SYSTEM_MSG_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_system_item, null);
                viewHolder = new InnerConversationMsgHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            //convert to object
            int idSurfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            InnerConversationDataManager dataManager = new InnerConversationDataManager(context, idSurfer);
            InnerConversation innerConversation = dataManager.convertCursorToInnerConversation(cursor);

            int type = getItemViewType(position);

            //initial parameter msg and date
            String msg = null;
            if (innerConversation.actionType == ChanelsTypes.callback || innerConversation.actionType == ChanelsTypes.webCall) {
                if (innerConversation.recordUrl != null)
                    msg = innerConversation.recordUrl;
                else
                    msg = context.getResources().getString(R.string.webcall_cancled);
            } else
                msg = innerConversation.getMess();
            String date = DateTimeHelper.getTimeFromDateToDisplay(innerConversation.getTimeRequest());
            if (msg != null && date != null)
                msg = msg.concat("   " + date);
            int index = msg.indexOf(date);
            Spannable span = new SpannableString(msg);
            span.setSpan(new RelativeSizeSpan(0.7f), index, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String name = null;
            switch (getItemViewType(position)) {
                case SYSTEM_MSG_VH:
                    InnerConversationMsgHolder innerConversationBaseHolder = (InnerConversationMsgHolder) holder;
                    innerConversationBaseHolder.msg.setText(span);
                    innerConversationBaseHolder.msg.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    break;
                case AGENT_TEXT_VH:
                    if (innerConversation.agentName == null)
                        name = "agent";
                    else
                        name = innerConversation.agentName;
                case VISITOR_TEXT_VH:
                    if (name == null) {
                        if (innerConversation.from_s == null)
                            name = "visitor";
                        else
                            name = innerConversation.from_s;
                    }
                    InnerConversationHolder innerConversationHolder = (InnerConversationHolder) holder;
                    innerConversationHolder.msg.setText(span);
                    innerConversationHolder.name.setText(name);
                    innerConversationHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));
                    break;
                case VISITOR_RECORD_VH:
                    if (name == null) {
                        if (innerConversation.from_s == null)
                            name = "visitor";
                        else
                            name = innerConversation.from_s;
                    }
                    InnerConversationVisitorRecordHolder visitorRecordHolder = ((InnerConversationVisitorRecordHolder) holder);
                    visitorRecordHolder.name.setText(name);
                    visitorRecordHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));
                    //  visitorRecordHolder.seekBar.setMax(50);
                    //  visitorRecordHolder.seekBar.setProgress(0);

                    //get url to record
                    String url = null;
                    if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_RECORD)) == 1 &&
                            innerConversation.mess != null && Integer.parseInt(innerConversation.mess) > 5 &&
                            (innerConversation.actionType == ChanelsTypes.callback || innerConversation.actionType == ChanelsTypes.webCall)) {
                        AgentDataManager agentDataManager = new AgentDataManager();
                        url = context.getResources().getString(R.string.domain_api);
                        url += "record/" + agentDataManager.getAgentToken(context) + "/" + innerConversation.req_id + "/" + innerConversation.req_id + ".mp3";
                        Uri recordUri = Uri.parse(url);
                        visitorRecordHolder.setPlayer(recordUri);
                        visitorRecordHolder.mediaPlayer=new MediaPlayer();
                        visitorRecordHolder.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                        try {
                            visitorRecordHolder.mediaPlayer.setDataSource(context,recordUri);
                            visitorRecordHolder.mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (innerConversation.actionType == ChanelsTypes.callback
                            && cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_RECORD)) == 1)
                        setShortCall(visitorRecordHolder);
                    else if (innerConversation.actionType == ChanelsTypes.callback)
                        setCallNotActive(visitorRecordHolder);
                    visitorRecordHolder.msg.setText(url);
                    visitorRecordHolder.msg.setVisibility(View.VISIBLE);

   */
    /*
    public void setShortCall(InnerConversationVisitorRecordHolder visitorRecordHolder) {
        visitorRecordHolder.mediaPlayer = MediaPlayer.create(context, R.raw.recorder);
    }

    public void setCallNotActive(InnerConversationVisitorRecordHolder visitorRecordHolder) {
        visitorRecordHolder.mediaPlayer = MediaPlayer.create(context, R.raw.callrecord);
    }


    @Override
    public int getItemViewType(int position) {

        if (cursor != null && cursor.moveToPosition(position)) {
            if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_SYSTEM_MSG)) == 0)//not a system msg
            {
                if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_REP_REQUEST)) == 1) {
                    return AGENT_TEXT_VH;
                } else {
                    if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.webCall ||
                            cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.callback)
                        return VISITOR_RECORD_VH;
                    return VISITOR_TEXT_VH;
                }
            }
        }
        return SYSTEM_MSG_VH;
    }

    @Override
    public int getItemCount() {
        if (cursor != null)
            return cursor.getCount();
        return 0;
    }

    public Cursor swapCursor(Cursor cursor) {
        if (getCursor() == cursor) {
            return null;
        }
        Cursor oldCursor = getCursor();
        setCursor(cursor);
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    //********************View Holder************************************

    class InnerConversationVisitorRecordHolder extends RecyclerView.ViewHolder {

        TextView chanelIcon, name, playBtn, pauseBtn;
        AppCompatSeekBar seekBar;
        MediaPlayer mediaPlayer;
        private double startTime = 0;
        private double finalTime = 0;
        Handler seekHandler;
        Uri recordUrl = null;

        public InnerConversationVisitorRecordHolder(View itemView) {
            super(itemView);
            chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
            chanelIcon.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            name = (TextView) itemView.findViewById(R.id.displayName);
            name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            playBtn = (TextView) itemView.findViewById(R.id.play_btn);
            pauseBtn = (TextView) itemView.findViewById(R.id.pause_btn);
            seekBar = (AppCompatSeekBar) itemView.findViewById(R.id.seekbar_visitor_record);
            playBtn.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            playBtn.setOnClickListener(playListener);
            pauseBtn.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));

            // seekHandler=new Handler();
        }

        public void setPlayer(Uri uri) {
            recordUrl = uri;
        }


        View.OnClickListener playListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playBtn.setText(R.string.play_btn_icon);
                } else {
                    if (mediaPlayer == null)
                        return;
                    playBtn.setText(R.string.pause_btn_icon);
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaPlayer.setDataSource(context, recordUrl);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        finalTime = mediaPlayer.getDuration();
                        startTime = mediaPlayer.getCurrentPosition();
                        seekBar.setMax((int) finalTime);
                        seekBar.setProgress((int) startTime);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // seekHandler.postDelayed(UpdateSongTime, 100);
                }
            }
        };
        private Runnable UpdateSongTime = new Runnable() {
            public void run() {
                startTime = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) startTime);
                seekHandler.postDelayed(this, 100);
            }
        };


    }

    class InnerConversationHolder extends InnerConversationMsgHolder {
        TextView chanelIcon, name;

        public InnerConversationHolder(View itemView) {
            super(itemView);
            chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
            chanelIcon.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            name = (TextView) itemView.findViewById(R.id.displayName);
            name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));

            itemView.setOnClickListener(this);
        }

    }

    class InnerConversationMsgHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView msg;

        public InnerConversationMsgHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.mess);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}

*/

