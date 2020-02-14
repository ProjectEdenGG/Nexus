package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
		send("&3Upcoming birthdays:");
		for (int i = 0; i < Math.min(amount, nerds.size()); i++) {
			send("&3" + (i + 1) + " &e" + nerds.get(i).getName() + " &7- " + ChronoUnit.DAYS.between(LocalDate.now(), getNextBirthday(nerds.get(i))) + " days");
		}
	}

	public LocalDate getNextBirthday(Nerd nerd) {
		LocalDate now = LocalDate.now();
		LocalDate birthday = nerd.getBirthday();
		boolean thisYear = true;
		if (birthday.getMonth().getValue() < now.getMonth().getValue() && birthday.getDayOfMonth() < now.getDayOfMonth())
			thisYear = false;
		return birthday.withYear(thisYear ? now.getYear() : now.getYear() + 1);
	}
}
