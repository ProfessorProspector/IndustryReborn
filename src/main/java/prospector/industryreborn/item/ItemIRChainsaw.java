package prospector.industryreborn.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.IHitSoundOverride;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class ItemIRChainsaw extends ItemIRElectricTool implements IHitSoundOverride {
	public boolean isAdvanced = false;

	public ItemIRChainsaw(String name, int operationEnergyCost, int transferLimit, int maxEnergy, ItemElectricTool.HarvestLevel harvestLevel, boolean isAdvanced) {
		super(name + "#chainsaw", operationEnergyCost, transferLimit, maxEnergy, harvestLevel, EnumSet.of(ToolClass.Axe, ToolClass.Sword, ToolClass.Shears));
		if (isAdvanced) {
			this.isAdvanced = true;
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			return super.onItemRightClick(world, player, hand);
		} else {
			if (IC2.keyboard.isModeSwitchKeyDown(player)) {
				NBTTagCompound compoundTag = StackUtil.getOrCreateNbtData(StackUtil.get(player, hand));
				if (compoundTag.getBoolean("disableShear")) {
					compoundTag.setBoolean("disableShear", false);
					IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.normal");
				} else {
					compoundTag.setBoolean("disableShear", true);
					IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.noShear");
				}
			}

			return super.onItemRightClick(world, player, hand);
		}
	}

	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		if (slot != EntityEquipmentSlot.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		} else {
			Multimap<String, AttributeModifier> ret = HashMultimap.create();
			if (ElectricItem.manager.canUse(stack, cost)) {
				ret.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double) this.attackSpeed, 0));
				ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", 9.0D, 0));
			}

			return ret;
		}
	}

	public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
		ElectricItem.manager.use(itemstack, cost, attacker);
		if (attacker instanceof EntityPlayer && entityliving instanceof EntityCreeper && entityliving.getHealth() <= 0.0F) {
			IC2.achievements.issueAchievement((EntityPlayer) attacker, "killCreeperChainsaw");
		}

		return true;
	}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		if (IC2.platform.isSimulating()) {
			Entity entity = event.getTarget();
			EntityPlayer player = event.getEntityPlayer();
			ItemStack itemstack = player.inventory.getStackInSlot(player.inventory.currentItem);
			if (!itemstack.isEmpty() && itemstack.getItem() == this && entity instanceof IShearable && !StackUtil.getOrCreateNbtData(itemstack).getBoolean("disableShear") && ElectricItem.manager.use(itemstack, cost, player)) {
				IShearable target = (IShearable) entity;
				World world = entity.getEntityWorld();
				BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
				if (target.isShearable(itemstack, world, pos)) {
					List<ItemStack> drops = target.onSheared(itemstack, world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack));

					EntityItem ent;
					for (Iterator var9 = drops.iterator(); var9.hasNext(); ent.motionZ += (double) ((itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F)) {
						ItemStack stack = (ItemStack) var9.next();
						ent = entity.entityDropItem(stack, 1.0F);
						ent.motionY += (double) (itemRand.nextFloat() * 0.05F);
						ent.motionX += (double) ((itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F);
					}
				}
			}

		}
	}

	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		if (!IC2.platform.isSimulating()) {
			return false;
		} else if (StackUtil.getOrCreateNbtData(itemstack).getBoolean("disableShear")) {
			return false;
		} else {
			World world = player.getEntityWorld();
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof IShearable) {
				IShearable target = (IShearable) block;
				if (target.isShearable(itemstack, world, pos) && ElectricItem.manager.use(itemstack, cost, player)) {
					List<ItemStack> drops = target.onSheared(itemstack, world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack));
					Iterator var9 = drops.iterator();

					while (var9.hasNext()) {
						ItemStack stack = (ItemStack) var9.next();
						StackUtil.dropAsEntity(world, pos, stack);
					}

					player.addStat(StatList.getBlockStats(block), 1);
				}
			}

			return false;
		}
	}

	@SideOnly(Side.CLIENT)
	public String getHitSoundForBlock(EntityPlayerSP player, World world, BlockPos pos, ItemStack stack) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	public String getBreakSoundForBlock(EntityPlayerSP player, World world, BlockPos pos, ItemStack stack) {
		return null;
	}

	public String getIdleSound(EntityLivingBase player, ItemStack stack) {
		return "Tools/Chainsaw/ChainsawIdle.ogg";
	}

	public String getStopSound(EntityLivingBase player, ItemStack stack) {
		return "Tools/Chainsaw/ChainsawStop.ogg";
	}
}
