package me.pugabyte.nexus.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.DelayedRegionOverlapAssociation;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegistryFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.AllArgsConstructor;
import me.pugabyte.nexus.Nexus;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

public class WorldGuardFlagUtils {

	@AllArgsConstructor
	public enum Flags {
		SNOW_EFFECT(registerFlag(new StateFlag("snow-effect", false))),
		HANGING_BREAK(registerFlag(new StateFlag("hanging-break", false))),
		GRASS_DECAY(registerFlag(new StateFlag("grass-decay", false))),
		HOSTILE_SPAWN(registerFlag(new StateFlag("hostile-spawn", false))),
		ALLOW_SPAWN(registerFlag(new SetFlag("allow-spawn", new RegistryFlag(null, com.sk89q.worldedit.world.entity.EntityType.REGISTRY)))),
		MOB_AGGRESSION(registerFlag(new StateFlag("mob-aggression", false))),
		TAMING(registerFlag(new StateFlag("taming", false))),
		USE_TRAP_DOORS(registerFlag(new StateFlag("use-trap-doors", false))),
		MINIGAMES_WATER_DAMAGE(registerFlag(new StateFlag("minigames-water-damage", false)));

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


	public static boolean test(Player player, Flags flag) {
		return test(player, (StateFlag) flag.get());
	}

	public static boolean test(Player player, StateFlag flag) {
		Validate.notNull(flag, "Flag cannot be null");

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		Location loc = BukkitAdapter.adapt(player.getLocation());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		return container.createQuery().testState(loc, localPlayer, flag);
	}

	public static boolean test(org.bukkit.Location location, Flags flag) {
		return test(location, (StateFlag) flag.get());
	}

	public static boolean test(org.bukkit.Location location, StateFlag flag) {
		Validate.notNull(flag, "Flag cannot be null");

		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.testState(loc, new DelayedRegionOverlapAssociation(query, loc), flag);
	}

	public static State query(Player player, Flags flag) {
		return query(player, (StateFlag) flag.get());
	}

	public static State query(Player player, StateFlag flag) {
		Validate.notNull(flag, "Flag cannot be null");

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		Location loc = BukkitAdapter.adapt(player.getLocation());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.queryState(loc, new DelayedRegionOverlapAssociation(query, loc), flag);
	}

	public static State query(org.bukkit.Location location, Flags flag) {
		return query(location, (StateFlag) flag.get());
	}

	public static State query(Block block, Flags flag) {
		return query(block.getLocation(), (StateFlag) flag.get());
	}

	public static State query(org.bukkit.Location location, StateFlag flag) {
		Validate.notNull(flag, "Flag cannot be null");

		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.queryState(loc, new DelayedRegionOverlapAssociation(query, loc), flag);
	}

	public static Set<EntityType> queryValue(org.bukkit.Location location, Flags flag) {
		Validate.notNull(flag, "Flag cannot be null");

		Location loc = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.queryValue(loc, Associables.constant(Association.NON_MEMBER), (SetFlag<EntityType>) Flags.ALLOW_SPAWN.get());
	}

}
