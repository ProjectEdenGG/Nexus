package gg.projecteden.nexus.models.trophy;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.lexikiq.HasUniqueId;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.utils.StringUtils.camelCase;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum Trophy {

//	BEAR_FAIR_2020_PARTICIPATION(Material.STONE),
//	BEAR_FAIR_2020_COMPLETION(Material.STONE),
//
//	HALLOWEEN_2020_PARTICIPATION(Material.STONE),
//	HALLOWEEN_2020_COMPLETION(Material.STONE),
//
//	PUGMAS_2020_PARTICIPATION(Material.STONE),
//	PUGMAS_2020_COMPLETION(Material.STONE),
//
//	EASTER_2021_PARTICIPATION(Material.STONE),
//	EASTER_2021_COMPLETION(Material.STONE),

	//	PRIDE_2021_PARTICIPATION(Material.STONE),
	PRIDE_2021(Material.GOLD_INGOT, 1),

	//	BEAR_FAIR_2021_PARTICIPATION(Material.STONE),
	BEAR_FAIR_2021(Material.CAKE, 1),
	BEAR_FAIR_2021_MINIGAME_NIGHT_QUEST(Material.CYAN_STAINED_GLASS_PANE, 101),
	BEAR_FAIR_2021_MINIGOLF(Material.GOLD_INGOT, 2),

	BIRTHDAY_PARTY_2021(Material.CAKE, 3) {
		@Override
		public String toString() {
			return "Griffin & Wakka Birthday Party 2021 Trophy";
		}
	},

	PUGMAS_2021(Material.GOLD_INGOT, 3);

	@NonNull
	private final Material material;
	private int customModelData;

	@Override
	public String toString() {
		return camelCase(name()) + " Trophy";
	}

	public ItemBuilder getItem() {
		return new ItemBuilder(material).name(toString()).customModelData(customModelData).untradeable();
	}

	public String getEvent() {
		return name().split("\\d{4}")[0];
	}

	public static List<String> getEvents() {
		return Arrays.stream(values())
			.map(Trophy::getEvent)
			.distinct()
			.toList();
	}

	public void give(HasUniqueId player) {
		TrophyHolderService trophyService = new TrophyHolderService();
		TrophyHolder holder = trophyService.get(player);
		holder.earnAndMessage(this);
		trophyService.save(holder);
	}

	public static List<Trophy> getTrophies(String event) {
		return Arrays.stream(values())
			.filter(trophy -> trophy.getEvent().equals(event))
			.toList();
	}

	public static List<Trophy> getEarnedTrophies(TrophyHolder holder, String event) {
		return Arrays.stream(values())
			.filter(trophy -> trophy.getEvent().equals(event))
				.filter(holder::hasEarned)
				.toList();
	}

	public static ItemBuilder getDisplayItem(TrophyHolder holder, String event) {
		List<Trophy> trophies = Utils.reverse(new ArrayList<>(getTrophies(event)));
		for (Trophy trophy : trophies)
			if (holder.hasEarned(trophy))
				return trophy.getItem();

		throw new InvalidInputException("Could not determine " + event + " display item for " + holder.getNickname());
	}

}
