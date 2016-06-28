package com.example.chaya.bontact.Ui.Activities;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.DividerItemDecoration;
import com.example.chaya.bontact.RecyclerViews.InboxAdapter;
import com.example.chaya.bontact.RecyclerViews.InnerConversationAdapter;

public class InnerConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener,EditText.OnKeyListener {



    private static final int INNER_CONVERSATION_LOADER = 0;
    private RecyclerView recyclerView;
    private InnerConversationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    EditText response_mess;
    ProgressBar loading;
    private int id_surfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_conversation);
        Bundle args = getIntent().getExtras();
        if (args != null) {
             this.id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);
        }

        //init compononent
        recyclerView = (RecyclerView) findViewById(R.id.inner_conversation_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        response_mess= (EditText) findViewById(R.id.response_message);
        response_mess.setOnKeyListener(this);
        Button btn_send_mess= (Button) findViewById(R.id.btn_send_message);
        loading= (ProgressBar) findViewById(R.id.loading_inner_conversation);
        loading.setVisibility(View.VISIBLE);

        //go bring data
        getSupportLoaderManager().initLoader(INNER_CONVERSATION_LOADER, null, this);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
        {
            switch (view.getId())
              {
               case R.id.response_message:
                 Log.d("enter pressed","enter pressed");
                   response_mess.setText("");
                   SendResponseMessage(response_mess.getText().toString());
                 break;
              }
             return true;
       }
        return false; // pass on to other listeners.
    }
    public void SendResponseMessage(String textMsg)
    {

    }



    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = null;
        String selectionCoulmns=Contract.InnerConversation.COLUMN_ID_SURFUR;
        String[] selectionArgs = { this.id_surfer+""};
        CursorLoader cursorLoader=new CursorLoader(this,Contract.InnerConversation.INNER_CONVERSATION_URI,null,selectionCoulmns+ "=?",selectionArgs,null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

//todo:if need the visibility
        recyclerView.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        if (cursor != null && cursor.moveToFirst()) {
            adapter = new InnerConversationAdapter(this, cursor);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_send_message:
                response_mess.setText("");
                SendResponseMessage(response_mess.getText().toString());

        }
    }



}
