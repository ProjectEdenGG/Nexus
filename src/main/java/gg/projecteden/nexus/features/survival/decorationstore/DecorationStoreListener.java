package gg.projecteden.nexus.features.survival.decorationstore;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.SurvivalNPCShopMenu;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.survival.avontyre.AvontyreNPCs;
import gg.projecteden.nexus.features.survival.decorationstore.models.BuyableData;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.FontUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class DecorationStoreListener implements Listener {

	public DecorationStoreListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!event.getRegion().getId().equals(DecorationStore.getStoreRegionSchematic())) return;

		if (PlayerUtils.isWGEdit(player))
			PlayerUtils.runCommand(player, "wgedit off");
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!(event.getDamager() instanceof Player player)) return;
		if (!DecorationStore.isInStore(player)) return;

		if (prompt(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!DecorationStore.isInStore(player)) return;
		if (!event.getAction().isLeftClick()) return;

		if (prompt(player))
			event.setCancelled(true);
	}

	private boolean prompt(Player player) {
		// TODO DECORATION: REMOVE ON RELEASE
		if (!DecorationUtils.canUseFeature(player))
			return false;
		//

		BuyableData data = DecorationStore.getTargetBuyable(player);
		if (data == null) return false;

		Pair<String, Double> namePricePair = data.getNameAndPrice();
		if (namePricePair == null || namePricePair.getSecond() == null)
			return false;

		Double itemPrice = namePricePair.getSecond();

		if (itemPrice == null)
			return false;

		BankerService bankerService = new BankerService();
		ShopGroup shopGroup = ShopGroup.SURVIVAL;
		WorldGroup worldGroup = WorldGroup.SURVIVAL;

		if (!bankerService.has(player, itemPrice, shopGroup)) {
			PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cYou don't have enough money to buy this.");
			return false;
		}

		ConfirmationMenu.builder()
			.title(FontUtils.getMenuTexture("埤", 3) + "&3Buy for &a" + StringUtils.prettyMoney(itemPrice) + "&3?")
			.displayItem(data.getItem(player))
			.cancelText("&cCancel")
			.confirmText("&aBuy")
			.onConfirm(e -> Catalog.buyItem(player, data.getItem(player), TransactionCause.DECORATION_STORE))
			.open(player);

		return true;
	}

	@EventHandler
	public void on(NPCRightClickEvent event) {
		if (!AvontyreNPCs.DECORATION__NULL.is(event.getNPC()))
			return;

		if (DecorationStoreLayouts.isAnimating()) {
			PlayerUtils.send(event.getClicker(), DecorationStore.PREFIX + "The store is currently being remodelled, come back shortly!");
			return;
		}

		// TODO: CATALOGS + PAINTBRUSH
		SurvivalNPCShopMenu.builder()
			.npcId(AvontyreNPCs.DECORATION__NULL.getNPCId())
			.title("Decoration Shop")
			.products(Map.of(
				new ItemStack(Material.DIRT), 1d,
				new ItemStack(Material.STONE), 2d
			))
			.open(event.getClicker());
	}
}
