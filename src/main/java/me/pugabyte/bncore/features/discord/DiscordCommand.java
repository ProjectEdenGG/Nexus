package me.pugabyte.bncore.features.discord;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import net.dv8tion.jda.api.entities.User;

public class DiscordCommand extends CustomCommand {
	DiscordService service = new DiscordService();
	DiscordUser user;

	public DiscordCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			user = service.get(player());
	}

	@Path
	void run() {
		send("&3Join our discord to stay up to date with the community");
		send("&ehttps://discord.bnn.gg");
	}

	@Path("link [code]")
	void link(String code) {
		if (isNullOrEmpty(code)) {
			if (!isNullOrEmpty(user.getUserId())) {
				User userById = Bot.KODA.jda().getUserById(user.getUserId());
				if (userById == null) {
					send(PREFIX + "Your minecraft account is linked to a discord account, but I could not find that account. " +
							"Are you in our discord server? &ehttps://discord..bnn.gg");
				} else {
					send(PREFIX + "Your minecraft account is linked to " + userById.getName());
				}
				send(PREFIX + "You can unlink your account with &c/discord unlink");
				return;
			} else {
				// TODO instructions
			}
		} else {
			// TODO code provided, link
		}
	}

}
