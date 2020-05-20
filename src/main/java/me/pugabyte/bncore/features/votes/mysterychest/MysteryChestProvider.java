package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.SoundUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MysteryChestProvider extends MenuUtils implements InventoryProvider {

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fillRect(0, 0, 2, 8, ClickableItem.empty(new ItemBuilder(
				Material.BLACK_STAINED_GLASS_PANE).name("").build()));
	}

	public static int time = 0;
	public static int index = 0;

	@Override
	public void update(Player player, InventoryContents contents) {
		if (time < 200) {
			String[] colors = {"LIME", "LIGHT_BLUE", "RED", "MAGENTA", "PINK", "YELLOW", "ORANGE"};
			if (index == colors.length) index = 0;
			time++;
			if (time % 4 == 0) {
				SoundUtils.Jingle.PING.play(player);
				contents.fillRow(0, ClickableItem.empty(new ItemBuilder(
						Material.valueOf(colors[index] + "_STAINED_GLASS_PANE")).name("").build()));
				contents.fillRow(2, ClickableItem.empty(new ItemBuilder(
						Material.valueOf(colors[index] + "_STAINED_GLASS_PANE")).name("").build()));
				contents.fillRow(1, ClickableItem.NONE);
				MenuUtils.centerItems(Utils.EnumUtils.nextWithLoop(MysteryChestLoot.class, index).getLoot(), contents, 1);
				index++;
			}
		} else {
			time = 0;
		}

	}
}
