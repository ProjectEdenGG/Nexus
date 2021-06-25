package me.pugabyte.nexus.features.events.y2021.bearfair21;

import com.destroystokyo.paper.ParticleBuilder;
import eden.utils.RandomUtils;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.PugmasIsland;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21Talker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.Beehive;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.Errors;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.RadioHeads;
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
import me.pugabyte.nexus.features.regionapi.events.common.EnteringRegionEvent;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundBuilder;
import me.pugabyte.nexus.utils.Tasks;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.pugabyte.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.isNotAtBearFair;
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
		new RadioHeads();
		new Beehive();
		//
		nextStepNPCTask();
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

	private void nextStepNPCTask() {
		List<Integer> excludeNPCs = Arrays.asList(BearFair21NPC.GRINCH_1.getId(),
			BearFair21NPC.MGN_CUSTOMER_2.getId(), BearFair21NPC.QUEEN_BEE.getId());

		Tasks.repeat(0, Time.SECOND.x(2), () -> {
			Set<Player> players = BearFair21.getPlayers();
			for (Player player : players) {
				BearFair21User user = userService.get(player);
				Set<Integer> npcs = new HashSet<>();

				// add all next step npcs
				npcs.addAll(user.getNextStepNPCs());

				// add npcs that the player has not met, excluding specific ones
				npcs.addAll(Arrays.stream(BearFair21NPC.values())
					.map(BearFair21NPC::getId)
					.filter(id -> !user.hasMet(id))
					.filter(id -> !excludeNPCs.contains(id))
					.toList());

				for (Integer npcId : npcs) {
					NPC npc = CitizensUtils.getNPC(npcId);
					if (!npc.isSpawned())
						continue;

					Location loc = npc.getEntity().getLocation().add(0, 1, 0);
					new ParticleBuilder(Particle.VILLAGER_HAPPY)
						.location(loc)
						.count(10)
						.receivers(player)
						.offset(.25, .5, .25)
						.spawn();

				}
			}
		});
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
		if (BearFair21.isNotAtBearFair(event))
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

	public static void removeItemStacks(BearFair21User user, List<ItemStack> items) {
		for (ItemStack item : items) {
			removeItem(user, item);
		}
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
		new SoundBuilder(Sound.ENTITY_PLAYER_LEVELUP).reciever(player).volume(0.5).pitch(2.0).play();
	}

	public static void sound_completeQuest(Player player) {
		new SoundBuilder(Sound.UI_TOAST_CHALLENGE_COMPLETE).reciever(player).volume(0.5).play();
	}

	public static void sound_villagerNo(Player player) {
		new SoundBuilder(Sound.ENTITY_VILLAGER_NO).reciever(player).volume(0.5).play();
	}

	public static void sound_npcAlert(Player player) {
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BIT).reciever(player).volume(0.5).play();
	}

	public static void poof(Location location) {
		new SoundBuilder(Sound.ENTITY_FIREWORK_ROCKET_BLAST).location(location).play();
		location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 500, 0.5, 1, 0.5, 0);
		location.getWorld().spawnParticle(Particle.FLASH, location, 10, 0, 0, 0);
	}

	public static void giveKey(BearFair21User user) {
		CrateType.BEAR_FAIR_21.give(user.getOnlinePlayer(), 1);
		Quests.sound_completeQuest(user.getPlayer());
	}

	@EventHandler
	public void onRegionEnter(EnteringRegionEvent event) {
		Entity entity = event.getEntity();
		if (isNotAtBearFair(entity))
			return;

		String id = event.getRegion().getId();
		if (id.equalsIgnoreCase(Beehive.getExitRg())) {
			if (entity instanceof Bee bee) {
				List<Location> beeLocs = Arrays.asList(
					new Location(BearFair21.getWorld(), -32, 96, -72).toCenterLocation(),
					new Location(BearFair21.getWorld(), -33, 107, -34).toCenterLocation(),
					new Location(BearFair21.getWorld(), -60, 114, -7).toCenterLocation()
				);

				bee.teleport(RandomUtils.randomElement(beeLocs));
			}
		}
	}

	@EventHandler
	public void onRightClickNPC(NPCRightClickEvent event) {
		if (!BearFair21.getConfig().isEnableQuests())
			return;

		Player player = event.getClicker();
		if (BearFair21.isNotAtBearFair(player))
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
		if (BearFair21.isNotAtBearFair(block)) return;
		if (canWorldGuardEdit(player)) return;

		event.setCancelled(true);

		if (Mining.breakBlock(event)) return;
		if (WoodCutting.breakBlock(event)) return;
		if (Farming.breakBlock(event)) return;

		if (new CooldownService().check(player, "BF21_cantbreak", Time.MINUTE)) {
			send(Errors.cantBreak, player);
			sound_villagerNo(player);
		}
	}
}
