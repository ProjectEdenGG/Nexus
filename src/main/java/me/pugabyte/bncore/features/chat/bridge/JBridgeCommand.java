package me.pugabyte.bncore.features.chat.bridge;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.nerd.Rank;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class JBridgeCommand extends CustomCommand {
	private final DiscordService service;

	public JBridgeCommand(CommandEvent event) {
		super(event);
		service = new DiscordService();
	}

	@Path("get <player>")
	void get(@Arg("self") OfflinePlayer player) {
		DiscordUser user = service.get(player);
		send("User: " + user);
	}

	@Path("getRoleColors")
	void getRoleColors() {
		List<Role> roles = Arrays.asList(Role.OWNER, Role.ADMINS, Role.OPERATORS, Role.MODERATORS, Role.ARCHITECTS, Role.BUILDERS, Role.VETERAN);
		roles.forEach(role -> {
			Color color = Discord.getGuild().getRoleById(role.getId()).getColor();
			BNCore.log(role.name() + " #" + Integer.toHexString(color.getRGB()).substring(2));
		});
	}

	@Async
	@Path("updateRoleColors <rank>")
	void updateRoleColors(Rank rank) {
		int updated = 0;
		for (DiscordUser user : service.getAll()) {
			if (user.getRoleId() == null || user.getUuid() == null)
				continue;

			Rank playerRank = Rank.getHighestRank(user.getOfflinePlayer());
			if (playerRank != rank)
				continue;

			net.dv8tion.jda.api.entities.Role role = Discord.getGuild().getRoleById(user.getRoleId());
			if (role == null)
				continue;

			role.getManager().setColor(rank.getDiscordColor()).queue();
			++updated;
		}

		send("Updated " + updated + " roles");
	}

}
