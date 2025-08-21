package gg.projecteden.nexus.features.shops;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.homes.HomesFeature;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.scheduledjobs.jobs.ResourceWorldSetupJob;
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
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Reloader.NexusReloadEvent;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.Reloader.reloader;

@Aliases("resource")
@NoArgsConstructor
public class ResourceWorldCommand extends CustomCommand implements Listener {
	private static final ResourceMarketLoggerService service = new ResourceMarketLoggerService();
	private ResourceMarketLogger logger;

	public ResourceWorldCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent() && "logger".equals(arg(1)))
			logger = getLogger(world());
	}

	public static JsonBuilder getNotice() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextReset = now.with(TemporalAdjusters.firstDayOfNextMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
		String tillReset = Timespan.of(nextReset).format();

		String prefix = StringUtils.getPrefix("&cNotice");

		return new JsonBuilder(prefix + "This world regenerates in &c" + tillReset + " &3[&eHover for more info&3]")
			.hover(List.of(
				"&3Welcome to the &eResource World!",
				"",
				"&3This world is regenerated on the &e&lfirst of every month&3,",
				"&3so don't leave your stuff here or you will lose it!",
				"",
				"&cImportant Notes: ",
				"&3- &eAuto Sell &3is &eON &3by default, change your setting in &e/market",
				"&3- &eMob Spawning &3has been &eincreased",
				"&3- &eExplosions &3cause &eblock damage"
			))
			.loreize(false);
	}

	@Path
	@Description("Teleport to the resource world")
	void warp() {
		runCommand("warp resource");
	}

	@Path("notice")
	@Description("Show the notice explaining the caveats and dangers of the resource world")
	void notice() {
		line();
		send(getNotice());
		line();
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("reset")
	@Description("Archive the existing resource world and generate a new world")
	void reset() {
		resetWorlds(player());
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("setup")
	@Description("Set up a new resource world")
	void setup() {
		setupWorlds();
	}

	@Async
	@Confirm
	@Path("logger add")
	@Permission(Group.ADMIN)
	@Description("Add all blocks in your selection the resource market logger")
	void logger_add() {
		if (SubWorldGroup.of(world()) != SubWorldGroup.RESOURCE)
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
	@Description("Count the number of coordinates logged")
	void logger_count() {
		send(PREFIX + logger.size() + " coordinates logged");
	}

	@Async
	@Environments(Env.TEST)
	@Path("logger add random [amount]")
	@Permission(Group.ADMIN)
	@Description("Track a large amount random coordinates")
	void logger_add_random(@Arg("10000") int amount) {
		if (SubWorldGroup.of(world()) != SubWorldGroup.RESOURCE)
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

	public static Map<Environment, String> SEEDS = new HashMap<>();

	@Path("setNewSeed <environment> <seed>")
	@Description("Set the next seed for the specified environment")
	void setNewSeed(Environment environment, String seed) {
		SEEDS.put(environment, seed);
		send(PREFIX + "Resource " + camelCase(environment) + " will generate with seed " + seed);
	}

	private ResourceMarketLogger getLogger(World world) {
		if (SubWorldGroup.of(world) != SubWorldGroup.RESOURCE)
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
		if (event.isCancelled())
			return;

		if (SubWorldGroup.of(event.getTo()) != SubWorldGroup.RESOURCE)
			return;

		if (SubWorldGroup.of(event.getFrom()) == SubWorldGroup.RESOURCE)
			return;

		Player player = event.getPlayer();
		Tip tip = new TipService().get(player);
		if (tip.show(TipType.RESOURCE_WORLD_WARNING)) {
			PlayerUtils.send(player, "");
			getNotice().send(player);
			PlayerUtils.send(player, "");
		}
	}

	private static final MaterialTag HOME_BLOCKS = new MaterialTag(Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BARREL).append(MaterialTag.DOORS);

	@EventHandler(ignoreCancelled = true)
	public void onHomeBlockPlace(BlockPlaceEvent event) {
		if (SubWorldGroup.of(event.getPlayer()) != SubWorldGroup.RESOURCE)
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
		if (SubWorldGroup.of(world) != SubWorldGroup.RESOURCE)
			return;

		getLogger(world).add(block.getLocation());
		save(world);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final World world = block.getWorld();
		if (SubWorldGroup.of(world) != SubWorldGroup.RESOURCE)
			return;

		Tasks.wait(2, () -> {
			if (block.getType() != Material.AIR)
				return;

			getLogger(world).remove(block.getLocation());
			save(world);
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void on(EntityChangeBlockEvent event) {
		var block = event.getBlock();
		var world = block.getWorld();

		if (SubWorldGroup.of(world) != SubWorldGroup.RESOURCE)
			return;

		if (!(event.getEntity() instanceof FallingBlock fallingBlock))
			return;

		var logger = getLogger(world);

		switch (event.getTo()) {
			case AIR -> {
				if (logger.contains(block.getLocation())) {
					logger.remove(block.getLocation());
					logger.track(fallingBlock);
				}
			}
			case SAND -> {
				if (logger.isTracked(fallingBlock)) {
					logger.add(block.getLocation());
					logger.untrack(fallingBlock);
				}
			}
		}

		save(world);
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
		if (SubWorldGroup.of(eventBlock) != SubWorldGroup.RESOURCE)
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
		if (SubWorldGroup.of(event.getBlock()) != SubWorldGroup.RESOURCE)
			return;

		event.getItems().removeIf(item ->
			trySell(event.getPlayer(), event.getBlockState(), item.getItemStack()));
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (SubWorldGroup.of(event.getLocation()) != SubWorldGroup.RESOURCE)
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
		if (Nullables.isNullOrAir(block.getType()) || Nullables.isNullOrAir(drop))
			return false;

		if (getLogger(block.getWorld()).contains(block.getLocation()))
			return false;

		final Shop shopper = new ShopService().get(player);
		switch (shopper.getResourceMarketAutoSellBehavior()) {
			case INDIVIDUAL -> {
				final boolean disabled = shopper.getDisabledResourceMarketItems().contains(drop.getType());
				if (disabled)
					return false;
			}

			case DISABLE_ALL -> {
				return false;
			}
		}

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
		return Market.RESOURCE_WORLD_PRODUCTS.stream()
			.filter(product -> product.getItem().isSimilar(item))
			.findFirst();
	}

	/* Find protections from people being dumb

	select
		mcmmo_users.user,
		lwc_blocks.name,
		date,
		CONCAT("/tppos ", x, " ", y, " ", z, " ", world) as command
	from bearnation_smp_lwc.lwc_protections
	inner join bearnation_smp_lwc.lwc_blocks
		on lwc_blocks.id = lwc_protections.blockId
	inner join bearnation_smp_mcmmo.mcmmo_users
		on lwc_protections.owner = mcmmo_users.uuid
	where world in ('resource', 'resource_nether', 'resource_the_end')
		and lwc_blocks.name not like "%DOOR%"
		and lwc_blocks.name not like "%GATE%"
		and date > '2023-04-01'

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

	public static final int RADIUS = 7500;
	private static boolean resetting = false;

	public static void resetWorlds(CommandSender executor) {
		resetting = true;

		OnlinePlayers.where().subWorldGroup(SubWorldGroup.RESOURCE).forEach(player -> {
			PlayerUtils.send(player, "&cThe resource world is resetting! Teleporting to Hub");
			WarpType.NORMAL.get("hub").teleportAsync(player);
		});

		AtomicInteger wait = new AtomicInteger();
		Tasks.wait(wait.getAndAdd(40), () -> {
			for (Environment environment : EnumUtils.valuesExcept(Environment.class, Environment.CUSTOM)) {
				String world = "resource" + (environment == Environment.NORMAL ? "" : "_" + environment.name().toLowerCase());

				final Consumer<String> run = command -> Tasks.wait(wait.getAndAdd(5), () -> {
					Nexus.log("Running /" + command);
					PlayerUtils.runCommandAsConsole(command);
				});

				final String args;
				switch (environment) {
					case NORMAL -> args = "normal";
					case NETHER -> args = "nether";
					case THE_END -> args = "end";
					default -> throw new IllegalStateException("Unexpected value: " + environment);
				}

				String seed = SEEDS.get(environment);

				run.accept("mv delete " + world);
				run.accept("mv confirm");
				run.accept("mv create " + world + " " + args + (seed == null ? "" : " -s " + seed));
			}

			Tasks.wait(wait.getAndAdd(5), () -> reloader.executor(executor).reload());
		});
	}

	@EventHandler
	public void on(NexusReloadEvent event) {
		if (resetting)
			new ResourceWorldSetupJob().scheduleSync(5);
	}

	public static void setupWorlds() {
		String worldName = "resource";

		World world = Bukkit.getWorld(worldName);
		if (world == null) throw new InvalidInputException("Resource world not found");
		new WorldEditUtils(worldName).paster()
			.file("resource-world-spawn")
			.at(new Location(world, 0, 150, 0))
			.air(false)
			.entities(true)
			.pasteAsync();

		HomesFeature.deleteFromWorld(worldName, null);
		HomesFeature.deleteFromWorld(worldName + "_nether", null);
		HomesFeature.deleteFromWorld(worldName + "_the_end", null);
		world.getChunkAt(0, 0).setForceLoaded(true);

		Warp warp = WarpType.NORMAL.get(worldName);
		Nexus.getMultiverseCore().getMVWorldManager().getMVWorld(worldName).setSpawnLocation(warp.getLocation());
		new ResourceMarketLoggerService().deleteAll();

		PlayerUtils.runCommandAsConsole("plugman reload holograms");

		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(RADIUS * 2);
		PlayerUtils.runCommandAsConsole("bluemap purge " + worldName);
		Tasks.wait(TickTime.SECOND.x(60), () -> PlayerUtils.runCommandAsConsole("chunky world resource"));
		Tasks.wait(TickTime.SECOND.x(61), () -> PlayerUtils.runCommandAsConsole("chunky worldborder"));
		Tasks.wait(TickTime.SECOND.x(62), () -> PlayerUtils.runCommandAsConsole("chunky start"));
		Tasks.wait(TickTime.SECOND.x(63), () -> PlayerUtils.runCommandAsConsole("chunky confirm"));
	}

}
