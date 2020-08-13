package me.pugabyte.bncore.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;

@Aliases("fw127")
@Permission("group.admin")
public class Firework127Command extends CustomCommand {

	public Firework127Command(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		Utils.giveItem(player(), new ItemBuilder(Material.FIREWORK_ROCKET).fireworkPower(127).build());
	}

}
