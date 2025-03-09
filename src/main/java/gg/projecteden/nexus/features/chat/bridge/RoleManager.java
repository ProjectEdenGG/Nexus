package gg.projecteden.nexus.features.chat.bridge;

import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Name;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.List;

import static gg.projecteden.nexus.utils.Debug.DebugType.ROLE_MANAGER;

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

		Debug.log(ROLE_MANAGER, "Updating role for " + user.getNickname());
		if (user.getRoleId() == null) {
			Debug.log(ROLE_MANAGER, "  No role found, searching");
			List<Role> rolesByName = Discord.getGuild().getRolesByName(name, true);
			if (rolesByName.size() > 0) {
				Debug.log(ROLE_MANAGER, "    Found matching username role");
				user.setRoleId(rolesByName.get(0).getId());
				service.save(user);
			} else {
				List<Role> rolesByNickname = Discord.getGuild().getRolesByName(nickname, true);
				if (rolesByNickname.size() > 0) {
					Debug.log(ROLE_MANAGER, "    Found matching nickname role");
					user.setRoleId(rolesByNickname.get(0).getId());
					service.save(user);
				} else {
					Debug.log(ROLE_MANAGER, "    No matching role found, creating a new one");
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
			Debug.log(ROLE_MANAGER, "  Unable to retrieve role, deleted?");
			return;
		}

		Debug.log(ROLE_MANAGER, "  Role found, checking for updates");
		boolean update = false;
		net.dv8tion.jda.api.managers.RoleManager manager = role.getManager();
		if (!roleColor.equals(role.getColor())) {
			Debug.log(ROLE_MANAGER, "    Updating color to " + roleColor);
			update = true;
			manager.setColor(roleColor);
		}
		if (!role.getName().equals(nickname)) {
			Debug.log(ROLE_MANAGER, "    Updating nickname to " + nickname);
			update = true;
			manager.setName(nickname);
		}

		if (update)
			manager.queue(success -> Debug.log("      Updated role"), error -> { throw new RuntimeException(error); });
		else
			Debug.log(ROLE_MANAGER, "    No updates needed");
	}

}
