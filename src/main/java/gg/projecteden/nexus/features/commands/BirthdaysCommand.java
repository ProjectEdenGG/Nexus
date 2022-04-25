package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BirthdaysRemoveRoleJob;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.utils.DiscordId.Role;
import gg.projecteden.utils.DiscordId.TextChannel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;

import static java.time.temporal.ChronoUnit.DAYS;

@Aliases("birthday")
@Description("View player birthdays")
public class BirthdaysCommand extends CustomCommand {
	private final NerdService service = new NerdService();

	public BirthdaysCommand(CommandEvent event) {
		super(event);
	}

	@Path("[page]")
	void birthday(@Arg("1") int page) {
		List<Nerd> nerds = service.getNerdsWithBirthdays();
		final LocalDate now = LocalDate.now();

		nerds.sort((nerd1, nerd2) -> (int) DAYS.between(now, getNextBirthday(nerd1)) - (int) DAYS.between(now, getNextBirthday(nerd2)));

		final BiFunction<Nerd, String, JsonBuilder> formatter = (nerd, index) -> {
			final JsonBuilder json = json("&3" + index + " &e" + nerd.getColoredName() + " &7- ");

			long until = DAYS.between(now, getNextBirthday(nerd));

			if (until == 0)
				json.next("&dToday!");
			else
				json.next("&7" + until + plural(" day", until));

			return json.hover(nerd.getBirthday().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
		};

		line();
		send(PREFIX + "Upcoming birthdays:");
		paginate(nerds, formatter, "/birthdays", page);
	}

	@Path("set <birthday> [player]")
	void set(LocalDate birthday, @Arg(value = "self", permission = Group.SENIOR_STAFF) Nerd nerd) {
		nerd.setBirthday(birthday);
		service.save(nerd);
		send(PREFIX + (isSelf(nerd) ? "Your" : Nickname.of(nerd) + "'s") + " birthday has been set to &e" + birthday.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + birthday.getDayOfMonth() + ", " + birthday.getYear());
	}

	@Permission(Group.ADMIN)
	@Path("forceAnnounce [player]")
	void forceAnnounce(DiscordUser user) {
		announcement(user);
	}

	public static void announcement(DiscordUser user) {
		Nerd nerd = Nerd.of(user);
		final Role role = nerd.getRank().isStaff() ? Role.STAFF_BIRTHDAY : Role.BIRTHDAY;
		user.addRole(role);

		Discord.koda("Happy Birthday " + user.getMember().getAsMention() + "!", TextChannel.BIRTHDAYS).thenAccept(message ->
			message.createThreadChannel("Wish " + nerd.getNickname() + " a happy birthday!").queue());

		new BirthdaysRemoveRoleJob(nerd.getUuid()).schedule(LocalDateTime.now().plusDays(1));
	}

	public LocalDate getNextBirthday(Nerd nerd) {
		LocalDate now = LocalDate.now();
		LocalDate birthday = nerd.getBirthday();
		boolean thisYear = !birthday.withYear(now.getYear()).isBefore(now);
		return birthday.withYear(thisYear ? now.getYear() : now.getYear() + 1);
	}

}
