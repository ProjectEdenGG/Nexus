package me.pugabyte.nexus.features.events.y2021.bearfair21;

import eden.utils.TimeUtils.Time;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.PugmasIsland;
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
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import java.util.Collections;
import java.util.List;

import static me.pugabyte.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.isAtBearFair;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.send;

public class Quests implements Listener {
	BearFair21UserService userService = new BearFair21UserService();

	@Getter
	private static final ItemStack crateKey = new ItemBuilder(Material.TRIPWIRE_HOOK).amount(1).name("Bear Fair Crate Key").build();

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
		PugmasIsland.startup();
	}

	public static void shutdown() {
		RegenCrops.shutdown();
		ClientsideContentManager.shutdown();
	}

	public static ItemStack getBackPack(Player player) {
		return Backpacks.getBackpack(null, player);
	}

	public static void giveItem(BearFair21User user, ItemStack itemStack) {
		giveItem(user.getOnlinePlayer(), itemStack);
	}

	public static void giveItem(Player player, ItemStack itemStack) {
		PlayerUtils.giveItem(player, itemStack);
		sound_obtainItem(player);
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

	public static List<ItemStack> getItemsListFrom(BearFair21User user, List<ItemBuilder> items) {
		List<ItemStack> result = new ArrayList<>();
		for (ItemBuilder item : items) {
			if (ItemUtils.isNullOrAir(item.build()))
				continue;

			ItemStack itemLike = getItemLikeFrom(user, item);
			if (!ItemUtils.isNullOrAir(itemLike))
				result.add(itemLike);
		}

		return result;
	}

	public static boolean hasAllItemsLikeFrom(BearFair21User user, List<ItemBuilder> items) {
		return getItemsListFrom(user, items).size() >= items.size();
	}

	public static boolean hasItemsLikeFrom(BearFair21User user, List<ItemBuilder> items) {
		return getItemsListFrom(user, items).size() > 0;
	}

	public static ItemStack getItemLikeFrom(BearFair21User user, ItemBuilder itemBuilder) {
		ItemStack _item = itemBuilder.build();
		for (ItemStack item : user.getOnlinePlayer().getInventory()) {
			if (ItemUtils.isNullOrAir(item))
				continue;

			if (ItemUtils.isFuzzyMatch(_item, item) && item.getAmount() >= _item.getAmount())
				return _item;
		}
		return null;
	}

	public static void removeItems(BearFair21User user, List<ItemBuilder> items) {
		removeItems(user.getPlayer(), items);
	}

	public static void removeItems(Player player, List<ItemBuilder> items) {
		List<ItemStack> result = new ArrayList<>();
		items.forEach(itemBuilder -> result.add(itemBuilder.build()));
		removeItemStacks(player, result);
	}

	public static void removeItem(BearFair21User user, ItemStack item) {
		removeItemStacks(user.getPlayer(), Collections.singletonList(item));
	}

	private static void removeItemStacks(Player player, List<ItemStack> items) {
		for (ItemStack item : items) {
			if (ItemUtils.isNullOrAir(item))
				continue;

			player.getInventory().removeItemAnySlot(item);
		}
	}

	public static void sound_obtainItem(Player player) {
		SoundUtils.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 2F);
	}

	public static void sound_completeQuest(Player player) {
		SoundUtils.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5F, 1F);
	}

	public static void sound_villagerNo(Player player) {
		SoundUtils.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
	}

	public static void sound_npcAlert(Player player) {
		SoundUtils.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.5F, 1F);
	}

	public static void poof(Location location) {
		location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 1F);
		location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 500, 0.5, 1, 0.5, 0);
		location.getWorld().spawnParticle(Particle.FLASH, location, 10, 0, 0, 0);
	}

	public static void giveKey(BearFair21User user) {
		Quests.sound_completeQuest(user.getPlayer());
		giveItem(user, crateKey.clone());
		user.sendMessage("TODO BF21: deliver actual key");
	}

	@EventHandler
	public void onRightClickNPC(NPCRightClickEvent event) {
		if (!BearFair21.getConfig().isEnableQuests())
			return;

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
		BearFair21Talker.runScript(user, id).thenAccept(bool -> {
			if (bool)
				Merchants.openMerchant(player, id);
		});

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
