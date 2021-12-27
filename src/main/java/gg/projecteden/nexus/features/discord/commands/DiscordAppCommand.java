package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.Optional;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.discord.appcommands.annotations.Verify;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.setting.Setting;
import gg.projecteden.nexus.models.setting.SettingService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.DiscordId;
import gg.projecteden.utils.DiscordId.User;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang.RandomStringUtils;

@HandledBy(Bot.KODA)
public class DiscordAppCommand extends NexusAppCommand {

	public DiscordAppCommand(AppCommandEvent event) {
		super(event);
	}

	@RequiredRole("Staff")
	@Command("Toggle lockdown")
	void lockdown(@Desc("Lockdown state") @Optional Boolean state) {
		SettingService settingService = new SettingService();
		Setting setting = settingService.get("discord", "lockdown");
		setting.setBoolean(state == null ? !setting.getBoolean() : state);
		settingService.save(setting);
		reply("Discord lockdown " + (setting.getBoolean() ? "enabled by " + nickname() + ", new members will be automatically kicked" : "disabled by " + nickname()));
	}

	@Command("Link your Discord and Minecraft accounts")
	void link(@Desc("Minecraft account") DiscordUser discordUser) {
		DiscordUserService service = new DiscordUserService();
		DiscordUser author = service.getFromUserId(member().getId());
		if (author != null)
			// Author already linked
			if (!StringUtils.isNullOrEmpty(discordUser.getUserId()))
				if (author.getUserId().equals(discordUser.getUserId()))
					throw new InvalidInputException("You are already linked to that minecraft account");
				else
					throw new InvalidInputException("That minecraft account is already linked to a different Discord account. Type `/discord unlink` in-game to remove the link.");
			else
				throw new InvalidInputException("You are already linked to a different Minecraft account. Use `/discord unlink` in-game to remove the link.");
		if (!StringUtils.isNullOrEmpty(discordUser.getUserId()))
			// Provided name is already linked
			if (discordUser.getUserId().equals(member().getId()))
				throw new InvalidInputException("This should never happen <@" + User.GRIFFIN.getId() + ">"); // Lookup by user id failed?
			else
				throw new InvalidInputException("That Minecraft account is already linked to a different discord account. Type `/discord unlink` in-game to remove the link.");

		String code = RandomStringUtils.randomAlphabetic(6);
		Discord.getCodes().put(code, new DiscordUser(discordUser.getUuid(), member().getId()));
		String name = Discord.getName(member().getId());
		Koda.console("Generated key " + code + " for " + name);
		replyEphemeral("Copy and paste the following command into minecraft to confirm the link ```/discord link %s```".formatted(code));
	}

	@Verify
	@RequiredRole("Staff")
	@Command("Force link a Minecraft and Discord account")
	void forcelink(
		@Desc("Minecraft account") DiscordUser discordUser,
		@Desc("Discord account") Member member
	) {
		discordUser.setUserId(member.getId());
		new DiscordUserService().save(discordUser);
		Discord.addRole(member.getId(), DiscordId.Role.VERIFIED);
		Discord.staffLog("**%s** Discord account force linked to **%s** by %s via Discord".formatted(discordUser.getNickname(), discordUser.getNameAndDiscrim(), user().getNickname()));
	}

}
