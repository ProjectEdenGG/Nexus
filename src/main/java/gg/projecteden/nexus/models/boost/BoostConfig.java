package gg.projecteden.nexus.models.boost;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.discord.DiscordId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.boost.Booster.Boost;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "boost_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class BoostConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private Map<Boostable, String> boosts = new ConcurrentHashMap<>();
	private List<Boost> personalBoosts = new ArrayList<>();

	public static BoostConfig get() {
		return new BoostConfigService().get0();
	}

	public static double multiplierOf(Boostable boostable) {
		return get().getMultiplier(boostable);
	}

	public boolean hasBoost(Boostable boostable) {
		return boosts.containsKey(boostable);
	}

	public Boost getBoost(Boostable boostable) {
		String id = boosts.get(boostable);
		String[] split = id.split("#");
		Booster booster = new BoosterService().get(UUID.fromString(split[0]));
		return booster.get(Integer.parseInt(split[1]));
	}

	public double getMultiplier(Boostable boostable) {
		if (hasBoost(boostable))
			return getBoost(boostable).getMultiplier();
		return 1d;
	}

	public void removeBoost(Boost boost) {
		if (boost.isPersonal()) {
			personalBoosts.remove(boost);
		}
		else {
			Boost active = getBoost(boost.getType());
			if (active == null) return;
			if (!active.equals(boost))
				throw new InvalidInputException("Specified boost (" + boost.getNicknameId() + ") is not the active boost (" + active.getNicknameId() + ")");

			boosts.remove(boost.getType());
		}
		save();
	}

	public void addBoost(Boost boost) {
		if (boost.isPersonal()) {
			personalBoosts.add(boost);
		}
		else {
			if (hasBoost(boost.getType()))
				throw new InvalidInputException("Cannot activate boost " + boost.getNicknameId() + ", boost " + getBoost(boost.getType()).getNicknameId() + " is already active");

			boosts.put(boost.getType(), boost.getRefId());
		}
		save();
	}

	private void save() {
		new BoostConfigService().save(this);
	}

	public static class DiscordHandler {

		static void deleteHistoryAndSendMessage() {
			if (!Discord.isConnected())
				return;

			deleteHistory(DiscordHandler::sendMessage);
		}

		public static void editMessage() {
			if (!Discord.isConnected())
				return;

			getHistory().thenAcceptAsync(history -> {
				if (history.size() == 0) {
					sendMessage();
					return;
				}

				Iterator<Message> iterator = history.iterator();
				Message message = iterator.next();

				while (iterator.hasNext())
					iterator.next().delete().queue();

				message.editMessage(getMessage()).queue();
			});
		}

		@NotNull
		private static Message getMessage() {
			BoostConfig config = get();

			MessageBuilder builder = new MessageBuilder()
					.append("**Active Boosts**")
					.append(System.lineSeparator())
					.append(System.lineSeparator());

			Set<Boostable> boosts = config.getBoosts().keySet();
			if (boosts.isEmpty())
				builder.append("None");
			else
				for (Boostable type : boosts) {
					Boost boost = config.getBoost(type);
					builder.append(String.format("**%s** %s - %s (%s)", boost.getMultiplierFormatted(), StringUtils.camelCase(type), boost.getNickname(), boost.getTimeLeft()))
							.append(System.lineSeparator());
				}

			return builder.build();
		}

		private static void sendMessage() {
			getChannel().sendMessage(getMessage()).queue();
		}

		private static void deleteHistory(Runnable then) {
			getHistory().thenAcceptAsync(history -> {
				Iterator<Message> iterator = history.iterator();
				while (iterator.hasNext()) {
					iterator.next().delete().queue();
					if (!iterator.hasNext())
						then.run();
				}
			});
		}

		@NotNull
		private static CompletableFuture<List<Message>> getHistory() {
			return getChannel().getIterableHistory().takeAsync(100);
		}

		private static GuildMessageChannel getChannel() {
			return DiscordId.TextChannel.BOOSTS.get(Bot.RELAY.jda());
		}
	}

}
