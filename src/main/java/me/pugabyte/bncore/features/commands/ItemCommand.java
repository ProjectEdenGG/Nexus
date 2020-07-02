package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;

@Aliases("i")
@Permission("essentials.item")
public class ItemCommand extends CustomCommand {

	public ItemCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<type> [amount] [nbt...]")
	void run(Material material, @Arg("64") int amount, String nbt) {
		Utils.giveItem(player(), material, amount, nbt);
	}

}
