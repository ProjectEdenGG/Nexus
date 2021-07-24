package gg.projecteden.nexus.features.quests.itemtags;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.quests.itemtags.ItemTagsUtils.updateItem;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

public class ItemTagListener implements Listener {

	public ItemTagListener() {
		Nexus.registerListener(this);
	}

	public static boolean isNotTesting(Player player) {
		return !Dev.WAKKA.is(player);
	}

	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player)) return;
		if (isNotTesting(player)) return;

		ItemStack result = event.getItem();
		if (isNullOrAir(result)) return;

		ItemStack updated = updateItem(result);

		result.setItemMeta(updated.getItemMeta());
	}

	@EventHandler
	public void onCraftItem(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player)) return;
		if (isNotTesting(player)) return;

		ItemStack result = event.getInventory().getResult();
		if (isNullOrAir(result)) return;
		if (!ItemTagsUtils.isArmor(result) && !ItemTagsUtils.isTool(result)) return;

		ItemStack updated = updateItem(result);

		event.getInventory().setResult(updated);
	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		if (isNotTesting(event.getPlayer())) return;
		ItemStack result = event.getItem();
		if (isNullOrAir(result)) return;

		ItemStack updated = updateItem(result);

		event.getItem().setItemMeta(updated.getItemMeta());
	}

	// Includes Anvil, Grindstone, and Smithing Table
	@EventHandler
	public void onPrepareItem(PrepareResultEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player)) return;
		if (isNotTesting(player)) return;

		ItemStack result = event.getResult();
		if (isNullOrAir(result)) return;

		ItemStack updated = updateItem(result);

		event.setResult(updated);
	}

	// TODO ItemTags: Uncomment
//	@EventHandler
//	public void onBreakItemFrame(EntityDamageByEntityEvent event){
//
//		if(!event.getEntityType().equals(EntityType.ITEM_FRAME))
//			return;
//
//		ItemFrame itemFrame = (ItemFrame) event.getEntity();
//		ItemStack item = itemFrame.getItem();
//		if(isNullOrAir(item)) return;
//
//		ItemStack updated = updateItem(item);
//
//		itemFrame.setItem(updated);
//	}

	// TODO ItemTags: Uncomment
//	@EventHandler
//	public void onItemFrameBreak(HangingBreakEvent event){
//		if (!(event.getEntity().getType().equals(EntityType.ITEM_FRAME)))
//			return;
//
//		if (event.isCancelled())
//			return;
//
//		ItemFrame itemFrame = (ItemFrame) event.getEntity();
//		ItemStack item = itemFrame.getItem();
//		if (isNullOrAir(item)) return;
//
//		ItemStack updated = updateItem(item);
//
//		itemFrame.setItem(updated);
//	}

	// TODO ItemTags: Uncomment
//	@EventHandler
//	public void onEntityDeath(EntityDeathEvent event) {
//		int ndx = 0;
//		for (ItemStack drop : new ArrayList<>(event.getDrops())) {
//			ItemStack updated = updateItem(drop);
//			event.getDrops().set(ndx, updated);
//			++ndx;
//		}
//	}

	// TODO: uncomment if mythic mobs is added
//	@EventHandler
//	public void onMythicMMobEntityDeath(MythicMobDeathEvent event) {
//		int ndx = 0;
//		for (ItemStack drop : new ArrayList<>(event.getDrops())) {
//			ItemStack updated = updateItem(drop);
//			event.getDrops().set(ndx, updated);
//			++ndx;
//		}
//	}

	@EventHandler
	public void onFishingLoot(PlayerFishEvent event) {
		if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
			return;

		if (isNotTesting(event.getPlayer())) return;

		Entity caught = event.getCaught();
		if (!(caught instanceof Item item)) return;

		ItemStack itemStack = item.getItemStack();
		updateItem(itemStack);
	}

	@EventHandler
	public void onMcMMORepair(McMMOPlayerRepairCheckEvent event) {
		if (isNotTesting(event.getPlayer())) return;

		ItemStack repaired = event.getRepairedObject().clone();
		updateItem(repaired);
		event.getRepairedObject().setItemMeta(repaired.getItemMeta());
	}

}
