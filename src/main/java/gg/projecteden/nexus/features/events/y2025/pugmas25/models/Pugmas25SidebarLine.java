package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.commands.staff.operator.WeatherCommand.FixedWeatherType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Districts;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Fishing;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public enum Pugmas25SidebarLine {
	ADVENT_DAY() {
		@Override
		public String render(Player player) {
			if (Pugmas25.get().is25thOrAfter())
				return "&3Days unopened: &e" + new Pugmas25UserService().get(player).advent().getUncollected();

			return "&3Advent Day: &e" + Pugmas25.get().now().getDayOfMonth();
		}
	},

	TIME(Pugmas25QuestItem.GOLD_WATCH, Pugmas25QuestItem.GPS) {
		@Override
		public String render(Player player) {
			GeoIP geoIP = new GeoIPService().get(player);
			int time = (int) player.getWorld().getTime();
			boolean is24HourFormat = geoIP.getTimeFormat() == GeoIP.TimeFormat.TWENTY_FOUR;
			String timeLabel = "(" + (player.getWorld().isDayTime() ? "Day" : "Night") + ")";
			return "&3Time: &e" + Utils.minecraftTimeToHumanTime(time, is24HourFormat) + " " + timeLabel;
		}
	},

	WEATHER(Pugmas25QuestItem.WEATHER_RADIO, Pugmas25QuestItem.FISH_FINDER) {
		@Override
		public String render(Player player) {
			return "&3Weather: &e" + StringUtils.camelCase(FixedWeatherType.of(player.getWorld()));
		}
	},

	DIRECTION(Pugmas25QuestItem.COMPASS, Pugmas25QuestItem.GPS) {
		@Override
		public String render(Player player) {
			return "&3Facing: &e" + getCardinalDirection(player);
		}

		private String getCardinalDirection(Player player) {
			float yaw = player.getLocation().getYaw();
			yaw = (yaw % 360 + 360) % 360; // Normalize yaw to 0–360

			String direction;
			if (yaw >= 337.5 || yaw < 22.5) direction = "S";
			else if (yaw < 67.5) direction = "SW";
			else if (yaw < 112.5) direction = "W";
			else if (yaw < 157.5) direction = "NW";
			else if (yaw < 202.5) direction = "N";
			else if (yaw < 247.5) direction = "NE";
			else if (yaw < 292.5) direction = "E";
			else direction = "SE";

			return String.format("%s (%.1f°)", direction, yaw);
		}
	},

	FISHING_LUCK(Pugmas25QuestItem.FISHING_POCKET_GUIDE, Pugmas25QuestItem.FISH_FINDER) {
		@Override
		public String render(Player player) {
			return "&3Fishing Luck: &e" + Pugmas25Fishing.getLuck(player);
		}
	},

	AREA_DESIGNATION(Pugmas25QuestItem.ADVENTURE_POCKET_GUIDE, Pugmas25QuestItem.GPS) {
		@Override
		public String render(Player player) {
			Pugmas25District district = Pugmas25Districts.of(player);
			return "&3Area: &e" + district.getName();
		}
	},

	HEIGHT(Pugmas25QuestItem.SEXTANT, Pugmas25QuestItem.FISH_FINDER) {
		@Override
		public String render(Player player) {
			return "&3Height: &e" + Pugmas25.getPlayerWorldHeight(player);
		}
	},
	;

	@Getter
	Pugmas25QuestItem specificItem;
	List<Pugmas25QuestItem> requiredItems = new ArrayList<>();

	Pugmas25SidebarLine(Pugmas25QuestItem specificItem, Pugmas25QuestItem combinedItem) {
		this.specificItem = specificItem;
		this.requiredItems = List.of(specificItem, combinedItem, Pugmas25QuestItem.PDA);
	}

	public boolean canRender(Player player) {
		if (requiredItems == null || requiredItems.isEmpty())
			return true;

		for (Pugmas25QuestItem item : requiredItems) {
			ItemModelType itemModelType = item.getItemModel();
			if (itemModelType == null)
				continue;

			if (itemModelType.isInInventoryOf(player))
				return true;
		}

		return false;
	}

	public abstract String render(Player player);

	public static List<Pugmas25SidebarLine> getGenericLines() {
		var toolLines = getToolLines();

		return Arrays.stream(values())
			.filter(line -> !toolLines.contains(line))
			.collect(Collectors.toList());
	}

	public static List<Pugmas25SidebarLine> getToolLines() {
		return Arrays.asList(TIME, WEATHER, DIRECTION, FISHING_LUCK, AREA_DESIGNATION, HEIGHT);
	}
}
