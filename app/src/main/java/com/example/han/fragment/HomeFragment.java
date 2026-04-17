package com.example.han.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.han.R;
import com.example.han.activity.PostDetailActivity;
import com.example.han.adapter.PostAdapter;
import com.example.han.model.ApiResponse;
import com.example.han.model.Post;
import com.example.han.model.PostPage;
import com.example.han.network.ApiService;
import com.example.han.network.RetrofitClient;
import com.example.han.util.Constants;
import com.example.han.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private ApiService apiService;
    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvPosts;
    private EditText etSearch;
    private ImageView ivSearchClear;
    private LinearLayout shimmerContainer;

    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMore = true;
    private String searchKeyword = "";

    private ActivityResultLauncher<Intent> postDetailLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService(requireContext());

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        rvPosts = view.findViewById(R.id.rvPosts);
        etSearch = view.findViewById(R.id.etSearch);
        ivSearchClear = view.findViewById(R.id.ivSearchClear);
        shimmerContainer = view.findViewById(R.id.shimmerContainer);

        postAdapter = new PostAdapter();
        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPosts.setAdapter(postAdapter);

        postDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        boolean deleted = result.getData().getBooleanExtra("deleted", false);
                        long postId = result.getData().getLongExtra("postId", -1);
                        List<Post> posts = postAdapter.getPosts();
                        if (deleted) {
                            for (int i = 0; i < posts.size(); i++) {
                                if (posts.get(i).getId() == postId) {
                                    posts.remove(i);
                                    postAdapter.notifyItemRemoved(i);
                                    break;
                                }
                            }
                        } else {
                            int likesCount = result.getData().getIntExtra("likesCount", -1);
                            int commentsCount = result.getData().getIntExtra("commentsCount", -1);
                            for (int i = 0; i < posts.size(); i++) {
                                if (posts.get(i).getId() == postId) {
                                    if (likesCount >= 0) {
                                        posts.get(i).setLikesCount(likesCount);
                                    }
                                    if (commentsCount >= 0) {
                                        posts.get(i).setCommentsCount(commentsCount);
                                    }
                                    postAdapter.notifyItemChanged(i);
                                    break;
                                }
                            }
                        }
                    }
                });

        postAdapter.setOnItemClickListener(post -> {
            Intent intent = new Intent(requireContext(), PostDetailActivity.class);
            intent.putExtra("postId", post.getId());
            postDetailLauncher.launch(intent);
        });

        // Setup search
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            searchKeyword = v.getText().toString().trim();
            ivSearchClear.setVisibility(searchKeyword.isEmpty() ? View.GONE : View.VISIBLE);
            refreshPosts();
            return true;
        });

        ivSearchClear.setOnClickListener(v -> {
            etSearch.setText("");
            searchKeyword = "";
            ivSearchClear.setVisibility(View.GONE);
            refreshPosts();
        });

        swipeRefresh.setOnRefreshListener(this::refreshPosts);
        swipeRefresh.setColorSchemeResources(R.color.primary);

        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int lastVisible = layoutManager.findLastVisibleItemPosition();
                    int total = layoutManager.getItemCount();
                    if (!isLoading && hasMore && lastVisible >= total - 3 && dy > 0) {
                        loadMore();
                    }
                }
            }
        });

        refreshPosts();
    }

    private void refreshPosts() {
        currentPage = 0;
        hasMore = true;
        postAdapter.clear();
        showShimmer(true);
        loadPosts();
    }

    private void loadMore() {
        if (isLoading || !hasMore) return;
        currentPage++;
        loadPosts();
    }

    private void showShimmer(boolean show) {
        shimmerContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        rvPosts.setVisibility(show ? View.GONE : View.VISIBLE);
        if (show) {
            startShimmerAnimation();
        }
    }

    private void startShimmerAnimation() {
        // Simple alpha animation for shimmer effect
        for (int i = 0; i < shimmerContainer.getChildCount(); i++) {
            View child = shimmerContainer.getChildAt(i);
            animateShimmer(child, 0);
        }
    }

    private void animateShimmer(View view, int delay) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (view == null || shimmerContainer.getVisibility() != View.VISIBLE) return;
            view.setAlpha(0.3f);
            view.animate()
                .alpha(1.0f)
                .setDuration(800)
                .withEndAction(() -> {
                    if (shimmerContainer.getVisibility() == View.VISIBLE) {
                        animateShimmer(view, 0);
                    }
                })
                .start();
        }, delay);
    }

    private void loadPosts() {
        isLoading = true;
        if (!searchKeyword.isEmpty()) {
            apiService.searchPosts(searchKeyword, Constants.PostType.ALL, currentPage, Constants.PAGE_SIZE, Constants.SortType.CREATED_AT)
                    .enqueue(new retrofit2.Callback<ApiResponse<PostPage>>() {
                        @Override
                        public void onResponse(retrofit2.Call<ApiResponse<PostPage>> call,
                                               retrofit2.Response<ApiResponse<PostPage>> response) {
                            isLoading = false;
                            swipeRefresh.setRefreshing(false);
                            showShimmer(false);
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                PostPage page = response.body().getData();
                                List<Post> posts = page.getContent();
                                if (posts != null && !posts.isEmpty()) {
                                    if (currentPage == 0) {
                                        postAdapter.setPosts(posts);
                                    } else {
                                        postAdapter.addPosts(posts);
                                    }
                                    hasMore = page.getCurrentPage() < page.getTotalPages() - 1;
                                } else {
                                    hasMore = false;
                                    if (currentPage == 0) {
                                        ToastUtils.show(requireContext(), R.string.search_empty);
                                    }
                                }
                            } else {
                                ToastUtils.show(requireContext(), R.string.load_failed);
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ApiResponse<PostPage>> call, Throwable t) {
                            isLoading = false;
                            swipeRefresh.setRefreshing(false);
                            showShimmer(false);
                            ToastUtils.show(requireContext(), R.string.network_error);
                        }
                    });
        } else {
            apiService.getPosts(Constants.PostType.ALL, currentPage, Constants.PAGE_SIZE, Constants.SortType.CREATED_AT)
                    .enqueue(new retrofit2.Callback<ApiResponse<PostPage>>() {
                        @Override
                        public void onResponse(retrofit2.Call<ApiResponse<PostPage>> call,
                                               retrofit2.Response<ApiResponse<PostPage>> response) {
                            isLoading = false;
                            swipeRefresh.setRefreshing(false);
                            showShimmer(false);
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                PostPage page = response.body().getData();
                                List<Post> posts = page.getContent();
                                if (posts != null && !posts.isEmpty()) {
                                    if (currentPage == 0) {
                                        postAdapter.setPosts(posts);
                                    } else {
                                        postAdapter.addPosts(posts);
                                    }
                                    hasMore = page.getCurrentPage() < page.getTotalPages() - 1;
                                } else {
                                    hasMore = false;
                                }
                            } else {
                                ToastUtils.show(requireContext(), R.string.load_failed);
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ApiResponse<PostPage>> call, Throwable t) {
                            isLoading = false;
                            swipeRefresh.setRefreshing(false);
                            showShimmer(false);
                            ToastUtils.show(requireContext(), R.string.network_error);
                        }
                    });
        }
    }
}
