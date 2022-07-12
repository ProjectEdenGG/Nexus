package gg.projecteden.nexus.models.trophy;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Getter
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
	PRIDE_2021(CustomMaterial.PRIDE21_TROPHY),

	//	BEAR_FAIR_2021_PARTICIPATION(Material.STONE),
	BEAR_FAIR_2021(CustomMaterial.BEARFAIR21_CAKE),
	BEAR_FAIR_2021_MINIGAME_NIGHT_QUEST(CustomMaterial.COSTUMES_GG_HAT),
	BEAR_FAIR_2021_MINIGOLF(CustomMaterial.BEARFAIR21_MINIGOLF),

	BIRTHDAY_PARTY_2021(CustomMaterial.BIRTHDAY21_TROPHY) {
		@Override
		public String toString() {
			return "Griffin & Wakka Birthday Party 2021 Trophy";
		}
	},

	PUGMAS_2021(CustomMaterial.PUGMAS_2021_TROPHY),
	EASTER_2022(CustomMaterial.EASTER_2022_TROPHY);

	@NonNull
	private final CustomMaterial material;

	Trophy(CustomMaterial material) {
		this.material = material;

		new DecorationConfig(
			toString(),
			material
		);
	}

	public static void init() {}

	@Override
	public String toString() {
		return camelCase(name()) + " Trophy";
	}

	public ItemBuilder getItem() {
		return new ItemBuilder(material).name(toString()).untradeable();
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
