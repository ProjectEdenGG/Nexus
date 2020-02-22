package me.pugabyte.bncore.features.discord.bridge;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import org.bukkit.OfflinePlayer;

public class JBridgeCommand extends CustomCommand {
	private DiscordService service;

	public JBridgeCommand(CommandEvent event) {
		super(event);
		service = new DiscordService();
	}

	@Path("get <player>")
	void get(@Arg("self") OfflinePlayer player) {
		DiscordUser user = service.get(player);
		send("User: " + user);
	}

}
