package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGroup;
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
		if (WorldGroup.get(player()).equals(WorldGroup.MINIGAMES))
			error("Cannot use in gameworld");

		Location playerArgLoc = playerArg.getLocation().clone();

		if (!player().getWorld().equals(playerArg.getWorld()))
			error(playerArg.getName() + " is in " + StringUtils.camelCase(playerArg.getWorld().getName()));

		double distance = player().getLocation().distance(playerArgLoc);
		if (distance > Chat.getLocalRadius())
			error(StringUtils.camelCase(playerArg.getName() + " not found"));

		LocationUtils.lookAt(player(), playerArgLoc);

		Tasks.GlowTask.builder()
				.duration(10 * 20)
				.entity(playerArg)
				.color(GlowAPI.Color.RED)
				.viewers(Collections.singletonList(player()))
				.start();
	}
}
