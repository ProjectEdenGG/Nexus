package gg.projecteden.nexus.models.trophy;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Trophy;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum TrophyType {

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
	PRIDE_2021(ItemModelType.PRIDE21_TROPHY),

	//	BEAR_FAIR_2021_PARTICIPATION(Material.STONE),
	BEAR_FAIR_2021(ItemModelType.BEARFAIR21_CAKE),
	BEAR_FAIR_2021_MINIGAME_NIGHT_QUEST(ItemModelType.COSTUMES_GG_HAT),
	BEAR_FAIR_2021_MINIGOLF(ItemModelType.BEARFAIR21_MINIGOLF),

	BIRTHDAY_PARTY_2021(ItemModelType.BIRTHDAY21_TROPHY) {
		@Override
		public String toString() {
			return "Griffin & Wakka Birthday Party 2021 Trophy";
		}
	},

	PUGMAS_2021(ItemModelType.PUGMAS_2021_TROPHY),
	EASTER_2022(ItemModelType.EASTER_2022_TROPHY),
	VULAN_2024(ItemModelType.VULAN_2024_TROPHY),

	;

	@NonNull
	private final ItemModelType itemModelType;

	public static void initDecorations() {
		for (TrophyType trophy : values())
			new Trophy(trophy);
	}

	public static @Nullable TrophyType of(ItemStack itemStack) {
		for (TrophyType type : values()) {
			if (ItemUtils.isFuzzyMatch(type.getItem().build(), itemStack))
				return type;
		}
		return null;
	}

	@Override
	public String toString() {
		return StringUtils.camelCase(name()) + " Trophy";
	}

	public ItemBuilder getItem() {
		return new ItemBuilder(itemModelType).name(toString()).untradeable().untrashable();
	}

	public String getEvent() {
		return name().split("\\d{4}")[0];
	}

	public static List<String> getEvents() {
		return Arrays.stream(values())
			.map(TrophyType::getEvent)
			.distinct()
			.toList();
	}

	public void give(HasUniqueId player) {
		new TrophyHolderService().edit(player, holder -> holder.earnAndMessage(this));
	}

	public static List<TrophyType> getTrophies(String event) {
		return Arrays.stream(values())
			.filter(trophy -> trophy.getEvent().equals(event))
			.toList();
	}

	public static List<TrophyType> getEarnedTrophies(TrophyHolder holder, String event) {
		return Arrays.stream(values())
			.filter(trophy -> trophy.getEvent().equals(event))
				.filter(holder::hasEarned)
				.toList();
	}

	public static ItemBuilder getDisplayItem(TrophyHolder holder, String event) {
		List<TrophyType> trophies = Utils.reverse(new ArrayList<>(getTrophies(event)));
		for (TrophyType trophy : trophies)
			if (holder.hasEarned(trophy))
				return trophy.getItem();

		throw new InvalidInputException("Could not determine " + event + " display item for " + holder.getNickname());
	}

}
