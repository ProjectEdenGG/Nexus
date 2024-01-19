package gg.projecteden.nexus.features.profiles;

import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.profiles.providers.ProfileProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.CitizensUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
public class ProfileCommand extends CustomCommand implements Listener {

	public ProfileCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("View a player's profile")
	public void open(@Arg("self") Nerd target) {
		openProfile(target, player(), null);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (CitizensUtils.isNPC(event.getRightClicked()))
			return;

		if (!(event.getRightClicked() instanceof Player clickedPlayer))
			return;

		Player player = event.getPlayer();
		if (!player.isSneaking())
			return;

		if (Minigamer.of(player).isPlaying())
			return;

		openProfile(clickedPlayer, player, null);
	}

	public static void openProfile(Player target, Player viewer, @Nullable InventoryProvider previousMenu) {
		openProfile(Nerd.of(target), viewer, previousMenu);
	}

	public static void openProfile(Nerd target, Player viewer, @Nullable InventoryProvider previousMenu) {
		new ProfileProvider(target.getOfflinePlayer(), previousMenu).open(viewer);
	}
}
