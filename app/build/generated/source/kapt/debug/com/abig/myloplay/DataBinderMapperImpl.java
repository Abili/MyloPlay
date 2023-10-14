package com.abig.myloplay;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.abig.myloplay.databinding.ActivityEditPlaylistBindingImpl;
import com.abig.myloplay.databinding.ActivityUserProfileBindingImpl;
import com.abig.myloplay.databinding.OthersPlaylistItemBindingImpl;
import com.abig.myloplay.databinding.OwnerPlItemBindingImpl;
import com.abig.myloplay.databinding.OwnerPlaylistItemBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYEDITPLAYLIST = 1;

  private static final int LAYOUT_ACTIVITYUSERPROFILE = 2;

  private static final int LAYOUT_OTHERSPLAYLISTITEM = 3;

  private static final int LAYOUT_OWNERPLITEM = 4;

  private static final int LAYOUT_OWNERPLAYLISTITEM = 5;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(5);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.abig.myloplay.R.layout.activity_edit_playlist, LAYOUT_ACTIVITYEDITPLAYLIST);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.abig.myloplay.R.layout.activity_user_profile, LAYOUT_ACTIVITYUSERPROFILE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.abig.myloplay.R.layout.others_playlist_item, LAYOUT_OTHERSPLAYLISTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.abig.myloplay.R.layout.owner_pl_item, LAYOUT_OWNERPLITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.abig.myloplay.R.layout.owner_playlist_item, LAYOUT_OWNERPLAYLISTITEM);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYEDITPLAYLIST: {
          if ("layout/activity_edit_playlist_0".equals(tag)) {
            return new ActivityEditPlaylistBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_edit_playlist is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYUSERPROFILE: {
          if ("layout/activity_user_profile_0".equals(tag)) {
            return new ActivityUserProfileBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_user_profile is invalid. Received: " + tag);
        }
        case  LAYOUT_OTHERSPLAYLISTITEM: {
          if ("layout/others_playlist_item_0".equals(tag)) {
            return new OthersPlaylistItemBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for others_playlist_item is invalid. Received: " + tag);
        }
        case  LAYOUT_OWNERPLITEM: {
          if ("layout/owner_pl_item_0".equals(tag)) {
            return new OwnerPlItemBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for owner_pl_item is invalid. Received: " + tag);
        }
        case  LAYOUT_OWNERPLAYLISTITEM: {
          if ("layout/owner_playlist_item_0".equals(tag)) {
            return new OwnerPlaylistItemBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for owner_playlist_item is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(2);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "activity");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(5);

    static {
      sKeys.put("layout/activity_edit_playlist_0", com.abig.myloplay.R.layout.activity_edit_playlist);
      sKeys.put("layout/activity_user_profile_0", com.abig.myloplay.R.layout.activity_user_profile);
      sKeys.put("layout/others_playlist_item_0", com.abig.myloplay.R.layout.others_playlist_item);
      sKeys.put("layout/owner_pl_item_0", com.abig.myloplay.R.layout.owner_pl_item);
      sKeys.put("layout/owner_playlist_item_0", com.abig.myloplay.R.layout.owner_playlist_item);
    }
  }
}
