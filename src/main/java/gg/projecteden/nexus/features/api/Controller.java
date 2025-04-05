package gg.projecteden.nexus.features.api;

import gg.projecteden.nexus.features.api.annotations.Get;
import gg.projecteden.nexus.features.commands.StaffHallCommand;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.recipes.functionals.InvisibleItemFrame;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.titan.models.CustomCreativeItem;
import gg.projecteden.nexus.features.workbenches.dyestation.CreativeBrushMenu;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsService;
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
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

	@Get("/minigames")
	Object minigames() {
		return new HashMap<>() {{
			ArenaManager.getAllEnabled().stream().map(Arena::getMechanicType).forEach(mechanic -> {
				put(mechanic.name().toLowerCase(), mechanic.get().getName());
			});
		}};
	}

	@Get("/minigames/stats")
	Object minigameStatistics() {
		return new ArrayList<>() {{
			for (MechanicType mechanic : ArenaManager.getAllEnabled().stream().map(Arena::getMechanicType).distinct().sorted().toList()) {
				Map<String, Object> map = new HashMap<>();
				map.put("mechanic", mechanic.name().toLowerCase());
				map.put("title", mechanic.get().getName());

				Map<String, String> stats = new HashMap<>();
				for (MinigameStatistic stat : mechanic.getStatistics())
					stats.put(stat.getId(), stat.getTitle());
				map.put("stats", stats);

				add(map);
			}
		}};
	}

	@Get("/minigames/stats/{mechanic}/{stat}/{date}/{uuid}")
	Object minigameLeaderboard(String mechanic, String stat, String dateTime, String uuid) {
		MechanicType type = MechanicType.valueOf(mechanic.toUpperCase());
		MinigameStatistic statistic = type.getStatistics().stream()
			.filter(_stat -> _stat.getId().equals(stat)).findFirst().orElse(null);

		if (statistic == null)
			return new ArrayList<>();

		LocalDateTime localDateTime = null;
		if (dateTime != null && !dateTime.equals("null")) {
			Instant instant = Instant.parse(dateTime);
			localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		}
		UUID self = null;
		if (uuid != null && !uuid.equals("null"))
			self = UUID.fromString(uuid);

		return new MinigameStatsService().getLeaderboard(type, statistic, localDateTime, self);
	}

}
