package me.pugabyte.bncore.features.clearinventory;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.clearinventory.models.ClearInventoryPlayer;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.features.clearinventory.ClearInventory.PREFIX;

@Aliases({"clean", "clear", "ci", "clearinvent", "eclean", "eclear", "eci", "eclearinvent", "eclearinventory"})
@NoArgsConstructor
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
		reply(PREFIX + "Inventory cleared. Undo with &c/clear undo");
	}

	@Path("undo")
	void undo() {
		ciPlayer.restoreCache();
	}

}
