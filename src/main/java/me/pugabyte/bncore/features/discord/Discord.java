package me.pugabyte.bncore.features.discord;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.commands.SuggestCommand;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.ChatColor;

public class Discord {
	@Getter
	private static JDA relayBot;
	private static String configPath = "discord.tokens.relayBot";

	static {
		BNCore.getInstance().addConfigDefault(configPath, "abcdef");
	}

	public Discord() {
		Tasks.async(() -> {
			try {
				relayBot = new JDABuilder(AccountType.BOT)
						.setToken(BNCore.getInstance().getConfig().getString(configPath))
//						.addEventListeners(new BridgeListener())
						.addEventListeners(new CommandClientBuilder()
								.setPrefix("/")
								.setOwnerId(DiscordId.User.PUGABYTE.getId())
								.addCommand(new SuggestCommand())
								.setStatus(OnlineStatus.INVISIBLE)
								.setActivity(Activity.playing("Minecraft"))
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
			if (relayBot != null) {
				relayBot.shutdown();
				relayBot = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Guild getGuild() {
		return relayBot.getGuildById(DiscordId.Guild.BEAR_NATION.getId());
	}

	public static void send(String message, DiscordId.Channel... targets) {
		for (DiscordId.Channel target : targets) {
			TextChannel channel = relayBot.getTextChannelById(target.getId());
			if (channel != null)
				channel.sendMessage(ChatColor.stripColor(StringUtils.colorize(message))).queue();
		}
	}

}
