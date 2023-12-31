package gg.projecteden.nexus.features.mcmmo.reset;

import gg.projecteden.nexus.features.mcmmo.reset.McMMOResetShopMenu.SkillTokenFilterType;
import gg.projecteden.nexus.features.mcmmo.reset.annotations.Costs.Cost;
import gg.projecteden.nexus.features.mcmmo.reset.annotations.Reward;
import gg.projecteden.nexus.features.survival.gem.GemCommand;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.SneakyThrows;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.ALCHEMY;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.AXES;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.FISHING;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.GRANDMASTER;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.REPAIR;

public enum ResetReward {
	@Cost(token = ALCHEMY, value = 30)
	POTION_LAUNCHER,

	@Cost(token = ALCHEMY, value = 70)
	@Reward.Permission("nexus.stackpotions")
	POTION_STACK,

	@Cost(token = ALCHEMY, value = 220)
	@Cost(token = GRANDMASTER, value = 10)
	ALCHEMY_TABLE,

	@Cost(token = AXES, value = 100)
	THOR_AXE,

	@Cost(token = AXES, value = 120)
	@Cost(token = REPAIR, value = 45)
	BEHEADING_AXE {
		@Override
		public ItemBuilder getItem() {
			return new ItemBuilder(GemCommand.makeGem(Enchant.BEHEADING));
		}
	},

	@Cost(token = FISHING, value = 100)
	GOD_ROD {
		@Override
		public ItemBuilder getItem() {
			return new ItemBuilder(Material.FISHING_ROD)
				.enchant(Enchant.LURE, 5)
				.enchant(Enchant.LUCK_OF_THE_SEA, 4)
				.enchant(Enchant.MENDING);
		}
	},

	@Cost(token = FISHING, value = 120)
	@Cost(token = REPAIR, value = 35)
	MAGNET_ROD,

	@Cost(token = FISHING, value = 999)
	@Cost(token = REPAIR, value = 50)
	MAGIC_MAGNET_ROD,
	;

	public static List<ResetReward> filter(SkillTokenFilterType filter) {
		if (filter == SkillTokenFilterType.ALL)
			return List.of(values());

		return Arrays.stream(values()).filter(reward -> reward.getCosts().containsKey(filter.toToken())).toList();
	}

	public Map<SkillTokenType, Integer> getCosts() {
		return Arrays.stream(getField().getAnnotationsByType(Cost.class))
			.collect(Collectors.toMap(Cost::token, Cost::value));
	}

	public ItemBuilder getItem() {
		return null;
	}

	public String getPermission() {
		return getField().getAnnotation(Reward.Permission.class).value();
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public ItemBuilder getDisplayItem() {
		var item = getItem();
		if (item == null)
			item = new ItemBuilder(Material.PAPER);
		return item.name(camelCase(this));
	}
}
