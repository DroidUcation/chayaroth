package com.example.chaya.bontact.Ui.Activities;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.DividerItemDecoration;
import com.example.chaya.bontact.RecyclerViews.InboxAdapter;
import com.example.chaya.bontact.RecyclerViews.InnerConversationAdapter;

public class InnerConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener,EditText.OnKeyListener {

    private static final int INNER_CONVERSATION_LOADER = 1;
    private RecyclerView recyclerView;
    private InnerConversationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    EditText response_mess;
    ProgressBar loading;
    Conversation current_conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_conversation);
        Bundle args = getIntent().getExtras();
        if (args != null) {
             int id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);
             ConverastionDataManager  converastionDataManager=new ConverastionDataManager(this);
           this.current_conversation= converastionDataManager.getConversationByIdSurfer(id_surfer);
            setTitle(current_conversation.displayname);

        }
        recyclerView = (RecyclerView) findViewById(R.id.inner_conversation_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        response_mess= (EditText) findViewById(R.id.response_message);
        response_mess.setOnKeyListener(this);
        Button btn_send_mess= (Button) findViewById(R.id.btn_send_message);
        btn_send_mess.setOnClickListener(this);
       setProgressBarState(View.VISIBLE);
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
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = null;
        String selectionCoulmns=Contract.InnerConversation.COLUMN_ID_SURFUR;
        String[] selectionArgs = { current_conversation.idSurfer+""};
        CursorLoader cursorLoader=new CursorLoader(this,Contract.InnerConversation.INNER_CONVERSATION_URI,null,selectionCoulmns+ "=?",selectionArgs,null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

       // loading.setVisibility(View.GONE);
        if (cursor != null && cursor.moveToFirst()) {
            if(cursor.getCount()>0)
                setProgressBarState(View.GONE);
            adapter = new InnerConversationAdapter(this, cursor);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.smoothScrollToPosition(cursor.getCount());
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

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
           case android.R.id.home:
               onBackPressed();
               return true;
           default:
               return super.onOptionsItemSelected(item);
       }
   }
    public void setProgressBarState(int state)
    {
        if(loading==null)
            loading= (ProgressBar) findViewById(R.id.loading_inner_conversation);
        loading.setVisibility(state);
    }
}
