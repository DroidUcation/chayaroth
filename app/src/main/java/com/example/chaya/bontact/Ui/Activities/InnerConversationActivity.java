package com.example.chaya.bontact.Ui.Activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.AlertCallbackResponse;
import com.example.chaya.bontact.Helpers.AlertComingSoon;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.SendResponseHelper;
import com.example.chaya.bontact.Helpers.SpecialFontsHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.InnerConversationAdapter;

public class InnerConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, EditText.OnKeyListener {

    private static final int INNER_CONVERSATION_LOADER = 1;
    private RecyclerView recyclerView;
    private InnerConversationAdapter adapter;
    EditText response_mess;
    ProgressBar loading;
    Conversation current_conversation;
    int selected_reply_type;
    SendResponseHelper sendResponseHelper;
    onlineStateChangesReciver broadcastReceiver;
    android.view.Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            int id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);
            ConversationDataManager conversationDataManager = new ConversationDataManager(this);
            this.current_conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);
            int current_unread_conversation_count = ConversationDataManager.getUnreadConversations(this);
            ConversationDataManager.setUnreadConversations(this, current_unread_conversation_count - 1);
            conversationDataManager.updateConversation(this, current_conversation.idSurfer, Contract.Conversation.COLUMN_UNREAD, 0);
            setTitle(current_conversation.displayname);
        }
        AgentDataManager agentDataManager = new AgentDataManager();
        String token = agentDataManager.getAgentToken(this);
        if (token != null && current_conversation != null) {
            InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(this,current_conversation);
            innerConversationDataManager.getData(this, token);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
       // getSupportActionBar().setHomeAsUpIndicator(R.drawable.avatar1);

        setContentView(R.layout.activity_inner_conversation);
        recyclerView = (RecyclerView) findViewById(R.id.inner_conversation_recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new InnerConversationAdapter(this, null);
            recyclerView.setAdapter(adapter);
        }
        response_mess = (EditText) findViewById(R.id.response_message);
        response_mess.setOnKeyListener(this);
        FloatingActionButton btn_send_mess = (FloatingActionButton) findViewById(R.id.btn_send_message);
        btn_send_mess.setOnClickListener(this);
        setProgressBarState(View.VISIBLE);
        sendResponseHelper = new SendResponseHelper();
        getSupportLoaderManager().initLoader(INNER_CONVERSATION_LOADER, null, this);

        //this.getActionBar().setDisplayShowCustomEnabled(true);
        //this.getActionBar().setDisplayShowTitleEnabled(false);

        //  initToolBar();
        //channel_icons = new ArrayList<>();
        // initChannelIcons();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = IntentFilter.create(getResources().getString(R.string.change_visitor_online_state), "*/*");
        broadcastReceiver = new onlineStateChangesReciver();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inner_conversation_menu, menu);
        this.menu = menu;
        setSelectedChannelType(current_conversation.lasttype);
        return super.onCreateOptionsMenu(menu);
       /* TextView icon = new TextView(this);
        icon.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        MenuItem item;

        for (int i = 0; i < menu.size(); i++) {
            item = menu.getItem(i);
            int current_channel = ChanelsTypes.convertStringChannelToInt(item.getTitle().toString());
            icon.setText(ChanelsTypes.getIconByChanelType(current_channel));*/
        // if (current_channel == selected_reply_type) {

        //}
        // item.setActionView(icon);
        //}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        if (id == R.id.chat_channel || id == R.id.sms_channel || id == R.id.email_channel || id == R.id.phone_call_channel) {
            setSelectedChannelType(ChanelsTypes.convertStringChannelToInt(item.getTitle().toString()));
        }
        return true;
    }

    public void setSelectedChannelType(int selected_reply_type) {

        if (sendResponseHelper != null &&
                !sendResponseHelper.isAllowedCurrentChannelResponse(current_conversation, selected_reply_type)) {
            if (selected_reply_type != ChanelsTypes.chat) {//selected channel not allowed
               /* View view = getLayoutInflater().inflate(R.layout.bottom_sheet_emp_cov, null);
                Spinner spin1 = (Spinner) view.findViewById(R.id.spin1);
                Spinner spin2 = (Spinner) view.findViewById(R.id.spin2);
                ListView catList = (ListView) view.findViewById(R.id.listItems);
                Button btnDone = (Button) view.findViewById(R.id.btnDone);*/
                final Dialog mBottomSheetDialog = new Dialog(this,
                        R.style.MaterialDialogSheet);
                mBottomSheetDialog.setContentView(new TextView(this));
                mBottomSheetDialog.setCancelable(true);
                mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
                mBottomSheetDialog.show();
            } else {//offline and last type chat
                //addTextMsgToList(); problem id
            }
        } else {//allowed
            this.selected_reply_type = selected_reply_type;
            response_mess.setHint(ChanelsTypes.getPlaceHolderByChannelIcon(this, selected_reply_type));
            if (selected_reply_type == ChanelsTypes.callback)
                ((FloatingActionButton) findViewById(R.id.btn_send_message)).setBackgroundResource(R.drawable.ic_phone_iphone_black_18dp);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = Contract.InnerConversation.COLUMN_TIME_REQUEST;
        String selectionCoulmns = Contract.InnerConversation.COLUMN_ID_SURFUR;
        String[] selectionArgs = {current_conversation.idSurfer + ""};
        CursorLoader cursorLoader = new CursorLoader(this, Contract.InnerConversation.INNER_CONVERSATION_URI, null, selectionCoulmns + "=?", selectionArgs, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getCount() > 0)
                adapter.swapCursor(cursor);
            recyclerView.smoothScrollToPosition(cursor.getCount());
            setProgressBarState(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            setProgressBarState(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }


    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            switch (view.getId()) {
                case R.id.response_message:
                    Log.d("enter pressed", "enter pressed");
                    SendResponseMessage(response_mess.getText().toString());
                    response_mess.setText("");
                    break;
            }
            return true;
        }
        return false; // pass on to other listeners.
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(InnerConversationActivity.this, "ACTION", Toast.LENGTH_SHORT).show();
        switch (v.getId()) {
            case R.id.btn_send_message:
                if (response_mess != null && !response_mess.equals("")) {
                    SendResponseMessage(response_mess.getText().toString());
                    response_mess.setText("");
                }
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
        }
    }

    public void SendResponseMessage(String textMsg) {
        if (current_conversation == null)
            return;

        sendResponseHelper = new SendResponseHelper();
        if (!sendResponseHelper.isAllowedCurrentChannelResponse(current_conversation, selected_reply_type)) {
            return;
        }
        if (selected_reply_type == ChanelsTypes.callback) {
            sendCallBack();
            return;
        }
        addTextMsgToList(textMsg, false);
        if (selected_reply_type == ChanelsTypes.chat) {
            sendResponseHelper.sendChat(this, textMsg, current_conversation.idSurfer);
        } else {
            sendResponseHelper.sendSmsOrEmail(this, selected_reply_type, textMsg, current_conversation.idSurfer);
        }
    }

    private void addTextMsgToList(String textMsg, boolean systemMsg) {
        InnerConversation innerConversation = new InnerConversation();
        innerConversation.id = InnerConversationDataManager.getIdAsPlaceHolder();
        innerConversation.actionType = selected_reply_type;
        innerConversation.mess = textMsg;
        innerConversation.rep_request = true;
        if (AgentDataManager.getAgentInstanse() != null)
            innerConversation.agentName = AgentDataManager.getAgentInstanse().getName();
        innerConversation.name = innerConversation.agentName;
        if (current_conversation != null)
            innerConversation.idSurfer = current_conversation.idSurfer;
        innerConversation.timeRequest = DateTimeHelper.getCurrentStringDateInGmtZero();
        //innerConversation.timeRequest = DateTimeHelper.dateFullFormat.format(new Date());
        if (selected_reply_type != ChanelsTypes.callback && selected_reply_type != ChanelsTypes.webCall)
            innerConversation.datatype = 1;//txt msg
        innerConversation.systemMsg = systemMsg;
        InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(this, current_conversation);
        //Toast.makeText(InnerConversationActivity.this, "ADD MSG " + innerConversation.toString(), Toast.LENGTH_SHORT).show();
        innerConversationDataManager.saveData(innerConversation);
    }

    private void sendCallBack() {
        AlertCallbackResponse alertCallbackResponse = new AlertCallbackResponse(this);
        alertCallbackResponse.create(current_conversation);
        alertCallbackResponse.show();
    }

    public void setProgressBarState(int state) {
        if (loading == null)
            loading = (ProgressBar) findViewById(R.id.loading_inner_conversation);
        loading.setVisibility(state);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class onlineStateChangesReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int state = intent.getIntExtra(getResources().getString(R.string.online_state), -1);
            int id_surfer = intent.getIntExtra(getResources().getString(R.string.id_surfer), -1);
            if (id_surfer == current_conversation.idSurfer) {
                if (state == 0)
                    current_conversation.isOnline = false;
                else if (state == 1)
                    current_conversation.isOnline = true;
                // setDisableChannelIcons();
            }
        }
    }


/* public void initToolBar() {

        ActionBar mActionBar = getSupportActionBar();
        LayoutInflater mInflater = LayoutInflater.from(this);
        mActionBar.setDisplayShowCustomEnabled(true);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        ImageView avatar = (ImageView) mCustomView.findViewById(R.id.avatar);
        avatar.setImageResource(Integer.parseInt(current_conversation.avatar));
        TextView chat_icon = (TextView) mCustomView.findViewById(R.id.chanel_chat);
        chat_icon.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        TextView sms_icon = (TextView) mCustomView.findViewById(R.id.chanel_sms);
        sms_icon.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        TextView email_icon = (TextView) mCustomView.findViewById(R.id.chanel_email);
        email_icon.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        TextView phone_icon = (TextView) mCustomView.findViewById(R.id.chanel_phone_call);
        phone_icon.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        mActionBar.setCustomView(mCustomView);
        //mActionBar.setLogo(Integer.parseInt(current_conversation.avatar));
    }
  public void initChannelIcons() {
        channel_icons.add(InitEveryChannelIcon(R.id.chanel_chat, R.string.chat_icon));
        channel_icons.add(InitEveryChannelIcon(R.id.chanel_sms, R.string.sms_icon));
        channel_icons.add(InitEveryChannelIcon(R.id.chanel_email, R.string.email_icon));
        channel_icons.add(InitEveryChannelIcon(R.id.chanel_phone_call, R.string.phone_calling_icon));
    }

    private TextView InitEveryChannelIcon(int elementId, int textRes) {
        TextView btn = null;
        btn = (TextView) findViewById(elementId);
        Log.d("btn", btn.getText().toString());
        btn.setText(textRes);
        btn.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        btn.setTag(ChanelsTypes.getChanelTypeByIcon(textRes));
        btn.setOnClickListener(channelListener);
        btn.setTextSize(20);
        if (current_conversation != null && current_conversation.lasttype == (int) btn.getTag()) {
            setAsSelectedChannel(btn);
        }
        setDisableChannelIcons();
        return btn;
    }
    View.OnClickListener channelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (TextView btn : channel_icons)
                if (view.getId() == btn.getId()) {
                    setAsSelectedChannel(btn);
                    if (selected_reply_type == ChanelsTypes.callback)
                        sendCallBack();
                } else {
                    btn.setTextColor(getResources().getColor(R.color.white));
                    setDisableChannelIcons();
                }
        }
    };

    public void setDisableChannelIcons() {
        if (channel_icons != null)
            for (TextView btn : channel_icons)
                if (!sendResponseHelper.isAllowedCurrentChannelResponse(current_conversation, (Integer) btn.getTag())) {
                    btn.setEnabled(false);
                    btn.setTextColor(getResources().getColor(R.color.gray_dark));
                }
    }

    public void setAsSelectedChannel(TextView btn) {
        btn.setTextColor(getResources().getColor(R.color.purple));
        selected_reply_type = (int) btn.getTag();
        response_mess.setHint(ChanelsTypes.getPlaceHolderByChannelIcon(this, selected_reply_type));
        // if(selected_reply_type == ChanelsTypes.callback)
        //((Button) findViewById(R.id.btn_send_message)).setBackgroundResource(R.id.chanel_phone_call);
    }*/
}
