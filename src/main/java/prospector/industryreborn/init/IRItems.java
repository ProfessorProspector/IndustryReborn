package prospector.industryreborn.init;

import ic2.core.item.tool.ItemElectricTool;
import prospector.industryreborn.core.IRConstants;
import prospector.industryreborn.item.ItemIRChainsaw;
import prospector.industryreborn.item.ItemIRDrill;
import prospector.shootingstar.ItemCompound;
import prospector.shootingstar.ShootingStar;

public class IRItems {
	public static void init() {
		ShootingStar.registerItem(new ItemCompound(IRConstants.MOD_ID, new ItemIRDrill("advanced#drill", 80, 1000, 300000, ItemElectricTool.HarvestLevel.Diamond, true)));
		ShootingStar.registerItem(new ItemCompound(IRConstants.MOD_ID, new ItemIRChainsaw("diamond#chainsaw", 80, 1000, 30000, ItemElectricTool.HarvestLevel.Diamond)));
		ShootingStar.registerItem(new ItemCompound(IRConstants.MOD_ID, new ItemIRChainsaw("iridium#chainsaw", 80, 1000, 300000, ItemElectricTool.HarvestLevel.Iridium)));
		ShootingStar.registerItem(new ItemCompound(IRConstants.MOD_ID, new ItemIRChainsaw("advanced#chainsaw", 80, 1000, 300000, ItemElectricTool.HarvestLevel.Diamond, true)));
	}
}
