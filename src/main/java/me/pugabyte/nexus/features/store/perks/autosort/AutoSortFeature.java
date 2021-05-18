package me.pugabyte.nexus.features.store.perks.autosort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.store.perks.autosort.features.AutoTool;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NoPermissionException;

import static eden.utils.StringUtils.camelCase;

@AllArgsConstructor
@Getter
public enum AutoSortFeature {
	REFILL,
	INVENTORY,
	CHESTS,
	QUICK_DEPOSIT,
	DEPOSIT_ALL,
	AUTOCRAFT,
	AUTOTRASH,
	AUTOTOOL(AutoTool.PERMISSION)
	;

	private final String permission;
	AutoSortFeature() {
		permission = AutoSort.PERMISSION;
	}

	@Override
	public String toString() {
		return camelCase(name().replaceFirst("AUTO", "AUTO_")).replaceFirst("Auto ", "Auto");
	}

	public void checkPermission(HasPlayer player) throws NoPermissionException {
		if (!player.getPlayer().hasPermission(getPermission()))
			throw new NoPermissionException("Purchase at https://store.projecteden.gg");
	}
}
