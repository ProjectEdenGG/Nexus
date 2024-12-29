package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus;

import gg.projecteden.api.interfaces.Nicknamed;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.SabotageColor;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.parchment.HasPlayer;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;

public abstract class AbstractVoteScreen extends InventoryProvider {

	protected static TextComponent getColoredName(Minigamer minigamer) {
		return new JsonBuilder(minigamer.getNickname(), minigamer.getMatch().<SabotageMatchData>getMatchData().getColor(minigamer).colored()).build();
	}

	protected static ItemBuilder headItemOf(Nicknamed minigamer, SabotageColor color) {
		return new ItemBuilder(color.getHead()).name(new JsonBuilder(minigamer.getNickname(), color.colored()));
	}

	protected static ItemBuilder headItemOf(Minigamer minigamer, SabotageMatchData matchData) {
		return headItemOf(minigamer, matchData.getColor(minigamer));
	}

	protected ClickableItem getClock(String prefix, int seconds) {
		seconds = Math.max(1, seconds);
		return ClickableItem.empty(new ItemBuilder(Material.CLOCK).name("&3" + prefix + " in &e" + StringUtils.plural(seconds + " second", seconds)).amount(seconds).build());
	}

	protected void setClock(String prefix, int seconds) {
		contents.set(5, 8, getClock(prefix, seconds));
	}

	public void close(HasPlayer player) {
		SmartInvsPlugin.close(player.getPlayer());
	}

}
