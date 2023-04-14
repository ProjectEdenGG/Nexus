package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BirthdayEndJob;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BirthdayEndJob.BirthdayEndJobBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Aliases("birthday")
public class BirthdaysCommand extends CustomCommand {
	private final NerdService service = new NerdService();
	@Getter
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

	public BirthdaysCommand(CommandEvent event) {
		super(event);
	}

	@Description("View a player's birthday")
	void of(@Optional("self") Nerd nerd) {
		if (nerd.getBirthday() == null)
			error((isSelf(nerd) ? "You haven't" : nerd.getNickname() + " hasn't") + " set a birthday");

		send(PREFIX + (isSelf(nerd) ? "Your" : nerd.getNickname() + "'s") + " birthday is &e" + formatter.format(nerd.getBirthday()));
	}

	@NoLiterals
	@Description("List upcoming birthdays")
	void list(@Optional("1") int page) {
		List<Nerd> nerds = service.getNerdsWithBirthdays();
		final LocalDate now = LocalDate.now();

		nerds.sort((nerd1, nerd2) -> (int) DAYS.between(now, getNextBirthday(nerd1)) - (int) DAYS.between(now, getNextBirthday(nerd2)));

		line();
		send(PREFIX + "Upcoming birthdays:");
		paginate(nerds, (nerd, index) -> {
			final JsonBuilder json = json("&3" + index + " &e" + nerd.getColoredName() + " &7- ");

			long until = DAYS.between(now, getNextBirthday(nerd));

			if (until == 0)
				json.next("&dToday!");
			else
				json.next("&7" + until + plural(" day", until));

			return json.hover(formatter.format(nerd.getBirthday()));
		}, "/birthdays", page);
	}

	@Description("Set your birthday")
	void set(LocalDate birthday, @Optional("self") @Permission(Group.SENIOR_STAFF) Nerd player) {
		final String formatted = formatter.format(birthday);

		if (!isStaff())
			if (player.getBirthday() != null)
				error("You have already set your birthday to &e" + formatted + ". If it is incorrect, please ask a staff member to change it.");

		player.setBirthday(birthday);
		service.save(player);
		send(PREFIX + (isSelf(player) ? "Your" : Nickname.of(player) + "'s") + " birthday has been set to &e" + formatted);
	}

	@Description("Unset your birthday")
	void unset(@Permission(Group.STAFF) Nerd player) {
		if (player.getBirthday() == null)
			error(player.getNickname() + " does not have a birthday set");

		player.setBirthday(null);
		service.save(player);
		send(PREFIX + (isSelf(player) ? "Your" : Nickname.of(player) + "'s") + " birthday has been unset");
	}

	@Permission(Group.ADMIN)
	@Description("Force a birthday announcement in Discord")
	void forceAnnounce(Nerd player) {
		announcement(player);
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

		new BadgeUserService().edit(nerd, badgeUser -> {
			if (badgeUser.getActive() != null)
				job.badge(badgeUser.getActive());

			badgeUser.give(Badge.BIRTHDAY);
			badgeUser.setActive(Badge.BIRTHDAY);
		});

		job.build().schedule(LocalDateTime.now().plusDays(1));
	}

	public LocalDate getNextBirthday(Nerd nerd) {
		LocalDate now = LocalDate.now();
		LocalDate birthday = nerd.getBirthday();
		boolean thisYear = !birthday.withYear(now.getYear()).isBefore(now);
		return birthday.withYear(thisYear ? now.getYear() : now.getYear() + 1);
	}

}
