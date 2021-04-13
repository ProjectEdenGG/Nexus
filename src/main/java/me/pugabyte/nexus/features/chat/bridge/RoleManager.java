package me.pugabyte.nexus.features.chat.bridge;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RoleManager {
	public static final List<UUID> ignore = Arrays.asList(
			Dev.GRIFFIN.getUuid(),
			Dev.WAKKA.getUuid(),
			Dev.FILID.getUuid(),
			Dev.BLAST.getUuid(),
			Dev.KODA.getUuid(),
			Dev.LEXI.getUuid()
	);

	public static void update(DiscordUser user) {
		if (Discord.getGuild() == null)
			return;

		DiscordUserService service = new DiscordUserService();
		OfflinePlayer player = user.getOfflinePlayer();

		if (player == null || player.getName() == null)
			return;

		if (ignore.contains(player.getUniqueId()))
			return;

		String nickname = Nickname.of(player);
		Color roleColor = Nerd.of(player).getRank().getDiscordColor();

		if (roleColor == null) {
			user.setRoleId(null);
			service.save(user);
			return;
		}

		debug("Updating role for " + user.getNickname());
		if (user.getRoleId() == null) {
			debug("  No role found, searching");
			List<Role> rolesByName = Discord.getGuild().getRolesByName(player.getName(), true);
			if (rolesByName.size() > 0) {
				debug("    Found matching username role");
				user.setRoleId(rolesByName.get(0).getId());
				service.save(user);
			} else {
				List<Role> rolesByNickname = Discord.getGuild().getRolesByName(nickname, true);
				if (rolesByNickname.size() > 0) {
					debug("    Found matching nickname role");
					user.setRoleId(rolesByNickname.get(0).getId());
					service.save(user);
				} else {
					debug("    No matching role found, creating a new one");
					Discord.getGuild().createRole()
							.setName(nickname)
							.setColor(Nerd.of(player).getRank().getDiscordColor())
							.queue();
					return;
				}
			}
		}

		Role role = Discord.getGuild().getRoleById(user.getRoleId());
		if (role == null) {
			debug("  Unable to retrieve role, deleted?");
			return;
		}

		debug("  Role found, checking for updates");
		boolean update = false;
		net.dv8tion.jda.api.managers.RoleManager manager = role.getManager();
		if (!roleColor.equals(role.getColor())) {
			debug("    Updating color to " + roleColor);
			update = true;
			manager.setColor(roleColor);
		}
		if (!role.getName().equals(nickname)) {
			debug("    Updating nickname to " + nickname);
			update = true;
			manager.setName(nickname);
		}

		if (update)
			manager.queue(success -> Nexus.debug("      Updated role"), error -> { throw new RuntimeException(error); });
		else {
			debug("    No updates needed");
		}
	}

	private static void debug(String message) {
		if (false)
			Nexus.debug(message);
	}

}
