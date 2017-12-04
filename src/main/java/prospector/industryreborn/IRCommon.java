package prospector.industryreborn;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import prospector.industryreborn.core.IRConstants;
import prospector.industryreborn.init.IRItems;
import prospector.shootingstar.ShootingStar;
import prospector.industryreborn.init.IRBlocks;
import prospector.industryreborn.tiles.TileEntityCentrifuge;

@Mod.EventBusSubscriber
public class IRCommon {

    static void registerFurnace(ItemStack output, ItemStack input, float experience) {
        GameRegistry.addSmelting(input, output, experience);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ShootingStar.registerModels(IRConstants.MOD_ID);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IRBlocks.init();
        ShootingStar.registerBlocks(IRConstants.MOD_ID, event);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IRItems.init();
        ShootingStar.registerItems(IRConstants.MOD_ID, event);
    }

    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityCentrifuge.class, "IRTestMachine");
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}
