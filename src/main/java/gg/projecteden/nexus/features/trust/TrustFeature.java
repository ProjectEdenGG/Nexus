package gg.projecteden.nexus.features.trust;

import com.griefcraft.lwc.LWC;
import com.griefcraft.scripting.MetaData;
import com.griefcraft.scripting.Module;
import com.griefcraft.scripting.ModuleLoader;
import com.griefcraft.scripting.ModuleLoader.Event;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrustFeature extends Feature {

	@Override
	public void onStart() {
		LWC.getInstance().getModuleLoader().registerModule(Nexus.getInstance(), new LWCTrustModule());
	}

	@Override
	@SneakyThrows
	public void onStop() {
		// LWC's ModuleLoader does not have a way to unload modules from their cache, so lets hack it
		ModuleLoader loader = LWC.getInstance().getModuleLoader();
		Class<? extends ModuleLoader> clazz = loader.getClass();
		Field fastModuleCacheField = clazz.getDeclaredField("fastModuleCache");
		Field overrideCacheField = clazz.getDeclaredField("overrideCache");
		fastModuleCacheField.setAccessible(true);
		overrideCacheField.setAccessible(true);

		Map<Event, List<Module>> fastModuleCache = (Map<Event, List<Module>>) fastModuleCacheField.get(loader);
		// TODO this uses class simplenames... not very unique
		Map<String, Boolean> overrideCache = (Map<String, Boolean>) overrideCacheField.get(loader);

		new HashMap<>(fastModuleCache).forEach((event, _modules) ->
				new ArrayList<>(_modules).forEach(module -> {
					if (module.getClass().getPackage().getName().contains(Nexus.getInstance().getClass().getPackage().getName()))
						fastModuleCache.get(event).remove(module);
				}));

		List<MetaData> modules = loader.getRegisteredModules().get(Nexus.getInstance());

		loader.removeModules(Nexus.getInstance());
	}

}
