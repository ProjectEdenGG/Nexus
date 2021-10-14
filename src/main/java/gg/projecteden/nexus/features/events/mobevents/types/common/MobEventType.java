package gg.projecteden.nexus.features.events.mobevents.types.common;

import gg.projecteden.nexus.features.events.mobevents.annotations.Affects;
import gg.projecteden.nexus.features.events.mobevents.annotations.Chance;
import gg.projecteden.nexus.features.events.mobevents.annotations.Description;
import gg.projecteden.nexus.features.events.mobevents.annotations.Duration;
import gg.projecteden.nexus.features.events.mobevents.annotations.FreezeTime;
import gg.projecteden.nexus.features.events.mobevents.annotations.Instance;
import gg.projecteden.nexus.features.events.mobevents.annotations.Skippable;
import gg.projecteden.nexus.features.events.mobevents.annotations.StartsAt;
import gg.projecteden.nexus.features.events.mobevents.types.BloodMoon;
import gg.projecteden.nexus.features.events.mobevents.types.RisenHell;
import gg.projecteden.nexus.features.events.mobevents.types.SlimeRain;
import gg.projecteden.nexus.features.events.mobevents.types.common.WorldSet.Dimension;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.SneakyThrows;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum MobEventType {
	@Instance(SlimeRain.class)
	@Chance(10)
	@StartsAt(value = 1000, random = 1000)
	@Affects(Dimension.OVERWORLD)
	@Duration(value = TickTime.MINUTE, x = 10)
	@FreezeTime(false)
	@Skippable(value = true, sleepPercent = 100)
	@Description(
		start = "&a&oSlime is falling from the sky!",
		end = "&o&7Slime Rain has ended"
	)
	SLIME_RAIN,

	@Instance(BloodMoon.class)
	@Chance(10)
	@StartsAt(value = 15000, random = 1000)
	@Affects(Dimension.OVERWORLD)
	@Duration(value = TickTime.MINUTE, x = 20)
	@FreezeTime(true)
	@Skippable(value = true, sleepPercent = 100)
	@Description(
		start = "&c&oThe Blood Moon is rising...",
		end = "&o&7The Blood Moon has ended"
	)
	BLOOD_MOON {
		@Override
		public double getIncreasedChance() {
			if (LocalDate.now().getMonth() == Month.OCTOBER)
				return 10;
			return 0;
		}
	},

	// TODO: decreases spawn chance in nether
	@Instance(RisenHell.class)
	@Chance(10)
	@StartsAt(value = 15000, random = 1000)
	@Affects({Dimension.OVERWORLD, Dimension.NETHER})
	@Duration(value = TickTime.MINUTE, x = 10)
	@FreezeTime(true)
	@Skippable(value = true, sleepPercent = 100)
	@Description(
		start = "&c&oThe gates to hell have been opened...",
		end = "&o&7Risen Hell has ended."
	)
	RISEN_HELL,
	;

	public static MobEventType random(DayPhase dayPhase) {
		return RandomUtils.getWeightedRandom(new HashMap<>() {{
			for (MobEventType type : MobEventType.values())
				if (type.getChance() > 0 && type.getTimeOfDay() == dayPhase)
					put(type, type.getChance());
		}});
	}

	public double getChance() {
		return getBaseChance() + getIncreasedChance();
	}

	public double getBaseChance() {
		return getField().getAnnotation(Chance.class).value();
	}

	public double getIncreasedChance() {
		return 0;
	}

	public long getStartTime() {
		return getField().getAnnotation(StartsAt.class).value();
	}

	public int getDelayTime() {
		return RandomUtils.randomInt(0, (int) getField().getAnnotation(StartsAt.class).random());
	}

	public DayPhase getTimeOfDay() {
		return DayPhase.at(getStartTime());
	}

	public int getDuration() {
		Duration annotation = getField().getAnnotation(Duration.class);
		return annotation.value().x(annotation.x());
	}

	public String getStartMessage() {
		return getField().getAnnotation(Description.class).start();
	}

	public String getEndMessage() {
		return getField().getAnnotation(Description.class).end();
	}

	public List<Dimension> getDimensions() {
		return Arrays.stream(getField().getAnnotation(Affects.class).value()).toList();
	}

	public boolean applies(World world) {
		return Arrays.asList(getField().getAnnotation(Affects.class).value()).contains(Dimension.of(world));
	}

	public boolean canBeSkipped() {
		return getField().getAnnotation(Skippable.class).value();
	}

	public boolean freezeTime() {
		return getField().getAnnotation(FreezeTime.class).value();
	}

	public int getSleepPercentage() {
		return getField().getAnnotation(Skippable.class).sleepPercent();
	}

	@SneakyThrows
	public <T extends IMobEvent> T newInstance() {
		try {
			Constructor<? extends IMobEvent> constructor = getField().getAnnotation(Instance.class).value().getConstructor();
			constructor.setAccessible(true);
			return (T) constructor.newInstance();
		} catch (Exception ignored) {
		}

		return null;
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public Entity handleEntity(Entity entity, DifficultyUser user) {
		return entity;
	}
}
