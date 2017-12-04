package prospector.industryreborn.blocks;

import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import prospector.shootingstar.ShootingStar;
import prospector.shootingstar.model.ModelCompound;
import prospector.industryreborn.core.IRConstants;
import prospector.industryreborn.core.IRTab;
import prospector.industryreborn.tiles.TileEntityMachine;

import javax.annotation.Nullable;
import java.util.List;

public class BlockMachine extends Block implements IWrenchable {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public final TileEntityMachine machine;
    public final String name;

    public BlockMachine(String name, TileEntityMachine machine) {
        super(Material.IRON, MapColor.IRON);
        setSoundType(SoundType.METAL);
        setHardness(2F);
        setRegistryName(IRConstants.MOD_ID, name);
        setUnlocalizedName(IRConstants.MOD_ID + "." + name);
        setCreativeTab(IRTab.TAB);
        this.machine = machine;
        this.name = name;
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH).withProperty(ACTIVE, false));
        ShootingStar.registerModel(new ModelCompound(IRConstants.MOD_ID, this, "machine").setInvVariant("active=false,facing=north"));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(ACTIVE, false).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        switch (state.getValue(FACING)) {
            case EAST:
                meta = 1;
                break;
            case SOUTH:
                meta = 2;
                break;
            case WEST:
                meta = 3;
                break;
        }
        if (state.getValue(ACTIVE)) {
            meta += 4;
        }
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean active = false;
        if (meta >= 4) {
            meta -= 4;
            active = true;
        }
        EnumFacing facing = EnumFacing.Plane.HORIZONTAL.facings()[meta];
        return getDefaultState().withProperty(FACING, facing).withProperty(ACTIVE, active);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ACTIVE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return machine;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public EnumFacing getFacing(World world, BlockPos pos) {
        return world.getBlockState(pos).getValue(BlockMachine.FACING);
    }

    @Override
    public boolean setFacing(World world, BlockPos pos, EnumFacing facing, EntityPlayer player) {
        return world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockMachine.FACING, facing));
    }

    @Override
    public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity tile, EntityPlayer player, int i) {
        return world.getBlockState(pos).getBlock().getDrops(world, pos, state, 0);
    }
}
