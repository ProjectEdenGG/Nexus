package me.pugabyte.nexus.features.autosort.tasks;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.utils.ItemUtils.ItemStackComparator;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@AllArgsConstructor
public class InventorySorter implements Runnable {
	private final Inventory inventory;
	private final int startIndex;

	@Override
	public void run() {
		ArrayList<ItemStack> stacks = new ArrayList<>();
		ItemStack[] contents = this.inventory.getContents();
		int inventorySize = contents.length;
		if (this.inventory.getType() == InventoryType.PLAYER) inventorySize = Math.min(contents.length, 36);
		for (int i = this.startIndex; i < inventorySize; i++) {
			ItemStack stack = contents[i];
			if (stack != null) {
				stacks.add(stack);
			}
		}

		stacks.sort(new ItemStackComparator());
		for (int i = 1; i < stacks.size(); i++) {
			ItemStack prevStack = stacks.get(i - 1);
			ItemStack thisStack = stacks.get(i);
			if (prevStack.isSimilar(thisStack)) {
				if (prevStack.getAmount() < prevStack.getMaxStackSize()) {
					int moveCount = Math.min(prevStack.getMaxStackSize() - prevStack.getAmount(), thisStack.getAmount());
					prevStack.setAmount(prevStack.getAmount() + moveCount);
					thisStack.setAmount(thisStack.getAmount() - moveCount);
					if (thisStack.getAmount() == 0) {
						stacks.remove(i);
						i--;
					}
				}
			}
		}

		int i;
		for (i = 0; i < stacks.size(); i++)
			this.inventory.setItem(i + this.startIndex, stacks.get(i));

		for (i = i + this.startIndex; i < inventorySize; i++)
			this.inventory.clear(i);
	}

}
