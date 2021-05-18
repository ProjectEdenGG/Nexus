package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;

import static me.pugabyte.nexus.utils.TimeUtils.shortDateFormat;

public class ModeratorCommand extends CustomCommand {

	public ModeratorCommand(CommandEvent event) {
		super(event);
	}

	String modApp = "https://projecteden.gg/apply/mod";

	@Path
	void moderator() {
		line(5);
		send(Rank.MODERATOR.getChatColor() + "Moderators &3are the first level of staff. They &eanswer any questions &3a player has, &efix grief&3, moderate chat, " +
				"and see too any other basic problems players have.");
		line();
		send(json()
				.next("&3[+] &eHow to achieve&3: ")
				.next("&eApply").url(modApp)
				.hover("&3Click to open the application on the", "&3website (&emust be " + Rank.ELITE.getChatColor() + "Elite &eor above&3)")
				.group());
		send(json("&3[+] &eClick here &3for a list of moderators").command("/moderator list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		line();
		send("&3All current " + Rank.MODERATOR.getChatColor() + "Moderators &3and the date they were promoted:");
		Rank.MODERATOR.getNerds().forEach(nerd ->
				send(nerd.getColoredName() + " &7-&e " + shortDateFormat(nerd.getPromotionDate())));
		line();
		RanksCommand.ranksReturn(player());
	}
}
