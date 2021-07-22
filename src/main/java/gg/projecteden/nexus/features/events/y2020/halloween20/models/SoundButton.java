package gg.projecteden.nexus.features.events.y2020.halloween20.models;

import gg.projecteden.nexus.features.events.y2020.halloween20.Halloween20;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Sound;

public enum SoundButton {

	ONE(new Location(Halloween20.getWorld(), 399, 165, -1948), Sound.ENTITY_GHAST_WARN),
	TWO(new Location(Halloween20.getWorld(), 309, 207, -1957), Sound.ENTITY_ZOMBIE_AMBIENT),
	THREE(new Location(Halloween20.getWorld(), 292, 203, -1921), Sound.ENTITY_VEX_CHARGE),
	FOUR(new Location(Halloween20.getWorld(), 300, 57, -1943), Sound.ENTITY_EVOKER_PREPARE_WOLOLO),
	FIVE(new Location(Halloween20.getWorld(), 265, 105, -1957), Sound.ENTITY_PHANTOM_SWOOP),
	SIX(new Location(Halloween20.getWorld(), 284, 62, -1962), Sound.ENTITY_WOLF_GROWL);

	@Getter
	private final Location location;
	@Getter
	private final Sound sound;

	SoundButton(Location location, Sound sound) {
		this.location = location;
		this.sound = sound;
	}

	public static SoundButton getByLocation(Location location) {
		for (SoundButton button : values())
			if (button.getLocation().equals(location))
				return button;

		return null;
	}
}
