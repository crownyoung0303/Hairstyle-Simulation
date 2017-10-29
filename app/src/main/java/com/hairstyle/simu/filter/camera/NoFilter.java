package com.hairstyle.simu.filter.camera;

import android.content.res.Resources;



public class NoFilter extends AFilter {

    public NoFilter(Resources res) {
        super(res);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh",
                "shader/base_fragment.sh");
    }

    @Override
    protected void onSizeChanged(int width, int height) {
    }
}
