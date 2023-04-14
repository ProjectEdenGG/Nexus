package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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
	@NoLiterals
	@Description("View players geographically near you")
	void run(@Optional("self") GeoIP player, @Optional("1") int page) {
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

		paginate(Utils.sortByValue(near).keySet(), formatter, "/irlnear " + player.getNickname(), page);
	}

}
