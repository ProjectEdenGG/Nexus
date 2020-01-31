package me.pugabyte.bncore.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.features.discord.DiscordId;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.litebans.LiteBansService;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.bukkit.ChatColor;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class SuggestCommand extends Command {

	public SuggestCommand() {
		this.name = "suggest";
		this.requiredRole = "Staff";
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(DiscordId.Channel.STAFF_PROMOTIONS.getId()))
			return;

		Tasks.async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: /suggest <player>");

				Nerd nerd = new NerdService().get(Utils.getPlayer(args[0]));
				if (!Arrays.asList(Rank.MEMBER, Rank.TRUSTED).contains(nerd.getRank()))
					throw new InvalidInputException(nerd.getName() + " is not eligble for promotion (They are " + nerd.getRank().noFormat() + ")");

				Hours hours = new HoursService().get(nerd);

				String firstJoin = nerd.getFirstJoin().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
				String hoursTotal = Utils.timespanFormat(hours.getTotal(), "None");
				String hoursMonthly = Utils.timespanFormat(hours.getMonthly(), "None");
				String history = "None";
				if (new LiteBansService().getHistory(nerd.getUuid()) > 0)
					 history = "[View](https://bans.bnn.gg/history.php?uuid=" + nerd.getUuid() + ")";

				EmbedBuilder embed = new EmbedBuilder()
						.appendDescription("\n:calendar_spiral: **First join**: " + firstJoin)
						.appendDescription("\n:clock" + Utils.randomInt(1, 12) + ": **Hours (Total)**: " + hoursTotal)
						.appendDescription("\n:clock" + Utils.randomInt(1, 12) + ": **Hours (Monthly)**: " + hoursMonthly)
						.appendDescription("\n:scroll: **History**: " + history)
						.setThumbnail("https://minotar.net/helm/" + nerd.getName() + "/100.png");

				String rank = null;
				if (nerd.getRank() == Rank.MEMBER) {
					embed.setColor(new Color(255, 255, 85));
					rank = "Trusted";
				} else if (nerd.getRank() == Rank.TRUSTED) {
					embed.setColor(new Color(255, 170, 0));
					rank = "Elite";
				}

				event.reply(new MessageBuilder()
						.setContent(event.getAuthor().getAsMention() + " is suggesting **" + nerd.getName() + "** for **" + rank + "**")
						.setEmbed(embed.build())
						.build());
			} catch (Exception ex) {
				ex.printStackTrace();
				event.reply(ChatColor.stripColor(ex.getMessage()));
			}
		});
	}

}
