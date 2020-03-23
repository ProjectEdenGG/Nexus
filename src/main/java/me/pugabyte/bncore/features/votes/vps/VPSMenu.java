package me.pugabyte.bncore.features.votes.vps;

import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class VPSMenu {
	private int rows = 6;
	private Map<Integer, VPSItem> items = new HashMap<>();

	public void addItem(int slot, VPSItem item) {
		items.put(slot, item);
	}

	@Data
	@Builder
	public static class VPSItem {
		private String name;
		private ItemStack display;

		private int price;
		private boolean takePoints;
		private boolean close;

		private int money;
		private List<ItemStack> items;
		private String command;
	}

}
