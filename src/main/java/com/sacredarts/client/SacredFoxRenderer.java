package com.sacredarts.client;

import com.sacredarts.SacredArts;
import com.sacredarts.entity.SacredFoxEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Renders the Sacred Fox with Kaleb's purple-marked texture. */
public class SacredFoxRenderer extends MobRenderer<SacredFoxEntity, SacredFoxModel<SacredFoxEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(SacredArts.MODID, "textures/entity/sacred_fox.png");

    public SacredFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new SacredFoxModel<>(context.bakeLayer(SacredFoxModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(SacredFoxEntity entity) {
        return TEXTURE;
    }
}
