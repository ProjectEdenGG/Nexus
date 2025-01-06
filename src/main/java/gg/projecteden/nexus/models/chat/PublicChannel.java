package gg.projecteden.nexus.models.chat;

import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.commands.NearCommand.Near;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.party.Party;
import gg.projecteden.nexus.models.party.PartyManager;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Builder;
import lombok.Builder.Default;
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
	@Default
	private boolean censor = true;
	private boolean isPrivate;
	private boolean local;
	private boolean crossWorld;
	private boolean party;
	private String permission;
	private Rank rank;
	@Default
	private String joinError = "You do not have permission to speak in that channel";
	@Default
	private List<WorldGroup> disabledWorldGroups = new ArrayList<>();
	@Default
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

	public JsonBuilder getChatterFormat(Chatter chatter, Chatter viewer, boolean isDiscord) {
		return getChatterFormat(chatter, null, viewer, isDiscord);
	}

	public JsonBuilder getChatterFormat(Chatter chatter, JsonBuilder chatterName, Chatter viewer, boolean isDiscord) {
		final JsonBuilder json = new JsonBuilder();

		if (isDiscord) {
			json.next(getDiscordColor() + "[D]")
				.hover(SocialMediaSite.DISCORD.getColor() + "&lDiscord &fChannel")
				.hover("&fMessages sent in &c#bridge &fon our")
				.hover("&c/discord &fare shown in this channel");
		} else {
			json.next(color + "[" + nickname.toUpperCase() + "]")
				.hover(color + name + " &fChannel");

			if (viewer != null && !this.equals(viewer.getActiveChannel()))
				json.hover("&fUse &c/ch " + nickname.toLowerCase() + " &fto switch", "&fto this channel");
		}

		json.group().next(" ").group();

		if (chatter != null)
			json.next(getChatterFormat(chatter, viewer));
		else if (chatterName != null)
			json.next(chatterName);
		else
			throw new InvalidInputException("No chatter provided");

		json.group().next(" " + (isDiscord ? getDiscordColor() : color) + ChatColor.BOLD + "> " + getMessageColor());

		return json;
	}

	private JsonBuilder getChatterFormat(Chatter chatter, Chatter viewer) {
		final JsonBuilder json = new JsonBuilder();

		final Nerd nerd = Nerd.of(chatter);
		json.next(nerd.getChatFormat(viewer));

		if (!Koda.is(nerd))
			json.hover("&3Rank: " + nerd.getRank().getColoredName());
		if (nerd.hasNickname())
			json.hover("&3Real name: &e" + nerd.getName());
		if (Nullables.isNotNullOrEmpty(nerd.getPronouns()))
			json.hover("&3Pronouns: " + nerd.getPronouns().stream().map(pronoun -> "&e" + pronoun + "&3").collect(Collectors.joining(", ")));
		if (Nullables.isNotNullOrEmpty(nerd.getFilteredPreferredNames()))
			json.hover(StringUtils.plural("&3Preferred name", nerd.getFilteredPreferredNames().size()) + ": &e" + String.join("&3, &e", nerd.getFilteredPreferredNames()));

		return json;
	}

	public Set<Chatter> getRecipients(Chatter chatter) {
		List<Player> recipients = new ArrayList<>();
		if (local)
			recipients.addAll(new Near(chatter.getPlayer()).includeUnseen().find());
		else if (crossWorld)
			recipients.addAll(OnlinePlayers.getAll());
		else if (party) {
			Party chatterParty = PartyManager.of(chatter);
			if (chatterParty != null)
				recipients.addAll(chatterParty.getOnlineMembers());
		}
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
