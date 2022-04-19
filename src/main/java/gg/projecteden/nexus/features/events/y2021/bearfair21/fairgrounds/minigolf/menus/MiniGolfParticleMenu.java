package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.menus;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfParticle;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21User;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.menus.MenuUtils.getRows;

public class MiniGolfParticleMenu extends InventoryProvider {
	private final MiniGolf21UserService service = new MiniGolf21UserService();
	private MiniGolf21User user;

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.title(ChatColor.DARK_AQUA + "Select a particle:")
			.rows(getRows(MiniGolfParticle.values().length))
			.closeable(true)
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		user = service.get(player);

		addCloseItem(contents);

		List<ClickableItem> clickableItems = new ArrayList<>();
		for (MiniGolfParticle miniGolfParticle : MiniGolfParticle.values()) {
			ItemStack item = new ItemBuilder(miniGolfParticle.getDisplay()).name(StringUtils.camelCase(miniGolfParticle)).build();
			clickableItems.add(ClickableItem.of(item, e -> setParticle(user, miniGolfParticle)));
		}

		paginator(player, contents, clickableItems).build();
	}

	private void setParticle(MiniGolf21User user, MiniGolfParticle particle) {
		user.setMiniGolfParticle(particle);
		service.save(user);

		MiniGolfUtils.send(user, "Set particle to: &e" + StringUtils.camelCase(user.getMiniGolfParticle()));

		user.getOnlinePlayer().closeInventory();
	}
}
