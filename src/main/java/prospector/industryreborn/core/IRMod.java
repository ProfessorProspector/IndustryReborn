package prospector.industryreborn.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import prospector.industryreborn.IRCommon;

@Mod(modid = IRConstants.MOD_ID, name = IRConstants.MOD_NAME, version = IRConstants.MOD_VERSION_MAJOR + "." + IRConstants.MOD_VERSION_MINOR + "." + IRConstants.MOD_VERSION_PATCH, acceptedMinecraftVersions = IRConstants.MINECRAFT_VERSIONS)
public class IRMod {

    @Mod.Instance(IRConstants.MOD_ID)
    public static IRMod instance;
    @SidedProxy(clientSide = IRConstants.CLIENT_PROXY_CLASS, serverSide = IRConstants.SERVER_PROXY_CLASS)
    public static IRCommon proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
