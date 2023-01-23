// Generated by data binding compiler. Do not edit!
package com.abig.myloplay.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.abig.myloplay.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class OwnerPlaylistItemBinding extends ViewDataBinding {
  @NonNull
  public final ImageView ownersProfileImage;

  @NonNull
  public final TextView textViewNumSongs;

  @NonNull
  public final TextView textViewPlaylistName;

  @NonNull
  public final TextView textViewUserName;

  @NonNull
  public final LinearLayout textViewsContainer;

  protected OwnerPlaylistItemBinding(Object _bindingComponent, View _root, int _localFieldCount,
      ImageView ownersProfileImage, TextView textViewNumSongs, TextView textViewPlaylistName,
      TextView textViewUserName, LinearLayout textViewsContainer) {
    super(_bindingComponent, _root, _localFieldCount);
    this.ownersProfileImage = ownersProfileImage;
    this.textViewNumSongs = textViewNumSongs;
    this.textViewPlaylistName = textViewPlaylistName;
    this.textViewUserName = textViewUserName;
    this.textViewsContainer = textViewsContainer;
  }

  @NonNull
  public static OwnerPlaylistItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.owner_playlist_item, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static OwnerPlaylistItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<OwnerPlaylistItemBinding>inflateInternal(inflater, R.layout.owner_playlist_item, root, attachToRoot, component);
  }

  @NonNull
  public static OwnerPlaylistItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.owner_playlist_item, null, false, component)
   */
  @NonNull
  @Deprecated
  public static OwnerPlaylistItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<OwnerPlaylistItemBinding>inflateInternal(inflater, R.layout.owner_playlist_item, null, false, component);
  }

  public static OwnerPlaylistItemBinding bind(@NonNull View view) {
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
  public static OwnerPlaylistItemBinding bind(@NonNull View view, @Nullable Object component) {
    return (OwnerPlaylistItemBinding)bind(component, view, R.layout.owner_playlist_item);
  }
}
