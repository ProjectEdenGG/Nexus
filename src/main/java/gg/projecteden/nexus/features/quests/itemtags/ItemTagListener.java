package gg.projecteden.nexus.features.quests.itemtags;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.WorldGroup;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static gg.projecteden.nexus.features.quests.itemtags.ItemTagsUtils.updateItem;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

public class ItemTagListener implements Listener {

	public ItemTagListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			return;

		ItemStack result = event.getItem();
		if (isNullOrAir(result)) return;

		ItemStack updated = updateItem(result);

		result.setItemMeta(updated.getItemMeta());
	}

	@EventHandler
	public void onCraftItem(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			return;

		ItemStack result = event.getInventory().getResult();
		if (isNullOrAir(result)) return;
		if (!ItemTagsUtils.isArmor(result) && !ItemTagsUtils.isTool(result)) return;

		ItemStack updated = updateItem(result);

		event.getInventory().setResult(updated);
	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		ItemStack result = event.getItem();
		if (isNullOrAir(result))
			return;

		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		ItemStack updated = updateItem(result);

		event.getItem().setItemMeta(updated.getItemMeta());
	}

	// Includes Anvil, Grindstone, and Smithing Table
	@EventHandler
	public void onPrepareItem(PrepareResultEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			return;

		ItemStack result = event.getResult();
		if (isNullOrAir(result)) return;

		ItemStack updated = updateItem(result);

		event.setResult(updated);
	}

	@EventHandler
	public void onBreakItemFrame(EntityDamageByEntityEvent event) {
		if (!event.getEntityType().equals(EntityType.ITEM_FRAME))
			return;

		if (WorldGroup.of(event.getEntity()) != WorldGroup.SURVIVAL)
			return;

		ItemFrame itemFrame = (ItemFrame) event.getEntity();
		ItemStack item = itemFrame.getItem();
		if (isNullOrAir(item)) return;

		ItemStack updated = updateItem(item);

		itemFrame.setItem(updated);
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		Hanging entity = event.getEntity();
		if (!(entity.getType().equals(EntityType.ITEM_FRAME)))
			return;

		if (WorldGroup.of(entity) != WorldGroup.SURVIVAL)
			return;

		if (event.isCancelled())
			return;

		ItemFrame itemFrame = (ItemFrame) entity;
		ItemStack item = itemFrame.getItem();
		if (isNullOrAir(item)) return;

		ItemStack updated = updateItem(item);

		itemFrame.setItem(updated);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (WorldGroup.of(event.getEntity()) != WorldGroup.SURVIVAL)
			return;

		int ndx = 0;
		for (ItemStack drop : new ArrayList<>(event.getDrops())) {
			ItemStack updated = updateItem(drop);
			event.getDrops().set(ndx, updated);
			++ndx;
		}
	}

	@EventHandler
	public void onFishingLoot(PlayerFishEvent event) {
		if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
			return;

		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		Entity caught = event.getCaught();
		if (!(caught instanceof Item item)) return;

		ItemStack itemStack = item.getItemStack();
		updateItem(itemStack);
	}

	@EventHandler
	public void onMcMMORepair(McMMOPlayerRepairCheckEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		ItemStack repaired = event.getRepairedObject().clone();
		updateItem(repaired);
		event.getRepairedObject().setItemMeta(repaired.getItemMeta());
	}

	// TODO: uncomment if mythic mobs is added
//	@EventHandler
//	public void onMythicMMobEntityDeath(MythicMobDeathEvent event) {
//		if(WorldGroup.of(event.getEntity()) != WorldGroup.SURVIVAL) return;
//		int ndx = 0;
//		for (ItemStack drop : new ArrayList<>(event.getDrops())) {
//			ItemStack updated = updateItem(drop);
//			event.getDrops().set(ndx, updated);
//			++ndx;
//		}
//	}

	@EventHandler
	public void onGenerateLoot(LootGenerateEvent event) {
		for (ItemStack itemStack : event.getLoot())
			ItemTagsUtils.updateItem(itemStack);
	}

}
