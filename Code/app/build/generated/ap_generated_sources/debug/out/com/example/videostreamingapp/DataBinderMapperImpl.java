package com.example.videostreamingapp;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.example.videostreamingapp.databinding.ActivityMainBindingImpl;
import com.example.videostreamingapp.databinding.ItemMenuCategoryBindingImpl;
import com.example.videostreamingapp.databinding.ItemMenuSubCategoryBindingImpl;
import com.example.videostreamingapp.databinding.LeftNavHeaderBindingImpl;
import com.example.videostreamingapp.databinding.PartialNavMenuBindingImpl;
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
  private static final int LAYOUT_ACTIVITYMAIN = 1;

  private static final int LAYOUT_ITEMMENUCATEGORY = 2;

  private static final int LAYOUT_ITEMMENUSUBCATEGORY = 3;

  private static final int LAYOUT_LEFTNAVHEADER = 4;

  private static final int LAYOUT_PARTIALNAVMENU = 5;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(5);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.example.videostreamingapp.R.layout.activity_main, LAYOUT_ACTIVITYMAIN);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.example.videostreamingapp.R.layout.item_menu_category, LAYOUT_ITEMMENUCATEGORY);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.example.videostreamingapp.R.layout.item_menu_sub_category, LAYOUT_ITEMMENUSUBCATEGORY);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.example.videostreamingapp.R.layout.left_nav_header, LAYOUT_LEFTNAVHEADER);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.example.videostreamingapp.R.layout.partial_nav_menu, LAYOUT_PARTIALNAVMENU);
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
        case  LAYOUT_ACTIVITYMAIN: {
          if ("layout/activity_main_0".equals(tag)) {
            return new ActivityMainBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_main is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMMENUCATEGORY: {
          if ("layout/item_menu_category_0".equals(tag)) {
            return new ItemMenuCategoryBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_menu_category is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMMENUSUBCATEGORY: {
          if ("layout/item_menu_sub_category_0".equals(tag)) {
            return new ItemMenuSubCategoryBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_menu_sub_category is invalid. Received: " + tag);
        }
        case  LAYOUT_LEFTNAVHEADER: {
          if ("layout/left_nav_header_0".equals(tag)) {
            return new LeftNavHeaderBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for left_nav_header is invalid. Received: " + tag);
        }
        case  LAYOUT_PARTIALNAVMENU: {
          if ("layout/partial_nav_menu_0".equals(tag)) {
            return new PartialNavMenuBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for partial_nav_menu is invalid. Received: " + tag);
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
    static final SparseArray<String> sKeys = new SparseArray<String>(5);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "email");
      sKeys.put(2, "isLogin");
      sKeys.put(3, "name");
      sKeys.put(4, "title");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(5);

    static {
      sKeys.put("layout/activity_main_0", com.example.videostreamingapp.R.layout.activity_main);
      sKeys.put("layout/item_menu_category_0", com.example.videostreamingapp.R.layout.item_menu_category);
      sKeys.put("layout/item_menu_sub_category_0", com.example.videostreamingapp.R.layout.item_menu_sub_category);
      sKeys.put("layout/left_nav_header_0", com.example.videostreamingapp.R.layout.left_nav_header);
      sKeys.put("layout/partial_nav_menu_0", com.example.videostreamingapp.R.layout.partial_nav_menu);
    }
  }
}
