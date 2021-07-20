package me.pugabyte.nexus.models.boost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.shops.Market;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static eden.utils.StringUtils.camelCase;

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
