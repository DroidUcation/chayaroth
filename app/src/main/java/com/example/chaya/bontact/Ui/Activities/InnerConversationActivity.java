package com.example.chaya.bontact.Ui.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.AlertCallbackResponse;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Helpers.SendResponseHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.InnerConversationAdapter;
import com.example.chaya.bontact.Socket.io.SocketManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class InnerConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, EditText.OnKeyListener {

    private static final int INNER_CONVERSATION_LOADER = 1;
    private RecyclerView recyclerView;
    private InnerConversationAdapter adapter;
    private EditText chat_response_edittext;
    private ProgressBar loading;
    private Conversation current_conversation;
    private SendResponseHelper sendResponseHelper;
    private onlineStateChangesReciver onlineStatebroadcastReceiver;
    private boolean isNew;
    int id_surfer;
    android.view.Menu menu;
    TextView no_chat_message;
    Button invite_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id_surfer = 0;
        Bundle args = getIntent().getExtras();
        if (args != null)
            id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);
        initData();
        initHeader();
        initContent();
        initFooter();
    }

    private void initData() {
        ConversationDataManager conversationDataManager = new ConversationDataManager(this);
        current_conversation = null;
        if (id_surfer != 0) {//init current conversation if possible
            this.current_conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);
        }
        if (current_conversation != null) {//THIS ID HAS CONVERSATIONS
            isNew = false;
            //update unread  numbers
            int current_unread_conversation_count = ConversationDataManager.getUnreadConversations(this);
            ConversationDataManager.setUnreadConversations(this, current_unread_conversation_count - 1);
            conversationDataManager.updateConversation(this, current_conversation.idSurfer, Contract.Conversation.COLUMN_UNREAD, 0);
        } else { //surfer is new
            isNew = true;
        }

        AgentDataManager agentDataManager = new AgentDataManager();
        String token = agentDataManager.getAgentToken(this);
        if (token != null && id_surfer != 0) {
            InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(this, id_surfer);
            innerConversationDataManager.getData(this, token, callResponse);
        }
    }

    ServerCallResponse callResponse = new ServerCallResponse() {
        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            if (response != null && response.equals("[]"))//empty data
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressBarState(View.GONE);
                        setEmptyDetails();
                        isNew = true;
                    }
                });
            }
        }
    };

    private void initHeader() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (current_conversation != null && current_conversation.displayname != null)
            setTitle(current_conversation.displayname);
        if (isNew) {
            setTitle("#" + id_surfer);
        }

    }

    private void initContent() {
        setContentView(R.layout.activity_inner_conversation);

        recyclerView = (RecyclerView) findViewById(R.id.inner_conversation_recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new InnerConversationAdapter(this, null);
            recyclerView.setAdapter(adapter);
        }
        setProgressBarState(View.VISIBLE);
        getSupportLoaderManager().initLoader(INNER_CONVERSATION_LOADER, null, this);
    }

    private void initFooter() {

        if (!isNew) {
            chat_response_edittext = (EditText) findViewById(R.id.chat_response_edittext);
            if (current_conversation != null) {
                if (!current_conversation.isOnline) {
                    setDisableFooter();
                } else {
                    chat_response_edittext.setEnabled(true);
                    chat_response_edittext.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
            chat_response_edittext.setOnKeyListener(this);
            FloatingActionButton btn_send_mess = (FloatingActionButton) findViewById(R.id.btn_send_chat_response);
            btn_send_mess.setOnClickListener(this);
            sendResponseHelper = new SendResponseHelper();
        } else {

        }
    }

    private void setEmptyDetails() {
        no_chat_message = (TextView) findViewById(R.id.no_chats);
        no_chat_message.setVisibility(View.VISIBLE);
        invite_btn = (Button) findViewById(R.id.invite_btn);
        invite_btn.setVisibility(View.VISIBLE);
        invite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketManager.getInstance().inviteToChat(id_surfer);
            }
        });
        LinearLayout bottom_layout = (LinearLayout) findViewById(R.id.bottom_layout);
        bottom_layout.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inner_conversation_menu, menu);
        if (isNew)
            menu.setGroupVisible(R.id.inner_conversation_menu, false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = Contract.InnerConversation.COLUMN_TIME_REQUEST;
        String selectionCoulmns = Contract.InnerConversation.COLUMN_ID_SURFUR;
        String[] selectionArgs = {id_surfer + ""};
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
            isNew = false;
            if (menu != null)
                menu.setGroupVisible(R.id.inner_conversation_menu, true);
            if(current_conversation==null)//conversation is not in list
            {
                //todo: get the current conversation
            }
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
                case R.id.chat_response_edittext:
                    Log.d("enter pressed", "enter pressed");
                    sendChatResponse(chat_response_edittext.getText().toString());
                    chat_response_edittext.setText("");
                    break;
            }
            return true;
        }
        return false; // pass on to other listeners.
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_chat_response:
                if (chat_response_edittext != null && !chat_response_edittext.equals("")) {
                    sendChatResponse(chat_response_edittext.getText().toString());
                    chat_response_edittext.setText("");
                }
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                EditText editText = new EditText(this);
                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
                dialog.setTitleText("Sweet!")
                        .setContentText("Here's a custom image.")
                        .setCustomImage(R.mipmap.bontact_launcher)
                        .setContentView(editText);
                dialog.show();
        }
        return true;
    }

    public void sendChatResponse(String msg) {
        if (current_conversation != null && current_conversation.isOnline) {
            addTextMsgToList(ChanelsTypes.chat, msg, false);
            sendResponseHelper.sendChat(this, msg, current_conversation.idSurfer);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = IntentFilter.create(getResources().getString(R.string.change_visitor_online_state), "*/*");
        onlineStatebroadcastReceiver = new onlineStateChangesReciver();
        registerReceiver(onlineStatebroadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(onlineStatebroadcastReceiver);
    }

    private void addTextMsgToList(int channelType, String textMsg, boolean systemMsg) {
        InnerConversation innerConversation = new InnerConversation();
        innerConversation.id = InnerConversationDataManager.getIdAsPlaceHolder();
        innerConversation.actionType = channelType;
        innerConversation.mess = textMsg;
        innerConversation.rep_request = true;
        if (AgentDataManager.getAgentInstanse() != null)
            innerConversation.agentName = AgentDataManager.getAgentInstanse().getName();
        innerConversation.name = innerConversation.agentName;
        if (current_conversation != null)
            innerConversation.idSurfer = current_conversation.idSurfer;
        innerConversation.timeRequest = DateTimeHelper.getCurrentStringDateInGmtZero();
        //innerConversation.timeRequest = DateTimeHelper.dateFullFormat.format(new Date());
        if (channelType != ChanelsTypes.callback && channelType != ChanelsTypes.webCall)
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

    public void setDisableFooter() {
        if (invite_btn != null && isNew)
            invite_btn.setVisibility(View.GONE);
        else if (chat_response_edittext != null) {
            chat_response_edittext.setEnabled(false);
            chat_response_edittext.setBackgroundColor(getResources().getColor(R.color.gray_very_light));
            chat_response_edittext.setHint("the visitor is offline right now :(");
        }
    }

    public class onlineStateChangesReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int state = intent.getIntExtra(getResources().getString(R.string.online_state), -1);
            int id_surfer = intent.getIntExtra(getResources().getString(R.string.id_surfer), -1);
            if (id_surfer == id_surfer) {
                if (current_conversation != null) {
                    if (state == 0)
                        current_conversation.isOnline = false;
                    else if (state == 1)
                        current_conversation.isOnline = true;
                }
                if (state == 0)
                    setDisableFooter();
            }
        }

    }

}
