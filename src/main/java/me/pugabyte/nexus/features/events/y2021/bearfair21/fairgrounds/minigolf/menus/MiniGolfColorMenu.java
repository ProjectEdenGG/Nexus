package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfColor;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfHole;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21User;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21UserService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MiniGolfColorMenu extends MenuUtils implements InventoryProvider {
	MiniGolf21UserService service = new MiniGolf21UserService();
	MiniGolf21User user;

	public static SmartInventory getInv() {
		return SmartInventory.builder()
				.provider(new MiniGolfColorMenu())
				.size(getRows(MiniGolfColor.values().length), 9)
				.title(ChatColor.DARK_AQUA + "Select a color:")
				.closeable(true)
				.build();
	}

	public void open(Player player) {
		getInv().open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		user = service.get(player);

		addCloseItem(contents);

		List<ClickableItem> clickableItems = new ArrayList<>();
		for (MiniGolfColor miniGolfColor : MiniGolfColor.values()) {
			ItemBuilder item = MiniGolf.getGolfBall().clone().customModelData(miniGolfColor.getCustomModelData());

			if (miniGolfColor.equals(MiniGolfColor.RAINBOW)) {
				item.name(StringUtils.Rainbow.apply(StringUtils.camelCase(miniGolfColor)));

				if (!user.isRainbow()) {
					Set<MiniGolfHole> userHoles = user.getHoleInOne();
					StringBuilder missing = new StringBuilder();
					for (MiniGolfHole hole : MiniGolfHole.getHoles()) {
						if (!userHoles.contains(hole))
							missing.append(hole.getHole()).append(" ");
					}

					item.lore("&cLocked", "", "&cMissing hole-in-ones on:", "&c" + missing.toString().trim());
				}
			} else
				item.name(miniGolfColor.getColorType().getChatColor() + StringUtils.camelCase(miniGolfColor));

			clickableItems.add(ClickableItem.from(item.build(), e -> setColor(user, miniGolfColor)));
		}

		addPagination(player, contents, clickableItems);
	}

	private void setColor(MiniGolf21User user, MiniGolfColor color) {
		if (color.equals(MiniGolfColor.RAINBOW) && !user.isRainbow())
			return;

		if (user.getSnowball() == null)
			MiniGolf.takeKit(user);

		user.setMiniGolfColor(color);
		service.save(user);

		if (user.getSnowball() == null)
			MiniGolf.giveKit(user);

		String message = MiniGolf.getPREFIX() + "Set color to: ";
		String colorName = StringUtils.camelCase(color);

		if (color.equals(MiniGolfColor.RAINBOW))
			MiniGolfUtils.send(user, message + StringUtils.Rainbow.apply(colorName));
		else
			MiniGolfUtils.send(user, message + color.getColorType().getChatColor() + colorName);

		user.getPlayer().closeInventory();
	}
}
