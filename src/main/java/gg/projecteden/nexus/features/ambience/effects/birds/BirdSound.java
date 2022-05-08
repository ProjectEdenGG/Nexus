package gg.projecteden.nexus.features.ambience.effects.birds;

import gg.projecteden.nexus.features.ambience.effects.birds.common.annotations.Biomes;
import gg.projecteden.nexus.features.ambience.effects.birds.common.annotations.Birdhouse;
import gg.projecteden.nexus.utils.BiomeTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.RandomUtils.randomDouble;
import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

@AllArgsConstructor
public enum BirdSound {
	@Birdhouse
	BLUEBIRD {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			long wait = 0;
			tasks.put(wait += 25L, sound(1));
			tasks.put(wait += 25L, sound(2));
			tasks.put(wait += 25L, sound(1));
			tasks.put(wait += 25L, sound(2));
			tasks.put(wait += 25L, sound(1));
			tasks.put(wait += 25L, sound(2));
		}
	},
	@Birdhouse
	BUDGERIGAR {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			long wait = 0;
			for (int i = 0; i < randomInt(5, 12); i++)
				tasks.put(wait += randomInt(7, 15), sound(randomInt(1, 2)).volume(.05));
		}
	},
	@Birdhouse
	CARDINAL {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			tasks.put(0L, sound(1));
		}
	},
	@Birdhouse
	FINCH {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			tasks.put(0L, sound(randomInt(1, 2)));
		}
	},
	@Birdhouse
	GOLDCREST {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			tasks.put(0L, sound(1));
			tasks.put(TickTime.SECOND.x(randomInt(4, 6)), sound(1));
		}
	},
	@Birdhouse
	GOULDIAN_FINCH {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			long wait = 0;
			for (int i = 0; i < randomInt(3, 7); i++)
				tasks.put(wait += randomInt(7, 15), sound(1));
		}
	},
	@Birdhouse
	KILLDEER {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			long wait = 0;
			for (int i = 0; i < randomInt(5, 12); i++)
				tasks.put(wait += randomInt(10, 15), sound(1).pitch(randomDouble(.9, 1.1)));
		}
	},
	@Birdhouse
	LEAF_WARBLER {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			tasks.put(0L, sound(randomInt(1, 2)));
		}
	},
	@Birdhouse
	MOCKINGBIRD {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			tasks.put(0L, sound(randomInt(1, 7)));
		}
	},
	@Birdhouse
	ROBIN {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			tasks.put(0L, sound(randomInt(1, 2)));
		}
	},
	@Birdhouse
	SHRIKE {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			tasks.put(0L, sound(randomInt(1, 2)));
		}
	},
	@Birdhouse
	SPARROW {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			tasks.put(0L, sound(1));
		}
	},
	@Birdhouse
	WILLOW_TIT {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			final SoundBuilder sound = sound(randomInt(1, 2)).volume(.07);

			long wait = 0;
			for (int i = 0; i < randomInt(2, 3); i++)
				tasks.put(wait += 25, sound);
		}
	},
	@Birdhouse
	WOODPECKER {
		@Override
		public void get(Map<Long, SoundBuilder> tasks) {
			tasks.put(0L, sound(1));
			tasks.put(TickTime.SECOND.x(randomDouble(1.5, 2.5)), sound(1));
		}
	},
	@Biomes(BiomeTag.JUNGLE)
	MACAW {
		@Override
		void get(Map<Long, SoundBuilder> tasks) {

		}
	},
	;

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public boolean isBirdhouse() {
		return getField().getAnnotation(Birdhouse.class) != null;
	}

	public boolean getBiomes() {
		return getField().getAnnotation(Birdhouse.class) != null;
	}

	public void play(Location location) {
		build().forEach((wait, sound) ->
			Tasks.wait(wait, () -> sound.location(location).play()));
	}

	public void play(Player player, Location location) {
		build().forEach((wait, sound) ->
			Tasks.wait(wait, () -> sound.receiver(player).location(location).play()));
	}

	public Map<Long, SoundBuilder> build() {
		final HashMap<Long, SoundBuilder> tasks = new HashMap<>();
		get(tasks);
		return tasks;
	}

	abstract void get(Map<Long, SoundBuilder> tasks);

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
