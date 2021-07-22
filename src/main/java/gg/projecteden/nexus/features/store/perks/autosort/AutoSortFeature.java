package gg.projecteden.nexus.features.store.perks.autosort;

import gg.projecteden.nexus.features.store.perks.autosort.features.AutoTool;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NoPermissionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lexikiq.HasPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.utils.StringUtils.camelCase;

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
	AUTOTOOL(AutoTool.PERMISSION),
	;

	private final List<String> permissions = new ArrayList<>(List.of(AutoSort.PERMISSION));

	AutoSortFeature(String... extraPermissions) {
		permissions.addAll(Arrays.asList(extraPermissions));
	}

	@Override
	public String toString() {
		return camelCase(name().replaceFirst("AUTO", "AUTO_")).replaceFirst("Auto ", "Auto");
	}

	public void checkPermission(HasPlayer player) throws NoPermissionException {
		if (hasPermission(player)) return;

		throw new NoPermissionException("Purchase at https://store.projecteden.gg");
	}

	public boolean hasPermission(HasPlayer player) {
		for (String permission : permissions)
			if (player.getPlayer().hasPermission(permission))
				return true;
		return false;
	}
}
