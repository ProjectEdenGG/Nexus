package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.StringUtils;

public class ModeratorCommand extends CustomCommand {

	public ModeratorCommand(CommandEvent event) {
		super(event);
	}

	String modApp = "https://bnn.gg/apply/mod";

	@Path
	void moderator() {
		line(5);
		send(Rank.MODERATOR.getColor() + "Moderators &3are the first level of staff. They &eanswer any questions &3a player has, &efix grief&3, moderate chat, " +
				"and see too any other basic problems players have.");
		line();
		send(json()
				.next("&3[+] &eHow to achieve&3: ")
				.next("&eApply").url(modApp)
				.hover("&eClick to open the application on the website (must be " + Rank.ELITE.getColor() + "Elite &3or above)")
				.group());
		send(json("&3[+] &eClick here &3for a list of moderators").command("/moderator list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		line();
		send("&3All current " + Rank.MODERATOR.getColor() + "Moderators &3and the date they were promoted:");
		Rank.MODERATOR.getNerds().forEach(nerd ->
				send(nerd.getNicknameFormat() + " &7-&e " + StringUtils.shortDateFormat(nerd.getPromotionDate())));
		line();
		RanksCommand.ranksReturn(player());
	}
}
