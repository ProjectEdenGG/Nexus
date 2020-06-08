package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.annotations.Disabled;
import org.bukkit.event.Listener;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

public class Listeners {

	public Listeners() {
		new Reflections(getClass().getPackage().getName()).getSubTypesOf(Listener.class).forEach(listener -> {
			try {
				if (listener.getAnnotation(Disabled.class) == null)
					BNCore.registerListener(new ObjenesisStd().newInstance(listener));
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		});
	}

}