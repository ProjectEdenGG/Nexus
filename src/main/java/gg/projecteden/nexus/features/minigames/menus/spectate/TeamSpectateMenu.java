package gg.projecteden.nexus.features.minigames.menus.spectate;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

public class TeamSpectateMenu extends SpectateMenu {

	public TeamSpectateMenu(Match match) {
		super(match);
	}

	@Override
	protected int getRows(Integer page) {
		int rows = 0;
		for (Team team : getMatch().getArena().getTeams()) {
			int minigamers = getMatch().getAliveMinigamers(team).size() + 1; // +1 for the row icon
			rows += (int) Math.ceil(minigamers / 9.0);
		}
		return rows;
	}

	@Override
	public void init() {
		int row = 0, column = 0;
		for (Team team : getMatch().getArena().getTeams()) {
			if (column != 0) {
				column = 0;
				row++;
			}

			contents.set(row, column, ClickableItem.empty(
				new ItemBuilder(Material.WHITE_WOOL).color(team.getColorType())
					.name(team.getColorType().getDisplayName())
					.build()
			));
			column++;

			for (Minigamer minigamer : getMatch().getAliveMinigamers(team)) {
				if (minigamer.isRespawning()) continue;
				contents.set(row, column, ClickableItem.of(
					new ItemBuilder(Material.PLAYER_HEAD)
						.skullOwner(minigamer)
						.name(minigamer.getVanillaColoredName())
						.build(),
					e -> command("mgm spectate player " + minigamer.getNickname())
				));

				column++;
				if (column == 9) {
					column = 0;
					row++;
				}
			}
		}
	}
}
