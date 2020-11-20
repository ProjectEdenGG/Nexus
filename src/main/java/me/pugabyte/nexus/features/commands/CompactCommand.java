package me.pugabyte.nexus.features.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Aliases("condense")
@Permission("group.staff")
public class CompactCommand extends CustomCommand {

	public CompactCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void compact() {
		if (player().getInventory().firstEmpty() == -1)
			error("There is no empty space in your inventory");
		compactAll();
		combineItems();
		compactAll();
		send(PREFIX + "Compacted all items that could be compacted");
	}

	public void compactAll() {
		for (ItemStack item : player().getInventory().getContents()) {
			compact(item);
		}
	}

	public void combineItems() {
		Loop:
		for (int slot = 0; slot < player().getInventory().getContents().length; slot++) {
			if (ItemUtils.isNullOrAir(player().getInventory().getContents()[slot])) continue;
			if (player().getInventory().getContents()[slot].getMaxStackSize() == 1) continue;
			ItemStack item = player().getInventory().getContents()[slot].clone();
			int amount = item.getAmount();
			Matcher:
			for (int i = 0; i < player().getInventory().getContents().length; i++) {
				if (player().getInventory().getContents()[i] == null) continue Matcher;
				if (i == slot) continue Matcher;
				item.setAmount(1);
				ItemStack temp = player().getInventory().getContents()[i].clone();
				temp.setAmount(1);
				if (temp.equals(item)) {
					amount += player().getInventory().getContents()[i].getAmount();
					if (item.getMaxStackSize() < amount || amount > 64)
						continue Loop;
					player().getInventory().setItem(i, null);
				}
			}
			item.setAmount(amount);
			player().getInventory().setItem(slot, item);
		}
	}

	@Path("hand")
	void hand() {
		if (ItemUtils.isNullOrAir(player().getInventory().getItemInMainHand()))
			error("You cannot be holding air while running this command");
		compact(player().getInventory().getItemInMainHand());
	}

	public void compact(ItemStack item) {
		if (item == null) return;
		for (Compactable compactable : Compactable.values()) {
			if (!item.getType().name().equals(compactable.name())) continue;
			if (item.getAmount() < compactable.getRequiredAmount()) return;
			int leftOver = item.getAmount() % compactable.getRequiredAmount();
			int crafted = item.getAmount() / compactable.getRequiredAmount();
			if (leftOver != 0 && player().getInventory().firstEmpty() == -1)
				error("There is not enough space in your inventory to compact items");
			item.setAmount(leftOver);
			ItemStack craftedItem = new ItemStack(compactable.getResult(), crafted);
			ItemStack leftOverItem = item.clone();
			leftOverItem.setAmount(leftOver);
			ItemUtils.giveItem(player(), craftedItem);
		}
	}

	@AllArgsConstructor
	@Getter
	public enum Compactable {
		COAL(Material.COAL_BLOCK, 9),
		IRON_NUGGET(Material.IRON_INGOT, 9),
		IRON_INGOT(Material.IRON_BLOCK, 9),
		GOLD_NUGGET(Material.GOLD_INGOT, 9),
		GOLD_INGOT(Material.GOLD_BLOCK, 9),
		REDSTONE(Material.REDSTONE_BLOCK, 9),
		LAPIS_LAZULI(Material.LAPIS_BLOCK, 9),
		DIAMOND(Material.DIAMOND_BLOCK, 9),
		EMERALD(Material.EMERALD_BLOCK, 9),
		QUARTZ(Material.QUARTZ_BLOCK, 4),
		//NETHERITE(Material.NETHERITE_BLOCK, 9);
		SNOWBALL(Material.SNOW_BLOCK, 4),
		ICE(Material.PACKED_ICE, 9),
		PACKED_ICE(Material.BLUE_ICE, 9),
		SLIME_BALL(Material.SLIME_BLOCK, 9);

		Material result;
		int requiredAmount;
	}


}
