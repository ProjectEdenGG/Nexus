package gg.projecteden.nexus.features.discord;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.vdurmont.emoji.EmojiManager;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.bridge.DiscordBridgeListener;
import gg.projecteden.nexus.features.commands.NicknameCommand.NicknameApprovalListener;
import gg.projecteden.nexus.features.discord.commands.TwitterDiscordCommand.TweetApprovalListener;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.DiscordId.User;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.EnumSet;

import static com.google.common.base.Strings.isNullOrEmpty;
import static gg.projecteden.utils.StringUtils.camelCase;

public enum Bot {

	KODA {
		@Override
		JDABuilder build() {
			return JDABuilder.createDefault(getToken())
					.addEventListeners(new DiscordListener(), new TweetApprovalListener(), new NicknameApprovalListener())
					// .addEventListeners(new DiscordCaptchaListener())
					.addEventListeners(getCommands().build());
		}

		@Override
		public String getId() {
			return User.KODA.getId();
		}
	},

	RELAY {
		@Override
		JDABuilder build() {
			return JDABuilder.createDefault(getToken())
					.addEventListeners(new DiscordBridgeListener())
					.addEventListeners(getCommands().setStatus(OnlineStatus.INVISIBLE).build());
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

	@SneakyThrows
	void connect() {
		if (this.jda != null) {
			debug("JDA already defined, aborting connection");
			return;
		}

		if (isNullOrEmpty(getToken())) {
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
		return "[Discord] [" + camelCase(this) + "] ";
	}

	@SneakyThrows
	protected CommandClientBuilder getCommands() {
		CommandClientBuilder commands = new CommandClientBuilder()
				.setPrefix("/")
				.setAlternativePrefix("!")
				.setOwnerId(User.GRIFFIN.getId())
				.setEmojis(EmojiManager.getForAlias("white_check_mark").getUnicode(), EmojiManager.getForAlias("warning").getUnicode(), EmojiManager.getForAlias("x").getUnicode())
				.setActivity(Activity.playing("Minecraft"));

		Reflections reflections = new Reflections(getClass().getPackage().getName());
		for (Class<? extends Command> command : reflections.getSubTypesOf(Command.class))
			if (Utils.canEnable(command))
				for (Class<? extends Command> superclass : Utils.getSuperclasses(command)) {
					HandledBy handledBy = superclass.getAnnotation(HandledBy.class);
					if (handledBy != null && handledBy.value() == this) {
						commands.addCommand(command.newInstance());
						break;
					}
				}
		return commands;
	}

}
