package com.example.chaya.bontact.Ui.Activities;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.Helpers.AlertComingSoon;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.InnerConversationAdapter;

import java.io.IOException;

public class InnerConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, EditText.OnKeyListener {

    private static final int INNER_CONVERSATION_LOADER = 1;
    private RecyclerView recyclerView;
    private InnerConversationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    EditText response_mess;
    ProgressBar loading;
    Conversation current_conversation;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_conversation);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            int id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);
            ConverastionDataManager converastionDataManager = new ConverastionDataManager(this);
            this.current_conversation = converastionDataManager.getConversationByIdSurfer(id_surfer);
            setTitle(current_conversation.displayname);

        }
        recyclerView = (RecyclerView) findViewById(R.id.inner_conversation_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new InnerConversationAdapter(this, null);
            recyclerView.setAdapter(adapter);
        }
        response_mess = (EditText) findViewById(R.id.response_message);
        response_mess.setOnKeyListener(this);
        Button btn_send_mess = (Button) findViewById(R.id.btn_send_message);
        btn_send_mess.setOnClickListener(this);
        setProgressBarState(View.VISIBLE);
        getSupportLoaderManager().initLoader(INNER_CONVERSATION_LOADER, null, this);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            switch (view.getId()) {
                case R.id.response_message:
                    Log.d("enter pressed", "enter pressed");
                    response_mess.setText("");
                    SendResponseMessage(response_mess.getText().toString());
                    break;
            }
            return true;
        }
        return false; // pass on to other listeners.
    }

    public void SendResponseMessage(String textMsg) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = null;
        String selectionCoulmns = Contract.InnerConversation.COLUMN_ID_SURFUR;
        String[] selectionArgs = {current_conversation.idSurfer + ""};
        CursorLoader cursorLoader = new CursorLoader(this, Contract.InnerConversation.INNER_CONVERSATION_URI, null, selectionCoulmns + "=?", selectionArgs, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getCount() > 0)

        /*    adapter = new InnerConversationAdapter(this, cursor);
            recyclerView.setAdapter(adapter);*/
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_message:
                response_mess.setText("");
                SendResponseMessage(response_mess.getText().toString());

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                AlertComingSoon.show(this);
        }
        return true;
    }

    public void setProgressBarState(int state) {
        if (loading == null)
            loading = (ProgressBar) findViewById(R.id.loading_inner_conversation);
        loading.setVisibility(state);
    }

    @Override
    public void onBackPressed() {
        ConverastionDataManager converastionDataManager = new ConverastionDataManager(this);
        converastionDataManager.updateConversation(this, current_conversation.idSurfer, Contract.Conversation.COLUMN_UNREAD, 0);
        getSupportMediaController();

        super.onBackPressed();
    }


}
