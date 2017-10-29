package com.simoncherry.arcamera.util;

import com.simoncherry.arcamera.R;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.CubeMapTexture;

/**
 * Created by wecut-simon on 2017/10/2.
 */

public class MaterialFactory {

    public final static int MATERIAL_DEFAULT = 0;
    public final static int MATERIAL_SKY_CUBE = 1;
    public final static int MATERIAL_FROST_CUBE = 2;

    public static Material getMaterialById(int id) {
        switch (id) {
            case MATERIAL_SKY_CUBE:
                return getSkyCubeMaterial();
            case MATERIAL_FROST_CUBE:
                return getFrostCubeMaterial();
            default:
                return new Material();
        }
    }

    public static Material getCubeMaterial(int[] resourceIds) {
        Material cubeMapMaterial = new Material();
        cubeMapMaterial.enableLighting(true);
        cubeMapMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        try {
            CubeMapTexture envMap = new CubeMapTexture("monkeyCubeMap",
                    resourceIds);
            envMap.isEnvironmentTexture(true);
            cubeMapMaterial.addTexture(envMap);
            cubeMapMaterial.setColorInfluence(0);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        return cubeMapMaterial;
    }

    public static Material getSkyCubeMaterial() {
        int[] resourceIds = new int[] {
                R.drawable.posx, R.drawable.negx,
                R.drawable.posy, R.drawable.negy,
                R.drawable.posz, R.drawable.negz };
        return getCubeMaterial(resourceIds);
    }

    public static Material getFrostCubeMaterial() {
        int[] resourceIds = new int[] {
                R.drawable.posx2, R.drawable.negx2,
                R.drawable.posy2, R.drawable.negy2,
                R.drawable.posz2, R.drawable.negz2 };
        return getCubeMaterial(resourceIds);
    }
}
