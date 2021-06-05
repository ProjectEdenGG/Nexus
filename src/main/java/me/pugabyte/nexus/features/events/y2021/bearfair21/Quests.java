package me.pugabyte.nexus.features.events.y2021.bearfair21;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21Talker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.Errors;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.Recycler;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.SellCrates;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.clientside.ClientsideContentManager;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.Collector;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.Merchants;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.Mining;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.WoodCutting;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.farming.Farming;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.farming.RegenCrops;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.fishing.Fishing;
import me.pugabyte.nexus.features.recipes.functionals.Backpacks;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.isAtBearFair;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.send;

public class Quests implements Listener {
	BearFair21UserService userService = new BearFair21UserService();

	public Quests() {
		Nexus.registerListener(this);
		//
		new Fishing();
		new Farming();
		new WoodCutting();
		new Mining();
		//
		new SellCrates();
		new Recycler();
		new ClientsideContentManager();
	}

	public static void startup() {
		Collector.startup();
		ClientsideContentManager.startup();
	}

	public static void shutdown() {
		RegenCrops.shutdown();
		ClientsideContentManager.shutdown();
	}

	public static ItemStack getBackPack(Player player) {
		return Backpacks.getBackpack(null, player);
	}

	public static void chime(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
	}

	public static String[] getMenuBlockLines(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return null;

		Player player = event.getPlayer();
		if (!BearFair21.isAtBearFair(player))
			return null;

		Block block = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(block))
			return null;

		Material type = block.getType();
		Sign sign = null;
		if (MaterialTag.SIGNS.isTagged(type)) {
			sign = (Sign) block.getState();
		} else {
			for (Block relativeBlock : BlockUtils.getAdjacentBlocks(block))
				if (MaterialTag.SIGNS.isTagged(relativeBlock.getType()))
					sign = (Sign) relativeBlock.getState();
		}

		if (sign == null)
			return null;

		return sign.getLines();
	}

	public static boolean hasItemsLikeFrom(BearFair21User user, List<ItemBuilder> items) {
		List<ItemStack> result = new ArrayList<>();
		for (ItemBuilder item : items) {
			if (ItemUtils.isNullOrAir(item.build()))
				continue;

			ItemStack itemLike = getItemLikeFrom(user, item);
			if (!ItemUtils.isNullOrAir(itemLike))
				result.add(itemLike);
		}
		return result.size() > 0;
	}

	public static ItemStack getItemLikeFrom(BearFair21User user, ItemBuilder itemBuilder) {
		ItemStack _item = itemBuilder.build();
		for (ItemStack item : user.getOnlinePlayer().getInventory()) {
			if (ItemUtils.isNullOrAir(item))
				continue;

			if (ItemUtils.isFuzzyMatch(itemBuilder.build(), item) && item.getAmount() >= _item.getAmount())
				return item;
		}
		return null;
	}

	public static void removeItems(Player player, List<ItemBuilder> items) {
		for (ItemBuilder itemBuilder : items) {
			ItemStack item = itemBuilder.build();
			if (ItemUtils.isNullOrAir(item))
				continue;

			player.getInventory().removeItemAnySlot(item);
		}
	}

	@EventHandler
	public void onRightClickNPC(NPCRightClickEvent event) {
		Player player = event.getClicker();
		if (!isAtBearFair(player))
			return;

		CooldownService cooldownService = new CooldownService();
		if (!cooldownService.check(player, "BF21_NPCInteract", Time.SECOND.x(2)))
			return;

		int id = event.getNPC().getId();
		BearFair21NPC npc = BearFair21NPC.from(id);
		if (npc == null)
			return;

		BearFair21User user = userService.get(player);
		BearFair21Talker.runScript(user, id).thenRun(() -> Merchants.openMerchant(player, id));

		user.getMetNPCs().add(id);
		userService.save(user);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (event.isCancelled()) return;
		if (!isAtBearFair(block)) return;
		if (canWorldGuardEdit(player)) return;

		event.setCancelled(true);

		if (Mining.breakBlock(event)) return;
		if (WoodCutting.breakBlock(event)) return;
		if (Farming.breakBlock(event)) return;

		if (new CooldownService().check(player, "BF21_cantbreak", Time.MINUTE)) {
			send(Errors.cantBreak, player);
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
		}
	}
}
