package com.example.han.network;

import com.example.han.model.ApiResponse;
import com.example.han.model.AuthData;
import com.example.han.model.Comment;
import com.example.han.model.CommentRequest;
import com.example.han.model.LikeResult;
import com.example.han.model.LikeStatus;
import com.example.han.model.LoginRequest;
import com.example.han.model.Post;
import com.example.han.model.PostDetail;
import com.example.han.model.PostItem;
import com.example.han.model.PostPage;
import com.example.han.model.PostRequest;
import com.example.han.model.ProfileUpdate;
import com.example.han.model.RefreshData;
import com.example.han.model.RefreshRequest;
import com.example.han.model.RegisterRequest;
import com.example.han.model.UploadResult;
import com.example.han.model.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ========== 用户模块 ==========

    @POST("api/user/register")
    Call<ApiResponse<AuthData>> register(@Body RegisterRequest request);

    @POST("api/user/login")
    Call<ApiResponse<AuthData>> login(@Body LoginRequest request);

    @POST("api/user/refresh")
    Call<ApiResponse<RefreshData>> refreshToken(@Body RefreshRequest request);

    @GET("api/user/profile")
    Call<ApiResponse<User>> getProfile();

    @PUT("api/user/profile")
    Call<ApiResponse<User>> updateProfile(@Body ProfileUpdate update);

    @POST("api/user/logout")
    Call<ApiResponse<Void>> logout();

    // ========== 帖子模块 ==========

    @GET("api/posts")
    Call<ApiResponse<PostPage>> getPosts(
            @Query("type") String type,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    @GET("api/posts/{id}")
    Call<ApiResponse<PostDetail>> getPostDetail(@Path("id") long id);

    @POST("api/posts")
    Call<ApiResponse<PostItem>> createPost(@Body PostRequest request);

    @DELETE("api/posts/{id}")
    Call<ApiResponse<Void>> deletePost(@Path("id") long id);

    @GET("api/posts/my")
    Call<ApiResponse<PostPage>> getMyPosts(
            @Query("page") int page,
            @Query("size") int size
    );

    // ========== 评论模块 ==========

    @POST("api/comments")
    Call<ApiResponse<Comment>> createComment(@Body CommentRequest request);

    @DELETE("api/comments/{id}")
    Call<ApiResponse<Void>> deleteComment(@Path("id") long id);

    // ========== 点赞模块 ==========

    @POST("api/likes/{postId}")
    Call<ApiResponse<LikeResult>> likePost(@Path("postId") long postId);

    @DELETE("api/likes/{postId}")
    Call<ApiResponse<LikeResult>> unlikePost(@Path("postId") long postId);

    @GET("api/likes/check/{postId}")
    Call<ApiResponse<LikeStatus>> checkLikeStatus(@Path("postId") long postId);

    // ========== 文件上传 ==========

    @Multipart
    @POST("api/upload/image")
    Call<ApiResponse<UploadResult>> uploadImage(
            @Part MultipartBody.Part file,
            @Part("type") String type
    );
}
