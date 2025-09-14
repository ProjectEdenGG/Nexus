package gg.projecteden.nexus.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.RegistryFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.parchment.HasPlayer;
import gg.projecteden.parchment.OptionalPlayer;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities for getting the state
 */
public class WorldGuardFlagUtils {

	/**
	 * Nexus's custom World Guard flags
	 */
	@AllArgsConstructor
	public enum CustomFlags {
		/**
		 * GSit's "Sit" Flag, don't register it!
		 */
		GSIT_SIT(new StateFlag("sit", true)),
		/**
		 * Enables showing the Pugmas snow effect to players in the region
		 */
		SNOW_EFFECT(registerFlag(new StateFlag("snow-effect", false))),
		/**
		 * Whether or not to allow item frames to break via means besides being destroyed by a player
		 */
		HANGING_BREAK(registerFlag(new StateFlag("hanging-break", false))),
		/**
		 * Toggles grass blocks becoming dirt
		 */
		GRASS_DECAY(registerFlag(new StateFlag("grass-decay", false))),
		/**
		 * Toggles grass blocks becoming dirt
		 */
		SAPLING_GROWTH(registerFlag(new StateFlag("sapling-growth", false))),
		/**
		 * Toggles hostile mob spawning
		 */
		HOSTILE_SPAWN(registerFlag(new StateFlag("hostile-spawn", false))),
		/**
		 * Allows a set of mobs to spawn
		 */
		ALLOW_SPAWN(registerFlag(new SetFlag("allow-spawn", new RegistryFlag(null, com.sk89q.worldedit.world.entity.EntityType.REGISTRY)))),
		/**
		 * Toggles mobs getting angry at entities
		 */
		MOB_AGGRESSION(registerFlag(new StateFlag("mob-aggression", false))),
		/**
		 * Toggles if players can tame animals
		 */
		TAMING(registerFlag(new StateFlag("taming", false))),
		/**
		 * Toggles if players can use trapdoors
		 */
		USE_TRAP_DOORS(registerFlag(new StateFlag("use-trap-doors", false))),
		/**
		 * Toggles if players can use fence gates
		 */
		USE_FENCE_GATES(registerFlag(new StateFlag("use-fence-gates", false))),
		/**
		 * Toggles if players can use note blocks
		 */
		USE_NOTE_BLOCKS(registerFlag(new StateFlag("use-note-blocks", false))),
		/**
		 * Sets which inventories a player can access (i.e. chest, furnace, shulker box, hopper, etc.)
		 */
		ALLOWED_BLOCK_INVENTORIES(registerFlag(new SetFlag<>("allowed-block-inventories", new StringFlag(null)))),
		/**
		 * Sets which inventories a player can access (i.e. chest, furnace, shulker box, hopper, etc.)
		 */
		VIRTUAL_BLOCK_INVENTORIES(registerFlag(new SetFlag<>("virtual-block-inventories", new StringFlag(null)))),
		/**
		 * Toggles if players get damaged while inside water
		 */
		MINIGAMES_WATER_DAMAGE(registerFlag(new StateFlag("minigames-water-damage", false))),
		/**
		 * Displays a message on a player's action bar upon entry
		 */
		GREETING_ACTIONBAR(registerFlag(new StringFlag("nexus-greeting-actionbar"))),
		/**
		 * Displays a message on a player's action bar upon exit
		 */
		FAREWELL_ACTIONBAR(registerFlag(new StringFlag("nexus-farewell-actionbar"))),
		/**
		 * Sets how long action bar messages should last in ticks
		 */
		ACTIONBAR_TICKS(registerFlag(new IntegerFlag("nexus-actionbar-ticks"))),
		/**
		 * Displays a title to a player upon entry
		 */
		GREETING_TITLE(registerFlag(new StringFlag("nexus-greeting-title"))),
		/**
		 * Displays a title to a player upon exit
		 */
		FAREWELL_TITLE(registerFlag(new StringFlag("nexus-farewell-title"))),
		/**
		 * Displays a subtitle to a player upon entry
		 */
		GREETING_SUBTITLE(registerFlag(new StringFlag("nexus-greeting-subtitle"))),
		/**
		 * Displays a subtitle to a player upon exit
		 */
		FAREWELL_SUBTITLE(registerFlag(new StringFlag("nexus-farewell-subtitle"))),
		/**
		 * Sets how long (sub)title messages should last in ticks
		 */
		TITLE_TICKS(registerFlag(new IntegerFlag("nexus-title-ticks"))),
		/**
		 * Sets how long (sub)title messages should fade in and out in ticks
		 */
		TITLE_FADE(registerFlag(new IntegerFlag("nexus-title-fade"))),
		DOUBLE_JUMP(registerFlag(new StateFlag("double-jump", false))),
		DOUBLE_JUMP_COOLDOWN(registerFlag(new IntegerFlag("double-jump-cooldown"))),
		BLOCK_GROW(registerFlag(new StateFlag("block-grow", false))),
		BLOCK_FADE(registerFlag(new StateFlag("block-fade", false))),
		SWEET_BERRY_BUSH_DAMAGE(registerFlag(new StateFlag("sweet-berry-bush-damage", false))),
		;

		public final Flag<?> flag;

		public <T extends Flag<?>> T get() {
			return (T) flag;
		}

		public static void register() {
			// static init
		}
	}

	public static final SimpleFlagRegistry registry = (SimpleFlagRegistry) WorldGuard.getInstance().getFlagRegistry();

	public static Flag<?> registerFlag(Flag<?> flag) {
//		removeFlags();

		if (WorldGuardUtils.plugin == null || registry == null) {
			Nexus.warn("Could not find WorldGuard, aborting registry of flag " + flag.getName());
			return null;
		}

		try {
			boolean fix = registry.isInitialized();

			try {
				if (fix) registry.setInitialized(false);
				registry.register(flag);
			} catch (FlagConflictException duplicate) {
				flag = registry.get(flag.getName());
			} finally {
				if (fix) registry.setInitialized(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		return flag;
	}

//	private static boolean removedFlags;
//
//	private static void removeFlags() {
//		if (!removedFlags) {
//			removeFlag("greeting-title");
//			removeFlag("farewell-title");
//			removedFlags = true;
//		}
//	}
//
//	public static void removeFlag(String name) {
//		try {
//			Field field = registry.getClass().getDeclaredField("flags");
//			field.setAccessible(true);
//			ConcurrentMap<String, Flag<?>> flags = (ConcurrentMap<String, Flag<?>>) field.get(registry);
//			flags.remove(name);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}

	@Nullable
	private static LocalPlayer getLocalPlayer(@Nullable OptionalPlayer player) {
		if (player != null && player.getPlayer() != null)
			return WorldGuardPlugin.inst().wrapPlayer(player.getPlayer());
		return null;
	}

	// Main Methods //

	/**
	 * Test if the effective value for a list of flags is {@code ALLOW}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param location location of interaction
	 * @param associable object to check for region membership
	 * @param flags flags to check
	 * @return true if the result was {@code ALLOW}
	 * @see #hasBypass(HasPlayer)
	 */
	public static boolean test(@NotNull org.bukkit.Location location, @Nullable RegionAssociable associable, @NotNull StateFlag... flags) {
		Validate.notNull(location, "Location cannot be null");
		Validate.notNull(flags, "Flags cannot be null");

		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		return container.createQuery().testState(loc, associable, flags);
	}

	/**
	 * Gets the effective state of a flag. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * @param location location of interaction
	 * @param associable object to check for region membership
	 * @param flags flags to check
	 * @return effective state
	 */
	public static State query(@NotNull org.bukkit.Location location, @Nullable RegionAssociable associable, @NotNull StateFlag... flags) {
		Validate.notNull(location, "Location cannot be null");
		Validate.notNull(flags, "Flags cannot be null");

		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		return container.createQuery().queryState(loc, associable, flags);
	}

	/**
	 * Gets the effective value of a flag.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param location location of interaction
	 * @param associable object to check for region membership
	 * @param flag flag to check
	 * @return effective value
	 * @see #hasBypass(HasPlayer)
	 */
	public static <T> T queryValue(@NotNull org.bukkit.Location location, @Nullable RegionAssociable associable, @NotNull Flag<T> flag) {
		Validate.notNull(location, "Location cannot be null");
		Validate.notNull(flag, "Flag cannot be null");

		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		return container.createQuery().queryValue(loc, associable, flag);
	}

	// Overloads //

	/**
	 * Test if the effective value of a flag is {@code ALLOW}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param location location of interaction
	 * @param player player to check region membership, assumes non-membership if null
	 * @param flag flag to check
	 * @return true if the result was {@code ALLOW}
	 * @see #hasBypass(HasPlayer)
	 */
	public static boolean test(@NotNull org.bukkit.Location location, @Nullable OptionalPlayer player, CustomFlags flag) {
		Validate.notNull(flag, "Flag cannot be null");
		return test(location, player, (StateFlag) flag.get());
	}

	/**
	 * Test if the effective value of a flag is {@code ALLOW}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param player player to check region membership and location
	 * @param flag flag to check
	 * @return true if the result was {@code ALLOW}
	 * @see #hasBypass(HasPlayer)
	 */
	public static boolean test(@NotNull HasPlayer player, CustomFlags flag) {
		Validate.notNull(player, "Player cannot be null");
		Validate.notNull(flag, "Flag cannot be null");
		return test(player.getPlayer().getLocation(), player, (StateFlag) flag.get());
	}

	/**
	 * Test whether the (effective) value for a list of state flags equals {@code ALLOW}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param location location of interaction
	 * @param player player to check region membership, assumes non-membership if null
	 * @param flags flags to check
	 * @return true if the result was {@code ALLOW}
	 * @see #hasBypass(HasPlayer)
	 */
	public static boolean test(@NotNull org.bukkit.Location location, @Nullable OptionalPlayer player, @NotNull StateFlag... flags) {
		return test(location, getLocalPlayer(player), flags);
	}

	/**
	 * Test whether the (effective) value for a list of state flags equals {@code ALLOW}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param player player to check region membership and location
	 * @param flags flags to check
	 * @return true if the result was {@code ALLOW}
	 * @see #hasBypass(HasPlayer)
	 */
	public static boolean test(@NotNull HasPlayer player, @NotNull StateFlag... flags) {
		Validate.notNull(player, "Player cannot be null");
		return test(player.getPlayer().getLocation(), player, flags);
	}

	/**
	 * Test if the effective value of a flag is {@code ALLOW}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param location location to check from
	 * @param flag flag to check
	 * @return true if the result was {@code ALLOW}
	 */
	public static boolean test(@NotNull org.bukkit.Location location, @NotNull WorldGuardFlagUtils.CustomFlags flag) {
		Validate.notNull(flag, "Flag cannot be null");
		return test(location, (StateFlag) flag.get());
	}

	/**
	 * Test whether the (effective) value for a list of state flags equals {@code ALLOW}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param location location to check from
	 * @param flags flags to check
	 * @return true if the result was {@code ALLOW}
	 */
	public static boolean test(@NotNull org.bukkit.Location location, @NotNull StateFlag... flags) {
		return test(location, Associables.constant(Association.OWNER), flags);
	}

	/**
	 * Test if the effective value of a flag is {@code ALLOW}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param block location to check from
	 * @param flag flag to check
	 * @return true if the result was {@code ALLOW}
	 */
	public static boolean test(@NotNull Block block, @NotNull WorldGuardFlagUtils.CustomFlags flag) {
		Validate.notNull(block, "Block cannot be null");
		return test(block.getLocation(), flag);
	}

	/**
	 * Test whether the (effective) value for a list of state flags equals {@code ALLOW}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param block location to check from
	 * @param flags flags to check
	 * @return true if the result was {@code ALLOW}
	 */
	public static boolean test(@NotNull Block block, @NotNull StateFlag... flags) {
		Validate.notNull(block, "Block cannot be null");
		return test(block.getLocation(), flags);
	}

	/**
	 * Gets the effective state of a flag. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param location location of interaction
	 * @param player player to check region membership, assumes non-membership if null
	 * @param flag flag to check
	 * @return effective state
	 * @see #hasBypass(HasPlayer)
	 */
	public static State query(@NotNull org.bukkit.Location location, @Nullable OptionalPlayer player, CustomFlags flag) {
		Validate.notNull(flag, "Flag cannot be null");
		return query(location, player, (StateFlag) flag.get());
	}

	/**
	 * Gets the effective state of a flag. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param player player to check region membership and location
	 * @param flag flag to check
	 * @return effective state
	 * @see #hasBypass(HasPlayer)
	 */
	public static State query(@NotNull HasPlayer player, CustomFlags flag) {
		Validate.notNull(player, "Player cannot be null");
		Validate.notNull(flag, "Flag cannot be null");
		return query(player.getPlayer().getLocation(), player, (StateFlag) flag.get());
	}

	/**
	 * Gets the effective state of a list of flags. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param location location of interaction
	 * @param player player to check region membership, assumes non-membership if null
	 * @param flags flags to check
	 * @return effective state
	 * @see #hasBypass(HasPlayer)
	 */
	public static State query(@NotNull org.bukkit.Location location, @Nullable OptionalPlayer player, @NotNull StateFlag... flags) {
		return query(location, getLocalPlayer(player), flags);
	}

	/**
	 * Gets the effective state of a list of flags. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param player player to check region membership and location
	 * @param flags flags to check
	 * @return effective state
	 * @see #hasBypass(HasPlayer)
	 */
	public static State query(@NotNull HasPlayer player, @NotNull StateFlag... flags) {
		Validate.notNull(player, "Player cannot be null");
		return query(player.getPlayer().getLocation(), player, flags);
	}

	/**
	 * Gets the effective state of a flag. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param location location of interaction
	 * @param flag flags to check
	 * @return effective state
	 */
	public static State query(@NotNull org.bukkit.Location location, @NotNull WorldGuardFlagUtils.CustomFlags flag) {
		Validate.notNull(flag, "Flag cannot be null");
		return query(location, (StateFlag) flag.get());
	}

	/**
	 * Gets the effective state of a list of flags. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param location location of interaction
	 * @param flags flags to check
	 * @return effective state
	 */
	public static State query(@NotNull org.bukkit.Location location, @NotNull StateFlag... flags) {
		return query(location, Associables.constant(Association.OWNER), flags);
	}

	/**
	 * Gets the effective state of a flag. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param block location of interaction
	 * @param flag flags to check
	 * @return effective state
	 */
	public static State query(@NotNull Block block, @NotNull WorldGuardFlagUtils.CustomFlags flag) {
		Validate.notNull(block, "Block cannot be null");
		return query(block.getLocation(), flag);
	}

	/**
	 * Gets the effective state of a list of flags. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param block location of interaction
	 * @param flags flags to check
	 * @return effective state
	 */
	public static State query(@NotNull Block block, @NotNull StateFlag... flags) {
		Validate.notNull(block, "Block cannot be null");
		return query(block.getLocation(), flags);
	}

	/**
	 * Gets the effective value of a flag.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param location location of interaction
	 * @param player player to check region membership, assumes non-membership if null
	 * @param flag flag to check
	 * @return effective value
	 * @see #hasBypass(HasPlayer)
	 */
	public static <T> T queryValue(@NotNull org.bukkit.Location location, @Nullable OptionalPlayer player, CustomFlags flag) {
		Validate.notNull(flag, "Flag cannot be null");
		return queryValue(location, player, (Flag<T>) flag.get());
	}

	/**
	 * Gets the effective value of a flag.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param player player to check region membership and location
	 * @param flag flag to check
	 * @return effective value
	 * @see #hasBypass(HasPlayer)
	 */
	public static <T> T queryValue(@NotNull HasPlayer player, CustomFlags flag) {
		Validate.notNull(player, "Player cannot be null");
		Validate.notNull(flag, "Flag cannot be null");
		return queryValue(player.getPlayer().getLocation(), player, (Flag<T>) flag.get());
	}

	/**
	 * Gets the effective value of a flag.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param location location of interaction
	 * @param player player to check region membership, assumes non-membership if null
	 * @param flag flag to check
	 * @return effective value
	 * @see #hasBypass(HasPlayer)
	 */
	public static <T> T queryValue(@NotNull org.bukkit.Location location, @Nullable OptionalPlayer player, @NotNull Flag<T> flag) {
		return queryValue(location, getLocalPlayer(player), flag);
	}

	/**
	 * Gets the effective value of a flag.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param player player to check region membership and location
	 * @param flag flag to check
	 * @return effective value
	 * @see #hasBypass(HasPlayer)
	 */
	public static <T> T queryValue(@NotNull HasPlayer player, @NotNull Flag<T> flag) {
		Validate.notNull(player, "Player cannot be null");
		return queryValue(player.getPlayer().getLocation(), player, flag);
	}

	/**
	 * Gets the effective value of a flag.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param location location to check from
	 * @param flag flag to check
	 * @return effective value
	 */
	public static <T> T queryValue(@NotNull org.bukkit.Location location, @NotNull WorldGuardFlagUtils.CustomFlags flag) {
		Validate.notNull(flag, "Flag cannot be null");
		return queryValue(location, flag.get());
	}

	/**
	 * Gets the effective value of a flag.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param location location to check from
	 * @param flag flag to check
	 * @return effective value
	 */
	public static <T> T queryValue(@NotNull org.bukkit.Location location, @NotNull Flag<T> flag) {
		return queryValue(location, Associables.constant(Association.OWNER), flag);
	}

	/**
	 * Gets the effective value of a flag.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param block location to check from
	 * @param flag flag to check
	 * @return effective value
	 */
	public static <T> T queryValue(@NotNull Block block, @NotNull Flag<T> flag) {
		Validate.notNull(block, "Block cannot be null");
		return queryValue(block.getLocation(), flag);
	}

	/**
	 * Gets the effective value of a flag.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param block location to check from
	 * @param flag flag to check
	 * @return effective value
	 */
	public static <T> T queryValue(@NotNull Block block, @NotNull WorldGuardFlagUtils.CustomFlags flag) {
		Validate.notNull(block, "Block cannot be null");
		return queryValue(block.getLocation(), flag);
	}

	/**
	 * Tests if a player bypasses world guard flags
	 * @param player player
	 * @return if the player bypasses world guard flags
	 */
	public static boolean hasBypass(@NotNull HasPlayer player) {
		Validate.notNull(player, "Player cannot be null");
		Player _player = player.getPlayer();
		return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(WorldGuardPlugin.inst().wrapPlayer(_player), BukkitAdapter.adapt(_player.getWorld()));
	}

	/**
	 * Tests if a player can place blocks in their current region(s)
	 * @param location location of block placement
	 * @return true if the player can place blocks
	 */
	public static boolean canPlace(@NotNull HasPlayer player, @NotNull org.bukkit.Location location) {
		Validate.notNull(player, "Player cannot be null");
		Validate.notNull(location, "Location cannot be null");
		return hasBypass(player) || test(location, player, com.sk89q.worldguard.protection.flags.Flags.BUILD, com.sk89q.worldguard.protection.flags.Flags.BLOCK_PLACE);
	}

	/**
	 * Tests if a player can place blocks in their current region(s)
	 * @param block location of block placement
	 * @return true if the player can place blocks
	 */
	public static boolean canPlace(@NotNull HasPlayer player, @NotNull Block block) {
		Validate.notNull(block, "Block cannot be null");
		return canPlace(player, block.getLocation());
	}

	/**
	 * Tests if a player can place blocks in their current region(s) at their feet
	 * @return true if the player can place blocks
	 */
	public static boolean canPlace(@NotNull HasPlayer player) {
		Validate.notNull(player, "Player cannot be null");
		return canPlace(player, player.getPlayer().getLocation());
	}

	/**
	 * Tests if a player can break blocks in their current region(s)
	 * @param location location of block breaking
	 * @return true if the player can break blocks
	 */
	public static boolean canBreak(@NotNull HasPlayer player, @NotNull org.bukkit.Location location) {
		Validate.notNull(player, "Player cannot be null");
		Validate.notNull(location, "Location cannot be null");
		return hasBypass(player) || test(location, player, com.sk89q.worldguard.protection.flags.Flags.BUILD, com.sk89q.worldguard.protection.flags.Flags.BLOCK_BREAK);
	}

	/**
	 * Tests if a player can break blocks in their current region(s)
	 * @param block location of block breaking
	 * @return true if the player can break blocks
	 */
	public static boolean canBreak(@NotNull HasPlayer player, @NotNull Block block) {
		Validate.notNull(block, "Location cannot be null");
		return canBreak(player, block.getLocation());
	}

	/**
	 * Tests if a player can break blocks in their current region(s) at their feet
	 * @return true if the player can break blocks
	 */
	public static boolean canBreak(@NotNull HasPlayer player) {
		Validate.notNull(player, "Player cannot be null");
		return canBreak(player, player.getPlayer().getLocation());
	}

	public static String command(World world, Flag<?> flag, State state) {
		return "rg flag -w \"" + world.getName() + "\" __global__ " + flag.getName() + " " + state.name();
	}

}
