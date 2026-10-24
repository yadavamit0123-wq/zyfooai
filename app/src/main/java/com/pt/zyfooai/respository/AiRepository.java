package com.pt.zyfooai.respository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pt.zyfooai.api.ApiClient;
import com.pt.zyfooai.api.ApiService;
import com.pt.zyfooai.model.AiChatResponse;
import com.pt.zyfooai.model.AiConfig;
import com.pt.zyfooai.model.AiConfigResponse;
import com.pt.zyfooai.model.AiConversationDetailResponse;
import com.pt.zyfooai.model.AiConversationListResponse;
import com.pt.zyfooai.model.AiImageResponse;
import com.pt.zyfooai.model.AiProcessResponse;
import com.pt.zyfooai.utils.AiPrefs;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class AiRepository {

    private static final String TAG = "AiRepository";
    private final ApiService api = ApiClient.getApiDataService();

    public LiveData<AiConfig> fetchConfig(Context context) {
        MutableLiveData<AiConfig> live = new MutableLiveData<>();
        String userId = userId(context);
        api.getAiConfig(userId).enqueue(new Callback<AiConfigResponse>() {
            @Override
            public void onResponse(Call<AiConfigResponse> call, Response<AiConfigResponse> response) {
                AiConfig cfg = null;
                if (response.isSuccessful() && response.body() != null) {
                    cfg = response.body().resolved();
                }
                if (cfg != null) {
                    AiPrefs.save(context, cfg);
                }
                live.setValue(cfg != null ? cfg : AiPrefs.get(context));
            }

            @Override
            public void onFailure(Call<AiConfigResponse> call, Throwable t) {
                Log.d(TAG, "fetchConfig: " + t.getMessage());
                live.setValue(AiPrefs.get(context));
            }
        });
        return live;
    }

    public LiveData<AiChatResponse> chat(
            Context context,
            String provider,
            String conversationId,
            String message,
            Uri imageUri
    ) {
        MutableLiveData<AiChatResponse> live = new MutableLiveData<>();
        String userId = userId(context);
        Map<String, RequestBody> fields = new HashMap<>();
        fields.put("user_id", textBody(userId));
        fields.put("provider", textBody(provider != null ? provider : ""));
        fields.put("message", textBody(message != null ? message : ""));
        if (conversationId != null && !conversationId.isEmpty()) {
            fields.put("conversation_id", textBody(conversationId));
        }

        Callback<AiChatResponse> cb = new Callback<AiChatResponse>() {
            @Override
            public void onResponse(Call<AiChatResponse> call, Response<AiChatResponse> response) {
                AiChatResponse body = parseChat(response);
                if (body != null && body.resolvedQuota() != null) {
                    AiPrefs.applyQuota(context, body.resolvedQuota());
                }
                live.setValue(body);
            }

            @Override
            public void onFailure(Call<AiChatResponse> call, Throwable t) {
                AiChatResponse err = new AiChatResponse();
                err.successRaw = false;
                err.message = t.getMessage() != null ? t.getMessage() : "Network error";
                live.setValue(err);
            }
        };
        MultipartBody.Part imagePart = imageUri != null ? uriPart(context, imageUri, "image") : null;
        if (imagePart != null) {
            api.aiChatWithImage(fields, imagePart).enqueue(cb);
        } else {
            api.aiChat(fields).enqueue(cb);
        }
        return live;
    }

    public LiveData<AiImageResponse> createImage(
            Context context,
            String provider,
            String prompt,
            String conversationId
    ) {
        MutableLiveData<AiImageResponse> live = new MutableLiveData<>();
        Map<String, String> fields = new HashMap<>();
        fields.put("user_id", userId(context));
        fields.put("provider", provider != null ? provider : "");
        fields.put("prompt", prompt != null ? prompt : "");
        if (conversationId != null && !conversationId.isEmpty()) {
            fields.put("conversation_id", conversationId);
        }
        api.aiCreateImage(fields).enqueue(new Callback<AiImageResponse>() {
            @Override
            public void onResponse(Call<AiImageResponse> call, Response<AiImageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AiImageResponse body = response.body();
                    if (body.resolvedQuota() != null) {
                        AiPrefs.applyQuota(context, body.resolvedQuota());
                    }
                    live.setValue(body);
                } else {
                    AiImageResponse err = new AiImageResponse();
                    err.successRaw = false;
                    err.code = response.code() == 403 ? "PACKAGE_REQUIRED" : String.valueOf(response.code());
                    err.message = "Image create failed";
                    live.setValue(err);
                }
            }

            @Override
            public void onFailure(Call<AiImageResponse> call, Throwable t) {
                AiImageResponse err = new AiImageResponse();
                err.successRaw = false;
                err.message = t.getMessage();
                live.setValue(err);
            }
        });
        return live;
    }

    public LiveData<AiProcessResponse> process(
            Context context,
            String tool,
            String imageUrl,
            String postId,
            Uri imageUri
    ) {
        MutableLiveData<AiProcessResponse> live = new MutableLiveData<>();
        Map<String, RequestBody> fields = new HashMap<>();
        fields.put("user_id", textBody(userId(context)));
        fields.put("tool", textBody(tool != null ? tool : "enhance"));
        if (imageUrl != null && !imageUrl.isEmpty()) {
            fields.put("image_url", textBody(imageUrl));
        }
        if (postId != null && !postId.isEmpty()) {
            fields.put("post_id", textBody(postId));
        }
        Callback<AiProcessResponse> cb = new Callback<AiProcessResponse>() {
            @Override
            public void onResponse(Call<AiProcessResponse> call, Response<AiProcessResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AiProcessResponse body = response.body();
                    if (body.resolvedQuota() != null) {
                        AiPrefs.applyQuota(context, body.resolvedQuota());
                    }
                    live.setValue(body);
                } else {
                    AiProcessResponse err = new AiProcessResponse();
                    err.successRaw = false;
                    err.code = response.code() == 403 ? "PACKAGE_REQUIRED" : String.valueOf(response.code());
                    err.message = "Process failed";
                    live.setValue(err);
                }
            }

            @Override
            public void onFailure(Call<AiProcessResponse> call, Throwable t) {
                AiProcessResponse err = new AiProcessResponse();
                err.successRaw = false;
                err.message = t.getMessage();
                live.setValue(err);
            }
        };
        MultipartBody.Part imagePart = imageUri != null ? uriPart(context, imageUri, "image") : null;
        if (imagePart != null) {
            api.aiProcessWithImage(fields, imagePart).enqueue(cb);
        } else {
            api.aiProcess(fields).enqueue(cb);
        }
        return live;
    }

    public LiveData<AiConversationListResponse> listConversations(Context context) {
        MutableLiveData<AiConversationListResponse> live = new MutableLiveData<>();
        api.aiConversations(userId(context)).enqueue(new Callback<AiConversationListResponse>() {
            @Override
            public void onResponse(Call<AiConversationListResponse> call, Response<AiConversationListResponse> response) {
                live.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<AiConversationListResponse> call, Throwable t) {
                live.setValue(null);
            }
        });
        return live;
    }

    public LiveData<AiConversationDetailResponse> getConversation(Context context, String id) {
        MutableLiveData<AiConversationDetailResponse> live = new MutableLiveData<>();
        api.aiConversation(id, userId(context)).enqueue(new Callback<AiConversationDetailResponse>() {
            @Override
            public void onResponse(Call<AiConversationDetailResponse> call, Response<AiConversationDetailResponse> response) {
                live.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<AiConversationDetailResponse> call, Throwable t) {
                live.setValue(null);
            }
        });
        return live;
    }

    public LiveData<Boolean> deleteConversation(Context context, String id) {
        MutableLiveData<Boolean> live = new MutableLiveData<>();
        api.deleteAiConversation(id, userId(context)).enqueue(new Callback<AiChatResponse>() {
            @Override
            public void onResponse(Call<AiChatResponse> call, Response<AiChatResponse> response) {
                live.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<AiChatResponse> call, Throwable t) {
                live.setValue(false);
            }
        });
        return live;
    }

    private AiChatResponse parseChat(Response<AiChatResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            AiChatResponse body = response.body();
            if (body.resolvedQuota() != null) {
                // applied by caller with context — stash on body only
            }
            return body;
        }
        AiChatResponse err = new AiChatResponse();
        err.successRaw = false;
        if (response.code() == 403) {
            err.code = "PACKAGE_REQUIRED";
            err.message = "Package required for AI";
        } else if (response.code() == 429) {
            err.code = "DAILY_LIMIT";
            err.message = "Daily AI limit reached";
        } else {
            err.message = "Chat failed (" + response.code() + ")";
        }
        return err;
    }

    private static String userId(Context context) {
        if (context == null) {
            return "";
        }
        String id = new PreferenceManager(context).getString(Constant.USER_ID);
        return id != null ? id : "";
    }

    private static RequestBody textBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value != null ? value : "");
    }

    private static MultipartBody.Part uriPart(Context context, Uri uri, String name) {
        if (context == null || uri == null) {
            return null;
        }
        try {
            File cache = new File(context.getCacheDir(), "ai_upload_" + System.currentTimeMillis() + ".jpg");
            try (InputStream in = context.getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(cache)) {
                if (in == null) {
                    return null;
                }
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                }
            }
            RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), cache);
            return MultipartBody.Part.createFormData(name, cache.getName(), body);
        } catch (Exception e) {
            Log.d(TAG, "uriPart: " + e.getMessage());
            return null;
        }
    }
}
