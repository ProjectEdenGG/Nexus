package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIP.Distance;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

@Permission(Group.SENIOR_STAFF)
public class IrlDistanceCommand extends CustomCommand {

	public IrlDistanceCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("GeoIP");
	}

	@Async
	@Path("<player> [player]")
	void run(GeoIP from, @Arg("self") GeoIP to) {
		Distance distance = new Distance(from, to);

		String mi = distance.getMilesFormatted();
		String km = distance.getKilometersFormatted();
		String message = "&e" + from.getName() + " &3is &e" + mi + " miles &3or &e" + km + " kilometers &3away from ";
		send(PREFIX + message + (isSelf(to) ? "you" : "&e" + to.getName()));
	}

}
