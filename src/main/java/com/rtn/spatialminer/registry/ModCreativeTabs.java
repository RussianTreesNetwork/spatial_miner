package com.rtn.spatialminer.registry;

import com.rtn.spatialminer.SpatialMiner;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpatialMiner.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPATIAL_MINER_TAB =
            CREATIVE_TABS.register("spatial_miner_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.spatialminer"))
                    .icon(() -> new ItemStack(ModBlocks.MINER_BLOCK.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModBlocks.BATTERY_BLOCK.get());
                        output.accept(ModBlocks.MINER_BLOCK.get());
                        output.accept(ModBlocks.CABLE_BLOCK.get());
                        output.accept(ModBlocks.PROTECTION_BLOCK.get());
                    })
                    .build());
}