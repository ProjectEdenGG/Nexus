package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.FallingBlocksMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.utils.PowerUpUtils;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

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
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);

		Match match = event.getMatch();
		FallingBlocksMatchData matchData = match.getMatchData();

		match.getTasks().cancel(matchData.fallingBlockTask);
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
				if (!MaterialTag.ALL_AIR.isTagged(location.getBlock()))
					continue;

				spawnFallingBlock(match, matchData.getColor(minigamer), location);
			}
		});

		// power ups: dynamic spawning locations
		PowerUpUtils powerUpUtils = new PowerUpUtils(match, powerUps);
		match.getTasks().repeat(TickTime.SECOND.x(10), TickTime.SECOND.x(5), () -> {
			if (matchData.spawnedPowerups == FallingBlocksMatchData.MAX_POWER_UPS)
				return;

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

			powerUpUtils.spawn(location, false, null);
			matchData.spawnedPowerups += 1;
		});
	}

	// Auto-select unique color for players who have not themselves
	private void setPlayerBlocks(List<Minigamer> minigamers, Match match) {
		FallingBlocksMatchData matchData = match.getMatchData();

		for (Minigamer minigamer : minigamers) {
			Material colorType = matchData.getColor(minigamer);
			if (colorType == null) {
				Material next = matchData.getAvailableColorId();
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

		if (event.getRegion().getId().contains("ceiling"))
			minigamer.scored();
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

				if (!matchData.containsColor(colorType)) {
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

			if (matchData.containsColor(colorItem.getType())) {
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

	// TODO: Powerup particle & sounds, weights

	PowerUpUtils.PowerUp CLEAR_ARENA = new PowerUpUtils.PowerUp("&bClear Arena", null,
		new ItemBuilder(Material.TNT).glow().build(),
		minigamer -> {
			clearArena(minigamer.getMatch(), null);
			minigamer.getMatch().broadcast("&bThe arena has been cleared by " + minigamer.getNickname());
			//
			decPowerups(minigamer.getMatch());
		}
	);

	PowerUpUtils.PowerUp CLEAR_SELF = new PowerUpUtils.PowerUp("&aClear Self", null,
		new ItemBuilder(Material.FLINT_AND_STEEL).glow().build(),

		minigamer -> {
			FallingBlocksMatchData matchData = minigamer.getMatch().getMatchData();
			Material material = matchData.getColor(minigamer);
			clearArena(minigamer.getMatch(), material);
			minigamer.tell("&aYou have cleared the arena of " + StringUtils.camelCase(material) + "!");
			//
			decPowerups(minigamer.getMatch());
		}
	);

	PowerUpUtils.PowerUp JUMP = new PowerUpUtils.PowerUp("&aJump Boost", null,
		new ItemBuilder(Material.LEATHER_BOOTS).glow().build(),
		minigamer -> {
			minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.JUMP).duration(TickTime.SECOND.x(10)).amplifier(2));
			minigamer.tell("&aYou have picked up jump boost for 10s!");
			//
			decPowerups(minigamer.getMatch());
		}
	);

	PowerUpUtils.PowerUp SPEED = new PowerUpUtils.PowerUp("&cSpeed Boost", null,
		new ItemBuilder(Material.SUGAR).glow().build(),
		minigamer -> {
			minigamer.tell("&aYou have given everyone a speed boost!");
			for (Minigamer _minigamer : minigamer.getMatch().getMinigamers()) {
				if (_minigamer != minigamer) {
					_minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.SPEED).duration(TickTime.SECOND.x(10)).amplifier(1));
					_minigamer.tell("&c" + minigamer.getNickname() + " has given you speed boost for 10s!");
				}
			}
			//
			decPowerups(minigamer.getMatch());
		}
	);

	PowerUpUtils.PowerUp DARKNESS = new PowerUpUtils.PowerUp("&cDarkness", null,
		new ItemBuilder(Material.SCULK).glow().build(),

		minigamer -> {
			minigamer.tell("&cYou have given everyone darkness!");
			for (Minigamer _minigamer : minigamer.getMatch().getMinigamers()) {
				if (_minigamer != minigamer) {
					_minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.DARKNESS).duration(TickTime.SECOND.x(10)).amplifier(1));
					_minigamer.tell("&c" + minigamer.getNickname() + " has given you darkness for 10s!");
				}
			}
			//
			decPowerups(minigamer.getMatch());
		}
	);

	PowerUpUtils.PowerUp REVERSE = new PowerUpUtils.PowerUp("&cReverse A Player", null,
		new ItemBuilder(Material.ENDER_PEARL).glow().build(),

		minigamer -> {
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

			_minigamer.tell("&c" + minigamer.getNickname() + " has reversed you!");
			minigamer.tell("&aYou have reversed " + _minigamer.getNickname() + "!");
			//
			decPowerups(minigamer.getMatch());
		}
	);

	PowerUpUtils.PowerUp RANDOM_BLOCK_FALL = new PowerUpUtils.PowerUp("&bRandom Falling Blocks", null,
		new ItemBuilder(Material.SAND).glow().build(),

		minigamer -> {
			Match match = minigamer.getMatch();
			FallingBlocksMatchData matchData = match.getMatchData();
			List<Block> blocks = match.worldedit().getBlocks(match.getArena().getRegion("ceiling"));
			matchData.fallingBlockTask = match.getTasks().repeat(0, TickTime.TICK, () -> {
				Block block = RandomUtils.randomElement(blocks);
				if (!MaterialTag.ALL_AIR.isTagged(block))
					return;

				spawnFallingBlock(match, Material.SAND, block.getLocation());
			});

			match.getTasks().wait(TickTime.SECOND.x(10), () -> match.getTasks().cancel(matchData.fallingBlockTask));

			match.broadcast("&bRandom blocks are falling from the sky!");
			//
			decPowerups(match);
		}
	);

	List<PowerUpUtils.PowerUp> powerUps = Arrays.asList(CLEAR_ARENA, CLEAR_SELF, JUMP, SPEED, DARKNESS, REVERSE, RANDOM_BLOCK_FALL);

	private void decPowerups(Match match) {
		((FallingBlocksMatchData) match.getMatchData()).spawnedPowerups -= 1;
	}

	private Minigamer getRandomOtherMinigamer(Minigamer self) {
		return RandomUtils.randomElement(self.getMatch().getAliveMinigamers().stream().filter(minigamer -> minigamer != self).toList());
	}

	private void spawnFallingBlock(Match match, Material material, Location location) {
		final BlockData blockData = Bukkit.createBlockData(material);
		final FallingBlock fallingBlock = match.getWorld().spawnFallingBlock(location.toCenterLocation(), blockData);

		fallingBlock.setDropItem(false);
		fallingBlock.setInvulnerable(true);
		fallingBlock.setVelocity(new Vector(0, -0.5, 0));
	}

}
