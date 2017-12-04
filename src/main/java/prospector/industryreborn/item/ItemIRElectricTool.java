package prospector.industryreborn.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.init.Localization;
import ic2.core.item.BaseElectricItem;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.IPseudoDamageItem;
import ic2.core.item.ItemIC2;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import prospector.shootingstar.ShootingStar;
import prospector.shootingstar.model.ModelCompound;
import prospector.industryreborn.core.IRConstants;
import prospector.industryreborn.core.IRTab;

import java.util.*;

public class ItemIRElectricTool extends ItemTool implements IElectricItem, IItemHudInfo, IBoxable, IPseudoDamageItem {

    public final Set<ToolClass> toolClasses;
    public double cost;
    public int maxEnergy;
    public int transferLimit;
    public AudioSource audioSource;
    public boolean wasEquipped;

    public ItemIRElectricTool(String name, int cost, int transferLimit, int maxEnergy) {
        this(name, cost, transferLimit, maxEnergy, ItemElectricTool.HarvestLevel.Iron, Collections.emptySet());
    }

    public ItemIRElectricTool(String name, int cost, int transferLimit, int maxEnergy, ItemElectricTool.HarvestLevel harvestLevel, Set<ToolClass> toolClasses) {
        this(name, 2.0F, -3.0F, cost, transferLimit, maxEnergy, harvestLevel, toolClasses, new HashSet());
    }

    private ItemIRElectricTool(String name, float attackDamage, float attackSpeed, int cost, int transferLimit, int maxEnergy, ItemElectricTool.HarvestLevel harvestLevel, Set<ToolClass> toolClasses, Set<Block> mineableBlocks) {
        super(attackDamage, attackSpeed, harvestLevel.toolMaterial, mineableBlocks);
        this.cost = (double) cost;
        this.transferLimit = transferLimit;
        this.maxEnergy = maxEnergy;
        this.toolClasses = toolClasses;
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
        String invVariant = "inventory";
        String fileName = name;
        if (name.contains("#")) {
            String[] splitName = name.split("\\#", 2);
            invVariant = "type=" + splitName[0];
            fileName = splitName[1];
            name = splitName[0] + "_" + splitName[1];
        }

        this.setRegistryName(IRConstants.MOD_ID, name);
        this.setUnlocalizedName(IRConstants.MOD_ID + "." + name);
        this.setCreativeTab(IRTab.TAB);

        Iterator classIterator = toolClasses.iterator();

        while (classIterator.hasNext()) {
            ToolClass toolClass = (ToolClass) classIterator.next();
            if (toolClass.name != null) {
                this.setHarvestLevel(toolClass.name, harvestLevel.level);
            }
        }

        if (toolClasses.contains(ToolClass.Pickaxe) && harvestLevel.toolMaterial == ToolMaterial.DIAMOND) {
            mineableBlocks.add(Blocks.OBSIDIAN);
            mineableBlocks.add(Blocks.REDSTONE_ORE);
            mineableBlocks.add(Blocks.LIT_REDSTONE_ORE);
        }

        ShootingStar.registerModel(new ModelCompound(IRConstants.MOD_ID, this, "tool").setFileName(fileName).setInvVariant(invVariant));
    }


    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return ItemIC2.shouldReequip(oldStack, newStack, slotChanged);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xOffset, float yOffset, float zOffset) {
        ElectricItem.manager.use(StackUtil.get(player, hand), 0.0D, player);
        return super.onItemUse(player, world, pos, hand, side, xOffset, yOffset, zOffset);
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ElectricItem.manager.use(StackUtil.get(player, hand), 0.0D, player);
        return super.onItemRightClick(world, player, hand);
    }

    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        Material material = state.getMaterial();
        Iterator var4 = this.toolClasses.iterator();

        ToolClass toolClass;
        do {
            if (!var4.hasNext()) {
                return super.canHarvestBlock(state, stack);
            }

            toolClass = (ToolClass) var4.next();
        } while (!toolClass.whitelist.contains(state.getBlock()) && !toolClass.whitelist.contains(material));

        return true;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
            ElectricItemManager.addChargeVariants(this, subItems);
        }
    }

    public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2) {
        return false;
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }

    public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase entityliving1) {
        return true;
    }

    public boolean isRepairable() {
        return false;
    }

    public int getItemEnchantability() {
        return 0;
    }

    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        return !ElectricItem.manager.canUse(stack, this.cost) ? 1.0F : (this.canHarvestBlock(state, stack) ? this.efficiencyOnProperMaterial : super.getStrVsBlock(stack, state));
    }


    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return maxEnergy;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        if (transferLimit <= 32) {
            return 1;
        }
        if (transferLimit <= 128) {
            return 2;
        }
        if (transferLimit <= 512) {
            return 3;
        }
        if (transferLimit <= 2048) {
            return 4;
        }
        if (transferLimit <= 8192) {
            return 5;
        }
        return 6;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return transferLimit;
    }

    public List<String> getHudInfo(ItemStack stack, boolean advanced) {
        List<String> info = new LinkedList();
        info.add(ElectricItem.manager.getToolTip(stack));
        info.add(Localization.translate("ic2.item.tooltip.PowerTier", new Object[]{Integer.valueOf(getTier(stack))}));
        return info;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemStack) {
        return true;
    }

    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        boolean isEquipped = flag && entity instanceof EntityLivingBase;
        if (IC2.platform.isRendering()) {
            if (isEquipped && !this.wasEquipped) {
                String initSound;
                if (this.audioSource == null) {
                    initSound = this.getIdleSound((EntityLivingBase) entity, itemstack);
                    if (initSound != null) {
                        this.audioSource = IC2.audioManager.createSource(entity, PositionSpec.Hand, initSound, true, false, IC2.audioManager.getDefaultVolume());
                    }
                }

                if (this.audioSource != null) {
                    this.audioSource.play();
                }

                initSound = this.getStartSound((EntityLivingBase) entity, itemstack);
                if (initSound != null) {
                    IC2.audioManager.playOnce(entity, PositionSpec.Hand, initSound, true, IC2.audioManager.getDefaultVolume());
                }
            } else if (!isEquipped && this.audioSource != null) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase theEntity = (EntityLivingBase) entity;
                    ItemStack stack = theEntity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
                    if (stack == null || stack.getItem() != this || stack == itemstack) {
                        this.removeAudioSource();
                        String sound = this.getStopSound(theEntity, itemstack);
                        if (sound != null) {
                            IC2.audioManager.playOnce(entity, PositionSpec.Hand, sound, true, IC2.audioManager.getDefaultVolume());
                        }
                    }
                }
            } else if (this.audioSource != null) {
                this.audioSource.updatePosition();
            }

            this.wasEquipped = isEquipped;
        }

    }


    public String getStopSound(EntityLivingBase player, ItemStack stack) {
        return null;
    }

    public String getStartSound(EntityLivingBase player, ItemStack stack) {
        return null;
    }

    public String getIdleSound(EntityLivingBase player, ItemStack stack) {
        return null;
    }

    public void removeAudioSource() {
        if (this.audioSource != null) {
            this.audioSource.stop();
            this.audioSource.remove();
            this.audioSource = null;
        }

    }

    public void setDamage(ItemStack stack, int damage) {
        int prev = this.getDamage(stack);
        if (damage != prev && BaseElectricItem.logIncorrectItemDamaging) {
            IC2.log.warn(LogCategory.Armor, new Throwable(), "Detected invalid armor damage application (%d):", new Object[]{Integer.valueOf(damage - prev)});
        }

    }

    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase user) {
        if (state.getBlockHardness(world, pos) != 0.0F) {
            if (user != null) {
                ElectricItem.manager.use(stack, this.cost, user);
            } else {
                ElectricItem.manager.discharge(stack, this.cost, getTier(stack), true, false, false);
            }
        }

        return true;
    }

    public void setStackDamage(ItemStack stack, int damage) {
        super.setDamage(stack, damage);
    }

    public enum ToolClass {
        Axe("axe", new Object[]{Material.WOOD, Material.PLANTS, Material.VINE}),
        Pickaxe("pickaxe", new Object[]{Material.IRON, Material.ANVIL, Material.ROCK}),
        Shears("shears", new Object[]{Blocks.WEB, Blocks.WOOL, Blocks.REDSTONE_WIRE, Blocks.TRIPWIRE, Material.LEAVES}),
        Shovel("shovel", new Object[]{Blocks.SNOW_LAYER, Blocks.SNOW}),
        Sword("sword", new Object[]{Blocks.WEB, Material.PLANTS, Material.VINE, Material.CORAL, Material.LEAVES, Material.GOURD}),
        Hoe((String) null, new Object[]{Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM});

        public final String name;
        public final Set<Object> whitelist;

        private ToolClass(String name, Object[] whitelist) {
            this.name = name;
            this.whitelist = new HashSet(Arrays.asList(whitelist));
        }
    }
}
