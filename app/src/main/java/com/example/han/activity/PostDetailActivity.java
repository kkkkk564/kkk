package com.example.han.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.han.R;
import com.example.han.adapter.CommentAdapter;
import com.example.han.model.ApiResponse;
import com.example.han.model.Comment;
import com.example.han.model.CommentRequest;
import com.example.han.model.LikeResult;
import com.example.han.model.LikeStatus;
import com.example.han.model.PostDetail;
import com.example.han.network.ApiService;
import com.example.han.network.RetrofitClient;
import com.example.han.network.TokenManager;
import com.example.han.util.TimeUtils;
import com.example.han.util.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    private long postId;
    private ApiService apiService;
    private TokenManager tokenManager;
    private CommentAdapter commentAdapter;

    // Views
    private Toolbar toolbar;
    private TextView tvTitle, tvAuthorName, tvContent, tvCreatedAt;
    private TextView tvLikesCount, tvCommentsCount;
    private ImageView ivCover, ivAuthorAvatar, ivLikeIcon;
    private LinearLayout llLike;
    private EditText etComment;
    private Button btnSendComment, btnDelete;
    private RecyclerView rvComments;

    private boolean isLiked = false;
    private int likesCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postId = getIntent().getLongExtra("postId", -1);
        if (postId == -1) {
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService(this);
        tokenManager = TokenManager.getInstance(this);

        initViews();
        loadPostDetail();
        loadLikeStatus();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        setSupportActionBar(toolbar);

        tvTitle = findViewById(R.id.tvTitle);
        tvAuthorName = findViewById(R.id.tvAuthorName);
        tvContent = findViewById(R.id.tvContent);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvLikesCount = findViewById(R.id.tvLikesCount);
        tvCommentsCount = findViewById(R.id.tvCommentsCount);
        ivCover = findViewById(R.id.ivCover);
        ivAuthorAvatar = findViewById(R.id.ivAuthorAvatar);
        ivLikeIcon = findViewById(R.id.ivLikeIcon);
        llLike = findViewById(R.id.llLike);
        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnDelete = findViewById(R.id.btnDelete);
        rvComments = findViewById(R.id.rvComments);

        commentAdapter = new CommentAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);

        llLike.setOnClickListener(v -> toggleLike());
        btnSendComment.setOnClickListener(v -> postComment());
        btnDelete.setOnClickListener(v -> confirmDeletePost());
    }

    private void loadPostDetail() {
        apiService.getPostDetail(postId).enqueue(new Callback<ApiResponse<PostDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostDetail>> call, Response<ApiResponse<PostDetail>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PostDetail detail = response.body().getData();
                    if (detail.getPost() != null) {
                        displayPost(detail.getPost());
                    }
                    if (detail.getComments() != null) {
                        commentAdapter.setComments(detail.getComments());
                        tvCommentsCount.setText("" + detail.getComments().size());
                    }
                } else {
                    ToastUtils.show(PostDetailActivity.this, "加载失败");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostDetail>> call, Throwable t) {
                ToastUtils.show(PostDetailActivity.this, R.string.network_error);
            }
        });
    }

    private void displayPost(com.example.han.model.Post post) {
        tvTitle.setText(post.getTitle());
        tvAuthorName.setText(post.getAuthorName());
        tvContent.setText(post.getContent());
        tvCreatedAt.setText(TimeUtils.formatTime(post.getCreatedAt()));
        likesCount = post.getLikesCount();
        tvLikesCount.setText("" + likesCount);

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            ivCover.setVisibility(View.VISIBLE);
            Glide.with(this).load(post.getImageUrl()).centerCrop().into(ivCover);
        }

        if (post.getAuthorAvatar() != null && !post.getAuthorAvatar().isEmpty()) {
            Glide.with(this).load(post.getAuthorAvatar()).circleCrop().into(ivAuthorAvatar);
        }

        // Show delete button if current user is the author
        if (tokenManager.getUserName().equals(post.getAuthorName())) {
            btnDelete.setVisibility(View.VISIBLE);
        }
    }

    private void loadLikeStatus() {
        apiService.checkLikeStatus(postId).enqueue(new Callback<ApiResponse<LikeStatus>>() {
            @Override
            public void onResponse(Call<ApiResponse<LikeStatus>> call, Response<ApiResponse<LikeStatus>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    LikeStatus status = response.body().getData();
                    isLiked = status.isLiked();
                    likesCount = status.getLikesCount();
                    updateLikeUI();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LikeStatus>> call, Throwable t) {
                // Ignore - default to not liked
            }
        });
    }

    private void toggleLike() {
        if (isLiked) {
            apiService.unlikePost(postId).enqueue(new Callback<ApiResponse<LikeResult>>() {
                @Override
                public void onResponse(Call<ApiResponse<LikeResult>> call, Response<ApiResponse<LikeResult>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        isLiked = false;
                        likesCount = response.body().getData().getLikesCount();
                        updateLikeUI();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<LikeResult>> call, Throwable t) {
                    ToastUtils.show(PostDetailActivity.this, R.string.network_error);
                }
            });
        } else {
            apiService.likePost(postId).enqueue(new Callback<ApiResponse<LikeResult>>() {
                @Override
                public void onResponse(Call<ApiResponse<LikeResult>> call, Response<ApiResponse<LikeResult>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        isLiked = true;
                        likesCount = response.body().getData().getLikesCount();
                        updateLikeUI();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<LikeResult>> call, Throwable t) {
                    ToastUtils.show(PostDetailActivity.this, R.string.network_error);
                }
            });
        }
    }

    private void updateLikeUI() {
        tvLikesCount.setText("" + likesCount);
        ivLikeIcon.setImageResource(isLiked
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
    }

    private void postComment() {
        String content = etComment.getText().toString().trim();
        if (content.isEmpty()) {
            ToastUtils.show(this, R.string.please_input_comment);
            return;
        }

        apiService.createComment(new CommentRequest(postId, content))
                .enqueue(new Callback<ApiResponse<Comment>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Comment>> call, Response<ApiResponse<Comment>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Comment comment = response.body().getData();
                            commentAdapter.addComment(comment);
                            etComment.setText("");
                            ToastUtils.show(PostDetailActivity.this, R.string.comment_success);
                        } else {
                            ToastUtils.show(PostDetailActivity.this, R.string.comment_failed);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Comment>> call, Throwable t) {
                        ToastUtils.show(PostDetailActivity.this, R.string.network_error);
                    }
                });
    }

    private void confirmDeletePost() {
        new android.app.AlertDialog.Builder(this)
                .setMessage(R.string.delete_post_confirm)
                .setPositiveButton(R.string.delete, (dialog, which) -> deletePost())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deletePost() {
        apiService.deletePost(postId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ToastUtils.show(PostDetailActivity.this, R.string.delete_success);
                    finish();
                } else {
                    ToastUtils.show(PostDetailActivity.this, R.string.delete_failed);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                ToastUtils.show(PostDetailActivity.this, R.string.network_error);
            }
        });
    }
}
