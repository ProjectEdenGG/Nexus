package me.pugabyte.nexus.features.discord;

import lombok.NonNull;
import me.pugabyte.nexus.features.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.features.Features;
import me.pugabyte.nexus.models.discord.DiscordCaptcha;
import me.pugabyte.nexus.models.discord.DiscordCaptchaService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.utils.StringUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.stream.Collectors;

public class DiscordCommand extends CustomCommand {
	DiscordUserService service = new DiscordUserService();
	DiscordUser user;

	public DiscordCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			user = service.get(player());
	}

	@Path
	@Async
	void run() {
		Guild guild = Discord.getGuild();
		if (guild == null)
			error("Discord bot is not connected");
		TextChannel textChannel = guild.getTextChannelById(DiscordId.TextChannel.GENERAL.getId());
		if (textChannel == null)
			error("General channel not found");

		send(json().next("&e" + textChannel.createInvite().complete().getUrl()));
	}

	@Async
	@Path("account [player]")
	void id(@Arg("self") OfflinePlayer player) {
		DiscordUser self = service.get(player());
		user = service.get(player);

		if (isNullOrEmpty(user.getUserId()))
			error(PREFIX + player.getName() + " has not linked their Discord account");

		try {
			String asMention = user.getMember().getAsMention();
			String message = "Discord account for " + player.getName() + ": ";
			send(json(PREFIX + message + user.getNameAndDiscrim()).hover("&eClick to copy").copy(user.getNameAndDiscrim()));
			if (self.getUserId() != null) {
				self.getMember().getUser().openPrivateChannel().complete()
						.sendMessage(message + asMention).queue();
				send(json(PREFIX + "Koda has sent your direct message on Discord with their username"));
			}
		} catch (ErrorResponseException ex) {
			if (ex.getErrorCode() == 10007)
				error("User has linked their Discord account but is not in the Discord server");
			else
				rethrow(ex);
		}
	}

	@Async
	@Path("link update roles")
	@Permission("group.seniorstaff")
	void updateRoles() {
		int errors = 0;
		Role verified = DiscordId.Role.VERIFIED.get();
		for (DiscordUser discordUser : new DiscordUserService().<DiscordUser>getAll()) {
			if (!isNullOrEmpty(discordUser.getUserId())) {
				try {
					Member member = discordUser.getMember();
					if (member == null) continue;
					if (!member.getRoles().contains(verified))
						Discord.addRole(discordUser.getUserId(), DiscordId.Role.VERIFIED);
				} catch (ErrorResponseException ex) {
					if (ex.getErrorCode() != 10007) {
						++errors;
						ex.printStackTrace();
					}
				}
			}
		}

		send(PREFIX + "Verified roles updated" + (errors > 0 ? " &c(" + errors + " errors)" : ""));
	}

	@Async
	@Path("forceLink <player> <id>")
	void forceLink(OfflinePlayer player, String id) {
		DiscordUserService service = new DiscordUserService();
		DiscordUser user = service.get(player);
		user.setUserId(id);
		if (user.getName() == null)
			error("Could not find user from userId &e" + id);
		service.save(user);
		send("Force linked &e" + player.getName() + " &3to &e" + user.getNameAndDiscrim());
		Discord.addRole(id, DiscordId.Role.VERIFIED);
		Discord.staffLog("**" + user.getIngameName() + "** Discord account force linked to **" + user.getNameAndDiscrim() +  "** by " + name());
	}

	@Async
	@Path("link [code]")
	void link(String code) {
		if (isNullOrEmpty(code)) {
			if (!isNullOrEmpty(user.getUserId())) {
				User userById = Bot.KODA.jda().retrieveUserById(user.getUserId()).complete();
				if (userById == null)
					send(PREFIX + "Your minecraft account is linked to a Discord account, but I could not find that account. " +
							"Are you in our Discord server? &e" + BNSocialMediaSite.DISCORD.getUrl());
				else
					send(PREFIX + "Your minecraft account is linked to " + user.getName());
				send(PREFIX + "You can unlink your account with &c/discord unlink");
				return;
			} else {
				send(PREFIX + "Hello! Looking to &elink &3your &eDiscord &3and &eMinecraft &3accounts? Here's how:");
				line();
				send("&3Step 1: &eOpen our Discord server");
				send("&3Step 2: Type &c/discord link " + name() + " &3in any channel");
				send("&3Step 3: &eCopy the command &3that appears in your &eDMs &3and &epaste it &3into Minecraft");
			}
		} else {
			if (Discord.getCodes().containsKey(code)) {
				DiscordUser newUser = Discord.getCodes().get(code);
				if (!uuid().toString().equals(newUser.getUuid()))
					error("There is no pending confirmation with this account");

				user.setUserId(newUser.getUserId());
				service.save(user);
				Bot.KODA.jda().retrieveUserById(newUser.getUserId()).complete()
						.openPrivateChannel().complete().sendMessage("You have successfully linked your Discord account with the Minecraft account **" + name() + "**").queue();
				send(PREFIX + "You have successfully linked your Minecraft account with the Discord account &e" + user.getNameAndDiscrim());
				Discord.addRole(newUser.getUserId(), DiscordId.Role.VERIFIED);
				Discord.staffLog("**" + name() + "** has linked their discord account to **" + user.getNameAndDiscrim() + "**");
				Discord.getCodes().remove(code);
			} else
				error("Invalid confirmation code");
		}
	}

	@Async
	@Path("unlink [player]")
	void unlink(@Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		user = service.get(player);
		if (isNullOrEmpty(user.getUserId()))
			error("This account is not linked to any Discord account");

		try {
			User userById = Bot.KODA.jda().retrieveUserById(user.getUserId()).complete();
			String name = user.getName();
			String discrim = user.getDiscrim();

			if (isSelf(player))
				userById.openPrivateChannel().complete().sendMessage("This Discord account has been unlinked from the Minecraft account **" + name() + "**").queue();
			send(PREFIX + "Successfully unlinked this Minecraft account from Discord account " + name);
			Discord.staffLog("**" + name() + "** has unlinked their account from **" + name + "#" + discrim + "**");
		} catch (ErrorResponseException ex) {
			if (ex.getErrorCode() == 10007) {
				send(PREFIX + "Successfully unlinked this Minecraft account from an unknown Discord account");
				Discord.staffLog("**" + name() + "** has unlinked their account from an unknown Discord account");
			}
		}

		user.setUserId(null);
		service.save(user);
	}

	@Async
	@Path("linkStatus [player]")
	@Permission("group.staff")
	void linkStatus(@Arg("self") DiscordUser discordUser) {
		send(PREFIX + "Link status of &e" + discordUser.getIngameName());

		if (isNullOrEmpty(discordUser.getUserId()))
			send(" &7- &cNot linked to any member");
		else {
			Member member = discordUser.getMember();
			if (member == null)
				send(json(" &7- &cLinked to unknown member with ID &e").next(discordUser.getUserId()).insert(discordUser.getUserId()));
			else
				send(json(" &7- &3Linked to member &e")
					.next(discordUser.getNameAndDiscrim()).insert(discordUser.getNameAndDiscrim())
					.next(" &3/ &e")
					.next(discordUser.getUserId()).insert(discordUser.getUserId()));
		}

		if (isNullOrEmpty(discordUser.getRoleId()))
			send(" &7- &cNot linked to any bridge role");
		else {
			Role role = Discord.getGuild().getRoleById(discordUser.getRoleId());
			if (role == null)
				send(json(" &7- &cLinked to unknown bridge role with ID &e").next(discordUser.getRoleId()).insert(discordUser.getRoleId()));
			else
				send(json(" &7- &3Linked to bridge role &e")
					.next(role.getName()).insert(role.getName())
					.next(" &3/ &e")
					.next(role.getId()).insert(role.getId()));
		}
	}

	@Async
	@Path("connect")
	@Permission("group.staff")
	void connect() {
		((Discord) Features.get(Discord.class)).connect();
	}

	@Async
	@Path("lockdown")
	@Permission("group.staff")
	void lockdown() {
		SettingService service = new SettingService();
		Setting setting = service.get("discord", "lockdown");
		setting.setBoolean(!setting.getBoolean());
		service.save(setting);

		send(PREFIX + "Lockdown " + (setting.getBoolean() ? "enabled, new members will be automatically kicked" : "disabled"));
	}

	@Async
	@Path("jda dms send <id> <message...>")
	@Permission("group.admin")
	void jda_dms_send(String id, String message) {
		Bot.KODA.jda().retrieveUserById(id).complete().openPrivateChannel().complete().sendMessage(message).queue();
	}

	@Async
	@Path("jda dms view <id>")
	@Permission("group.admin")
	void jda_dms_view(String id) {
		Bot.KODA.jda().retrieveUserById(id).complete().openPrivateChannel().complete().getHistory().retrievePast(50).complete().forEach(message ->
				send(message.getContentRaw()));
	}

	@Async
	@Path("jda dms delete <id>")
	@Permission("group.admin")
	void jda_dms_delete(String id) {
		Bot.KODA.jda().retrieveUserById(id).complete().openPrivateChannel().complete().getHistory().retrievePast(50).complete().forEach(message ->
				message.delete().queue());
	}

	@Async
	@Path("jda getUser <id>")
	@Permission("group.admin")
	void jda_getUser(String id) {
		User user = Bot.KODA.jda().retrieveUserById(id).complete();
		if (user == null)
			error("User is null");
		send(user.getName() + "#" + user.getDiscriminator());
		send("Mutual guilds: " + user.getMutualGuilds().stream().map(Guild::getName).collect(Collectors.joining(", ")));
		send("Has Private Channel: " + user.hasPrivateChannel());
	}

	static {
		new DiscordCaptchaService().get();
	}

	@Async
	@Path("captcha debug")
	@Permission("group.staff")
	void debug() {
		send(new DiscordCaptchaService().get().toString());
	}

	@Async
	@Path("captcha unconfirm <id>")
	@Permission("group.staff")
	void unconfirm(String id) {
		DiscordCaptchaService captchaService = new DiscordCaptchaService();
		DiscordCaptcha captcha = captchaService.get();

		User user = Bot.KODA.jda().retrieveUserById(id).complete();
		Member member = Discord.getGuild().retrieveMemberById(id).complete();

		if (user == null)
			send(PREFIX + "&cWarning: &3User is null");
		if (member == null)
			send(PREFIX + "&cWarning: &3Member is null");

		String name = Discord.getName(id);

		if (!captcha.getConfirmed().containsKey(id))
			error(name + " is not confirmed");

		captcha.getConfirmed().remove(id);
		captchaService.save(captcha);
		send(PREFIX + "Unconfirmed " + name);
	}

	// TODO Restrospective confirmation checks
	@Async
	@Path("captcha info")
	@Permission("group.staff")
	void info() {
		DiscordCaptcha captcha = new DiscordCaptchaService().get();

		captcha.getUnconfirmed().forEach((id, date) -> {
			String name = Discord.getName(id);
			send("ID: " + name + " / Date: " + StringUtils.shortDateTimeFormat(date));
		});
	}

	@ConverterFor(DiscordUser.class)
	DiscordUser convertToDiscordUser(String value) {
		if (value.length() == 18)
			return new DiscordUserService().getFromUserId(value);
		return new DiscordUserService().get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(DiscordUser.class)
	List<String> tabCompleteDiscordUser(String value) {
		return tabCompletePlayer(value);
	}
}
