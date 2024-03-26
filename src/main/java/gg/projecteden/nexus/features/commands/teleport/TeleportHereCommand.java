package gg.projecteden.nexus.features.commands.teleport;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@NoArgsConstructor
@Aliases({"tphere", "s"})
public class TeleportHereCommand extends CustomCommand implements Listener {

	public TeleportHereCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Description("Summon a player to your location")
	@Cooldown(value = TickTime.SECOND, x = 5, bypass = Group.ADMIN)
	void run(Nerd nerd) {
		if (nerd.isOnline())
			if (!player().hasPermission("essentials.tphere"))
				runCommand("tpahere " + argsString());
			else {

				nerd.getOnlinePlayer().teleportAsync(location(), TeleportCause.COMMAND);
			}
		else {
			if (!player().hasPermission("essentials.tphere"))
				throw new PlayerNotOnlineException(nerd);

			nerd.setTeleportOnLogin(location());
			new NerdService().save(nerd);
			send(PREFIX + Nickname.of(nerd) + " will be teleported to this location when they log in");
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Nerd nerd = Nerd.of(event.getPlayer());
		if (nerd.getTeleportOnLogin() == null)
			return;

		// No /back
		event.getPlayer().teleportAsync(nerd.getTeleportOnLogin());
		nerd.setTeleportOnLogin(null);
		new NerdService().save(nerd);
	}

}
