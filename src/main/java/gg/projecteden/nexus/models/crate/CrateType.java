package gg.projecteden.nexus.models.crate;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Map.Entry;

@Getter
public enum CrateType {
	@KeyModel(ItemModelType.CRATE_KEY_VOTE)
	@TitleCharacter("痪")
	VOTE,

	@Disabled
	@KeyModel(ItemModelType.CRATE_KEY_WITHER)
	@TitleCharacter("囱")
	WITHER,

	@KeyModel(ItemModelType.CRATE_KEY_MYSTERY)
	@TitleCharacter("笯")
	@PaginationButtonColor("#dcb30d")
	MYSTERY,

	@KeyModel(ItemModelType.CRATE_KEY_WAKKA)
	@TitleCharacter("清")
	WEEKLY_WAKKA,

	@KeyModel(ItemModelType.CRATE_KEY_MINIGAMES)
	@TitleCharacter("禘")
	MINIGAMES,

	@TitleCharacter("皂")
	ONE_BLOCK,

	@KeyModel(ItemModelType.CRATE_KEY_HALLOWEEN)
	@TitleCharacter("灿")
	HALLOWEEN,
	;

	final ItemStack OLD_KEY = new ItemBuilder(Material.TRIPWIRE_HOOK)
		.name("&eCrate Key")
		.glow()
		.lore(" ")
		.lore("&3Type: &e" + StringUtils.camelCase(name()))
		.lore("&7Use me on the Crate at")
		.lore("&7spawn to receive a reward")
		.build();

	public ItemStack getOldKey() {
		return OLD_KEY;
	}

	public ItemStack getKey() {
		ItemModelType keyItemModel = getKeyItemModel();

		if (keyItemModel == null)
			throw new InvalidInputException("Key model not defined for " + StringUtils.camelCase(this) + " Crate");

		return new ItemBuilder(Material.PAPER)
			.name("&e" + StringUtils.camelCase(this) + " Crate Key")
			.glow()
			.model(keyItemModel.getModel())
			.lore("&7Use me at &e/crates &7to receive a reward")
			.build();
	}

	public static CrateType fromEntity(Entity entity) {
		return CrateConfigService.get().getCrateEntities().entrySet().stream()
			.filter(entry -> entry.getValue().contains(entity.getUniqueId()))
			.map(Entry::getKey)
			.findFirst().orElse(null);
	}

	public static CrateType fromKey(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return null;

		for (CrateType type : values()) {
			ItemModelType keyItemModel = type.getKeyItemModel();
			if (keyItemModel == null)
				continue;

			if (keyItemModel.is(item))
				return type;
		}

		return null;
	}

	public static CrateType fromOldKey(ItemStack item) {
		if (Nullables.isNullOrAir(item)) return null;
		for (CrateType type : values())
			if (type.getOldKey().isSimilar(item))
				return type;
		return null;
	}

	public void give(OfflinePlayer player) {
		give(player, 1);
	}

	public boolean giveVPS(Player player, int amount) {
		ItemStack item = getKey().clone();
		item.setAmount(amount);

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL) {
			PlayerUtils.send(player, "&cYou must be in survival to buy this");
			return false;
		}

		if (!PlayerUtils.hasRoomFor(player.getPlayer(), item)) {
			PlayerUtils.send(player, "&cYou don't have enough space in your inventory");
			return false;
		}

		player.getInventory().addItem(item);
		return true;
	}

	public void give(OfflinePlayer player, int amount) {
		ItemStack item = getKey().clone();
		item.setAmount(amount);
		PlayerUtils.giveItemAndMailExcess(player, item, WorldGroup.SURVIVAL);
	}

	/*
	 * Currently doesn't do anything besides these 2 settings
	 * Added incase a crate needs to override this default behavior
	 */
	public void handleItem(Item item) {
		item.setGravity(false);
		Tasks.wait(10, () -> item.setCustomNameVisible(true));
	}

	public boolean isEnabled() {
		return !getField().isAnnotationPresent(Disabled.class);
	}

	public ItemModelType getKeyItemModel() {
		KeyModel annotation = getField().getAnnotation(KeyModel.class);
		return annotation == null ? null : annotation.value();
	}

	public String getTitleCharacter() {
		TitleCharacter annotation = getField().getAnnotation(TitleCharacter.class);
		return annotation == null ? null : annotation.value();
	}

	public Color getPaginationButtonColor() {
		PaginationButtonColor annotation = getField().getAnnotation(PaginationButtonColor.class);
		return annotation == null ? ColorType.CYAN.getBukkitColor() : ColorType.hexToBukkit(annotation.value());
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface KeyModel {
		ItemModelType value();
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TitleCharacter {
		String value();
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface PaginationButtonColor {
		String value();
	}

}
