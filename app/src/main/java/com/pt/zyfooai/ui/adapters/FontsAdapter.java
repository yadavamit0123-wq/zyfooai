package com.pt.zyfooai.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.databinding.ItemviewFontBinding;
import com.pt.zyfooai.listener.AdapterClickListener;
import com.pt.zyfooai.model.FontDataModel;

import java.util.ArrayList;
import java.util.List;


public class FontsAdapter extends RecyclerView.Adapter<FontsAdapter.ViewHolder> {

    private List<FontDataModel> list;
    private AdapterClickListener listener;
    Context context;

    public FontsAdapter(Context context, List<FontDataModel> list, AdapterClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemviewFontBinding binding = ItemviewFontBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            WebSettings webSettings = holder.binding.gridText.getSettings();
            webSettings.setJavaScriptEnabled(true);
            String htmlContent = "<html><head>" +
                    "<style type=\"text/css\">" +
                    "@font-face {" +
                    "    font-family: 'BengaliFont';" +
                    "    src: url('"+list.get(position).getUrl()+"') format('truetype');" +
                    "}" +
                    "body {" +
                    "    font-family: 'BengaliFont', sans-serif;" + // Use the custom font family
                    "    display: flex;" +
                    "    text-align: center;" + // Center the text
                    "    align-items: center;" + // Center vertically
                    "    justify-content: center;" + // Center horizontally
                    "    height: 100vh;" + // 100% of the viewport height
                    "}" +
                    "</style></head><body>"+list.get(position).getName()+"</body></html>";

            holder.binding.gridText.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    return false;
                }
            });
            holder.binding.gridText.setOnClickListener(view -> {
                listener.onItemClick(view,position,list.get(position));
            });
            holder.binding.gridText.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {
                    holder.binding.progress.setVisibility(View.GONE);
                }
            });
            holder.binding.gridText.loadData(htmlContent, "text/html", "UTF-8");

        } catch (Exception e) {
            Log.e("onBindViewHolder", "getView: font not found");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ItemviewFontBinding binding;

        ViewHolder(@NonNull ItemviewFontBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

    }




    public static List<FontDataModel> getEnglishFonts(){
        List<FontDataModel> list = new ArrayList<>();
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1708448279/Fonts/Hindi_23_aqtwlp.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394460/Fonts/English_1_nghgvm.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394456/Fonts/English_2_btdkxn.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705392181/Fonts/bengali_3_ika6bs.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705392266/Fonts/bengali_4_rtzn7k.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705392205/Fonts/bengali_5_ss6bdn.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705392256/Fonts/bengali_6_ig6lpa.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705392197/Fonts/bengali_7_t1bkrt.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705392302/Fonts/bengali_8_l5nef3.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705392306/Fonts/bengali_9_jhkrsu.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705392262/Fonts/bengali_10_pw19rb.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394464/Fonts/English_11_mywnia.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394461/Fonts/English_12_i0ydn2.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394461/Fonts/English_13_xyfo8b.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394461/Fonts/English_14_tohnwg.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394463/Fonts/English_15_qj9kcb.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394517/Fonts/English_16_ennwtx.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394569/Fonts/English_17_mnv5ta.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394598/Fonts/English_18_w1sp3y.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394600/Fonts/English_19_nfuxvq.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394599/Fonts/English_20_g6ikin.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394600/Fonts/English_21_yadybk.ttf"));
        list.add(new FontDataModel("Aa","https://res.cloudinary.com/dse9nnmqr/raw/upload/v1705394638/Fonts/English_22_tbc3v1.ttf"));

        return list;
    }

}