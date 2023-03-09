package gg.projecteden.nexus.features.party;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.party.PartyManager;
import gg.projecteden.nexus.models.party.Party;
import gg.projecteden.nexus.models.party.PartyService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@Permission(Group.ADMIN)
@Redirect(from = {"/partychat", "/pchat", "/pc"}, to = "/ch p")
public class PartyCommand extends CustomCommand {

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
	@Path("join <uuid>")
	void join(UUID uuid) {
		Party party = PartyManager.byPartyId(uuid);
		if (party == null)
			error("That party no longer exists");
		if (party.contains(uuid()))
			error("You are already in the party");
		checkInvites(uuid);
		if (party.contains(uuid()))
			error("You are already in the party");
		if (PartyManager.of(player()) != null && PartyManager.of(player()) != party)
			error("You must leave your current party before joining another");
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
		if (PartyManager.byPartyId(uuid) == null)
			error("You do not have a pending invite from that player");
		if (!PartyManager.byPartyId(uuid).getPendingInvites().contains(uuid()))
			error("You do not have a pending invite from that player");
	}

}
