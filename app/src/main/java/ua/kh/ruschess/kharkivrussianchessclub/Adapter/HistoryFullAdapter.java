package ua.kh.ruschess.kharkivrussianchessclub.Adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.kh.ruschess.kharkivrussianchessclub.R;

public class HistoryFullAdapter extends RecyclerView.Adapter<HistoryFullAdapter.ViewHolder> {
    Context context;
    OnItemClickListener clickListener;
    private ArrayList movesList;

    public HistoryFullAdapter(Context context) {
        this.context = context;
    }

    public HistoryFullAdapter(ArrayList<HashMap<String, String>> movesList) {
        this.movesList = movesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_full_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder ViewHolder, int i) {
        Map<String, String> hashmap;
        hashmap = (Map<String, String>) movesList.get(i);

        ViewHolder.dateFigure.setText(hashmap.get("time"));
        ViewHolder.imageFigure.setImageResource(Integer.parseInt(hashmap.get("icon")));
    }

    @Override
    public int getItemCount() {
        return movesList == null ? 0 : movesList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardItemLayout;
        TextView dateFigure;
        ImageView imageFigure;

        public ViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            dateFigure = (TextView) itemView.findViewById(R.id.dateFigure);
            imageFigure = (ImageView) itemView.findViewById(R.id.imageFigure);
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