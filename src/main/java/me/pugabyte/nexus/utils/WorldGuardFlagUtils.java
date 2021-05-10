package me.pugabyte.nexus.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.association.Associables;
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
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.AllArgsConstructor;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.Nexus;
import org.apache.commons.lang.Validate;
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
	public enum Flags {
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
		;

		public final Flag<?> flag;

		public Flag<?> get() {
			return flag;
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
	public static boolean test(HasPlayer player, Flags flag) {
		return test(player, (StateFlag) flag.get());
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
		Validate.notNull(flags, "Flag cannot be null");

		Player _player = player.getPlayer();
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(_player);
		Location loc = BukkitAdapter.adapt(_player.getLocation());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		return container.createQuery().testState(loc, localPlayer, flags);
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
	public static boolean test(org.bukkit.Location location, Flags flag) {
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
	public static boolean test(org.bukkit.Location location, StateFlag... flags) {
		Validate.notNull(flags, "Flag cannot be null");

		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.testState(loc, Associables.constant(Association.OWNER), flags);
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
	@Nullable
	public static State query(HasPlayer player, Flags flag) {
		return query(player, (StateFlag) flag.get());
	}

	/**
	 * Gets the effective state for a list of flags. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method does not check the region bypass permission. That must be done by the calling code.</p>
	 *
	 * @param player player to check region membership and location
	 * @param flags flags to check
	 * @return effective state
	 * @see #hasBypass(HasPlayer)
	 */
	@Nullable
	public static State query(HasPlayer player, StateFlag... flags) {
		Validate.notNull(flags, "Flag cannot be null");

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player.getPlayer());
		Location loc = BukkitAdapter.adapt(player.getPlayer().getLocation());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.queryState(loc, localPlayer, flags);
	}

	/**
	 * Gets the effective state of a flag. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param location location to check from
	 * @param flag flag to check
	 * @return effective state
	 */
	@Nullable
	public static State query(org.bukkit.Location location, Flags flag) {
		return query(location, (StateFlag) flag.get());
	}

	/**
	 * Gets the effective state of a flag. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param block location to check from
	 * @param flag flag to check
	 * @return effective state
	 */
	@Nullable
	public static State query(Block block, Flags flag) {
		return query(block.getLocation(), (StateFlag) flag.get());
	}

	/**
	 * Gets the effective state for a list of flags. This follows state rules, where {@code DENY} overrides
	 * {@code ALLOW} overrides {@code NONE}.
	 *
	 * <p>This method assumes it is being run by the owner of a region.</p>
	 *
	 * @param location location to check from
	 * @param flags flags to check
	 * @return effective state
	 */
	@Nullable
	public static State query(org.bukkit.Location location, StateFlag... flags) {
		Validate.notNull(flags, "Flag cannot be null");

		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.queryState(loc, Associables.constant(Association.OWNER), flags);
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
	@Nullable
	public static <T> T queryValue(org.bukkit.Location location, Flags flag) {
		return queryValue(location, (Flag<T>) flag.get());
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
	@Nullable
	public static <T> T queryValue(org.bukkit.Location location, Flag<T> flag) {
		Validate.notNull(flag, "Flag cannot be null");

		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.queryValue(loc, Associables.constant(Association.OWNER), flag);
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
	@Nullable
	public static <T> T getValueFor(HasPlayer player, Flag<T> flag) {
		Player _player = player.getPlayer();
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(_player);
		com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(_player.getLocation());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		return container.createQuery().queryValue(loc, localPlayer, flag);
	}

	/**
	 * Tests if a player bypasses world guard flags
	 * @param player player
	 * @return if the player bypasses world guard flags
	 */
	public static boolean hasBypass(HasPlayer player) {
		Player _player = player.getPlayer();
		return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(WorldGuardPlugin.inst().wrapPlayer(_player), BukkitAdapter.adapt(_player.getWorld()));
	}

	/**
	 * Tests if a player can place blocks in their current region(s)
	 * @return true if the player can place blocks
	 */
	public static boolean canPlace(HasPlayer player) {
		// TODO: test block location too (requires some rewriting)
		return hasBypass(player) || test(player, com.sk89q.worldguard.protection.flags.Flags.BUILD, com.sk89q.worldguard.protection.flags.Flags.BLOCK_PLACE);
	}

	/**
	 * Tests if a player can break blocks in their current region(s)
	 * @return true if the player can break blocks
	 */
	public static boolean canBreak(HasPlayer player) {
		return hasBypass(player) || test(player, com.sk89q.worldguard.protection.flags.Flags.BUILD, com.sk89q.worldguard.protection.flags.Flags.BLOCK_BREAK);
	}

}
