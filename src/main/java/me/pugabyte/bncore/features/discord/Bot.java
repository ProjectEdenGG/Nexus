package me.pugabyte.bncore.features.discord;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.DiscordId.User;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.reflections.Reflections;

public enum Bot {

	KODA {
		@Override
		@SneakyThrows
		void connect() {
			String token = getToken("kodaBear");
			if (token != null && token.length() > 0)
				super.jda = new JDABuilder(AccountType.BOT)
						.setToken(token)
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

			String token = getToken("relayBot");
			if (token != null && token.length() > 0)
				super.jda = new JDABuilder(AccountType.BOT)
						.setToken(getToken("relayBot"))
//						.addEventListeners(new BridgeListener())
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

	private static String getToken(String id) {
		return BNCore.getInstance().getConfig().getString("tokens.discord." + id);
	}
}
