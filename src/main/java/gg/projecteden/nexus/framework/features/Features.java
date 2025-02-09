package gg.projecteden.nexus.framework.features;

import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Features {
	private final Plugin plugin;
	private final Set<Class<? extends Feature>> featureSet;
	@Getter
	private final static Map<Class<? extends Feature>, Feature> registered = new HashMap<>();

	public Features(Plugin plugin, String path) {
		this.plugin = plugin;
		this.featureSet = ReflectionUtils.subTypesOf(Feature.class, path);
	}

	public static <T extends Feature> T get(Class<T> clazz) {
		return (T) Optional.of(registered.get(clazz)).orElseThrow(() -> new NexusException("Feature " + prettyName(clazz) + " not found"));
	}

	public static <T extends Feature> List<T> getInheritors(Class<T> superclazz) {
		List<T> features = new ArrayList<>();

		for (Class<? extends Feature> clazz : registered.keySet())
			if (superclazz.isAssignableFrom(clazz))
				features.add((T) get(clazz));

		return features;
	}

	public static String prettyName(Feature feature) {
		return prettyName(feature.getClass());
	}

	public static String prettyName(Class<? extends Feature> clazz) {
		return clazz.getSimpleName().replaceAll("Feature$", "");
	}

	public static boolean isRegistered(Class<? extends Feature> clazz) {
		return registered.containsKey(clazz);
	}

	public void registerAll() {
		Nexus.debug(" Registering " + featureSet.size() + " features");
		featureSet.forEach(this::register);
	}

	public void register(Class<? extends Feature>... features) {
		for (Class<? extends Feature> clazz : features)
			try {
				if (isRegistered(clazz))
					continue;

				if (Utils.canEnable(clazz))
					register(Nexus.singletonOf(clazz));
			} catch (Throwable ex) {
				plugin.getLogger().info("Error while registering feature " + prettyName(clazz));
				ex.printStackTrace();
			}
	}

	public void registerExcept(Class<? extends Feature>... features) {
		List<Class<? extends Feature>> excluded = Arrays.asList(features);
		for (Class<? extends Feature> clazz : featureSet)
			if (!excluded.contains(clazz))
				register(clazz);
	}

	public void register(Feature feature) {
		final Class<? extends Feature> clazz = feature.getClass();

		if (isRegistered(clazz))
			return;

		final Depends depends = clazz.getAnnotation(Depends.class);
		if (depends != null)
			register(depends.value());

		if (isRegistered(clazz))
			return;

		new Timer("  Register feature " + feature.getName(), () -> {
			try {
				feature.onStart();
				Utils.tryRegisterListener(feature);
				registered.put(clazz, feature);
			} catch (Exception ex) {
				plugin.getLogger().info("Error while registering feature " + feature.getName());
				ex.printStackTrace();
			}
		});
	}

	public void unregisterAll() {
		for (Class<? extends Feature> clazz : featureSet)
			if (!Modifier.isAbstract(clazz.getModifiers()))
				unregister(clazz);
	}

	public void unregister(Class<? extends Feature>... features) {
		for (Class<? extends Feature> clazz : features)
			if (isRegistered(clazz))
				unregister(Features.registered.get(clazz));
			else if (Utils.canEnable(clazz))
				plugin.getLogger().severe("Cannot unregister feature " + prettyName(clazz) + " because it was never registered");
	}

	public void unregisterExcept(Class<? extends Feature>... features) {
		List<Class<? extends Feature>> excluded = Arrays.asList(features);
		for (Class<? extends Feature> clazz : featureSet)
			if (!excluded.contains(clazz))
				unregister(clazz);
	}

	public void unregister(Feature feature) {
		new Timer("  Unregister feature " + feature.getName(), () -> {
			try {
				feature.onStop();
			} catch (Exception ex) {
				plugin.getLogger().info("Error while unregistering feature " + feature.getName());
				ex.printStackTrace();
			}
			registered.remove(feature.getClass());
		});
	}

}
