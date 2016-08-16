package com.example.chaya.bontact.Ui.Activities;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.example.chaya.bontact.Ui.Dialogs.DialogInput;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Helpers.SendResponseHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.InnerConversationAdapter;
import com.example.chaya.bontact.Socket.io.SocketManager;
import com.example.chaya.bontact.Ui.Dialogs.EmailDialogActivity;


public class InnerConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final int INNER_CONVERSATION_LOADER = 1;
    private RecyclerView recyclerView;
    private InnerConversationAdapter adapter;
    private EditText chat_response_edittext;
    private ProgressBar loading;
    private Conversation current_conversation;
    private SendResponseHelper sendResponseHelper;
    private onlineStateChangesReceiver onlineStateBroadcastReceiver;
    private InviteReceiver inviteReceiver;
    private CurrentConversationChangedReceiver conversationChangedReceiver;
    private boolean isNew;
    int id_surfer;
    android.view.Menu menu;
    TextView no_chat_message;
    Button invite_btn;
    ConversationDataManager conversationDataManager;
    InnerConversationDataManager innerConversationDataManager;
    FloatingActionButton btn_send_mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversationDataManager = new ConversationDataManager(this);
        id_surfer = 0;
        Bundle args = getIntent().getExtras();
        if (args != null)
            id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);
        init();
    }

    public void init() {
        initData();
        initHeader();
        initContent();
        initFooter();
    }

    private void initData() {
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
            innerConversationDataManager = new InnerConversationDataManager(this, id_surfer);
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
            btn_send_mess = (FloatingActionButton) findViewById(R.id.btn_send_chat_response);
            btn_send_mess.setOnClickListener(this);
            chat_response_edittext = (EditText) findViewById(R.id.chat_response_edittext);
            chat_response_edittext.addTextChangedListener(textWatcher);
            if (current_conversation != null) {
                if (!current_conversation.isOnline) {
                    setDisableFooter();
                } else {
                    chat_response_edittext.setEnabled(true);
                    chat_response_edittext.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
            //  chat_response_edittext.setOnKeyListener(this);

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
                setProgressBarState(View.VISIBLE);
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

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            adapter.swapCursor(cursor);
            recyclerView.smoothScrollToPosition(cursor.getCount());
            setProgressBarState(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            isNew = false;
            if (menu != null)
                menu.setGroupVisible(R.id.inner_conversation_menu, true);
            if (current_conversation == null)//conversation is not in list
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

   /* public boolean onKey(View view, int keyCode, KeyEvent event) {
       *//* if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            switch (view.getId()) {
                case R.id.chat_response_edittext:
                    Log.d("enter pressed", "enter pressed");
                    sendChatResponse(chat_response_edittext.getText().toString());
                    chat_response_edittext.setText("");
                    break;
            }
            return true;
        }*//*
        return false; // pass on to other listeners.
    }*/

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
            case R.id.sms_channel:
                SendResponse(ChanelsTypes.sms);
                break;
            case R.id.phone_call_channel:
                SendResponse(ChanelsTypes.callback);
                break;
            case R.id.email_channel:
                SendResponse(ChanelsTypes.email);
                break;
        }
        return true;
    }

    public void SendResponse(int channel) {

        if (current_conversation == null)
            return;

        sendResponseHelper = new SendResponseHelper();
        if (!sendResponseHelper.isAllowedChannelToResponse(current_conversation, channel)) {
            Snackbar.make(chat_response_edittext, "you are not allowed do this action", Snackbar.LENGTH_LONG).show();
            return;
        }
        switch (channel) {
            case ChanelsTypes.sms:
                DialogInput smsInput = new DialogInput(this);
                smsInput.create(id_surfer, ChanelsTypes.sms);
                break;
            case ChanelsTypes.callback:
                DialogInput callbackInput = new DialogInput(this);
                callbackInput.create(id_surfer, ChanelsTypes.callback);
                break;
            case ChanelsTypes.email:
                Intent intent=new Intent(this, EmailDialogActivity.class);
                intent.putExtra(Contract.InnerConversation.COLUMN_ID_SURFUR,id_surfer);
                intent.putExtra(Contract.Conversation.COLUMN_EMAIL,current_conversation.email);
                startActivity(intent);
                break;

        }

    }

    public void sendChatResponse(String msg) {
        if (current_conversation != null && current_conversation.isOnline) {
            sendResponseHelper.sendChat(this, msg, current_conversation.idSurfer);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = IntentFilter.create(getResources().getString(R.string.change_visitor_online_state), "*/*");
        onlineStateBroadcastReceiver = new onlineStateChangesReceiver();
        registerReceiver(onlineStateBroadcastReceiver, intentFilter);
        intentFilter = IntentFilter.create(getResources().getString(R.string.invite_complete_action), "*/*");
        inviteReceiver = new InviteReceiver();
        registerReceiver(inviteReceiver, intentFilter);
        intentFilter = IntentFilter.create(getResources().getString(R.string.change_conversation_list_action), "*/*");
        conversationChangedReceiver = new CurrentConversationChangedReceiver();
        registerReceiver(conversationChangedReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(onlineStateBroadcastReceiver);
        unregisterReceiver(inviteReceiver);
        unregisterReceiver(conversationChangedReceiver);
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
            // chat_response_edittext.setEnabled(false);
            chat_response_edittext.setBackgroundColor(getResources().getColor(R.color.gray_very_light));
            chat_response_edittext.setHint("the visitor is offline right now :(");
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 0) {
                btn_send_mess.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple)));
                /*if (charSequence.length() == 1) {
                    btn_send_mess.animate()
                            .scaleX(0f)
                            .scaleY(0f)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    btn_send_mess.animate()
                                            .scaleX(1f)
                                            .scaleY(1f)
                                            .start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            })
                            .start();

               }*/
            } else {
                btn_send_mess.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_light)));
              /*  btn_send_mess.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                btn_send_mess.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                btn_send_mess.animate()
                                        .scaleX(0f)
                                        .scaleY(0f)
                                        .start();
                            }
                        })
                        .start();*/
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public class onlineStateChangesReceiver extends BroadcastReceiver {
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

    public class InviteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean status = intent.getBooleanExtra(getResources().getString(R.string.is_successed_key), false);
            int invited_id_surfer = intent.getIntExtra(getResources().getString(R.string.id_surfer), 0);
            if (invited_id_surfer != id_surfer || invited_id_surfer == 0) {
                setProgressBarState(View.GONE);
                return;
            }
            if (status) {
                if (AgentDataManager.getAgentInstanse() != null) {
                    conversationDataManager.getFirstDataFromServer(context, AgentDataManager.getAgentInstanse().getToken());
                }
                isNew = false;
                init();
            } else {
                setProgressBarState(View.GONE);
                //invite failed
            }
        }
    }

    public class CurrentConversationChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int changed_id_surfer = intent.getIntExtra(getResources().getString(R.string.id_surfer), 0);
            if (changed_id_surfer == id_surfer)
                current_conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);
        }
    }

}
