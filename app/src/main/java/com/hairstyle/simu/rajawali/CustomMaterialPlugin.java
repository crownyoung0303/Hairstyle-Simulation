package com.hairstyle.simu.rajawali;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.plugins.IMaterialPlugin;
import org.rajawali3d.materials.shaders.AShader;
import org.rajawali3d.materials.shaders.IShaderFragment;



public class CustomMaterialPlugin implements IMaterialPlugin {
    private CustomMaterialFragmentShaderFragment mFragmentShader;

    public CustomMaterialPlugin()
    {
        mFragmentShader = new CustomMaterialFragmentShaderFragment();
    }

    @Override
    public Material.PluginInsertLocation getInsertLocation() {
        return Material.PluginInsertLocation.PRE_LIGHTING;
    }

    @Override
    public IShaderFragment getVertexShaderFragment() {
        return null;
    }

    @Override
    public IShaderFragment getFragmentShaderFragment() {
        return mFragmentShader;
    }

    @Override
    public void bindTextures(int nextIndex) {}
    @Override
    public void unbindTextures() {}

    private class CustomMaterialFragmentShaderFragment extends AShader implements IShaderFragment
    {
        public final static String SHADER_ID = "CUSTOM_MATERIAL_FRAGMENT";

        public CustomMaterialFragmentShaderFragment()
        {
            super(ShaderType.FRAGMENT_SHADER_FRAGMENT);
        }

        @Override
        public void main() {
            RVec2 gTexCoord = (RVec2) getGlobal(DefaultShaderVar.G_TEXTURE_COORD);
            RVec4 gColor = (RVec4) getGlobal(DefaultShaderVar.G_COLOR);
            RFloat uTime = (RFloat) getGlobal(DefaultShaderVar.U_TIME);

            gColor.r().assign(gTexCoord.s());
            gColor.g().assign(gTexCoord.t());
            RFloat time = new RFloat("time");
            time.assign(new RFloat(0.5f).add(sin(uTime.multiply(3.0f))));
            gColor.b().assign(time);
            gColor.a().assign(1.0f);
        }

        @Override
        public String getShaderId() {
            return SHADER_ID;
        }

        @Override
        public Material.PluginInsertLocation getInsertLocation() {
            return Material.PluginInsertLocation.IGNORE;
        }

        @Override
        public void bindTextures(int nextIndex) {}
        @Override
        public void unbindTextures() {}
    }
}
