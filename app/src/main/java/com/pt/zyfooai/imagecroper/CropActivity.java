package com.pt.zyfooai.imagecroper;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.utils.MyUtils;
//import com.huawei.hmf.tasks.Task;
//import com.huawei.hms.mlsdk.MLAnalyzerFactory;
//import com.huawei.hms.mlsdk.common.MLFrame;
//import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
//import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;


public class CropActivity extends AppCompatActivity {

    public static final String BG_IMAGE_PATH = "bg_image_path";
//    MLImageSegmentationAnalyzer analyzer;
    int id = 0;
    Bitmap b = null;
    private ImageView back;
    private CardView btnDone;
    private ImageView ivImage;
    private ProgressBar progressBar;
    private Bitmap processedBitmap = null;
    private CardView btnEdit;
    private CardView btnBgRemove;
    private String path;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        MyUtils.hideNavigation(this, true);

//        analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer();

        initView();

        back.setOnClickListener(view -> onBackPressed());


        Intent intent1 = getIntent();

        if (intent1.getExtras() != null) {

            path = intent1.getStringExtra(BG_IMAGE_PATH);
            ivImage.setImageURI(Uri.parse(path));

            if (intent1.hasExtra("id")) {
                this.id = getIntent().getExtras().getInt("id");

            }

        }

        btnDone.setOnClickListener(view -> {

            if (processedBitmap != null) {

                new MyUtils.GetImageFileAsync(CropActivity.this, processedBitmap).onBitmapSaved(file1 -> {


                    Intent intent = new Intent();
                    intent.putExtra(BG_IMAGE_PATH, file1.getAbsolutePath());
                    intent.putExtra("id", id);
                    setResult(RESULT_OK, intent);


                    finish();

                });


            } else {


                Intent intent = new Intent();
                intent.putExtra(BG_IMAGE_PATH, path);
                setResult(RESULT_OK, intent);

                finish();

            }
        });

        btnEdit.setOnClickListener(view -> {

            if (processedBitmap != null) {

                new MyUtils.GetImageFileAsync(CropActivity.this, processedBitmap).onBitmapSaved(file1 -> startActivityForResult(new Intent(getApplicationContext(), ManualBGRemoverActivity.class)
                        .putExtra(BG_IMAGE_PATH, file1.getAbsolutePath()), 12));

            } else {

                startActivityForResult(new Intent(getApplicationContext(), ManualBGRemoverActivity.class)
                        .putExtra(BG_IMAGE_PATH, path), 12);

            }


        });

        btnBgRemove.setOnClickListener(view -> {

            progressBar.setVisibility(View.VISIBLE);

            Bitmap bitmap = null;
            try {

                bitmap = BitmapFactory.decodeFile(path);

                ivImage.setImageBitmap(BitmapFactory.decodeFile(path));

                analyse(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12) {

            if (data != null) {

                Intent intent = new Intent();
                intent.putExtra(BG_IMAGE_PATH, data.getStringExtra(BG_IMAGE_PATH));
                intent.putExtra("id", id);
                setResult(RESULT_OK, intent);

                finish();

            } else {
                Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private final void analyse(Bitmap bitmap) {

//        MLFrame frame = MLFrame.fromBitmap(bitmap);
//
//        Task<MLImageSegmentation> task = analyzer.asyncAnalyseFrame(frame);
//
//        task.addOnSuccessListener(segmentation -> {
//
//            progressBar.setVisibility(View.GONE);
//
//            ivImage.setImageBitmap(segmentation.foreground);
//            processedBitmap = segmentation.foreground;
//
//        }).addOnFailureListener(e -> {
//
//            Toast.makeText(CropActivity.this, getString(R.string.something_went_wrong) + e.getMessage(), Toast.LENGTH_SHORT).show();
//            progressBar.setVisibility(View.GONE);
//        });


    }


    private void initView() {
        back = findViewById(R.id.back);
        btnDone = findViewById(R.id.btnDone);
        ivImage = findViewById(R.id.ivImage);
        progressBar = findViewById(R.id.progressBar);
        btnEdit = findViewById(R.id.btnEdit);
        btnBgRemove = findViewById(R.id.btnBgRemove);
    }
}
