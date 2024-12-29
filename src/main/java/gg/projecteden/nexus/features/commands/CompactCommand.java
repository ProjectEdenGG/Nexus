package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Aliases("condense")
@Permission(Group.STAFF)
public class CompactCommand extends CustomCommand {

	public CompactCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Combine all stackable items")
	void compact() {
		if (inventory().firstEmpty() == -1)
			error("There is no empty space in your inventory");
		compactAll();
		combineItems();
		compactAll();
		send(PREFIX + "Compacted all items that could be compacted");
	}

	public void compactAll() {
		for (ItemStack item : inventory().getContents()) {
			compact(item);
		}
	}

	public void combineItems() {
		Loop:
		for (int slot = 0; slot < inventory().getContents().length; slot++) {
			if (Nullables.isNullOrAir(inventory().getContents()[slot])) continue;
			if (inventory().getContents()[slot].getMaxStackSize() == 1) continue;
			ItemStack item = inventory().getContents()[slot].clone();
			int amount = item.getAmount();
			Matcher:
			for (int i = 0; i < inventory().getContents().length; i++) {
				if (inventory().getContents()[i] == null) continue Matcher;
				if (i == slot) continue Matcher;
				item.setAmount(1);
				ItemStack temp = inventory().getContents()[i].clone();
				temp.setAmount(1);
				if (temp.equals(item)) {
					amount += inventory().getContents()[i].getAmount();
					if (item.getMaxStackSize() < amount || amount > 64)
						continue Loop;
					inventory().setItem(i, null);
				}
			}
			item.setAmount(amount);
			inventory().setItem(slot, item);
		}
	}

	@Path("hand")
	@Description("Combine the item in your hand into an existing stack")
	void hand() {
		if (Nullables.isNullOrAir(inventory().getItemInMainHand()))
			error("You cannot be holding air while running this command");
		compact(inventory().getItemInMainHand());
	}

	public void compact(ItemStack item) {
		if (item == null) return;
		for (Compactable compactable : Compactable.values()) {
			if (!item.getType().name().equals(compactable.name())) continue;
			if (item.getAmount() < compactable.getRequiredAmount()) return;
			int leftOver = item.getAmount() % compactable.getRequiredAmount();
			int crafted = item.getAmount() / compactable.getRequiredAmount();
			if (leftOver != 0 && inventory().firstEmpty() == -1)
				error("There is not enough space in your inventory to compact items");
			item.setAmount(leftOver);
			ItemStack craftedItem = new ItemStack(compactable.getResult(), crafted);
			ItemStack leftOverItem = item.clone();
			leftOverItem.setAmount(leftOver);
			PlayerUtils.giveItem(player(), craftedItem);
		}
	}

	@Getter
	@AllArgsConstructor
	public enum Compactable {
		COAL(Material.COAL_BLOCK, 9),
		RAW_IRON(Material.RAW_IRON_BLOCK, 9),
		IRON_NUGGET(Material.IRON_INGOT, 9),
		IRON_INGOT(Material.IRON_BLOCK, 9),
		RAW_GOLD(Material.RAW_GOLD_BLOCK, 9),
		GOLD_NUGGET(Material.GOLD_INGOT, 9),
		GOLD_INGOT(Material.GOLD_BLOCK, 9),
		RAW_COPPER(Material.RAW_COPPER_BLOCK, 9),
		COPPER_INGOT(Material.COPPER_BLOCK, 9),
		REDSTONE(Material.REDSTONE_BLOCK, 9),
		LAPIS_LAZULI(Material.LAPIS_BLOCK, 9),
		DIAMOND(Material.DIAMOND_BLOCK, 9),
		EMERALD(Material.EMERALD_BLOCK, 9),
		QUARTZ(Material.QUARTZ_BLOCK, 4),
		NETHERITE(Material.NETHERITE_BLOCK, 9),
		SNOWBALL(Material.SNOW_BLOCK, 4),
		ICE(Material.PACKED_ICE, 9),
		PACKED_ICE(Material.BLUE_ICE, 9),
		SLIME_BALL(Material.SLIME_BLOCK, 9);

		private final Material result;
		private final int requiredAmount;
	}

}
