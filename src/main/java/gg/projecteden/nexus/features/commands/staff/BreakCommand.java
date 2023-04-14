package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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

	@Override
	@NoLiterals
	@Description("Break the block you are looking at")
	public void help() {
		if (isStaff()) {
			Block block = getTargetBlockRequired();

			final BlockBreakEvent event = new BlockBreakEvent(block, player());
			if (!event.callEvent())
				error("Cannot break that block");

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
