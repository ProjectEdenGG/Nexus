package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;

import static me.pugabyte.nexus.utils.TimeUtils.shortDateFormat;

public class BuilderCommand extends CustomCommand {

	public BuilderCommand(CommandEvent event) {
		super(event);
	}

	String builderApp = "https://bnn.gg/apply/builder";

	@Path
	void builder() {
		line(5);
		send(Rank.BUILDER.getChatColor() + "Builders &3help with any build related needs for the server, such as &ewarps&3, &eminigame maps&3, and &eevents&3");
		line();
		send(json()
				.next("&3[+] &eHow to achieve&3: ")
				.next("&eApply").url(builderApp)
				.hover("&eClick to open the application on the website (must be " + Rank.TRUSTED.getChatColor() + "Trusted &3or above)")
				.group());
		send(json("&3[+] &eClick here &3for a list of builders").command("/builder list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		line();
		send("&3All current " + Rank.BUILDER.getChatColor() + "Builders &3and the date they were promoted:");
		Rank.BUILDER.getNerds().forEach(nerd ->
				send(nerd.getColoredName() + " &7-&e " + shortDateFormat(nerd.getPromotionDate())));
		line();
		RanksCommand.ranksReturn(player());
	}
}
