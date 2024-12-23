package gg.projecteden.nexus.features.minigames.menus.spectate;

import gg.projecteden.nexus.features.menus.api.InventoryManager;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
@Title("Spectate")
@RequiredArgsConstructor
public abstract class SpectateMenu extends InventoryProvider {

	@NotNull
	private Match match;

	@Override
	public void open(Player viewer, int page) {
		super.open(viewer, page);
		Minigamer.of(viewer).setSpectateMenu(this);
	}

	@Override
	public void onClose(InventoryManager manager) {
		super.onClose(manager);
		Minigamer.of(viewer).setSpectateMenu(null);
	}
}
