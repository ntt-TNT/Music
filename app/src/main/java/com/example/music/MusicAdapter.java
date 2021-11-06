package com.example.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private List<SongModel> songsList = new ArrayList<>();
    private SongModel songModel;
    private Integer position=0;
    private OnClickListener onClickListener;

    public MusicAdapter(List<SongModel> songsList){
        this.songsList = songsList;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs_list,parent,false);

        return new MusicViewHolder(view);
    }

    /**
     * 定义点击事件回调接口.
     */
    interface OnClickListener {
        /**
         * 点击事件.
         */
        void onClick(SongModel songModel, int position);

    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        this.position = (Integer) position;
        songModel = songsList.get(position);
        holder.tvSongName.setText(songModel.getName());
        final View itemView = holder.itemView;
        CardView cardView = (CardView) itemView.findViewById(R.id.music_card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置单击事件并回调给页面
                if (onClickListener != null) {
                    onClickListener.onClick(songModel, holder.getLayoutPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }





    class MusicViewHolder extends RecyclerView.ViewHolder{
        TextView tvSongName;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongName = itemView.findViewById(R.id.tvSongName);
        }
    }
}
