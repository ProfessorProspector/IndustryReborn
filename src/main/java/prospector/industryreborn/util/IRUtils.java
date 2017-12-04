package prospector.industryreborn.util;

import net.minecraft.block.Block;
import prospector.industryreborn.core.IRConstants;
import prospector.shootingstar.ShootingStar;

public class IRUtils {
    public static Block getBlock(String name) {
        return ShootingStar.getBlock(IRConstants.MOD_ID, name);
    }
}
