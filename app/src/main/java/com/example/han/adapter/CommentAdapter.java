package com.example.han.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.han.R;
import com.example.han.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments = new ArrayList<>();
    private long currentUserId = -1;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Comment comment);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setCurrentUserId(long userId) {
        this.currentUserId = userId;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments != null ? comments : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        comments.add(0, comment);
        notifyItemInserted(0);
    }

    public void removeComment(int position) {
        if (position >= 0 && position < comments.size()) {
            comments.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername, tvContent, tvTimestamp;
        Button btnDelete;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            btnDelete = itemView.findViewById(R.id.btnDeleteComment);

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(comments.get(getAdapterPosition()));
                }
            });
        }

        void bind(Comment comment) {
            tvUsername.setText(comment.getUsername());
            tvContent.setText(comment.getContent());
            tvTimestamp.setText(comment.getTimestamp());

            if (comment.getAvatarUrl() != null && !comment.getAvatarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(comment.getAvatarUrl())
                        .circleCrop()
                        .into(ivAvatar);
            }

            btnDelete.setVisibility(View.GONE);
        }
    }
}
