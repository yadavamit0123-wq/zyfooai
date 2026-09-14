package com.pt.zyfooai.ui.activities;

import static com.pt.zyfooai.binding.GlideDataBinding.viewToBitmap;
import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.pt.zyfooai.R;
import com.pt.zyfooai.binding.GlideDataBinding;
import com.pt.zyfooai.databinding.ActivitySavePostBinding;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.FrameBindHelper;
import com.pt.zyfooai.utils.FrameMediaInsetHelper;
import com.pt.zyfooai.utils.FrameOverlayHelper;
import com.pt.zyfooai.utils.FramePolishHelper;
import com.pt.zyfooai.utils.FrameSelectionHelper;
import com.pt.zyfooai.utils.FrameStickerHelper;
import com.pt.zyfooai.utils.ModernFrameCatalog;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.SnapHelperOneByOne;
import com.pt.zyfooai.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SavePostActivity extends AppCompatActivity {


    private Activity context;
    private String path = null;
    private PreferenceManager preferenceManager;
    public static ActivitySavePostBinding binding;
    CustomPagerAdapter customPagerAdapter;
    String musicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        topIconBar(this);

        preferenceManager = new PreferenceManager(context);

        path = getIntent().getStringExtra("uri");
        musicPath = getIntent().getStringExtra("music");
        if (path == null || path.isEmpty()) {
            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (preferenceManager.getString("DataType").equals("Business")) {

            binding.imageB.setVisibility(View.VISIBLE);
            binding.imageP.setVisibility(View.GONE);

        } else {

            binding.imageP.setVisibility(View.VISIBLE);
            binding.imageB.setVisibility(View.GONE);

        }

        if (preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Business")) {
            Glide.with(this).load(path).placeholder(R.drawable.placeholder).into(binding.imageB);
            binding.imageB.setVisibility(View.VISIBLE);
            binding.imageP.setVisibility(View.GONE);
        } else {
            Glide.with(this).load(path).placeholder(R.drawable.placeholder).into(binding.imageP);
            binding.imageB.setVisibility(View.GONE);
            binding.imageP.setVisibility(View.VISIBLE);
        }


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        customPagerAdapter = new CustomPagerAdapter(path);
        binding.recyclerview.setLayoutManager(linearLayoutManager);
        binding.recyclerview.setAdapter(customPagerAdapter);
        binding.recyclerview.scrollToPosition(
                FrameSelectionHelper.defaultSaveFramePosition(preferenceManager));
        binding.indicator.attachToRecyclerView(binding.recyclerview);

        SnapHelperOneByOne snapHelperOneByOne = new SnapHelperOneByOne();
        snapHelperOneByOne.attachToRecyclerView(binding.recyclerview);

        setupSavePostFrameParity(path);

        binding.layShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(viewToBitmap(binding.pLayoutTemp), false);
            }
        });

        binding.layDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(viewToBitmap(binding.pLayoutTemp), true);
            }
        });

        binding.backImg.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.editTxt.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        if (musicPath != null && !musicPath.isEmpty()) {
            playMusic(musicPath);
        }

        binding.pLayoutTemp.post(() -> {
            alignFrameOverlayToImage();
            FrameMediaInsetHelper.apply(binding.pLayoutTemp, binding.recyclerview, preferenceManager);
        });
    }

    private void alignFrameOverlayToImage() {
        View anchor = binding.imageB.getVisibility() == View.VISIBLE ? binding.imageB : binding.imageP;
        android.widget.RelativeLayout.LayoutParams params =
                (android.widget.RelativeLayout.LayoutParams) binding.recyclerview.getLayoutParams();
        params.addRule(android.widget.RelativeLayout.ALIGN_TOP, anchor.getId());
        params.addRule(android.widget.RelativeLayout.ALIGN_BOTTOM, anchor.getId());
        params.addRule(android.widget.RelativeLayout.ALIGN_START, anchor.getId());
        params.addRule(android.widget.RelativeLayout.ALIGN_END, anchor.getId());
        binding.recyclerview.setLayoutParams(params);

        android.widget.RelativeLayout.LayoutParams blurParams =
                (android.widget.RelativeLayout.LayoutParams) binding.mediaBlurBg.getLayoutParams();
        blurParams.addRule(android.widget.RelativeLayout.ALIGN_TOP, anchor.getId());
        blurParams.addRule(android.widget.RelativeLayout.ALIGN_BOTTOM, anchor.getId());
        blurParams.addRule(android.widget.RelativeLayout.ALIGN_START, anchor.getId());
        blurParams.addRule(android.widget.RelativeLayout.ALIGN_END, anchor.getId());
        binding.mediaBlurBg.setLayoutParams(blurParams);
    }

    private void setupSavePostFrameParity(String mediaPath) {
        binding.pLayoutTemp.setTag(R.id.media_blur_source_url, mediaPath);
        FrameOverlayHelper.applyFrameOverlay(binding.recyclerview, preferenceManager);
        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    FrameMediaInsetHelper.apply(binding.pLayoutTemp, binding.recyclerview, preferenceManager);
                }
            }
        });
        binding.pLayoutTemp.post(() ->
                FrameMediaInsetHelper.apply(binding.pLayoutTemp, binding.recyclerview, preferenceManager));
    }

    ExoPlayer musicPlayer;

    private void playMusic(String path) {
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.setPlayWhenReady(false);
            musicPlayer.release();
        }
        TrackSelector trackSelectorDef = new DefaultTrackSelector();
        musicPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelectorDef);

        int appNameStringRes = R.string.app_name;
        String userAgent = com.google.android.exoplayer2.util.Util.getUserAgent(context, context.getString(appNameStringRes));
        DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
        Uri uriOfContentUrl = Uri.parse(path);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl);  // creating a media source

        musicPlayer.prepare(mediaSource);
        musicPlayer.setPlayWhenReady(true);
        musicPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        musicPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicPlayer != null) {
            musicPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicPlayer != null) {
            musicPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            musicPlayer.setPlayWhenReady(false);
            musicPlayer.release();
        }
    }

    public class CustomPagerAdapter extends RecyclerView.Adapter<CustomPagerAdapter.ViewHolder> {

        String item_url;
        List<Integer> list = new ArrayList<>();

        public CustomPagerAdapter(String str) {
            this.item_url = str;

            list.addAll(ModernFrameCatalog.imageFrameLayouts());

        }

        @NonNull
        @Override
        public CustomPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(list.get(viewType), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CustomPagerAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);

            holder.userNameTv.setText(preferenceManager.getString(Constant.USER_NAME));
            holder.userDesTv.setText(preferenceManager.getString(Constant.USER_DESIGNATION));

            if (preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Personal")) {
                holder.facebookTv.setText(preferenceManager.getString(Constant.USER_FACEBOOK));
                holder.instagramTv.setText(preferenceManager.getString(Constant.USER_INSTAGRAM));

                holder.businessAddressTv.setText(preferenceManager.getString(Constant.USER_PHONE));
                Drawable[] drawables = holder.businessAddressTv.getCompoundDrawables();
                Drawable leftDrawable = drawables[0]; // Left position
                Drawable rightDrawable = drawables[2]; // Right position
                if (leftDrawable != null) {
                    holder.businessAddressTv.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_phone_number), null, null, null);
                } else if (rightDrawable != null) {
                    holder.businessAddressTv.setCompoundDrawableTintList(null);
                    holder.businessAddressTv.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.frame_6_call_icon), null);
                }

                FrameBindHelper.hideIfPresent(holder.itemView, R.id.topLay);
                FrameBindHelper.hideIfPresent(holder.itemView, R.id.whatsappLay);

                Glide.with(context)
                        .load(preferenceManager.getString(Constant.USER_IMAGE))
                        .placeholder(R.drawable.ic_add_images)
                        .into(holder.userImgView);

            } else {
                holder.businessNameTv.setText(preferenceManager.getString(Constant.BUSINESS_NAME));
                holder.businessDesTv.setText(preferenceManager.getString(Constant.BUSINESS_DETAIL));
                holder.businessNumberTv.setText(preferenceManager.getString(Constant.BUSINESS_NUMBER));
                holder.businessWebsiteTv.setText(preferenceManager.getString(Constant.BUSINESS_WEBSITE));
                holder.businessAddressTv.setText(preferenceManager.getString(Constant.BUSINESS_ADDRESS));
                holder.whatsapptv.setText(preferenceManager.getString(Constant.BUSINESS_WHATSAPP));
                holder.facebookTv.setText(preferenceManager.getString(Constant.BUSINESS_FACEBOOK));
                holder.instagramTv.setText(preferenceManager.getString(Constant.BUSINESS_INSTAGRAM));

                // Load business image
                Glide.with(context)
                        .load(preferenceManager.getString(Constant.USER_IMAGE))
                        .placeholder(R.drawable.ic_add_images)
                        .into(holder.userImgView);
                GlideDataBinding.bindImage(holder.businessImgView, preferenceManager.getString(Constant.BUSINESS_IMAGE));
            }

            holder.userImgView.setOnClickListener(view -> {
                context.startActivity(new Intent(context, EditProfileActivity.class));
            });
//            if (!preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Personal")) {
//                int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, holder.itemView.getResources().getDisplayMetrics());
//
//                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.userDesTv.getLayoutParams();
//                int topMarginInPx = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP,
//                        2,
//                        holder.itemView.getResources().getDisplayMetrics()
//                );
//                layoutParams.topMargin = topMarginInPx;
//                holder.userDesTv.setLayoutParams(layoutParams);
//                holder.userDesTv.setMaxLines(2);
//                holder.userNameTv.setTextSize(textSize);
//            }
//
//            if (holder.businessAddressTv.getText().toString().isEmpty()){
//                int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, holder.itemView.getResources().getDisplayMetrics());
//                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.userDesTv.getLayoutParams();
//                int topMarginInPx = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP,
//                        2,
//                        holder.itemView.getResources().getDisplayMetrics()
//                );
//                layoutParams.topMargin = topMarginInPx;
//                holder.userDesTv.setLayoutParams(layoutParams);
//                holder.userDesTv.setMaxLines(2);
//                holder.userNameTv.setTextSize(textSize);
//            }

            FrameBindHelper.setVisibilityIfEmpty(holder.facebookTv, holder.itemView.findViewById(R.id.facebookLay));
            FrameBindHelper.setVisibilityIfEmpty(holder.instagramTv, holder.itemView.findViewById(R.id.instagramLay));
            FrameBindHelper.setVisibilityIfEmpty(holder.whatsapptv, holder.itemView.findViewById(R.id.whatsappLay));

            FrameBindHelper.setVisibilityIfEmpty(holder.userDesTv);
            FrameBindHelper.setVisibilityIfEmpty(holder.businessNameTv);
            FrameBindHelper.setVisibilityIfEmpty(holder.businessDesTv);
            FrameBindHelper.setVisibilityIfEmpty(holder.businessNumberTv);
            FrameBindHelper.setVisibilityIfEmpty(holder.businessWebsiteTv);
            FrameBindHelper.setVisibilityIfEmpty(holder.businessAddressTv);

            if (holder.dateTv != null) {
                String currentDate = new SimpleDateFormat("dd MMM", Locale.getDefault())
                        .format(new Date()).toUpperCase(Locale.ROOT);
                holder.dateTv.setText(currentDate);
            }
            FrameStickerHelper.applyBusinessHeader(holder.itemView, preferenceManager);
            FramePolishHelper.apply(holder.itemView, position);
            FrameStickerHelper.bindDynamicContent(holder.itemView, preferenceManager);
            holder.itemView.post(() ->
                    FrameMediaInsetHelper.apply(binding.pLayoutTemp, binding.recyclerview, preferenceManager));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView businessNameTv, businessDesTv, businessNumberTv, businessWebsiteTv, businessAddressTv;
            TextView userNameTv, userDesTv, facebookTv, instagramTv, whatsapptv;
            TextView dateTv;
            ImageView userImgView, businessImgView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                businessNameTv = itemView.findViewById(R.id.businessNameTv);
                businessDesTv = itemView.findViewById(R.id.businessDesTv);
                businessNumberTv = itemView.findViewById(R.id.businessNumberTv);
                businessWebsiteTv = itemView.findViewById(R.id.businessWebsiteTv);
                businessAddressTv = itemView.findViewById(R.id.businessAddressTv);

                userNameTv = itemView.findViewById(R.id.userNameTv);
                userDesTv = itemView.findViewById(R.id.userDesTv);
                facebookTv = itemView.findViewById(R.id.facebookTv);
                instagramTv = itemView.findViewById(R.id.instagramTv);
                whatsapptv = itemView.findViewById(R.id.whatsappTv);

                dateTv = itemView.findViewById(R.id.dateTv);

                userImgView = itemView.findViewById(R.id.profileImg);
                businessImgView = itemView.findViewById(R.id.businesslogoImg);

            }
        }
    }


    private void saveImage(Bitmap bitmap, boolean save) {

        String fileName = System.currentTimeMillis() + ".png";
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                + File.separator + fileName;

        boolean success = false;

        if (!new File(filePath).exists()) {
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ), "/" + getResources().getString(R.string.app_name));
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Toast.makeText(context,
                                getResources().getString(R.string.create_dir_err),
                                Toast.LENGTH_LONG).show();
                        success = false;
                    }
                }
                File file2 = new File(filePath);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    Bitmap.Config config = bitmap.getConfig() != null
                            ? bitmap.getConfig()
                            : Bitmap.Config.ARGB_8888;
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                            bitmap.getHeight(), config);
                    Canvas canvas = new Canvas(createBitmap);
                    canvas.drawColor(-1);
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                    createBitmap.compress(Bitmap.CompressFormat.PNG,
                            100, fileOutputStream);
                    createBitmap.recycle();
                    fileOutputStream.flush();
                    fileOutputStream.close();


                    MediaScannerConnection.scanFile(context, new String[]{file2.getAbsolutePath()},
                            (String[]) null, (str, uri) -> {
                                StringBuilder sb = new StringBuilder();
                                sb.append("-> uri=");
                                sb.append(uri);
                                sb.append("-> FILE=");
                                sb.append(file2.getAbsolutePath());
                                Uri muri = Uri.fromFile(file2);
                            });
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }

            } catch (Exception e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (success) {
                if (musicPath != null && !musicPath.isEmpty()) {
                    downloadMp3(filePath, musicPath, save);
                } else {
                    if (save) {
                        Util.showToast(context, getString(R.string.image_saved));
                    } else {
                        shareFileImageUri(getImageContentUri(new File(filePath)), "type");
                    }
                }
            } else {
                Util.showToast(context, getString(R.string.error));
            }

        }

    }

    ProgressDialog progressDialog;

    private void downloadMp3(String framePath, String musicUrl, boolean save) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloading Music File...");
        progressDialog.show();

        String fileName = musicUrl.substring(musicUrl.lastIndexOf('/') + 1);
        ;
        File cacheDir = context.getExternalCacheDir(); // External cache directory
        if (cacheDir == null) {
            cacheDir = context.getCacheDir(); // Fallback to internal cache directory if external is not available
        }

        File finalCacheFile = new File(cacheDir, fileName);
        if (!finalCacheFile.exists()) {
            AndroidNetworking.download(musicUrl, cacheDir.getPath(), fileName).build().startDownload(new DownloadListener() {
                public void onDownloadComplete() {
                    applyMp3OnFrame(framePath, finalCacheFile.getAbsolutePath(), save);
                }

                public void onError(ANError aNError) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "" + aNError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            applyMp3OnFrame(framePath, finalCacheFile.getAbsolutePath(), save);
        }
    }


    public Uri getImageContentUri(File imageFile) {
        return Uri.parse(imageFile.getAbsolutePath());
    }

    public void shareFileImageUri(Uri path, String shareTo) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        switch (shareTo) {
            case "whtsapp":
                shareIntent.setPackage("com.whatsapp");
                break;
            case "fb":
                shareIntent.setPackage("com.facebook.katana");
                break;
            case "insta":
                shareIntent.setPackage("com.instagram.android");
                break;
            case "twter":
                shareIntent.setPackage("com.twitter.android");
                break;
        }

        String mimeType = "image/*"; // Default MIME type
        if (path.getPath().endsWith(".mp4")) {
            mimeType = "video/*"; // Set video MIME type if it's an MP4 file
        }
        shareIntent.setDataAndType(path, mimeType);

        shareIntent.putExtra(Intent.EXTRA_STREAM, path);

        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt) + getPackageName());

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_txt) + getPackageName()));
    }

    String musicFrameName = System.currentTimeMillis() + ".mp4";
    private void applyMp3OnFrame(String framePath, String musicPath, boolean save) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String musicFramePath = Environment.getExternalStorageDirectory() + File.separator
                        + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                        + File.separator + musicFrameName;

                Log.e("applyMp3__", "Music Path: " + musicPath);
                Log.e("applyMp3__", "Frame Path: " + framePath);
                Log.e("applyMp3__", "OUT Path: " + musicFramePath);

                File frameFile = new File(framePath);
                File musicFile = new File(musicPath);

                if (!frameFile.exists()) {
                    Log.e("applyMp3__", "Image file not found: " + framePath);
                }
                if (!musicFile.exists()) {
                    Log.e("applyMp3__", "Music file not found: " + musicPath);
                }

                if (!new File(musicFramePath).exists()) {

                    String outputDir = musicFramePath;

                    // Now create the video with the image and audio
                    String[] cmd = {
                            "-loop", "1",
                            "-framerate", "1",
                            "-i", framePath,
                            "-i", musicPath,
                            "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2", // Resize to even height
                            "-c:v", "libx264",
                            "-tune", "stillimage",
                            "-c:a", "aac",
                            "-b:a", "192k",
                            "-shortest",
                            "-pix_fmt", "yuv420p",
                            "-y",
                            outputDir
                    };


                    progressDialog.setMessage("Applying Music..");
                    FFmpeg.executeAsync(cmd, new ExecuteCallback() {
                        @Override
                        public void apply(long executionId, int returnCode) {
                            progressDialog.dismiss();
                            if (returnCode == 1) {
                                FFmpeg.cancel(executionId);
                                Toast.makeText(SavePostActivity.this, "Try Again Later", Toast.LENGTH_SHORT).show();
                            }
                            if (returnCode == 0) {
                                MediaScannerConnection.scanFile(context, new String[]{musicFramePath},
                                        (String[]) null, (str, uri) -> {
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("-> uri=");
                                            sb.append(uri);
                                            sb.append("-> FILE=");
                                            sb.append(musicFramePath);
                                            Uri muri = Uri.parse(musicFramePath);
                                        });

                                if (save) {
                                    Util.showToast(context, getString(R.string.video_saved));
                                } else {
                                    shareFileImageUri(getImageContentUri(new File(musicFramePath)), "type");
                                }
                            } else if (returnCode == 255) {
                                Log.e("applyMp3__", "Command execution cancelled by user.");
                            } else {
                                String str = String.format("Command execution failed with rc=%d and the output below.", Arrays.copyOf(new Object[]{Integer.valueOf(returnCode)}, 1));
                                Log.i("applyMp3__", str);
                            }
                        }
                    });

                } else {

                    progressDialog.dismiss();

                    if (save) {
                        Util.showToast(context, getString(R.string.video_saved));
                    } else {
                        shareFileImageUri(getImageContentUri(new File(musicFramePath)), "type");
                    }

                }

            }
        });
    }
}