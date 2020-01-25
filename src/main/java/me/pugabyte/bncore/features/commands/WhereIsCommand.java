package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;

import java.util.Collections;

@Permission("group.staff")
public class WhereIsCommand extends CustomCommand {

	public WhereIsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void whereIs(Player playerArg) {
		Location playerArgLoc = playerArg.getLocation().clone();

		if (!player().getWorld().equals(playerArg.getWorld()))
			error(playerArg.getName() + " is in " + Utils.camelCase(playerArg.getWorld().getName()));

		double distance = player().getLocation().distance(playerArgLoc);
		if (distance > Chat.getLocalRadius())
			error(Utils.camelCase(playerArg.getName() + " not found"));

		Utils.lookAt(player(), playerArgLoc);

		Tasks.GlowTask.builder()
				.duration(10 * 20)
				.entity(playerArg)
				.color(GlowAPI.Color.RED)
				.viewers(Collections.singletonList(player()))
				.start();
	}
}
