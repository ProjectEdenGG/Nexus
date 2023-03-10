package gg.projecteden.nexus.models.party;

import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.party.Parties;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.scheduledjobs.jobs.party.InviteExpiryJob;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.parchment.HasOfflinePlayer;
import gg.projecteden.parchment.HasPlayer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.ComponentLike;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Party {
	private static final long INVITE_EXPIRY_MINUTES = 2;

	@NonNull
	private UUID id;
	@NonNull
	private UUID owner;
	private List<UUID> members = new ArrayList<>();
	private boolean isOpen;
	private List<UUID> pendingInvites = new ArrayList<>();

	Party(@NotNull UUID owner) {
		id = UUID.randomUUID();
		this.owner = owner;
	}

	public boolean contains(UUID player) {
		if (owner.equals(player))
			return true;
		return members.contains(player);
	}

	public List<Player> getOnlineMembers() {
		List<UUID> uuids = new ArrayList<>(members) {{
			add(0, owner);
		}};
		return uuids.stream()
			       .map(Nerd::of)
			       .filter(Nerd::isOnline)
			       .map(Nerd::getPlayer)
			       .collect(Collectors.toList());
	}

	public List<OfflinePlayer> getAllMembers() {
		List<UUID> uuids = new ArrayList<>(members) {{
			add(0, owner);
		}};
		return uuids.stream().map(Nerd::of).map(Nerd::getOfflinePlayer).collect(Collectors.toList());
	}

	public int size() {
		return getAllMembers().size();
	}

	public void invite(HasPlayer player) {
		pendingInvites.add(player.getPlayer().getUniqueId());
		if (members.isEmpty())
			PlayerUtils.send(owner, new JsonBuilder(Parties.PREFIX + Nerd.of(player.getPlayer()).getColoredName() + " &3has been invited to the party"));
		else
			broadcast(new JsonBuilder("&e" + Nerd.of(player.getPlayer()).getColoredName() + " &3has been invited to the party"));

		List<String> hover = new ArrayList<>();
		hover.add("&d&lParty Members:");
		hover.addAll(getAllMembers().stream().map(Nerd::of).map(Nerd::getColoredName).toList());

		PlayerUtils.send(player, new JsonBuilder(Parties.PREFIX + "&3You have been invited to a party with ").group()
									.next("&e" + Nerd.of(owner).getColoredName() +
										(members.isEmpty() ? "" : " and " + members.size() + (members.size() > 1 ? " others&3." : " other&3.")))
									.hover(hover));
		PlayerUtils.send(player, new JsonBuilder("&3 Click one &3 || &3 ").group()
									.next("&a&lAccept").command("/party accept " + id).hover("&eClick &3to accept").group()
									.next("&3 &3 || &3 ").group()
									.next("&c&lDeny").command("/party deny " + id).hover("&eClick &3to deny").group()
									.next("&3 &3 ||"));
		PartyManager.SERVICE.save();

		InviteExpiryJob job = new InviteExpiryJob(id, player.getPlayer().getUniqueId());
		job.schedule(LocalDateTime.now().plusMinutes(INVITE_EXPIRY_MINUTES));
	}

	public void expireInvite(UUID player) {
		pendingInvites.remove(player);
		if (getMembers().isEmpty())
			PlayerUtils.send(owner, new JsonBuilder(Parties.PREFIX + Nerd.of(player).getColoredName() + "'s &3invite has expired"));
		else
			broadcast(new JsonBuilder(Nerd.of(player).getColoredName() + "'s &3invite has expired"));
		PlayerUtils.send(player, new JsonBuilder(Parties.PREFIX + "&3Your invite to " + Nerd.of(owner).getColoredName() + "'s &3party has expired"));
		PartyManager.SERVICE.save();
		if (members.isEmpty())
			PartyManager.disband(this, true);
	}

	public void join(HasPlayer player) {
		if (members.isEmpty()) {
			Nerd.of(owner).sendMessage(new JsonBuilder(Parties.PREFIX + Nerd.of(player.getPlayer()).getColoredName() + " &3has joined the party"));
			if (WorldGroup.of(Nerd.of(owner)) != WorldGroup.MINIGAMES)
				Chatter.of(Nerd.of(owner)).joinSilent(StaticChannel.PARTY.getChannel());
		} else
			broadcast(new JsonBuilder(Nerd.of(player.getPlayer()).getColoredName() + " &3has joined the party"));
		members.add(player.getPlayer().getUniqueId());
		PlayerUtils.send(player, new JsonBuilder(Parties.PREFIX + "&3You have joined " + Nerd.of(owner).getColoredName() + "'s &3party"));
		pendingInvites.remove(player.getPlayer().getUniqueId());
		PartyManager.SERVICE.save();
		if (WorldGroup.of(player.getPlayer()) != WorldGroup.MINIGAMES)
			Chatter.of(player.getPlayer()).joinSilent(StaticChannel.PARTY.getChannel());
	}

	public void kick(HasOfflinePlayer player) {
		PlayerUtils.send(player, Parties.PREFIX + "&3You have been kicked from " + Nerd.of(owner).getColoredName() + "'s &3party");
		members.remove(player.getOfflinePlayer().getUniqueId());
		broadcast(new JsonBuilder(Nerd.of(player.getOfflinePlayer().getUniqueId()).getColoredName() + " &3has been kicked from the party"));
		PartyManager.SERVICE.save();
		Chatter.of(player.getOfflinePlayer()).leaveSilent(StaticChannel.PARTY.getChannel());
		if (members.isEmpty())
			PartyManager.disband(this, false);
	}

	public void kickOffline(UUID player) {
		members.remove(player);
		broadcast(new JsonBuilder(Nerd.of(player).getColoredName() + " &3has been kicked from the party due to being offline"));
		PartyManager.SERVICE.save();
		Chatter.of(Nerd.of(player)).leaveSilent(StaticChannel.PARTY.getChannel());
		if (members.isEmpty() || (owner.equals(player) && members.size() == 1))
			PartyManager.disband(this, false);
		else if (owner == player)
			promote(RandomUtils.randomElement(members), false);
	}

	public void leave(HasOfflinePlayer player) {
		PlayerUtils.send(player, Parties.PREFIX + "&3You have left " + Nerd.of(owner).getColoredName() + "'s &3party");
		members.remove(player.getOfflinePlayer().getUniqueId());
		if (owner == player.getOfflinePlayer().getUniqueId() && members.size() > 1)
			promote(RandomUtils.randomElement(members), false);
		broadcast(new JsonBuilder(Nerd.of(player.getOfflinePlayer().getUniqueId()).getColoredName() + " &3has left the party"));
		PartyManager.SERVICE.save();
		Chatter.of(player.getOfflinePlayer()).leaveSilent(StaticChannel.PARTY.getChannel());
		if (members.isEmpty() || (owner.equals(player.getOfflinePlayer().getUniqueId()) && members.size() == 1))
			PartyManager.disband(this, false);
	}

	public void promote(UUID uuid, boolean keepOwner) {
		if (keepOwner)
			members.add(owner);
		owner = uuid;
		members.remove(uuid);
		Tasks.wait(1, () -> broadcast(Nerd.of(uuid).getColoredName() + " &3has been promoted to &eparty leader"));
		PartyManager.SERVICE.save();
	}

	public void setOpen(boolean open) {
		this.isOpen = open;
		PartyManager.SERVICE.save();
		broadcast("&3The party has been set to &e" + (open ? "open" : "closed"));
	}

	public void broadcast(String message) {
		broadcast(new JsonBuilder(message));
	}

	public void broadcast(ComponentLike componentLike) {
		JsonBuilder builder = new JsonBuilder(Parties.PREFIX).next(componentLike);
		getOnlineMembers().forEach(p -> p.sendMessage(builder));
	}

	public void broadcastWithFilter(ComponentLike componentLike) {
		JsonBuilder builder = new JsonBuilder(Parties.PREFIX).next(componentLike);
		getOnlineMembers().stream().filter(p -> Chatter.of(p).hasJoined(StaticChannel.PARTY.getChannel())).forEach(p -> p.sendMessage(builder));
	}

	public void silenceChat(Player player) {
		Chatter.of(player).leaveSilent(StaticChannel.PARTY.getChannel());
		Tasks.wait(3, () -> {
			broadcastWithFilter(new JsonBuilder(Nerd.of(player).getColoredName() + " &3left the chat because they joined minigames"));
			PlayerUtils.send(player, new JsonBuilder(Parties.PREFIX + "&3You have left the party chat because you joined minigames"));
		});
	}

	public void rejoinChat(Player player) {
		Chatter.of(player).joinSilent(StaticChannel.PARTY.getChannel());
		Tasks.wait(3, () -> broadcast(Nerd.of(player).getColoredName() + " &3rejoined the chat"));
	}

}
