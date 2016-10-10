package com.example.chaya.bontact.Ui.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.AgentListDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Helpers.DatesHelper;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.Ui.Dialogs.callbackDialog;
import com.example.chaya.bontact.Helpers.ChannelsTypes;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Helpers.SendResponseHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.InnerConversationAdapter;
import com.example.chaya.bontact.Socket.io.SocketManager;
import com.example.chaya.bontact.Ui.Dialogs.EmailDialogActivity;
import com.example.chaya.bontact.Ui.Dialogs.SmsDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class InnerConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final int INNER_CONVERSATION_LOADER = 1;
    private RecyclerView recyclerView;
    private InnerConversationAdapter adapter;
    private EditText chat_response_edittext;
    private Conversation current_conversation;
    private SendResponseHelper sendResponseHelper;
    private onlineStateChangesReceiver onlineStateBroadcastReceiver;
    private InviteReceiver inviteReceiver;
    private CurrentConversationChangedReceiver conversationChangedReceiver;
    private TypingReceiver typingReceiver;
    private boolean isNew;
    int id_surfer;
    android.view.Menu menu;
    TextView no_msg_title;
    TextView no_msg_text;
    ImageView no_msg_image;
    Button invite_btn;
    ConversationDataManager conversationDataManager;
    InnerConversationDataManager innerConversationDataManager;
    FloatingActionButton btn_send_mess;
    ImageView loading;
    Animation.AnimationListener animationListener;
    private boolean isConversationBusy;
    int tryRequestCount;
    String assigned = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryRequestCount = 0;
        conversationDataManager = new ConversationDataManager(this);
        id_surfer = 0;
        Bundle args = getIntent().getExtras();
        if (args != null)
            id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);

        if (id_surfer == 0)
            onBackPressed();

        initData();//open conversation and update current conversation
        drawHeader();
        initContent();
        initFooter();
    }

    private void initData() {
        current_conversation = null;
        AgentDataManager agentDataManager = new AgentDataManager();
        String token = agentDataManager.getAgentToken(this);
        if (id_surfer != 0) {//init current conversation if possible
            this.current_conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);
        }
        if (current_conversation != null) {//THIS ID HAS CONVERSATIONS
            isNew = false;
            if (token != null) {
                innerConversationDataManager = new InnerConversationDataManager(this, current_conversation.idSurfer);
                innerConversationDataManager.getData(this, token, callResponse);
            }
        } else { //surfer is new
            isNew = true;
            if (token != null && id_surfer != 0) {
                conversationDataManager.getConversationByIdFromServer(token, id_surfer, null, callResponse);
                // innerConversationDataManager = new InnerConversationDataManager(this, id_surfer);
                //innerConversationDataManager.getData(this, token, callResponse);
            }
        }


    }

    private void drawHeader() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (current_conversation != null && current_conversation.displayname != null)
            setTitle(current_conversation.displayname);
        else
            setTitle("#" + id_surfer);
        if (current_conversation != null) {
            assigned = AgentListDataManager.getAgentName(current_conversation.assign);
            if (assigned != null)
                getSupportActionBar().setSubtitle(getString(R.string.assign_to) + " " + assigned);
            else
                getSupportActionBar().setSubtitle("");

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
        initLoading();
        getSupportLoaderManager().initLoader(INNER_CONVERSATION_LOADER, null, this);
    }

    private void initFooter() {
        sendResponseHelper = new SendResponseHelper();
        btn_send_mess = (FloatingActionButton) findViewById(R.id.btn_send_chat_response);
        chat_response_edittext = (EditText) findViewById(R.id.chat_response_edittext);
        btn_send_mess.setOnClickListener(this);
//        if (current_conversation != null)
        if (!VisitorsDataManager.isOnline(id_surfer))
            setEnableFooter(false);
        else
            setEnableFooter(true);
    }

    ServerCallResponse callResponse = new ServerCallResponse() {
        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            if (response != null && response.equals("[]") && isNew == true && current_conversation == null)//empty data
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        load_animations(false);
                        setEmptyDetails(true);
                        isNew = true;
                    }
                });
            }
        }
    };


    public void initLoading() {
        animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                System.out.println("End Animation!");
            }
        };
        loading = (ImageView) findViewById(R.id.loading);
        load_animations(true);
    }

    void load_animations(boolean state) {
        new AnimationUtils();
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
        rotation.setAnimationListener(animationListener);
        if (state) {
            loading.setVisibility(View.VISIBLE);
            loading.startAnimation(rotation);
        } else {
            loading.setVisibility(View.GONE);
            loading.clearAnimation();
        }
    }

    private void setEmptyDetails(boolean value) {
        no_msg_title = (TextView) findViewById(R.id.no_msg_title);
        no_msg_text = (TextView) findViewById(R.id.no_msg_txt);
        no_msg_image = (ImageView) findViewById(R.id.no_msg_img);
        invite_btn = (Button) findViewById(R.id.invite_btn);
        if (!value) {
            no_msg_image.setVisibility(View.GONE);
            no_msg_title.setVisibility(View.GONE);
            no_msg_text.setVisibility(View.GONE);
            invite_btn.setVisibility(View.GONE);
        } else {
            load_animations(false);
            no_msg_image.setVisibility(View.VISIBLE);
            no_msg_title.setVisibility(View.VISIBLE);
            no_msg_text.setVisibility(View.GONE);
            if (VisitorsDataManager.isOnline(id_surfer)) {
                invite_btn.setVisibility(View.VISIBLE);
                invite_btn.setOnClickListener(inviteListener);
            }
            btn_send_mess.setVisibility(View.GONE);
            chat_response_edittext.setVisibility(View.GONE);
        }
    }

    View.OnClickListener inviteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            load_animations(true);
            setEmptyDetails(false);
            SocketManager.getInstance().inviteToChat(id_surfer);

        }
    };


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inner_conversation_menu, menu);
//        if (isNew)
//            menu.setGroupVisible(R.id.inner_conversation_menu, false);
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
            load_animations(false);
            recyclerView.setVisibility(View.VISIBLE);
            isNew = false;
            if (menu != null)
                menu.setGroupVisible(R.id.inner_conversation_menu, true);
            if (current_conversation == null)//conversation is not in list
            {
                current_conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);
            }
            if(no_msg_title!=null&&no_msg_title.getVisibility()==View.VISIBLE) {
                setEmptyDetails(false);
                setEnableFooter(VisitorsDataManager.isOnline(id_surfer));
            }
        } else {
            load_animations(true);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_chat_response:
                String msg = chat_response_edittext.getText().toString();

                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                chat_response_edittext.setText("");
                if (msg != null && !msg.equals("")) {
                    msg = msg.trim();
                    sendChatResponse(msg);
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (loading != null && loading.getVisibility() != View.VISIBLE && !isNew) {//DONT WORKING IN BACKGROUND
            switch (id) {
                case R.id.sms_channel:
                    SendResponse(ChannelsTypes.sms);
                    break;
                case R.id.phone_call_channel:
                    SendResponse(ChannelsTypes.callback);
                    break;
                case R.id.email_channel:
                    SendResponse(ChannelsTypes.email);
                    break;
            }
        }
        return true;
    }

    public void SendResponse(int channel) {

        if (current_conversation == null)
            return;

        sendResponseHelper = new SendResponseHelper();
        if (!sendResponseHelper.isAllowedChannelToResponse(conversationDataManager.getConversationByIdSurfer(id_surfer), channel)) {
            String msg = ChannelsTypes.getNotAllowedMsgByChannelType(this, channel);
            Toast.makeText(InnerConversationActivity.this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        switch (channel) {
            case ChannelsTypes.sms:
                SmsDialog smsDialog = new SmsDialog(this);
                smsDialog.create(id_surfer);
                break;
            case ChannelsTypes.callback:
                callbackDialog callbackInput = new callbackDialog(this);
                callbackInput.create(id_surfer);
                break;
            case ChannelsTypes.email:
                Intent intent = new Intent(this, EmailDialogActivity.class);
                intent.putExtra(Contract.InnerConversation.COLUMN_ID_SURFUR, id_surfer);
                intent.putExtra(Contract.Conversation.COLUMN_EMAIL, current_conversation.email);
                isConversationBusy = true;
                startActivity(intent);
                break;
        }
    }

    public void sendChatResponse(String msg) {
        if (current_conversation != null) {
            if (VisitorsDataManager.isOnline(id_surfer))
                sendResponseHelper.sendChat(this, msg, current_conversation.idSurfer);
//            else
//                setEnableFooter(false);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        isConversationBusy = false;
        IntentFilter intentFilter = IntentFilter.create(getResources().getString(R.string.change_visitors_list_action), "*/*");
        onlineStateBroadcastReceiver = new onlineStateChangesReceiver();
        registerReceiver(onlineStateBroadcastReceiver, intentFilter);
        intentFilter = IntentFilter.create(getResources().getString(R.string.invite_complete_action), "*/*");
        inviteReceiver = new InviteReceiver();
        registerReceiver(inviteReceiver, intentFilter);
        intentFilter = IntentFilter.create(getResources().getString(R.string.change_conversation_list_action), "*/*");
        conversationChangedReceiver = new CurrentConversationChangedReceiver();
        registerReceiver(conversationChangedReceiver, intentFilter);
        intentFilter = IntentFilter.create(getResources().getString(R.string.action_typing), "*/*");
        typingReceiver = new TypingReceiver();
        registerReceiver(typingReceiver, intentFilter);
        if (id_surfer != 0)
            conversationDataManager.selectedIdConversation = id_surfer;

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(onlineStateBroadcastReceiver);
        unregisterReceiver(inviteReceiver);
        unregisterReceiver(conversationChangedReceiver);
        unregisterReceiver(typingReceiver);
        if (current_conversation != null) {
            conversationDataManager.updateUnread(current_conversation.idSurfer, 0);
        }
        if (id_surfer != 0)
            conversationDataManager.selectedIdConversation = 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setEnableFooter(boolean value) {
        if (!value) {
            if (isNew) {
                if (invite_btn != null)
                    invite_btn.setVisibility(View.GONE);
            } else if (chat_response_edittext != null) {
                chat_response_edittext.setEnabled(false);
                btn_send_mess.setEnabled(false);
                btn_send_mess.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_medium)));
                chat_response_edittext.setHint("the visitor is offline right now :(");
            }
        } else {
            if (isNew) {
                if (invite_btn != null)
                    invite_btn.setVisibility(View.VISIBLE);
            } else if (chat_response_edittext != null) {
                btn_send_mess.setVisibility(View.VISIBLE);
                chat_response_edittext.setVisibility(View.VISIBLE);
                chat_response_edittext.setEnabled(true);
                btn_send_mess.setEnabled(true);
                btn_send_mess.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange_dark)));
            }
        }
    }

    public class onlineStateChangesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int action = intent.getIntExtra(getResources().getString(R.string.notify_adapter_key_action), -1);
            int changed_id_surfer = intent.getIntExtra(getResources().getString(R.string.notify_adapter_key_id_surfer), -1);

            if (id_surfer == changed_id_surfer) {
                if (action == VisitorsDataManager.ACTION_REMOVE_VISITOR)
                    setEnableFooter(false);
                else
                    setEnableFooter(true);
            }
        }

    }

    public class InviteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean status = intent.getBooleanExtra(getResources().getString(R.string.is_successed_key), false);
            int invited_id_surfer = intent.getIntExtra(getResources().getString(R.string.id_surfer), 0);
            if (invited_id_surfer != id_surfer || invited_id_surfer == 0) {
                load_animations(false);
                return;
            }
            if (status) {
                isNew = false;
                if (AgentDataManager.getAgentInstance() != null)
                    conversationDataManager.getConversationByIdFromServer(AgentDataManager.getAgentInstance().token, id_surfer, getConversationByIdOnResponse, null);
                VisitorsDataManager.updateIsNewState(InnerConversationActivity.this, id_surfer, false);
            } else {
                load_animations(false);
            }
        }
    }

    public class TypingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int idSurfer = intent.getIntExtra(getString(R.string.id_surfer), 0);
            if (idSurfer == id_surfer) {
                String name = intent.getStringExtra(getString(R.string.typing_name_key));
                boolean state = intent.getBooleanExtra(getString(R.string.typing_state_key), false);
                if (name != null && state)
                    getSupportActionBar().setSubtitle(name + " is typing...");
                else {
                    if (assigned != null)
                        getSupportActionBar().setSubtitle(getString(R.string.assign_to) + " " + assigned);
                    else
                        getSupportActionBar().setSubtitle("");
                }
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

    ServerCallResponse getConversationByIdOnResponse = new ServerCallResponse() {
        int tryCount = 0;

        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            Gson gson = new Gson();
            try {
                if (response == null) {
                    retryCallGetConversationByIdFromServer();
                    return;
                }
                JSONObject jsonObject = new JSONObject(response).getJSONObject("conversations");
                current_conversation = gson.fromJson(jsonObject.toString(), Conversation.class);
                if ((current_conversation == null || current_conversation.innerConversationData == null || current_conversation.innerConversationData.size() == 0) && tryCount < 3) {
                    retryCallGetConversationByIdFromServer();
                    return;
                }
                current_conversation.lastdate = DatesHelper.convertDateToCurrentGmt(current_conversation.lastdate);
                conversationDataManager.insertOrUpdate(current_conversation, true);
                if (innerConversationDataManager == null)
                    innerConversationDataManager = new InnerConversationDataManager(InnerConversationActivity.this, current_conversation);
                //todo: change inner save data
                for (InnerConversation innerConversation : current_conversation.innerConversationData)
                    innerConversationDataManager.saveData(innerConversation);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isNew = false;
//                        drawHeader();
                        load_animations(false);
                        setEnableFooter(true);
                    }
                });
            } catch (JSONException e) {
                retryCallGetConversationByIdFromServer();
            }
        }

        private void retryCallGetConversationByIdFromServer() {
            if (tryCount < 4) {
                tryCount++;
                conversationDataManager.getConversationByIdFromServer(AgentDataManager.getAgentInstance().token, id_surfer, getConversationByIdOnResponse, null);
            } else {

            }

        }
    };


}





