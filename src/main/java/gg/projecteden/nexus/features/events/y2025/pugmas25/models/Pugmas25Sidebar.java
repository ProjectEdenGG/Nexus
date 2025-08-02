package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.commands.staff.operator.WeatherCommand.FixedWeatherType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.parchment.sidebar.Sidebar;
import gg.projecteden.parchment.sidebar.SidebarLayout;
import gg.projecteden.parchment.sidebar.SidebarStage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Pugmas25Sidebar {
	private static final int UPDATE_TICK_INTERVAL = 4;
	private final Map<Player, Pugmas25SidebarLayout> scoreboards = new HashMap<>();


	public void update() {
		Pugmas25.get().getOnlinePlayers().forEach(player -> {
			scoreboards.computeIfAbsent(player, Pugmas25SidebarLayout::new);
		});

		scoreboards.forEach(((player, layout) -> layout.refresh()));
	}

	public void handleJoin(Player player) {
		Pugmas25SidebarLayout layout = scoreboards.put(player, new Pugmas25SidebarLayout(player));
		if (layout != null) {
			Sidebar.get(player).applyLayout(layout);
			layout.start();
		}

		update();
	}

	public void handleQuit(Player player) {
		Pugmas25SidebarLayout layout = scoreboards.remove(player);
		if (layout != null) {
			layout.stop();
		}

		Sidebar.get(player).applyLayout(null);
		update();
	}

	public void handleEnd() {
		scoreboards.forEach((player, scoreboard) -> Sidebar.get(player).applyLayout(null));
		scoreboards.clear();
	}

	public class Pugmas25SidebarLayout extends SidebarLayout {
		private final Player player;
		private int taskId;
		private Iterator<String> headerFrames = titleFrames.iterator();

		public Pugmas25SidebarLayout(Player player) {
			this.player = player;
		}

		@Override
		protected void setup(SidebarStage stage) {
			renderHeader(stage);
			renderLines(stage);
		}

		private void renderHeader(SidebarStage stage) {
			if (headerFrames.hasNext())
				stage.setTitle(headerFrames.next());
			else
				headerFrames = Pugmas25Sidebar.titleFrames.iterator();
		}

		private void renderLines(SidebarStage stage) {
			if (player == null || !player.isOnline())
				return;

			// Clear lines
			for (int i = 0; i < 15; i++) {
				stage.setLine(i, null);
			}

			// Setup lines
			int ndx = 1;
			LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
			for (Pugmas25SidebarLine line : Pugmas25SidebarLine.getGenericLines()) {
				lines.put(line.render(player), ndx++);
			}

			List<Pugmas25SidebarLine> toolLines = Pugmas25SidebarLine.getToolLines().stream()
				.filter(line -> line.canRender(player))
				.toList();

			if (!toolLines.isEmpty()) {
				lines.put("", ndx++);
				for (Pugmas25SidebarLine line : toolLines) {
					lines.put(line.render(player), ndx++);
				}
			}

			// Set lines
			AtomicInteger lineNum = new AtomicInteger();
			lines.forEach((line, score) -> {
				if (lineNum.get() >= 15)
					return;

				stage.setLine(lineNum.getAndIncrement(), line);
			});
		}

		@Override
		protected void update(SidebarStage stage) {
			setup(stage);
		}

		public void stop() {
			Tasks.cancel(this.taskId);
		}

		public void start() {
			this.taskId = Tasks.repeatAsync(1, UPDATE_TICK_INTERVAL, this::refresh);
		}
	}

	private static final List<String> titleFrames = Arrays.asList(
		"&f⛄ &3Pugmas 2025 &f⛄",
		"&f⛄ &3Pugmas 2025 &f⛄",
		"&f⛄ &3Pugmas 2025 &f⛄",
		"&f⛄ &3Pugmas 2025 &f⛄",
		//
		"&f⛄ &bP&3ugmas 2025 &f⛄",
		"&f⛄ &3P&bu&3gmas 2025 &f⛄",
		"&f⛄ &3Pu&bg&3mas 2025 &f⛄",
		"&f⛄ &3Pug&bm&3as 2025 &f⛄",
		"&f⛄ &3Pugm&ba&3s 2025 &f⛄",
		"&f⛄ &3Pugma&bs &32025 &f⛄",
		"&f⛄ &3Pugmas &b2&3025 &f⛄",
		"&f⛄ &3Pugmas 2&b0&325 &f⛄",
		"&f⛄ &3Pugmas 20&b2&35 &f⛄",
		"&f⛄ &3Pugmas 202&b5 &f⛄",
		"&f⛄ &3Pugmas 20&b2&35 &f⛄",
		"&f⛄ &3Pugmas 2&b0&325 &f⛄",
		"&f⛄ &3Pugmas &b2&3025 &f⛄",
		"&f⛄ &3Pugma&bs &32025 &f⛄",
		"&f⛄ &3Pugm&ba&3s 2025 &f⛄",
		"&f⛄ &3Pug&bm&3as 2025 &f⛄",
		"&f⛄ &3Pu&bg&3mas 2025 &f⛄",
		"&f⛄ &3P&bu&3gmas 2025 &f⛄",
		"&f⛄ &bP&3ugmas 2025 &f⛄"
	);

	private enum Pugmas25SidebarLine {
		ADVENT_DAY {
			@Override
			public boolean canRender(Player player) {
				return true;
			}

			@Override
			public String render(Player player) {
				return "&3Advent Day: &e" + Pugmas25.get().now().getDayOfMonth();
			}
		},

		TIME {
			@Override
			public boolean canRender(Player player) {
				return Pugmas25QuestItem.GOLD_WATCH.isInInventory(player) || Pugmas25QuestItem.GPS.isInInventory(player);
			}

			@Override
			public String render(Player player) {
				GeoIP geoIP = new GeoIPService().get(player);
				int time = (int) player.getWorld().getTime();
				boolean is24HourFormat = geoIP.getTimeFormat() == GeoIP.TimeFormat.TWENTY_FOUR;
				return "&3Time: &e" + Utils.minecraftTimeToHumanTime(time, is24HourFormat);
			}
		},

		WEATHER {
			@Override
			public boolean canRender(Player player) {
				return Pugmas25QuestItem.WEATHER_RADIO.isInInventory(player) || Pugmas25QuestItem.FISH_FINDER.isInInventory(player);
			}

			@Override
			public String render(Player player) {
				return "&3Weather: &e" + StringUtils.camelCase(FixedWeatherType.of(player.getWorld()));
			}
		},

		DIRECTION {
			@Override
			public boolean canRender(Player player) {
				return Pugmas25QuestItem.COMPASS.isInInventory(player) || Pugmas25QuestItem.GPS.isInInventory(player);
			}

			@Override
			public String render(Player player) {
				return "&3Facing: &e" + getCardinalDirection(player);
			}

			private String getCardinalDirection(Player player) {
				float yaw = player.getLocation().getYaw();
				yaw = (yaw % 360 + 360) % 360; // Normalize yaw to 0–360

				String direction;
				if (yaw >= 337.5 || yaw < 22.5) direction = "N";
				else if (yaw < 67.5) direction = "NE";
				else if (yaw < 112.5) direction = "E";
				else if (yaw < 157.5) direction = "SE";
				else if (yaw < 202.5) direction = "S";
				else if (yaw < 247.5) direction = "SW";
				else if (yaw < 292.5) direction = "W";
				else direction = "NW";

				return String.format("%s (%.1f°)", direction, yaw);
			}
		},

		FISHING_LUCK {
			@Override
			public boolean canRender(Player player) {
				return Pugmas25QuestItem.FISHING_POCKET_GUIDE.isInInventory(player) || Pugmas25QuestItem.FISH_FINDER.isInInventory(player);
			}

			@Override
			public String render(Player player) {
				return "&3Fishing Luck: &enull";
			}
		},

		AREA_DESIGNATION {
			@Override
			public boolean canRender(Player player) {
				return Pugmas25QuestItem.ADVENTURE_POCKET_GUIDE.isInInventory(player) || Pugmas25QuestItem.GPS.isInInventory(player);
			}

			@Override
			public String render(Player player) {
				Pugmas25District district = Pugmas25Districts.of(player);
				return "&3Area: &e" + district.getName();
			}
		},

		COORDS {
			@Override
			public boolean canRender(Player player) {
				return Pugmas25QuestItem.SEXTANT.isInInventory(player) || Pugmas25QuestItem.FISH_FINDER.isInInventory(player);
			}

			@Override
			public String render(Player player) {
				Location location = player.getLocation();
				return "&3XYZ: &e" + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ();
			}
		},
		;

		public abstract boolean canRender(Player player);

		public abstract String render(Player player);

		public static List<Pugmas25SidebarLine> getGenericLines() {
			var toolLines = getToolLines();

			return Arrays.stream(values())
				.filter(line -> !toolLines.contains(line))
				.collect(Collectors.toList());
		}

		public static List<Pugmas25SidebarLine> getToolLines() {
			return Arrays.asList(TIME, WEATHER, DIRECTION, FISHING_LUCK, AREA_DESIGNATION, COORDS);
		}
	}
}
