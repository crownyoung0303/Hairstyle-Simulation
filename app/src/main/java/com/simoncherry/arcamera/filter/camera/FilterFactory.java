package com.simoncherry.arcamera.filter.camera;

import android.content.res.Resources;

import com.simoncherry.arcamera.R;
import com.simoncherry.arcamera.model.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 2017/7/6.
 */

public class FilterFactory {

    public static AFilter getFilter(Resources res, int menuId) {
        switch (menuId) {
            case R.id.menu_camera_default:
                return new NoFilter(res);


            case R.id.menu_camera_landmark:
                return new LandmarkFilter(res);


            default:
                return new NoFilter(res);
        }
    }

    public static List<Filter> getPresetFilter() {
        List<Filter> filterList = new ArrayList<>();
        filterList.add(new Filter(R.id.menu_camera_default, R.drawable.filter_thumb_0, "原图"));

        return filterList;
    }

    public static List<Filter> getPresetEffect() {
        List<Filter> filterList = new ArrayList<>();
        filterList.add(new Filter(R.id.menu_camera_default, R.drawable.ic_remove, "原图"));

        return filterList;
    }
}
