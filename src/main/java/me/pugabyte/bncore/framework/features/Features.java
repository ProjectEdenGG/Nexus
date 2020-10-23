package me.pugabyte.bncore.framework.features;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.annotations.Disabled;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.plugin.Plugin;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Features {
	private final Plugin plugin;
	private final String path;
	private final Set<Class<? extends Feature>> featureSet;
	private final static Map<Class<? extends Feature>, Feature> features = new HashMap<>();

	public Features(Plugin plugin, String path) {
		this.plugin = plugin;
		this.path = path;
		this.featureSet = new Reflections(path).getSubTypesOf(Feature.class);
	}

	public static <T extends Feature> T get(Class<? extends Feature> clazz) {
		return (T) Optional.of(features.get(clazz)).orElseThrow(() -> new BNException("Feature " + prettyName(clazz) + " not found"));
	}

	public static String prettyName(Feature feature) {
		return prettyName(feature.getClass());
	}

	public static String prettyName(Class<? extends Feature> clazz) {
		return clazz.getSimpleName().replaceAll("Feature$", "");
	}

	public void registerAll() {
		featureSet.forEach(this::register);
	}

	public void register(Class<? extends Feature>... features) {
		for (Class<? extends Feature> clazz : features)
			if (Utils.canEnable(clazz))
				register(new ObjenesisStd().newInstance(clazz));
	}

	public void registerExcept(Class<? extends Feature>... features) {
		List<Class<? extends Feature>> excluded = Arrays.asList(features);
		for (Class<? extends Feature> clazz : featureSet)
			if (!excluded.contains(clazz))
				register(clazz);
	}

	public void register(Feature feature) {
		if (features.containsKey(feature.getClass()))
			// Already registered
			return;

		new Timer("  Register feature " + feature.getName(), () -> {
			try {
				feature.startup();
				Utils.tryRegisterListener(feature);
				features.put(feature.getClass(), feature);
			} catch (Exception ex) {
				BNCore.log("Error while registering feature " + feature.getName());
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
			if (Features.features.containsKey(clazz))
				unregister(Features.features.get(clazz));
			else if (clazz.getAnnotation(Disabled.class) == null)
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
				feature.shutdown();
			} catch (Exception ex) {
				BNCore.log("Error while unregistering feature " + feature.getName());
				ex.printStackTrace();
			}
			features.remove(feature.getClass());
		});
	}

}
