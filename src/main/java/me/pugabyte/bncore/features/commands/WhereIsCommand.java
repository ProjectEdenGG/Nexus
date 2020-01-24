package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;

public class WhereIsCommand extends CustomCommand {

	public WhereIsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void whereIs(@Arg Player playerArg) {
		Location playerArgLoc = playerArg.getLocation().clone();

		double distance = player().getLocation().distance(playerArgLoc);
		if (distance > Chat.getLocalRadius()) {
			error(Utils.camelCase(playerArg.getName() + " not found"));
			return;
		}

		GlowAPI.setGlowing(playerArg, GlowAPI.Color.RED, player());
		Utils.lookAt(player(), playerArgLoc);

		Utils.wait(10 * 20, () -> GlowAPI.setGlowing(playerArg, false, player()));
	}
}
