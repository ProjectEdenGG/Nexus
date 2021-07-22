package me.pugabyte.nexus.features.shops;

import com.sk89q.worldedit.regions.Region;
import eden.annotations.Environments;
import eden.utils.Env;
import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.listeners.ResourceWorld;
import me.pugabyte.nexus.features.shops.providers.BrowseMarketProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.shop.ResourceMarketLogger;
import me.pugabyte.nexus.models.shop.ResourceMarketLoggerService;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.BuyExchange;
import me.pugabyte.nexus.models.shop.Shop.ExchangeType;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static eden.utils.StringUtils.prettyMoney;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.WorldGroup.isResourceWorld;

@NoArgsConstructor
public class MarketCommand extends CustomCommand implements Listener {

	public MarketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		new BrowseMarketProvider(null).open(player());
	}

	@Path("reload")
	@Permission("group.staff")
	void reload() {
		Market.load();
		send(PREFIX + "Market reloaded");
	}

	@NotNull
	private ResourceMarketLoggerService getLoggerService() {
		return new ResourceMarketLoggerService();
	}

	private ResourceMarketLogger getLogger() {
		return getLoggerService().get0();
	}

	private void save() {
		getLoggerService().queueSave(Time.SECOND.get(), getLogger());
	}

	@Async
	@Confirm
	@Path("logger add")
	@Permission("group.admin")
	void logger_add() {
		WorldEditUtils worldEditUtils = new WorldEditUtils(player());
		Region selection = worldEditUtils.getPlayerSelection(player());
		if (selection.getArea() > 1000000)
			error("Max selection size is 1000000");

		for (Block block : worldEditUtils.getBlocks(selection))
			getLogger().add(block.getLocation());

		save();
		send(PREFIX + "Added &e" + selection.getArea() + " &3locations to logger, new size: &e" + getLogger().size());
	}

	@Async
	@Environments(Env.TEST)
	@Path("logger add random [amount]")
	@Permission("group.admin")
	void logger_add_random(@Arg("10000") int amount) {
		if (isResourceWorld(world()))
			throw new InvalidInputException("You must be in a resource world");

		final ResourceMarketLogger logger = getLogger();
		for (int i = 0; i < amount; i++) {
			while (true) {
				final Location location = getRandomLocation();
				if (logger.contains(location))
					continue;

				logger.add(location);
				break;
			}
		}

		save();
		send(PREFIX + "Added &e" + amount + " &3locations to logger, new size: &e" + getLogger().size());
	}

	@NotNull
	private Location getRandomLocation() {
		final int x = RandomUtils.randomInt(-ResourceWorld.RADIUS, ResourceWorld.RADIUS);
		final int y = RandomUtils.randomInt(-64, 319);
		final int z = RandomUtils.randomInt(-ResourceWorld.RADIUS, ResourceWorld.RADIUS);
		return new Location(world(), x, y, z);
	}

	@Async
	@Path("logger count")
	@Permission("group.admin")
	void logger_count() {
		send(PREFIX + getLogger().size() + " coordinates logged");
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!isResourceWorld(event.getBlock()))
			return;

		getLogger().add(event.getBlock().getLocation());
		save();
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!isResourceWorld(event.getBlock()))
			return;

		getLogger().remove(event.getBlock().getLocation());
		save();
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

		getLogger().add(eventBlock.getLocation());
		for (Block block : blocks) {
			getLogger().add(block.getLocation());
			getLogger().add(block.getRelative(direction).getLocation());
		}

		save();
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

		if (getLogger().contains(block.getLocation()))
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

}
