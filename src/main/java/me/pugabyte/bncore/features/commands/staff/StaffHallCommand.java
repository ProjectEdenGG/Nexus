package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;

import java.time.LocalDate;

import static me.pugabyte.bncore.utils.StringUtils.shortDateFormat;
import static me.pugabyte.bncore.utils.StringUtils.shortDateTimeFormat;

public class StaffHallCommand extends CustomCommand {

	public StaffHallCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("view <player>")
	void view(Nerd nerd) {
		line(4);

		send("&e&lIGN: &3" + nerd.getName());
		send("&e&lRank: &3" + nerd.getRank());
		if (!isNullOrEmpty(nerd.getPreferredName()))
			send("&e&lPreferred name: &3" + nerd.getPreferredName());
		if (nerd.getBirthday() != null)
			send("&e&lBirthday: &3" + shortDateFormat(nerd.getBirthday()) + " (" + nerd.getBirthday().until(LocalDate.now()).getYears() + " years)");
		if (nerd.getFirstJoin() != null)
			send("&e&lJoin date: &3" + shortDateTimeFormat(nerd.getFirstJoin()));
		if (nerd.getPromotionDate() != null)
			send("&e&lPromotion date: &3" + shortDateFormat(nerd.getPromotionDate()));

		line();

		if (!isNullOrEmpty(nerd.getAbout())) {
			send("&e&lAbout me: &3" + nerd.getAbout());
			line();
		}
		if (nerd.isMeetMeVideo()) {
			send(json("&eMeet me! &chttps://bnn.gg/meet/" + nerd.getName().toLowerCase()).url("https://bnn.gg/meet/" + nerd.getName().toLowerCase()));
			line();
		}
	}

}
