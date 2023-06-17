package com.atodium.iridynamics.common.item;

import com.atodium.iridynamics.common.entity.BulletEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GunItem extends Item {
    public GunItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        Vec3 vec = player.getLookAngle();
        BulletEntity bullet = new BulletEntity(level, player);
        bullet.shoot(vec.x, vec.y, vec.z, 12, 0);
        level.addFreshEntity(bullet);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}