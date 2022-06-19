package gg.projecteden.nexus.models.boost;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.shops.Market;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum Boostable {
	EXPERIENCE(Material.EXPERIENCE_BOTTLE),
	MCMMO_EXPERIENCE(Material.NETHERITE_PICKAXE),
	MARKET_SELL_PRICES(Material.OAK_SIGN) {
		@Override
		public void onActivate() {
			Market.load();
		}

		@Override
		public void onExpire() {
			Market.load();
		}
	},
	VOTE_POINTS(Material.DIAMOND),
	MINIGAME_DAILY_TOKENS(Material.DIAMOND_SWORD),
	KILLER_MONEY(Material.GOLD_INGOT),
	MOB_HEADS(Material.ZOMBIE_HEAD),
	MYSTERY_CRATE_KEY(Material.TRIPWIRE_HOOK),
	HALLOWEEN_CANDY(CustomMaterial.FOOD_CANDY_CORN),
	;

	private final Material material;
	private final int modelId;

	Boostable(Material material) {
		this(material, 0);
	}

	Boostable(CustomMaterial customMaterial) {
		this(customMaterial.getMaterial(), customMaterial.getModelId());
	}

	@NotNull
	public ItemBuilder getDisplayItem() {
		return new ItemBuilder(material).customModelData(modelId).name(camelCase(name()));
	}

	public void onActivate() {}

	public void onExpire() {}

}
