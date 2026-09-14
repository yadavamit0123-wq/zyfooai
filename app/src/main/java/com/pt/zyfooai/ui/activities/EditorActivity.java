package com.pt.zyfooai.ui.activities;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.pt.zyfooai.ui.adapters.FontsAdapter.getEnglishFonts;
import static com.pt.zyfooai.binding.GlideDataBinding.viewToBitmap;
import static com.pt.zyfooai.utils.MyUtils.getAppFolder;
import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.pt.zyfooai.R;
import com.pt.zyfooai.View.AutoFitEditText;
import com.pt.zyfooai.View.StickerView;
import com.pt.zyfooai.View.ViewIdGenerator;
import com.pt.zyfooai.View.text.AutofitTextInfo;
import com.pt.zyfooai.View.text.AutofitTextRel;
import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.ui.adapters.ColorAdapter;
import com.pt.zyfooai.ui.adapters.FontsAdapter;
import com.pt.zyfooai.binding.GlideDataBinding;
import com.pt.zyfooai.databinding.ActivityEditorBinding;
import com.pt.zyfooai.listener.AdapterClickListener;
import com.pt.zyfooai.listener.ClickListener;
import com.pt.zyfooai.model.FontDataModel;
import com.pt.zyfooai.model.TemplateInfo;
import com.pt.zyfooai.ui.fragments.SelectMusicFragment;
import com.pt.zyfooai.ui.fragments.SelectStickerFragment;
import com.pt.zyfooai.ui.model.ElementInfo;
import com.pt.zyfooai.ui.model.textInfo;
import com.pt.zyfooai.ui.utility.ImageUtils;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.ImageCropperFragment;
import com.pt.zyfooai.utils.MyUtils;
import com.pt.zyfooai.utils.Util;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import yuku.ambilwarna.AmbilWarnaDialog;

public class EditorActivity extends AppCompatActivity implements AutofitTextRel.TouchEventListener,StickerView.TouchEventListener {

    private Activity context;
    private String ratio;
    private String type;


    private Bitmap bitmap;


    ListFragment listFragment;
    private int isTamplate;


    ArrayList<ElementInfo> elementInfos = new ArrayList<>();
    ArrayList<textInfo> textInfoArrayList = new ArrayList<>();
    ArrayList<AutofitTextInfo> textInfosUR = new ArrayList<>();
    HashMap<Integer, Object> txtShapeList;
    ArrayList<TemplateInfo> templateListUR = new ArrayList<>();


    private boolean isMovie;

    private float letterSpacing = 0.0f;
    private float lineSpacing = 0.0f;

    public static ActivityEditorBinding binding;
    String backgroundPosterPath;

    private AutofitTextRel selectedTextView;
    private StickerView selectedStickerView;
    private String cameraTempFile;
    public static int bgColor = ViewCompat.MEASURED_STATE_MASK;

    private String imageUri;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;


        topIconBar(this);
        addTextNew("Enter Text");

        intilization();


        imageUri = getIntent().getStringExtra("imageUri");

        GlideDataBinding.bindImage(binding.backgroundImg, imageUri);


        binding.changeFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.llFont.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(VISIBLE);
                    binding.llAlign.setVisibility(View.GONE);
                    binding.llShadow.setVisibility(View.GONE);
                    binding.llBoxColor.setVisibility(View.GONE);
                    binding.llChangeColor.setVisibility(GONE);

                    slideUp(binding.llFont);
                    binding.changeFontImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.changeFontTxt.setTextColor(getResources().getColor(R.color.see_more));
                }

            }
        });

        binding.boxAddSticker.setOnClickListener(view -> {
            hideTextStickerBorders();
            binding.llFont.setVisibility(GONE);
            binding.llAlign.setVisibility(View.GONE);
            binding.llShadow.setVisibility(View.GONE);
            binding.llBoxColor.setVisibility(GONE);
            binding.llChangeColor.setVisibility(GONE);

            new SelectStickerFragment(new ClickListener<PostItem>() {
                @Override
                public void onClick(PostItem data) {
                    addSticker("",data.image_url,null);
                }
            }).show(getSupportFragmentManager(),"");
        });

        binding.boxAddMusic.setOnClickListener(view -> {
            hideTextStickerBorders();
            binding.llFont.setVisibility(GONE);
            binding.llAlign.setVisibility(View.GONE);
            binding.llShadow.setVisibility(View.GONE);
            binding.llBoxColor.setVisibility(GONE);
            binding.llChangeColor.setVisibility(GONE);

            new SelectMusicFragment(new ClickListener<PostItem>() {
                @Override
                public void onClick(PostItem data) {
                    playMusic(data.image_url);
                }
            }).show(getSupportFragmentManager(),"");
        });

        binding.boxColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.llBoxColor.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(GONE);
                    binding.llAlign.setVisibility(View.GONE);
                    binding.llShadow.setVisibility(View.GONE);
                    binding.llBoxColor.setVisibility(VISIBLE);
                    binding.llChangeColor.setVisibility(GONE);
                    slideUp(binding.llBoxColor);
                    binding.boxColorImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.boxColorTxt.setTextColor(getResources().getColor(R.color.see_more));
                }

            }
        });

        binding.boxAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideTextStickerBorders();
                binding.llFont.setVisibility(GONE);
                binding.llAlign.setVisibility(View.GONE);
                binding.llShadow.setVisibility(View.GONE);
                binding.llBoxColor.setVisibility(GONE);
                binding.llChangeColor.setVisibility(GONE);

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(i);
            }
        });


        binding.changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.llChangeColor.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(GONE);
                    binding.llAlign.setVisibility(View.GONE);
                    binding.llShadow.setVisibility(View.GONE);
                    binding.llBoxColor.setVisibility(View.GONE);
                    binding.llChangeColor.setVisibility(VISIBLE);
                    slideUp(binding.llChangeColor);
                    binding.changeColorImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.changeColorTxt.setTextColor(getResources().getColor(R.color.see_more));
                }

            }
        });


        binding.alignMent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.llAlign.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(GONE);
                    binding.llAlign.setVisibility(VISIBLE);
                    binding.llShadow.setVisibility(View.GONE);
                    binding.llBoxColor.setVisibility(View.GONE);
                    binding.llChangeColor.setVisibility(GONE);
                    slideUp(binding.llAlign);
                    binding.alignmentImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.alignmentTxt.setTextColor(getResources().getColor(R.color.see_more));
                }


            }
        });


        binding.shadowColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (binding.llShadow.getVisibility() != VISIBLE) {
                    clearTint();
                    binding.llFont.setVisibility(GONE);
                    binding.llAlign.setVisibility(GONE);
                    binding.llShadow.setVisibility(VISIBLE);
                    binding.llBoxColor.setVisibility(View.GONE);
                    binding.llChangeColor.setVisibility(GONE);
                    slideUp(binding.llShadow);
                    binding.shadowColorImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
                    binding.shadowColorTxt.setTextColor(getResources().getColor(R.color.see_more));
                }

            }
        });


        binding.llAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideTextStickerBorders();

               // binding.addTextImg.setColorFilter(getResources().getColor(R.color.see_more), PorterDuff.Mode.SRC_IN);
              //  binding.addTextTxt.setTextColor(getResources().getColor(R.color.see_more));

                addTextDialog(null);

            }
        });


        setFontAdapter(getEnglishFonts());

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAllLayouts("");
                saveImage(viewToBitmap(binding.mainRelativeLay));
            }
        });

        binding.removeMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicPath = "";
                binding.diskLay.setVisibility(GONE);
                stopMusic();
            }
        });

        binding.playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicPlayer != null && musicPlayer.isPlaying()) {
                    stopMusic();
                } else {
                    if (musicPlayer != null) {
                        playMusic();
                    } else {
                        playMusic(musicPath);
                    }

                }
            }
        });
    }

    private void stopMusic() {
        if (musicPlayer != null){
            musicPlayer.setPlayWhenReady(false);
            binding.playPauseBtn.setImageDrawable(getDrawable(com.google.android.exoplayer2.R.drawable.exo_controls_play));
        }
    }

    private void playMusic() {
        if (musicPlayer != null){
            musicPlayer.setPlayWhenReady(true);
            binding.playPauseBtn.setImageDrawable(getDrawable(com.google.android.exoplayer2.R.drawable.exo_controls_pause));
        }
    }

    String musicPath = "";
    ExoPlayer musicPlayer;
    private void playMusic(String path) {
        musicPath = path;
        binding.diskLay.setVisibility(VISIBLE);
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
                if (playWhenReady){
                    binding.diskLottie.playAnimation();
                }else {
                    binding.diskLottie.pauseAnimation();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        musicPlayer.release();
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    if (result.getData() != null) {
                        getImageFromURI(result);
                    }
                }
            });

    private void getImageFromURI(ActivityResult result) {
        Uri selectedImage = result.getData().getData();
        beginCrop(selectedImage);
    }

    private void beginCrop(Uri uri) {
        if (uri != null) {
            try {
                Uri destinationUri = Uri.fromFile(new File(context.getCacheDir(), new File(uri.getPath()).getName()));
                UCrop.Options options2 = new UCrop.Options();
                options2.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options2.setFreeStyleCropEnabled(true);

                // Start cropping activity with startActivityForResult
                UCrop.of(uri, destinationUri)
                        .withAspectRatio(Constant.BUSINESS_LOGO_WIDTH, Constant.BUSINESS_LOGO_HEIGHT)
                        .withOptions(options2)
                        .start(context);  // "this" refers to your activity or fragment
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                new ImageCropperFragment(0, MyUtils.getPathFromURI(context, UCrop.getOutput(data)), (id, out) -> {
                    addSticker("",out,null);
                }).show(getSupportFragmentManager(), "");
            }
        }
    }

    private void addSticker(String str, String str2, Bitmap bitmap2) {
        ElementInfo elementInfo = new ElementInfo();
        elementInfo.setPOS_X((float) ((binding.textViewRelative.getWidth() / 2) - ImageUtils.dpToPx(this, 70.0f)));
        elementInfo.setPOS_Y((float) ((binding.textViewRelative.getHeight() / 2) - ImageUtils.dpToPx(this, 70.0f)));
        elementInfo.setWIDTH(ImageUtils.dpToPx(this, 140.0f));
        elementInfo.setHEIGHT(ImageUtils.dpToPx(this, 140.0f));
        elementInfo.setROTATION(0.0f);
        elementInfo.setRES_ID(str);
//        elementInfo.setBITMAP(bitmap2);
//        elementInfo.setCOLORTYPE(this.colorType);
        elementInfo.setTYPE("STICKER");
        elementInfo.setSTC_OPACITY(255);
        elementInfo.setSTC_COLOR(0);
        elementInfo.setSTKR_PATH(str2);
//        elementInfo.setSTC_HUE(this.hueSeekbar.getProgress());
//        elementInfo.setFIELD_TWO("0,0");
        StickerView stickerView = new StickerView(this);
//        stickerView.optimizeScreen(this.screenWidth, this.screenHeight);
        stickerView.setDefaultTouchListener(true);
        stickerView.isMultiTouchEnabled = true;
        stickerView.setViewWH((float) binding.textViewRelative.getWidth(), (float) binding.textViewRelative.getHeight());
        stickerView.setComponentInfo(elementInfo);
        stickerView.setId(ViewIdGenerator.generateViewId());
        binding.textViewRelative.addView(stickerView);


        stickerView.setOnTouchCallbackListener(this);
        stickerView.setBorderVisibility(true);
        selectedStickerView = stickerView;

    }

    public void saveBitmapUndu() {
        try {
            TemplateInfo templateInfo = new TemplateInfo();
            templateInfo.setRATIO(this.ratio);
            templateInfo.setBACKGROUND_PATH(this.backgroundPosterPath);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int childCount = binding.textViewRelative.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = binding.textViewRelative.getChildAt(i);
                if (childAt instanceof AutofitTextRel) {
                    AutofitTextInfo textInfo = ((AutofitTextRel) childAt).getTextInfo();
                    textInfo.setORDER(i);
                    textInfo.setTYPE("TEXT");
                    arrayList.add(textInfo);
                } else {
                    ElementInfo componentInfo = ((StickerView) binding.textViewRelative.getChildAt(i)).getComponentInfo();
                    componentInfo.setTYPE("STICKER");
                    componentInfo.setORDER(i);
                    arrayList2.add(componentInfo);

                }
            }
            templateInfo.setTextInfoArrayList(arrayList);
            templateInfo.setElementInfoArrayList(arrayList2);
            this.templateListUR.add(templateInfo);
            //  iconVisibility();
        } catch (Exception e) {
            Log.i("testing", "Exception " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable th) {
        }
    }

    private void clearTint() {

        binding.alignmentImg.clearColorFilter();
        binding.addTextImg.clearColorFilter();
        binding.boxColorImg.clearColorFilter();
        binding.changeColorImg.clearColorFilter();
        binding.shadowColorImg.clearColorFilter();
        binding.changeFontImg.clearColorFilter();
        binding.addTextTxt.setTextColor(getResources().getColor(R.color.black));
        binding.alignmentTxt.setTextColor(getResources().getColor(R.color.black));
        binding.boxColorTxt.setTextColor(getResources().getColor(R.color.black));
        binding.changeColorTxt.setTextColor(getResources().getColor(R.color.black));
        binding.shadowColorTxt.setTextColor(getResources().getColor(R.color.black));
        binding.changeFontTxt.setTextColor(getResources().getColor(R.color.black));

    }


    public void downoloadFonts(String str, String str2, String str3) {

        AndroidNetworking.download("" + str, "" + str2, "" + str3).build().startDownload(new DownloadListener() {
            @Override
            public void onDownloadComplete() {

                setTextFonts(str3);
            }

            public void onError(ANError aNError) {

            }
        });
    }

    public String ExtractFileNameFromUrl(String urlString) {
        try {
            String fileName = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Path path = Paths.get(new URL(urlString).getPath());
                fileName = path.getFileName().toString();
            }
            return fileName;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public void setTextFonts(String str) {
        if (selectedTextView != null) {
            selectedTextView.setTextFont(str);
            saveBitmapUndu();
        }
    }



    private void setBoldFonts() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                Log.d("setTextFont___", "Check -> " + selectedTextView.isBold());
                if (selectedTextView.isBold()) {
                    selectedTextView.setNormalFont();
                } else {
                    selectedTextView.setBoldFont();
                }
                saveBitmapUndu();
            }
        }
    }

    private void setItalicFont() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                selectedTextView.setItalicFont();
                saveBitmapUndu();
            }
        }
    }

    private void setLeftAlignMent() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                selectedTextView.setLeftAlignMent();
                saveBitmapUndu();
            }
        }
    }

    private void setCenterAlignMent() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                selectedTextView.setCenterAlignMent();
                saveBitmapUndu();
            }
        }
    }

    private void setRightAlignMent() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                selectedTextView.setRightAlignMent();
                saveBitmapUndu();
            }
        }
    }

    private void copyTextView() {
        if (selectedTextView != null) {
            if (selectedTextView.getBorderVisibility()) {
                AutofitTextRel autofitTextRel2 = new AutofitTextRel(this);
                binding.textViewRelative.addView(autofitTextRel2);
                autofitTextRel2.setTextInfo(selectedTextView.getTextInfo(), false);
                autofitTextRel2.setId(ViewIdGenerator.generateViewId());
                autofitTextRel2.setOnTouchCallbackListener(this);
                autofitTextRel2.setBorderVisibility(true);
                saveBitmapUndu();
            }
        }
    }

    boolean editMode = false;

    public void addTextDialog(final AutofitTextInfo originAutofitTextInfo) {
        hideAllLayouts("");
        final Dialog dialog = new Dialog(this, R.style.MyAlertDialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog.setContentView(R.layout.add_text_dialog);

        final AutoFitEditText autoFitEditText = dialog.findViewById(R.id.auto_fit_edit_text);
        ImageView button = dialog.findViewById(R.id.btnCancelDialog);
        LinearLayout button2 = dialog.findViewById(R.id.btnAddTextSDialog);

        if (originAutofitTextInfo != null) {
            autoFitEditText.setText(originAutofitTextInfo.getTEXT());
        } else {
            autoFitEditText.setText("Enter Text");
        }

        button.setOnClickListener(view -> dialog.dismiss());
        button2.setOnClickListener(view -> {
            if (autoFitEditText.getText().toString().length() > 0) {
                if (originAutofitTextInfo != null) {
                    selectedTextView.setText(autoFitEditText.getText().toString());
                } else {
                    addText(autoFitEditText.getText().toString());
                }
                dialog.dismiss();
            } else {
                Toast.makeText(EditorActivity.this, "Please enter text here.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void addText(String text) {
        AutofitTextInfo autofitTextInfo = new AutofitTextInfo();
        AutofitTextRel autofitTextRel = null;
        autofitTextInfo.setTEXT(text);
        autofitTextInfo.setTEXT_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setTEXT_ALPHA(100);
        autofitTextInfo.setSHADOW_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setSHADOW_PROG(0);
        autofitTextInfo.setBG_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setBG_DRAWABLE("0");
        autofitTextInfo.setBG_ALPHA(0);
        autofitTextInfo.setROTATION(0.0f);

        // Calculate center positions dynamically
        float posX = (binding.textViewRelative.getWidth() / 2) - (ImageUtils.dpToPx(EditorActivity.this, 100.0f)); // Half of text width
        float posY = (binding.textViewRelative.getHeight() / 2) - (ImageUtils.dpToPx(EditorActivity.this, 25.0f)); // Half of text height

        autofitTextInfo.setPOS_X(posX);
        autofitTextInfo.setPOS_Y(posY);
        autofitTextInfo.setWIDTH(ImageUtils.dpToPx(EditorActivity.this, 200.0f)); // Text width
        autofitTextInfo.setHEIGHT(ImageUtils.dpToPx(EditorActivity.this, 50.0f));  // Text height


    /*    autofitTextInfo.setPOS_X((float) ((binding.textViewRelative.getWidth() / 2) - ImageUtils.dpToPx(EditorActivity.this, -80.0f)));
        autofitTextInfo.setPOS_Y((float) ((binding.textViewRelative.getHeight() / 2) - ImageUtils.dpToPx(EditorActivity.this, -180.0f)));
        autofitTextInfo.setWIDTH(ImageUtils.dpToPx(EditorActivity.this, 200.0f));
        autofitTextInfo.setHEIGHT(ImageUtils.dpToPx(EditorActivity.this, 50.0f));*/
        try {
            autofitTextRel = new AutofitTextRel(EditorActivity.this);
            binding.textViewRelative.addView(autofitTextRel);
            autofitTextRel.setTextInfo(autofitTextInfo, false);
            autofitTextRel.setId(ViewIdGenerator.generateViewId());
            autofitTextRel.setOnTouchCallbackListener(EditorActivity.this);
            autofitTextRel.setBorderVisibility(true);
        } catch (ArrayIndexOutOfBoundsException e2) {
            e2.printStackTrace();
        }
        if (autofitTextRel != null) {
            selectedTextView = autofitTextRel;
        }
        saveBitmapUndu();
    }

    private void addTextNew(String text) {
        AutofitTextInfo autofitTextInfo = new AutofitTextInfo();
        AutofitTextRel autofitTextRel = null;
        autofitTextInfo.setTEXT(text);
        autofitTextInfo.setTEXT_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setTEXT_ALPHA(100);
        autofitTextInfo.setSHADOW_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setSHADOW_PROG(0);
        autofitTextInfo.setBG_COLOR(ViewCompat.MEASURED_STATE_MASK);
        autofitTextInfo.setBG_DRAWABLE("0");
        autofitTextInfo.setBG_ALPHA(0);
        autofitTextInfo.setROTATION(0.0f);


        autofitTextInfo.setPOS_X((float) ((binding.textViewRelative.getWidth() / 2) - ImageUtils.dpToPx(EditorActivity.this, -80.0f)));
        autofitTextInfo.setPOS_Y((float) ((binding.textViewRelative.getHeight() / 2) - ImageUtils.dpToPx(EditorActivity.this, -180.0f)));
        autofitTextInfo.setWIDTH(ImageUtils.dpToPx(EditorActivity.this, 200.0f));
        autofitTextInfo.setHEIGHT(ImageUtils.dpToPx(EditorActivity.this, 50.0f));
        try {
            autofitTextRel = new AutofitTextRel(EditorActivity.this);
            binding.textViewRelative.addView(autofitTextRel);
            autofitTextRel.setTextInfo(autofitTextInfo, false);
            autofitTextRel.setId(ViewIdGenerator.generateViewId());
            autofitTextRel.setOnTouchCallbackListener(EditorActivity.this);
            autofitTextRel.setBorderVisibility(true);
        } catch (ArrayIndexOutOfBoundsException e2) {
            e2.printStackTrace();
        }
        if (autofitTextRel != null) {
            selectedTextView = autofitTextRel;
            //   showTextControlles();
        }
        saveBitmapUndu();
    }


    private void setFontAdapter(List<FontDataModel> list) {
        ((RecyclerView) findViewById(R.id.ep_fontRv)).setAdapter(new FontsAdapter(context, list, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                FontDataModel model = (FontDataModel) object;
                if (!new File(getAppFolder(context) + "font/" + ExtractFileNameFromUrl(model.getUrl())).exists()) {
                    downoloadFonts(model.getUrl(), getAppFolder(context) + "font/", ExtractFileNameFromUrl(model.getUrl()));
                } else {
                    setTextFonts(ExtractFileNameFromUrl(model.getUrl()));
                }
            }
        }));
    }


    private void hideAllLayouts(String nohide) {

//        if (!(nohide.equals("text") || nohide.equals("image"))) {
            hideTextStickerBorders();
//        }

    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(300);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }, 500);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(300);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    private WindowManager.LayoutParams getLayoutParams(@NonNull Dialog dialog) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null) {
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
        }
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return layoutParams;
    }


    public void hideTextStickerBorders() {
        RelativeLayout relativeLayout = binding.textViewRelative;
        if (relativeLayout != null) {
            int childCount = relativeLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = binding.textViewRelative.getChildAt(i);
                if (childAt instanceof AutofitTextRel) {
                    ((AutofitTextRel) childAt).setBorderVisibility(false);
                }
                if (childAt instanceof StickerView) {
                    ((StickerView) childAt).setBorderVisibility(false);
                }
            }
        }
    }

    private void touchDown(View view, String str) {
        Log.d("editorLogs--->", "check -> touchDown()");
        if (str.equals("hideboder")) {
            hideTextStickerBorders();
        }
    }

    private void touchMove(View view) {
        Log.d("editorLogs--->", "check -> touchMove()");
    }

    private void touchUp(final View view) {
        Log.d("editorLogs--->", "check -> touchUp()");
        if (view instanceof AutofitTextRel) {
            selectedTextView = (AutofitTextRel) view;
            saveBitmapUndu();
        }
        if ((view instanceof StickerView)) {
            selectedStickerView = (StickerView) view;
            saveBitmapUndu();
        }
    }


    @Override
    public void onEdit(View view, Uri uri) {

    }

    @Override
    public void onMidX(View view) {

    }

    @Override
    public void onMidXY(View view) {

    }

    @Override
    public void onMidY(View view) {

    }


    public void onRotateDown(View view) {
        touchDown(view, "viewboder");
    }

    public void onRotateMove(View view) {
        touchMove(view);
    }

    public void onRotateUp(View view) {
        touchUp(view);
    }

    public void onScaleDown(View view) {
        touchDown(view, "viewboder");
    }

    public void onScaleMove(View view) {
        touchMove(view);
    }

    public void onScaleUp(View view) {
        touchUp(view);
    }

    boolean checkTouchContinue = false;

    public void onTouchDown(View view) {
        touchDown(view, "hideboder");
        if (this.checkTouchContinue) {
//            binding.layStkrMain.post(() -> {
//                checkTouchContinue = true;
//            });
        }
    }

    public void onTouchMove(View view) {
        touchMove(view);
    }

    public void onTouchUp(View view) {
        this.checkTouchContinue = false;
        touchUp(view);
    }

    @Override
    public void onXY(View view) {

    }


    public void onDoubleTap() {
        doubleTabPrass();
    }

    private void doubleTabPrass() {
        this.editMode = true;
        try {
            if (selectedTextView.getBorderVisibility()) {
                AutofitTextInfo autofitTextInfo = selectedTextView.getTextInfo();
                addTextDialog(autofitTextInfo);
            }
        } catch (NullPointerException e) {

            e.printStackTrace();

        }
    }

    private int gcd(int i, int i2) {
        return i2 == 0 ? i : gcd(i2, i % i2);
    }


    private float getnewHeight(int i, int i2, float f, float f2) {
        return (((float) i2) * f) / ((float) i);
    }

    private float getnewWidth(int i, int i2, float f, float f2) {
        return (((float) i) * f2) / ((float) i2);
    }

    public float getXpos(float f) {
        return (((float) binding.mainRelativeLay.getWidth()) * f) / 100.0f;
    }

    public float getYpos(float f) {
        return (((float) binding.mainRelativeLay.getHeight()) * f) / 100.0f;
    }

    public int getNewWidht(float f, float f2) {
        return (int) ((((float) binding.mainRelativeLay.getWidth()) * (f2 - f)) / 100.0f);
    }

    public int getNewHeight(float f, float f2) {
        return (int) ((((float) binding.mainRelativeLay.getHeight()) * (f2 - f)) / 100.0f);
    }

    public int getNewHeightText(float f, float f2) {
        float height = (((float) binding.mainRelativeLay.getHeight()) * (f2 - f)) / 100.0f;
        return (int) (((float) ((int) height)) + (height / 2.0f));
    }

    public void onTouchMoveUpClick(View view) {
        // saveBitmapUndu();
    }

    public void onDelete() {
        hideAllLayouts("");
          saveBitmapUndu();
    }


    private void intilization() {
        binding.mainRelativeLay.setOnClickListener(view -> {
            hideAllLayouts("");
        });


        binding.fontBoldImg.setOnClickListener(view -> {
            setBoldFonts();
        });
        binding.fontItalicImg.setOnClickListener(view -> {
            setItalicFont();
        });

        binding.alignLeftLay.setOnClickListener(view -> {
            setLeftAlignMent();
        });
        binding.alignCenterLay.setOnClickListener(view -> {
            setCenterAlignMent();
        });
        binding.alignRightLay.setOnClickListener(view -> {
            setRightAlignMent();
        });
        binding.llAddText.setOnClickListener(view -> {
            copyTextView();
        });

        binding.colorBtn.setOnClickListener(view -> {
            new AmbilWarnaDialog(this, selectedTextView.getTextColor(), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                    selectedTextView.setTextColor(i);
                }

                public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {

                }
            }).show();
        });

        binding.colorPickerImg2.setOnClickListener(view -> {
            new AmbilWarnaDialog(this, selectedTextView.getBgColor(), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                    selectedTextView.setBgColor(i);
                }

                public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {

                }
            }).show();
        });


        binding.colorPickerImg.setOnClickListener(view -> {
            new AmbilWarnaDialog(this, selectedTextView.getTextShadowColor(), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                public void onOk(AmbilWarnaDialog ambilWarnaDialog, int color) {
                    selectedTextView.setTextShadowColor(color);
                    saveBitmapUndu();
                }

                public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {

                }
            }).show();
        });

        binding.fontSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b && selectedTextView != null) {
                    selectedTextView.setFontSize(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                saveBitmapUndu();


            }
        });


        binding.fontOpacitySeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectedTextView.setTextAlpha(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        binding.fontBgOpacitySeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectedTextView.setBgAlpha(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });




        binding.fontSizeSeekBar.setProgress((int) selectedTextView.getFontSize());

        selectedTextView.setTextShadowOpacity(100);

        binding.shadowSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (selectedTextView.getBorderVisibility()) {


                        selectedTextView.setTextShadowProg(i);

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveBitmapUndu();
            }
        });




        ArrayList<Integer> colorList = new ArrayList<>();
        colorList.add(Integer.valueOf(Color.parseColor("#00000000")));
        colorList.add(Integer.valueOf(Color.parseColor("#000000")));
        colorList.add(Integer.valueOf(Color.parseColor("#0098F1")));
        colorList.add(Integer.valueOf(Color.parseColor("#4CC259")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFC859")));
        colorList.add(Integer.valueOf(Color.parseColor("#FF8523")));
        colorList.add(Integer.valueOf(Color.parseColor("#FF3A4A")));
        colorList.add(Integer.valueOf(Color.parseColor("#E90060")));
        colorList.add(Integer.valueOf(Color.parseColor("#B300B6")));
        colorList.add(Integer.valueOf(Color.parseColor("#FF0000")));
        colorList.add(Integer.valueOf(Color.parseColor("#FF7E88")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFD0D1")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFDAB2")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFC07E")));
        colorList.add(Integer.valueOf(Color.parseColor("#E18B42")));
        colorList.add(Integer.valueOf(Color.parseColor("#a36138")));
        colorList.add(Integer.valueOf(Color.parseColor("#4A2829")));
        colorList.add(Integer.valueOf(Color.parseColor("#004C30")));
        colorList.add(Integer.valueOf(Color.parseColor("#2C2C2C")));
        colorList.add(Integer.valueOf(Color.parseColor("#393939")));
        colorList.add(Integer.valueOf(Color.parseColor("#555555")));
        colorList.add(Integer.valueOf(Color.parseColor("#727272")));
        colorList.add(Integer.valueOf(Color.parseColor("#989898")));
        colorList.add(Integer.valueOf(Color.parseColor("#B1B1B1")));
        colorList.add(Integer.valueOf(Color.parseColor("#C7C7C7")));
        colorList.add(Integer.valueOf(Color.parseColor("#DBDBDB")));
        colorList.add(Integer.valueOf(Color.parseColor("#F0F0F0")));
        colorList.add(Integer.valueOf(Color.parseColor("#FFFFFF")));

// Add the rest of your colors


        ArrayList<Integer> fontColor = new ArrayList<>();
        fontColor.add(Integer.valueOf(Color.parseColor("#000000")));
        fontColor.add(Integer.valueOf(Color.parseColor("#000000")));
        fontColor.add(Integer.valueOf(Color.parseColor("#0098F1")));
        fontColor.add(Integer.valueOf(Color.parseColor("#4CC259")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFC859")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FF8523")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FF3A4A")));
        fontColor.add(Integer.valueOf(Color.parseColor("#E90060")));
        fontColor.add(Integer.valueOf(Color.parseColor("#B300B6")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FF0000")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FF7E88")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFD0D1")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFDAB2")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFC07E")));
        fontColor.add(Integer.valueOf(Color.parseColor("#E18B42")));
        fontColor.add(Integer.valueOf(Color.parseColor("#a36138")));
        fontColor.add(Integer.valueOf(Color.parseColor("#4A2829")));
        fontColor.add(Integer.valueOf(Color.parseColor("#004C30")));
        fontColor.add(Integer.valueOf(Color.parseColor("#2C2C2C")));
        fontColor.add(Integer.valueOf(Color.parseColor("#393939")));
        fontColor.add(Integer.valueOf(Color.parseColor("#555555")));
        fontColor.add(Integer.valueOf(Color.parseColor("#727272")));
        fontColor.add(Integer.valueOf(Color.parseColor("#989898")));
        fontColor.add(Integer.valueOf(Color.parseColor("#B1B1B1")));
        fontColor.add(Integer.valueOf(Color.parseColor("#C7C7C7")));
        fontColor.add(Integer.valueOf(Color.parseColor("#DBDBDB")));
        fontColor.add(Integer.valueOf(Color.parseColor("#F0F0F0")));
        fontColor.add(Integer.valueOf(Color.parseColor("#FFFFFF")));

        ColorAdapter colorAdapter = new ColorAdapter(fontColor, this, new ClickListener<Integer>() {
            @Override
            public void onClick(Integer data) {
                selectedTextView.setTextColor(data.intValue());

            }
        });
        binding.fontColorRv.setAdapter(colorAdapter);


        ColorAdapter colorAdapter2 = new ColorAdapter(colorList, this, new ClickListener<Integer>() {
            @Override
            public void onClick(Integer data) {
                selectedTextView.setBgColor(data.intValue());
                bgColor = data.intValue();

            }
        });
        binding.boxColorRv.setAdapter(colorAdapter2);


        ColorAdapter colorAdapter3 = new ColorAdapter(colorList, this, new ClickListener<Integer>() {
            @Override
            public void onClick(Integer data) {


                selectedTextView.setTextShadowColor(data.intValue());

                saveBitmapUndu();

            }
        });
        binding.shadowColorRv.setAdapter(colorAdapter3);


    }


    private void saveImage(Bitmap bitmap) {


        String fileName = System.currentTimeMillis() + ".png";

        // Get the cache directory for your app
        File cacheDir = context.getExternalCacheDir(); // External cache directory
        if (cacheDir == null) {
            cacheDir = context.getCacheDir(); // Fallback to internal cache directory if external is not available
        }

        // Create the file in the cache directory
        File file = new File(cacheDir, fileName);
        boolean success;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Bitmap.Config config = bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawColor(-1);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            createBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            createBitmap.recycle();
            fileOutputStream.flush();
            fileOutputStream.close();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        if (success) {
            Intent intent = new Intent(context, SavePostActivity.class);
            intent.putExtra("uri", file.getAbsolutePath());
            intent.putExtra("music", musicPath);
            startActivity(intent);
        } else {
            Util.showToast(context, getString(R.string.error));
        }
    }

}
