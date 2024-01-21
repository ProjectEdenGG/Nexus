package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.FallingBlocksMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.utils.PowerUpUtils;
import gg.projecteden.nexus.features.minigames.utils.PowerUpUtils.PowerUp;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public class FallingBlocks extends TeamlessMechanic {

	@Getter
	private final List<Material> COLOR_CHOICES = MaterialTag.CONCRETE_POWDERS.getValues().stream().toList();

	@Override
	public @NotNull String getName() {
		return "Falling Blocks";
	}

	@Override
	public @NotNull String getDescription() {
		return "Climb to the top without getting squished";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.SAND);
	}

	@Override
	public boolean shouldClearInventory() {
		return false;
	}

	@Override
	public boolean canMoveArmor() {
		return false;
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);
		Match match = event.getMatch();
		clearArena(match, null);
	}

	@Override
	public boolean useScoreboardNumbers() {
		return false;
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match) {
		return new LinkedHashMap<>() {{
			put("&3&lReach the top to win!", 0);
			match.getAliveMinigamers().forEach(minigamer -> put(minigamer.getNickname(), 0));
			match.getDeadMinigamers().forEach(minigamer -> put("&8&m&o" + minigamer.getNickname(), 0));
		}};
	}

	private void clearArena(Match match, @Nullable Material material) {
		match.worldedit().getBlocks(match.getArena().getRegion("arena")).forEach(block -> {
			if (material != null && !block.getType().equals(material))
				return;

			block.setType(Material.AIR);
		});
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);
		Minigamer minigamer = event.getMinigamer();
		Player player = minigamer.getOnlinePlayer();

		ItemStack menuItem = new ItemBuilder(Material.BLUE_CONCRETE_POWDER).name("Choose A Block!").build();
		player.getInventory().setItem(0, menuItem);

		minigamer.getMatch().getTasks().wait(30, () -> minigamer.tell("Click a block to select it!"));
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();
		List<Minigamer> minigamers = match.getMinigamers();

		setPlayerBlocks(minigamers, match);
		for (Minigamer minigamer : minigamers) {
			minigamer.clearInventory();
		}
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		final Match match = event.getMatch();
		final FallingBlocksMatchData matchData = match.getMatchData();
		final WorldGuardUtils worldGuardUtils = match.worldguard();

		final ProtectedRegion ceiling = match.getArena().getProtectedRegion("ceiling");
		final int y = (int) worldGuardUtils.toLocation(ceiling.getMinimumPoint()).getY();

		// falling blocks
		match.getTasks().repeat(0, TickTime.TICK.x(3), () -> {
			for (Minigamer minigamer : match.getAliveMinigamers()) {
				final Location location = minigamer.getLocation();
				location.setY(y);

				if (matchData.pauseBlocks.contains(minigamer))
					continue;

				if (!MaterialTag.ALL_AIR.isTagged(location.getBlock()))
					continue;

				Material material = matchData.getColor(minigamer);
				int radius = matchData.thickLines.contains(minigamer) ? 1 : 0;

				spawnFallingBlock(match, material, location, radius);
			}
		});

		// power ups: dynamic spawning locations
		matchData.maxPowerUps = Math.min(match.getAliveMinigamers().size() + 3, 8);
		matchData.powerUpUtils = new PowerUpUtils(match, powerUpWeights.keySet().stream().toList());
		match.getTasks().repeat(TickTime.SECOND.x(10), TickTime.SECOND.x(5), () -> {
			if (matchData.spawnedPowerups == matchData.maxPowerUps)
				return;

			if (RandomUtils.chanceOf(25))
				return;

			spawnNextPowerUp(match);
		});
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		Minigamer minigamer = event.getMinigamer();
		FallingBlocksMatchData matchData = minigamer.getMatch().getMatchData();

		Player player = minigamer.getPlayer();
		String message = null;
		if (player != null) {
			EntityDamageEvent e = player.getLastDamageCause();

			if (e != null) {
				message = "&e" + minigamer.getNickname();
				Minigamer killer = matchData.getMinigamer(e.getEntity().getLocation().getBlock().getType());
				if (killer != null) {
					if (killer == minigamer)
						message += " &3squished themselves";
					else
						message += " &3was squished by &e" + killer.getNickname();
				} else
					message += " &3was squished";
			}
		}

		if (message == null)
			message = "&e" + minigamer.getNickname() + " &3was squished";

		event.setDeathMessage(message);
		super.onDeath(event);
	}

	private void spawnNextPowerUp(Match match) {
		ProtectedRegion ceiling = match.getArena().getProtectedRegion("ceiling");

		Location location = null;
		for (int i = 0; i < 10; i++) {
			Block block = match.worldguard().getRandomBlock(ceiling);
			if (block.getType().equals(Material.AIR)) {
				location = block.getLocation().toHighestLocation().toCenterLocation();
				break;
			}
		}

		if (location == null)
			return;

		FallingBlocksMatchData matchData = match.getMatchData();

		matchData.powerUpUtils.spawn(location, false, null, getNextPowerUp());
		matchData.spawnedPowerups += 1;
	}

	// Auto-select unique color for players who have not themselves
	private void setPlayerBlocks(List<Minigamer> minigamers, Match match) {
		FallingBlocksMatchData matchData = match.getMatchData();

		for (Minigamer minigamer : minigamers) {
			Material colorType = matchData.getColor(minigamer);
			if (colorType == null) {
				Material next = matchData.getNextColor();
				matchData.setColor(minigamer, next);
				colorType = next;
			}
			minigamer.getOnlinePlayer().getInventory().setHelmet(new ItemStack(colorType));
		}
	}

	// Select unique color
	@EventHandler
	public void setPlayerBlock(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (!MaterialTag.CONCRETE_POWDERS.isTagged(event.getItem().getType())) return;
		if (!ActionGroup.CLICK_AIR.applies(event)) return;

		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld())) return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isInLobby(this)) return;

		Match match = minigamer.getMatch();
		if (match.isStarted()) return;

		new ColorPickMenu(getCOLOR_CHOICES(), "_CONCRETE_POWDER").open(event.getPlayer());
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (!event.getTo().equals(Material.SAND))
			return;

		final Location location = event.getBlock().getLocation();
		Match match = MatchManager.getActiveMatchFromLocation(this, location);
		if (match == null)
			return;

		for (ProtectedRegion region : match.worldguard().getRegionsAt(location)) {
			if (!match.getArena().ownsRegion(region))
				continue;

			for (Minigamer minigamer : match.getAliveMinigamers())
				if (LocationUtils.blockLocationsEqual(minigamer.getLocation(), location))
					kill(minigamer);
			return;
		}
	}

	@EventHandler
	public void onPlayerEnteringRegion(PlayerEnteringRegionEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		if (event.getRegion().getId().contains("ceiling")) {
			minigamer.scored();
			minigamer.getMatch().end();
		}
	}

	private static final List<DamageCause> DAMAGE_CAUSES = List.of(DamageCause.SUFFOCATION, DamageCause.FALLING_BLOCK);

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!DAMAGE_CAUSES.contains(event.getCause()))
			return;

		if (!(event.getEntity() instanceof Player player))
			return;

		final Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		kill(minigamer);
	}

	@Rows(2)
	@Title("Select Your Color")
	private static class ColorPickMenu extends InventoryProvider {
		private final List<Material> COLOR_CHOICES;
		private final String filter;

		public ColorPickMenu(@NotNull List<Material> choices, String filter) {
			this.COLOR_CHOICES = choices;
			this.filter = filter;
		}

		@Override
		public void init() {
			Minigamer minigamer = Minigamer.of(viewer);
			Match match = minigamer.getMatch();
			FallingBlocksMatchData matchData = match.getMatchData();

			int row = 0;
			int col = 0;
			for (Material colorType : COLOR_CHOICES) {
				ItemStack colorItem = new ItemStack(colorType);

				if (!matchData.isColorChosen(colorType)) {
					if (col > 8) {
						++row;
						col = 0;
					}

					contents.set(new SlotPos(row, col++), ClickableItem.of(colorItem, e -> pickColor(colorItem, viewer)));
				}
			}
		}

		public void pickColor(ItemStack colorItem, Player player) {
			Minigamer minigamer = Minigamer.of(player);
			Match match = minigamer.getMatch();
			FallingBlocksMatchData matchData = match.getMatchData();

			if (matchData.isColorChosen(colorItem.getType())) {
				minigamer.tell("&cThis color has already been chosen.");
				init();
				return;
			}

			matchData.removeColor(minigamer);

			player.getInventory().setHelmet(colorItem);
			player.getInventory().setItem(0, colorItem);
			matchData.setColor(minigamer, colorItem.getType());

			minigamer.tell("You chose " + camelCase(colorItem.getType().name().replace(filter, "")) + "!");

			player.closeInventory();
		}
	}

	PowerUpUtils.PowerUp CLEAR_ARENA = new PowerUpUtils.PowerUp("&bClear Arena", null,
		new ItemBuilder(Material.TNT).glow().build(),
		minigamer -> {
			pickupPowerup(minigamer);

			clearArena(minigamer.getMatch(), null);
			minigamer.getMatch().broadcast("&bThe arena has been cleared by " + minigamer.getNickname());
		}
	);

	PowerUpUtils.PowerUp CLEAR_SELF = new PowerUpUtils.PowerUp("&aClear Self", null,
		new ItemBuilder(Material.FLINT_AND_STEEL).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			FallingBlocksMatchData matchData = minigamer.getMatch().getMatchData();
			Material material = matchData.getColor(minigamer);
			clearArena(minigamer.getMatch(), material);
			minigamer.tell("&aYou have cleared the arena of " + StringUtils.camelCase(material) + "!");
		}
	);

	PowerUpUtils.PowerUp JUMP = new PowerUpUtils.PowerUp("&aJump Boost", null,
		new ItemBuilder(Material.RABBIT_FOOT).glow().build(),
		minigamer -> {
			pickupPowerup(minigamer);

			applyPotionEffect(minigamer, PotionEffectType.JUMP, 2);
			minigamer.tell("&aYou have picked up jump boost for 10s!");
		}
	);

	PowerUpUtils.PowerUp SPEED_OTHERS = new PowerUpUtils.PowerUp("&cSpeed Boost", null,
		new ItemBuilder(Material.SUGAR).glow().build(),
		minigamer -> {
			pickupPowerup(minigamer);

			minigamer.tell("&aYou have given others a speed boost!");
			for (Minigamer _minigamer : minigamer.getMatch().getAliveMinigamers()) {
				if (_minigamer != minigamer) {
					effectSound(_minigamer);
					applyPotionEffect(_minigamer, PotionEffectType.SPEED, 1);
					_minigamer.tell("&c" + minigamer.getNickname() + " has given you speed boost for 10s!");
				}
			}
		}
	);

	PowerUpUtils.PowerUp SPEED_SELF = new PowerUpUtils.PowerUp("&aSpeed Boost", null,
		new ItemBuilder(Material.SUGAR).glow().build(),
		minigamer -> {
			pickupPowerup(minigamer);

			applyPotionEffect(minigamer, PotionEffectType.SPEED, 1);
			minigamer.tell("&aYou have picked up speed boost for 10s!");
		}
	);

	PowerUpUtils.PowerUp DARKNESS = new PowerUpUtils.PowerUp("&cDarkness", null,
		new ItemBuilder(Material.SCULK).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			minigamer.tell("&aYou have given others darkness!");
			for (Minigamer _minigamer : minigamer.getMatch().getAliveMinigamers()) {
				if (_minigamer != minigamer) {
					effectSound(_minigamer);
					applyPotionEffect(_minigamer, PotionEffectType.DARKNESS, 1);
					_minigamer.tell("&c" + minigamer.getNickname() + " has given you darkness for 10s!");
				}
			}
		}
	);

	PowerUpUtils.PowerUp REVERSE = new PowerUpUtils.PowerUp("&cReverse A Player", null,
		new ItemBuilder(Material.ENDER_EYE).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			Minigamer _minigamer = getRandomOtherMinigamer(minigamer);
			if (_minigamer == null)
				return;

			Player player = _minigamer.getPlayer();
			if (player != null) {
				Location loc = _minigamer.getLocation().clone();
				Vector velocity = player.getVelocity();

				loc.setYaw(loc.getYaw() + 180);
				player.teleport(loc);
				player.setVelocity(velocity);
			}

			effectSound(_minigamer);
			_minigamer.tell("&c" + minigamer.getNickname() + " has reversed you!");
			minigamer.tell("&aYou have reversed " + _minigamer.getNickname() + "!");
		}
	);

	PowerUpUtils.PowerUp SWAP_EVERYONE = new PowerUpUtils.PowerUp("&bEveryone Swaps Places", null,
		new ItemBuilder(Material.ENDER_PEARL).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			List<Minigamer> swapList = new ArrayList<>(minigamer.getMatch().getAliveMinigamers());
			if (swapList.size() == 1)
				return;

			Collections.shuffle(swapList);

			if (swapList.size() % 2 != 0) {
				Minigamer a = swapList.remove(0);
				Minigamer b = swapList.remove(0);
				Minigamer c = swapList.remove(0);

				swapPlaces(a, b, c);
			}

			while (swapList.size() > 0) {
				Minigamer a = swapList.remove(0);
				Minigamer b = swapList.remove(0);

				swapPlaces(a, b);
			}
		}
	);

	PowerUpUtils.PowerUp SWAP_SELF = new PowerUpUtils.PowerUp("&aSwap Places With A Player", null,
		new ItemBuilder(Material.ENDER_PEARL).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			Minigamer _minigamer = getRandomOtherMinigamer(minigamer);
			if (_minigamer == null)
				return;

			swapPlaces(minigamer, _minigamer);
		}
	);

	PowerUpUtils.PowerUp RANDOM_BLOCK_FALL = new PowerUpUtils.PowerUp("&bRandom Falling Blocks", null,
		new ItemBuilder(Material.SAND).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			Match match = minigamer.getMatch();
			FallingBlocksMatchData matchData = match.getMatchData();
			List<Block> blocks = match.worldedit().getBlocks(match.getArena().getRegion("ceiling"));

			int taskId = match.getTasks().repeat(0, TickTime.TICK, () -> {
				Block block = RandomUtils.randomElement(blocks);
				if (!MaterialTag.ALL_AIR.isTagged(block))
					return;

				spawnFallingBlock(match, Material.SAND, block.getLocation(), 0);
			});

			matchData.fallingBlockTasks.add(taskId);
			match.getTasks().wait(TickTime.SECOND.x(10), () -> match.getTasks().cancel(taskId));

			match.broadcast("&bRandom blocks are falling from the sky!");
		}
	);

	PowerUpUtils.PowerUp LINE_THICKENER = new PowerUpUtils.PowerUp("&cLine Thickener", null,
		new ItemBuilder(Material.CAKE).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			Match match = minigamer.getMatch();
			FallingBlocksMatchData matchData = match.getMatchData();
			for (Minigamer _minigamer : match.getAliveMinigamers()) {
				if (_minigamer != minigamer) {
					matchData.thickLines.add(_minigamer);
					effectSound(_minigamer);
					_minigamer.tell("&c" + minigamer.getNickname() + " has thickened your line for 5s!");

					match.getTasks().wait(TickTime.SECOND.x(5), () -> matchData.thickLines.remove(_minigamer));
				}
			}

			minigamer.tell("&aYou thickened other's lines!");
		}
	);

	PowerUpUtils.PowerUp PAUSE_BLOCKS = new PowerUpUtils.PowerUp("&aPause Falling Blocks", null,
		new ItemBuilder(Material.BLUE_ICE).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			Match match = minigamer.getMatch();
			FallingBlocksMatchData matchData = match.getMatchData();

			matchData.pauseBlocks.add(minigamer);
			match.getTasks().wait(TickTime.SECOND.x(10), () -> matchData.pauseBlocks.remove(minigamer));

			minigamer.tell("&aBlocks have stopped falling on you for 10s!");
		}
	);

	@AllArgsConstructor
	private enum Axis {
		X(1),
		Z(1),
		X_NEG(-1),
		Z_NEG(-1);

		@Getter
		final int value;
	}

	PowerUpUtils.PowerUp ADD_LAYER = new PowerUpUtils.PowerUp("&bAdd A Layer", null,
		new ItemBuilder(Material.SNOW).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			Match match = minigamer.getMatch();
			FallingBlocksMatchData matchData = match.getMatchData();

			Region ceiling = match.getArena().getRegion("ceiling");
			List<Block> blocks = match.worldedit().getBlocks(ceiling);

			Axis axis = RandomUtils.randomElement(Axis.values());
			AtomicInteger min;
			AtomicInteger max;
			int value = axis.getValue();
			switch (axis) {
				case X_NEG -> {
					min = new AtomicInteger(ceiling.getMaximumPoint().getBlockX());
					max = new AtomicInteger(ceiling.getMinimumPoint().getBlockX());
				}
				case Z -> {
					min = new AtomicInteger(ceiling.getMinimumPoint().getBlockZ());
					max = new AtomicInteger(ceiling.getMaximumPoint().getBlockZ());
				}
				case Z_NEG -> {
					min = new AtomicInteger(ceiling.getMaximumPoint().getBlockZ());
					max = new AtomicInteger(ceiling.getMinimumPoint().getBlockZ());
				}
				default -> {
					min = new AtomicInteger(ceiling.getMinimumPoint().getBlockX());
					max = new AtomicInteger(ceiling.getMaximumPoint().getBlockX());
				}

			}

			match.broadcast("&bA layer is being added by " + minigamer.getNickname() + "! " + axis.name());
			matchData.addLayerTask.add(match.getTasks().repeat(0, TickTime.TICK.x(5), () -> {
				for (Block block : new ArrayList<>(blocks)) {
					if (!MaterialTag.ALL_AIR.isTagged(block)) {
						blocks.remove(block);
						continue;
					}

					boolean spawnBlock = false;
					switch (axis) {
						case X, X_NEG -> spawnBlock = block.getX() == min.get();
						case Z, Z_NEG -> spawnBlock = block.getZ() == min.get();
					}

					if (spawnBlock) {
						spawnFallingBlock(match, matchData.getColor(minigamer), block.getLocation(), 0);
						blocks.remove(block);
					}
				}

				if (min.getAndAdd(value) >= max.get()) {
					cancelLayerTask(match);
				}
			}));
		}
	);

	private void cancelLayerTask(Match match) {
		FallingBlocksMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.addLayerTask.get(0));
	}

	PowerUpUtils.PowerUp MORE_POWERUPS = new PowerUpUtils.PowerUp("&bSpawn More PowerUps", null,
		new ItemBuilder(Material.COOKIE).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			Match match = minigamer.getMatch();
			int amount = RandomUtils.randomInt(2, 10);
			int wait = 0;
			match.broadcast("&b" + minigamer.getNickname() + " spawned " + amount + " more powerups!");
			for (int i = 0; i < amount; i++) {
				match.getTasks().wait(wait += 5, () -> spawnNextPowerUp(match));
			}
		}
	);

	PowerUpUtils.PowerUp INCONSISTENT_BLOCKS = new PowerUpUtils.PowerUp("&bInconsistent Blocks", null,
		new ItemBuilder(Material.IRON_SHOVEL).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			Match match = minigamer.getMatch();
			FallingBlocksMatchData matchData = match.getMatchData();

			int chance = MathUtils.clamp(matchData.inconsistentChance + RandomUtils.randomInt(10, 25), 0, 100);

			matchData.inconsistentChance = chance;
			match.getTasks().wait(TickTime.SECOND.x(10), () -> matchData.inconsistentChance = MathUtils.clamp(matchData.inconsistentChance - chance, 0, 100));

			match.broadcast("&bBlocks are now dropping inconsistently (" + chance + "%) for 10s!");
		}
	);

	PowerUpUtils.PowerUp TNT_RUN = new PowerUpUtils.PowerUp("&aTNT Run", null,
		new ItemBuilder(Material.LEATHER_BOOTS).glow().build(),

		minigamer -> {
			pickupPowerup(minigamer);

			Match match = minigamer.getMatch();
			FallingBlocksMatchData matchData = match.getMatchData();

			// pause falling blocks
			matchData.pauseBlocks.add(minigamer);

			// tnt run
			int taskId = match.getTasks().repeat(0, TickTime.TICK, () -> {
				if (match.isEnded())
					return;

				Block standingOn = BlockUtils.getBlockStandingOn(minigamer.getOnlinePlayer());
				if (standingOn == null)
					return;

				if (!MaterialTag.CONCRETE_POWDERS.isTagged(standingOn) || standingOn.getType().equals(Material.SAND))
					return;

				match.getTasks().wait(4, () -> {
					if (match.isEnded())
						return;

					standingOn.setType(Material.AIR);
				});
			});

			// cancel tasks
			match.getTasks().wait(TickTime.SECOND.x(10), () -> matchData.pauseBlocks.remove(minigamer));
			match.getTasks().wait(TickTime.SECOND.x(10), () -> match.getTasks().cancel(taskId));
			//

			minigamer.tell("&aYou have picked up TNT Run for 10s!");
		}
	);

	Map<PowerUp, Double> powerUpWeights = new HashMap<>() {{
		put(JUMP, 25.0);
		put(SPEED_SELF, 25.0);

		put(SPEED_OTHERS, 20.0);
		put(DARKNESS, 20.0);

		put(INCONSISTENT_BLOCKS, 17.0);
		put(TNT_RUN, 17.0);

		put(CLEAR_SELF, 15.0);
		put(SWAP_SELF, 15.0);
		put(RANDOM_BLOCK_FALL, 15.0);

		put(PAUSE_BLOCKS, 12.5);
		put(ADD_LAYER, 12.5);
		put(MORE_POWERUPS, 12.5);

		put(CLEAR_ARENA, 10.0);
		put(LINE_THICKENER, 10.0);

		put(REVERSE, 8.0);
		put(SWAP_EVERYONE, 8.0);
	}};


	private void pickupPowerup(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		FallingBlocksMatchData matchData = match.getMatchData();
		matchData.spawnedPowerups -= 1;

		new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).location(minigamer.getLocation()).play();
	}

	private void effectSound(Minigamer minigamer) {
		new SoundBuilder(Sound.ENTITY_ILLUSIONER_CAST_SPELL).receiver(minigamer.getPlayer()).play();
	}

	private void applyPotionEffect(Minigamer minigamer, PotionEffectType type, int amplifier) {
		Player player = minigamer.getPlayer();

		if (player != null) {
			PotionEffect potionEffect = player.getPotionEffect(type);
			if (potionEffect != null) {
				amplifier += potionEffect.getAmplifier();
			}
		}

		minigamer.addPotionEffect(new PotionEffectBuilder(type).duration(TickTime.SECOND.x(10)).amplifier(amplifier));
	}

	private PowerUp getNextPowerUp() {
		return RandomUtils.getWeightedRandom(powerUpWeights);
	}

	private Minigamer getRandomOtherMinigamer(Minigamer self) {
		return RandomUtils.randomElement(self.getMatch().getAliveMinigamers().stream().filter(minigamer -> minigamer != self).toList());
	}

	private void spawnFallingBlock(Match match, Material material, Location location, int radius) {
		FallingBlocksMatchData matchData = match.getMatchData();
		Set<Location> locations = new HashSet<>();
		locations.add(location);

		if (radius > 0) {
			final WorldGuardUtils worldguard = match.getArena().worldguard();
			final ProtectedRegion ceiling = match.getArena().getProtectedRegion("ceiling");

			BlockUtils.getBlocksInRadius(location, radius).forEach(block -> {
				if (!MaterialTag.ALL_AIR.isTagged(block)) {
					return;
				}

				if (!worldguard.isInRegion(block.getLocation(), ceiling)) {
					return;
				}

				locations.add(block.getLocation());
			});
		}

		if (matchData.inconsistentChance != 0) {
			if (RandomUtils.chanceOf(matchData.inconsistentChance))
				return;
		}

		final BlockData blockData = Bukkit.createBlockData(material);
		for (Location spawnLocation : locations) {
			final FallingBlock fallingBlock = match.getWorld().spawnFallingBlock(spawnLocation.toCenterLocation(), blockData);

			fallingBlock.setDropItem(false);
			fallingBlock.setInvulnerable(true);
			fallingBlock.setVelocity(new Vector(0, -0.5, 0));
		}
	}

	private void swapPlaces(Minigamer a, Minigamer b) {
		Player playerA = a.getPlayer();
		Player playerB = b.getPlayer();
		if (playerA != null && playerB != null) {
			Location locA = playerA.getLocation();
			Vector velA = playerA.getVelocity();

			Location locB = b.getLocation();
			Vector velB = playerB.getVelocity();

			effectSound(a);
			a.canTeleport(true);
			playerA.teleport(locB);
			playerA.setVelocity(velB);
			a.tell("&cYou have swapped placed with " + b.getNickname() + "!");
			a.canTeleport(false);

			effectSound(b);
			b.canTeleport(true);
			playerB.teleport(locA);
			playerB.setVelocity(velA);
			b.tell("&cYou have swapped placed with " + a.getNickname() + "!");
			b.canTeleport(false);
		}
	}

	private void swapPlaces(Minigamer a, Minigamer b, Minigamer c) {
		Player playerA = a.getPlayer();
		Player playerB = b.getPlayer();
		Player playerC = c.getPlayer();
		if (playerA == null || playerB == null || playerC == null)
			return;

		Location locA = playerA.getLocation();
		Vector velA = playerA.getVelocity();

		Location locB = b.getLocation();
		Vector velB = playerB.getVelocity();

		Location locC = c.getLocation();
		Vector velC = playerC.getVelocity();


		effectSound(a);
		a.canTeleport(true);
		playerA.teleport(locC);
		playerA.setVelocity(velC);
		a.tell("&cYou have swapped placed with " + c.getNickname() + "!");
		a.canTeleport(false);

		effectSound(b);
		b.canTeleport(true);
		playerB.teleport(locA);
		playerB.setVelocity(velA);
		b.tell("&cYou have swapped placed with " + a.getNickname() + "!");
		b.canTeleport(false);

		effectSound(c);
		c.canTeleport(true);
		playerC.teleport(locB);
		playerC.setVelocity(velB);
		c.tell("&cYou have swapped placed with " + b.getNickname() + "!");
		c.canTeleport(false);
	}

	@EventHandler
	public void on(EntitySpawnEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType() != EntityType.FALLING_BLOCK)
			return;

		if (!(entity instanceof FallingBlock fallingBlock))
			return;

		if (!Minigames.isMinigameWorld(entity.getWorld()))
			return;

		Arena arena = ArenaManager.getFromLocation(event.getLocation());
		if (arena == null)
			return;

		Match match = MatchManager.find(arena);
		if (match == null)
			return;

		fallingBlock.setDropItem(false);
		fallingBlock.setInvulnerable(true);
	}

}
