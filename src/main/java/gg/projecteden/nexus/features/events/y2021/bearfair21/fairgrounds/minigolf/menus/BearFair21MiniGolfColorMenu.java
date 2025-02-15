package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.menus;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.BearFair21MiniGolf;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.BearFair21MiniGolfUtils;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfColor;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfHole;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21User;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Title("&3Select a color:")
public class BearFair21MiniGolfColorMenu extends InventoryProvider {
	private final MiniGolf21UserService service = new MiniGolf21UserService();
	private MiniGolf21User user;

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(BearFair21MiniGolfColor.values().length);
	}

	@Override
	public void init() {
		user = service.get(viewer);

		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();
		for (BearFair21MiniGolfColor miniGolfColor : BearFair21MiniGolfColor.values()) {
			ItemBuilder item = BearFair21MiniGolf.getGolfBall().clone().model(miniGolfColor.getModel());

			if (miniGolfColor.equals(BearFair21MiniGolfColor.RAINBOW)) {
				item.name(StringUtils.Rainbow.apply(StringUtils.camelCase(miniGolfColor)));

				if (!user.isRainbow()) {
					Set<BearFair21MiniGolfHole> userHoles = user.getHoleInOne();
					StringBuilder missing = new StringBuilder();
					for (BearFair21MiniGolfHole hole : BearFair21MiniGolfHole.getHoles()) {
						if (!userHoles.contains(hole))
							missing.append(hole.getHole()).append(" ");
					}

					item.lore("&cLocked", "", "&cMissing hole-in-ones on:", "&c" + missing.toString().trim());
				}
			} else
				item.name(miniGolfColor.getColorType().getChatColor() + StringUtils.camelCase(miniGolfColor));

			items.add(ClickableItem.of(item.build(), e -> setColor(user, miniGolfColor)));
		}

		paginate(items);
	}

	private void setColor(MiniGolf21User user, BearFair21MiniGolfColor color) {
		if (color.equals(BearFair21MiniGolfColor.RAINBOW) && !user.isRainbow())
			return;

		if (user.isPlaying() && user.getSnowball() == null)
			BearFair21MiniGolf.takeKit(user);

		user.setMiniGolfColor(color);
		service.save(user);

		if (user.isPlaying() && user.getSnowball() == null)
			BearFair21MiniGolf.giveKit(user);

		String message = "Set color to: ";
		String colorName = StringUtils.camelCase(color);

		if (color.equals(BearFair21MiniGolfColor.RAINBOW))
			BearFair21MiniGolfUtils.send(user, message + StringUtils.Rainbow.apply(colorName));
		else
			BearFair21MiniGolfUtils.send(user, message + color.getColorType().getChatColor() + colorName);

		user.getOnlinePlayer().closeInventory();
	}
}
