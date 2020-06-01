package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.SoundUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.pugabyte.bncore.utils.ColorType.LIGHT_BLUE;
import static me.pugabyte.bncore.utils.ColorType.LIGHT_GREEN;
import static me.pugabyte.bncore.utils.ColorType.MAGENTA;
import static me.pugabyte.bncore.utils.ColorType.ORANGE;
import static me.pugabyte.bncore.utils.ColorType.PINK;
import static me.pugabyte.bncore.utils.ColorType.RED;
import static me.pugabyte.bncore.utils.ColorType.YELLOW;

public class MysteryChestProvider extends MenuUtils implements InventoryProvider {

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fill(ClickableItem.empty(new ItemBuilder(
				Material.BLACK_STAINED_GLASS_PANE).name(" ").build()));
	}

	public static int time = 0;
	public static int colorIndex = 0;
	public static int lootIndex = 0;
	public static int speed = 4;

	@Override
	public void update(Player player, InventoryContents contents) {
		time++;
		if (time < 10) return;
		ColorType[] colors = {LIGHT_GREEN, LIGHT_BLUE, RED, MAGENTA, PINK, YELLOW, ORANGE};
		if (colorIndex == colors.length) colorIndex = 0;

		if (time % speed == 0 && time < 350) {
			SoundUtils.Jingle.PING.play(player);
			contents.fillRow(0, ClickableItem.empty(new ItemBuilder(colors[colorIndex].getStainedGlassPane()).name(" ").build()));
			contents.fillRow(2, ClickableItem.empty(new ItemBuilder(colors[colorIndex].getStainedGlassPane()).name(" ").build()));
			colorIndex++;
			if (lootIndex == MysteryChestLoot.values().length) lootIndex = 0;
			contents.fillRow(1, ClickableItem.NONE);
			MenuUtils.centerItems(Utils.EnumUtils.nextWithLoop(MysteryChestLoot.class, lootIndex).getMenuLoot(), contents, 1, true);
			lootIndex++;
		}

		if (time == 250)
			speed = 10;

		if (speed == 300)
			speed = 15;

		if (time == 350) {
			if (lootIndex == MysteryChestLoot.values().length) lootIndex = 0;
			contents.fillRect(0, 0, 2, 8, ClickableItem.empty(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name(" ").build()));
			contents.fillRow(1, ClickableItem.NONE);
			MenuUtils.centerItems(Utils.EnumUtils.nextWithLoop(MysteryChestLoot.class, lootIndex).getMenuLoot(), contents, 1, true);
			Tasks.wait(10, () -> SoundUtils.Jingle.RANKUP.play(player));
		}

		if (time == 450) {
			MysteryChest.INV.close(player);
			player.sendMessage(StringUtils.colorize(StringUtils.getPrefix("MysteryChest") + "You have received the &e" +
					MysteryChestLoot.values()[lootIndex + 1].getName() + "&3 reward"));
			Utils.giveItems(player, Arrays.asList(MysteryChestLoot.values()[lootIndex + 1].getLoot()));

			new MysteryChest(player).take(1);
		}
	}
}
