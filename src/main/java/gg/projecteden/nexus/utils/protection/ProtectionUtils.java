package gg.projecteden.nexus.utils.protection;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.protection.compatibilities.BentoBoxCompat;
import gg.projecteden.nexus.utils.protection.compatibilities.PlotSquaredCompat;
import gg.projecteden.nexus.utils.protection.compatibilities.WorldGuardCompat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ProtectionUtils {
	@Getter
	private final static Set<ProtectionCompatibility> compatibilities = new HashSet<>();

	@SuppressWarnings({"Convert2MethodRef", "CallToPrintStackTrace"})
	public static void init(JavaPlugin plugin) {
		try {
			handleCompatibility("WorldGuard", plugin, (m, p) -> new WorldGuardCompat(m, p));
		} catch (Exception | NoClassDefFoundError ex) {
			Debug.log(ex);
		}

		try {
			handleCompatibility("PlotSquared", plugin, (m, p) -> new PlotSquaredCompat(m, p));
		} catch (Exception | NoClassDefFoundError ex) {
			Debug.log(ex);
		}

		try {
			handleCompatibility("BentoBox", plugin, (m, p) -> new BentoBoxCompat(m, p));
		} catch (Exception | NoClassDefFoundError ex) {
			Debug.log(ex);
		}
	}

	public static boolean canBuild(Player player, Block block) {
		return canBuild(player, block.getLocation());
	}

	public static boolean canBuild(Player player, Location location) {
		return _canBuild(player, location).getFirst();
	}

	public static Pair<Boolean, @Nullable String> _canBuild(Player player, Location location) {
		try {
			for (ProtectionCompatibility compat : compatibilities) {
				if (!compat.canBuild(player, location)) {
					return Pair.of(false, compat.getPlugin().getName());
				}
			}
		} catch (Exception ex) {
			Debug.log(ex);
		}

		return new Pair<>(true, null);
	}

	public static boolean canBreak(Player player, Block block) {
		return canBreak(player, block.getLocation());
	}

	public static boolean canBreak(Player player, Location location) {
		return _canBreak(player, location).getFirst();
	}

	public static Pair<Boolean, @Nullable String> _canBreak(Player player, Location location) {
		try {
			for (ProtectionCompatibility compat : compatibilities) {
				if (!compat.canBreak(player, location)) {
					return Pair.of(false, compat.getPlugin().getName());
				}
			}
		} catch (Exception ex) {
			Debug.log(ex);
		}

		return new Pair<>(true, null);
	}

	public static boolean canInteract(Player player, Block block) {
		return canInteract(player, block.getLocation());
	}

	public static boolean canInteract(Player player, Location location) {
		return _canInteract(player, location).getFirst();
	}

	public static Pair<Boolean, @Nullable String> _canInteract(Player player, Location location) {
		try {
			for (ProtectionCompatibility compat : compatibilities) {
				if (!compat.canInteract(player, location)) {
					return Pair.of(false, compat.getPlugin().getName());
				}
			}
		} catch (Exception ex) {
			Debug.log(ex);
		}

		return new Pair<>(true, null);
	}

	public static boolean canUse(Player player, Block block) {
		return canUse(player, block.getLocation());
	}

	public static boolean canUse(Player player, Location location) {
		return _canUse(player, location).getFirst();
	}

	public static Pair<Boolean, @Nullable String> _canUse(Player player, Location location) {
		try {
			for (ProtectionCompatibility compat : compatibilities) {
				if (!compat.canUse(player, location)) {
					return Pair.of(false, compat.getPlugin().getName());
				}
			}
		} catch (Exception ex) {
			Debug.log(ex);
		}

		return new Pair<>(true, null);
	}

	private static void handleCompatibility(String pluginName, JavaPlugin mainPlugin, CompatibilityConstructor constructor) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
		if (plugin != null) {
			try {
				compatibilities.add(constructor.create(mainPlugin, plugin));
			} catch (Exception e) {
				Nexus.warn("Failed to register protection compatibility for " + pluginName + ":");
				e.printStackTrace();
			}
		}
	}

	@FunctionalInterface
	private interface CompatibilityConstructor {
		ProtectionCompatibility create(JavaPlugin mainPlugin, Plugin plugin);
	}

}
