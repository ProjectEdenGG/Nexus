package me.pugabyte.nexus.features.ambience.effects.sounds;

import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.SoundBuilder;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.nexus.utils.RandomUtils.randomDouble;
import static me.pugabyte.nexus.utils.RandomUtils.randomInt;

@AllArgsConstructor
public enum BirdSound {
	BLUEBIRD(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			int wait = 0;
			tasks.put(wait += 25, sound(1));
			tasks.put(wait += 25, sound(2));
			tasks.put(wait += 25, sound(1));
			tasks.put(wait += 25, sound(2));
			tasks.put(wait += 25, sound(1));
			tasks.put(wait += 25, sound(2));
		}
	},
	BUDGERIGAR(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			int wait = 0;
			for (int i = 0; i < randomInt(5, 12); i++)
				tasks.put(wait += randomInt(7, 15), sound(randomInt(1, 2)).volume(.05));
		}
	},
	CARDINAL(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			tasks.put(0, sound(1));
		}
	},
	FINCH(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			tasks.put(0, sound(randomInt(1, 2)));
		}
	},
	GOLDCREST(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			tasks.put(0, sound(1));
			tasks.put(Time.SECOND.x(randomInt(4, 6)), sound(1));
		}
	},
	GOULDIAN_FINCH(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			int wait = 0;
			for (int i = 0; i < randomInt(3, 7); i++)
				tasks.put(wait += randomInt(7, 15), sound(1));
		}
	},
	KILLDEER(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			int wait = 0;
			for (int i = 0; i < randomInt(5, 12); i++)
				tasks.put(wait += randomInt(10, 15), sound(1).pitch(randomDouble(.9, 1.1)));
		}
	},
	LEAF_WARBLER(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			tasks.put(0, sound(randomInt(1, 2)));
		}
	},
	MOCKINGBIRD(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			tasks.put(0, sound(randomInt(1, 7)));
		}
	},
	ROBIN(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			tasks.put(0, sound(randomInt(1, 2)));
		}
	},
	SHRIKE(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			tasks.put(0, sound(randomInt(1, 2)));
		}
	},
	SPARROW(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			tasks.put(0, sound(1));
		}
	},
	WILLOW_TIT(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			final SoundBuilder sound = sound(randomInt(1, 2)).volume(.07);

			int wait = 0;
			for (int i = 0; i < randomInt(2, 3); i++)
				tasks.put(wait += 25, sound);
		}
	},
	WOODPECKER(true) {
		@Override
		public void get(Map<Integer, SoundBuilder> tasks) {
			tasks.put(0, sound(1));
			tasks.put(Time.SECOND.x(randomDouble(1.5, 2.5)), sound(1));
		}
	},
	;

	@Getter
	private final boolean birdhouse;

	public void play(Location location) {
		build().forEach((wait, sound) ->
			Tasks.wait(wait, () -> sound.location(location).play()));
	}

	public void play(Player player, Location location) {
		build().forEach((wait, sound) ->
			Tasks.wait(wait, () -> sound.receiver(player).location(location).play()));
	}

	public Map<Integer, SoundBuilder> build() {
		final HashMap<Integer, SoundBuilder> tasks = new HashMap<>();
		get(tasks);
		return tasks;
	}

	abstract void get(Map<Integer, SoundBuilder> tasks);

	protected SoundBuilder sound(int number) {
		return new SoundBuilder("minecraft:custom.ambient.birds." + name().toLowerCase() + "_" + number)
			.category(SoundCategory.AMBIENT)
			.volume(.3);
	}

	public static BirdSound random() {
		return RandomUtils.randomElement(List.of(values()));
	}

	public static BirdSound randomBirdhouse() {
		return RandomUtils.randomElement(Arrays.stream(values()).filter(BirdSound::isBirdhouse).toList());
	}
}
