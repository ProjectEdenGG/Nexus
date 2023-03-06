package gg.projecteden.nexus.features.survival.decorationstore;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.SurvivalNPCShopMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.survival.avontyre.AvontyreNPCs;
import gg.projecteden.nexus.features.survival.decorationstore.models.BuyableData;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.FontUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
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
		if (!event.getRegion().getId().equals(DecorationStoreUtils.getSchematicStoreRegion())) return;

		if (PlayerUtils.isWGEdit(player))
			PlayerUtils.runCommand(player, "wgedit off");
	}

	@EventHandler
	public void on(HangingBreakByEntityEvent event) {
		if (!(event.getEntity() instanceof Painting)) return;
		if (!(event.getRemover() instanceof Player player)) return;
		if (!DecorationStoreUtils.isInStore(player)) return;

		if (prompt(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!(event.getDamager() instanceof Player player)) return;
		if (!DecorationStoreUtils.isInStore(player)) return;

		if (prompt(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!DecorationStoreUtils.isInStore(player)) return;
		if (!event.getAction().isLeftClick()) return;

		if (prompt(player))
			event.setCancelled(true);
	}

	private boolean prompt(Player player) {
		BuyableData data = DecorationStore.getTargetBuyable(player);
		if (data == null) return false;

		Pair<String, Integer> namePrice = data.getNameAndPrice();
		if (namePrice == null || namePrice.getSecond() == null)
			return false;

		// TODO: REMOVE
		if (!Rank.of(player).isStaff())
			return false;
		//

		String itemName = namePrice.getFirst();
		Integer itemPrice = namePrice.getSecond();

		ConfirmationMenu.builder()
			.title(FontUtils.getMenuTexture("埤", 3) + "&3Buy this decoration for &a$" + itemPrice + "&3?")
			.additionalContents(contents -> contents.set(0, 4, ClickableItem.empty(data.getItem())))
			.cancelText("&cCancel")
			.confirmText("&aBuy")
			.onConfirm(e -> PlayerUtils.send(e.getPlayer(), "TODO: Bought " + itemName + " for " + itemPrice))
			.open(player);

		return true;
	}

	@EventHandler
	public void on(NPCRightClickEvent event) {
		if (!AvontyreNPCs.DECORATION__NULL.is(event.getNPC()))
			return;

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
