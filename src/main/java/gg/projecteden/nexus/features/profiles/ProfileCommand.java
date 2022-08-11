package gg.projecteden.nexus.features.profiles;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@NoArgsConstructor
public class ProfileCommand extends CustomCommand implements Listener {

	public ProfileCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	public void open(@Arg("self") Nerd target) {
		openProfile(target, player());
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player clickedPlayer))
			return;

		Player player = event.getPlayer();
		if (!player.isSneaking())
			return;

		if (Minigamer.of(player).isPlaying())
			return;

		openProfile(clickedPlayer, player);
	}

	public void openProfile(Player target, Player viewer) {
		openProfile(Nerd.of(target), viewer);
	}

	public void openProfile(Nerd target, Player viewer) {
		new ProfileMenuProvider(target.getOfflinePlayer()).open(viewer);
	}
}
