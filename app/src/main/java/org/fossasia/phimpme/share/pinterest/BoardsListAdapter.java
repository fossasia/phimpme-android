package org.fossasia.phimpme.share.pinterest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by @codedsun on 14/Oct/2019
 */

public class BoardsListAdapter extends RecyclerView.Adapter<BoardsListAdapter.ViewHolder> {

    BasicCallBack callback;

    BoardsListAdapter(BasicCallBack callback) {
        this.callback = callback;
    }

    private JSONArray boardsArray = new JSONArray();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pinterest_boards, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.tvName.setText(((JSONObject) boardsArray.get(0)).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(v -> {
            try {
                callback.callBack(Constants.SUCCESS, boardsArray.get(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return boardsArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public void addDatatoArray(JSONArray dataArray) {
        boardsArray = dataArray;
        notifyDataSetChanged();
    }


}
