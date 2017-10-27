package ua.kh.ruschess.kharkivrussianchessclub.Adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.kh.ruschess.kharkivrussianchessclub.R;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    Context context;
    OnItemClickListener clickListener;
    private ArrayList eventsList;

    public EventAdapter(Context context) {
        this.context = context;
    }

    public EventAdapter(ArrayList<HashMap<String, String>> eventsList) {
        this.eventsList = eventsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder ViewHolder, int i) {
        Map<String, String> hashmap;
        hashmap = (Map<String, String>) eventsList.get(i);

        ViewHolder.nameEvent.setText(hashmap.get("title"));
        ViewHolder.descEvent.setText(hashmap.get("content"));
        ViewHolder.start_date.setText(hashmap.get("start_date"));
        ViewHolder.end_date.setText(hashmap.get("end_date"));
        ViewHolder.locationEvent.setText(hashmap.get("address"));
    }

    @Override
    public int getItemCount() {
        return eventsList == null ? 0 : eventsList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardItemLayout;
        TextView nameEvent;
        TextView descEvent;
        TextView start_date;
        TextView end_date;
        TextView locationEvent;

        public ViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            nameEvent = (TextView) itemView.findViewById(R.id.nameEvent);
            descEvent = (TextView) itemView.findViewById(R.id.descEvent);
            start_date = (TextView) itemView.findViewById(R.id.start_date);
            end_date = (TextView) itemView.findViewById(R.id.end_date);
            locationEvent = (TextView) itemView.findViewById(R.id.locationEvent);
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