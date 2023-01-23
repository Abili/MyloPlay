// Generated by view binder compiler. Do not edit!
package com.abig.myloplay.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.abig.myloplay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityHomeBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final RecyclerView currentUserPlaylists;

  @NonNull
  public final TextView currentUserTv;

  @NonNull
  public final FloatingActionButton floatingActionButton;

  @NonNull
  public final TextView friendsTv;

  @NonNull
  public final RecyclerView otherUsersPlaylists;

  private ActivityHomeBinding(@NonNull RelativeLayout rootView,
      @NonNull RecyclerView currentUserPlaylists, @NonNull TextView currentUserTv,
      @NonNull FloatingActionButton floatingActionButton, @NonNull TextView friendsTv,
      @NonNull RecyclerView otherUsersPlaylists) {
    this.rootView = rootView;
    this.currentUserPlaylists = currentUserPlaylists;
    this.currentUserTv = currentUserTv;
    this.floatingActionButton = floatingActionButton;
    this.friendsTv = friendsTv;
    this.otherUsersPlaylists = otherUsersPlaylists;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityHomeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityHomeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_home, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityHomeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.current_user_playlists;
      RecyclerView currentUserPlaylists = ViewBindings.findChildViewById(rootView, id);
      if (currentUserPlaylists == null) {
        break missingId;
      }

      id = R.id.current_user_tv;
      TextView currentUserTv = ViewBindings.findChildViewById(rootView, id);
      if (currentUserTv == null) {
        break missingId;
      }

      id = R.id.floatingActionButton;
      FloatingActionButton floatingActionButton = ViewBindings.findChildViewById(rootView, id);
      if (floatingActionButton == null) {
        break missingId;
      }

      id = R.id.friendsTv;
      TextView friendsTv = ViewBindings.findChildViewById(rootView, id);
      if (friendsTv == null) {
        break missingId;
      }

      id = R.id.other_users_playlists;
      RecyclerView otherUsersPlaylists = ViewBindings.findChildViewById(rootView, id);
      if (otherUsersPlaylists == null) {
        break missingId;
      }

      return new ActivityHomeBinding((RelativeLayout) rootView, currentUserPlaylists, currentUserTv,
          floatingActionButton, friendsTv, otherUsersPlaylists);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
