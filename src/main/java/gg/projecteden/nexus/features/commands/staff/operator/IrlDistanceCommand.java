package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIP.Distance;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

@HideFromWiki
@Permission(Group.SENIOR_STAFF)
public class IrlDistanceCommand extends CustomCommand {

	public IrlDistanceCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("GeoIP");
	}

	@Async
	@NoLiterals
	@Description("View the geographic distance between two players")
	void run(GeoIP from, @Optional("self") GeoIP to) {
		Distance distance = new Distance(from, to);

		String mi = distance.getMilesFormatted();
		String km = distance.getKilometersFormatted();
		String message = "&e" + from.getName() + " &3is &e" + mi + " miles &3or &e" + km + " kilometers &3away from ";
		send(PREFIX + message + (isSelf(to) ? "you" : "&e" + to.getName()));
	}

}
