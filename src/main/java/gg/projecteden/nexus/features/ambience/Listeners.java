package gg.projecteden.nexus.features.ambience;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class Listeners implements Listener {

	public Listeners() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(WeatherChangeEvent event) {
		// if set to raining
		if (event.toWeatherState()) {
			new SoundBuilder(CustomSound.WEATHER_THUNDER)
				.category(SoundCategory.WEATHER)
				.receivers(event.getWorld().getPlayers())
				.play();
		}

	}
}
