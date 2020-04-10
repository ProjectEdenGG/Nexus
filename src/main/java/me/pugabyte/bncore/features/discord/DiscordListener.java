package me.pugabyte.bncore.features.discord;

import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.features.discord.DiscordId.User;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.Tasks;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Arrays;

@NoArgsConstructor
public class DiscordListener extends ListenerAdapter {

	@Override
	public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
		Tasks.async(() -> {
			String name = Discord.getName(event.getMember());
			String channel = event.getChannelJoined().getName();

			BNCore.fileLog("discord", name + " joined " + channel);
		});
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		Tasks.async(() -> {
			String name = Discord.getName(event.getMember(), event.getAuthor());
			String channel = event.getChannel().getName();
			String message = event.getMessage().getContentRaw();

			if (Arrays.asList(Channel.STAFF_ALERTS.getId(), Channel.STAFF_BRIDGE.getId(), Channel.BRIDGE.getId()).contains(event.getChannel().getId()))
				if (event.getMember().getUser().getId().equals(User.RELAY.getId()))
					return;

			for (Message.Attachment attachment : event.getMessage().getAttachments())
				message += " " + attachment.getUrl();

			BNCore.fileLog("discord", "[#" + channel + "] " + name + ": " + message.trim());
		});
	}

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		Tasks.waitAsync(5, () -> {
			Discord.addRole(event.getUser().getId(), Role.NERD);
			DiscordUser user = new DiscordService().getFromUserId(event.getUser().getId());
			if (!Strings.isNullOrEmpty(user.getUserId()))
				Discord.addRole(event.getUser().getId(), Role.VERIFIED);
		});
	}

}
