package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Nullables;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;

@Redirect(from = "/fuck", to = "/break")
public class BreakCommand extends CustomCommand {

	public BreakCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Break the block you are looking at")
	void fuck() {
		if (isStaff()) {
			Block block = getTargetBlockRequired();

			final BlockBreakEvent event = new BlockBreakEvent(block, player());
			if (!event.callEvent())
				error("Cannot break that block");

			if (!CheatsCommand.canEnableCheats(player()))
				error("You cannot use cheats in this world");

			if (block.getState() instanceof BlockInventoryHolder inventoryHolder) {
				ItemStack[] contents = inventoryHolder.getInventory().getContents();
				if (contents.length > 1) {
					for (ItemStack content : contents) {
						if (!Nullables.isNullOrAir(content))
							block.getWorld().dropItemNaturally(block.getLocation(), content);
					}
				}
			}

			block.setType(Material.AIR);

			block.breakNaturally();
		} else if ("fuck".equalsIgnoreCase(getAliasUsed()))
			send("&4rude.");
		else
			permissionError();
	}

}
