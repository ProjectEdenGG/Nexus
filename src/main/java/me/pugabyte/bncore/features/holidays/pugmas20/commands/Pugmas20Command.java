package me.pugabyte.bncore.features.holidays.pugmas20.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.holidays.pugmas20.AdventChests;
import me.pugabyte.bncore.features.holidays.pugmas20.Ores;
import me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20;
import me.pugabyte.bncore.features.holidays.pugmas20.Train;
import me.pugabyte.bncore.features.holidays.pugmas20.menu.AdventMenu;
import me.pugabyte.bncore.features.holidays.pugmas20.models.PugmasTreeType;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.pugmas20.Pugmas20Service;
import me.pugabyte.bncore.models.pugmas20.Pugmas20User;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20.isBeforePugmas;
import static me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20.isPastPugmas;
import static me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20.isSecondChance;

@Aliases("pugmas")
@NoArgsConstructor
public class Pugmas20Command extends CustomCommand implements Listener {
	private final Pugmas20Service service = new Pugmas20Service();
	String timeLeft = StringUtils.timespanDiff(Pugmas20.openingDay);

	public Pugmas20Command(CommandEvent event) {
		super(event);
	}

	@Path()
	void pugmas() {
		send("Soon™ (" + timeLeft + ")");
	}

	@Path("advent [month] [day]")
	void advent(@Arg(value = "12", permission = "group.staff") int month, @Arg(value = "-1", permission = "group.staff") int day) {
		LocalDateTime now = LocalDateTime.now();
		int year = 2020;
		if (month < 11)
			year = 2021;
		if (day == -1)
			day = now.getDayOfMonth();

		now = now.withYear(year).withMonth(month).withDayOfMonth(day);

		if (isBeforePugmas(now) && !player().hasPermission("group.staff"))
			error("Soon™ (" + timeLeft + ")");

		if (isPastPugmas(now))
			error("Next year!");

		if (isSecondChance(now))
			now = now.withYear(2020).withMonth(12).withDayOfMonth(25);

		AdventMenu.openAdvent(player(), now);
	}

	@Permission("group.admin")
	@Path("advent give <day>")
	void adventGiveHead(int day) {
		AdventChests.giveAdventHead(player(), day);
	}

	@Permission("group.admin")
	@Path("advent open <day>")
	void adventOpenDay(int day) {
		AdventChests.openAdventLootInv(player(), day);
	}

	@Permission("group.admin")
	@Path("advent addDay <player> <day>")
	void adventAddDay(OfflinePlayer player, int day) {
		Pugmas20User user = service.get(player);
		user.getFoundDays().add(day);
		service.save(user);

		send("Added day " + day + " to " + player.getName());
	}

	@Permission("group.admin")
	@Path("database delete <player>")
	void databaseDelete(OfflinePlayer player) {
		Pugmas20User user = service.get(player);
		service.delete(user);
		send("Deleted data for " + player.getName());
	}

	@Path("testtrain")
	@Permission("group.admin")
	void testTrain() {
		if (!Train.animate())
			error("Train is animating!");
	}

	@Permission("group.admin")
	@Path("tree build <treeType> <id>")
	void treeSchematic(PugmasTreeType treeType, int id) {
		treeType.build(id);
	}

	@Permission("group.admin")
	@Path("tree feller <treeType> <id>")
	void treeFeller(PugmasTreeType treeType, int id) {
		treeType.feller(player(), id);
	}

	@Permission("group.admin")
	@Path("tree feller all")
	void treeFeller() {
		for (PugmasTreeType treeType : PugmasTreeType.values())
			for (Integer id : treeType.getPasters().keySet())
				treeType.feller(player(), id);
	}

	@Permission("group.admin")
	@Path("tree copy <treeType>")
	void treeCopy(PugmasTreeType treeType) {
		runCommand("/copy -m orange_wool,snow," + treeType.getAllMaterialsString());
	}

	@Permission("group.admin")
	@Path("tree save <treeType> <id>")
	void treeSave(PugmasTreeType treeType, int id) {
		runCommand("mcmd /copy -m snow," + treeType.getAllMaterialsString() + " ;; /schem save pugmas20/trees/" + treeType.name().toLowerCase() + "/" + id + " -f");
	}

	@Permission("group.admin")
	@Path("tree region <treeType> <id>")
	void treeRegion(PugmasTreeType treeType, int id) {
		ProtectedRegion region = treeType.getRegion(id);
		String command = region == null ? "define" : "redefine";
		runCommand("mcmd /here ;; rg " + command + " pugmas20_trees_" + treeType.name().toLowerCase() + "_" + id);
	}

	@Permission("group.admin")
	@Path("tree get")
	void treeGet() {
		send(PREFIX + "You are looking at a " + camelCase(PugmasTreeType.of(getTargetBlock().getType())) + " tree");
	}

	@Permission("group.admin")
	@Path("tree counts")
	void treeCounts() {
		int total = 0;
		JsonBuilder json = json(PREFIX + "Pugmas tree counts:");
		for (PugmasTreeType treeType : PugmasTreeType.values()) {
			Set<Integer> ids = treeType.getPasters().keySet();
			if (ids.size() == 0)
				continue;

			String collect = ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
			json.newline().next("&e " + camelCase(treeType) + " &7- " + ids.size() + " &3[" + collect + "]");
			total += ids.size();
		}

		if (total == 0)
			error("No pugmas trees found");

		send(json.newline().next("&3Total: &e" + total));
	}

	@Permission("group.admin")
	@Path("kit miners pickaxe")
	void kitMinersPickaxe() {
		ItemUtils.giveItem(player(), Ores.getMinersPickaxe());
	}

	@Permission("group.admin")
	@Path("kit miners sieve")
	void kitMinersSieve() {
		ItemUtils.giveItem(player(), Ores.getMinersSieve());
	}

}
