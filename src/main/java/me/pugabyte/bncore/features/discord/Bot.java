package me.pugabyte.bncore.features.discord;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.bridge.BridgeListener;
import me.pugabyte.bncore.features.discord.DiscordId.User;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.reflections.Reflections;

import static com.google.common.base.Strings.isNullOrEmpty;

public enum Bot {

	KODA {
		@Override
		@SneakyThrows
		void connect() {
			if (!isNullOrEmpty(getToken()))
				super.jda = new JDABuilder(AccountType.BOT)
						.setToken(getToken())
						.build()
						.awaitReady();
		}
	},

	RELAY {
		@Override
		@SneakyThrows
		void connect() {
			CommandClientBuilder commands = new CommandClientBuilder()
					.setPrefix("/")
					.setOwnerId(User.PUGABYTE.getId())
					.setStatus(OnlineStatus.INVISIBLE)
					.setActivity(Activity.playing("Minecraft"));

			Reflections reflections = new Reflections(getClass().getPackage().getName());
			for (Class<? extends Command> command : reflections.getSubTypesOf(Command.class))
				commands.addCommand(command.newInstance());

			if (!isNullOrEmpty(getToken()))
				super.jda = new JDABuilder(AccountType.BOT)
						.setToken(getToken())
						.addEventListeners(new BridgeListener(), new DiscordListener())
						.addEventListeners(commands.build())
						.build()
						.awaitReady();
		}
	};

	@Getter
	@Accessors(fluent = true)
	private JDA jda;

	abstract void connect();

	void shutdown() {
		if (jda != null) {
			jda.shutdown();
			jda = null;
		}
	}

	protected String getToken() {
		return BNCore.getInstance().getConfig().getString("tokens.discord." + name().toLowerCase());
	}
}
