package gg.projecteden.nexus.features.events.mobevents.types.common;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.mobevents.annotations.Affects;
import gg.projecteden.nexus.features.events.mobevents.annotations.Chance;
import gg.projecteden.nexus.features.events.mobevents.annotations.Description;
import gg.projecteden.nexus.features.events.mobevents.annotations.Duration;
import gg.projecteden.nexus.features.events.mobevents.annotations.FreezeTime;
import gg.projecteden.nexus.features.events.mobevents.annotations.Instance;
import gg.projecteden.nexus.features.events.mobevents.annotations.Skippable;
import gg.projecteden.nexus.features.events.mobevents.annotations.StartsAt;
import gg.projecteden.nexus.features.events.mobevents.types.BloodMoon;
import gg.projecteden.nexus.features.events.mobevents.types.Raid;
import gg.projecteden.nexus.features.events.mobevents.types.RisenHell;
import gg.projecteden.nexus.features.events.mobevents.types.SlimeRain;
import gg.projecteden.nexus.features.events.mobevents.types.common.WorldSet.Dimension;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.SneakyThrows;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum MobEventType {
	@Instance(SlimeRain.class)
	@Chance(10)
	@StartsAt(value = {1000}, random = 1000)
	@Affects(Dimension.OVERWORLD)
	@Duration(value = TickTime.MINUTE, x = 6)
	@FreezeTime(false)
	@Skippable(value = true, sleepPercent = 100)
	@Description(
		warning = "&a&oSlime Rain is about to start!",
		start = "&a&oSlime is falling from the sky!",
		end = "&o&7Slime Rain has ended"
	)
	SLIME_RAIN,

	@Instance(BloodMoon.class)
	@Chance(10)
	@StartsAt(value = {15000}, random = 1000)
	@Affects(Dimension.OVERWORLD)
	@Duration(value = TickTime.MINUTE, x = 8)
	@FreezeTime(true)
	@Skippable(value = true, sleepPercent = 100)
	@Description(
		warning = "&c&oThe Blood Moon is about to start!",
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
	@StartsAt(value = {15000}, random = 1000)
	@Affects({Dimension.OVERWORLD, Dimension.NETHER})
	@Duration(value = TickTime.MINUTE, x = 8)
	@FreezeTime(true)
	@Skippable(value = true, sleepPercent = 100)
	@Description(
		warning = "&c&oRisen Hell is about to start!",
		start = "&c&oThe gates to hell have been opened...",
		end = "&o&7Risen Hell has ended."
	)
	RISEN_HELL,

	@Instance(Raid.class)
	@Chance(10)
	@StartsAt(value = {5000, 15000}, random = 1000)
	@Affects(Dimension.OVERWORLD)
	@Duration(value = TickTime.MINUTE, x = 8)
	@FreezeTime(false)
	@Skippable(value = true, sleepPercent = 100)
	@Description(
		warning = "&c&oAn Illager Raid is about to start!",
		start = "&c&oIllager Raid is starting...",
		end = "&o&7Illager Raid has ended."
	)
	RAID,
	;

	public static MobEventType random(DayPhase dayPhase) {
		return RandomUtils.getWeightedRandom(new HashMap<>() {{
			for (MobEventType type : MobEventType.values())
				if (type.getChance() > 0 && type.getDayPhases().contains(dayPhase))
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

	public Set<Long> getStartTimes() {
		return Arrays.stream(getField().getAnnotation(StartsAt.class).value()).boxed().collect(Collectors.toSet());
	}

	public int getDelayTime() {
		return RandomUtils.randomInt(0, (int) getField().getAnnotation(StartsAt.class).random());
	}

	public Set<DayPhase> getDayPhases() {
		Set<DayPhase> result = new HashSet<>();
		for (long startTime : getStartTimes()) {
			result.add(DayPhase.at(startTime));
		}

		return result;
	}

	public long getDuration() {
		Duration annotation = getField().getAnnotation(Duration.class);
		return annotation.value().x(annotation.x());
	}

	public String getWarningMessage() {
		return getField().getAnnotation(Description.class).warning();
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
