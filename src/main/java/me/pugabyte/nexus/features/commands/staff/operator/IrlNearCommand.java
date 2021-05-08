package me.pugabyte.nexus.features.commands.staff.operator;

import eden.utils.TimeUtils.Time;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.geoip.GeoIP;
import me.pugabyte.nexus.models.geoip.GeoIP.Distance;
import me.pugabyte.nexus.models.geoip.GeoIPService;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Permission("group.seniorstaff")
public class IrlNearCommand extends CustomCommand {

	public IrlNearCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("GeoIP");
	}

	@Async
	@Path("[player] [page]")
	void run(@Arg("self") GeoIP player, @Arg("1") int page) {
		Map<UUID, Distance> near = new HashMap<>() {{
			for (GeoIP geoip : new GeoIPService().getAll())
				if (new HoursService().get(geoip).getTotal() > Time.MINUTE.x(30) / 20)
					put(geoip.getUuid(), new Distance(player, geoip));
		}};

		BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			Distance distance = near.get(uuid);
			String mi = distance.getMilesFormatted();
			String km = distance.getKilometersFormatted();
			return json("&3" + index + " &e" + Nickname.of(uuid) + " &7- " + mi + "mi / " + km + "km");
		};

		paginate(Utils.sortByValue(near).keySet(), formatter, "/irlnear " + player.getNickname(), page);
	}

}
