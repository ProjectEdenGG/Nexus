package gg.projecteden.nexus.features.commands.teleport;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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

	@NoLiterals
	@Description("Summon a player to your location")
	void run(Nerd player) {
		if (player.isOnline())
			if (!player().hasPermission("essentials.tphere"))
				runCommand("tpahere " + argsString());
			else
				player.getOnlinePlayer().teleportAsync(location(), TeleportCause.COMMAND);
		else {
			if (!player().hasPermission("essentials.tphere"))
				throw new PlayerNotOnlineException(player);

			player.setTeleportOnLogin(location());
			new NerdService().save(player);
			send(PREFIX + Nickname.of(player) + " will be teleported to this location when they log in");
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
