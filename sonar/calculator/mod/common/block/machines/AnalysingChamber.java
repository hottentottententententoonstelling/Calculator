package sonar.calculator.mod.common.block.machines;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.api.IWrench;
import sonar.calculator.mod.common.item.misc.UpgradeCircuit;
import sonar.calculator.mod.common.tileentity.machines.TileEntityAnalysingChamber;
import sonar.calculator.mod.network.CalculatorGui;
import sonar.calculator.mod.utils.helpers.CalculatorHelper;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.utils.IDropTile;
import sonar.core.utils.SonarMaterials;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AnalysingChamber extends SonarMachineBlock implements IWrench {
	@SideOnly(Side.CLIENT)
	private IIcon front;
	@SideOnly(Side.CLIENT)
	private IIcon front2;
	@SideOnly(Side.CLIENT)
	private IIcon slot1;
	@SideOnly(Side.CLIENT)
	private IIcon slot2;
	private static boolean keepInventory;
	private Random rand = new Random();

	public AnalysingChamber() {
		super(SonarMaterials.machine);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.front = iconRegister
				.registerIcon("Calculator:analysis_front_slot1");

		this.front2 = iconRegister
				.registerIcon("Calculator:analysis_front_slot2");

		this.slot1 = iconRegister
				.registerIcon("Calculator:analysis_side_slot1");

		this.slot2 = iconRegister
				.registerIcon("Calculator:analysis_side_slot2");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
		TileEntity entity = access.getTileEntity(x, y, z);
		if (entity instanceof TileEntityAnalysingChamber) {
			TileEntityAnalysingChamber t = (TileEntityAnalysingChamber) access
					.getTileEntity(x, y, z);
			int metadata = access.getBlockMetadata(x, y, z);
			if (side != metadata) {
				return t.getBlockTexture(side, metadata) ? this.slot1
						: this.slot2;
			}
			if (side == metadata) {
				return t.getBlockTexture(side, metadata) ? this.front
						: this.front2;
			}
		}

		return this.slot1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		return side == metadata ? this.slot2 : side == 0 ? this.slot1
				: side == 1 ? this.slot1
						: (metadata == 0) && (side == 3) ? this.front
								: this.slot1;
	}

	@Override
	public boolean operateBlock(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player != null) {
			if (player.getHeldItem() != null
					&& player.getHeldItem().getItem() instanceof UpgradeCircuit) {
				return false;
			} else if (player.getHeldItem() != null
					&& player.getHeldItem().getItem() == Calculator.wrench) {
				return false;
			} else {
				if (!world.isRemote) {
					player.openGui(Calculator.instance,
							CalculatorGui.AnalysingChamber, world, x, y, z);

				}
				return true;
			}
		}

		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityAnalysingChamber();
	}

	@Override
	public boolean dropStandard(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public void addSpecialToolTip(ItemStack stack, EntityPlayer player,
			List list) {
		CalculatorHelper.addEnergytoToolTip(stack, player, list);

	}

	@Override
	public void standardInfo(ItemStack stack, EntityPlayer player, List list) {
		
	}

}