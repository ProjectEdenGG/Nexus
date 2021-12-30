package gg.projecteden.nexus.features.discord.commands;

import com.google.common.base.Strings;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.discord.DiscordId;
import gg.projecteden.discord.DiscordId.User;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NoPermissionException;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.setting.Setting;
import gg.projecteden.nexus.models.setting.SettingService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import net.dv8tion.jda.api.Permission;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.OfflinePlayer;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class DiscordDiscordCommand extends Command {

	public DiscordDiscordCommand() {
		this.name = "discord";
		this.guildOnly = false;
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: `/discord link <name>`");

				DiscordUserService service = new DiscordUserService();
				DiscordUser author = service.getFromUserId(event.getAuthor().getId());

				switch (args[0].toLowerCase()) {
					case "lockdown" -> {
						if (!event.getMember().hasPermission(Permission.KICK_MEMBERS))
							throw new NoPermissionException();
						SettingService settingService = new SettingService();
						Setting setting = settingService.get("discord", "lockdown");
						setting.setBoolean(!setting.getBoolean());
						settingService.save(setting);
						event.reply("Discord lockdown " + (setting.getBoolean() ? "enabled, new members will be automatically kicked" : "disabled"));
					}
					case "link" -> {
						if (args.length < 2)
							throw new InvalidInputException("Correct usage: `/discord link <name>`");
						OfflinePlayer player = PlayerUtils.getPlayer(args[1]);
						DiscordUser fromInput = service.get(player);
						if (author != null)
							// Author already linked
							if (!Strings.isNullOrEmpty(fromInput.getUserId()))
								if (author.getUserId().equals(fromInput.getUserId()))
									throw new InvalidInputException("You are already linked to that minecraft account");
								else
									throw new InvalidInputException("That minecraft account is already linked to a different discord account. Type `/discord unlink` in-game to remove the link.");
							else
								throw new InvalidInputException("You are already linked to a different minecraft account. Use `/discord unlink` in-game to remove the link.");
						if (!Strings.isNullOrEmpty(fromInput.getUserId()))
							// Provided name is already linked
							if (fromInput.getUserId().equals(event.getAuthor().getId()))
								throw new InvalidInputException("This should never happen <@" + User.GRIFFIN.getId() + ">"); // Lookup by user id failed?
							else
								throw new InvalidInputException("That minecraft account is already linked to a different discord account. Type `/discord unlink` in-game to remove the link.");
						String code = RandomStringUtils.randomAlphabetic(6);
						Discord.getCodes().put(code, new DiscordUser(player.getUniqueId(), event.getAuthor().getId()));
						String name = Discord.getName(event.getMember().getId());
						Koda.console("Generated key " + code + " for " + name);
						event.getAuthor().openPrivateChannel().complete().sendMessage("Hey there " + name + "! I've successfully found that minecraft account. " +
								"Please copy and paste the following command into minecraft to confirm the link. (You can use `ctrl+v` or `cmd+v`) " +
								"```/discord link " + code + "```").queue();
						if (event.getMessage().getChannel().getType().isGuild())
							event.reply(event.getAuthor().getAsMention() + " Check your direct messages with " + Bot.KODA.jda().getSelfUser().getAsMention() + " for a confirmation code! (top left of the screen)");
					}
					case "forcelink" -> {
						if (args.length < 3)
							throw new InvalidInputException("Correct usage: `/discord forceLink <name> <mention>`");
						if (!event.getMember().hasPermission(Permission.MANAGE_ROLES))
							throw new NoPermissionException();
						DiscordUser discordUser = service.get(PlayerUtils.getPlayer(args[1]));
						String id = args[2];
						if (Discord.getGuild().getMemberById(id) == null)
							throw new InvalidInputException("Could not find a user from that ID");
						discordUser.setUserId(id);
						service.save(discordUser);
						event.reactSuccess();
						Tasks.wait(TickTime.SECOND.x(5), () -> event.getMessage().delete().queue());
						Discord.addRole(id, DiscordId.Role.VERIFIED);
						Discord.staffLog("**" + discordUser.getNickname() + "** Discord account force linked to **" + discordUser.getNameAndDiscrim() + "** by " + author.getNickname() + " via Discord");
					}
				}
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});

	}

}
