package com.sacredarts.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sacredarts.entity.ForgedWispEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** The wisp is pure particles — nothing to render. */
public class ForgedWispRenderer extends EntityRenderer<ForgedWispEntity> {
    public ForgedWispRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ForgedWispEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Intentionally empty.
    }

    @Override
    public ResourceLocation getTextureLocation(ForgedWispEntity entity) {
        return new ResourceLocation("minecraft", "textures/block/black_concrete.png");
    }
}
