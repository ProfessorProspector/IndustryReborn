package prospector.industryreborn.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import prospector.industryreborn.util.IRUtils;

public class IRTab {
    public static final CreativeTabs TAB = new CreativeTabs(IRConstants.MOD_ID) {
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(IRUtils.getBlock("test_machine"));
        }
    };
}
