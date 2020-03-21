package me.pugabyte.bncore.features.discord.bridge;

import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.nerd.Nerd;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleManager {
	static Map<Rank, Color> roleColors = new HashMap<Rank, Color>() {{
		put(Rank.OWNER, new Color(153, 45, 34));
		put(Rank.ADMIN, new Color(32, 102, 148));
		put(Rank.OPERATOR, new Color(0, 170, 170));
		put(Rank.MODERATOR, new Color(25, 211, 211));
		put(Rank.ARCHITECT, new Color(132, 61, 164));
		put(Rank.BUILDER, new Color(132, 61, 164));
		put(Rank.VETERAN, new Color(230, 126, 34));
		put(Rank.ELITE, new Color(230, 126, 34));
		put(Rank.TRUSTED, new Color(241, 196, 15));
	}};

	public static List<String> ignore = Arrays.asList(
			"Pugabyte",
			"WakkaFlocka",
			"Filid",
			"Blast",
			"KodaBear");

	public static void update(OfflinePlayer player) {
		if (ignore.contains(player.getName()))
			return;

		DiscordService service = new DiscordService();
		DiscordUser user = service.get(player);

		Nerd nerd = new Nerd(player);
		if (!roleColors.containsKey(nerd.getRank())) {
			user.setRoleId(null);
			service.save(user);
			return;
		}

		if (user == null || user.getRoleId() == null)
			create(player);
		else {
			Role role = Discord.getGuild().getRoleById(user.getRoleId());
			if (role == null)
				create(player);
			else {
				Color color = roleColors.get(nerd.getRank());
				if (role.getColor() != color)
					role.getManager().setColor(color).queue();
			}
		}
	}

	private static void create(OfflinePlayer player) {
		Nerd nerd = new Nerd(player);

		Discord.getGuild().createRole()
				.setColor(roleColors.get(nerd.getRank()))
				.setMentionable(true)
				.queue();
	}

}
