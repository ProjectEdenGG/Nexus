package gg.projecteden.nexus.hooks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.hooks.citizens.CitizensHook;
import gg.projecteden.nexus.hooks.citizens.CitizensHookImpl;
import gg.projecteden.nexus.hooks.glowapi.GlowAPIHook;
import gg.projecteden.nexus.hooks.glowapi.GlowAPIHookImpl;
import gg.projecteden.nexus.hooks.viaversion.ViaVersionHook;
import gg.projecteden.nexus.hooks.viaversion.ViaVersionHookImpl;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

@Getter
public class Hook {
	public static final ViaVersionHook VIAVERSION = hook("ViaVersion", ViaVersionHook.class, ViaVersionHookImpl.class);
	public static final GlowAPIHook GLOWAPI = hook("GlowAPI", GlowAPIHook.class, GlowAPIHookImpl.class);
	public static final CitizensHook CITIZENS = hook("Citizens", CitizensHook.class, CitizensHookImpl.class);

	@SneakyThrows
	private static <T extends IHook<?>> T hook(String plugin, Class<? extends IHook<T>> defaultImpl, Class<? extends IHook<T>> workingImpl) {
		final IHook<T> hook;

		if (isEnabled(plugin))
			hook = Nexus.singletonOf(workingImpl);
		else
			hook = Nexus.singletonOf(defaultImpl);

		Utils.tryRegisterListener(hook);
		return (T) hook;
	}

	public static boolean isEnabled(String plugin) {
		return Bukkit.getServer().getPluginManager().isPluginEnabled(plugin);
	}

}
