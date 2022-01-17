package gg.projecteden.nexus.features.shops;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.annotations.Async;
import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.homes.HomesFeature;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.shop.ResourceMarketLogger;
import gg.projecteden.nexus.models.shop.ResourceMarketLoggerService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.BuyExchange;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.models.tip.Tip;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.models.tip.TipService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.utils.Env;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static gg.projecteden.nexus.features.shops.Market.RESOURCE_WORLD_PRODUCTS;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.WorldGroup.isResourceWorld;

@NoArgsConstructor
public class ResourceWorldCommand extends CustomCommand implements Listener {
	private static final ResourceMarketLoggerService service = new ResourceMarketLoggerService();
	private ResourceMarketLogger logger;

	public ResourceWorldCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent() && "logger".equals(arg(1)))
			logger = getLogger(world());
	}

	@Path
	void warp() {
		runCommand("warp resource");
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("reset")
	void reset() {
		resetWorlds();
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("setup")
	void setup() {
		setupWorlds();
	}

	@Async
	@Confirm
	@Path("logger add")
	@Permission(Group.ADMIN)
	void logger_add() {
		if (!isResourceWorld(world()))
			throw new InvalidInputException("You must be in a resource world");

		WorldEditUtils worldedit = new WorldEditUtils(player());
		Region selection = worldedit.getPlayerSelection(player());
		if (selection.getArea() > 1000000)
			error("Max selection size is 1000000");

		for (Block block : worldedit.getBlocks(selection))
			logger.add(block.getLocation());

		save(world());
		send(PREFIX + "Added &e" + selection.getArea() + " &3locations to logger, new size: &e" + logger.size());
	}

	@Async
	@Path("logger count")
	@Permission(Group.ADMIN)
	void logger_count() {
		send(PREFIX + logger.size() + " coordinates logged");
	}

	@Async
	@Environments(Env.TEST)
	@Path("logger add random [amount]")
	@Permission(Group.ADMIN)
	void logger_add_random(@Arg("10000") int amount) {
		if (!isResourceWorld(world()))
			throw new InvalidInputException("You must be in a resource world");

		for (int i = 0; i < amount; i++) {
			while (true) {
				final Location location = getRandomLocation();
				if (logger.contains(location))
					continue;

				logger.add(location);
				break;
			}
		}

		save(world());
		send(PREFIX + "Added &e" + amount + " &3locations to logger, new size: &e" + getLogger(world()).size());
	}

	private ResourceMarketLogger getLogger(World world) {
		if (!isResourceWorld(world))
			throw new InvalidInputException("Not allowed outside of resource world");

		return service.get(world.getUID());
	}

	private void save(World world) {
		service.queueSave(TickTime.SECOND.get(), getLogger(world));
	}

	@NotNull
	private Location getRandomLocation() {
		final int x = RandomUtils.randomInt(-RADIUS, RADIUS);
		final int y = RandomUtils.randomInt(-64, 319);
		final int z = RandomUtils.randomInt(-RADIUS, RADIUS);
		return new Location(world(), x, y, z);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEnterResourceWorld(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (!isResourceWorld(event.getTo()))
			return;

		if (isResourceWorld(event.getFrom()))
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

	private static final MaterialTag HOME_BLOCKS = new MaterialTag(Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BARREL).append(MaterialTag.DOORS);

	@EventHandler(ignoreCancelled = true)
	public void onHomeBlockPlace(BlockPlaceEvent event) {
		if (!isResourceWorld(event.getPlayer()))
			return;

		if (!HOME_BLOCKS.isTagged(event.getBlockPlaced()))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.RESOURCE_WORLD_STORAGE))
			PlayerUtils.send(event.getPlayer(), " &4Warning: &cYou are currently building in the resource world! " +
				"This world is regenerated on the &c&lfirst of every month, &cso don't leave your stuff here or you will lose it!");
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		final Block block = event.getBlock();
		final World world = block.getWorld();
		if (!isResourceWorld(world))
			return;

		getLogger(world).add(block.getLocation());
		save(world);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final World world = block.getWorld();
		if (!isResourceWorld(world))
			return;

		Tasks.wait(2, () -> {
			if (block.getType() != Material.AIR)
				return;

			getLogger(world).remove(block.getLocation());
			save(world);
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		handlePiston(event.getBlock(), event.getBlocks(), event.getDirection());
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
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

	@EventHandler(ignoreCancelled = true)
	public void onBlockDropItem(BlockDropItemEvent event) {
		if (!isResourceWorld(event.getBlock()))
			return;

		event.getItems().removeIf(item ->
			trySell(event.getPlayer(), event.getBlockState(), item.getItemStack()));
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!isResourceWorld(event.getLocation()))
			return;

		if (!(event.getEntity() instanceof TNTPrimed tnt))
			return;

		if (!(tnt.getSource() instanceof Player player))
			return;

		Utils.removeIf(block -> {
			final BlockState state = block.getState();
			for (ItemStack drop : block.getDrops())
				if (trySell(player, state, drop))
					return true;
			return false;
		}, block -> block.setType(Material.AIR), event.blockList());
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

	private boolean trySell(Player player, ItemStack item) {
		final Optional<Product> product = getMatchingProduct(item);
		if (product.isEmpty())
			return false;

		if (!(product.get().getExchange() instanceof BuyExchange exchange))
			throw new InvalidInputException("Cannot process resource market exchange: " + camelCase(product.get().getExchangeType()));

		exchange.processResourceMarket(player, item);

		return true;
	}

	private Optional<Product> getMatchingProduct(ItemStack item) {
		return RESOURCE_WORLD_PRODUCTS.stream()
			.filter(product -> product.getItem().isSimilar(item))
			.findFirst();
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

	public static void resetWorlds() {
		getFilidNPC().despawn();

		AtomicInteger wait = new AtomicInteger();
		Tasks.wait(wait.getAndAdd(40), () -> {
			for (String world : Arrays.asList("resource", "resource_nether", "resource_the_end")) {
				final Consumer<String> run = command -> Tasks.wait(wait.getAndAdd(5), () -> {
					Nexus.log("Running /" + command);
					PlayerUtils.runCommandAsConsole(command);
				});

				run.accept("mv delete " + world);
				run.accept("mv confirm");

				final String args;
				if (world.contains("nether"))
					args = "nether";
				else if (world.contains("the_end"))
					args = "end";
				else
					args = "normal -s -900287747221759";

				run.accept("mv create " + world + " " + args);
			}
		});
	}

	public static void setupWorlds() {
		String worldName = "resource";

		new WorldEditUtils(worldName).paster()
			.file("resource-world-spawn")
			.at(new Location(Bukkit.getWorld(worldName), 0, 150, 0))
			.air(false)
			.pasteAsync()
			.thenRun(() -> getFilidNPC().spawn(new Location(Bukkit.getWorld(worldName), .5, 152, -36.5)));

		HomesFeature.deleteFromWorld(worldName, null);
		new ResourceMarketLoggerService().deleteAll();

		Warp warp = WarpType.NORMAL.get(worldName);
		Nexus.getMultiverseCore().getMVWorldManager().getMVWorld(worldName).setSpawnLocation(warp.getLocation());
		new ResourceMarketLoggerService().deleteAll();

		PlayerUtils.runCommandAsConsole("wb " + worldName + " set " + RADIUS + " 0 0");
		PlayerUtils.runCommandAsConsole("bluemap purge " + worldName);
		Tasks.wait(TickTime.MINUTE, () -> PlayerUtils.runCommandAsConsole("chunkmaster generate " + worldName + " " + (RADIUS + 200) + " circle"));
	}

	private static NPC getFilidNPC() {
		return CitizensAPI.getNPCRegistry().getById(filidId);
	}

}
