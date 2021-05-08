package me.pugabyte.nexus.features.commands.staff.operator;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.geoip.GeoIP;
import me.pugabyte.nexus.models.geoip.GeoIP.Distance;
import me.pugabyte.nexus.utils.StringUtils;

@Permission("group.seniorstaff")
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
		String message = "&e" + from.getOfflinePlayer().getName() + " &3is &e" + mi + " miles &3or &e" + km + " kilometers &3away from ";
		send(PREFIX + message + (isSelf(to.getOfflinePlayer()) ? "you" : "&e" + to.getOfflinePlayer().getName()));
	}

}
