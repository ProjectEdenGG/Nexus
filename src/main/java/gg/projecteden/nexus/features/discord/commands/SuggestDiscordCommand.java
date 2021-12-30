package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.discord.DiscordId.Role;
import gg.projecteden.discord.DiscordId.TextChannel;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.justice.Justice;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Timespan.TimespanBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

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
					 history = Punishments.of(nerd).getPunishments().size() + " [View](" + Justice.URL + "/history/" + nerd.getName() + ")";

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
