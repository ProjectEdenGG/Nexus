package me.pugabyte.nexus.features.listeners;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.homes.HomesFeature;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.shop.ResourceMarketLogger;
import me.pugabyte.nexus.models.shop.ResourceMarketLoggerService;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.BuyExchange;
import me.pugabyte.nexus.models.shop.Shop.ExchangeType;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.models.tip.Tip;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.models.tip.TipService;
import me.pugabyte.nexus.models.warps.Warp;
import me.pugabyte.nexus.models.warps.WarpService;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldEditUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static eden.utils.StringUtils.camelCase;
import static eden.utils.StringUtils.prettyMoney;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.PlayerUtils.runCommandAsConsole;
import static me.pugabyte.nexus.utils.WorldGroup.isResourceWorld;

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

		if (!event.getTo().getWorld().getName().startsWith("resource"))
			return;

		if (event.getFrom().getWorld().getName().startsWith("resource"))
			return;

		if (Rank.of(player).isStaff())
			return;

		if (event.isCancelled())
			return;

		PlayerUtils.send(player, " &4Warning |");
		PlayerUtils.send(player, " &4Warning | &cYou are entering the resource world!");
		PlayerUtils.send(player, " &4Warning | &cThis world is regenerated on the &c&lfirst of every month&c,");
		PlayerUtils.send(player, " &4Warning | &cso don't leave your stuff here or you will lose it!");
		PlayerUtils.send(player, " &4Warning |");
		PlayerUtils.send(player, " &4Warning | &cThe darkness is dangerous in this world");
		PlayerUtils.send(player, " &4Warning |");
	}

	@EventHandler
	public void onHomeBlockPlace(BlockPlaceEvent event) {
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

	private static final ResourceMarketLoggerService service = new ResourceMarketLoggerService();

	private ResourceMarketLogger getLogger(World world) {
		if (!isResourceWorld(world))
			throw new InvalidInputException("Not allowed outside of resource world");

		return service.get(world.getUID());
	}

	private void save(World world) {
		service.queueSave(Time.SECOND.get(), getLogger(world));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		final Block block = event.getBlock();
		final World world = block.getWorld();
		if (!isResourceWorld(world))
			return;

		getLogger(world).add(block.getLocation());
		save(world);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final World world = block.getWorld();
		if (!isResourceWorld(world))
			return;

		getLogger(world).remove(block.getLocation());
		save(world);
	}

	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		handlePiston(event.getBlock(), event.getBlocks(), event.getDirection());
	}

	@EventHandler
	public void onBlockPistonExtend(BlockPistonRetractEvent event) {
		handlePiston(event.getBlock(), event.getBlocks(), event.getDirection());
	}

	private void handlePiston(Block eventBlock, List<Block> blocks, BlockFace direction) {
		if (!isResourceWorld(eventBlock))
			return;

		final ResourceMarketLogger logger = getLogger(eventBlock.getWorld());

		logger.add(eventBlock.getLocation());
		for (Block block : blocks) {
			logger.add(block.getLocation());
			logger.add(block.getRelative(direction).getLocation());
		}

		save(eventBlock.getWorld());
	}

	@EventHandler
	public void onBlockDropItem(BlockDropItemEvent event) {
		if (!isResourceWorld(event.getBlock()))
			return;

		event.getItems().removeIf(item ->
			trySell(event.getPlayer(), event.getBlockState(), item.getItemStack()));
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!isResourceWorld(event.getLocation()))
			return;

		if (!(event.getEntity() instanceof TNTPrimed tnt))
			return;

		if (!(tnt.getSource() instanceof Player player))
			return;

		final Iterator<Block> iterator = event.blockList().iterator();
		while (iterator.hasNext()) {
			final Block next = iterator.next();
			for (ItemStack drop : next.getDrops())
				if (trySell(player, next.getState(), drop)) {
					next.setType(Material.AIR);
					iterator.remove();
				}
		}
	}

	private boolean trySell(Player player, BlockState block, ItemStack drop) {
		if (isNullOrAir(block.getType()) || isNullOrAir(drop))
			return false;

		final Shop shopper = new ShopService().get(player);
		final boolean disabled = shopper.getDisabledResourceMarketItems().contains(drop.getType());
		if (disabled)
			return false;

		if (getLogger(block.getWorld()).contains(block.getLocation()))
			return false;

		return trySell(player, drop);
	}

	private static final Map<UUID, BigDecimal> earned = new HashMap<>();
	private static final Map<UUID, Integer> taskIds = new HashMap<>();

	private void actionBar(Player player) {
		final UUID uuid = player.getUniqueId();
		final BigDecimal number = earned.get(uuid);

		if (number.signum() != 0) {
			Tasks.cancel(taskIds.getOrDefault(uuid, -1));
			ActionBarUtils.sendActionBar(player, "&a+" + prettyMoney(number));
			final int taskId = Tasks.wait(Time.SECOND.x(3.5), () -> earned.put(uuid, new BigDecimal(0)));
			taskIds.put(uuid, taskId);
		}
	}

	private boolean trySell(Player player, ItemStack item) {
		final Optional<Product> product = getMatchingProduct(item);
		if (product.isEmpty())
			return false;

		if (!(product.get().getExchange() instanceof BuyExchange exchange))
			throw new InvalidInputException("Cannot process resource market exchange: " + camelCase(product.get().getExchangeType()));

		process(player, item, exchange);
		actionBar(player);

		return true;
	}

	private void process(Player player, ItemStack item, BuyExchange exchange) {
		BigDecimal thing = earned.getOrDefault(player.getUniqueId(), new BigDecimal(0));
		earned.put(player.getUniqueId(), thing.add(exchange.processResourceMarket(player, item)));
	}

	private Optional<Product> getMatchingProduct(ItemStack item) {
		return getResourceWorldProducts().stream()
			.filter(product -> product.getItem().isSimilar(item))
			.findFirst();
	}

	private List<Product> getResourceWorldProducts() {
		return new ShopService().getMarket().getProducts().stream()
			.filter(product -> product.isResourceWorld() && product.getExchangeType() == ExchangeType.BUY)
			.toList();
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
	public static final int RADIUS = 7500;

	public static void reset(boolean test) {
		getFilidNPC().despawn();

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
				if (world != null)
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
					new ResourceMarketLoggerService().deleteAll();
				});
			}
		});
	}

	public static void setup(boolean test) {
		String worldName = (test ? "test_" : "") + "resource";

		new WorldEditUtils(worldName).paster()
				.file("resource-world-spawn")
				.at(new Location(Bukkit.getWorld(worldName), 0, 150, 0))
				.air(false)
				.paste();

		Warp warp = new WarpService().get(worldName, WarpType.NORMAL);
		Nexus.getMultiverseCore().getMVWorldManager().getMVWorld(worldName).setSpawnLocation(warp.getLocation());
		getFilidNPC().spawn(new Location(Bukkit.getWorld(worldName), .5, 151, -36.5, 0F, 0F));

		runCommandAsConsole("wb " + worldName + " set " + RADIUS + " 0 0");
		runCommandAsConsole("bluemap purge " + worldName);
		Tasks.wait(Time.MINUTE, () -> runCommandAsConsole("chunkmaster generate " + worldName + " " + (RADIUS + 200) + " circle"));
	}

	private static NPC getFilidNPC() {
		return CitizensAPI.getNPCRegistry().getById(filidId);
	}

}
