package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.DiscordId;
import gg.projecteden.discord.DiscordId.User;
import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.Optional;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.discord.appcommands.annotations.Verify;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.discord.DiscordConfigService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.utils.DiscordId;
import gg.projecteden.utils.DiscordId.User;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang.RandomStringUtils;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Command("General discord commands")
public class DiscordAppCommand extends NexusAppCommand {

	public DiscordAppCommand(AppCommandEvent event) {
		super(event);
	}

	@RequiredRole("Staff")
	@Command("Toggle lockdown")
	void lockdown(@Desc("Lockdown state") @Optional Boolean state) {
		new DiscordConfigService().edit0(config -> {
			config.setLockdown(state == null ? !config.isLockdown() : state);
			reply("Discord lockdown " + (config.isLockdown() ? "enabled by " + nickname() + ", new members will be automatically kicked" : "disabled by " + nickname()));
		});
	}

	@Command("Link your Discord and Minecraft accounts")
	void link(@Desc("Minecraft account") DiscordUser player) {
		DiscordUserService service = new DiscordUserService();
		DiscordUser author = service.getFromUserId(member().getId());
		if (author != null)
			// Author already linked
			if (!isNullOrEmpty(player.getUserId()))
				if (author.getUserId().equals(player.getUserId()))
					throw new InvalidInputException("You are already linked to that minecraft account");
				else
					throw new InvalidInputException("That minecraft account is already linked to a different Discord account. Type `/discord unlink` in-game to remove the link.");
			else
				throw new InvalidInputException("You are already linked to a different Minecraft account. Use `/discord unlink` in-game to remove the link.");
		if (!isNullOrEmpty(player.getUserId()))
			// Provided name is already linked
			if (player.getUserId().equals(member().getId()))
				throw new InvalidInputException("This should never happen <@" + User.GRIFFIN.getId() + ">"); // Lookup by user id failed?
			else
				throw new InvalidInputException("That Minecraft account is already linked to a different discord account. Type `/discord unlink` in-game to remove the link.");

		String code = RandomStringUtils.randomAlphabetic(6);
		Discord.getCodes().put(code, new DiscordUser(player.getUuid(), member().getId()));
		Koda.console("Generated key " + code + " for " + Discord.getName(member().getId()));
		replyEphemeral("Copy and paste the following command into minecraft to confirm the link ```/discord link %s```".formatted(code));
	}

	@Verify
	@RequiredRole("Staff")
	@Command("Force link a Minecraft and Discord account")
	void forcelink(
		@Desc("Minecraft account") DiscordUser player,
		@Desc("Discord account") Member discordUser
	) {
		player.setUserId(discordUser.getId());
		new DiscordUserService().save(player);
		Discord.addRole(discordUser.getId(), DiscordId.Role.VERIFIED);
		Discord.staffLog("**%s** Discord account force linked to **%s** by %s via Discord".formatted(player.getNickname(), player.getNameAndDiscrim(), user().getNickname()));
	}

}
