package me.pugabyte.bncore.features.commands.staff.freeze;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.Chat.StaticChannel;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.freeze.Freeze;
import me.pugabyte.bncore.models.freeze.FreezeService;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.List;

@Permission("group.staff")
public class UnfreezeCommand extends CustomCommand {
	private final FreezeService service = new FreezeService();

	public UnfreezeCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Freeze");
	}

	@Path("<players...>")
	void unfreeze(@Arg(type = Player.class) List<Player> players) {
		for (Player player : players) {
			try {
				Freeze freeze = new FreezeService().get(player);
				if (!freeze.isFrozen()) {
					send(PREFIX + player.getName() + " is not frozen");
					continue;
				}

				freeze.setFrozen(false);
				service.save(freeze);

				if (player.getVehicle() != null && player.getVehicle() instanceof ArmorStand)
					player.getVehicle().remove();

				send(player, "&cYou have been unfrozen.");
				Chat.broadcastIngame(PREFIX + "&e" + player().getName() + " &3has unfrozen &e" + player.getName(), StaticChannel.STAFF);
				Chat.broadcastDiscord("**[Freeze]** " + player().getName() + " has unfrozen " + player.getName(), StaticChannel.STAFF);
			} catch (Exception ex) {
				event.handleException(ex);
			}
		}
	}
}
