package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Aliases("birthday")
public class BirthdaysCommand extends CustomCommand {

	public BirthdaysCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void birthday(@Arg("5") int amount) {
		NerdService service = new NerdService();
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
				send("&3" + (i + 1) + " &e" + nerds.get(i).getName() + " &7- Today");
			else
				json("&3" + (i + 1) + " &e" + nerds.get(i).getName() + " &7- " +
						ChronoUnit.DAYS.between(LocalDate.now(), getNextBirthday(nerds.get(i))) + " days").hover(nerds.get(i).getBirthday().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		}
	}

	@Path("[set]")
	void set() {
		if (arg(2) == null) format();
		NerdService service = new NerdService();
		Nerd nerd = new Nerd();
		nerd.fromPlayer(player());
		try {
			LocalDate birthday = LocalDate.parse(arg(2), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
			nerd.setBirthday(birthday);
			service.save(nerd);
			send(PREFIX + "Your birthday has been set to &e" + birthday.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + birthday.getDayOfMonth() + ", " + birthday.getYear());
		} catch (Exception e) {
			format();
		}

	}

	@Path("[format]")
	void format() {
		error("Correct birthday format: MM/dd/yyyy");
	}

	public LocalDate getNextBirthday(Nerd nerd) {
		LocalDate now = LocalDate.now();
		LocalDate birthday = nerd.getBirthday();
		boolean thisYear = true;
		if (birthday.getMonth().getValue() < now.getMonth().getValue() ||
				(birthday.getMonth().getValue() == now.getMonth().getValue() && birthday.getDayOfMonth() <= now.getDayOfMonth()))
			thisYear = false;
		return birthday.withYear(thisYear ? now.getYear() : now.getYear() + 1);
	}
}
