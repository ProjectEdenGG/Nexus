package gg.projecteden.nexus.features.chat.bridge;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Name;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.List;

public class RoleManager {

	public static void update(DiscordUser user) {
		if (Discord.getGuild() == null)
			return;

		DiscordUserService service = new DiscordUserService();

		String name = Name.of(user);
		if (name == null)
			return;

		if (user.isPreventRoleUpdates())
			return;

		String nickname = Nickname.of(user);
		Color roleColor = user.getRank().getDiscordColor();

		if (roleColor == null) {
			user.setRoleId(null);
			service.save(user);
			return;
		}

		debug("Updating role for " + user.getNickname());
		if (user.getRoleId() == null) {
			debug("  No role found, searching");
			List<Role> rolesByName = Discord.getGuild().getRolesByName(name, true);
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
							.setColor(roleColor)
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
		else
			debug("    No updates needed");
	}

	private static void debug(String message) {
		if (false)
			Nexus.debug(message);
	}

}
