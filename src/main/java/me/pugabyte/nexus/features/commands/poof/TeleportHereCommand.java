package me.pugabyte.nexus.features.commands.poof;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.models.nickname.Nickname;
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
	void run(Nerd nerd) {
		if (nerd.isOnline())
			if (!player().hasPermission("essentials.tphere"))
				runCommand("tpahere " + argsString());
			else
				nerd.getPlayer().teleportAsync(location(), TeleportCause.COMMAND);
		else {
			if (!player().hasPermission("essentials.tphere"))
				throw new PlayerNotOnlineException(nerd.getOfflinePlayer());

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
