package ua.kh.ruschess.kharkivrussianchessclub.Adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.kh.ruschess.kharkivrussianchessclub.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    Context context;
    OnItemClickListener clickListener;
    private ArrayList gamesList;

    public HistoryAdapter(Context context) {
        this.context = context;
    }

    public HistoryAdapter(ArrayList<HashMap<String, String>> gamesList) {
        this.gamesList = gamesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder ViewHolder, int i) {
        Map<String, String> hashmap;
        hashmap = (Map<String, String>) gamesList.get(i);

        ViewHolder.name.setText(hashmap.get("name"));
        ViewHolder.dateStart.setText(hashmap.get("time_start"));
        ViewHolder.dateEnd.setText(hashmap.get("time_end"));
        ViewHolder.white.setText(hashmap.get("white_figures"));
        ViewHolder.brown.setText(hashmap.get("brown_figures"));
        ViewHolder.black.setText(hashmap.get("black_figures"));
        ViewHolder.total.setText(hashmap.get("total_figures"));
    }

    @Override
    public int getItemCount() {
        return gamesList == null ? 0 : gamesList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardItemLayout;
        TextView name;
        TextView dateStart;
        TextView dateEnd;
        TextView white;
        TextView brown;
        TextView black;
        TextView total;

        public ViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            name = (TextView) itemView.findViewById(R.id.nameGame);
            dateStart = (TextView) itemView.findViewById(R.id.startGame);
            dateEnd = (TextView) itemView.findViewById(R.id.endGame);
            white = (TextView) itemView.findViewById(R.id.countWhite);
            brown = (TextView) itemView.findViewById(R.id.countBrown);
            black = (TextView) itemView.findViewById(R.id.countBlack);
            total = (TextView) itemView.findViewById(R.id.countTotal);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}