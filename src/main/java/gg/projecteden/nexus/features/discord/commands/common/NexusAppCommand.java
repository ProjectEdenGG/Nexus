package gg.projecteden.nexus.features.discord.commands.common;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.discord.appcommands.AppCommand;
import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.AppCommandRegistry;
import gg.projecteden.api.discord.appcommands.annotations.GuildCommand;
import gg.projecteden.api.discord.appcommands.exceptions.AppCommandException;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.discord.commands.common.annotations.Verify;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@GuildCommand("132680070480396288")
public abstract class NexusAppCommand extends AppCommand {

	public NexusAppCommand(AppCommandEvent event) {
		super(event);
	}

	protected void error(String message) {
		throw new InvalidInputException(message);
	}

	public @NotNull UUID uuid() {
		return new DiscordUserService().getFromUserId(member().getId()).getUuid();
	}

	public @NotNull String nickname() {
		return user().getNickname();
	}

	public @NotNull Nerd nerd() {
		return user().getNerd();
	}

	public DiscordUser user() {
		return new DiscordUserService().getFromUserId(member().getId());
	}

	public DiscordUser verify() {
		return new DiscordUserService().checkVerified(member().getId());
	}

	public boolean isVerified() {
		try {
			verify();
			return true;
		} catch (InvalidInputException ex) {
			return false;
		}
	}

	protected boolean isSelf(PlayerOwnedObject object) {
		return isVerified() && isSelf(PlayerUtils.getPlayer(object.getUuid()));
	}

	protected boolean isSelf(HasUniqueId uuid) {
		return isVerified() && isSelf(uuid(), uuid.getUniqueId());
	}

	protected boolean isSelf(UUID self, UUID uuid) {
		return isVerified() && self.equals(uuid);
	}

	static {
		AppCommandRegistry.registerConverter(Enum.class, argument -> {
			String input = argument.getInput();
			final Class<?> type = argument.getMeta().getType();
			if (input == null) throw new AppCommandException("Missing argument");
			return Arrays.stream(type.getEnumConstants())
				.filter(constant -> ((Enum<?>) constant).name().equalsIgnoreCase(input))
				.findFirst()
				.orElseThrow(() -> new InvalidInputException(type.getSimpleName() + " from &e" + input + " &cnot found"));
		});

		AppCommandRegistry.registerConverter(PlayerOwnedObject.class, argument -> {
			String input = argument.getInput();
			try {
				var service = (Class<? extends MongoPlayerService<?>>) MongoPlayerService.ofObject((Class<? extends DatabaseObject>) argument.getMeta().getType());
				if (service == null)
					return null;
				return service.getConstructor().newInstance().get(convertToOfflinePlayer(input, argument.getCommand()));
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new AppCommandException("Internal error");
			}
		});

		AppCommandRegistry.registerConverter(OfflinePlayer.class, argument -> convertToOfflinePlayer(argument.getInput(), argument.getCommand()));

		AppCommandRegistry.registerConverter(LocalDateTime.class, argument -> {
			final String input = argument.getInput();
			if (input.startsWith("+"))
				return Timespan.of(input.replaceFirst("\\+", "")).fromNow();
			if (input.startsWith("-"))
				return Timespan.of(input.replaceFirst("-", "")).sinceNow();

			return TimeUtils.parseDateTime(input);
		});

		AppCommandRegistry.registerConverter(LocalDate.class, argument -> {
			final String input = argument.getInput();
			if (input.startsWith("+"))
				return Timespan.of(input.replaceFirst("\\+", "")).fromNow();
			if (input.startsWith("-"))
				return Timespan.of(input.replaceFirst("-", "")).sinceNow();

			return TimeUtils.parseDate(input);
		});

		AppCommandRegistry.registerConverter(Timespan.class, argument -> Timespan.of(argument.getInput()));

		AppCommandRegistry.registerAnnotationHandler(Verify.class, (command, annotation) -> ((NexusAppCommand) command).verify());
	}

	@Nullable
	private static OfflinePlayer convertToOfflinePlayer(String input, AppCommand command) {
		if (input == null) return null;
		if ("null".equalsIgnoreCase(input)) return null;
		if ("self".equalsIgnoreCase(input)) input = ((NexusAppCommand) command).verify().getUuid().toString();
		return PlayerUtils.getPlayer(input.replaceFirst("[pP]:", ""));
	}

}
