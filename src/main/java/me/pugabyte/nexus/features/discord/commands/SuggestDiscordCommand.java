package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Bot.HandledBy;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.litebans.LiteBansService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils.Timespan;
import me.pugabyte.nexus.utils.Tasks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class SuggestDiscordCommand extends Command {

	public SuggestDiscordCommand() {
		this.name = "suggest";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(TextChannel.STAFF_PROMOTIONS.getId()))
			return;

		Tasks.async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: `/suggest <player> [rank]`");

				Nerd nerd = Nerd.of(PlayerUtils.getPlayer(args[0]));

				Rank next;
				if (args.length >= 2) {
					try {
						next = Rank.valueOf(args[1].toUpperCase());
					} catch (IllegalArgumentException e) {
						throw new InvalidInputException("Rank `" + args[1] + "` not found.");
					}
				}
				// this avoids trying to promote them to veteran
				else if (nerd.getRank() == Rank.ELITE)
					next = Rank.NOBLE;
				else
					next = nerd.getRank().next();

				if (!Arrays.asList(Rank.MEMBER, Rank.TRUSTED, Rank.ELITE, Rank.VETERAN).contains(nerd.getRank()))
					throw new InvalidInputException(nerd.getName() + " is not eligible for promotion (They are " + nerd.getRank().plain() + ")");

				Hours hours = new HoursService().get(nerd);

				String firstJoin = nerd.getFirstJoin().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
				String hoursTotal = Timespan.of(hours.getTotal()).noneDisplay(true).format();
				String hoursMonthly = Timespan.of(hours.getMonthly()).noneDisplay(true).format();
				String history = "None";
				if (new LiteBansService().getHistory(nerd.getUuid().toString()) > 0)
					 history = "[View](https://bans.bnn.gg/history.php?uuid=" + nerd.getUuid() + ")";

				EmbedBuilder embed = new EmbedBuilder()
						.appendDescription("\n:information_source: **Rank**: " + nerd.getRank().plain())
						.appendDescription("\n:calendar_spiral: **First join**: " + firstJoin)
						.appendDescription("\n:clock" + RandomUtils.randomInt(1, 12) + ": **Hours (Total)**: " + hoursTotal)
						.appendDescription("\n:clock" + RandomUtils.randomInt(1, 12) + ": **Hours (Monthly)**: " + hoursMonthly)
						.appendDescription("\n:scroll: **History**: " + history)
						.setThumbnail("https://minotar.net/helm/" + nerd.getName() + "/100.png");

				embed.setColor(next.getDiscordColor());

				String name = nerd.getName();
				if (nerd.hasNickname())
					name += " (" + Nickname.of(nerd) + ")";
				event.reply(new MessageBuilder()
						.setContent("@here " + event.getAuthor().getAsMention() + " is suggesting **" + name + "** for **" + camelCase(next.plain()) + "**")
						.setEmbed(embed.build())
						.build());
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof NexusException))
					ex.printStackTrace();
			}
		});
	}

}
