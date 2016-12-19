package com.abjlab.subsub_demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.description;

/**
 * Created by joseba on 19/12/2016.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> implements View.OnClickListener {

    private ArrayList<Post> data;
    private View.OnClickListener listener;

    public PostAdapter(ArrayList<Post> data) {
        this.data = data;
    }


    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);
        itemView.setOnClickListener(this);
        PostViewHolder viewHolder = new PostViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostAdapter.PostViewHolder holder, int position) {
        Post post = data.get(position);
        holder.bindPost(post);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View view) {
        if(listener != null){
            listener.onClick(view);
        }
    }

    public void removeItem(int pos){
        data.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, data.size());
    }

    public void clear(){
        data.clear();
    }

    public void add(Post p){
        data.add(p);
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView desc;
       // private View mainView;


        public PostViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titlePost);
            desc = (TextView) itemView.findViewById(R.id.descPost);
        }

        public void bindPost(Post post){
            title.setText(post.getTitle());
            desc.setText(post.getDesc());
        }
    }


}
