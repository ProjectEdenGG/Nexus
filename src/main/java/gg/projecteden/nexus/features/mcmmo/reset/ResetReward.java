package gg.projecteden.nexus.features.mcmmo.reset;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.mcmmo.reset.McMMOResetShopMenu.SkillTokenFilterType;
import gg.projecteden.nexus.features.mcmmo.reset.annotations.Cost;
import gg.projecteden.nexus.features.mcmmo.reset.annotations.Reward;
import gg.projecteden.nexus.features.survival.gem.GemCommand;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum ResetReward {
	@Cost.Alchemy(30)
	POTION_LAUNCHER,

	@Cost.Alchemy(70)
	@Reward.Permission("nexus.stackpotions")
	POTION_STACK,

	@Cost.Alchemy(220)
	@Cost.Grandmaster(10)
	ALCHEMY_TABLE,

	@Cost.Axes(100)
	THOR_AXE,

	@Cost.Axes(120)
	@Cost.Repair(45)
	@Cost.Grandmaster(15)
	BEHEADING_GEM {
		@Override
		public ItemBuilder getItem() {
			return new ItemBuilder(GemCommand.makeGem(Enchant.BEHEADING));
		}
	},

	@Cost.Fishing(100)
	GOD_ROD {
		@Override
		public ItemBuilder getItem() {
			return new ItemBuilder(Material.FISHING_ROD)
				.enchant(Enchant.LURE, 5)
				.enchant(Enchant.LUCK_OF_THE_SEA, 4)
				.enchant(Enchant.MENDING);
		}
	},

	@Cost.Fishing(120)
	@Cost.Repair(35)
	MAGNET_ROD,

	@Cost.Fishing(999)
	@Cost.Repair(50)
	@Cost.Grandmaster(25)
	MAGIC_MAGNET_ROD,

	@Cost.Mining(20)
	@Cost.Repair(60)
	@Cost.Grandmaster(10)
	ARCANE_REPAIR_ANVIL,

	@Cost.Fishing(20)
	@Cost.Repair(60)
	@Cost.Grandmaster(10)
	ARCANE_SALVAGE_ANVIL,

	@Cost.Repair(100)
	@Cost.Grandmaster(25)
	AUTO_REPAIR_GEM,

	@Cost.Unarmed(30)
	STICK_OF_DISARM_AND_DISROBE,

	@Cost.Unarmed(70)
	BANDAGE_OF_WOUND_CLOSURE,

	@Cost.Fishing(40)
	@Cost.Unarmed(60)
	GRAPPLING_HOOK,

	@Cost.Woodcutting(100)
	SUPER_TREE_FELLER,

	@Cost.Woodcutting(100)
	ENERGY_GEM,

	@Cost.Mining(100)
	GLOWING_GEM,

	@Cost.Alchemy(10)
	@Cost.Excavation(30)
	@Cost.Mining(120)
	PICKAXE_TUNNELING_GEM,

	@Cost.Mining(120)
	VEIN_MINER_GEM,

	@Cost.Herbalism(20)
	GNOME,

	@Cost.Herbalism(100)
	DEMETER_BOOTS,

	@Cost.Alchemy(35)
	@Cost.Herbalism(65)
	FLOWER_GENERATOR,

	@Cost.Swords(100)
	FROST_SWORD,

	@Cost.Repair(20)
	@Cost.Swords(120)
	@Cost.Grandmaster(5)
	LOOTING_SWORD,

	@Cost.Alchemy(20)
	@Cost.Swords(100)
	LIFESTEAL_GEM,

	@Cost.Acrobatics(100)
	GOGO_BOOTS,

	@Cost.Alchemy(120)
	@Cost.Acrobatics(120)
	@Cost.Grandmaster(25)
	HOVER_BOOTS,

	@Cost.Archery(120)
	@Cost.Grandmaster(10)
	GOD_BOW,

	@Cost.Archery(100)
	GOD_CROSSBOW,

	@Cost.Archery(100)
	RESERVES_CROSSBOW,

	@Cost.Acrobatics(45)
	@Cost.Archery(150)
	ADVANCED_FLETCHING_TABLE,

	@Cost.Excavation(70)
	COLUMN_QUAKE_GEM,

	@Cost.Excavation(100)
	SHOVEL_TUNNELING_GEM,

	@Cost.Taming(75)
	WOLF_PACK_COMMAND,

	@Cost.Taming(15)
	PLUNDERING_FANGS,

	@Cost.Taming(20)
	HORSE_PET,

	@Cost.Taming(50)
	@Cost.Grandmaster(10)
	ALLAY_PET,

	@Cost.Fishing(30)
	@Cost.Unarmed(30)
	ROD_OF_DISARMING_AND_DISROBING,

	@Cost.Excavation(20)
	@Cost.Mining(20)
	@Cost.Woodcutting(20)
	@Cost.Grandmaster(10)
	MAGNET_GEM,

	@Cost.Acrobatics(120)
	@Cost.Herbalism(120)
	@Cost.Grandmaster(15)
	BOOTS_OF_THE_TRAILBLAZED_FOREST,

	@Cost.Archery(50)
	@Cost.Swords(50)
	THUNDERING_TRIDENT,

	@Cost.Alchemy(15)
	@Cost.Excavation(10)
	@Cost.Mining(10)
	@Cost.Woodcutting(30)
	ENERGIZING_GEM,

	@Cost.Alchemy(10)
	@Cost.Excavation(20)
	@Cost.Mining(20)
	@Cost.Woodcutting(20)
	EFFICIENCY_GEM,
	;

	public static List<ResetReward> filter(SkillTokenFilterType filter) {
		if (filter == SkillTokenFilterType.ALL)
			return List.of(values());

		return Arrays.stream(values()).filter(reward -> reward.getCosts().containsKey(filter.toToken())).toList();
	}

	public Map<SkillTokenType, Integer> getCosts() {
		return Arrays.stream(getField().getAnnotations())
			.filter(annotation -> SkillTokenType.isToken(annotation.annotationType().getSimpleName()))
			.collect(Collectors.toMap(annotation -> SkillTokenType.valueOf(annotation.annotationType().getSimpleName().toUpperCase()), annotation -> {
				try {
					return (int) annotation.annotationType().getMethod("value").invoke(annotation);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}));
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

	public ItemBuilder buildDisplayItem() {
		ItemBuilder item = getDisplayItem();
		getCosts().forEach((token, amount) -> {
			item.lore(token + ": " + amount);
		});
		return item.name(StringUtils.camelCase(this));
	}

	@NotNull
	private ItemBuilder getDisplayItem() {
		var item = getItem();
		if (item == null)
			item = new ItemBuilder(Material.PAPER);
		return item;
	}
}
