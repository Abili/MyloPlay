// Generated by data binding compiler. Do not edit!
package com.abig.myloplay.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.abig.myloplay.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class OthersPlaylistItemBinding extends ViewDataBinding {
  @NonNull
  public final CircleImageView othersProfileImage;

  @NonNull
  public final TextView textViewNumSongs;

  @NonNull
  public final TextView textViewPlaylistName;

  @NonNull
  public final TextView textViewUserName;

  @NonNull
  public final LinearLayout textViewsContainer;

  @NonNull
  public final LinearLayout textViewsContainer2;

  protected OthersPlaylistItemBinding(Object _bindingComponent, View _root, int _localFieldCount,
      CircleImageView othersProfileImage, TextView textViewNumSongs, TextView textViewPlaylistName,
      TextView textViewUserName, LinearLayout textViewsContainer,
      LinearLayout textViewsContainer2) {
    super(_bindingComponent, _root, _localFieldCount);
    this.othersProfileImage = othersProfileImage;
    this.textViewNumSongs = textViewNumSongs;
    this.textViewPlaylistName = textViewPlaylistName;
    this.textViewUserName = textViewUserName;
    this.textViewsContainer = textViewsContainer;
    this.textViewsContainer2 = textViewsContainer2;
  }

  @NonNull
  public static OthersPlaylistItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.others_playlist_item, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static OthersPlaylistItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<OthersPlaylistItemBinding>inflateInternal(inflater, R.layout.others_playlist_item, root, attachToRoot, component);
  }

  @NonNull
  public static OthersPlaylistItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.others_playlist_item, null, false, component)
   */
  @NonNull
  @Deprecated
  public static OthersPlaylistItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<OthersPlaylistItemBinding>inflateInternal(inflater, R.layout.others_playlist_item, null, false, component);
  }

  public static OthersPlaylistItemBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static OthersPlaylistItemBinding bind(@NonNull View view, @Nullable Object component) {
    return (OthersPlaylistItemBinding)bind(component, view, R.layout.others_playlist_item);
  }
}
