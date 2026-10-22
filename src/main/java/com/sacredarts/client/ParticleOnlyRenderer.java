package com.sacredarts.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/** For particle-only entities — nothing to render. */
public class ParticleOnlyRenderer<T extends Entity> extends EntityRenderer<T> {
    public ParticleOnlyRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Intentionally empty.
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return new ResourceLocation("minecraft", "textures/block/black_concrete.png");
    }
}
