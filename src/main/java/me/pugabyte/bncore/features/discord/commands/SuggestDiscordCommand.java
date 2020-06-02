package me.pugabyte.bncore.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Bot.HandledBy;
import me.pugabyte.bncore.features.discord.DiscordId;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.litebans.LiteBansService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class SuggestDiscordCommand extends Command {

	public SuggestDiscordCommand() {
		this.name = "suggest";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(DiscordId.Channel.STAFF_PROMOTIONS.getId()))
			return;

		Tasks.async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: `/suggest <player>`");

				Nerd nerd = new NerdService().get(Utils.getPlayer(args[0]));
				if (!Arrays.asList(Rank.MEMBER, Rank.TRUSTED).contains(nerd.getRank()))
					throw new InvalidInputException(nerd.getName() + " is not eligible for promotion (They are " + nerd.getRank().plain() + ")");

				Hours hours = new HoursService().get(nerd);

				String firstJoin = nerd.getFirstJoin().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
				String hoursTotal = StringUtils.timespanFormat(hours.getTotal(), "None");
				String hoursMonthly = StringUtils.timespanFormat(hours.getMonthly(), "None");
				String history = "None";
				if (new LiteBansService().getHistory(nerd.getUuid()) > 0)
					 history = "[View](https://bans.bnn.gg/history.php?uuid=" + nerd.getUuid() + ")";

				EmbedBuilder embed = new EmbedBuilder()
						.appendDescription("\n:calendar_spiral: **First join**: " + firstJoin)
						.appendDescription("\n:clock" + Utils.randomInt(1, 12) + ": **Hours (Total)**: " + hoursTotal)
						.appendDescription("\n:clock" + Utils.randomInt(1, 12) + ": **Hours (Monthly)**: " + hoursMonthly)
						.appendDescription("\n:scroll: **History**: " + history)
						.setThumbnail("https://minotar.net/helm/" + nerd.getName() + "/100.png");

				Rank next = nerd.getRank().next();
				embed.setColor(next.getColor());

				event.reply(new MessageBuilder()
						.setContent(event.getAuthor().getAsMention() + " is suggesting **" + nerd.getName() + "** for **" + camelCase(next.plain()) + "**")
						.setEmbed(embed.build())
						.build());
			} catch (Exception ex) {
				ex.printStackTrace();
				event.reply(stripColor(ex.getMessage()));
			}
		});
	}

}
