package gg.projecteden.nexus.hooks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.hooks.bentobox.BentoBoxHook;
import gg.projecteden.nexus.hooks.bentobox.BentoBoxHookImpl;
import gg.projecteden.nexus.hooks.citizens.CitizensHook;
import gg.projecteden.nexus.hooks.citizens.CitizensHookImpl;
import gg.projecteden.nexus.hooks.headdatabase.HeadDatabaseHook;
import gg.projecteden.nexus.hooks.headdatabase.HeadDatabaseHookImpl;
import gg.projecteden.nexus.hooks.libsdisguises.LibsDisguisesHook;
import gg.projecteden.nexus.hooks.libsdisguises.LibsDisguisesHookImpl;
import gg.projecteden.nexus.hooks.viaversion.ViaVersionHook;
import gg.projecteden.nexus.hooks.viaversion.ViaVersionHookImpl;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

@Getter
public class Hook {
	public static final HeadDatabaseHook HEADDATABASE = hook(HeadDatabaseHook.class, HeadDatabaseHookImpl.class);
	public static final LibsDisguisesHook LIBSDISGUISES = hook(LibsDisguisesHook.class, LibsDisguisesHookImpl.class);
	public static final ViaVersionHook VIAVERSION = hook(ViaVersionHook.class, ViaVersionHookImpl.class);
	public static final CitizensHook CITIZENS = hook(CitizensHook.class, CitizensHookImpl.class);
	public static final BentoBoxHook BENTOBOX = hook(BentoBoxHook.class, BentoBoxHookImpl.class);

	@SneakyThrows
	private static <T extends IHook<?>> T hook(Class<? extends IHook<T>> defaultImpl, Class<? extends IHook<T>> workingImpl) {
		IHook<T> hook = Nexus.singletonOf(defaultImpl);

		if (isEnabled(hook.getPluginName()))
			hook = Nexus.singletonOf(workingImpl);

		Utils.tryRegisterListener(hook);
		return (T) hook;
	}

	public static boolean isEnabled(String plugin) {
		return Bukkit.getServer().getPluginManager().isPluginEnabled(plugin);
	}

}
