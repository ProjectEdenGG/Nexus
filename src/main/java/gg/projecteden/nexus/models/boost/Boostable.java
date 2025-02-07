package gg.projecteden.nexus.models.boost;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.shops.Market;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

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

		@Override
		public boolean isPossiblePersonal() {
			return false;
		}
	},
	VOTE_POINTS(Material.DIAMOND),
	MINIGAME_DAILY_TOKENS(Material.DIAMOND_SWORD),
	KILLER_MONEY(Material.GOLD_INGOT),
	MOB_HEADS(Material.ZOMBIE_HEAD),
	@Disabled
	MYSTERY_CRATE_KEY(Material.TRIPWIRE_HOOK),
	@Disabled
	HALLOWEEN_CANDY(CustomMaterial.FOOD_CANDY_CORN),
	;

	private final Material material;
	private final String model;
	private final boolean possiblePersonal = true;

	Boostable(Material material) {
		this(material, null);
	}

	Boostable(CustomMaterial customMaterial) {
		this(customMaterial.getMaterial(), customMaterial.getModel());
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
		return new ItemBuilder(material).model(model).name("&6" + StringUtils.camelCase(name()));
	}

	public void onActivate() {}

	public void onExpire() {}

}
