package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.models.nickname.Nickname;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Aliases("birthday")
public class BirthdaysCommand extends CustomCommand {
	NerdService service = new NerdService();

	public BirthdaysCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void birthday(@Arg("10") int amount) {
		List<Nerd> nerds = service.getNerdsWithBirthdays();
		nerds.sort((nerd1, nerd2) -> {
			LocalDate now = LocalDate.now();
			return (int) ChronoUnit.DAYS.between(now, getNextBirthday(nerd1)) - (int) ChronoUnit.DAYS.between(now, getNextBirthday(nerd2));
		});

		line();
		send("&3Upcoming birthdays:");
		line();
		for (int i = 0; i < Math.min(amount, nerds.size()); i++) {
			if (LocalDate.now().getDayOfYear() == getNextBirthday(nerds.get(i)).getDayOfYear())
				send("&3" + (i + 1) + " &e" + Nickname.of(nerds.get(i)) + " &7- Today");
			else {
				long until = ChronoUnit.DAYS.between(LocalDate.now(), getNextBirthday(nerds.get(i)));
				send(json()
						.next("&3" + (i + 1) + " &e" + Nickname.of(nerds.get(i)) + " &7- " + until + plural(" day", until))
						.hover(nerds.get(i).getBirthday().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
			}
		}
	}

	@Path("set <birthday> [player]")
	void set(LocalDate birthday, @Arg(value = "self", permission = "group.seniorstaff") Nerd nerd) {
		nerd.setBirthday(birthday);
		service.save(nerd);
		send(PREFIX + (isSelf(nerd) ? "Your" : Nickname.of(nerd) + "'s") + " birthday has been set to &e" + birthday.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + birthday.getDayOfMonth() + ", " + birthday.getYear());
	}

	public LocalDate getNextBirthday(Nerd nerd) {
		LocalDate now = LocalDate.now();
		LocalDate birthday = nerd.getBirthday();
		boolean thisYear = !birthday.withYear(now.getYear()).isBefore(now);
		return birthday.withYear(thisYear ? now.getYear() : now.getYear() + 1);
	}
}
