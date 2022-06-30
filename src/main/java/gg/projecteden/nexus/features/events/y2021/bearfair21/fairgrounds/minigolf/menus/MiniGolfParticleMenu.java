package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.menus;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfParticle;
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
public class MiniGolfParticleMenu extends InventoryProvider {
	private final MiniGolf21UserService service = new MiniGolf21UserService();
	private MiniGolf21User user;

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(MiniGolfParticle.values().length);
	}

	@Override
	public void init() {
		user = service.get(player);

		addCloseItem();

		List<ClickableItem> clickableItems = new ArrayList<>();
		for (MiniGolfParticle miniGolfParticle : MiniGolfParticle.values()) {
			ItemStack item = new ItemBuilder(miniGolfParticle.getDisplay()).name(StringUtils.camelCase(miniGolfParticle)).build();
			clickableItems.add(ClickableItem.of(item, e -> setParticle(user, miniGolfParticle)));
		}

		paginator().items(clickableItems).build();
	}

	private void setParticle(MiniGolf21User user, MiniGolfParticle particle) {
		user.setMiniGolfParticle(particle);
		service.save(user);

		MiniGolfUtils.send(user, "Set particle to: &e" + StringUtils.camelCase(user.getMiniGolfParticle()));

		user.getOnlinePlayer().closeInventory();
	}
}
