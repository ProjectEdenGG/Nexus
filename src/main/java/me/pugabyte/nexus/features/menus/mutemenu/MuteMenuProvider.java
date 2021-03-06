package me.pugabyte.nexus.features.menus.mutemenu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.mutemenu.MuteMenuService;
import me.pugabyte.nexus.models.mutemenu.MuteMenuUser;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MuteMenuProvider extends MenuUtils implements InventoryProvider {

	@Getter
	public enum MuteMenuItem {
		GLOBAL("Global and Discord Chat", new ItemStack(Material.GREEN_WOOL)),
		LOCAL("Local Chat", new ItemStack(Material.YELLOW_WOOL)),
		AUTO("Automatic Broadcasts", new ItemStack(Material.REPEATER)),
		AFK("AFK Announcements", new ItemStack(Material.REDSTONE_LAMP)),
		FIRSTJOIN("First Join Sounds", new ItemStack(Material.GOLD_BLOCK)),
		JQSOUND("Join/Quit Sounds", new ItemStack(Material.NOTE_BLOCK)),
		JQ("Join/Quit Messages", new ItemStack(Material.OAK_FENCE_GATE)),
		EVENT_ANNOUNCEMENTS("Event Announcements", new ItemStack(Material.WITHER_SKELETON_SKULL)), // TODO
		MINIGAME_ANNOUNCEMENTS("Minigame Announcements", new ItemStack(Material.DIAMOND_SWORD)); // TODO

		public String title;
		public ItemStack itemStack;

		MuteMenuItem(String title, ItemStack itemStack) {
			this.title = title;
			this.itemStack = itemStack;
		}
	}

	MuteMenuService service = new MuteMenuService();

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		MuteMenuUser user = service.get(player.getUniqueId());
		int row = 1;
		int column = 1;
		for (MuteMenuItem item : MuteMenuItem.values()) {
			ItemStack stack = nameItem(item.getItemStack(), "&e" + item.getTitle());
			if (user.hasMuted(item))
				addGlowing(stack);

			contents.set(column, row, ClickableItem.from(stack, e -> toggleMute(user, item)));

			if (row == 8) {
				row = 2;
				column++;
			} else
				row++;
		}
	}

	public void toggleMute(MuteMenuUser user, MuteMenuItem item) {
		Player player = user.getPlayer();
		switch (item) {
			case GLOBAL:
			case LOCAL:
				if (user.hasMuted(item))
					PlayerUtils.runCommand(player, "ch join " + item.name().toLowerCase());
				else
					PlayerUtils.runCommand(player, "ch leave " + item.name().toLowerCase());
				break;
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
