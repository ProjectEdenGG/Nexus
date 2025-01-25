package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIP.Distance;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@HideFromWiki
@Permission(Group.SENIOR_STAFF)
public class IrlNearCommand extends CustomCommand {
	private final GeoIPService service = new GeoIPService();
	private final HoursService hoursService = new HoursService();

	public IrlNearCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("GeoIP");
	}

	@Async
	@Path("[player] [page]")
	void run(@Arg("self") GeoIP player, @Arg("1") int page) {
		Map<UUID, Distance> near = new HashMap<>() {{
			for (GeoIP geoip : service.getAll()) {
				if (hoursService.get(geoip).has(TickTime.MINUTE.x(30)))
					try {
						put(geoip.getUuid(), new Distance(player, geoip));
					} catch (InvalidInputException ignore) {}
			}
		}};

		BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			Distance distance = near.get(uuid);
			String mi = distance.getMilesFormatted();
			String km = distance.getKilometersFormatted();
			return json(index + " &e" + Nickname.of(uuid) + " &7- " + mi + "mi / " + km + "km");
		};

		new Paginator<UUID>()
			.values(Utils.sortByValue(near).keySet())
			.formatter(formatter)
			.command("/irlnear " + player.getNickname())
			.page(page)
			.send();
	}

}
