package com.pt.zyfooai.respository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pt.zyfooai.api.ApiClient;
import com.pt.zyfooai.api.ApiService;

import com.pt.zyfooai.model.AppInfos;
import com.pt.zyfooai.model.CategoryItem;

import com.pt.zyfooai.model.LanguageItem;
import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.model.StoryItem;
import com.pt.zyfooai.model.SubscriptionModel;
import com.pt.zyfooai.model.UserItem;


import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRespository {


    private ApiService apiService;

    public HomeRespository() {
        apiService = ApiClient.getApiDataService();
    }

    public LiveData<UserItem> login(String loginType, String displayName, String email, String photoUrl, String phoneNumber) {

        MutableLiveData<UserItem> data = new MutableLiveData<>();
        apiService.login(loginType,displayName,email,photoUrl,phoneNumber).enqueue(new Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                data.setValue(response.body());
                Log.d("login__", "onResponse " + response.body());
            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {
                Log.d("login__", "onFailure " + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<List<SubscriptionModel>> getSubscriptionPlan() {

        MutableLiveData<List<SubscriptionModel>> data = new MutableLiveData<>();
        apiService.getSubscriptionPlan().enqueue(new Callback<List<SubscriptionModel>>() {
            @Override
            public void onResponse(Call<List<SubscriptionModel>> call, Response<List<SubscriptionModel>> response) {
                data.setValue(response.body());
                Log.d("getFestival", "111 " + response.body());
            }

            @Override
            public void onFailure(Call<List<SubscriptionModel>> call, Throwable t) {
                data.setValue(null);

            }
        });
        return data;
    }

    public LiveData<List<CategoryItem>> getCategories(String type) {
        MutableLiveData<List<CategoryItem>> data = new MutableLiveData<>();
        apiService.getCategories(type).enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<CategoryItem>> getBusinessCategory(String search, String type) {
        MutableLiveData<List<CategoryItem>> data = new MutableLiveData<>();
        apiService.getBusinessCategory(search,type).enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<CategoryItem>> getBackgroundCategory() {
        MutableLiveData<List<CategoryItem>> data = new MutableLiveData<>();
        apiService.getBackgroundCategory().enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<CategoryItem>> getStickerCategory() {
        MutableLiveData<List<CategoryItem>> data = new MutableLiveData<>();
        apiService.getStickerCategory().enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<CategoryItem>> getMusicCategory() {
        MutableLiveData<List<CategoryItem>> data = new MutableLiveData<>();
        apiService.getMusicCategory().enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }



    public LiveData<List<PostItem>> getDailyPosts(int page, String catid, String language, String business_id, String political_id) {
        MutableLiveData<List<PostItem>> data = new MutableLiveData<>();
        apiService.getDailyPostData(page, catid, language, business_id,political_id).enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                data.setValue(response.body());
                Log.d("zyfooai__", "onResponse: "+response.body());
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                data.setValue(null);
                Log.d("zyfooai__", "onFailure: "+t.getMessage());
            }
        });
        return data;
    }
    public LiveData<List<PostItem>> getFestivalPost(int page, String catid, String language) {
        MutableLiveData<List<PostItem>> data = new MutableLiveData<>();
        apiService.getFestivalPost(page, catid, language).enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                data.setValue(response.body());
                Log.d("zyfooai__", "onResponse: "+response.body());
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                Log.d("zyfooai__", "onFailure: "+t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }
    public LiveData<List<PostItem>> getBackgroundByCategory( String catid) {
        MutableLiveData<List<PostItem>> data = new MutableLiveData<>();
        apiService.getBackgroundByCategory(catid).enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<PostItem>> getStickerByCategory( String catid) {
        MutableLiveData<List<PostItem>> data = new MutableLiveData<>();
        apiService.getStickersByCategory(catid).enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<PostItem>> getMusicByCategory( String catid) {
        MutableLiveData<List<PostItem>> data = new MutableLiveData<>();
        apiService.getMusicByCategory(catid).enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }


    public LiveData<List<LanguageItem>> getLanguagess() {


        MutableLiveData<List<LanguageItem>> data = new MutableLiveData<>();

        apiService.getLanguagess().enqueue(new Callback<List<LanguageItem>>() {
            @Override
            public void onResponse(Call<List<LanguageItem>> call, Response<List<LanguageItem>> response) {

                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<LanguageItem>> call, Throwable t) {

                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<AppInfos> getAppInfo(String userID) {

        MutableLiveData<AppInfos> data = new MutableLiveData<>();

        apiService.getAppInfo(userID).enqueue(new Callback<AppInfos>() {
            @Override
            public void onResponse(Call<AppInfos> call, Response<AppInfos> response) {

                data.setValue(response.body());

            }

            @Override
            public void onFailure(Call<AppInfos> call, Throwable t) {

                Log.d("errrorrr",""+t.getMessage());

                t.printStackTrace();
                data.setValue(null);

            }
        });
        return data;
    }

    public LiveData<List<StoryItem>> getFestival() {

        MutableLiveData<List<StoryItem>> data = new MutableLiveData<>();
        apiService.getFestival().enqueue(new Callback<List<StoryItem>>() {
            @Override
            public void onResponse(Call<List<StoryItem>> call, Response<List<StoryItem>> response) {
                data.setValue(response.body());
                Log.d("zyfooai__", "getFestival onResponse: "+response.body());
            }

            @Override
            public void onFailure(Call<List<StoryItem>> call, Throwable t) {
                data.setValue(null);
                Log.d("zyfooai__", "getFestival onFailure: "+t.getMessage());

            }
        });
        return data;
    }

    public LiveData<SubscriptionModel> storeDevice(String id_device) {
        MutableLiveData<SubscriptionModel> data = new MutableLiveData<>();

        apiService.storeDevice(id_device).enqueue(new Callback<SubscriptionModel>() {
            @Override
            public void onResponse(Call<SubscriptionModel> call, Response<SubscriptionModel> response) {
                Log.d("response___t", "" + response.body());
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<SubscriptionModel> call, Throwable t) {
                data.setValue(null);
                Log.d("response___t", " E-> " + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<UserItem> updateBusiness(String user_id, String businessName, String businessAddress, String businessDetails,
                                             String businessWhatsapp, String businessInstagram, String businessLogoPath) {
        MutableLiveData<UserItem> data = new MutableLiveData<>();

        // Create RequestBody for text data
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), businessName);
        RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), businessAddress);
        RequestBody detailsBody = RequestBody.create(MediaType.parse("text/plain"), businessDetails);
        RequestBody whatsappBody = RequestBody.create(MediaType.parse("text/plain"), businessWhatsapp);
        RequestBody instagramBody = RequestBody.create(MediaType.parse("text/plain"), businessInstagram);

        // Create MultipartBody.Part for the image
        MultipartBody.Part logoPart = null;
        if (businessLogoPath != null && !businessLogoPath.isEmpty()) {
            File file = new File(businessLogoPath);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            logoPart = MultipartBody.Part.createFormData("business_logo", file.getName(), fileBody);
        }

        // Make the API call
        apiService.updateBusiness(userIdBody, nameBody, addressBody, detailsBody, whatsappBody, instagramBody, logoPart)
                .enqueue(new Callback<UserItem>() {
                    @Override
                    public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                        Log.d("response___t", "" + response.body());
                        data.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<UserItem> call, Throwable t) {
                        data.setValue(null);
                        Log.d("response___t", " E-> " + t.getMessage());
                    }
                });

        return data;
    }

    public LiveData<UserItem> updateProfile(String user_id,String name, String designation, String number, String instagram,String facebook, String profile) {
        MutableLiveData<UserItem> data = new MutableLiveData<>();

        // Create RequestBody for text data
        RequestBody useridBody = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody designationBody = RequestBody.create(MediaType.parse("text/plain"), designation);
        RequestBody numberBody = RequestBody.create(MediaType.parse("text/plain"), number);
        RequestBody instagramBody = RequestBody.create(MediaType.parse("text/plain"), instagram);
        RequestBody facebookBody = RequestBody.create(MediaType.parse("text/plain"), facebook);

        // Create MultipartBody.Part for the image
        MultipartBody.Part logoPart = null;
        if (profile != null && !profile.isEmpty()) {
            File file = new File(profile);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            logoPart = MultipartBody.Part.createFormData("logo", file.getName(), fileBody);
        }

        // Make the API call
        apiService.updateProfile(useridBody, nameBody, designationBody, numberBody, instagramBody, facebookBody, logoPart)
                .enqueue(new Callback<UserItem>() {
                    @Override
                    public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                        Log.d("response___t", "Response: " + response.body());
                        data.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<UserItem> call, Throwable t) {
                        if (t instanceof IOException) {
                            Log.d("response___t", "Network Failure: " + t.getMessage());
                        } else {
                            Log.d("response___t", "Conversion Issue: " + t.getMessage());
                        }
                        data.setValue(null);
                    }

                });

        return data;
    }

    public LiveData<UserItem> updateProfile(String user_id,String name, String designation, String number, String instagram,String facebook, String profile,String defaultType) {
        MutableLiveData<UserItem> data = new MutableLiveData<>();

        // Create RequestBody for text data
        RequestBody useridBody = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody designationBody = RequestBody.create(MediaType.parse("text/plain"), designation);
        RequestBody numberBody = RequestBody.create(MediaType.parse("text/plain"), number);
        RequestBody instagramBody = RequestBody.create(MediaType.parse("text/plain"), instagram);
        RequestBody facebookBody = RequestBody.create(MediaType.parse("text/plain"), facebook);
        RequestBody defaultTypeBody = RequestBody.create(MediaType.parse("text/plain"), defaultType);

        // Create MultipartBody.Part for the image
        MultipartBody.Part logoPart = null;
        if (profile != null && !profile.isEmpty()) {
            File file = new File(profile);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            logoPart = MultipartBody.Part.createFormData("logo", file.getName(), fileBody);
        }

        // Make the API call
        apiService.updateProfile(useridBody, nameBody, designationBody, numberBody, instagramBody, facebookBody,defaultTypeBody, logoPart)
                .enqueue(new Callback<UserItem>() {
                    @Override
                    public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                        Log.d("response___t", "Response: " + response.body());
                        data.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<UserItem> call, Throwable t) {
                        if (t instanceof IOException) {
                            Log.d("response___t", "Network Failure: " + t.getMessage());
                        } else {
                            Log.d("response___t", "Conversion Issue: " + t.getMessage());
                        }
                        data.setValue(null);
                    }

                });

        return data;
    }

    public LiveData<UserItem>  updateBusinessProfile(String userId, String name,String politicalID, String businessID,String businessname, String businessemail, String businessdesignation, String designation, String number, String website, String instagram, String facebook,String defaultType, String businessAddress, String userImageUrl, String businessImageUrl) {
        MutableLiveData<UserItem> data = new MutableLiveData<>();

        // Create RequestBody for text data
        RequestBody useridBody = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody businessIDBody = RequestBody.create(MediaType.parse("text/plain"), businessID);
        RequestBody politicalIDBody = RequestBody.create(MediaType.parse("text/plain"), politicalID);
        RequestBody businessNameBody = RequestBody.create(MediaType.parse("text/plain"), businessname);
        RequestBody businessEmailBody = RequestBody.create(MediaType.parse("text/plain"), businessemail);
        RequestBody businessDesignationBody = RequestBody.create(MediaType.parse("text/plain"), businessdesignation);
        RequestBody designationBody = RequestBody.create(MediaType.parse("text/plain"), designation);
        RequestBody numberBody = RequestBody.create(MediaType.parse("text/plain"), number);
        RequestBody websiteBody = RequestBody.create(MediaType.parse("text/plain"), website);
        RequestBody instagramBody = RequestBody.create(MediaType.parse("text/plain"), instagram);
        RequestBody facebookBody = RequestBody.create(MediaType.parse("text/plain"), facebook);
        RequestBody defaultTypeBody = RequestBody.create(MediaType.parse("text/plain"), defaultType);
        RequestBody businessAddressBody = RequestBody.create(MediaType.parse("text/plain"), businessAddress);

        MultipartBody.Part businessLogoPart = null;
        if (businessImageUrl != null && !businessImageUrl.isEmpty()) {
            File file = new File(businessImageUrl);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            businessLogoPart = MultipartBody.Part.createFormData("business_logo", file.getName(), fileBody);
        }


        MultipartBody.Part logoPart = null;
        if (userImageUrl != null && !userImageUrl.isEmpty()) {
            File file = new File(userImageUrl);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            logoPart = MultipartBody.Part.createFormData("logo", file.getName(), fileBody);
        }

        // Make the API call
        apiService.updateBusinessProfile(useridBody, nameBody,politicalIDBody,businessIDBody,  businessNameBody, businessEmailBody, businessDesignationBody, designationBody, numberBody, websiteBody, instagramBody, facebookBody, defaultTypeBody,businessAddressBody, logoPart, businessLogoPart)
                .enqueue(new Callback<UserItem>() {
                    @Override
                    public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                        Log.d("response___t", "Response: " + response.body());
                        data.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<UserItem> call, Throwable t) {
                        if (t instanceof IOException) {
                            Log.d("response___t", "Network Failure: " + t.getMessage());
                        } else {
                            Log.d("response___t", "Conversion Issue: " + t.getMessage());
                        }
                        data.setValue(null);
                    }

                });

        return data;
    }
}
