package me.pugabyte.bncore.features.geoip;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.models.geoip.GeoIP.Distance;
import me.pugabyte.bncore.utils.Utils;

import java.text.DecimalFormat;

public class IrlDistanceCommand extends CustomCommand {

	public IrlDistanceCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Utils.getPrefix("GeoIP");
	}

	@Path("<player> [player]")
	void run(GeoIP from, @Arg("self") GeoIP to) {
		Distance distance = new Distance(from, to);
		DecimalFormat nf = new DecimalFormat("#.00");

		String mi = nf.format(distance.getMiles());
		String km = nf.format(distance.getKilometers());

		String message = "&e" + from.getOfflinePlayer().getName() + " &3is &e" + mi + " miles &3or &e" + km + " kilometers &3away from ";
		send(PREFIX + message + (isSelf(to.getOfflinePlayer()) ? "you" : "&e" + to.getOfflinePlayer().getName()));
	}

}
