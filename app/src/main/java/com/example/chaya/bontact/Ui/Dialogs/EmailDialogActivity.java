package com.example.chaya.bontact.Ui.Dialogs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.Helpers.SendResponseHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;

public class EmailDialogActivity extends AppCompatActivity {
    EditText addressee_edit_text;
    EditText from_edit_text;
    EditText subject_edit_text;
    EditText content_edit_text;
    int id_surfer;
    String email_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        id_surfer = 0;
        if (args != null) {
            id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);
            email_address = args.getString(Contract.Conversation.COLUMN_EMAIL);
        }
        if (id_surfer == 0) {
            Toast.makeText(EmailDialogActivity.this, "Not allowed", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        setContentView(R.layout.activity_email_dialog);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.exit_icon);
        setTitle("Email");

        addressee_edit_text = (EditText) findViewById(R.id.edit_text_addressee);
        from_edit_text = (EditText) findViewById(R.id.edit_text_from);
        subject_edit_text = (EditText) findViewById(R.id.edit_text_subject);
        content_edit_text = (EditText) findViewById(R.id.edit_text_content);
        if (email_address != null)
            addressee_edit_text.setText(email_address);
        if (AgentDataManager.getAgentInstanse().getRep().username != null)
            from_edit_text.setText(AgentDataManager.getAgentInstanse().getRep().username);


    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.email_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.exit_action:
                onBackPressed();
                return true;
            case R.id.send_action:
                sendEmail();
                return true;
        }
        return true;
    }

    public void sendEmail() {
        onBackPressed();
        //BACKGROUND
        SendResponseHelper sendResponseHelper = new SendResponseHelper();
        sendResponseHelper.sendEmail(this, content_edit_text.getText().toString(), id_surfer);
    }
}
