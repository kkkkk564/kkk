package com.example.han.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.han.R;
import com.example.han.model.Post;
import com.example.han.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts != null ? posts : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addPosts(List<Post> newPosts) {
        if (newPosts != null) {
            int startPos = posts.size();
            posts.addAll(newPosts);
            notifyItemRangeInserted(startPos, newPosts.size());
        }
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void updatePost(int position, int likesCount, int commentsCount) {
        if (position >= 0 && position < posts.size()) {
            Post post = posts.get(position);
            post.setLikesCount(likesCount);
            post.setCommentsCount(commentsCount);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover, ivAuthorAvatar;
        TextView tvTitle, tvDescription, tvAuthorName, tvLikesCount, tvCommentsCount, tvCreatedAt;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            ivAuthorAvatar = itemView.findViewById(R.id.ivAuthorAvatar);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);
            tvCommentsCount = itemView.findViewById(R.id.tvCommentsCount);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(posts.get(getAdapterPosition()));
                }
            });

            // Press animation
            itemView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        break;
                }
                return false;
            });
        }

        void bind(Post post) {
            tvTitle.setText(post.getTitle());
            tvDescription.setText(post.getDescription());
            tvAuthorName.setText(post.getAuthorName());
            tvLikesCount.setText("" + post.getLikesCount());
            tvCommentsCount.setText("" + post.getCommentsCount());
            tvCreatedAt.setText(TimeUtils.formatRelativeTime(post.getCreatedAt()));

            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                ivCover.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(post.getImageUrl())
                        .centerCrop()
                        .into(ivCover);
            } else {
                ivCover.setVisibility(View.GONE);
            }

            if (post.getAuthorAvatar() != null && !post.getAuthorAvatar().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(post.getAuthorAvatar())
                        .circleCrop()
                        .into(ivAuthorAvatar);
            }
        }
    }
}
