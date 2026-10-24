package com.pt.zyfooai.ui.adapters;

import android.text.TextUtils;
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
        UserStoryFeedItem ownStory = null;
        List<UserStoryFeedItem> others = new ArrayList<>();
        if (feed != null) {
            for (UserStoryFeedItem item : feed) {
                if (item == null) {
                    continue;
                }
                if (isSameUser(currentUserId, item.userId)) {
                    if (ownStory == null) {
                        ownStory = item;
                    }
                } else {
                    others.add(item);
                }
            }
        }

        if (ownStory != null) {
            // Own posted story first — tap opens viewer; + badge adds more.
            ownStory.isAddButton = false;
            ownStory.showAddBadge = true;
            if (TextUtils.isEmpty(ownStory.userName)) {
                ownStory.userName = "Your story";
            }
            items.add(ownStory);
        } else {
            UserStoryFeedItem add = new UserStoryFeedItem();
            add.isAddButton = true;
            add.showAddBadge = true;
            add.userName = "Your story";
            add.userAvatar = currentUserAvatar;
            add.userId = currentUserId;
            items.add(add);
        }
        items.addAll(others);
        notifyDataSetChanged();
    }

    private static boolean isSameUser(String currentUserId, String storyUserId) {
        if (TextUtils.isEmpty(currentUserId) || TextUtils.isEmpty(storyUserId)) {
            return false;
        }
        return currentUserId.trim().equals(String.valueOf(storyUserId).trim());
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
        boolean isAddOnly = item.isAddButton;
        boolean isOwnWithStory = !isAddOnly && item.showAddBadge;

        holder.nameTv.setText(isAddOnly || isOwnWithStory
                ? "Your story"
                : (item.userName != null && !item.userName.isEmpty() ? item.userName : "Story"));

        String imageUrl;
        if (isAddOnly) {
            imageUrl = item.userAvatar;
        } else {
            imageUrl = item.thumbnailUrl != null && !item.thumbnailUrl.isEmpty()
                    ? item.thumbnailUrl
                    : item.userAvatar;
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.avatar).load(imageUrl).placeholder(R.drawable.ic_profile).into(holder.avatar);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_profile);
        }

        if (isAddOnly) {
            holder.addBadge.setVisibility(View.VISIBLE);
            holder.ringBorder.setBackgroundResource(R.drawable.bg_user_story_ring_unseen);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddStory();
                }
            });
            holder.addBadge.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddStory();
                }
            });
        } else {
            holder.addBadge.setVisibility(item.showAddBadge ? View.VISIBLE : View.GONE);
            holder.ringBorder.setBackgroundResource(
                    item.seen ? R.drawable.bg_user_story_ring_seen : R.drawable.bg_user_story_ring_unseen);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOpenStory(item);
                }
            });
            holder.addBadge.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddStory();
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
