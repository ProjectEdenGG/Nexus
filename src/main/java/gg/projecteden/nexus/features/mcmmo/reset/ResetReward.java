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
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.ACROBATICS;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.ALCHEMY;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.ARCHERY;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.AXES;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.EXCAVATION;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.FISHING;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.GRANDMASTER;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.HERBALISM;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.MINING;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.REPAIR;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.SWORDS;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.TAMING;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.UNARMED;
import static gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType.WOODCUTTING;

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
	@Cost(token = GRANDMASTER, value = 15)
	BEHEADING_GEM {
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
	@Cost(token = GRANDMASTER, value = 25)
	MAGIC_MAGNET_ROD,

	@Cost(token = MINING, value = 20)
	@Cost(token = REPAIR, value = 60)
	@Cost(token = GRANDMASTER, value = 10)
	ARCANE_REPAIR_ANVIL,

	@Cost(token = FISHING, value = 20)
	@Cost(token = REPAIR, value = 60)
	@Cost(token = GRANDMASTER, value = 10)
	ARCANE_SALVAGE_ANVIL,

	@Cost(token = REPAIR, value = 100)
	@Cost(token = GRANDMASTER, value = 25)
	AUTO_REPAIR_GEM,

	@Cost(token = UNARMED, value = 30)
	STICK_OF_DISARM_AND_DISROBE,

	@Cost(token = UNARMED, value = 70)
	BANDAGE_OF_WOUND_CLOSURE,

	@Cost(token = FISHING, value = 40)
	@Cost(token = UNARMED, value = 60)
	GRAPPLING_HOOK,

	@Cost(token = WOODCUTTING, value = 100)
	SUPER_TREE_FELLER,

	@Cost(token = WOODCUTTING, value = 100)
	ENERGY_GEM,

	@Cost(token = MINING, value = 100)
	GLOWING_GEM,

	@Cost(token = ALCHEMY, value = 10)
	@Cost(token = EXCAVATION, value = 30)
	@Cost(token = MINING, value = 120)
	PICKAXE_TUNNELING_GEM,

	@Cost(token = MINING, value = 120)
	VEIN_MINER_GEM,

	@Cost(token = HERBALISM, value = 20)
	GNOME,

	@Cost(token = HERBALISM, value = 100)
	DEMETER_BOOTS,

	@Cost(token = ALCHEMY, value = 35)
	@Cost(token = HERBALISM, value = 65)
	FLOWER_GENERATOR,

	@Cost(token = SWORDS, value = 100)
	FROST_SWORD,

	@Cost(token = REPAIR, value = 20)
	@Cost(token = SWORDS, value = 120)
	@Cost(token = GRANDMASTER, value = 5)
	LOOTING_SWORD,

	@Cost(token = ALCHEMY, value = 20)
	@Cost(token = SWORDS, value = 100)
	LIFESTEAL_GEM,

	@Cost(token = ACROBATICS, value = 100)
	GOGO_BOOTS,

	@Cost(token = ALCHEMY, value = 120)
	@Cost(token = ACROBATICS, value = 120)
	@Cost(token = GRANDMASTER, value = 25)
	HOVER_BOOTS,

	@Cost(token = ARCHERY, value = 120)
	@Cost(token = GRANDMASTER, value = 10)
	GOD_BOW,

	@Cost(token = ARCHERY, value = 100)
	GOD_CROSSBOW,

	@Cost(token = ARCHERY, value = 100)
	RESERVES_CROSSBOW,

	@Cost(token = ACROBATICS, value = 45)
	@Cost(token = ARCHERY, value = 150)
	ADVANCED_FLETCHING_TABLE,

	@Cost(token = EXCAVATION, value = 70)
	COLUMN_QUAKE_GEM,

	@Cost(token = EXCAVATION, value = 100)
	SHOVEL_TUNNELING_GEM,

	@Cost(token = TAMING, value = 75)
	WOLF_PACK_COMMAND,

	@Cost(token = TAMING, value = 15)
	PLUNDERING_FANGS,

	@Cost(token = TAMING, value = 20)
	HORSE_PET,

	@Cost(token = TAMING, value = 50)
	@Cost(token = GRANDMASTER, value = 10)
	ALLAY_PET,

	@Cost(token = FISHING, value = 30)
	@Cost(token = UNARMED, value = 30)
	ROD_OF_DISARMING_AND_DISROBING,

	@Cost(token = EXCAVATION, value = 20)
	@Cost(token = MINING, value = 20)
	@Cost(token = WOODCUTTING, value = 20)
	@Cost(token = GRANDMASTER, value = 10)
	MAGNET_GEM,

	@Cost(token = ACROBATICS, value = 120)
	@Cost(token = HERBALISM, value = 120)
	@Cost(token = GRANDMASTER, value = 15)
	BOOTS_OF_THE_TRAILBLAZED_FOREST,

	@Cost(token = ARCHERY, value = 50)
	@Cost(token = SWORDS, value = 50)
	THUNDERING_TRIDENT,

	@Cost(token = ALCHEMY, value = 15)
	@Cost(token = EXCAVATION, value = 10)
	@Cost(token = MINING, value = 10)
	@Cost(token = WOODCUTTING, value = 30)
	ENERGIZING_GEM,

	@Cost(token = ALCHEMY, value = 10)
	@Cost(token = EXCAVATION, value = 20)
	@Cost(token = MINING, value = 20)
	@Cost(token = WOODCUTTING, value = 20)
	EFFICIENCY_GEM,
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
