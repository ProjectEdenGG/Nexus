package gg.projecteden.nexus.features.api;

import gg.projecteden.nexus.features.api.annotations.Get;
import gg.projecteden.nexus.features.commands.StaffHallCommand;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

	@Get("/votes/sites")
	Object votes_sites() {
		var sites = new HashMap<>();
		for (VoteSite site : VoteSite.getActiveSites())
			sites.put(site.getName(), site.getUrl());
		return sites;
	}

	@Get("/staff")
	@SneakyThrows
	Object staff() {
		return Utils.flatten(Rank.getStaffNerds().get().values())
			.stream()
			.sorted(new StaffHallCommand.SeniorityComparator())
			.map(nerd -> Map.of(
				"uuid", nerd.getUuid(),
				"uuidNoDashes", nerd.getUuid().toString().replaceAll("-", ""),
				"username", nerd.getName(),
				"nickname", nerd.getNickname(),
				"rank", camelCase(nerd.getRank()),
				"about", nerd.getAbout() == null ? "" : nerd.getAbout(),
				"birthday", nerd.getBirthday() == null ? "" : "%s (%d years)".formatted(shortDateFormat(nerd.getBirthday()), nerd.getBirthday().until(LocalDate.now()).getYears()),
				"pronouns", nerd.getPronouns() == null ? "" : String.join(", ", nerd.getPronouns().stream().map(Nerd.Pronoun::toString).toList()),
				"preferredName", nerd.getPreferredName() == null ? "" : nerd.getPreferredName(),
				"promotionDate", nerd.getPromotionDate() == null ? "" : "%s (%d years)".formatted(shortDateFormat(nerd.getPromotionDate()), nerd.getPromotionDate().until(LocalDate.now()).getYears())
			))
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
