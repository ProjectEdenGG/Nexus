package me.pugabyte.nexus.features.listeners;

import eden.annotations.Disabled;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.features.Feature;
import org.bukkit.event.Listener;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

public class Listeners extends Feature {

	@Override
	public void onStart() {
		new Reflections(getClass().getPackage().getName()).getSubTypesOf(Listener.class).forEach(listener -> {
			try {
				if (listener.getAnnotation(Disabled.class) == null)
					Nexus.registerListener(new ObjenesisStd().newInstance(listener));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

}