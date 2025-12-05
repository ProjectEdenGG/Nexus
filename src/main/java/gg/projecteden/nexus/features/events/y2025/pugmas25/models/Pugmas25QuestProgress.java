package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25DailyTokens.Pugmas25DailyTokenSource;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25NPC;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.pugmas25.Advent25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestProgress {
	MINI_NUTCRACKERS(null) {
		@Override
		public Pugmas25QuestStatus getStatus(Pugmas25User user) {
			int numCollected = user.getFoundNutCrackers().size();
			if (numCollected == 0)
				return Pugmas25QuestStatus.NOT_STARTED;

			int numTotal = Pugmas25Config.get().getNutCrackerLocations().size();
			if (numCollected == numTotal)
				return Pugmas25QuestStatus.COMPLETED;

			return Pugmas25QuestStatus.IN_PROGRESS;
		}

		@Override
		@Nullable
		List<String> getProgressMessage(Pugmas25User user) {
			int numCollected = user.getFoundNutCrackers().size();
			int numTotal = Pugmas25Config.get().getNutCrackerLocations().size();

			return switch (getStatus(user)) {
				case NOT_STARTED -> List.of(
					"&3 " + getName() + " &7- &eStarted",
					"&7   - Find a mini nutcracker"
				);
				case IN_PROGRESS -> List.of(
					"&3 " + getName() + " &7- &eStarted",
					"&7   - " + numCollected + "/" + numTotal + " mini nutcrackers found"
				);
				case COMPLETED -> List.of("&3 " + getName() + " &7- &aCompleted");
			};
		}
	},

	ADVENT(Pugmas25NPC.ELF) {
		@Override
		public Pugmas25QuestStatus getStatus(Pugmas25User user) {
			var adventUser = user.advent();
			if (!adventUser.isUnlockedQuest())
				return Pugmas25QuestStatus.NOT_STARTED;

			int numCollected = adventUser.getCollected().size();
			int numTotal = Advent25Config.get().getDays().size();
			if (numCollected == numTotal)
				return Pugmas25QuestStatus.COMPLETED;

			return Pugmas25QuestStatus.IN_PROGRESS;
		}

		@Override
		@Nullable
		List<String> getProgressMessage(Pugmas25User user) {
			var adventUser = user.advent();
			int numCollected = adventUser.getCollected().size();
			int numTotal = Advent25Config.get().getDays().size();

			return switch (getStatus(user)) {
				case NOT_STARTED -> List.of(
					"&3 " + getName() + " &7- &eStarted",
					"&7   - Talk to the Elf"
				);
				case IN_PROGRESS -> List.of(
					"&3 " + getName() + " &7- &eStarted",
					"&7   - " + numCollected + "/" + numTotal + " presents collected"
				);
				case COMPLETED -> List.of("&3 " + getName() + " &7- &aCompleted");
			};
		}
	},

	DESIGN_A_BALLOON(Pugmas25NPC.AERONAUT) {
		@Override
		public Pugmas25QuestStatus getStatus(Pugmas25User user) {
			if (!user.isReceivedAeronautInstructions())
				return Pugmas25QuestStatus.NOT_STARTED;

			if (user.isBalloonSchemExists())
				return Pugmas25QuestStatus.COMPLETED;

			return Pugmas25QuestStatus.IN_PROGRESS;
		}

		@Override
		@Nullable
		List<String> getProgressMessage(Pugmas25User user) {
			return switch (getStatus(user)) {
				case NOT_STARTED -> List.of(
					"&3 " + getName() + " &7- &eStarted",
					"&7   - Talk to the Aeronaut"
				);
				case IN_PROGRESS -> List.of(
					"&3 " + getName() + " &7- &eStarted",
					"&7   - Save your hot air balloon"
				);
				case COMPLETED -> List.of("&3 " + getName() + " &7- &aCompleted");
			};
		}
	},

	ANGLER(Pugmas25NPC.ANGLER) {
		@Override
		public Pugmas25QuestStatus getStatus(Pugmas25User user) {
			if (!user.isReceivedAnglerQuestInstructions())
				return Pugmas25QuestStatus.NOT_STARTED;

			if (user.isCompletedAnglerQuest())
				return Pugmas25QuestStatus.COMPLETED;

			return Pugmas25QuestStatus.IN_PROGRESS;
		}

		@Override
		@Nullable
		List<String> getProgressMessage(Pugmas25User user) {
			Pugmas25Config config = new Pugmas25ConfigService().get0();
			String timeUntilReset = TimeUtils.Timespan.of(Pugmas25.get().now(), config.getAnglerQuestResetDateTime()).format(FormatType.LONG);

			return switch (getStatus(user)) {
				case NOT_STARTED -> List.of(
					"&3 " + getName() + " &7- &eStarted",
					"&7   - Talk to the Angler"
				);
				case IN_PROGRESS -> List.of(
					"&3 " + getName() + " &7- &eStarted",
					"&7   - Talk to the Angler for more info (reset in " + timeUntilReset + ")"
				);
				case COMPLETED -> List.of("&3 " + getName() + " &7- &aCompleted (resets in " + timeUntilReset + ")");
			};
		}
	},

	DAILY_FAIRGROUND_TOKENS(null) {
		@Override
		public Pugmas25QuestStatus getStatus(Pugmas25User user) {
			EventUserService eventUserService = new EventUserService();
			EventUser eventUser = eventUserService.get(user);

			for (Pugmas25DailyTokenSource source : Pugmas25DailyTokenSource.values()) {
				if (eventUser.getTokensReceivedToday(source.getId()) < source.getMaxDailyTokens())
					return Pugmas25QuestStatus.IN_PROGRESS;
			}

			return Pugmas25QuestStatus.COMPLETED;
		}

		@Override
		@Nullable
		List<String> getProgressMessage(Pugmas25User user) {
			EventUserService eventUserService = new EventUserService();
			EventUser eventUser = eventUserService.get(user);

			LocalDateTime tomorrow = Pugmas25.get().now().plusDays(1).toLocalDate().atStartOfDay();
			var timeUntilReset = TimeUtils.Timespan.of(Pugmas25.get().now(), tomorrow).format(FormatType.LONG);

			switch (getStatus(user)) {
				case IN_PROGRESS -> {
					List<String> result = new ArrayList<>();
					result.add("&3 " + getName() + " &7- &eStarted (resets in " + timeUntilReset + ")");

					String sourceName;
					for (Pugmas25DailyTokenSource source : Pugmas25DailyTokenSource.values()) {
						sourceName = source.getName();
						if (source == Pugmas25DailyTokenSource.WHACAMOLE)
							sourceName = "WhacAWakka";

						int received = eventUser.getTokensReceivedToday(source.getId());
						int max = source.getMaxDailyTokens();
						if (received == 0)
							result.add("&7   - " + sourceName + ": " + "&c" + received + "&7/&e" + max + " &7event tokens");
						else if (received < max)
							result.add("&7   - " + sourceName + ": " + "&6" + received + "&7/&e" + max + " &7event tokens");
						else
							result.add("&7   - " + sourceName + ": " + "&a" + received + "&7/&a" + max + " &7event tokens");
					}

					return result;
				}
				case COMPLETED -> {
					return List.of("&3 " + getName() + " &7- &aCompleted (resets in " + timeUntilReset + ")");
				}
			}
			;

			return null;
		}
	};

	@Nullable
	private final Pugmas25NPC npc;

	public abstract Pugmas25QuestStatus getStatus(Pugmas25User user);

	abstract @Nullable List<String> getProgressMessage(Pugmas25User user);

	public String getName() {
		return StringUtils.camelCase(this);
	}

	public void send(Player viewer, Pugmas25User target) {
		List<String> progress = getProgressMessage(target);
		if (Nullables.isNullOrEmpty(progress))
			return;

		for (String line : progress) {
			PlayerUtils.send(viewer, line);
		}
	}

	public enum Pugmas25QuestStatus {
		NOT_STARTED,
		IN_PROGRESS,
		COMPLETED,
		;
	}

	public boolean isRepeatable() {
		return getField().isAnnotationPresent(Repeatable.class);
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Repeatable {
	}
}
