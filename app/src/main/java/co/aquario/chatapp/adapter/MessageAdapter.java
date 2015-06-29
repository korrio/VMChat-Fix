package co.aquario.chatapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import co.aquario.chatapp.R;
import co.aquario.chatapp.model.Message;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;
    private int[] mUsernameColors;
    private Context mContext;

    public MessageAdapter(Context context, List<Message> messages) {
        mContext = context;
        mMessages = messages;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            //left
        case Message.TYPE_RIGHT:
            layout = R.layout.item_right;
            break;
        //right
        case Message.TYPE_LEFT:
            layout = R.layout.item_left;
            break;
        //log
        case Message.TYPE_LOG:
                layout = R.layout.item_log;
                break;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        try {
            viewHolder.setMessage(message.getMessageType(),message.getMessage(),message.getData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        viewHolder.setUsername(message.getUsername());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private TextView mMessageView;
        private ImageView mTattooView;
        private ImageView mImageView;

        private JSONObject mData;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            //stub = (ViewStub) itemView.findViewById(R.id.layout_stub);


        }



        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setMessage(int messageType,String message,String data) throws JSONException {
            if (null == mMessageView) return;
            switch (messageType) {
                case 0:

                    //mMessageView = (TextView) inflated.findViewById(R.id.message);
                    mMessageView.setText(message);
                    break;
                case 1:
                    mData = new JSONObject(data);

                    mMessageView.setText(message);

                    //mTattooView = (ImageView) inflated.findViewById(R.id.tattoo);
                    //Picasso.with(mContext).load(mData.optString("tattooUrl")).into(mTattooView);
                    //mMessageView.setText(message);
                    break;
                case 2:
                    mData = new JSONObject(data);

                    mMessageView.setText(message);

                    //mImageView = (ImageView) inflated.findViewById(R.id.image);
                    //Picasso.with(mContext).load("https://chat.vdomax.com:1313"+mData.optString("url")).into(mTattooView);

                    break;



            }

        }

        private int getUsernameColor(String username) {
            int hash = 7;
            for (int i = 0, len = username.length(); i < len; i++) {
                hash = username.codePointAt(i) + (hash << 5) - hash;
            }
            int index = Math.abs(hash % mUsernameColors.length);
            return mUsernameColors[index];
        }
    }
}
