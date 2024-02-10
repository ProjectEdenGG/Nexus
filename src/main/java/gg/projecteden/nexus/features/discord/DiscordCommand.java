package gg.projecteden.nexus.features.discord;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.discord.DiscordId;
import gg.projecteden.api.discord.DiscordId.VoiceChannel;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NoPermissionException;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.discord.DiscordCaptcha;
import gg.projecteden.nexus.models.discord.DiscordCaptchaService;
import gg.projecteden.nexus.models.discord.DiscordConfigService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.List;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;
import static gg.projecteden.nexus.features.discord.Discord.executeOnMessage;
import static gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic.getVoiceChannelMember;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

public class DiscordCommand extends CustomCommand {
	private final DiscordUserService service = new DiscordUserService();
	private DiscordUser user;

	public DiscordCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			user = service.get(player());
	}

	@Path
	@Async
	@Description("Get a link to our Discord")
	void run() {
		String url = EdenSocialMediaSite.DISCORD.getUrl();
		send(json().next("&e" + url).url(url));
	}

	@Async
	@Permission(Group.ADMIN)
	@Path("appcommands register")
	@Description("Force register app commands")
	void appcommands_register() {
		Discord.registerAppCommands();
	}

	@Async
	@Permission(Group.ADMIN)
	@Path("appcommands unregister")
	@Description("Force unregister app commands")
	void appcommands_unregister() {
		Discord.unregisterAppCommands();
	}

	@Async
	@Path("account [player]")
	@Permission(Group.ADMIN)
	@Description("View a player's linked Discord account")
	void id(@Arg("self") OfflinePlayer player) {
		String nickname = Nickname.of(player);

		DiscordUser self = service.get(player());
		user = service.get(player);

		if (isNullOrEmpty(user.getUserId()))
			error(PREFIX + nickname + " has not linked their Discord account");

		if (user.getMember() == null)
			error(PREFIX + nickname + " has not joined the Discord server");

		try {
			String asMention = user.getMember().getAsMention();
			String message = "Discord account for " + nickname + ": ";
			send(json(PREFIX + message + user.getNameAndDiscrim()).hover("&eClick to copy").copy(user.getNameAndDiscrim()));
			if (self.getUserId() != null) {
				self.getMember().getUser().openPrivateChannel().complete().sendMessage(message + asMention).queue();
				send(json(PREFIX + "Koda has sent you a direct message on Discord with their username"));
			}
		} catch (ErrorResponseException ex) {
			if (ex.getErrorCode() == 10007)
				error(new JsonBuilder("User has linked their Discord account but is not in the Discord server. ID: " + user.getUserId()).copy(user.getUserId()).hover("&eClick to copy"));
			else
				rethrow(ex);
		}
	}

	@Async
	@Path("link update roles")
	@Permission(Group.SENIOR_STAFF)
	@Description("Add the verified role to all linked users")
	void updateRoles() {
		int added = 0;
		int errors = 0;
		int notInGuild = 0;
		int noAction = 0;

		Role verified = DiscordId.Role.VERIFIED.get(Bot.KODA.jda());

		for (DiscordUser discordUser : new DiscordUserService().getAll()) {
			if (isNullOrEmpty(discordUser.getUserId()))
				continue;

			try {
				Member member = discordUser.getMember();
				if (member == null) {
					++notInGuild;
					continue;
				}

				if (!member.getRoles().contains(verified)) {
					Discord.addRole(discordUser.getUserId(), DiscordId.Role.VERIFIED);
					++added;
				} else {
					++noAction;
				}
			} catch (ErrorResponseException ex) {
				if (ex.getErrorCode() == 10007) {
					++notInGuild;
				} else {
					++errors;
					ex.printStackTrace();
				}
			}
		}

		send(PREFIX + "Verified roles updated:");
		send("  Added: " + added);
		send("  Errors: " + errors);
		send("  Not in Guild: " + notInGuild);
		send("  No Action: " + noAction);
	}

	@Async
	@Path("forceLink <player> <id>")
	@Permission(Group.MODERATOR)
	@Description("Force link a player's Minecraft and Discord accounts")
	void forceLink(OfflinePlayer player, String id) {
		DiscordUserService service = new DiscordUserService();
		DiscordUser user = service.get(player);
		user.setUserId(id);
		if (user.getUser() == null)
			error("Could not find user from userId &e" + id);
		service.save(user);
		send("&3Force linked &e" + Nickname.of(player) + " &3to &e" + user.getNameAndDiscrim());
		Discord.addRole(id, DiscordId.Role.VERIFIED);
		Discord.staffLog("**" + user.getIngameName() + "** Discord account force linked to **" + user.getNameAndDiscrim() +  "** by " + name());
	}

	@Async
	@Path("link [code]")
	@Description("Link your Minecraft and Discord accounts with the code generated by Koda")
	void link(String code) {
		if (isNullOrEmpty(code)) {
			if (!isNullOrEmpty(user.getUserId())) {
				User userById = Bot.KODA.jda().retrieveUserById(user.getUserId()).complete();
				if (userById == null)
					send(PREFIX + "Your minecraft account is linked to a Discord account, but I could not find that account. " +
							"Are you in our Discord server? &e" + EdenSocialMediaSite.DISCORD.getUrl());
				else
					send(PREFIX + "Your minecraft account is linked to " + user.getDiscordName());
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
				if (!uuid().equals(newUser.getUuid()))
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
	@Description("Unlink your Minecraft and Discord accounts")
	void unlink(@Arg(value = "self", permission = Group.MODERATOR) OfflinePlayer player) {
		user = service.get(player);
		if (isNullOrEmpty(user.getUserId()))
			error("This account is not linked to any Discord account");

		try {
			User userById = Bot.KODA.jda().retrieveUserById(user.getUserId()).complete();
			String name = user.getDiscordName();
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
	@Permission(Group.MODERATOR)
	@Description("View a player's Discord account link status")
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
	@Path("(voicechannel|vc) <channel>")
	@Description("Move voice channels")
	void voicechannel(VoiceChannel channel) {
		Guild guild = Discord.getGuild();
		if (guild == null)
			error("Could not load the Discord server");

		Member member = getVoiceChannelMember(player());
		if (member == null)
			error("Could not find you in a voice channel");

		guild.moveVoiceMember(member, channel.get(Bot.KODA.jda())).queue();
	}

	@Path("boosts")
	@Permission(Group.ADMIN)
	@Description("View server boosters")
	void boosts() {
		for (Member booster : Discord.getGuild().getBoosters())
			send(" - " + booster.getEffectiveName());
	}

	@Path("applyRoles <user>")
	void applyRoles(DiscordUser discordUser) {
		final User user = discordUser.getUser();
		if (user == null)
			throw new InvalidInputException("Discord user for " + discordUser.getNickname() + " not found");
		Discord.applyRoles(user);
		send(PREFIX + "Applied roles to " + discordUser.getNickname());
	}

	@Async
	@Path("connect")
	@Permission(Group.ADMIN)
	@Description("Force connect to discord")
	void connect() {
		Features.get(Discord.class).connect();
	}

	@Async
	@Path("lockdown")
	@Permission(Group.MODERATOR)
	@Description("Enable lockdown mode on Discord")
	void lockdown() {
		new DiscordConfigService().edit0(config -> {
			config.toggleLockdown();
			send(PREFIX + "Lockdown " + (config.isLockdown() ? "enabled, new members will be automatically kicked" : "disabled"));
		});
	}

	@Async
	@Path("jda dms send <id> <message...>")
	@Permission(Group.ADMIN)
	@Description("Send a DM to a member")
	void jda_dms_send(String id, String message) {
		Bot.KODA.jda().retrieveUserById(id).complete().openPrivateChannel().complete().sendMessage(message).queue();
	}

	@Async
	@Path("jda dms view <id>")
	@Permission(Group.ADMIN)
	@Description("Retrieve the last 50 DMs to a user")
	void jda_dms_view(String id) {
		Bot.KODA.jda().retrieveUserById(id).complete().openPrivateChannel().complete().getHistory().retrievePast(50).complete().forEach(message ->
				send(message.getContentRaw()));
	}

	@Async
	@Path("jda dms delete <id>")
	@Permission(Group.ADMIN)
	@Description("Delete the last 50 DMs to a user")
	void jda_dms_delete(String id) {
		Bot.KODA.jda().retrieveUserById(id).complete().openPrivateChannel().complete().getHistory().retrievePast(50).complete().forEach(message ->
				message.delete().queue());
	}

	@Async
	@Confirm
	@Path("jda edit <channelId> <messageId> <content...>")
	@Permission(Group.ADMIN)
	@Description("Set the content of a message")
	void jda_edit(String channelId, String messageId, String content) {
		executeOnMessage(channelId, messageId, message -> message.editMessage(content).queue());
	}

	static {
		new DiscordCaptchaService().get();
	}

	@Disabled
	@Async
	@Path("captcha unconfirm <id>")
	@Permission(Group.STAFF)
	@Description("Revoke a member's captcha confirmation")
	void unconfirm(String id) {
		DiscordCaptchaService captchaService = new DiscordCaptchaService();
		DiscordCaptcha captcha = captchaService.get0();

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
	@Disabled
	@Async
	@Path("captcha info")
	@Permission(Group.STAFF)
	void info() {
		DiscordCaptcha captcha = new DiscordCaptchaService().get();

		captcha.getUnconfirmed().forEach((id, date) -> {
			String name = Discord.getName(id);
			send("ID: " + name + " / Date: " + shortDateTimeFormat(date));
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

	@ConverterFor(VoiceChannel.class)
	VoiceChannel convertToVoiceChannel(String value) {
		try {
			final VoiceChannel voiceChannel = VoiceChannel.valueOf(value.toUpperCase());
			if (!isNullOrEmpty(voiceChannel.getPermission()))
				if (!player().hasPermission(voiceChannel.getPermission()))
					throw new NoPermissionException();
			return voiceChannel;
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException("Discord voice channel &e" + value + " &cnot found");
		}
	}

	@TabCompleterFor(VoiceChannel.class)
	List<String> tabCompleteVoiceChannel(String value) {
		return Arrays.stream(VoiceChannel.values())
			.filter(voiceChannel -> isNullOrEmpty(voiceChannel.getPermission()) || player().hasPermission(voiceChannel.getPermission()))
			.map(voiceChannel -> voiceChannel.name().toLowerCase())
			.toList();
	}
}
