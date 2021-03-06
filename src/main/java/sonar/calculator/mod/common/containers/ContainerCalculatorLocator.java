package sonar.calculator.mod.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.tileentity.generators.TileEntityCalculatorLocator;
import sonar.calculator.mod.utils.SlotLocatorModule;
import sonar.core.api.SonarAPI;
import sonar.core.inventory.ContainerSync;

public class ContainerCalculatorLocator extends ContainerSync {
	private TileEntityCalculatorLocator entity;

	public ContainerCalculatorLocator(InventoryPlayer inventory, TileEntityCalculatorLocator entity) {
		super(entity);
		this.entity = entity;

		addSlotToContainer(new Slot(entity, 0, 28, 60));
		addSlotToContainer(new SlotLocatorModule(entity, 1, 132, 60));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int id) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(id);

		if ((slot != null) && (slot.getHasStack())) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if ((id != 1) && (id != 0)) {
				if (SonarAPI.getEnergyHelper().canTransferEnergy(itemstack1)!=null) {
					if (!mergeItemStack(itemstack1, 0, 1, false)) {
						return null;
					}
				}else if (itemstack1.getItem() == Calculator.itemLocatorModule) {
					if (!mergeItemStack(itemstack1, 1, 2, false)) {
						return null;
					}
				} else if ((id >= 3) && (id < 30)) {
					if (!mergeItemStack(itemstack1, 29, 38, false)) {
						return null;
					}
				} else if ((id >= 29) && (id < 38) && (!mergeItemStack(itemstack1, 2, 29, false))) {
					return null;
				}
			} else if (!mergeItemStack(itemstack1, 2, 38, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return entity.isUseableByPlayer(player);
	}
}
