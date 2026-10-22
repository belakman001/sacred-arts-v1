package com.sacredarts.entity;

import com.sacredarts.SacredArts;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SacredArts.MODID);

    public static final RegistryObject<EntityType<ForgedWispEntity>> FORGED_WISP =
            TYPES.register("forged_wisp", () -> EntityType.Builder
                    .<ForgedWispEntity>of(ForgedWispEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("forged_wisp"));

    public static final RegistryObject<EntityType<FireFistEntity>> FIRE_FIST =
            TYPES.register("fire_fist", () -> EntityType.Builder
                    .<FireFistEntity>of(FireFistEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("fire_fist"));

    public static final RegistryObject<EntityType<BladeProjectileEntity>> BLADE =
            TYPES.register("blade", () -> EntityType.Builder
                    .<BladeProjectileEntity>of(BladeProjectileEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("blade"));

    public static final RegistryObject<EntityType<NeedleProjectileEntity>> NEEDLE =
            TYPES.register("needle", () -> EntityType.Builder
                    .<NeedleProjectileEntity>of(NeedleProjectileEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("needle"));
}
