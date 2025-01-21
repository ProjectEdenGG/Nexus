package gg.projecteden.nexus.features.profiles;

import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.profiles.providers.ProfileProvider;
import gg.projecteden.nexus.features.profiles.providers.ProfileSettingsProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.profile.ProfileUser;
import gg.projecteden.nexus.models.profile.ProfileUserService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
public class ProfileCommand extends CustomCommand implements Listener {
	private final ProfileUserService service = new ProfileUserService();

	public ProfileCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("View a player's profile")
	public void open(@Arg("self") Nerd target) {
		openProfile(target, player(), null);
	}

	@HideFromWiki
	@HideFromHelp
	@Description("Set your profile's about me")
	@Path("setAbout <text...>")
	public void setAbout(String input) {
		if (Nullables.isNullOrEmpty(input))
			error("Missing input");

		input = StringUtils.stripColor(input.trim());
		if (Censor.isCensored(player(), input))
			error("Inappropriate input in about");

		ProfileUser user = service.get(player());
		if (!input.equals(user.getNerd().getAbout())) {
			user.getNerd().setAbout(input.trim());
		}

		new ProfileSettingsProvider(player(), null, user).open(player());
	}

	@HideFromWiki
	@HideFromHelp
	@Description("Set your profile's status")
	@Path("setStatus <text...>")
	public void setStatus(String input) {
		if (Nullables.isNullOrEmpty(input))
			error("Missing input");

		input = StringUtils.stripColor(input.trim());
		if (Censor.isCensored(player(), input))
			error("Inappropriate input in status");

		ProfileUser user = service.get(player());
		if (!input.equals(user.getStatus())) {
			user.setStatus(input);
			Discord.staffLog(StringUtils.getDiscordPrefix("Status") + user.getNerd().getNickname() + " set their profile status to `" + user.getStatus() + "`");
			service.save(user);
		}

		new ProfileSettingsProvider(player(), null, user).open(player());
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
