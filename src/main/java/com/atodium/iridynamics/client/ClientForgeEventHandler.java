package com.atodium.iridynamics.client;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.ITipInfoRenderer;
import com.atodium.iridynamics.api.capability.*;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.heat.impl.HeatProcessPhasePortrait;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.block.equipment.AnvilBlock;
import com.atodium.iridynamics.common.block.equipment.CarvingTableBlock;
import com.atodium.iridynamics.common.block.equipment.ForgeBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.equipment.AnvilBlockEntity;
import com.atodium.iridynamics.common.blockEntity.equipment.CarvingTableBlockEntity;
import com.atodium.iridynamics.common.entity.ProjectileBaseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Iridynamics.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler {
    @SubscribeEvent
    public static void onOverlayRender(RenderGameOverlayEvent.PreLayer event) {
        if (event.getOverlay() != ForgeIngameGui.CROSSHAIR_ELEMENT) return;
        Pair<Long, Boolean> last = ProjectileBaseEntity.playerLastHitTime(Minecraft.getInstance().player);
        if (System.currentTimeMillis() - last.getLeft() > 600) return;
        (last.getRight() ? ProjectileBaseEntity.SHOOT_OVERLAY_KILLED : ProjectileBaseEntity.SHOOT_OVERLAY).draw(event.getMatrixStack(), Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2.0f - 8.0f, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2.0f - 8.0f, 16.0f, 16.0f);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void addItemTooltip(ItemTooltipEvent e) {
        ItemStack stack = e.getItemStack();
        List<Component> tooltip = e.getToolTip();
        stack.getCapability(HeatCapability.HEAT).ifPresent((heat) -> {
            if (MaterialEntry.containsMaterialEntry(stack)) {
                MaterialEntry entry = MaterialEntry.getItemMaterialEntry(stack);
                if (entry.shape().hasWeldingResult() && entry.material().hasHeatInfo()) {
                    double point = entry.material().getHeatInfo().getMeltingPoint() * 0.9;
                    if (heat.getTemperature() >= point) tooltip.add(new TextComponent(ChatFormatting.GREEN + I18n.get("iridynamics.info.heat.weldable")));
                }
            }
            tooltip.add(new TextComponent(ChatFormatting.DARK_RED + I18n.get("iridynamics.info.heat.heatable") + ChatFormatting.WHITE + " - " + String.format("%.1f", heat.getTemperature() - HeatModule.CELSIUS_ZERO) + "℃"));
            if (heat.getPhasePortrait() instanceof HeatProcessPhasePortrait process) {
                int progress = (int) (process.progress(heat.getEnergy()) * 20.0);
                tooltip.add(new TextComponent(ChatFormatting.DARK_AQUA + I18n.get("iridynamics.info.heat.progress") + " " + ChatFormatting.GREEN + "=".repeat(Math.max(0, progress)) + ">" + ChatFormatting.WHITE + "-".repeat(Math.max(0, 19 - progress))));
            }
        });
        stack.getCapability(ForgingCapability.FORGING).ifPresent((forging) -> {
            if (forging.processed()) tooltip.add(new TextComponent(ChatFormatting.WHITE + I18n.get("iridynamics.info.forging.processed")));
            else tooltip.add(new TextComponent(ChatFormatting.GRAY + I18n.get("iridynamics.info.forging.unprocessed")));
        });
        stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).ifPresent((container) -> {
            LiquidContainerCapability cap = (LiquidContainerCapability) container;
            double temperature = cap.getTemperature();
            int usedCapacity = cap.usedCapacity();
            tooltip.add(new TextComponent(ChatFormatting.WHITE + I18n.get("iridynamics.info.liquid_container.title") + " - " + String.format("%.1f", temperature - HeatModule.CELSIUS_ZERO) + "℃" + " - " + usedCapacity + "/" + cap.liquidCapacity()));
            cap.getAllMaterials().forEach((material, unit) -> tooltip.add(new TextComponent(ChatFormatting.GRAY + "---- " + material.getLocalizedName() + ChatFormatting.AQUA + " [" + (temperature >= material.getHeatInfo().getMeltingPoint() ? I18n.get("iridynamics.info.liquid_container.liquid") : I18n.get("iridynamics.info.liquid_container.solid")) + "]" + ChatFormatting.GRAY + " - " + unit + "/" + usedCapacity)));
        });
        stack.getCapability(InventoryCapability.INVENTORY).ifPresent((inventory) -> {
            int slots = inventory.getSlots();
            int usedSlots = 0;
            for (int i = 0; i < slots; i++) if (!inventory.getStackInSlot(i).isEmpty()) usedSlots++;
            tooltip.add(new TextComponent(ChatFormatting.WHITE + I18n.get("iridynamics.info.inventory.title") + " - " + usedSlots + "/" + slots));
            for (int i = 0; i < slots; i++) {
                ItemStack invStack = inventory.getStackInSlot(i);
                if (invStack.isEmpty()) continue;
                StringBuilder add = new StringBuilder();
                invStack.getCapability(HeatCapability.HEAT).ifPresent((heat) -> add.append(ChatFormatting.GRAY).append(" - ").append(String.format("%.1f", heat.getTemperature() - HeatModule.CELSIUS_ZERO)).append("℃"));
                if (MaterialEntry.containsMaterialEntry(invStack)) add.append(ChatFormatting.GRAY).append(" - ").append(ChatFormatting.GREEN).append(MaterialEntry.getItemMaterialEntry(invStack).shape().getUnit()).append(ChatFormatting.GRAY).append("L");
                tooltip.add(new TextComponent(ChatFormatting.GRAY + "---- " + invStack.getDisplayName().getString() + ChatFormatting.AQUA + " [" + (i + 1) + "]" + add));
            }
        });
        stack.getCapability(CarvingCapability.CARVING).ifPresent((carving) -> {
            if (carving.processed()) tooltip.add(new TextComponent(ChatFormatting.WHITE + I18n.get("iridynamics.info.carving.processed")));
            else tooltip.add(new TextComponent(ChatFormatting.GRAY + I18n.get("iridynamics.info.carving.unprocessed")));
        });
    }

    @SubscribeEvent
    public static void renderBlockTipInfo(DrawSelectionEvent.HighlightBlock event) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockHitResult result = event.getTarget();
        BlockPos pos = result.getBlockPos();
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof ITipInfoRenderer renderer) renderer.render(event.getCamera(), result, event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTicks());
    }

    @SubscribeEvent
    public static void drawBlockHighlight(DrawSelectionEvent.HighlightBlock event) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockHitResult result = event.getTarget();
        BlockPos pos = result.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() == ModBlocks.FORGE.get() && result.getDirection() == Direction.UP) {
            VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
            Vec3 cameraPos = event.getCamera().getPosition();
            PoseStack transform = event.getPoseStack();
            transform.pushPose();
            transform.translate(pos.getX() - cameraPos.x + 0.5, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z + 0.5);
            transform.mulPose(Vector3f.YP.rotationDegrees(RendererUtil.getDirectionAngel(state.getValue(ForgeBlock.DIRECTION).getOpposite())));
            transform.translate(-0.5, 0.0, -0.5);
            PoseStack.Pose pose = transform.last();
            consumer.vertex(pose.pose(), 0.5f, 0.5f, 0.0f).color(0, 0, 0, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
            consumer.vertex(pose.pose(), 0.5f, 0.5f, 1.0f).color(0, 0, 0, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
            transform.popPose();
        } else if (state.getBlock() == ModBlocks.ANVIL.get() && result.getDirection() == Direction.UP) {
            VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
            Vec3 cameraPos = event.getCamera().getPosition();
            PoseStack transform = event.getPoseStack();
            transform.pushPose();
            transform.translate(pos.getX() - cameraPos.x + 0.5, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z + 0.5);
            transform.mulPose(Vector3f.YP.rotationDegrees(RendererUtil.getDirectionAngel(state.getValue(AnvilBlock.DIRECTION).getOpposite())));
            transform.translate(-0.5, 0.0, -0.5);
            PoseStack.Pose pose = transform.last();
            float height = 0.5625f + Math.max(state.getValue(AnvilBlock.HEIGHT_LEFT), state.getValue(AnvilBlock.HEIGHT_RIGHT)) / 16.0f;
            Vec3 location = MathUtil.transformPosition(MathUtil.minus(result.getLocation(), pos), state.getValue(AnvilBlock.DIRECTION));
            AtomicReference<AnvilBlockEntity.Inventory> inventory = new AtomicReference<>();
            level.getBlockEntity(pos, ModBlockEntities.ANVIL.get()).ifPresent((anvil) -> inventory.set(anvil.getInventory()));
            consumer.vertex(pose.pose(), 0.125f, height, 0.1875f).color(0.0f, 0.0f, 0.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
            consumer.vertex(pose.pose(), 0.125f, height, 0.8125f).color(0.0f, 0.0f, 0.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
            consumer.vertex(pose.pose(), 0.5625f, height, 0.1875f).color(0.0f, 0.0f, 0.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
            consumer.vertex(pose.pose(), 0.5625f, height, 0.8125f).color(0.0f, 0.0f, 0.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
            if (location.x <= 0.5625 && !inventory.get().left().isEmpty()) {
                for (int i = 0; i <= 7; i++) {
                    float z = 0.3125f + i / 16.0f;
                    consumer.vertex(pose.pose(), 0.125f, height, z).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 1.0f, 0.0f, 0.0f).endVertex();
                    consumer.vertex(pose.pose(), 0.5625f, height, z).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 1.0f, 0.0f, 0.0f).endVertex();
                }
                for (int i = 0; i <= 7; i++) {
                    float x = 0.125f + i / 16.0f;
                    consumer.vertex(pose.pose(), x, height, 0.3125f).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
                    consumer.vertex(pose.pose(), x, height, 0.75f).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
                }
            }
            if (location.x > 0.5625 && !inventory.get().right().isEmpty()) {
                for (int i = 0; i <= 7; i++) {
                    float z = 0.3125f + i / 16.0f;
                    consumer.vertex(pose.pose(), 0.5625f, height, z).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 1.0f, 0.0f, 0.0f).endVertex();
                    consumer.vertex(pose.pose(), 1.0f, height, z).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 1.0f, 0.0f, 0.0f).endVertex();
                }
                for (int i = 0; i <= 7; i++) {
                    float x = 0.5625f + i / 16.0f;
                    consumer.vertex(pose.pose(), x, height, 0.3125f).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
                    consumer.vertex(pose.pose(), x, height, 0.75f).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
                }
            }
            transform.popPose();
        } else if (state.getBlock() == ModBlocks.CARVING_TABLE.get() && result.getDirection() == Direction.UP) {
            VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
            Vec3 cameraPos = event.getCamera().getPosition();
            PoseStack transform = event.getPoseStack();
            transform.pushPose();
            transform.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            PoseStack.Pose pose = transform.last();
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CarvingTableBlockEntity table && !table.isEmpty()) {
                float height = 0.25f + state.getValue(CarvingTableBlock.HEIGHT) / 16.0f;
                for (int i = 0; i <= 12; i++) {
                    float z = 0.125f + i / 16.0f;
                    consumer.vertex(pose.pose(), 0.125f, height, z).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 1.0f, 0.0f, 0.0f).endVertex();
                    consumer.vertex(pose.pose(), 0.875f, height, z).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 1.0f, 0.0f, 0.0f).endVertex();
                }
                for (int i = 0; i <= 12; i++) {
                    float x = 0.125f + i / 16.0f;
                    consumer.vertex(pose.pose(), x, height, 0.125f).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
                    consumer.vertex(pose.pose(), x, height, 0.875f).color(0.0f, 0.0f, 1.0f, 0.4f).normal(pose.normal(), 0.0f, 0.0f, 1.0f).endVertex();
                }
            }
            transform.popPose();
        }
    }
}