package com.pt.zyfooai.utils;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.pt.zyfooai.R;
import com.pt.zyfooai.imagecroper.ManualBGRemoverActivity;
import com.bumptech.glide.Glide;
//import com.huawei.hmf.tasks.Task;
//import com.huawei.hms.mlsdk.MLAnalyzerFactory;
//import com.huawei.hms.mlsdk.common.MLFrame;
//import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
//import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;


public class ImageCropperFragment extends DialogFragment {

    public interface OnBitmapSelect {
        void output(int id, String out);
    }

    int id = 0;


    OnBitmapSelect onBitmapSelect;

    public static String path;
    public static Bitmap bitmap;

    public ImageCropperFragment(int id, String path, OnBitmapSelect onBitmapSelect) {
        this.id = id;
        this.path = path;
        this.onBitmapSelect = onBitmapSelect;
    }

    public ImageCropperFragment(int id, Bitmap path, OnBitmapSelect onBitmapSelect) {
        this.id = id;
        this.bitmap = path;
        this.onBitmapSelect = onBitmapSelect;
    }

//    MLImageSegmentationAnalyzer analyzer;
    TextView remove_bg, use_original, use_this;
    ShapesImage normalPreviewImage, ovalPreviewImage, diamondPreviewImage, squarePreviewImage;
    LottieAnimationView animation_view;
    ShapesImage normal_img, oval_img, diamond_img, square_img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picked_image_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);

//        analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer();


        remove_bg = view.findViewById(R.id.remove_bg);
        use_original = view.findViewById(R.id.use_original);
        use_this = view.findViewById(R.id.use_this);

        normalPreviewImage = view.findViewById(R.id.normalPreviewImage);
        ovalPreviewImage = view.findViewById(R.id.ovalPreviewImage);
        diamondPreviewImage = view.findViewById(R.id.diamondPreviewImage);
        squarePreviewImage = view.findViewById(R.id.squarePreviewImage);
        animation_view = view.findViewById(R.id.animation_view);

        if (path == null) {
            Glide.with(getContext()).load(bitmap).placeholder(R.drawable.logo).into(normalPreviewImage);
            Glide.with(getContext()).load(bitmap).placeholder(R.drawable.logo).into(ovalPreviewImage);
            Glide.with(getContext()).load(bitmap).placeholder(R.drawable.logo).into(diamondPreviewImage);
            Glide.with(getContext()).load(bitmap).placeholder(R.drawable.logo).into(squarePreviewImage);
        } else {
            Glide.with(getContext()).load(path).placeholder(R.drawable.logo).into(normalPreviewImage);
            Glide.with(getContext()).load(path).placeholder(R.drawable.logo).into(ovalPreviewImage);
            Glide.with(getContext()).load(path).placeholder(R.drawable.logo).into(diamondPreviewImage);
            Glide.with(getContext()).load(path).placeholder(R.drawable.logo).into(squarePreviewImage);
        }


        normal_img = view.findViewById(R.id.normal_img);
        oval_img = view.findViewById(R.id.oval_img);
        diamond_img = view.findViewById(R.id.diamond_img);
        square_img = view.findViewById(R.id.square_img);

        selectedShapesImage = normalPreviewImage;
        normal_img.setOnClickListener(v -> {
            selectedShapesImage = normalPreviewImage;
            selectedShapesImage(selectedShapesImage);
            use_this.setVisibility(View.VISIBLE);
        });
        oval_img.setOnClickListener(v -> {
            selectedShapesImage = ovalPreviewImage;
            selectedShapesImage(selectedShapesImage);
            use_this.setVisibility(View.VISIBLE);
        });
        diamond_img.setOnClickListener(v -> {
            selectedShapesImage = diamondPreviewImage;
            selectedShapesImage(selectedShapesImage);
            use_this.setVisibility(View.VISIBLE);
        });
        square_img.setOnClickListener(v -> {
            selectedShapesImage = squarePreviewImage;
            selectedShapesImage(selectedShapesImage);
            use_this.setVisibility(View.VISIBLE);
        });

        remove_bg.setOnClickListener(view14 -> {
            if (remove_bg.getText().toString().equals(getString(R.string.remove_bg))) {
                removeBgAuto();
            } else {
                resultCallbackForEraser.launch(new Intent(getContext(), ManualBGRemoverActivity.class));
            }
        });
        view.findViewById(R.id.close_btn).setOnClickListener(view13 -> dismiss());
        view.findViewById(R.id.use_this).setOnClickListener(view12 -> {
            new MyUtils.GetImageFileAsync(getActivity(), selectedShapesImage.getBitmap()).onBitmapSaved(file1 -> onBitmapSelect.output(id, file1.getAbsolutePath()));
            dismiss();
        });
        use_original.setOnClickListener(view1 -> {
            onBitmapSelect.output(id, path);
            dismiss();
        });
    }

    ActivityResultLauncher<Intent> resultCallbackForEraser = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {

                            Glide.with(getContext()).load(path).placeholder(R.drawable.logo).into(selectedShapesImage);
                            Glide.with(getContext()).load(path).placeholder(R.drawable.logo).into(normalPreviewImage);
                            Glide.with(getContext()).load(path).placeholder(R.drawable.logo).into(ovalPreviewImage);
                            Glide.with(getContext()).load(path).placeholder(R.drawable.logo).into(diamondPreviewImage);
                            Glide.with(getContext()).load(path).placeholder(R.drawable.logo).into(squarePreviewImage);

                        } else {
                            Util.showToast(getActivity(), getString(R.string.something_wrong));
                        }
                    }
                }
            });

    private void selectedShapesImage(ShapesImage img) {

        if (img.getId() == R.id.normalPreviewImage){

            normalPreviewImage.setVisibility(View.VISIBLE);
            ovalPreviewImage.setVisibility(View.GONE);
            diamondPreviewImage.setVisibility(View.GONE);
            squarePreviewImage.setVisibility(View.GONE);
        }else  if (img.getId() == R.id.ovalPreviewImage){

            normalPreviewImage.setVisibility(View.GONE);
            ovalPreviewImage.setVisibility(View.VISIBLE);
            diamondPreviewImage.setVisibility(View.GONE);
            squarePreviewImage.setVisibility(View.GONE);
        }else  if (img.getId() == R.id.diamondPreviewImage){
            normalPreviewImage.setVisibility(View.GONE);
            ovalPreviewImage.setVisibility(View.GONE);
            diamondPreviewImage.setVisibility(View.VISIBLE);
            squarePreviewImage.setVisibility(View.GONE);
        } else if (img.getId() == R.id.squarePreviewImage) {

            normalPreviewImage.setVisibility(View.GONE);
            ovalPreviewImage.setVisibility(View.GONE);
            diamondPreviewImage.setVisibility(View.GONE);
            squarePreviewImage.setVisibility(View.VISIBLE);
        }

    }

    ShapesImage selectedShapesImage;

    private void removeBgAuto() {
        animation_view.setVisibility(View.VISIBLE);
        remove_bg.setEnabled(false);
        use_original.setEnabled(false);
        if (path == null) {
            analyse(bitmap);
        } else {
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeFile(path);
                analyse(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private final void analyse(Bitmap bitmap) {
//        MLFrame frame = MLFrame.fromBitmap(bitmap);
//        Task<MLImageSegmentation> task = analyzer.asyncAnalyseFrame(frame);
//        task.addOnSuccessListener(segmentation -> {
//
//            Glide.with(getContext()).load(segmentation.foreground).placeholder(R.drawable.logo).into(selectedShapesImage);
//            Glide.with(getContext()).load(segmentation.foreground).placeholder(R.drawable.logo).into(normalPreviewImage);
//            Glide.with(getContext()).load(segmentation.foreground).placeholder(R.drawable.logo).into(ovalPreviewImage);
//            Glide.with(getContext()).load(segmentation.foreground).placeholder(R.drawable.logo).into(diamondPreviewImage);
//            Glide.with(getContext()).load(segmentation.foreground).placeholder(R.drawable.logo).into(squarePreviewImage);
//
//            use_this.setVisibility(View.VISIBLE);
//            remove_bg.setVisibility(View.VISIBLE);
//            remove_bg.setEnabled(true);
//            use_original.setEnabled(true);
//            remove_bg.setText(getString(R.string.manual));
//            animation_view.setVisibility(View.GONE);
//
//        }).addOnFailureListener(e -> {
//
//            Util.showToast(getActivity(), getString(R.string.something_wrong));
//            animation_view.setVisibility(View.GONE);
//
//        });


    }

}