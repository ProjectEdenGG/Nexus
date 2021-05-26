package me.pugabyte.nexus.features.menus.sabotage;

import eden.interfaces.Nicknamed;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.matchdata.SabotageMatchData;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageColor;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static eden.utils.StringUtils.plural;

public abstract class AbstractVoteScreen extends MenuUtils implements InventoryProvider {
	public abstract SmartInventory getInventory();

	@Override
	public void open(Player viewer, int page) {
		getInventory().open(viewer, page);
	}

	protected static TextComponent getColoredName(Minigamer minigamer) {
		return new JsonBuilder(minigamer.getNickname(), minigamer.getMatch().<SabotageMatchData>getMatchData().getColor(minigamer)).build();
	}

	protected static ItemBuilder headItemOf(Nicknamed minigamer, SabotageColor color) {
		return new ItemBuilder(color.getHead()).name(new JsonBuilder(minigamer.getNickname(), color));
	}

	protected static ItemBuilder headItemOf(Minigamer minigamer, SabotageMatchData matchData) {
		return headItemOf(minigamer, matchData.getColor(minigamer));
	}

	protected static ClickableItem getClock(String prefix, int seconds) {
		seconds = Math.max(1, seconds);
		return ClickableItem.empty(new ItemBuilder(Material.CLOCK).name("&3" + prefix + " in &e" + plural(seconds + " second", seconds)).amount(seconds).build());
	}

	protected static void setClock(InventoryContents contents, String prefix, int seconds) {
		contents.set(5, 8, getClock(prefix, seconds));
	}

	public void close(HasPlayer player) {
		getInventory().close(player.getPlayer());
	}
}
