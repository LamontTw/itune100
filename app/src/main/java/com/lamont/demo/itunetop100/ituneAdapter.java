package com.lamont.demo.itunetop100;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;

import com.lamont.demo.itunetop100.database.model.ITuneItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ituneAdapter extends RecyclerView.Adapter<ituneAdapter.ViewHolder> {

    private Context mContext;
    private List<ITuneItem> iTuneItemList;
    public ituneAdapter(Context context, List<ITuneItem> iTuneItemList) {
        this.mContext = context;
        this.iTuneItemList = iTuneItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.itune_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindTo(iTuneItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return iTuneItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivArtwork;
        TextView tvRank;
        TextView tvSongName;
        TextView tvArtistName;
        public ViewHolder(View itemView) {
            super(itemView);
            ivArtwork = (ImageView) itemView.findViewById(R.id.artwork);
            tvRank = (TextView) itemView.findViewById(R.id.rank);
            tvSongName = (TextView) itemView.findViewById(R.id.song_name);
            tvArtistName = (TextView) itemView.findViewById(R.id.artist_name);
        }

        public void bindTo(ITuneItem iTuneItem) {
            tvRank.setText("" + iTuneItem.getRank());
            tvSongName.setText("" + iTuneItem.getSongName());
            tvArtistName.setText("" + iTuneItem.getArtistName());

            Picasso.get()
                    .load(iTuneItem.getArtworkUrl100())
                    .placeholder(R.drawable.icon_refresh)
                    .into(ivArtwork);
        }
    }


}