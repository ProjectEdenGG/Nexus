package gg.projecteden.nexus.features.events.y2024.vulan24.decorations;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.Decorations;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import gg.projecteden.nexus.features.survival.decorationstore.models.BuyableData;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class VuLanDecorStoreListener implements Listener {

	public VuLanDecorStoreListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();

		String regionId = event.getRegion().getId();
		if (!regionId.equals(VuLanDecorStore.getStoreRegionFlorist()) && !regionId.equals(VuLanDecorStore.getStoreRegionMarket()))
			return;

		if (PlayerUtils.isWGEdit(player))
			PlayerUtils.runCommand(player, "wgedit off");
	}

	@EventHandler
	public void onStorePrompt(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!(event.getDamager() instanceof Player player)) return;
		if (!VuLanDecorStore.isInAStore(player)) return;

		if (Decorations.isServerReloading())
			return;

		if (prompt(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onStorePrompt(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!VuLanDecorStore.isInAStore(player)) return;
		if (!event.getAction().isLeftClick()) return;

		if (Decorations.isServerReloading())
			return;

		if (prompt(player))
			event.setCancelled(true);
	}

	private boolean prompt(Player player) {
		PlayerUtils.send(player, "TODO: ASK FOR WORLDGROUP");

		BuyableData data = VuLanDecorStore.getTargetBuyable(player);
		if (data == null) return false;

		Pair<String, Double> namePricePair = data.getNameAndPrice();
		if (namePricePair == null || namePricePair.getSecond() == null)
			return false;

		Double itemPrice = namePricePair.getSecond();

		if (itemPrice == null)
			return false;

		BankerService bankerService = new BankerService();
		ShopGroup shopGroup = ShopGroup.SURVIVAL; // TODO: CHOICE
		WorldGroup worldGroup = WorldGroup.SURVIVAL; // TODO: CHOICE

		if (!bankerService.has(player, itemPrice, shopGroup)) {
			DecorationError.LACKING_FUNDS.send(player);
			return false;
		}

		ConfirmationMenu.builder()
				.title(CustomTexture.GUI_CONFIRMATION_SLOT.getMenuTexture() + "&3Buy for &a" + StringUtils.prettyMoney(itemPrice) + "&3?")
				.displayItem(data.getItem(player))
				.cancelText("&cCancel")
				.confirmText("&aBuy")
				.onConfirm(e -> Catalog.tryBuyEventItem(player, data.getItem(player), TransactionCause.DECORATION_STORE, worldGroup, shopGroup))
				.open(player);

		return true;
	}
}
