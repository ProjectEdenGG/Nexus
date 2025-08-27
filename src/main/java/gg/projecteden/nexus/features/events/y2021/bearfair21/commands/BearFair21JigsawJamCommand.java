package gg.projecteden.nexus.features.events.y2021.bearfair21.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.jigsawjam.JigsawJamService;
import gg.projecteden.nexus.models.jigsawjam.JigsawJammer;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.LocationUtils.Axis;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.Utils.MapRotation;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Aliases("jj")
@HideFromWiki
@NoArgsConstructor
public class BearFair21JigsawJamCommand extends CustomCommand implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("JigsawJam");
	private static final String WORLD = "gameworld";
	private static final String SCHEMATIC = "jigsawjam4";
	private static final int LENGTH = 9, HEIGHT = 5;
	private static final int firstX = 1952, firstY = 4, firstZ = -64;
	private static final int separatorZ = -25, arenas = 20;

	private final JigsawJamService service = new JigsawJamService();
	private WorldGuardUtils worldGuardUtils;

	public BearFair21JigsawJamCommand(@NonNull CommandEvent event) {
		super(event);
		worldGuardUtils = new WorldGuardUtils(getWorld());
	}

	@Path
	void warp() {
		final Location first = new Location(getWorld(), firstX, firstY, firstZ);

		List<Location> locations = getLocations(first);
		List<Location> filtered = filter(locations);
		Location result = choose(locations, filtered);

		if (result == null)
			error("Could not find an arena to teleport to");

		result.setYaw(270);
		player().teleportAsync(LocationUtils.getCenteredLocation(result), TeleportCause.COMMAND);
	}

	@Path("validate")
	void validate() {
		validate(service.get(player()));
	}

	@Path("paste")
	@Permission(Group.STAFF)
	void paste() {
		paste(location());
	}

	@Path("clear")
	@Permission(Group.STAFF)
	void clear() {
		clear(location());
	}

	@Path("reset")
	@Permission(Group.STAFF)
	void reset() {
		paste(location());
		clear(location());
	}

	@Async
	@Confirm
	@Path("quit [player]")
	void delete(@Arg(value = "self", permission = Group.STAFF) JigsawJammer jammer) {
		service.delete(jammer);
		send(PREFIX + "Quit game. Ask a staff member to reset the board.");
	}

	@Path("time [player]")
	void time(@Arg("self") JigsawJammer jammer) {
		if (!jammer.isPlaying())
			error("You have not started a game");

		send(PREFIX + "Your current time: " + Timespan.ofSeconds(jammer.getTime()).format());
	}

	@Path("view")
	void view() {
		runCommand("mcmd warp minigames ;; wait 7 ;; back");
	}

	@NotNull
	private ArrayList<Location> getLocations(Location first) {
		return new ArrayList<>() {{
			for (int i = 0; i < arenas; i++)
				add(first.clone().add(0, 0, separatorZ * i));
		}};
	}

	@NotNull
	private ArrayList<Location> filter(List<Location> locations) {
		return new ArrayList<>() {{
			for (Location next : locations) {
				final Set<ProtectedRegion> regions = worldGuardUtils.getRegionsLikeAt("jigsawjam_\\d+", next);
				if (regions.isEmpty()) {
					Nexus.warn("Could not find Jigsaw Jam region at " + StringUtils.xyz(next));
					add(next);
				} else {
					var players = worldGuardUtils.getPlayersInRegion(regions.iterator().next());
					if (players.isEmpty())
						add(next);
				}
			}
		}};
	}

	private Location choose(List<Location> locations, List<Location> filtered) {
		Location result;
		if (filtered.isEmpty()) {
			Nexus.warn("Could not find an empty Jigsaw Jam arena, choosing randomly");
			result = RandomUtils.randomElement(locations);
		} else {
			result = RandomUtils.randomElement(filtered);
		}
		return result;
	}

	private static World getWorld() {
		return Bukkit.getWorld(WORLD);
	}

	private static final int INTERVAL = 5;

	static {
		final WorldGuardUtils worldGuardUtils = new WorldGuardUtils(getWorld());
		Tasks.repeat(INTERVAL, INTERVAL, () -> OnlinePlayers.where().world(getWorld()).get().stream()
				.filter(player -> !AFK.get(player).isAfk())
				.filter(player -> worldGuardUtils.isInRegion(player.getLocation(), "jigsawjam"))
				.map(player -> new JigsawJamService().get(player))
				.filter(JigsawJammer::isPlaying)
				.forEach(jammer -> {
					jammer.incrementTime(INTERVAL);
					new JigsawJamService().save(jammer);
				}));
	}

	private boolean isInJigsawJam(Entity entity) {
		return new WorldGuardUtils(entity).getRegionNamesAt(entity.getLocation()).contains("jigsawjam");
	}

	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		if (!event.getEntity().getWorld().getName().equals(WORLD)) return;
		if (!(event.getRemover() instanceof Player player)) return;
		if (!isInJigsawJam(event.getEntity())) return;
		if (WorldGuardEditCommand.isEnabled(player)) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getBlock().getWorld().getName().equals(WORLD)) return;
		if (!isInJigsawJam(event.getPlayer())) return;
		if (WorldGuardEditCommand.isEnabled(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (!event.getBlock().getWorld().getName().equals(WORLD)) return;
		if (!isInJigsawJam(event.getPlayer())) return;
		if (WorldGuardEditCommand.isEnabled(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(WORLD)) return;
		if (!isInJigsawJam(event.getPlayer())) return;
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getClickedBlock() == null) return;
		if (!MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		Sign sign = (Sign) event.getClickedBlock().getState();
		if (!StringUtils.stripColor(sign.getLine(0)).equals(StringUtils.stripColor(PREFIX.trim()))) return;

		JigsawJamService service = new JigsawJamService();
		JigsawJammer jammer = service.get(event.getPlayer());
		if (sign.getLine(2).toLowerCase().contains("start")) {
			if (!jammer.isPlaying()) {
				start(jammer);
				sign.setLine(1, StringUtils.colorize("&c&lClick me"));
				sign.setLine(2, StringUtils.colorize("&c&lto finish"));
				sign.update();
			} else
				jammer.sendMessage(PREFIX + "&cYou have already started a game");
		} else if (sign.getLine(2).toLowerCase().contains("finish")) {
			if (jammer.isPlaying()) {
				if (validate(jammer)) {
					end(jammer);
					clear(event.getClickedBlock().getLocation());
				}
			} else
				jammer.sendMessage(PREFIX + "&cYou have not started a game");
		}
	}

	@EventHandler
	public void onMapPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!player.getWorld().getName().equals(WORLD)) return;
		if (!isInJigsawJam(player)) return;
		if (event.getItem().getItemStack().getType() != Material.FILLED_MAP) return;

		ItemBuilder.setName(event.getItem().getItemStack(), null);
	}

	@EventHandler
	public void onChestOpen(InventoryOpenEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(WORLD)) return;
		if (!isInJigsawJam(event.getPlayer())) return;
		if (event.getInventory().getLocation() == null) return;
		if (!(event.getInventory().getHolder() instanceof Chest)) return;
		if (WorldGuardEditCommand.isEnabled(event.getPlayer())) return;

		JigsawJammer jammer = new JigsawJamService().get(event.getPlayer());
		if (!jammer.isPlaying()) {
			event.setCancelled(true);
			jammer.sendMessage(PREFIX + "You must start the timer by clicking on the sign before collecting the pieces");
		}
	}

	private void start(JigsawJammer jammer) {
		jammer.setPlaying(true);
		new JigsawJamService().save(jammer);
		jammer.sendMessage(PREFIX + "You have begun the Jigsaw Jam! Put the puzzle together as fast as you can!");
	}

	private void end(JigsawJammer jammer) {
		Discord.staffLog(DISCORD_PREFIX + jammer.getName() + " finished in " + Timespan.ofSeconds(jammer.getTime() / 20).format());
		jammer.setPlaying(false);
		jammer.setTime(0);
		new JigsawJamService().save(jammer);
	}

	private void clear(Location location) {
		List<ItemFrame> maps = new ArrayList<>();

		for (Entity entity : EntityUtils.getNearbyEntities(location, 10).keySet())
			if (entity.getType() == EntityType.ITEM_FRAME)
				maps.add((ItemFrame) entity);

		int size = maps.size();
		int wait = 0;

		for (int i = 0; i < size; i++) {
			int random = RandomUtils.randomInt(0, maps.size() - 1);
			ItemFrame map = maps.remove(random);

			Tasks.wait(++wait, () -> map.setItem(null));
			Tasks.wait(++wait, () -> map.setRotation(Rotation.NONE));
		}

		Tasks.wait(wait + 20, () -> paste(location));
	}

	private void paste(Location location) {
		new WorldEditUtils(location).paster().file(SCHEMATIC).at(location).pasteAsync();
	}

	private boolean validate(JigsawJammer jammer) {
		Player player = jammer.getOnlinePlayer();
		Block blue = null;
		Block orange = null;
		for (Block block : BlockUtils.getBlocksInRadius(player.getLocation(), 20)) {
			if (block.getType() == Material.AIR)
				continue;
			else if (block.getType() == Material.LIGHT_BLUE_CONCRETE)
				blue = block;
			else if (block.getType() == Material.ORANGE_CONCRETE)
				orange = block;
			if (blue != null && orange != null)
				break;
		}

		if (blue == null) {
			send(player, PREFIX + "&cAnswer block not found");
			return false;
		}
		if (orange == null) {
			send(player, PREFIX + "&cAttempt block not found");
			return false;
		}

		Location answer = getAdjacentItemFrame(blue.getLocation());
		Location attempt = getAdjacentItemFrame(orange.getLocation());

		if (answer == null) {
			send(player, PREFIX + "&cAnswer board not found");
			return false;
		}
		if (attempt == null) {
			send(player, PREFIX + "&cAttempt board not found");
			return false;
		}

		Location startFloored = floorLocation(orange.getLocation());
		Location attemptFloored = floorLocation(attempt);

		Axis axis = Axis.of(startFloored, attemptFloored);
		double diff;

		if (axis == Axis.Z)
			diff = startFloored.getX() - attemptFloored.getX();
		else if (axis == Axis.X)
			diff = startFloored.getZ() - attemptFloored.getZ();
		else {
			send(player, PREFIX + "&cBoard not configured correctly");
			return false;
		}

		double direction = diff / 2;

		int correct = 0;
		int totalMaps = LENGTH * HEIGHT;

		Location validate = answer.clone();

		List<Integer> order = new ArrayList<>();
		List<MapRotation> rotation = new ArrayList<>();

		for (int i = 0; i < HEIGHT; i++) {
			if (axis == Axis.X)
				validate.setX(answer.getX());
			else
				validate.setZ(answer.getZ());

			for (int j = 0; j < LENGTH; j++) {
				for (Entity entity : EntityUtils.getNearbyEntities(validate, 1).keySet())
					if (entity.getType() == EntityType.ITEM_FRAME)
						if (isEntityAtLocation(entity, validate)) {
							ItemFrame itemFrame = (ItemFrame) entity;
							ItemStack item = itemFrame.getItem();
							if (item.getType() == Material.FILLED_MAP) {
								order.add(((MapMeta) item.getItemMeta()).getMapId());
								rotation.add(MapRotation.getRotation(itemFrame.getRotation()));
							}
					}

				if (axis == Axis.X)
					validate.add(0, 0, direction);
				else
					validate.add(0, 0, -direction);
			}

			validate.add(0, -1, 0);
		}

		if (order.size() != totalMaps) {
			send(player, PREFIX + "&cCould not find all validation maps");
			return false;
		}

		int index = 0;
		Location check = attempt.clone();
		for (int i = 0; i < HEIGHT; i++) {
			if (axis == Axis.X)
				check.setX(attempt.getX());
			else
				check.setZ(attempt.getZ());

			for (int j = 0; j < LENGTH; j++) {
				for (Entity entity : EntityUtils.getNearbyEntities(check, 1).keySet())
					if (entity.getType() == EntityType.ITEM_FRAME)
						if (isEntityAtLocation(entity, check)) {
							ItemFrame itemFrame = (ItemFrame) entity;
							ItemStack item = itemFrame.getItem();
							if (item.getType() == Material.FILLED_MAP) {
								int mapId = ((MapMeta) item.getItemMeta()).getMapId();
								MapRotation mapRotation = MapRotation.getRotation(itemFrame.getRotation());
								if (order.get(index) == mapId && rotation.get(index) == mapRotation)
									++correct;
							}
					}

				++index;

				if (axis == Axis.X)
					check.add(0, 0, -direction);
				else
					check.add(0, 0, direction);
			}

			check.add(0, -1, 0);
		}

		if (correct == totalMaps) {
			send(player, PREFIX + "You have finished the Jigsaw Jam! Congratulations! Your final time is " + Timespan.ofSeconds(jammer.getTime() / 20).format());

			return true;
		} else {
			send(player, PREFIX + "Uh oh! You missed something! Check for mistakes");
			int incorrect = totalMaps - correct;
			Nexus.log(PREFIX + player.getName() + " got " + incorrect + " maps incorrect");
			return false;
		}
	}

	private boolean isEntityAtLocation(Entity entity, Location location) {
		return floorLocation(entity.getLocation()).equals(floorLocation(location));
	}

	private Location getAdjacentItemFrame(Location location) {
		for (Entity entity : EntityUtils.getNearbyEntities(location, 3).keySet())
			if (entity.getType() == EntityType.ITEM_FRAME)
				if (Math.floor(location.getY()) == Math.floor(entity.getLocation().getY()))
					if (Axis.of(location, entity.getLocation()) != null)
						return entity.getLocation();
		return null;
	}

	private Location floorLocation(Location location) {
		return location.getBlock().getLocation();
	}

}
