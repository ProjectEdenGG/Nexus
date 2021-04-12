package me.pugabyte.nexus.features.chat.bridge;

import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RoleManager {
	public static final List<UUID> ignore = Arrays.asList(
			Dev.PUGA.getUuid(),
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
		OfflinePlayer player = PlayerUtils.getPlayer(user.getUuid());

		if (ignore.contains(player.getUniqueId()))
			return;

		String username = Nickname.of(player);
		Color roleColor = Nerd.of(player).getRank().getDiscordColor();

		if (roleColor == null) {
			user.setRoleId(null);
			service.save(user);
			return;
		}

		Role role = null;
		if (user.getRoleId() != null)
			role = Discord.getGuild().getRoleById(user.getRoleId());

		if (user.getRoleId() == null || role == null) {
			List<Role> rolesByName = Discord.getGuild().getRolesByName(username, true);
			if (rolesByName.size() > 0) {
				user.setRoleId(rolesByName.get(0).getId());
				service.save(user);
			} else
				Discord.getGuild().createRole()
						.setName(username)
						.setColor(Nerd.of(player).getRank().getDiscordColor())
						.queue();
		} else {
			if (role.getColor() != roleColor)
				role.getManager().setColor(roleColor).queue();
			if (!role.getName().equals(username))
				role.getManager().setName(username).queue();
		}
	}

}
