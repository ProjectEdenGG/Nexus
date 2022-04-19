package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.menus;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfColor;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfHole;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21User;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.features.menus.MenuUtils.getRows;

public class MiniGolfColorMenu extends InventoryProvider {
	private final MiniGolf21UserService service = new MiniGolf21UserService();
	private MiniGolf21User user;

	@Override
	public void open(Player player, int apge) {
		SmartInventory.builder()
			.provider(this)
			.title("&3Select a color:")
			.rows(getRows(MiniGolfColor.values().length))
			.closeable(true)
			.build()
			.open(player);
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

			clickableItems.add(ClickableItem.of(item.build(), e -> setColor(user, miniGolfColor)));
		}

		paginator(player, contents, clickableItems).build();
	}

	private void setColor(MiniGolf21User user, MiniGolfColor color) {
		if (color.equals(MiniGolfColor.RAINBOW) && !user.isRainbow())
			return;

		if (user.isPlaying() && user.getSnowball() == null)
			MiniGolf.takeKit(user);

		user.setMiniGolfColor(color);
		service.save(user);

		if (user.isPlaying() && user.getSnowball() == null)
			MiniGolf.giveKit(user);

		String message = "Set color to: ";
		String colorName = StringUtils.camelCase(color);

		if (color.equals(MiniGolfColor.RAINBOW))
			MiniGolfUtils.send(user, message + StringUtils.Rainbow.apply(colorName));
		else
			MiniGolfUtils.send(user, message + color.getColorType().getChatColor() + colorName);

		user.getOnlinePlayer().closeInventory();
	}
}
