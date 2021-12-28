package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.Optional;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.justice.Justice;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.Timespan.TimespanBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@RequiredRole("Staff")
@Command("Suggest a player for promotion")
public class SuggestAppCommand extends NexusAppCommand {

	public SuggestAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Suggest a player for promotion")
	void run(
		@Desc("Player") Nerd nerd,
		@Desc("Rank") @Optional Rank rank
	) {
		if (rank == null)
			rank = nerd.getRank().getPromotion();

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

		embed.setColor(rank.getDiscordColor());

		String name = nerd.getName();
		if (nerd.hasNickname())
			name = "%s (%s)".formatted(nerd.getNickname(), name);

		reply(new MessageBuilder()
			.setContent("@here " + member().getAsMention() + " is suggesting **" + name + "** for **" + camelCase(rank.getName()) + "**")
			.setEmbeds(embed.build()));
	}

}
