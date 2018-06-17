/**
 * 教室Activity---聊天室
 */
package com.horizonshd.www.barrageclassstudent;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessageList;

    public MessageAdapter(List<Message> messageList){
        mMessageList = messageList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout middleLayout;
        LinearLayout rightLayout;

        TextView leftMessage;
        TextView middleMessage;
        TextView rightMessage;

        TextView leftName;
        TextView rightName;

        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            middleLayout = (LinearLayout) view.findViewById(R.id.middle_layout);
            rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            leftMessage = (TextView) view.findViewById(R.id.left_message);
            rightMessage = (TextView) view.findViewById(R.id.right_message);
            middleMessage = (TextView) view.findViewById(R.id.middle_message);

            leftName = (TextView) view.findViewById(R.id.left_name);
            rightName = (TextView) view.findViewById(R.id.right_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        if(message.getType() == Message.TYPE_RECEIVED){
            holder.leftName.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.middleLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.rightName.setVisibility(View.GONE);
            holder.leftName.setText(message.getFrom());
            holder.leftMessage.setText(message.getContent());
        }else if(message.getType() == Message.TYPE_SENT){
            holder.rightName.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.middleLayout.setVisibility(View.GONE);
            holder.leftName.setVisibility(View.GONE);
            holder.rightName.setText(message.getFrom());
            holder.rightMessage.setText(message.getContent());
        }else if(message.getType() == Message.TYPE_MENTION){
            holder.middleLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftName.setVisibility(View.GONE);
            holder.rightName.setVisibility(View.GONE);
            holder.middleMessage.setText(message.getContent());
        }
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}