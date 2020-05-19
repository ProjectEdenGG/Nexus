package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class BFQuests implements Listener {
	private ProtectedRegion mainRegion = WGUtils.getProtectedRegion(BearFair20.mainRg);
	private List<Material> breakList = Arrays.asList(Material.WHEAT, Material.POTATOES, Material.CARROTS,
			Material.BEETROOTS, Material.MELON, Material.PUMPKIN, Material.SUGAR_CANE, Material.COCOA);
	private List<Material> noAge = Collections.singletonList(Material.SUGAR_CANE);
	public static String itemLore = "BearFair20 Item";
	// Data
	private Map<Location, Material> multiRegenMap = new HashMap<>();
	private Map<Location, Material> blockRegenMap = new HashMap<>();
	private List<Location> cropRegenList = new ArrayList<>();
	// Error Messages
	private String prefix = "&8&l[&eBFQuests&8&l] ";
	private String cantBreak = prefix + "&c&lHey! &7That's not a block you can break";
	private String notFullyGrown = prefix + "&c&lHey! &7That's not fully grown";
	private String bottomBlock = prefix + "&c&lHey! &7You can't break the bottom block";
	private String decor = prefix + "&c&lHey! &7This block is just decoration";
	private String craftItem = prefix + "&c&lHey! &7You can only craft that item with BearFair20 items!";

	public BFQuests() {
		BNCore.registerListener(this);
		regenTasks();
		new Beehive();
		Recipes.loadRecipes();
	}

	private void regenTasks() {
		// CROPS
		Tasks.repeat(0, Time.SECOND.x(5), () -> {
			List<Location> locations = new ArrayList<>(cropRegenList);
			for (Location loc : locations) {
				Block block = loc.getBlock();
				BlockData blockData = block.getBlockData();

				if (!(blockData instanceof Ageable)) {
					cropRegenList.remove(loc);
					continue;
				}

				if (Utils.chanceOf(20)) {
					Ageable ageable = (Ageable) blockData;
					int age = ageable.getAge();
					if (age == ageable.getMaximumAge()) {
						cropRegenList.remove(loc);
						continue;
					}
					++age;
					ageable.setAge(age);
					block.setBlockData(ageable);

					if (age == ageable.getMaximumAge()) {
						cropRegenList.remove(loc);
					}
				}
			}
		});

		// BLOCKS
		Tasks.repeat(0, Time.SECOND.x(10), () -> {
			Set<Location> locations = new HashSet<>(blockRegenMap.keySet());
			for (Location loc : locations) {
				Block block = loc.getBlock();
				Material material = blockRegenMap.get(loc);
				if (block.getType().equals(material)) {
					blockRegenMap.remove(loc);
					continue;
				}

				if (Utils.chanceOf(20)) {
					block.setType(material);
					blockRegenMap.remove(loc);
				}
			}
		});

		// MULTIBLOCK
		Tasks.repeat(0, Time.SECOND.x(10), () -> {
			Set<Location> locations = new HashSet<>(multiRegenMap.keySet());
			for (Location loc : locations) {
				Block block = loc.getBlock();
				Material material = multiRegenMap.get(loc);
				if (material == null) {
					multiRegenMap.remove(loc);
					continue;
				}

				if (block.getType().equals(material)) {
					multiRegenMap.remove(loc);
					continue;
				}

				if (Utils.chanceOf(20)) {
					Block down = block.getRelative(0, -1, 0);
					if (down.getType().equals(material)) {
						block.setType(material);
						multiRegenMap.remove(loc);
					}
				}
			}
		});
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (event.isCancelled()) return;
		if (!event.getPlayer().getWorld().equals(BearFair20.world)) return;
		if (!WGUtils.getRegionsAt(block.getLocation()).contains(mainRegion)) return;
		if (!breakList.contains(block.getType())) {
			if (player.hasPermission("worldguard.region.bypass.*")) return;
			player.sendMessage(colorize(cantBreak));
			event.setCancelled(true);
			return;
		}

		if (player.hasPermission("worldguard.region.bypass.*")) {
			if (player.getInventory().getItemInMainHand().getType().equals(Material.NETHER_BRICK))
				return;
		}

		BlockData blockData = block.getState().getBlockData();
		Material material = block.getType();
		if (!(blockData instanceof Ageable) || noAge.contains(material)) {
			switch (material) {
				case MELON:
				case PUMPKIN:
					if (!(block.getRelative(0, -1, 0).getType().equals(Material.COARSE_DIRT))) {
						player.sendMessage(colorize(decor));
						event.setCancelled(true);
						return;
					}
					Tasks.wait(20, () -> blockRegenMap.put(block.getLocation(), material));
					break;
				case SUGAR_CANE:
					if (!(block.getRelative(0, -1, 0).getType().equals(material))) {
						player.sendMessage(colorize(bottomBlock));
						event.setCancelled(true);
						return;
					}
					multiRegenMap.put(block.getLocation(), material);

					Block up = block.getRelative(0, 1, 0);
					if (up.getType().equals(material)) {
						up.setType(Material.AIR, false);
						up.getWorld().dropItemNaturally(up.getLocation(), new ItemBuilder(material).lore(itemLore).build());
						multiRegenMap.put(up.getLocation(), material);
					}

					break;
				default:
					if (player.hasPermission("worldguard.region.bypass.*")) return;
					player.sendMessage(colorize(cantBreak));
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
					event.setCancelled(true);
			}
			return;
		}

		Ageable ageable = (Ageable) blockData;
		if (ageable.getAge() != ageable.getMaximumAge()) {
			player.sendMessage(colorize(notFullyGrown));
			event.setCancelled(true);
			return;
		}

		Tasks.wait(5, () -> {
			ageable.setAge(0);
			block.setType(material);
			block.setBlockData(ageable);
			Location loc = block.getLocation();
			cropRegenList.add(loc);
		});
	}

	@EventHandler
	public void onBlockDropItemEvent(BlockDropItemEvent event) {
		Location loc = event.getBlock().getLocation();
		if (!event.getBlock().getLocation().getWorld().equals(BearFair20.world)) return;
		if (!WGUtils.getRegionsAt(loc).contains(mainRegion)) return;
		event.getItems().forEach(item -> item.getItemStack().setLore(Collections.singletonList(itemLore)));
	}

	@EventHandler
	public void onMcMMOXpGainEvent(McMMOPlayerXpGainEvent event) {
		Location loc = event.getPlayer().getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(mainRegion)) return;
		event.setRawXpGained(0F);
		event.setCancelled(true);
	}

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		if (!(event.getView().getPlayer() instanceof Player)) return;
		Player player = (Player) event.getView().getPlayer();
		Location loc = player.getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(mainRegion)) return;

		ItemStack result = event.getInventory().getResult();
		ItemStack[] ingredients = event.getInventory().getMatrix();

		boolean questCrafting = true;
		for (ItemStack ingredient : ingredients) {
			if (Utils.isNullOrAir(ingredient))
				continue;
			ItemMeta meta = ingredient.getItemMeta();
			if (!meta.hasLore() || meta.getLore() == null) {
				questCrafting = false;
				continue;
			}

			if (!meta.getLore().contains(itemLore))
				questCrafting = false;
		}

		if (!questCrafting) {
			if (result != null && result.getItemMeta().getLore() != null && result.getItemMeta().getLore().contains(itemLore)) {
				event.getInventory().setResult(new ItemStack(Material.AIR));
				player.sendMessage(colorize(craftItem));
			}
		}
	}

	@EventHandler
	public void onPrepareCraftItem(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player)) return;
		Player player = (Player) event.getView().getPlayer();
		Location loc = player.getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(mainRegion)) return;

		ItemStack[] ingredients = event.getInventory().getMatrix();
		ItemStack result = event.getInventory().getResult();

		// Each item must be a BF20 item, for it to result in a BF20 item
		boolean questCrafting = true;
		for (ItemStack ingredient : ingredients) {
			if (Utils.isNullOrAir(ingredient))
				continue;
			ItemMeta meta = ingredient.getItemMeta();
			if (!meta.hasLore() || meta.getLore() == null) {
				questCrafting = false;
				continue;
			}

			if (!meta.getLore().contains(itemLore))
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

	@EventHandler
	public void onRightClickNPC(NPCRightClickEvent event) {
		Player player = event.getClicker();
		Location loc = event.getClicker().getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(BearFair20.mainRg);
		if (player.getWorld().equals(BearFair20.world) && WGUtils.getRegionsAt(loc).contains(region))
			Merchants.openMerchant(player, event.getNPC().getId());
	}

	//TODO:
	// - on milk cow --> give BF20 milk bucket, ignore if bucket is bf20 or not
	// - on shear bee nest, if 2 blocks below nest is a campfire, give BF20 honeycomb
}
