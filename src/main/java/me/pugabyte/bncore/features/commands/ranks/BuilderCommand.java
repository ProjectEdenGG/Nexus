package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.utils.Utils;

public class BuilderCommand extends CustomCommand {

	public BuilderCommand(CommandEvent event) {
		super(event);
	}

	String builderApp = "https://bnn.gg/apply/builder";

	@Path()
	void builder() {
		line(5);
		send("&5Builders &3help with any build related needs for the server, such as &ewarps&3, &eminigame maps&3, and &eevents&3");
		line();
		json("&3[+] &eHow to achieve&3: ||&eApply||url:" + builderApp +
				"||ttp:&eClick to open the application||&3 on the website (must be &eTrusted &3or above)");
		json("&3[+] &eClick here &3for a list of builders||cmd:/builder list");
		line();
		RanksCommand.ranksReturn(player());
	}

	@Path("list")
	void list() {
		line();
		send("&3All current &5Builders &3and the date they were promoted:");
		Rank.BUILDER.getNerds().forEach(nerd -> {
			send(Rank.BUILDER.getFormat() + nerd.getName() + " &7-&e " + Utils.shortDateFormat(nerd.getPromotionDate()));
		});
		line();
		RanksCommand.ranksReturn(player());
	}
}
