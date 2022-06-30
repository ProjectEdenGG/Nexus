package gg.projecteden.nexus.models.boost;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.shops.Market;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Getter
@AllArgsConstructor
public enum Boostable {
	EXPERIENCE(Material.EXPERIENCE_BOTTLE),
	MCMMO_EXPERIENCE(Material.NETHERITE_PICKAXE),
	@Disabled
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
	@Disabled
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

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public boolean isDisabled() {
		return getField().getAnnotation(Disabled.class) != null;
	}

	@NotNull
	public ItemBuilder getDisplayItem() {
		return new ItemBuilder(material).modelId(modelId).name("&6" + camelCase(name()));
	}

	public void onActivate() {}

	public void onExpire() {}

}
