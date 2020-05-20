package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.SoundUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MysteryChestProvider extends MenuUtils implements InventoryProvider {

	@Override
	public void init(Player player, InventoryContents contents) {

	}

	public static int time = 0;
	public static int index = 0;

	@Override
	public void update(Player player, InventoryContents contents) {
		String[] colors = {"LIME", "LIGHT_BLUE", "RED", "MAGENTA", "PINK", "YELLOW", "ORANGE"};
		if (index == colors.length) index = 0;
		time++;
		if (time % 2 == 0) {
			SoundUtils.Jingle.PING.play(player);
			contents.fillRow(0, ClickableItem.empty(new ItemBuilder(
					Material.valueOf(colors[index] + "_STAINED_GLASS_PANE")).name("").build()));
			contents.fillRow(2, ClickableItem.empty(new ItemBuilder(
					Material.valueOf(colors[index] + "_STAINED_GLASS_PANE")).name("").build()));
			index++;
		}
	}
}
