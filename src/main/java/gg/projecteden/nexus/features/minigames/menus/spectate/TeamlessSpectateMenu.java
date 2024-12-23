package gg.projecteden.nexus.features.minigames.menus.spectate;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

public class TeamlessSpectateMenu extends SpectateMenu {

	public TeamlessSpectateMenu(Match match) {
		super(match);
	}

	@Override
	protected int getRows(Integer page) {
		return (int) Math.ceil(getMatch().getAliveMinigamers().size() / 9.0);
	}

	@Override
	public void init() {
		for (Minigamer minigamer: getMatch().getAliveMinigamers()) {
			if (minigamer.isRespawning()) continue;
			contents.add(ClickableItem.of(
				new ItemBuilder(Material.PLAYER_HEAD)
					.skullOwner(minigamer)
					.name(minigamer.getVanillaColoredName())
					.build(),
				e -> command("mgm spectate player " + minigamer.getNickname())
			));
		}
	}
}
