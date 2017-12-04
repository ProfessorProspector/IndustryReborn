package prospector.industryreborn.init;

import prospector.industryreborn.blocks.BlockMachine;
import prospector.industryreborn.core.IRConstants;
import prospector.shootingstar.BlockCompound;
import prospector.shootingstar.ShootingStar;
import prospector.industryreborn.tiles.TileEntityCentrifuge;

public class IRBlocks {

    public static void init() {
        ShootingStar.registerBlock(new BlockCompound(IRConstants.MOD_ID, new BlockMachine("test_machine", new TileEntityCentrifuge())));
    }
}
