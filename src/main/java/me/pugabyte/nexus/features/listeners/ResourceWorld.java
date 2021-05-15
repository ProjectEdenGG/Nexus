package me.pugabyte.nexus.features.listeners;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.homes.HomesFeature;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.models.tip.Tip;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.models.tip.TipService;
import me.pugabyte.nexus.models.warps.Warp;
import me.pugabyte.nexus.models.warps.WarpService;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.ItemUtils.getShulkerContents;
import static me.pugabyte.nexus.utils.PlayerUtils.getAllInventoryContents;
import static me.pugabyte.nexus.utils.PlayerUtils.runCommandAsConsole;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public class ResourceWorld implements Listener {

	static {
		World survival = Bukkit.getWorld("survival");
		World resource = Bukkit.getWorld("resource");
		if (survival != null && resource != null)
			resource.setMonsterSpawnLimit((int) (survival.getMonsterSpawnLimit() * 1.5));
	}

	@EventHandler
	public void onEnterResourceWorld(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (event.getFrom().getWorld().getName().startsWith("resource")) return;

		if (Rank.of(player).isStaff()) return;

		if (event.getTo().getWorld().getName().startsWith("resource")) {
			if (!WorldGroup.get(event.getFrom()).equals(WorldGroup.SURVIVAL) || event.getFrom().getWorld().getName().startsWith("staff")) {
				if (!PlayerUtils.isAdminGroup(player)) {
					PlayerUtils.send(player, "&eYou can only enter the resource world from the Survival world");
					event.setCancelled(true);
					return;
				}
			}
			List<Material> materials = new ShopService().getMarket().getProducts(ShopGroup.SURVIVAL).stream()
					.filter(product -> product.isResourceWorld() && product.getExchangeType() == Shop.ExchangeType.BUY)
					.map(product -> product.getItem().getType())
					.collect(Collectors.toList());

			// Crafting materials
			materials.add(Material.CLAY_BALL);
			materials.add(Material.GRAVEL);
			materials.add(Material.GLOWSTONE_DUST);
			materials.add(Material.ICE);
			materials.add(Material.PACKED_ICE);

			ArrayList<Material> rejectedMaterials = new ArrayList<>();
			boolean appendMessage = false;

			for (Material material : materials)
				if (player.getInventory().contains(material)) {
					rejectedMaterials.add(material);
					event.setCancelled(true);
					appendMessage = true;
				}

			if (rejectedMaterials.size() != 0) {
				PlayerUtils.send(player, "&cYou can not go to the resource world with the below items, " +
						"please remove them from your inventory before continuing:");
				for (Material material : rejectedMaterials)
					PlayerUtils.send(player, "&e- " + camelCase(material.name()));
			}

			rejectedMaterials.clear();

			for (ItemStack item : getAllInventoryContents(player))
				for (ItemStack content : getShulkerContents(item))
					if (materials.contains(content.getType())) {
						rejectedMaterials.add(content.getType());
						event.setCancelled(true);
					}

			if (rejectedMaterials.size() != 0)
				if (appendMessage) {
					for (Material material : rejectedMaterials)
						PlayerUtils.send(player, "&e- " + camelCase(material.name()) + " (in shulkerbox)");
				} else {
					PlayerUtils.send(player, "&cYou can not go to the resource world with the below items, " +
							"please remove them from your shulkerbox before continuing:");
					for (Material material : rejectedMaterials)
						PlayerUtils.send(player, "&e- " + camelCase(material.name()) + " (in shulkerbox)");
				}

			if (!event.isCancelled()) {
				PlayerUtils.send(player, " &4Warning |");
				PlayerUtils.send(player, " &4Warning | &cYou are entering the resource world!");
				PlayerUtils.send(player, " &4Warning | &cThis world is regenerated on the &c&lfirst of every month&c,");
				PlayerUtils.send(player, " &4Warning | &cso don't leave your stuff here or you will lose it!");
				PlayerUtils.send(player, " &4Warning |");
				PlayerUtils.send(player, " &4Warning | &cThe darkness is dangerous in this world");
				PlayerUtils.send(player, " &4Warning |");
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.getPlayer().getWorld().getName().startsWith("resource")) return;

		List<Material> materials = new ArrayList<>(Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BARREL));
		materials.addAll(MaterialTag.WOODEN_DOORS.getValues());
		if (!materials.contains(event.getBlockPlaced().getType()))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.RESOURCE_WORLD_STORAGE))
			PlayerUtils.send(event.getPlayer(), " &4Warning: &cYou are currently building in the resource world! " +
					"This world is regenerated on the &c&lfirst of every month, &cso don't leave your stuff here or you will lose it!");
	}

	@EventHandler
	public void onOpenEnderChest(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player player)) return;
		if (event.getInventory().getType() != InventoryType.ENDER_CHEST) return;

		if (event.getPlayer().getWorld().getName().startsWith("resource")) {
			event.setCancelled(true);
			PlayerUtils.send(player, "&cYou can't open your enderchest while in the resource world, due to restrictions in place to keep the /market balanced");
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().getWorld().getName().startsWith("resource")) {
			switch (event.getMessage().split(" ")[0].replace("playervaults:", "")) {
				case "/pv", "/vc", "/chest", "/vault", "/playervaults" -> {
					event.setCancelled(true);
					PlayerUtils.send(event.getPlayer(), "&cYou cannot use vaults while in the resource world");
				}
			}
		}
	}

	/* Find protections from people being dumb

	select
		mcmmo_users.user,
		lwc_blocks.name,
		CONCAT("/tppos ", x, " ", y, " ", z, " ", world)
	from bearnation_smp_lwc.lwc_protections
	inner join bearnation_smp_lwc.lwc_blocks
		on lwc_blocks.id = lwc_protections.blockId
	inner join bearnation_smp_mcmmo.mcmmo_users
		on lwc_protections.owner = mcmmo_users.uuid
	where world in ('resource', 'resource_nether', 'resource_the_end')
		and lwc_blocks.name not like "%DOOR%"
		and lwc_blocks.name not like "%GATE%"
	 */

	// TODO Automation
	/*
	- #unload all 3 worlds
	- #move the directories to old_<world>
	- #remove uuid.dat
	- #delete homes
	- #create new worlds
	- paste spawn (y = 150)
	- #mv setspawn
	- clean light
	- #create npc for filid
	- #set world border
	- #fill chunks
	- #dynamap purge
	- #delete from bearnation_smp_lwc.lwc_protections where world in ('resource', 'resource_nether', 'resource_the_end');
	*/

	private static final int filidId = 2766;
	private static final int radius = 7500;

	public static void reset(boolean test) {
		NPC filid = CitizensAPI.getNPCRegistry().getById(filidId);
		filid.despawn();

		AtomicInteger wait = new AtomicInteger();
		Tasks.wait(wait.getAndAdd(5), () -> {
			for (String _worldName : Arrays.asList("resource", "resource_nether", "resource_the_end")) {
				if (test)
					_worldName = "test_" + _worldName;
				final String worldName = _worldName;

				String root = new File(".").getAbsolutePath().replace(".", "");
				File worldFolder = Paths.get(root + worldName).toFile();
				File newFolder = Paths.get(root + "old_" + worldName).toFile();

				World world = Bukkit.getWorld(worldName);
				if (world == null) {
					Nexus.severe("World " + worldName + " not loaded");
					return;
				}

				try {
					Nexus.getMultiverseCore().getMVWorldManager().unloadWorld(worldName);
				} catch (Exception ex) {
					Nexus.severe("Error unloading world " + worldName);
					ex.printStackTrace();
					return;
				}

				if (newFolder.exists())
					if (!newFolder.delete()) {
						Nexus.severe("Could not delete " + newFolder.getName() + " folder");
						return;
					}

				boolean renameSuccess = worldFolder.renameTo(newFolder);
				if (!renameSuccess) {
					Nexus.severe("Could not rename " + worldName + " folder");
					return;
				}

				boolean deleteSuccess = Paths.get(newFolder.getAbsolutePath() + "/uid.dat").toFile().delete();
				if (!deleteSuccess) {
					Nexus.severe("Could not delete " + worldName + " uid.dat file");
					return;
				}

				final Environment env;
				final String seed;
				if (worldName.contains("nether")) {
					env = Environment.NETHER;
					seed = null;
				} else if (worldName.contains("the_end")) {
					env = Environment.THE_END;
					seed = null;
				} else {
					env = Environment.NORMAL;
					seed = "-460015119172653"; // TODO List of approved seeds
				}

				Tasks.wait(wait.getAndAdd(5), () -> {
					Nexus.getMultiverseCore().getMVWorldManager().addWorld(worldName, env, seed, WorldType.NORMAL, true, null);

					Tasks.wait(wait.getAndAdd(5), () -> HomesFeature.deleteFromWorld(worldName, null));
				});
			}
		});

		Tasks.wait(wait.getAndAdd(20), () -> {
			String worldName = (test ? "test_" : "") + "resource";

			new WorldEditUtils(worldName).paster()
					.file("resource-world-spawn")
					.at(new Location(Bukkit.getWorld(worldName), 0, 150, 0))
					.air(false)
					.paste();

			Warp warp = new WarpService().get(worldName, WarpType.NORMAL);
			Nexus.getMultiverseCore().getMVWorldManager().getMVWorld(worldName).setSpawnLocation(warp.getLocation());
			filid.spawn(new Location(Bukkit.getWorld(worldName), .5, 151, -36.5, 0F, 0F));

			runCommandAsConsole("wb " + worldName + " set " + radius + " 0 0");
			runCommandAsConsole("bluemap purge " + worldName);
			Tasks.wait(wait.getAndAdd(10), () -> runCommandAsConsole("chunkmaster generate " + worldName + " " + (radius + 200) + " circle"));
		});
	}

}
