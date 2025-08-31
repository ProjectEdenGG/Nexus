package gg.projecteden.nexus.features.store.perks.inventory.autoinventory;

import gg.projecteden.nexus.features.store.StoreCommand;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features.AutoTool;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DescriptionExtra;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NoPermissionException;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.parchment.HasPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum AutoInventoryFeature {
	@Description("Refill tools and items in your hotbar as they run out")
	REFILL,
	@Description("Sort items in your own inventory")
	SORT_OWN_INVENTORY,
	@HasSettings
	@Description("Sort items in chests, shulker boxes, backpacks, etc")
	@DescriptionExtra("&c/autoinv settings inventoryTypes")
	SORT_OTHER_INVENTORIES,
	@Description("Deposit all items in your inventory into chests by shift clicking them")
	QUICK_DEPOSIT,
	@Internal
	@Description("Deposit all items in your inventory into nearby chests with matching items")
	@DescriptionExtra("&c/autoinv depositall")
	DEPOSIT_ALL,
	@HasSettings
	@Description("Automatically craft items into their block form to save space")
	@DescriptionExtra("&c/autoinv settings crafting")
	AUTOCRAFT,
	@HasSettings
	@Description("Disable picking up unwanted items")
	@DescriptionExtra("&c/autoinv settings trash < materials | behavior >")
	AUTOTRASH,
	@HasSettings
	@Description("Automatically switch to the correct tool for the block you are breaking")
	@DescriptionExtra("&c/autoinv settings tools exclude <toolType> &7- Exclude certain tool types from activating AutoTool")
	AUTOTOOL(AutoTool.PERMISSION),
	;

	private final List<String> permissions = new ArrayList<>(List.of(AutoInventory.PERMISSION));

	AutoInventoryFeature(String... extraPermissions) {
		permissions.addAll(Arrays.asList(extraPermissions));
	}

	@Override
	public String toString() {
		return StringUtils.camelCase(name().replaceFirst("AUTO", "AUTO_")).replaceFirst("Auto ", "Auto");
	}

	public void checkPermission(HasPlayer player) throws NoPermissionException {
		if (hasPermission(player)) return;

		throw new NoPermissionException("Purchase at " + StoreCommand.URL);
	}

	public boolean hasPermission(HasPlayer player) {
		for (String permission : permissions)
			if (player.getPlayer().hasPermission(permission))
				return true;
		return false;
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public String getDescription() {
		return getField().getAnnotation(Description.class).value();
	}

	public boolean hasExtraDescription() {
		return getField().isAnnotationPresent(DescriptionExtra.class);
	}

	public String getExtraDescription() {
		if (!hasExtraDescription())
			return null;
		return getField().getAnnotation(DescriptionExtra.class).value();
	}

	public boolean hasSettings() {
		return getField().isAnnotationPresent(HasSettings.class);
	}

	public boolean isInternal() {
		return getField().isAnnotationPresent(Internal.class);
	}

	@Target({ElementType.METHOD, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface HasSettings {}

	@Target({ElementType.METHOD, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Internal {}

}
