package gg.projecteden.nexus.features.crates.gemcrafter;

import gg.projecteden.nexus.features.crates.CrateHandler;
import gg.projecteden.nexus.features.crates.gemcrafter.TomeItem.TomeType;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class GemCrafterHandler extends CrateHandler {

	private static GemCrafterHandler instance;

	public static GemCrafterHandler get() {
		if (instance == null)
			instance = new GemCrafterHandler();
		return instance;
	}

	public void handle(ArmorStand armorStand, @NotNull Player player) {
		ItemStack item = ItemUtils.getTool(player);
		if (TomeItem.of(item) == null)
			return;

		openCrate(CrateType.GEM_CRAFTER, armorStand, player, 1, true, item);
	}

	@Override
	public CrateLoot pickCrateLoot(CrateType type, Player player, @Nullable ItemStack key) {
		TomeItem tome = TomeItem.of(key);
		if (tome == null) return null;

		Enchantment enchantment = RandomUtils.randomElement(tome.getType().getEnchantments());
		// Weight only unbreaking
		if (tome.getType() == TomeType.DURABILITY)
			if (RandomUtils.chanceOf(20))
				enchantment = Enchantment.MENDING;
			else
				enchantment = Enchantment.UNBREAKING;

		int maxLevel = enchantment.getMaxLevel();
		maxLevel = Math.clamp(maxLevel, 1, 5);

		double level = 1 + tome.getLevel().ordinal() * (maxLevel - 1) / 4.0;
		if (level % 1 != 0)
			level = RandomUtils.chanceOf((level % 1) * 100) ? Math.ceil(level) : Math.floor(level);

		return new CrateLoot(-1,
				"&d" + AdventureUtils.asPlainText(enchantment.displayName((int) level)),
				Arrays.asList(GemCommand.makeGem(enchantment, (int) level)),
				0, true, CrateType.GEM_CRAFTER, null, null, false, null
			);
	}



}
