package me.pugabyte.bncore.features.commands.creative;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGroup;

@Fallback("plotsquared")
@Aliases({"plotme", "plots", "plotsquard"})
public class PlotCommand extends CustomCommand {

	public PlotCommand(CommandEvent event) {
		super(event);
	}

	@Path("limit")
	void plot() {
		if (WorldGroup.get(player()) != WorldGroup.CREATIVE)
			error("&3You must be in the &c/creative &3world to use this command!");
		int limit = getLimit();
		if (limit == 0)
			error("&3You cannot claim any plots");
		send("&3You can claim &e" + limit + plural(" &3plot", limit));
	}

	public int getLimit() {
		if (player().hasPermission("plots.plot.6")) return 6;
		if (player().hasPermission("plots.plot.5")) return 5;
		if (player().hasPermission("plots.plot.4")) return 4;
		if (player().hasPermission("plots.plot.3")) return 3;
		if (player().hasPermission("plots.plot.2")) return 2;
		if (player().hasPermission("plots.plot.1")) return 1;
		return 0;
	}


}
