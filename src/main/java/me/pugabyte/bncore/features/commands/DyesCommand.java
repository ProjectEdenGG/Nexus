package me.pugabyte.bncore.features.commands;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DyesCommand extends CustomCommand {

	public DyesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void dyes() {
		SmartInventory INV = SmartInventory.builder()
				.title("Dyes")
				.size(1, 9)
				.provider(new DyesProvider())
				.build();
		INV.open(player());
	}

	public class DyesProvider implements InventoryProvider {

		@Override
		public void init(Player player, InventoryContents contents) {
			contents.set(0, ClickableItem.empty(new ItemStack(Material.INK_SACK, 5, (byte) 15)));
			contents.set(1, ClickableItem.empty(new ItemStack(Material.INK_SACK, 2, (byte) 11)));
			contents.set(2, ClickableItem.empty(new ItemStack(Material.INK_SACK, 2, (byte) 7)));
			contents.set(3, ClickableItem.empty(new ItemStack(Material.INK_SACK, 5, (byte) 4)));
			contents.set(4, ClickableItem.empty(new ItemStack(Material.INK_SACK, 1, (byte) 3)));
			contents.set(5, ClickableItem.empty(new ItemStack(Material.INK_SACK, 3, (byte) 2)));
			contents.set(6, ClickableItem.empty(new ItemStack(Material.INK_SACK, 6, (byte) 1)));
			contents.set(7, ClickableItem.empty(new ItemStack(Material.INK_SACK, 2, (byte) 0)));
		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {
		}
	}

}
