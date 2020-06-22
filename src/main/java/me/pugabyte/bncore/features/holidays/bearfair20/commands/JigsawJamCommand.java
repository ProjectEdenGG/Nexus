package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.discord.Discord;
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
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;
import static me.pugabyte.bncore.utils.StringUtils.timespanFormat;

// TODO Make logic common for minigames

@Aliases("jj")
@NoArgsConstructor
public class JigsawJamCommand extends CustomCommand implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("JigsawJam");
	private static final String WORLD = "gameworld";
	private static final String SCHEMATIC = "jigsawjam2";
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
	@Permission("group.seniorstaff")
	void paste() {
		paste(player().getLocation());
	}

	@Path("clear")
	@Permission("group.seniorstaff")
	void clear() {
		clear(player().getLocation());
	}

	@Path("reset [player]")
	@Permission("group.seniorstaff")
	void reset(@Arg("self") OfflinePlayer player) {
		jammer = service.get(player);
		jammer.setPlaying(false);
		jammer.setTime(0);
		service.save(jammer);
		send(PREFIX + "Reset");
	}

	@Path("debug [player]")
	@Permission("group.seniorstaff")
	void debug(@Arg("self") OfflinePlayer player) {
		jammer = service.get(player);
		send(jammer.toString());
	}

	@Path("time [player]")
	void time(@Arg("self") OfflinePlayer player) {
		jammer = service.get(player);
		if (!jammer.isPlaying())
			error("You have not started a game");

		send(PREFIX + "Your current time: " + timespanFormat(jammer.getTime()));
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
	public void onSignClick(PlayerInteractEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(WORLD))
		if (!Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK).contains(event.getAction())) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getClickedBlock() == null || !MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		Sign sign = (Sign) event.getClickedBlock().getState();
		if (!stripColor(sign.getLine(0)).equals(stripColor(PREFIX.trim()))) return;

		JigsawJamService service = new JigsawJamService();
		JigsawJammer jammer = service.get(event.getPlayer());
		if (sign.getLine(2).toLowerCase().contains("start")) {
			if (!jammer.isPlaying()) {
				start(jammer, event.getClickedBlock().getLocation());
				sign.setLine(1, colorize("&c&lClick me"));
				sign.setLine(2, colorize("&c&lto finish"));
				sign.update();
			} else
				jammer.send(PREFIX + "&cYou have already started a game");
		} else if (sign.getLine(2).toLowerCase().contains("finish")) {
			if (jammer.isPlaying()) {
				if (validate(jammer, LENGTH, HEIGHT))
					end(jammer, event.getClickedBlock().getLocation());
			} else
				jammer.send(PREFIX + "&cYou have not started a game");
		}
	}

	private void start(JigsawJammer jammer, Location location) {
		jammer.setPlaying(true);
		new JigsawJamService().save(jammer);
		jammer.send(PREFIX + "You have begun the Jigsaw Jam! Put the puzzle together as fast as you can!");
	}

	private void end(JigsawJammer jammer, Location location) {
		Discord.staffLog("**[JigsawJam]** " + jammer.getOfflinePlayer().getName() + " finished in " + timespanFormat(jammer.getTime()));
		jammer.setPlaying(false);
		jammer.setTime(0);
		new JigsawJamService().save(jammer);
		clear(location);
	}

	private void clear(Location location) {
		List<ItemFrame> maps = new ArrayList<>();
		int index = 0;

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
			Tasks.wait(++wait, map::remove);
		}

		Tasks.wait(wait + 20, () -> paste(location));
	}

	private void paste(Location location) {
		new WorldEditUtils(location).paste(SCHEMATIC, location);
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

		Utils.puga("order.size(): " + order.size());
		Utils.puga("totalMaps: " + totalMaps);

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
			send(player, PREFIX + "You have finished the Jigsaw Jam! Congratulations! Your final time is " + timespanFormat(jammer.getTime()));

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

	private enum Axis {
		X,
		Z;

		static Axis getAxis(Location location1, Location location2) {
			if (Math.floor(location1.getZ()) == Math.floor(location2.getZ()))
				return Z;
			else if (Math.floor(location1.getX()) == Math.floor(location2.getX()))
				return X;

			return null;
		}
	}

	private enum MapRotation {
		DEGREE_0,
		DEGREE_90,
		DEGREE_180,
		DEGREE_270;

		static MapRotation getRotation(Rotation rotation) {
			switch (rotation) {
				case CLOCKWISE_45:
				case FLIPPED_45:
					return DEGREE_90;
				case CLOCKWISE:
				case COUNTER_CLOCKWISE:
					return DEGREE_180;
				case CLOCKWISE_135:
				case COUNTER_CLOCKWISE_45:
					return DEGREE_270;
				default:
					return DEGREE_0;
			}
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
