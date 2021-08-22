package gg.projecteden.nexus.features.votes.vps;

import gg.projecteden.nexus.features.commands.staff.admin.PermHelperCommand;
import gg.projecteden.nexus.features.commands.staff.admin.PermHelperCommand.NumericPermission;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import lombok.NonNull;
import net.luckperms.api.context.ImmutableContextSet;

public class VPSCommand extends CustomCommand {

	public VPSCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[page]")
	void run(@Arg("1") int page) {
		VPS.open(player(), page);
	}

	@Path("buy plot")
	void buyPlot() {
		if (LuckPermsUtils.hasPermission(uuid(), "plots.plot.6", ImmutableContextSet.of("world", "creative")))
			error("You have already purchased the maximum amount of plots");

		new VoterService().edit(player(), voter -> voter.takePoints(150));
		PermHelperCommand.add(NumericPermission.PLOTS, uuid(), 1);
		send(PREFIX + "Purchased &e1 creative plot &3for &e150 vote points");
	}

}
