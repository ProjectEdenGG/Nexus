package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.clearinventory.models.ClearInventoryPlayer;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.inventory.ItemStack;

@Aliases({"clean", "clear", "ci", "clearinvent", "eclean", "eclear", "eci", "eclearinvent", "eclearinventory"})
public class ClearInventoryCommand extends CustomCommand {
	private ClearInventoryPlayer ciPlayer;

	ClearInventoryCommand(CommandEvent event) {
		super(event);
		ciPlayer = BNCore.clearInventory.getPlayer(player());
	}

	@Path
	void clear() {
		ciPlayer.addCache();
		player().getInventory().setContents(new ItemStack[0]);
		send(PREFIX + "Inventory cleared. Undo with &c/clear undo");
	}

	@Path("undo")
	void undo() {
		ciPlayer.restoreCache();
	}

}
