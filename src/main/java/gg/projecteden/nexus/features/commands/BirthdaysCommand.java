package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.offline.OfflineMessage;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BirthdayEndJob;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BirthdayEndJob.BirthdayEndJobBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Aliases("birthday")
public class BirthdaysCommand extends CustomCommand {
	private final NerdService service = new NerdService();
	@Getter
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

	public BirthdaysCommand(CommandEvent event) {
		super(event);
	}

	@Path("of [player]")
	@Description("View a player's birthday")
	void of(@Arg("self") Nerd nerd) {
		if (nerd.getBirthday() == null)
			error((isSelf(nerd) ? "You haven't" : nerd.getNickname() + " hasn't") + " set a birthday");

		send(PREFIX + (isSelf(nerd) ? "Your" : nerd.getNickname() + "'s") + " birthday is &e" + formatter.format(nerd.getBirthday()));
	}

	@Path("[page]")
	@Description("List upcoming birthdays")
	void list(@Arg("1") int page) {
		List<Nerd> nerds = service.getNerdsWithBirthdays();
		final LocalDate now = LocalDate.now();

		nerds.sort((nerd1, nerd2) -> (int) ChronoUnit.DAYS.between(now, getNextBirthday(nerd1)) - (int) ChronoUnit.DAYS.between(now, getNextBirthday(nerd2)));

		line();
		send(PREFIX + "Upcoming birthdays:");
		paginate(nerds, (nerd, index) -> {
			final JsonBuilder json = json("&3" + index + " &e" + nerd.getColoredName() + " &7- ");

			long until = ChronoUnit.DAYS.between(now, getNextBirthday(nerd));

			if (until == 0)
				json.next("&dToday!");
			else
				json.next("&7" + until + plural(" day", until));

			return json.hover(formatter.format(nerd.getBirthday()));
		}, "/birthdays", page);
	}

	@Path("set <birthday> [player]")
	@Description("Set your birthday")
	void set(LocalDate birthday, @Arg(value = "self", permission = Group.SENIOR_STAFF) Nerd nerd) {
		final String formatted = formatter.format(birthday);

		if (!isStaff())
			if (nerd.getBirthday() != null)
				error("You have already set your birthday to &e" + formatted + ". If it is incorrect, please ask a staff member to change it.");

		nerd.setBirthday(birthday);
		service.save(nerd);
		send(PREFIX + (isSelf(nerd) ? "Your" : Nickname.of(nerd) + "'s") + " birthday has been set to &e" + formatted);
	}

	@Path("unset [player]")
	@Description("Unset your birthday")
	void unset(@Arg(permission = Group.STAFF) Nerd nerd) {
		if (nerd.getBirthday() == null)
			error(nerd.getNickname() + " does not have a birthday set");

		nerd.setBirthday(null);
		service.save(nerd);
		send(PREFIX + (isSelf(nerd) ? "Your" : Nickname.of(nerd) + "'s") + " birthday has been unset");
	}

	@Permission(Group.ADMIN)
	@Path("forceAnnounce [player]")
	@Description("Force a birthday announcement in Discord")
	void forceAnnounce(Nerd nerd) {
		announcement(nerd);
	}

	public static boolean ownsPartyHat(CostumeUser user, Costume partyHat) {
		if (user.getOwnedCostumes().contains(partyHat.getId()))
			return true;

		return user.getTemporarilyOwnedCostumes().contains(partyHat.getId());
	}

	public static void announcement(Nerd nerd) {
		DiscordUser user = new DiscordUserService().get(nerd);

		if (user.getMember() != null) {
			final Role role = nerd.getRank().isStaff() ? Role.STAFF_BIRTHDAY : Role.BIRTHDAY;
			user.addRole(role);

			Discord.koda("Happy Birthday " + user.getMember().getAsMention() + "!", TextChannel.BIRTHDAYS).thenAccept(message ->
				message.createThreadChannel("Wish " + nerd.getNickname() + " a happy birthday!").queue());
		}

		final BirthdayEndJobBuilder job = BirthdayEndJob.builder().uuid(nerd.getUuid());

		if (nerd.isOnline())
			Koda.say("Happy Birthday &e" + nerd.getNickname() + "&f!");

		new BadgeUserService().edit(nerd, badgeUser -> {
			if (badgeUser.getActive() != null)
				job.badge(badgeUser.getActive());

			badgeUser.give(Badge.BIRTHDAY);
			badgeUser.setActive(Badge.BIRTHDAY);
		});

		Costume partyHat = Costume.of("hat/misc/party_hat");
		if (partyHat != null) {
			CostumeUserService costumeUserService = new CostumeUserService();
			CostumeUser costumeUser = costumeUserService.get(nerd);
			costumeUser.getBirthdayCostumes().add(partyHat.getId());
			costumeUserService.save(costumeUser);

			if (!ownsPartyHat(costumeUser, partyHat)) {
				OfflineMessage.send(nerd, new JsonBuilder(Koda.getDmFormat() + "You have been given access to the " +
					"&bparty hat costume &efor the duration of your birthday. Equip it in &b/costumes"));
			}
		}

		job.build().schedule(LocalDateTime.now().plusDays(1));
	}

	public LocalDate getNextBirthday(Nerd nerd) {
		LocalDate now = LocalDate.now();
		LocalDate birthday = nerd.getBirthday();
		boolean thisYear = !birthday.withYear(now.getYear()).isBefore(now);
		return birthday.withYear(thisYear ? now.getYear() : now.getYear() + 1);
	}

}
