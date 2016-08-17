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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Helpers.AvatarHelper;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.CircleTransform;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.Visitor;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.InnerConversationActivity;
import com.squareup.picasso.Picasso;

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
            if (current_visitor.timeConnect != null)
                holder.connect_time.setText(DateTimeHelper.getDateToInbox(current_visitor.timeConnect, context));
            holder.page_title.setText(current_visitor.title);
            setAvatar(current_visitor, holder.avatar);
            //holder.avatar.setImageResource(AvatarHelper.getNextAvatar());
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
                            int idSurfer = visitor.idSurfer;
                            Bundle b = new Bundle();
                            b.putInt(Contract.InnerConversation.COLUMN_ID_SURFUR, idSurfer); //Your id
                            intent.putExtras(b);
                            break;
                        }
                    }
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public void setAvatar(Visitor visitor, ImageView avatarView) {
        //set default
        avatarView.setBackground(context.getResources().getDrawable(R.drawable.avatar_bg));
        avatarView.setImageResource(R.drawable.default_avatar);
        if (visitor != null) {
         /*   if (conversation.avatar != null) {//has picture
                Picasso.with(context)
                        .load(conversation.avatar)
                        .transform(new CircleTransform())
                        .into(avatarView);
            } else {//maybe letters*/
            String letter = null;
            letter = visitor.displayName != null && !visitor.displayName.startsWith("#") ? visitor.displayName.substring(0, 1) : null;
            if (letter != null)
                avatarView.setImageDrawable(TextDrawable.builder()
                        .buildRound(letter,ColorGenerator.MATERIAL.getRandomColor()));
            //context.getResources().getColor(R.color.orange_light)));
        }
    }


    @Override
    public int getItemCount() {
        if (visitorsList != null)
            return visitorsList.size();
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