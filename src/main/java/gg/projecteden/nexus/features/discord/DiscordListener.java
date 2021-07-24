package gg.projecteden.nexus.features.discord;

import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.DiscordId.Role;
import gg.projecteden.nexus.features.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.discord.DiscordId.User;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.setting.Setting;
import gg.projecteden.nexus.models.setting.SettingService;
import gg.projecteden.nexus.utils.HttpUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
public class DiscordListener extends ListenerAdapter {

	@Override
	public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
		Tasks.async(() -> {
			String name = Discord.getName(event.getMember());
			String channel = event.getChannelJoined().getName();

			Nexus.fileLog("discord", name + " joined " + channel);
		});
	}

	@Override
	public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
		Tasks.async(() -> {
			String name = Discord.getName(event.getMember());
			String channel = event.getChannelLeft().getName();

			Nexus.fileLog("discord", name + " left " + channel);
		});
	}

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		Tasks.async(() -> {
			SettingService service = new SettingService();
			Setting setting = service.get("discord", "lockdown");

			if (setting.getBoolean())
				event.getMember().kick("This discord is currently on lockdown mode").queue();
			else {
				Tasks.waitAsync(5, () -> {
					Discord.addRole(event.getUser().getId(), Role.NERD);
					DiscordUser user = new DiscordUserService().getFromUserId(event.getUser().getId());
					if (user != null) {
						Discord.addRole(event.getUser().getId(), Role.VERIFIED);

						if (Nerd.of(user.getUuid()).getRank() == Rank.VETERAN)
							Discord.addRole(event.getUser().getId(), Role.VETERAN);

						if (Nexus.getPerms().playerHas(null, user.getOfflinePlayer(), "donated"))
							Discord.addRole(event.getUser().getId(), Role.SUPPORTER);
					}
				});
			}
		});
	}

	@Data
	private class RandomPugClubResponse {
		private String image;
		private String link;
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		Tasks.async(() -> {
			String name = Discord.getName(event.getMember());
			String channel = event.getChannel().getName();
			String message = event.getMessage().getContentRaw();

			if (Arrays.asList(TextChannel.STAFF_BRIDGE.getId(), TextChannel.BRIDGE.getId()).contains(event.getChannel().getId()))
				if (event.getMember().getUser().getId().equals(User.RELAY.getId()))
					return;

			for (Message.Attachment attachment : event.getMessage().getAttachments())
				message += " " + attachment.getUrl();

			Nexus.fileLog("discord", "[#" + channel + "] " + name + ": " + message.trim());

			if (TextChannel.BOTS.getId().equals(event.getChannel().getId())) {
				if (message.toLowerCase().startsWith(".pug")) {
					try {
						RandomPugClubResponse result = HttpUtils.mapJson(RandomPugClubResponse.class, "http://randompug.club/loaf");
						Discord.koda(result.getImage(), TextChannel.BOTS);
					} catch (Exception ex) {
						event.getChannel().sendMessage(stripColor(ex.getMessage())).queue();
						if (!(ex instanceof EdenException))
							ex.printStackTrace();
					}
				}
			}
		});
	}

}
