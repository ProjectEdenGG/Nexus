package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class BirthdaysCommand extends CustomCommand {

	public BirthdaysCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void birthday(@Arg("5") int amount) {
		NerdService service = new NerdService();
		List<Nerd> nerds = service.getNerdsWithBirthdays();
		nerds.sort(Comparator.comparing(Nerd::getBirthday));
		send("&3Upcoming birthdays:");
		for (int i = 0; i < amount; i++) {
			LocalDate now = LocalDate.now();
			LocalDate birthday = nerds.get(i).getBirthday();
			boolean thisYear = true;
			if (birthday.getMonth().getValue() < now.getMonth().getValue()) thisYear = false;
			LocalDate nextBirthday = birthday.withYear(thisYear ? now.getYear() : now.getYear() + 1);
			send("&3" + (i + 1) + " &e" + nerds.get(i).getName() + " &7- " + now.until(nextBirthday).getDays() + " days");
		}
	}


}
