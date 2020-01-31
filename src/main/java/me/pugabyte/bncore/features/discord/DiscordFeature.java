package me.pugabyte.bncore.features.discord;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.commands.SuggestCommand;
import me.pugabyte.bncore.utils.Tasks;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordFeature {
	@Getter
	private static JDA relayBot;
	private static String configPath = "discord.tokens.relayBot";

	static {
		BNCore.getInstance().addConfigDefault(configPath, "abcdef");
	}

	public DiscordFeature() {
		Tasks.async(() -> {
			try {
				relayBot = new JDABuilder(AccountType.BOT)
						.setToken(BNCore.getInstance().getConfig().getString(configPath))
//						.addEventListeners(new MessageListener())
						.addEventListeners(new CommandClientBuilder()
								.setPrefix("/")
								.setOwnerId(DiscordId.User.PUGABYTE.getId())
								.addCommand(new SuggestCommand())
								.build())
						.build()
						.awaitReady();

				BNCore.log("Successfully connected to Discord");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public static void shutdown() {
		try {
			if (relayBot != null) relayBot.shutdown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
