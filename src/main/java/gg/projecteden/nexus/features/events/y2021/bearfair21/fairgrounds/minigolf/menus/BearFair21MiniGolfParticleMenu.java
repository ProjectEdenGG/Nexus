package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.menus;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.BearFair21MiniGolfUtils;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfParticle;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21User;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("&3Select a particle:")
public class BearFair21MiniGolfParticleMenu extends InventoryProvider {
	private final MiniGolf21UserService service = new MiniGolf21UserService();
	private MiniGolf21User user;

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(BearFair21MiniGolfParticle.values().length);
	}

	@Override
	public void init() {
		user = service.get(viewer);

		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();
		for (BearFair21MiniGolfParticle miniGolfParticle : BearFair21MiniGolfParticle.values()) {
			ItemStack item = new ItemBuilder(miniGolfParticle.getDisplay()).name(StringUtils.camelCase(miniGolfParticle)).build();
			items.add(ClickableItem.of(item, e -> setParticle(user, miniGolfParticle)));
		}

		paginate(items);
	}

	private void setParticle(MiniGolf21User user, BearFair21MiniGolfParticle particle) {
		user.setMiniGolfParticle(particle);
		service.save(user);

		BearFair21MiniGolfUtils.send(user, "Set particle to: &e" + StringUtils.camelCase(user.getMiniGolfParticle()));

		user.getOnlinePlayer().closeInventory();
	}
}
