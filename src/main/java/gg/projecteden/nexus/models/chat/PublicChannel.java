package gg.projecteden.nexus.models.chat;

import gg.projecteden.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.commands.NearCommand.Near;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.Builder;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class PublicChannel implements Channel {
	private String name;
	private String nickname;
	private ChatColor color;
	private ChatColor messageColor;
	private TextChannel discordTextChannel;
	private ChatColor discordColor;
	private MuteMenuItem muteMenuItem;
	@Builder.Default
	private boolean censor = true;
	private boolean isPrivate;
	private boolean local;
	private boolean crossWorld;
	private String permission;
	private Rank rank;
	@Builder.Default
	private boolean persistent = true;

	public ChatColor getDiscordColor() {
		return discordColor == null ? color : discordColor;
	}

	public ChatColor getMessageColor() {
		return messageColor == null ? Channel.super.getMessageColor() : messageColor;
	}

	@Override
	public String getAssignMessage(Chatter chatter) {
		return "Now chatting in " + color + name;
	}

	public JsonBuilder getChatterFormat(Chatter chatter, Chatter viewer) {
		final Nerd nerd = Nerd.of(chatter);

		final JsonBuilder json = new JsonBuilder(color + "[" + nickname.toUpperCase() + "]")
			.hover(color + name + " &fChannel");

		if (viewer != null && !this.equals(viewer.getActiveChannel()))
			json.hover("&fUse &c/ch " + nickname.toLowerCase() + " &fto switch", "&fto this channel");

		json
			.group()
			.next(" ")
			.group()
			.next(nerd.getChatFormat(viewer))
			.next(" " + color + ChatColor.BOLD + "> " + getMessageColor())
			.hover("&3Rank: " + nerd.getRank().getColoredName());

		if (nerd.hasNickname())
			json.hover("&3Real name: &e" + nerd.getName());
		if (!nerd.getPronouns().isEmpty())
			json.hover("&3Pronouns: " + nerd.getPronouns().stream().map(pronoun -> "&e" + pronoun + "&3").collect(Collectors.joining(", ")));

		return json;
	}

	public Set<Chatter> getRecipients(Chatter chatter) {
		List<Player> recipients = new ArrayList<>();
		if (local)
			recipients.addAll(new Near(chatter.getPlayer()).includeUnseen().find());
		else if (crossWorld)
			recipients.addAll(OnlinePlayers.getAll());
		else
			recipients.addAll(OnlinePlayers.where().world(chatter.getOnlinePlayer().getWorld()).get());

		return recipients.stream()
				.map(player -> new ChatterService().get(player))
				.filter(_chatter -> _chatter.canJoin(this))
				.filter(_chatter -> _chatter.hasJoined(this))
				.collect(Collectors.toSet());
	}

	public String getPermission() {
		if (permission == null)
			return "chat.use." + name.toLowerCase();
		return permission;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PublicChannel that = (PublicChannel) o;
		return Objects.equals(name, that.name);
	}

}
