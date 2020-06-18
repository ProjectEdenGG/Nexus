package me.pugabyte.bncore.features.discord;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.bridge.DiscordBridgeListener;
import me.pugabyte.bncore.features.discord.DiscordId.User;
import me.pugabyte.bncore.utils.Tasks;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.reflections.Reflections;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.google.common.base.Strings.isNullOrEmpty;

public enum Bot {

	KODA {
		@Override
		@SneakyThrows
		JDA build() {
			return new JDABuilder(AccountType.BOT)
					.setToken(getToken())
					.addEventListeners(new DiscordListener())
					.addEventListeners(getCommands().build())
					.build()
					.awaitReady();
		}
	},

	RELAY {
		@Override
		@SneakyThrows
		JDA build() {
			return new JDABuilder(AccountType.BOT)
					.setToken(getToken())
					.addEventListeners(new DiscordBridgeListener())
					.addEventListeners(getCommands().setStatus(OnlineStatus.INVISIBLE).build())
					.build()
					.awaitReady();
		}
	};

	@Getter
	@Accessors(fluent = true)
	private JDA jda;

	abstract JDA build();

	void connect() {
		if (this.jda == null && !isNullOrEmpty(getToken())) {
			final JDA jda = build();
			Tasks.sync(() -> {
				if (this.jda == null)
					this.jda = jda;
				else {
					BNCore.log("Discarding extra Discord connection");
					jda.shutdown();
				}
			});
		}
	}

	void shutdown() {
		if (jda != null) {
			jda.shutdown();
			jda = null;
		}
	}

	protected String getToken() {
		return BNCore.getInstance().getConfig().getString("tokens.discord." + name().toLowerCase(), "");
	}

	@SneakyThrows
	protected CommandClientBuilder getCommands() {
		CommandClientBuilder commands = new CommandClientBuilder()
				.setPrefix("/")
				.setOwnerId(User.PUGABYTE.getId())
				.setActivity(Activity.playing("Minecraft"));

		Reflections reflections = new Reflections(getClass().getPackage().getName());
		for (Class<? extends Command> command : reflections.getSubTypesOf(Command.class)) {
			HandledBy handledBy = command.getAnnotation(HandledBy.class);
			if (handledBy != null && handledBy.value() == this)
				commands.addCommand(command.newInstance());
		}
		return commands;
	}

	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface HandledBy {
		Bot value();
	}
}
