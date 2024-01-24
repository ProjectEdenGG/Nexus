package gg.projecteden.nexus.features.party;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.party.*;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Aliases("p")
@Redirect(from = {"/partychat", "/pchat", "/pc"}, to = "/ch p")
public class PartyCommand extends CustomCommand {

	@Getter
	private static final int PARTY_SIZE_LIMIT = 5;

	public PartyCommand(CommandEvent event) {
		super(event);
		PREFIX = "&8&l[&dParty&8&l] &3";
	}

	@Path("invite <player>")
	@Description("Invite a player to your party")
	void invite(Player player) {
		if (isSelf(player))
			error("You cannot invite yourself to a party");
		if (PartyManager.of(player()) == null) {
			Party party = PartyManager.create(player());
			party.invite(player);
			return;
		}
		testForLeader();
		Party party = PartyManager.of(player());
		if (party.contains(player.getUniqueId()))
			error("That person is already in the party");
		if (party.getPendingInvites().contains(player.getUniqueId()))
			error("You have already invited that player");
		PartyManager.of(player()).invite(player);
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("accept <uuid>")
	void accept(UUID uuid) {
		Party party = PartyManager.byPartyId(uuid);
		if (party == null)
			error("That party no longer exists");
		if (party.contains(uuid()))
			error("You are already in the party");
		checkInvites(uuid);
		if (PartyManager.of(player()) != null && PartyManager.of(player()) != party)
			error("You must leave your current party before joining another");
		if (party.size() >= PARTY_SIZE_LIMIT)
			error("That party has reached it's max player size");
		party.join(player());
	}

	@Path("join <player>")
	@Description("Join a player's open party")
	void join(Player player) {
		Party party = PartyManager.of(player);
		if (party == null)
			error("That player is not in a party");
		if (party.contains(uuid()))
			error("You are already in the party");
		if (!party.isOpen() && !party.getPendingInvites().contains(uuid()))
			error("You must be invited to this party to join");
		if (PartyManager.of(player()) != null && PartyManager.of(player()) != party)
			error("You must leave your current party before joining another");
		if (party.size() >= PARTY_SIZE_LIMIT)
			error("That party has reached it's max player size");
		party.join(player());
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("deny <uuid>")
	void deny(UUID uuid) {
		Party party = PartyManager.byPartyId(uuid);
		if (party == null)
			error("That party no longer exists");
		if (party.contains(uuid()))
			error("You are already in the party");
		checkInvites(uuid);
		if (party.getMembers().isEmpty())
			send(party.getOwner(), json(PREFIX + nerd().getColoredName() + " &3has denied the invitation"));
		else
			party.broadcast(json(nickname() + " &3has denied the invitation"));
		party.getPendingInvites().remove(uuid());
		send(json("&3You denied the party invitation"));
		new PartyService().save();
	}

	@Path("kick <player>")
	@Description("Kick a player from your party")
	void kick(OfflinePlayer player) {
		testForParty();
		testForLeader();
		Party party = PartyManager.of(player());
		if (!party.contains(player.getUniqueId()))
			error("That person is not in the party");
		party.kick(player);
	}

	@Path("leave")
	@Description("Leave your current party")
	void leave() {
		testForParty();
		PartyManager.of(player()).leave(player());
	}

	@Path("disband")
	@Description("Disband your current party")
	void disband() {
		testForParty();
		testForLeader();
		PartyManager.disband(PartyManager.of(player()), false);
	}

	@Path("promote <player>")
	@Description("Promote a party member to party leader")
	void promote(Player player) {
		testForParty();
		testForLeader();
		Party party = PartyManager.of(player());
		if (!party.contains(player.getUniqueId()))
			error("That person is not in the party");
		party.promote(player.getUniqueId(), true);
	}

	@Path("info")
	@Description("View information about your party")
	void info() {
		testForParty();
		Party party = PartyManager.of(player());
		send(PREFIX + "&d&lYour Party &e(" + party.size() + "/" + PARTY_SIZE_LIMIT + ")&d:");
		send("");
		send("&eLeader: " + Nerd.of(party.getOwner()).getColoredName());
		party.getMembers().forEach(p -> send(" &e- " + Nerd.of(p).getColoredName()));
	}

	@Path("open")
	@Description("Allow anyone to join your party without invites")
	void open() {
		testForParty();
		testForLeader();
		PartyManager.of(player()).setOpen(true);
	}

	@Path("close")
	@Description("Require invites to join your party")
	void close() {
		testForParty();
		testForLeader();
		PartyManager.of(player()).setOpen(false);
	}

	@Path("warp")
	@Description("Warp your party to you")
	void warp() {
		testForParty();
		testForLeader();
		PartyManager.of(player()).warp();
	}

	@Path("settings xpshare [on/off]")
	@Description("Toggle sharing your exp with nearby party members")
	void xpShare(Boolean share) {
		PartyUserService service = new PartyUserService();
		PartyUser user = service.get(uuid());
		user.setXpShare(share == null ? !user.isXpShare() : share);
		service.save(user);
		send(PREFIX + "&3XP Share turned &e" + (user.isXpShare() ? "on" : "off"));
	}

	@Path("settings warp [on/off]")
	@Description("Toggle being automatically warped by the party leader")
	void instantWarp(Boolean warp) {
		PartyUserService service = new PartyUserService();
		PartyUser user = service.get(uuid());
		user.setInstantWarp(warp == null ? !user.isInstantWarp() : warp);
		service.save(user);
		send(PREFIX + "&3Instant Warp turned &e" + (user.isInstantWarp() ? "on" : "off"));
	}

	@Path("admin list")
	@Description("List all parties")
	@Permission(Group.ADMIN)
	void adminList() {
		List<Party> parties = new PartyService().get0().getParties();
		if (parties.isEmpty())
			error("There are no active parties");
		send("&3There are &e" + parties.size() + " &3active parties");
		line();
		parties.forEach(party -> {
			send(Nerd.of(party.getOwner()).getColoredName() + "'s &dParty &e(" + party.size() + "/5)");
			party.getMembers().forEach(member -> {
				send(" &e- " + Nerd.of(member).getColoredName());
			});
			line();
		});
	}


	void testForParty() {
		if (PartyManager.of(player()) == null)
			error("You are not in a party");
	}

	void testForLeader() {
		if (!PartyManager.of(player()).getOwner().equals(uuid()))
			error("You must be the leader of the party to run this command");
	}

	void checkInvites(UUID uuid) {
		Party party = PartyManager.byPartyId(uuid);
		if (party == null)
			error("You do not have a pending invite from that player");
		if (!party.getPendingInvites().contains(uuid()))
			error("You do not have a pending invite from that player");
	}

}
