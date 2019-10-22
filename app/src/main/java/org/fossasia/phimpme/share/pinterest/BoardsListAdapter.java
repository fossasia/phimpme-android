package org.fossasia.phimpme.share.pinterest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;

/** Created by @codedsun on 14/Oct/2019 */
public class BoardsListAdapter extends RecyclerView.Adapter<BoardsListAdapter.ViewHolder> {

  BasicCallBack callback;

  BoardsListAdapter(BasicCallBack callback) {
    this.callback = callback;
  }

  private ArrayList<PinterestBoardsResp.Data> boardsArray = new ArrayList<>();

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_pinterest_boards, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.tvName.setText((boardsArray.get(position)).getName());
    holder.itemView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            callback.callBack(Constants.SUCCESS, boardsArray.get(position));
          }
        });
  }

  @Override
  public int getItemCount() {
    return boardsArray.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvName = itemView.findViewById(R.id.tv_name);
    }
  }

  public void addDatatoArray(ArrayList<PinterestBoardsResp.Data> dataArray) {
    boardsArray = dataArray;
    notifyDataSetChanged();
  }
}
