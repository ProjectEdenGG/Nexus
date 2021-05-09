package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import eden.exceptions.EdenException;
import eden.utils.TimeUtils.Timespan.TimespanBuilder;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
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
				} else
					next = nerd.getRank().getPromotion();

				if (!Arrays.asList(Rank.MEMBER, Rank.TRUSTED, Rank.ELITE, Rank.VETERAN).contains(nerd.getRank()))
					throw new InvalidInputException(nerd.getName() + " is not eligible for promotion (They are " + nerd.getRank().getName() + ")");

				Hours hours = new HoursService().get(nerd);

				String firstJoin = nerd.getFirstJoin().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
				String hoursTotal = TimespanBuilder.of(hours.getTotal()).noneDisplay(true).format();
				String hoursMonthly = TimespanBuilder.of(hours.getMonthly()).noneDisplay(true).format();
				String history = "None";
				if (Punishments.of(nerd).hasHistory())
					 history = Punishments.of(nerd).getPunishments().size() + " [View](https://justice.projecteden.gg/history/" + nerd.getName() + ")";

				EmbedBuilder embed = new EmbedBuilder()
						.appendDescription("\n:information_source: **Rank**: " + nerd.getRank().getName())
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
						.setContent("@here " + event.getAuthor().getAsMention() + " is suggesting **" + name + "** for **" + camelCase(next.getName()) + "**")
						.setEmbed(embed.build())
						.build());
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
