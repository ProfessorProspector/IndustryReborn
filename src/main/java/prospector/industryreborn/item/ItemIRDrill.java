package prospector.industryreborn.item;

import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.IHitSoundOverride;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class ItemIRDrill extends ItemIRElectricTool implements IHitSoundOverride {
    public boolean isAdvanced = false;

    public ItemIRDrill(String name, int operationEnergyCost, int transferLimit, int maxEnergy, ItemElectricTool.HarvestLevel harvestLevel, boolean isAdvanced) {
        super(name+"#drill", operationEnergyCost, transferLimit, maxEnergy, harvestLevel, EnumSet.of(ToolClass.Pickaxe, ToolClass.Shovel));
        if (isAdvanced) {
            this.isAdvanced = true;
        }
    }

    private static EntityPlayer getPlayerHoldingItem(ItemStack stack) {
        if (IC2.platform.isRendering()) {
            EntityPlayer player = IC2.platform.getPlayerInstance();
            if (player != null && player.inventory.getCurrentItem() == stack) {
                return player;
            }
        } else {
            Iterator var3 = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().iterator();

            while (var3.hasNext()) {
                EntityPlayer player = (EntityPlayer) var3.next();
                if (player.inventory.getCurrentItem() == stack) {
                    return player;
                }
            }
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public String getHitSoundForBlock(EntityPlayerSP player, World world, BlockPos pos, ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        float hardness = state.getBlockHardness(world, pos);
        return hardness <= 1.0F && hardness >= 0.0F ? "Tools/Drill/DrillSoft.ogg" : "Tools/Drill/DrillHard.ogg";
    }

    @SideOnly(Side.CLIENT)
    public String getBreakSoundForBlock(EntityPlayerSP player, World world, BlockPos pos, ItemStack stack) {
        if (player.capabilities.isCreativeMode) {
            return null;
        } else {
            IBlockState state = world.getBlockState(pos);
            float hardness = state.getBlockHardness(world, pos);
            return (double) hardness <= 0.5D && ElectricItem.manager.canUse(stack, this.cost) ? "Tools/Drill/DrillSoft.ogg" : null;
        }
    }

    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        float speed = super.getStrVsBlock(stack, state);
        EntityPlayer player = getPlayerHoldingItem(stack);
        if (player != null) {
            if (player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(player)) {
                speed *= 5.0F;
            }

            if (!player.onGround) {
                speed *= 5.0F;
            }
        }

        return speed;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState blockIn, BlockPos pos, EntityLivingBase entityLiving) {
        if (stack.getTagCompound() != null && stack.getTagCompound().getBoolean("3x3")) {
            EnumFacing enumfacing = entityLiving.getHorizontalFacing().getOpposite();
            if (entityLiving.rotationPitch < -50) {
                enumfacing = EnumFacing.DOWN;
            } else if (entityLiving.rotationPitch > 50) {
                enumfacing = EnumFacing.UP;
            }
            if (enumfacing == EnumFacing.SOUTH || enumfacing == EnumFacing.NORTH) {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        breakBlock(pos.add(i, j, 0), stack, worldIn, entityLiving, pos);
                    }
                }
            } else if (enumfacing == EnumFacing.EAST || enumfacing == EnumFacing.WEST) {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        breakBlock(pos.add(0, j, i), stack, worldIn, entityLiving, pos);
                    }
                }
            } else if (enumfacing == EnumFacing.DOWN || enumfacing == EnumFacing.UP) {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        breakBlock(pos.add(j, 0, i), stack, worldIn, entityLiving, pos);
                    }
                }
            }
        }
        return super.onBlockDestroyed(stack, worldIn, blockIn, pos, entityLiving);
    }

    public void breakBlock(BlockPos pos, ItemStack stack, World world, EntityLivingBase entityLiving, BlockPos oldPos) {
        if (oldPos == pos) {
            return;
        }
        if (!ElectricItem.manager.canUse(stack, cost)) {
            return;
        }
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block.getBlockHardness(blockState, world, pos) == -1.0F) {
            return;
        }
        List<ItemStack> stuff = block.getDrops(world, pos, blockState, 0);
        List<ItemStack> dropList = new ArrayList<>();
        BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(world, pos, blockState, 0, 1, dropList, (EntityPlayer) entityLiving, false);
        MinecraftForge.EVENT_BUS.post(event);
        for (ItemStack drop : dropList) {
            if (!drop.isEmpty() && drop.getCount() > 0) {
                stuff.add(drop);
            }
        }
        for (ItemStack drop : stuff) {
            if (world.isRemote) {
                continue;
            }
            final EntityItem entityitem = new EntityItem(world, oldPos.getX(), oldPos.getY(), oldPos.getZ(), drop);
            entityitem.motionX = (oldPos.getX() - oldPos.getX()) / 10.0f;
            entityitem.motionY = 0.15000000596046448;
            entityitem.motionZ = (oldPos.getZ() - oldPos.getZ()) / 10.0f;
            world.spawnEntity(entityitem);
        }
        ElectricItem.manager.use(stack, cost, entityLiving);
        world.setBlockToAir(pos);
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && IC2.keyboard.isModeSwitchKeyDown(player)) {
            ItemStack stack = StackUtil.get(player, hand);
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
            }
            nbt.setBoolean("3x3", !nbt.getBoolean("3x3"));
            if (nbt.getBoolean("3x3")) {
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", new Object[]{"ic2.tooltip.mode.3x3"});
            } else {
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", new Object[]{"ic2.tooltip.mode.normal"});
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xOffset, float yOffset, float zOffset) {
        if (isAdvanced) {
            return IC2.keyboard.isModeSwitchKeyDown(player) ? EnumActionResult.PASS : super.onItemUse(player, world, pos, hand, side, xOffset, yOffset, zOffset);
        }
        return super.onItemUse(player, world, pos, hand, side, xOffset, yOffset, zOffset);
    }
}
