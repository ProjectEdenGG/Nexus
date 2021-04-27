package me.pugabyte.nexus.features.commands;

import eden.models.hours.Hours;
import eden.models.hours.HoursService;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.inviterewards.InviteRewards;
import me.pugabyte.nexus.models.inviterewards.InviteRewardsService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.vote.Voter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Aliases("invited")
@Permission("invite.rewards")
public class InviteRewardsCommand extends CustomCommand {

	InviteRewardsCommand(CommandEvent event) {
		super(event);
	}

	@Path("convert")
	void convert() {
		InviteRewardsService service = new InviteRewardsService();
		InviteRewards user;
		user = service.get(UUID.fromString("e9e07315-d32c-4df7-bd05-acfe51108234"));
		user.getInvited().addAll(Arrays.asList(
				UUID.fromString("d1729990-0ad4-4db8-8a95-779128e9fa1a"),
				UUID.fromString("c4f09cd1-4028-44a6-9e7b-bae40e1c27c4")
		));
		user = service.get(UUID.fromString("d1729990-0ad4-4db8-8a95-779128e9fa1a"));
		user.getInvited().addAll(Arrays.asList(
				UUID.fromString("8da5e4d7-bda7-455d-841a-eef0cd25f45d"),
				UUID.fromString("60bd466b-501d-4064-99e8-85a5a2c23501"),
				UUID.fromString("0b314d07-3b00-4edd-aecb-8ce810a1b555"),
				UUID.fromString("f11ac40b-f40a-49a5-865b-f75b20dad9e8"),
				UUID.fromString("76ef16fd-dfc3-470d-b150-967314c70c43"),
				UUID.fromString("9f656468-c399-41fb-850b-1ede702f40a3"),
				UUID.fromString("6133ca26-eb23-423b-b60b-4b6d48f2627c"),
				UUID.fromString("77966ca3-ac85-44b2-bcb0-b7c5f9342e86"),
				UUID.fromString("6613616e-c46d-440f-90bc-7f36e36cc036"),
				UUID.fromString("25458230-77a4-47a9-ac4d-728e1e6096bc"),
				UUID.fromString("61f84cda-c894-4273-aa5d-30508654fb9a"),
				UUID.fromString("435a83e9-488e-44c0-b351-8dab13001282"),
				UUID.fromString("08c6ea4b-32f4-4d15-bc49-c2086719b932"),
				UUID.fromString("7ca2dbc7-eecd-4f30-80d1-a1d76f0f0e70"),
				UUID.fromString("7cfe8f22-0ed5-450f-952c-2459ff79ede6"),
				UUID.fromString("24aeb9e0-0ff1-49a1-8f12-647ba2d6d1ea"),
				UUID.fromString("9a2ba9e8-ecee-400e-b3fa-2a14b67d8898")
		));
		user = service.get(UUID.fromString("86d7e0e2-c95e-4f22-8f99-a6e83b398307"));
		user.getInvited().addAll(Arrays.asList(
				UUID.fromString("e9e07315-d32c-4df7-bd05-acfe51108234"),
				UUID.fromString("33bff644-37b1-4ecd-949f-b46c010b14ae")
		));
		user = service.get(UUID.fromString("c4f09cd1-4028-44a6-9e7b-bae40e1c27c4"));
		user.getInvited().addAll(Arrays.asList(
				UUID.fromString("16df69e6-cd87-4cee-9a4a-67081c7946e8")
		));
		user = service.get(UUID.fromString("77966ca3-ac85-44b2-bcb0-b7c5f9342e86"));
		user.getInvited().addAll(Arrays.asList(
				UUID.fromString("56478eec-98b5-4be5-bf7f-df8d87bcb35b")
		));
		user = service.get(UUID.fromString("827b71ba-159d-4595-8bd7-f56608f9b795"));
		user.getInvited().addAll(Arrays.asList(
				UUID.fromString("2c71e62b-e11d-451f-bad8-b87b2dc63ddf")
		));
		user = service.get(UUID.fromString("eca5c891-d73d-43ed-ab50-23fe818afcc7"));
		user.getInvited().addAll(Arrays.asList(
				UUID.fromString("545bc534-bbe6-4349-8741-bec3c5b451b7")
		));
		user = service.get(UUID.fromString("b0a4215d-4e1b-4a1b-8c26-8cf397794f5a"));
		user.getInvited().addAll(Arrays.asList(
				UUID.fromString("ad2e9c2c-225c-4b70-a740-c0612c8ee6cd")
		));

		service.saveCache();
		send("done");
	}

	@Path("<username>")
	void send(Player invited) {
		Player inviter = player();
		if (inviter.equals(invited))
			error(colorize("You cannot invite yourself!"));

		if (Nerd.of(inviter).getFirstJoin().isAfter(Nerd.of(invited).getFirstJoin()))
			error("You joined after &e" + invited.getName() + "&c, so you can't have invited them!");

		if (hasBeenInvitedBefore(invited))
			error("&e" + invited.getName() + "&c has already confirmed being invited by someone else");

		if (!invited.hasPermission("invite.rewards.confirm"))
			error("The person you are inviting must be a &fMember &cor above.");

		if (getMinutesPlayed(invited) < 60)
			error("&e" + invited.getName() + "&c has to play for an hour before you can do that.");

		sendInviteConfirmation(inviter, invited);
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("confirm <inviter>")
	void confirm(Player inviter) {
		Player invited = player();
		if (Nerd.of(inviter).getFirstJoin().isAfter(Nerd.of(invited).getFirstJoin()))
			error("&e" + inviter.getName() + " &cjoined after you, so you can't have been invited by them!");

		if (hasBeenInvitedBefore(invited))
			error("&cYou have already confirmed being invited by someone else");

		if (getMinutesPlayed(invited) < 60)
			error("You have to play for an hour before you can do that.");

		send(inviter, PREFIX + "&e" + invited.getName() + "&3 has confirmed your invite; thank you for " +
				"helping Bear Nation grow! You earned &e15 vote points");
		send(invited, PREFIX + "You have confirmed &e" + inviter.getName() + "'s &3invite. Thank you " +
				"for flying Bear Nation!");
		reward(inviter);
		saveInvitation(inviter, invited);
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("deny <inviter>")
	void deny(Player inviter) {
		Player invited = player();
		send(inviter, PREFIX + "&e" + invited.getName() + "&3 has denied your invite confirmation.");
		send(invited, PREFIX + "You have denied &e" + inviter.getName() + "&3's invite.");
	}

	private static boolean hasBeenInvitedBefore(Player invited) {
		return new InviteRewardsService().hasBeenInvited(invited.getUniqueId());
	}

	private void sendInviteConfirmation(Player inviter, Player invited) {
		// Invited player
		send(invited, "");
		send(invited, json("  &3Did &e" + Nickname.of(inviter) + " &3invite you to Bear Nation?"));

		send(invited, json()
				.next("  &3Click one  ||").color(NamedTextColor.DARK_AQUA)
				.next("  &a&lYes  ").command("/invited confirm " + inviter.getName())
				.group()
				.next("&3||")
				.group()
				.next("  &c&lNo  ").command("/invited deny " + inviter.getName()));
		send(invited, "");

		// Inviter
		send(inviter, PREFIX + "Invite confirmation sent to &e" + invited.getName());
	}

	static void saveInvitation(Player inviter, Player invitee) {
		InviteRewards inviteRewards = new InviteRewardsService().get(inviter);
		inviteRewards.getInvited().add(invitee.getUniqueId());
		new InviteRewardsService().save(inviteRewards);
	}

	private void reward(Player inviter) {
		new Voter(inviter).givePoints(15);
	}

	private long getMinutesPlayed(Player player) {
		Hours hours = new HoursService().get(player.getUniqueId());
		return hours.getTotal() / 60;
	}

}
