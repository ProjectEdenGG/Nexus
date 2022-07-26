package gg.projecteden.nexus.hooks;

import gg.projecteden.nexus.hooks.vanish.VanishHook;
import gg.projecteden.nexus.hooks.vanish.VanishHookImpl;
import gg.projecteden.nexus.hooks.viaversion.ViaVersionHook;
import gg.projecteden.nexus.hooks.viaversion.ViaVersionHookImpl;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import static gg.projecteden.nexus.Nexus.singletonOf;

@Getter
public class Hook {
	public static final ViaVersionHook VIAVERSION = hook("ViaVersion", ViaVersionHook.class, ViaVersionHookImpl.class);
	public static final VanishHook VANISH = hook("PremiumVanish", VanishHook.class, VanishHookImpl.class);

	@SneakyThrows
	private static <T extends IHook<?>> T hook(String plugin, Class<? extends IHook<T>> defaultImpl, Class<? extends IHook<T>> workingImpl) {
		final IHook<T> hook;

		if (isEnabled(plugin))
			hook = singletonOf(workingImpl);
		else
			hook = singletonOf(defaultImpl);

		Utils.tryRegisterListener(hook);
		return (T) hook;
	}

	public static boolean isEnabled(String plugin) {
		return Bukkit.getServer().getPluginManager().isPluginEnabled(plugin);
	}

}
