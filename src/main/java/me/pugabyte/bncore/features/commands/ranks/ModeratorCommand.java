package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.StringUtils;

public class ModeratorCommand extends CustomCommand {

	public ModeratorCommand(CommandEvent event) {
		super(event);
	}

	String modApp = "https://bnn.gg/apply/mod";

	@Path
	void moderator() {
		line(5);
		send("&b&oModerators &3are the first level of staff. They &eanswer any questions &3a player has, &efix grief&3, moderate chat, " +
				"and see too any other basic problems players have.");
		line();
		send(json()
				.next("&3[+] &eHow to achieve&3:")
				.next(" &eApply").url(modApp)
				.hover("&eClick to open the application on the website (must be &6Elite &3or above)")
				.group());
		send(json("&3[+] &eClick here &3for a list of moderators").command("/moderator list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Path("list")
	void list() {
		line();
		send("&3All current &b&oModerators &3and the date they were promoted:");
		Rank.MODERATOR.getNerds().forEach(nerd -> {
			send(Rank.MODERATOR.getFormat() + nerd.getName() + " &7-&e " + StringUtils.shortDateFormat(nerd.getPromotionDate()));
		});
		line();
		RanksCommand.ranksReturn(player());
	}
}
