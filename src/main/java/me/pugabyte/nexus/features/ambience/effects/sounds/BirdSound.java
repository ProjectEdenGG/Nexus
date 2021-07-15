package me.pugabyte.nexus.features.ambience.effects.sounds;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.SoundBuilder;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.SoundCategory;

import java.util.List;

import static me.pugabyte.nexus.utils.RandomUtils.randomDouble;
import static me.pugabyte.nexus.utils.RandomUtils.randomInt;

public enum BirdSound {
	BLUEBIRD {
		@Override
		public void play(Location location) {
			Runnable one = () -> sound(1).location(location).play();
			Runnable two = () -> sound(2).location(location).play();

			int wait = 0;
			Tasks.wait(wait += 25, one);
			Tasks.wait(wait += 25, two);
			Tasks.wait(wait += 25, one);
			Tasks.wait(wait += 25, two);
			Tasks.wait(wait += 25, one);
			Tasks.wait(wait += 25, two);
		}
	},
	BUDGERIGAR {
		@Override
		public void play(Location location) {
			int wait = 0;
			for (int i = 0; i < randomInt(5, 12); i++)
				Tasks.wait(wait += randomInt(7, 15), () ->
					sound(randomInt(1, 2)).location(location).volume(.05).play());
		}
	},
	CARDINAL {
		@Override
		public void play(Location location) {
			sound(1).location(location).play();
		}
	},
	FINCH {
		@Override
		public void play(Location location) {
			sound(randomInt(1, 2)).location(location).play();
		}
	},
	GOLDCREST {
		@Override
		public void play(Location location) {
			Runnable one = () -> sound(1).location(location).play();

			one.run();
			Tasks.wait(Time.SECOND.x(randomInt(4, 6)), one);
		}
	},
	GOULDIAN_FINCH {
		@Override
		public void play(Location location) {
			int wait = 0;
			for (int i = 0; i < randomInt(3, 7); i++)
				Tasks.wait(wait += randomInt(7, 15), () ->
					sound(1).location(location).play());
		}
	},
	KILLDEER {
		@Override
		public void play(Location location) {
			int wait = 0;
			for (int i = 0; i < randomInt(5, 12); i++)
				Tasks.wait(wait += randomInt(10, 15), () ->
					sound(1).location(location).pitch(randomDouble(.9, 1.1)).play());
		}
	},
	LEAF_WARBLER {
		@Override
		public void play(Location location) {
			sound(randomInt(1, 2)).location(location).play();
		}
	},
	MOCKINGBIRD {
		@Override
		public void play(Location location) {
			sound(randomInt(1, 7)).location(location).play();
		}
	},
	ROBIN {
		@Override
		public void play(Location location) {
			sound(randomInt(1, 2)).location(location).play();
		}
	},
	SHRIKE {
		@Override
		public void play(Location location) {
			sound(randomInt(1, 2)).location(location).play();
		}
	},
	SPARROW {
		@Override
		public void play(Location location) {
			sound(1).location(location).play();
		}
	},
	WILLOW_TIT {
		@Override
		public void play(Location location) {
			final SoundBuilder sound = sound(randomInt(1, 2)).location(location).volume(.07);

			int wait = 0;
			for (int i = 0; i < randomInt(2, 3); i++)
				Tasks.wait(wait += 25, sound::play);
		}
	},
	WOODPECKER {
		@Override
		public void play(Location location) {
			Runnable one = () -> sound(1).location(location).play();

			one.run();
			Tasks.wait(Time.SECOND.x(randomDouble(1.5, 2.5)), one);
		}
	},
	;

	abstract public void play(Location location);

	protected SoundBuilder sound(int number) {
		return new SoundBuilder("minecraft:custom.ambient.birds." + name().toLowerCase() + "_" + number).category(SoundCategory.AMBIENT).volume(.3);
	}

	public static BirdSound random() {
		return RandomUtils.randomElement(List.of(values()));
	}
}
