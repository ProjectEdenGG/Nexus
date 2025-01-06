package gg.projecteden.nexus.features.discord;

import gg.projecteden.api.discord.DiscordId.User;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.bridge.DiscordBridgeListener;
import gg.projecteden.nexus.features.discord.commands.TwitterAppCommand.TweetApprovalListener;
import gg.projecteden.nexus.features.store.perks.chat.NicknameCommand.NicknameApprovalListener;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public enum Bot {

	KODA {
		@Override
		JDABuilder build() {
			return JDABuilder.createDefault(getToken())
					.addEventListeners(
						new DiscordListener(),
						new TweetApprovalListener(),
						new NicknameApprovalListener()
					);
		}

		@Override
		public String getId() {
			return User.KODA.getId();
		}

		@Override
		void onConnect() {
			super.onConnect();
			Discord.registerAppCommands();
		}
	},

	RELAY {
		@Override
		JDABuilder build() {
			return JDABuilder.createDefault(getToken())
				.setStatus(OnlineStatus.INVISIBLE)
				.addEventListeners(new DiscordBridgeListener());
		}

		@Override
		public String getId() {
			return User.RELAY.getId();
		}
	};

	@Getter
	@Accessors(fluent = true)
	private JDA jda;

	abstract JDABuilder build();

	void onConnect() {
		new DiscordConnectedEvent(this).callEvent();
	}

	@Data
	public static class DiscordConnectedEvent extends Event {
		private final Bot bot;

		@Getter
		private static final HandlerList handlerList = new HandlerList();

		@Override
		public @NotNull HandlerList getHandlers() {
			return handlerList;
		}
	}

	@SneakyThrows
	void connect() {
		if (this.jda != null) {
			debug("JDA already defined, aborting connection");
			return;
		}

		if (Nullables.isNullOrEmpty(getToken())) {
			log("Token empty, aborting connection");
			return;
		}

		final JDA jda = build()
			.enableIntents(EnumSet.allOf(GatewayIntent.class))
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.enableCache(CacheFlag.ACTIVITY)
			.build()
			.awaitReady();

		if (jda == null) {
			log("Could not connect " + name() + " to Discord");
			return;
		}

		Tasks.sync(() -> {
			if (this.jda == null) {
				log("Connected to Discord");
				this.jda = jda;
				onConnect();
			} else {
				log("Discarding extra Discord connection");
				jda.shutdown();
			}
		});
	}

	void shutdown() {
		if (jda != null) {
			jda.cancelRequests();
			jda.shutdown();
			jda = null;
		}
	}

	protected String getToken() {
		return Nexus.getInstance().getConfig().getString("tokens.discord." + name().toLowerCase(), "");
	}

	public abstract String getId();

	private void log(String message) {
		Nexus.log(prefix() + message);
	}
	private void debug(String message) {
		Nexus.debug(prefix() + message);
	}

	@NotNull
	private String prefix() {
		return "[Discord] [" + StringUtils.camelCase(this) + "] ";
	}

}
