package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Helpers.AvatarHelper;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.DatesHelper;
import com.example.chaya.bontact.Models.Visitor;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.InnerConversationActivity;

/**
 * Created by chaya on 8/1/2016.
 */
public class OnlineVisitorsAdapter extends RecyclerView.Adapter<OnlineVisitorsAdapter.OnlineVisitorsHolder> {

    Context context;

    public OnlineVisitorsAdapter(Context context) {
        this.context = context;
    }


    @Override
    public OnlineVisitorsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.online_visitors_item, null);
        return new OnlineVisitorsHolder(view);
    }

    @Override
    public void onBindViewHolder(OnlineVisitorsHolder holder, int position) {
        final Visitor current_visitor = VisitorsDataManager.getVisitorsList().get(position);
        if (current_visitor != null) {
            Log.d("bind", current_visitor.isNew ? "true" : "false");

            if (current_visitor.timeConnect != null)
                holder.connect_time.setText("since " + DatesHelper.getDateToDisplayInbox(context, current_visitor.timeConnect));
            holder.page_title.setText(current_visitor.title);
            AvatarHelper.setAvatar(context, current_visitor.avatar, current_visitor.displayName, holder.avatar);
            holder.browser_icon.setImageResource(VisitorsDataManager.getBrowserIcon(current_visitor.browseType));
            holder.displayName.setText(current_visitor.displayName);
            holder.country_flag.setImageResource(context.getResources().getIdentifier("country_" + current_visitor.country.toLowerCase(), "drawable", context.getPackageName()));
            if (current_visitor.isNew)
                holder.invite_btn.setVisibility(View.VISIBLE);
            else
                holder.invite_btn.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), InnerConversationActivity.class);
                    for (Visitor visitor : VisitorsDataManager.getVisitorsList()) {
                        if (visitor.idSurfer == current_visitor.idSurfer) {
                            Bundle b = new Bundle();
                            b.putInt(Contract.InnerConversation.COLUMN_ID_SURFUR, visitor.idSurfer); //Your id
                            intent.putExtras(b);
                            break;
                        }
                    }
                    if (current_visitor.idSurfer != 0)
                        v.getContext().startActivity(intent);
                    //  else
                    //   VisitorsDataManager.removeVisitorFromList(context, current_visitor);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (VisitorsDataManager.getVisitorsList() != null)
            return VisitorsDataManager.getVisitorsList().size();
        return 0;
    }

    class OnlineVisitorsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView connect_time, page_title, displayName;
        ImageView avatar, browser_icon, country_flag, invite_btn;

        public OnlineVisitorsHolder(View itemView) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            connect_time = (TextView) itemView.findViewById(R.id.connect_time);
            browser_icon = (ImageView) itemView.findViewById(R.id.browser_icon);
            page_title = (TextView) itemView.findViewById(R.id.page_title);
            displayName = (TextView) itemView.findViewById(R.id.displayName);
            country_flag = (ImageView) itemView.findViewById(R.id.country_flag);
            invite_btn = (ImageView) itemView.findViewById(R.id.invite_btn);

        }

        @Override
        public void onClick(View v) {


        }
    }
}