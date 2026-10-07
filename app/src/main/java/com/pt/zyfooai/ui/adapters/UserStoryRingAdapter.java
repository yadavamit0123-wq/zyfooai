package com.pt.zyfooai.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pt.zyfooai.R;
import com.pt.zyfooai.model.UserStoryFeedItem;

import java.util.ArrayList;
import java.util.List;

public class UserStoryRingAdapter extends RecyclerView.Adapter<UserStoryRingAdapter.Holder> {

    public interface Listener {
        void onAddStory();

        void onOpenStory(UserStoryFeedItem item);
    }

    private final List<UserStoryFeedItem> items = new ArrayList<>();
    private final Listener listener;

    public UserStoryRingAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<UserStoryFeedItem> feed, String currentUserId, String currentUserAvatar) {
        items.clear();
        UserStoryFeedItem add = new UserStoryFeedItem();
        add.isAddButton = true;
        add.userName = "Your story";
        add.userAvatar = currentUserAvatar;
        add.userId = currentUserId;
        items.add(add);
        if (feed != null) {
            for (UserStoryFeedItem item : feed) {
                if (item == null) {
                    continue;
                }
                // Own active story still shows in feed; tapping opens viewer (add stays first).
                items.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_story_ring, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        UserStoryFeedItem item = items.get(position);
        holder.nameTv.setText(item.isAddButton
                ? "Your story"
                : (item.userName != null && !item.userName.isEmpty() ? item.userName : "Story"));

        String imageUrl = item.isAddButton
                ? item.userAvatar
                : (item.thumbnailUrl != null && !item.thumbnailUrl.isEmpty()
                ? item.thumbnailUrl
                : item.userAvatar);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.avatar).load(imageUrl).placeholder(R.drawable.ic_profile).into(holder.avatar);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_profile);
        }

        if (item.isAddButton) {
            holder.addBadge.setVisibility(View.VISIBLE);
            holder.ringBorder.setBackgroundResource(R.drawable.bg_user_story_ring_unseen);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddStory();
                }
            });
        } else {
            holder.addBadge.setVisibility(View.GONE);
            holder.ringBorder.setBackgroundResource(
                    item.seen ? R.drawable.bg_user_story_ring_seen : R.drawable.bg_user_story_ring_unseen);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOpenStory(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final View ringBorder;
        final CircularImageView avatar;
        final ImageView addBadge;
        final TextView nameTv;

        Holder(@NonNull View itemView) {
            super(itemView);
            ringBorder = itemView.findViewById(R.id.ringBorder);
            avatar = itemView.findViewById(R.id.avatar);
            addBadge = itemView.findViewById(R.id.addBadge);
            nameTv = itemView.findViewById(R.id.nameTv);
        }
    }
}
