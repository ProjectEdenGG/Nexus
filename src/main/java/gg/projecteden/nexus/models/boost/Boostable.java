package gg.projecteden.nexus.models.boost;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.shops.Market;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Getter
@AllArgsConstructor
public enum Boostable {
	@PossiblePersonal
	EXPERIENCE(Material.EXPERIENCE_BOTTLE),

	@PossiblePersonal
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

	@AfkAllowed
	@PossiblePersonal
	EXTRA_VOTE_POINTS_CHANCE(Material.DIAMOND),

	@AfkAllowed
	VOTE_POINTS(Material.DIAMOND),

	@AfkAllowed
	@PossiblePersonal
	MINIGAME_DAILY_TOKENS(Material.DIAMOND_SWORD),

	@PossiblePersonal
	KILLER_MONEY(Material.GOLD_INGOT),

	@PossiblePersonal
	MOB_HEADS(Material.ZOMBIE_HEAD),

	@AfkAllowed
	@PossiblePersonal
	MYSTERY_CRATE_KEY(Material.TRIPWIRE_HOOK),

	HALLOWEEN_CANDY(ItemModelType.CANDY_CANDY_CORN),

	HALLOWEEN_CRATE_KEY(ItemModelType.CRATE_KEY_HALLOWEEN),
	;

	private final Material material;
	private final String model;

	Boostable(Material material) {
		this(material, null);
	}

	Boostable(ItemModelType itemModelType) {
		this(itemModelType.getMaterial(), itemModelType.getModel());
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

	public boolean isPossiblePersonal() {
		return getField().isAnnotationPresent(PossiblePersonal.class);
	}

	public boolean isAfkAllowed() {
		return getField().isAnnotationPresent(AfkAllowed.class);
	}

	public void onActivate() {}

	public void onExpire() {}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface PossiblePersonal {}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface AfkAllowed {}

}
