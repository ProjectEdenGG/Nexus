package gg.projecteden.nexus.features.chat.events;

import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class DiscordChatEvent extends ChatEvent {
	private Member member;
	private PublicChannel channel;
	private final String originalMessage;
	private String message;
	private String permission;
	@Accessors(fluent = true)
	private boolean hasAttachments;
	private boolean filtered;
	private boolean bad;

	public DiscordChatEvent(Member member, PublicChannel channel, String originalMessage, String message, boolean hasAttachments, String permission) {
		this.member = member;
		this.channel = channel;
		this.originalMessage = originalMessage;
		this.message = message;
		this.hasAttachments = hasAttachments;
		this.permission = permission;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPermission() {
		return this.permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public Chatter getChatter() {
		if (member != null) {
			DiscordUser user = new DiscordUserService().getFromUserId(member.getUser().getId());
			if (user != null)
				return new ChatterService().get(PlayerUtils.getPlayer(user.getUuid()));
		}
		return null;
	}

	@Override
	public String getOrigin() {
		if (getChatter() != null)
			return getChatter().getNickname();
		return Discord.getName(member);
	}

	public TextChannel getDiscordTextChannel() {
		return channel.getDiscordTextChannel().get(Bot.RELAY.jda());
	}

	@Override
	public Set<Chatter> getRecipients() {
		return OnlinePlayers.getAll().stream()
						.filter(player -> player.hasPermission(permission))
						.map(player -> new ChatterService().get(player))
						.collect(Collectors.toSet());
	}

}
