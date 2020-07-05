package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.commands.staff.WorldGuardEditCommand;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.bncore.features.particles.effects.DotEffect;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.models.jigsawjam.JigsawJamService;
import me.pugabyte.bncore.models.jigsawjam.JigsawJammer;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.StringUtils.TimespanFormatter;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.Axis;
import me.pugabyte.bncore.utils.Utils.MapRotation;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

// TODO Make logic common for minigames

@Aliases("jj")
@NoArgsConstructor
public class JigsawJamCommand extends CustomCommand implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("JigsawJam");
	private static final String WORLD = "gameworld";
	private static final String SCHEMATIC = "jigsawjam3";
	private static final int LENGTH = 9, HEIGHT = 5;

	private final JigsawJamService service = new JigsawJamService();
	private JigsawJammer jammer;

	public JigsawJamCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("validate")
	void validate() {
		validate(service.get(player()), LENGTH, HEIGHT);
	}

	@Path("paste")
	@Permission("group.staff")
	void paste() {
		paste(player().getLocation());
	}

	@Path("clear")
	@Permission("group.staff")
	void clear() {
		clear(player().getLocation());
	}

	@Path("reset")
	@Permission("group.staff")
	void reset(@Arg("self") OfflinePlayer player) {
		paste(player().getLocation());
		clear(player().getLocation());
	}

	@Path("quit [player]")
	void delete(@Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		ConfirmationMenu.builder()
				.onConfirm(e -> Tasks.async(() -> {
					service.delete(service.get(player));
					send(PREFIX + "Quit game. Ask a staff member to reset the board.");
				}))
				.open(player());
	}

	@Path("deleteAll")
	@Permission("group.seniorstaff")
	void deleteAll() {
		ConfirmationMenu.builder()
				.onConfirm(e -> Tasks.async(() -> {
					service.deleteAll();
					send(PREFIX + "Reset all");
				}))
				.open(player());
	}

	@Path("debug [player]")
	@Permission("group.seniorstaff")
	void debug(@Arg("self") OfflinePlayer player) {
		send(service.get(player).toString());
	}

	@Path("time [player]")
	void time(@Arg("self") OfflinePlayer player) {
		jammer = service.get(player);
		if (!jammer.isPlaying())
			error("You have not started a game");

		send(PREFIX + "Your current time: " + TimespanFormatter.of(jammer.getTime()).format());
	}

	@Path("view")
	void view() {
		runCommand("mcmd warp minigames ;; wait 7 ;; back");
	}

	private static final int INTERVAL = 5;

	static {
		Tasks.repeat(INTERVAL, INTERVAL, () -> Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.getWorld().getName().equals(WORLD))
				.filter(player -> !AFK.get(player).isAfk())
				.filter(player -> new WorldGuardUtils(player).getRegionNamesAt(player.getLocation()).contains("jigsawjam"))
				.map(player -> (JigsawJammer) new JigsawJamService().get(player))
				.filter(JigsawJammer::isPlaying)
				.forEach(jammer -> {
					jammer.incrementTime(INTERVAL);
					new JigsawJamService().save(jammer);
				}));
	}

	@EventHandler
	public void onEntityDamage(HangingBreakByEntityEvent event) {
		if (!event.getEntity().getWorld().getName().equals(WORLD)) return;
		if (!(event.getRemover() instanceof Player)) return;
		if (!new WorldGuardUtils(event.getEntity()).getRegionNamesAt(event.getEntity().getLocation()).contains("jigsawjam")) return;
		if (event.getRemover().hasPermission(WorldGuardEditCommand.getPermission())) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getBlock().getWorld().getName().equals(WORLD)) return;
		if (!new WorldGuardUtils(event.getPlayer()).getRegionNamesAt(event.getPlayer().getLocation()).contains("jigsawjam")) return;
		if (event.getPlayer().hasPermission(WorldGuardEditCommand.getPermission())) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.getBlock().getWorld().getName().equals(WORLD)) return;
		if (!new WorldGuardUtils(event.getPlayer()).getRegionNamesAt(event.getPlayer().getLocation()).contains("jigsawjam")) return;
		if (event.getPlayer().hasPermission(WorldGuardEditCommand.getPermission())) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(WORLD)) return;
		if (!Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK).contains(event.getAction())) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getClickedBlock() == null || !MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		Sign sign = (Sign) event.getClickedBlock().getState();
		if (!stripColor(sign.getLine(0)).equals(stripColor(PREFIX.trim()))) return;

		JigsawJamService service = new JigsawJamService();
		JigsawJammer jammer = service.get(event.getPlayer());
		if (sign.getLine(2).toLowerCase().contains("start")) {
			if (!jammer.isPlaying()) {
				start(jammer);
				sign.setLine(1, colorize("&c&lClick me"));
				sign.setLine(2, colorize("&c&lto finish"));
				sign.update();
			} else
				jammer.send(PREFIX + "&cYou have already started a game");
		} else if (sign.getLine(2).toLowerCase().contains("finish")) {
			if (jammer.isPlaying()) {
				if (validate(jammer, LENGTH, HEIGHT)) {
					end(jammer);
					clear(event.getClickedBlock().getLocation());
				}
			} else
				jammer.send(PREFIX + "&cYou have not started a game");
		}
	}

	@EventHandler
	public void onMapPickup(EntityPickupItemEvent event) {
		if (!event.getEntity().getWorld().getName().equals(WORLD)) return;
		if (event.getItem().getItemStack().getType() != Material.FILLED_MAP) return;
		if (!(event.getEntity() instanceof Player)) return;
		if (!new WorldGuardUtils(event.getEntity()).getRegionNamesAt(event.getEntity().getLocation()).contains("jigsawjam")) return;

		ItemBuilder.setName(event.getItem().getItemStack(), null);
	}

	@EventHandler
	public void onChestOpen(InventoryOpenEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(WORLD)) return;
		if (event.getInventory().getLocation() == null) return;
		if (!(event.getInventory().getHolder() instanceof Chest)) return;
		if (!new WorldGuardUtils(event.getPlayer()).getRegionNamesAt(event.getInventory().getLocation()).contains("jigsawjam")) return;

		JigsawJamService service = new JigsawJamService();
		JigsawJammer jammer = service.get((Player) event.getPlayer());
		if (!jammer.isPlaying()) {
			event.setCancelled(true);
			jammer.send(PREFIX + "You must start the timer by clicking on the sign before collecting the pieces");
		}
	}

	private void start(JigsawJammer jammer) {
		jammer.setPlaying(true);
		new JigsawJamService().save(jammer);
		jammer.send(PREFIX + "You have begun the Jigsaw Jam! Put the puzzle together as fast as you can!");
	}

	private void end(JigsawJammer jammer) {
		Discord.staffLog("**[JigsawJam]** " + jammer.getOfflinePlayer().getName() + " finished in " + TimespanFormatter.of(jammer.getTime() / 20).format());
		jammer.setPlaying(false);
		jammer.setTime(0);
		new JigsawJamService().save(jammer);
	}

	private void clear(Location location) {
		List<ItemFrame> maps = new ArrayList<>();

		for (Entity entity : Utils.getNearbyEntities(location, 10).keySet())
			if (entity.getType() == EntityType.ITEM_FRAME)
				maps.add((ItemFrame) entity);

		int size = maps.size();
		int wait = 0;

		for (int i = 0; i < size; i++) {
			int random = Utils.randomInt(0, maps.size() - 1);
			ItemFrame map = maps.remove(random);

			final ItemFrame finalMap = map;
			Tasks.wait(++wait, () -> finalMap.setItem(null));
			Tasks.wait(++wait, () -> map.setRotation(Rotation.NONE));
		}

		Tasks.wait(wait + 20, () -> paste(location));
	}

	private void paste(Location location) {
		new WorldEditUtils(location).paster().file(SCHEMATIC).at(location).paste();
	}

	private boolean validate(JigsawJammer jammer, int length, int height) {
		Player player = jammer.getPlayer();
		Block blue = null;
		Block orange = null;
		for (Block block : Utils.getBlocksInRadius(player.getLocation(), 20)) {
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

		Axis axis = Axis.getAxis(startFloored, attemptFloored);
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
		int totalMaps = length * height;

		Location validate = answer.clone();

		List<Integer> order = new ArrayList<>();
		List<MapRotation> rotation = new ArrayList<>();

		for (int i = 0; i < height; i++) {
			if (axis == Axis.X)
				validate.setX(answer.getX());
			else
				validate.setZ(answer.getZ());

			for (int j = 0; j < length; j++) {
				for (Entity entity : Utils.getNearbyEntities(validate, 1).keySet())
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
		for (int i = 0; i < height; i++) {
			if (axis == Axis.X)
				check.setX(attempt.getX());
			else
				check.setZ(attempt.getZ());

			for (int j = 0; j < length; j++) {
				for (Entity entity : Utils.getNearbyEntities(check, 1).keySet())
					if (entity.getType() == EntityType.ITEM_FRAME)
						if (isEntityAtLocation(entity, check)) {
							ItemFrame itemFrame = (ItemFrame) entity;
							ItemStack item = itemFrame.getItem();
							if (item.getType() == Material.FILLED_MAP) {
								int mapId = ((MapMeta) item.getItemMeta()).getMapId();
								MapRotation mapRotation = MapRotation.getRotation(itemFrame.getRotation());
								if (order.get(index) == mapId && rotation.get(index) == mapRotation)
									++correct;
								else
									DotEffect.builder().player(player).location(entity.getLocation().getBlock().getLocation()).ticks(5 * 20).color(Color.RED).start();
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
			send(player, PREFIX + "You have finished the Jigsaw Jam! Congratulations! Your final time is " + TimespanFormatter.of(jammer.getTime() / 20).format());

			BearFairService bearFairService = new BearFairService();
			BearFairUser user = bearFairService.get(player);

			if (!jammer.hasPlayed()) {
				user.givePoints(50);
				bearFairService.save(user);
				jammer.hasPlayed(true);
				new JigsawJamService().save(jammer);
			}

			return true;
		} else {
			send(player, PREFIX + "Uh oh! You missed something! Check for mistakes");
			int incorrect = totalMaps - correct;
			BNCore.log(PREFIX + player.getName() + " got " + incorrect + " maps incorrect");
			return false;
		}
	}

	private boolean isEntityAtLocation(Entity entity, Location location) {
		return floorLocation(entity.getLocation()).equals(floorLocation(location));
	}

	private Location getAdjacentItemFrame(Location location) {
		for (Entity entity : Utils.getNearbyEntities(location, 3).keySet())
			if (entity.getType() == EntityType.ITEM_FRAME)
				if (Math.floor(location.getY()) == Math.floor(entity.getLocation().getY()))
					if (Axis.getAxis(location, entity.getLocation()) != null)
						return entity.getLocation();
		return null;
	}

	private Location floorLocation(Location location) {
		Location floored = location.clone();
		floored.setX(Math.floor(floored.getX()));
		floored.setY(Math.floor(floored.getY()));
		floored.setZ(Math.floor(floored.getZ()));
		floored.setYaw(0);
		floored.setPitch(0);
		return floored;
	}

}
