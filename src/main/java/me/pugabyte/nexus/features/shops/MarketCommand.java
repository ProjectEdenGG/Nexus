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
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import static me.pugabyte.nexus.utils.WorldGroup.isResourceWorld;

@NoArgsConstructor
public class MarketCommand extends CustomCommand implements Listener {
	private static final ResourceMarketLoggerService service = new ResourceMarketLoggerService();
	private ResourceMarketLogger logger;

	public MarketCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			logger = getLogger(world());
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

	private ResourceMarketLogger getLogger(World world) {
		if (!isResourceWorld(world))
			throw new InvalidInputException("Not allowed outside of resource world");

		return service.get(world.getUID());
	}

	private void save(World world) {
		service.queueSave(Time.SECOND.get(), getLogger(world));
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
			logger.add(block.getLocation());

		save(world());
		send(PREFIX + "Added &e" + selection.getArea() + " &3locations to logger, new size: &e" + logger.size());
	}

	@Async
	@Path("logger count")
	@Permission("group.admin")
	void logger_count() {
		send(PREFIX + logger.size() + " coordinates logged");
	}

	@Async
	@Environments(Env.TEST)
	@Path("logger add random [amount]")
	@Permission("group.admin")
	void logger_add_random(@Arg("10000") int amount) {
		if (isResourceWorld(world()))
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

	@NotNull
	private Location getRandomLocation() {
		final int x = RandomUtils.randomInt(-ResourceWorld.RADIUS, ResourceWorld.RADIUS);
		final int y = RandomUtils.randomInt(-64, 319);
		final int z = RandomUtils.randomInt(-ResourceWorld.RADIUS, ResourceWorld.RADIUS);
		return new Location(world(), x, y, z);
	}

}
