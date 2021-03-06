package sonar.calculator.mod.common.block.misc;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.tileentity.misc.TileEntityGasLantern;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.utils.IGuiTile;

public class GasLantern extends SonarMachineBlock {

	private static boolean keepInventory;
	public final boolean isActive;
	public static final PropertyDirection DIR = PropertyDirection.create("dir");

	public GasLantern(boolean active) {
		super(SonarMaterials.machine, false, true);
		this.isActive = active;
		this.setDefaultState(this.blockState.getBaseState().withProperty(DIR, EnumFacing.NORTH));
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact) {
		if (!world.isRemote && player != null) {
			player.openGui(Calculator.instance, IGuiTile.ID, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
		if (state.getBlock() == Calculator.gas_lantern_on) {
			float x1 = pos.getX() + random.nextFloat();
			float y1 = pos.getY() + 0.5F;
			float z1 = pos.getZ() + random.nextFloat();
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x1, y1, z1, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x1, y1, z1, 0.0D, 0.0D, 0.0D);
		}
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!keepInventory) {
			super.breakBlock(world, pos, state);
		}
		world.removeTileEntity(pos);
	}

	/* @Override public void onBlockAdded(World world, BlockPos pos, IBlockState state) { super.onBlockAdded(world, pos, state); setDefaultFacing(world, pos, state); }
	 * 
	 * public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbour) { super.onNeighborChange(world, pos, neighbour); try { //setDefaultFacing((World) world, pos, world.getBlockState(pos)); } catch (ClassCastException exception) { Calculator.logger.error("Lantern: Tried to cast IBlockAccess to World"); } } */
	private EnumFacing getDefaultFacing(IBlockAccess world, BlockPos pos, IBlockState state) {
		if (world != null) {
			Iterator<EnumFacing> iterator = EnumFacing.Plane.VERTICAL.iterator();
			boolean vertical = false;
			do {
				if (!iterator.hasNext()) {
					if (!vertical) {
						vertical = true;
						iterator = EnumFacing.Plane.HORIZONTAL.iterator();
					} else {
						return EnumFacing.DOWN;
					}
				}
				EnumFacing facing = iterator.next();
				IBlockState stateOff = world.getBlockState(pos.offset(facing));
				Block block = stateOff.getBlock();
				if (block.isFullBlock(stateOff)) {
					return facing;
				}
			} while (iterator.hasNext() || !vertical);
		}
		return EnumFacing.DOWN;
	}

	public static void setState(boolean active, World worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		keepInventory = true;
		if (active) {
			worldIn.setBlockState(pos, Calculator.gas_lantern_on.getDefaultState().withProperty(DIR, iblockstate.getValue(DIR)), 3);
		} else {
			worldIn.setBlockState(pos, Calculator.gas_lantern_off.getDefaultState().withProperty(DIR, iblockstate.getValue(DIR)), 3);
		}
		keepInventory = false;

		if (tileentity != null) {
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}

	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (customBB != null) {
			return customBB;
		}
		
		EnumFacing dir = getDefaultFacing(world, pos, state);	
		return new AxisAlignedBB(0.3F + (dir.getFrontOffsetX() * 0.32F), 0.0F + getY(dir), 0.3F + (dir.getFrontOffsetZ() * 0.32F), 0.7F + (dir.getFrontOffsetX() * 0.32F), 0.7F + getY(dir), 0.7F + (dir.getFrontOffsetZ() * 0.32F));
	}

	public float getY(EnumFacing meta) {
		if (meta == EnumFacing.DOWN) {
			return 0.0F;
		} else {
			return 0.1F;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {

		return new TileEntityGasLantern();
	}

	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Calculator.gas_lantern_off);
	}

	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state) {
		return this.getDefaultState().withProperty(DIR, EnumFacing.SOUTH);
	}

	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		return this.getDefaultState().withProperty(DIR, enumfacing);
	}

	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(DIR)).getIndex();
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.withProperty(DIR, getDefaultFacing(world, pos, state));
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { DIR });
	}
}
