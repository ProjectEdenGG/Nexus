package gg.projecteden.nexus.features.api;

import gg.projecteden.nexus.features.api.annotations.Get;
import gg.projecteden.nexus.features.api.annotations.Post;
import gg.projecteden.nexus.features.commands.StaffHallCommand;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.recipes.functionals.InvisibleItemFrame;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.titan.models.CustomCreativeItem;
import gg.projecteden.nexus.features.workbenches.dyestation.CreativeBrushMenu;
import gg.projecteden.nexus.models.checkpoint.CheckpointService;
import gg.projecteden.nexus.models.checkpoint.CheckpointUser;
import gg.projecteden.nexus.models.checkpoint.RecordTotalTime;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsService;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsService.LeaderboardRanking;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.voter.TopVoter;
import gg.projecteden.nexus.models.voter.VotePartyService;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.api.common.utils.TimeUtils.shortDateFormat;
import static gg.projecteden.nexus.utils.StringUtils.toHex;

public class Controller {

	@Get("/status")
	Object status() {
		return Map.of(
			"players", PlayerUtils.OnlinePlayers.where().vanished(false).count(),
			"version", Bukkit.getServer().getMinecraftVersion()
		);
	}

	@Get("/votes")
	Object votes() {
		var voterService = new VoterService();
		var voteParty = new VotePartyService().get0();
		var sites = new ArrayList<>();
		var activeVotes = new HashMap<UUID, List<VoteSite>>();

		for (VoteSite site : VoteSite.getActiveSites())
			sites.add(Map.of(
				"id", site.name(),
				"name", site.getName(),
				"url", site.getUrlRaw())
			);

		for (var vote : new VoterService().getActiveVotes())
			activeVotes.computeIfAbsent(vote.getUuid(), $ -> new ArrayList<>()).add(vote.getSite());

		Function<List<TopVoter>, List<Map<String, Object>>> transformer = input -> {
			input = input.subList(0, Math.min(input.size(), 100));
			List<Map<String, Object>> list = new ArrayList<>();
			for (TopVoter voter : input)
				list.add(Map.of(
					"uuid", voter.getVoter().getUniqueId().toString(),
					"name", voter.getNickname(),
					"count", voter.getCount())
				);
			return list;
		};

		return Map.of(
			"sites", sites,
			"voteParty", Map.of(
				"target", voteParty.getCurrentTarget(),
				"current", voteParty.getCurrentAmount()
			),
			"topVoters", Map.of(
				"voteParty", transformer.apply(voterService.getTopVotersSince(voteParty.getStartDate())),
				"monthly", transformer.apply(voterService.getTopVoters(LocalDate.now().getMonth())),
				"allTime", transformer.apply(voterService.getTopVoters())
			),
			"activeVotes", activeVotes
		);
	}

	@Get("/staff")
	@SneakyThrows
	Object staff() {
		return Utils.flatten(Rank.getStaffNerds().get().values())
			.stream()
			.sorted(new StaffHallCommand.SeniorityComparator())
			.map(nerd -> {
				var map = new HashMap<>();
				map.put("uuid", nerd.getUuid());
				map.put("username", nerd.getName());
				map.put("nickname", nerd.getNickname());
				map.put("rank", camelCase(nerd.getRank()));
				map.put("about", nerd.getAbout() == null ? "" : StringUtils.stripColor(nerd.getAbout()));
				map.put("birthday", nerd.getBirthday() == null ? "" : "%s (%d years)".formatted(shortDateFormat(nerd.getBirthday()), nerd.getBirthday().until(LocalDate.now()).getYears()));
				map.put("pronouns", nerd.getPronouns() == null ? "" : String.join(", ", nerd.getPronouns().stream().map(Nerd.Pronoun::toString).toList()));
				map.put("preferredName", nerd.getPreferredName() == null ? "" : nerd.getPreferredName());
				map.put("promotionDate", nerd.getPromotionDate() == null ? "" : "%s (%d years)".formatted(shortDateFormat(nerd.getPromotionDate()), nerd.getPromotionDate().until(LocalDate.now()).getYears()));
				map.put("countryCode", new GeoIPService().get(nerd).getCountryCode());
				return map;
			})
			.toList();
	}

	@Get("/ranks")
	Object ranks() {
		return Arrays.stream(Rank.values()).map(rank -> Map.of(
			"name", rank.getName(),
			"color", toHex(rank.getChatColor())
		)).toList();
	}

	@Get("/nerd/{name}")
	Object nerd(String name) {
		try {
			Nerd nerd = Nerd.of(name);
			return Map.of(
				"uuid", nerd.getUniqueId().toString(),
				"username", nerd.getName(),
				"nickname", nerd.getNickname(),
				"rank", nerd.getRank().name().toLowerCase()
			);
		} catch (Exception ex) {
			Debug.log(ex);
			return null;
		}
	}

	@Get("/diversity")
	Object diversity() {
		Map<String, Map<String, Integer>> data = new HashMap<>();

		var geoipService = new GeoIPService();
		var hoursService = new HoursService();

		geoipService.getAll().forEach(geoip -> {
			var countryCode = geoip.getCountryCode();
			if (countryCode == null)
				return;

			var hours = hoursService.get(geoip);
			int hoursPlayed = hours.getTotal() / 3600;

			if (hoursPlayed < 1)
				return;

			Map<String, Integer> country = data.computeIfAbsent(countryCode, $ -> new HashMap<>());
			country.put("players", country.getOrDefault("players", 0) + 1);
			country.put("hours", country.getOrDefault("hours", 0) + hoursPlayed);
		});

		return data;
	}

	@Get("/maps/{world}/live/players.json")
	Object map_world_live_players(String world) {
		var players = new ArrayList<>();

		for (var player : OnlinePlayers.where().vanished(false).notSpectator().get()) {
			players.add(Map.of(
				"uuid", player.getUniqueId(),
				"name", Nickname.of(player),
				"foreign", !player.getWorld().getName().equals(world),
				"position", Map.of(
					"x", player.getLocation().getX(),
					"y", player.getLocation().getY(),
					"z", player.getLocation().getZ()
				),
				"rotation", Map.of(
					"pitch", player.getLocation().getPitch(),
					"yaw", player.getLocation().getYaw(),
					"roll", 0
				)
			));
		}

		return Map.of("players", players);
	}

	@Get("/titan/creative/categories/{uuid}")
	Object categories(String uuid) {
		Nerd nerd = null;
		if (uuid != null)
			nerd = Nerd.of(UUID.fromString(uuid));

		List<CustomCreativeItem> categories = new ArrayList<>();
		categories.add(new CustomCreativeItem(CreativeBrushMenu.getCreativeBrush(), "Project Eden"));

		if (nerd != null && nerd.getRank().gte(Rank.BUILDER)) // TODO CUSTOM BLOCKS: REMOVE
			categories.addAll(Arrays.asList(CustomBlockUtils.getCreativeCategories()));

		categories.addAll(Arrays.asList(DecorationUtils.getCreativeCategories()));
		return categories.toArray();
	}

	@Get("/titan/creative/items/{uuid}")
	Object items(String uuid) {
		Nerd nerd = null;
		if (uuid != null)
			nerd = Nerd.of(UUID.fromString(uuid));

		List<CustomCreativeItem> items = new ArrayList<>();
		if (nerd != null && nerd.getRank().gte(Rank.BUILDER)) // TODO CUSTOM BLOCKS: REMOVE
			items.addAll(Arrays.asList(CustomBlockUtils.getCreativeItems()));
		items.addAll(Arrays.asList(DecorationUtils.getCreativeItems()));

		items.add(new CustomCreativeItem(CreativeBrushMenu.getCreativeBrush(), "Project Eden"));
		items.add(new CustomCreativeItem(InvisibleItemFrame.getItem(), "Project Eden"));
		items.add(new CustomCreativeItem(new ItemBuilder(Material.LIGHT), "Project Eden"));

		return items.toArray();
	}

	@Get("/minigames/stats")
	Object minigameStatistics() {
		return new ArrayList<>() {{
			for (MechanicType mechanic : ArenaManager.getAllEnabled().stream().map(Arena::getMechanicType)
						.distinct().filter(MechanicType::isEnabled)
						.sorted(Comparator.comparing(Enum::name)).toList()) {

				Map<String, Object> map = new HashMap<>();
				map.put("mechanic", mechanic.name().toLowerCase());
				map.put("title", mechanic.get().getName());
				map.put("description", mechanic.get().getDescription());

				Map<String, String> stats = new HashMap<>();

				if (mechanic.get() instanceof CheckpointMechanic) {
					map.put("timed", true);
					for (Arena arena : ArenaManager.getAllEnabled(mechanic))
						stats.put(arena.getName(), arena.getDisplayName());
				}
				else
					for (MinigameStatistic stat : mechanic.getStatistics())
						stats.put(stat.getId(), stat.getTitle());

				map.put("stats", stats);

				add(map);
			}
		}};
	}

	@Post("/minigames/stats")
	Object minigameLeaderboard(JSONObject body) {
		String mechanic = body.getString("mechanic");
		String stat = body.getString("stat");
		String dateTime = body.optString("date", null);
		String uuid = body.optString("uuid", null);
		int page = body.getInt("page");

		MechanicType type = MechanicType.valueOf(mechanic.toUpperCase());
		MinigameStatistic statistic = type.getStatistics().stream()
			.filter(_stat -> _stat.getId().equals(stat)).findFirst().orElse(null);

		if (statistic == null) {
			if (!(type.get() instanceof CheckpointMechanic))
				return new ArrayList<>();

			try {
				Arena arena = ArenaManager.get(stat);
				statistic = new MinigameStatistic(arena.getName(), arena.getDisplayName()) {
					@Override
					public Object format(long score) {
						return StringUtils.getTimeFormat(Duration.ofMillis(score));
					}
				};
			} catch (Exception ignored) {
				return new ArrayList<>();
			}
		}

		LocalDateTime localDateTime;
		if (dateTime != null) {
			Instant instant = Instant.parse(dateTime);
			localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		} else {
			localDateTime = null;
		}
		UUID self;
		if (uuid != null)
			self = UUID.fromString(uuid);
		else
			self = null;

		MinigameStatistic finalStatistic = statistic;

		CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
		List<LeaderboardRanking> list = new MinigameStatsService().getLeaderboard(type, finalStatistic, localDateTime);
		List<LeaderboardRanking> pageList = list.subList(Math.min(list.size(), (page - 1) * 10), Math.min(list.size(), page * 10));

		Map<String, Object> map = new HashMap<>();
		map.put("leaderboard", pageList);
		map.put("totalRows", list.size());
		if (self != null)
			map.put("self", list.stream().filter(record -> record.getUuid().equals(self)).findFirst().orElse(null));

		return map;
	}

	@Post("/minigames/stats/aggregate")
	Object minigameAggregates(JSONObject body) {
		String mechanic = body.getString("mechanic");
		String dateTime = body.optString("date", null);
		String uuid = body.optString("uuid", null);

		MechanicType type = MechanicType.valueOf(mechanic.toUpperCase());

		LocalDateTime localDateTime = null;
		if (dateTime != null) {
			Instant instant = Instant.parse(dateTime);
			localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		}
		UUID self = null;
		if (uuid != null)
			self = UUID.fromString(uuid);

		List<StatValuePair> list = new ArrayList<>();
		if (type.get() instanceof CheckpointMechanic) {
			if (self == null)
				return new ArrayList<>();

			CheckpointService service = new CheckpointService();
			CheckpointUser user = service.get(self);

			for (Arena arena : ArenaManager.getAllEnabled(type)) {
				RecordTotalTime time = user.getBestTotalTime(arena);
				if (time != null && time.getTime() != null)
					list.add(new StatValuePair(arena.getDisplayName(), StringUtils.getTimeFormat(time.getTime())));
				else
					list.add(new StatValuePair(arena.getDisplayName(), "N/A"));
			}
		}
		else {
			for (MinigameStatistic _stat : type.getStatistics()) {
				list.add(new StatValuePair(_stat.getTitle(), (String) _stat.format(new MinigameStatsService().getAggregates(type, _stat, localDateTime, self))));
			}
		}

		return list;
	}

	@AllArgsConstructor
	public static class StatValuePair {
		private String stat;
		private String value;
	}

}
