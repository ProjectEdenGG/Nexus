package gg.projecteden.nexus.features.api;

import gg.projecteden.nexus.features.api.annotations.Get;
import gg.projecteden.nexus.features.commands.StaffHallCommand;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.voter.TopVoter;
import gg.projecteden.nexus.models.voter.VotePartyService;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		var sites = new HashMap<>();

		for (VoteSite site : VoteSite.getActiveSites())
			sites.put(site.getName(), site.getUrl());

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
			)
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
				map.put("uuidNoDashes", nerd.getUuid().toString().replaceAll("-", ""));
				map.put("username", nerd.getName());
				map.put("nickname", nerd.getNickname());
				map.put("rank", camelCase(nerd.getRank()));
				map.put("about", nerd.getAbout() == null ? "" : nerd.getAbout());
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
}
