package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.Visitor;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.InnerConversationActivity;
import com.example.chaya.bontact.Ui.Activities.MenuActivity;
import com.mikepenz.iconics.typeface.ITypeface;

import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.security.Timestamp;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chaya on 8/1/2016.
 */
public class OnlineVisitorsAdapter extends RecyclerView.Adapter<OnlineVisitorsAdapter.OnlineVisitorsHolder> {

    List<Visitor> visitorsList;
    Context context;


    public OnlineVisitorsAdapter(Context context) {
        this.context = context;
        visitorsList = VisitorsDataManager.visitorsList;
    }


    @Override
    public OnlineVisitorsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.online_visitors_item, null);
        return new OnlineVisitorsHolder(view);
    }

    @Override
    public void onBindViewHolder(OnlineVisitorsHolder holder, int position) {
        if (visitorsList == null)
            return;
        final Visitor current_visitor = visitorsList.get(position);
        if (current_visitor != null) {
            holder.time_visit.setText(DateTimeHelper.getDiffToOnlineVisitors(current_visitor.timeConnect));
            holder.title.setText(current_visitor.title);
            holder.flag.setText(current_visitor.country);
            holder.displayName.setText(String.valueOf(current_visitor.idSurfer));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), InnerConversationActivity.class);

                    for (Visitor visitor : VisitorsDataManager.getVisitorsList()) {
                        if (visitor.idSurfer == current_visitor.idSurfer) {
                            int idSurfer = visitor.idSurfer;
                            Bundle b = new Bundle();
                            b.putInt(Contract.InnerConversation.COLUMN_ID_SURFUR,idSurfer ); //Your id
                            intent.putExtras(b);
                            break;
                        }
                    }
                    v.getContext().startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        if (visitorsList != null)
            return visitorsList.size();
        return 0;
    }

    class OnlineVisitorsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView time_visit, flag, title, displayName;

        public OnlineVisitorsHolder(View itemView) {
            super(itemView);
            time_visit = (TextView) itemView.findViewById(R.id.time_visit);
            flag = (TextView) itemView.findViewById(R.id.flag);
            title = (TextView) itemView.findViewById(R.id.title_page);
            displayName = (TextView) itemView.findViewById(R.id.displayName);

        }

        @Override
        public void onClick(View v) {


        }
    }
}