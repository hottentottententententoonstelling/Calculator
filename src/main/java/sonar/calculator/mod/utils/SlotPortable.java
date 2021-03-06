package sonar.calculator.mod.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotPortable extends Slot {
	public boolean isRemote;
	public IInventory invItem;
	private Item type;

	public SlotPortable(IInventory inv, int index, int x, int y, boolean isRemote, Item type) {
		super(inv, index, x, y);
		this.isRemote = isRemote;
		this.invItem = inv;
		this.type = type;
	}

	public boolean isItemValid(ItemStack stack) {
		if (type == null || stack == null) {
			return super.isItemValid(stack);
		}
		return stack.getItem() != type;
	}

	public void putStack(ItemStack stack) {
		invItem.setInventorySlotContents(getSlotIndex(), stack);
	}

	public void onSlotChanged() {
		if (!this.isRemote)
			this.inventory.markDirty();
	}
}