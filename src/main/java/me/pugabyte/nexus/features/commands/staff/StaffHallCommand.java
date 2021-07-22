package me.pugabyte.nexus.features.commands.staff;

import com.google.common.base.Strings;
import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

import static eden.utils.TimeUtils.shortDateFormat;
import static eden.utils.TimeUtils.shortDateTimeFormat;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
public class StaffHallCommand extends CustomCommand implements Listener {

	public StaffHallCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(Time.MINUTE, Time.HOUR.x(6), StaffHallCommand::writeHtml);
	}

	private static void writeHtml() {
		File folder = Paths.get("plugins/website/meetthestaff/").toFile();
		if (!folder.exists())
			folder.mkdir();

		Tasks.async(() -> Rank.getStaff().forEach(rank -> rank.getNerds().forEach(staff -> {
			try {
				String html = "";
				if (!Strings.isNullOrEmpty(staff.getPreferredName()))
					html += "<span style=\"font-weight: bold;\">Preferred name:</span> " + staff.getPreferredName() + "<br/>";
				if (staff.getBirthday() != null)
					html += "<span style=\"font-weight: bold;\">Birthday:</span> " + shortDateFormat(staff.getBirthday())
						+ " (" + staff.getBirthday().until(LocalDate.now()).getYears() + " years)<br/>";
				if (staff.getPromotionDate() != null)
					html += "<span style=\"font-weight: bold;\">Promotion date:</span> " + shortDateFormat(staff.getPromotionDate()) + "<br/>";
				html += "<br/>";
				if (!Strings.isNullOrEmpty(staff.getAbout()))
					html += "<span style=\"font-weight: bold;\">About me:</span> " + staff.getAbout();

				File file = Paths.get("plugins/website/meetthestaff/" + staff.getUuid() + ".html").toFile();
				if (!file.exists())
					file.createNewFile();
				Files.write(file.toPath(), html.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		})));
	}

	@Path
	void tp() {
		runCommand("warp staffhall");
	}

	@Path("view <player>")
	void view(Nerd nerd) {
		line(4);

		send("&e&lNickname: &3" + nerd.getNickname());
		send("&e&lIGN: &3" + nerd.getName());
		send("&e&lRank: &3" + nerd.getRank().getColoredName());
		if (!isNullOrEmpty(nerd.getPreferredName()))
			send("&e&lPreferred name: &3" + nerd.getPreferredName());
		if (!nerd.getPronouns().isEmpty())
			send("&e&lPronouns: &3" + String.join(",", nerd.getPronouns()));
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
			send(json("&eMeet me! &chttps://projecteden.gg/meet/" + nerd.getName().toLowerCase()).url("https://projecteden.gg/meet/" + nerd.getName().toLowerCase()));
			line();
		}
	}

	@Path("write")
	@Permission("group.admin")
	void write() {
		writeHtml();
	}

	@EventHandler
	public void onNpcRightClick(NPCRightClickEvent event) {
		NPC npc = event.getNPC();
		Location location = event.getClicker().getLocation();
		WorldGuardUtils wgUtils = new WorldGuardUtils(location);

		if (wgUtils.getRegionsLikeAt("staffhall", location).size() > 0) {
			if (!npc.getName().contains(" "))
				runCommand(event.getClicker(), "staffhall view " + stripColor(npc.getName()));
		} else if (wgUtils.getRegionsLikeAt("hallofhistory", location).size() > 0)
			runCommand(event.getClicker(), "hoh view " + stripColor(npc.getName()));
		else if (npc.getId() == 2678)
			runCommand(event.getClicker(), "griffinwelc");
		else if (npc.getId() == 2697)
			runCommand(event.getClicker(), "filidwelc");
		else if (npc.getId() == 2990)
			runCommand(event.getClicker(), "crates");
	}

}
