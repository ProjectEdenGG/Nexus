package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MainIsland;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MinigameNightIsland;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.fishing.Fishing;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Merchants;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.CitizensUtils;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.RandomUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.isAtBearFair;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.isBFItem;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.send;

public class BFQuests implements Listener {
	public static String itemLore = "BearFair20 Item";
	// Data
	private static List<Location> collectorLocs = new ArrayList<>();
	private static List<Location> prevCollectorLocs = new ArrayList<>();
	// Error Messages
	private static String prefix = "&8&l[&eBFQuests&8&l] ";
	public static String cantBreakError = prefix + "&c&lHey! &7That's not a block you can break";
	public static String notFullyGrownError = prefix + "&c&lHey! &7That's not fully grown";
	public static String bottomBlockError = prefix + "&c&lHey! &7You can't break the bottom block";
	public static String decorOnlyError = prefix + "&c&lHey! &7This block is just decoration";
	public static String craftItemError = prefix + "&c&lHey! &7You can only craft that item with BearFair20 items!";
	public static String toolError = prefix + "&c&lHey! &7You may only use BearFair20 tools!";
	public static String miningError = prefix + "&c&lHey! &7You can't mine here!";

	public BFQuests() {
		BNCore.registerListener(this);
		setupCollector();
		new RegenCrops();
		new Beehive();
		new Quarry();
		new Fishing();
		new EasterEggs();
		new SellCrates();
		Recipes.loadRecipes();
	}

	public static void shutdown() {
		Quarry.shutdown();
		RegenCrops.shutdown();
	}

	public static void chime(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
	}

	private void setupCollector() {
		World world = BearFair20.getWorld();
		Location observatory = new Location(world, -1097, 157, -1550);
		Location town = new Location(world, -1095, 139, -1666);
		Location forest = new Location(world, -1031, 140, -1556);
		Location flag = new Location(world, -984, 144, -1615);
		Location campsite = new Location(world, -1020, 153, -1760);
		Location ruins = new Location(world, -919, 137, -1711);
		Location carnival = new Location(world, -888, 136, -1659);
		collectorLocs = Arrays.asList(observatory, town, forest, flag, campsite, ruins, carnival);
	}

	public static void moveCollector() {
		if (prevCollectorLocs.size() == collectorLocs.size())
			prevCollectorLocs.clear();

		Location newLoc = RandomUtils.randomElement(collectorLocs);
		if (newLoc == null) return;

		for (int i = 0; i < 10; i++) {
			if (!prevCollectorLocs.contains(newLoc)) {
				prevCollectorLocs.add(newLoc);
				break;
			}
			newLoc = RandomUtils.randomElement(collectorLocs);
		}

		if (newLoc == null) return;
		newLoc = Utils.getCenteredLocation(newLoc);
		Location finalNewLoc = newLoc;

		NPC npc = CitizensUtils.getNPC(2750);
		Location oldLoc = npc.getEntity().getLocation();
		World world = BearFair20.getWorld();

		world.playSound(oldLoc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 1F);
		world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, oldLoc, 500, 0.5, 1, 0.5, 0);
		world.spawnParticle(Particle.FLASH, oldLoc, 10, 0, 0, 0);
		npc.despawn();

		world.playSound(finalNewLoc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 1F);
		world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, finalNewLoc, 500, 0.5, 1, 0.5, 0);
		world.spawnParticle(Particle.FLASH, finalNewLoc, 10, 0, 0, 0);
		npc.spawn(finalNewLoc);
	}

	// Add Lore to items
	@EventHandler
	public void onBlockDropItemEvent(BlockDropItemEvent event) {
		Location loc = event.getBlock().getLocation();
		if (!isAtBearFair(loc)) return;
		event.getItems().forEach(item -> {
			Material type = item.getItemStack().getType();
			if (type.equals(Material.WHEAT_SEEDS) || type.equals(Material.BEETROOT_SEEDS)) {
				item.remove();
				return;
			}

			if (type.equals(Material.DIORITE_SLAB) || type.equals(Material.DIORITE_STAIRS) || type.equals(Material.DIORITE_WALL) || type.equals(Material.DIORITE)) {
				item.setItemStack(MainIsland.unpurifiedMarble.clone());
			}

			item.getItemStack().setLore(Collections.singletonList(itemLore));
		});
	}

	@EventHandler
	public void onEntityDropItem(EntityDropItemEvent event) {
		Location loc = event.getEntity().getLocation();
		if (!isAtBearFair(loc)) return;
		event.getItemDrop().getItemStack().setLore(Collections.singletonList(itemLore));
	}

	@EventHandler
	public void onMilkCow(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (!isAtBearFair(player)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getRightClicked().getType().equals(EntityType.COW)) return;

		event.setCancelled(true);

		ItemStack tool = Utils.getTool(player);
		if (tool != null && tool.getType().equals(Material.BUCKET)) {
			tool.setAmount(tool.getAmount() - 1);
			ItemStack milkBucket = new ItemBuilder(Material.MILK_BUCKET).lore(itemLore).amount(1).build();
			Utils.giveItem(player, milkBucket);
		}

	}

	@EventHandler
	public void onHoneyBottleFill(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!isAtBearFair(player)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block clicked = event.getClickedBlock();
		if (Utils.isNullOrAir(clicked)) return;

		ProtectedRegion beehiveRg = WGUtils.getProtectedRegion(Beehive.beehiveRg);
		if (WGUtils.getRegionsAt(clicked.getLocation()).contains(beehiveRg)) {
			event.setCancelled(true);
			return;
		}

		if (!clicked.getType().equals(Material.BEE_NEST) && !clicked.getType().equals(Material.BEEHIVE)) return;
		if (event.getItem() == null || !event.getItem().getType().equals(Material.GLASS_BOTTLE)) return;

		event.setCancelled(true);
		event.getItem().setAmount(event.getItem().getAmount() - 1);
		ItemStack honeyBottle = new ItemBuilder(Material.HONEY_BOTTLE).lore(itemLore).amount(1).build();
		Utils.giveItem(player, honeyBottle);
	}

	@EventHandler
	public void onBeeHiveShear(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!isAtBearFair(player)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block clicked = event.getClickedBlock();
		if (Utils.isNullOrAir(clicked)) return;

		ProtectedRegion beehiveRg = WGUtils.getProtectedRegion(Beehive.beehiveRg);
		if (WGUtils.getRegionsAt(clicked.getLocation()).contains(beehiveRg)) {
			event.setCancelled(true);
			return;
		}

		if (!clicked.getType().equals(Material.BEE_NEST) && !clicked.getType().equals(Material.BEEHIVE)) return;
		if (event.getItem() == null || !event.getItem().getType().equals(Material.SHEARS)) return;
		if (!isBFItem(event.getItem())) return;

		event.setCancelled(true);
		ItemMeta meta = event.getItem().getItemMeta();
		Damageable damageable = (Damageable) meta;
		((Damageable) meta).setDamage(damageable.getDamage() + 40);
		if (((Damageable) meta).getDamage() >= 238)
			event.getItem().setAmount(0);
		else
			event.getItem().setItemMeta(meta);
		ItemStack honeyComb = new ItemBuilder(Material.HONEYCOMB).lore(itemLore).amount(3).build();
		Utils.giveItem(player, honeyComb);
	}

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		if (!(event.getView().getPlayer() instanceof Player)) return;
		Player player = (Player) event.getView().getPlayer();
		Location loc = player.getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(BearFair20.getProtectedRegion())) return;

		ItemStack result = event.getInventory().getResult();
		ItemStack[] ingredients = event.getInventory().getMatrix();

		boolean questCrafting = true;
		for (ItemStack ingredient : ingredients) {
			if (Utils.isNullOrAir(ingredient))
				continue;
			if (!isBFItem(ingredient))
				questCrafting = false;

		}

		if (!questCrafting) {
			if (isBFItem(result)) {
				event.getInventory().setResult(new ItemStack(Material.AIR));
				send(craftItemError, player);
			}
		}
	}

	@EventHandler
	public void onPrepareCraftItem(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player)) return;
		Player player = (Player) event.getView().getPlayer();
		Location loc = player.getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(BearFair20.getProtectedRegion())) return;

		ItemStack[] ingredients = event.getInventory().getMatrix();
		ItemStack result = event.getInventory().getResult();

		// Each item must be a BF20 item, for it to result in a BF20 item
		boolean questCrafting = true;
		for (ItemStack ingredient : ingredients) {
			if (Utils.isNullOrAir(ingredient))
				continue;
			if (!isBFItem(ingredient))
				questCrafting = false;
		}

		if (!questCrafting)
			return;

		if (result == null) {
			result = Recipes.getRecipe(Arrays.asList(ingredients));
			if (result == null)
				return;
		}

		ItemMeta resultMeta = result.getItemMeta();
		resultMeta.setLore(Collections.singletonList(itemLore));
		result.setItemMeta(resultMeta);
		event.getInventory().setResult(result);
	}

	// NPC Stuff
	@EventHandler
	public void onRightClickNPC(NPCRightClickEvent event) {
		Player player = event.getClicker();
		if (isAtBearFair(player)) {
			CooldownService cooldownService = new CooldownService();
			if (!cooldownService.check(player, "BF_NPCInteract", Time.SECOND.x(2)))
				return;

			int id = event.getNPC().getId();
			Talkers.startScript(player, id);
			Merchants.openMerchant(player, id);
		}
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event) {
		if (!event.getInventory().getType().equals(InventoryType.MERCHANT)) return;
		if (!(event.getPlayer() instanceof Player)) return;

		Player player = (Player) event.getPlayer();
		ProtectedRegion region = WGUtils.getProtectedRegion(BearFair20.getRegion());
		if (!WGUtils.getRegionsAt(player.getLocation()).contains(region)) return;

		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		if (player.getInventory().contains(MinigameNightIsland.joystick.clone().build())) {
			MinigameNightIsland.foundPiece(player, MinigameNightIsland.joystick.clone().build());
			service.save(user);
		} else if (player.getInventory().contains(MainIsland.honeyStroopWafel)) {
			MainIsland.setStep(player, 3);
		}
	}

	// Preventers
	@EventHandler
	public void onInteractWithVillager(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (!(entity instanceof Villager)) return;
		if (CitizensAPI.getNPCRegistry().isNPC(entity)) return;

		Player player = event.getPlayer();
		Location loc = player.getLocation();

		if (isAtBearFair(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMcMMOXpGainEvent(McMMOPlayerXpGainEvent event) {
		Location loc = event.getPlayer().getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(BearFair20.getProtectedRegion())) return;
		event.setRawXpGained(0F);
		event.setCancelled(true);
	}
	//

	//TODO:
	// give bf items:
	//		- Bucket -> Milk Bucket

}
