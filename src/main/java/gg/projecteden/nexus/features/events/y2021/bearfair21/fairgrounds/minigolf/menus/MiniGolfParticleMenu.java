package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfParticle;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21User;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MiniGolfParticleMenu extends MenuUtils implements InventoryProvider {
	MiniGolf21UserService service = new MiniGolf21UserService();
	MiniGolf21User user;

	public static SmartInventory getInv() {
		return SmartInventory.builder()
				.provider(new MiniGolfParticleMenu())
				.size(getRows(MiniGolfParticle.values().length), 9)
				.title(ChatColor.DARK_AQUA + "Select a particle:")
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
		for (MiniGolfParticle miniGolfParticle : MiniGolfParticle.values()) {
			ItemStack item = new ItemBuilder(miniGolfParticle.getDisplay()).name(StringUtils.camelCase(miniGolfParticle)).build();
			clickableItems.add(ClickableItem.from(item, e -> setParticle(user, miniGolfParticle)));
		}

		paginator(player, contents, clickableItems);
	}

	private void setParticle(MiniGolf21User user, MiniGolfParticle particle) {
		user.setMiniGolfParticle(particle);
		service.save(user);

		MiniGolfUtils.send(user, "Set particle to: &e" + StringUtils.camelCase(user.getMiniGolfParticle()));

		user.getOnlinePlayer().closeInventory();
	}
}
