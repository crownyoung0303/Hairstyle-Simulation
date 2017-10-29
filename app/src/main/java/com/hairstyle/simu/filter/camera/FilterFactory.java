package com.hairstyle.simu.filter.camera;

import android.content.res.Resources;

import com.hairstyle.simu.R;
import com.hairstyle.simu.model.Filter;

import java.util.ArrayList;
import java.util.List;


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
