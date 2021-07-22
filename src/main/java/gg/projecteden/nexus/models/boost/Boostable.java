package gg.projecteden.nexus.models.boost;

import gg.projecteden.nexus.features.shops.Market;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.utils.StringUtils.camelCase;

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
	MYSTERY_CRATE_KEY(Material.TRIPWIRE_HOOK),
	;

	@NonNull
	public final Material material;
	private int customModelData;

	@NotNull
	public ItemBuilder getDisplayItem() {
		return new ItemBuilder(material).customModelData(customModelData).name(camelCase(name()));
	}

	public void onActivate() {}

	public void onExpire() {}

}
