package me.pugabyte.nexus.features.events.y2020.pugmas20.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.y2020.pugmas20.AdventChests;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Train;
import me.pugabyte.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Ores;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Ores.OreType;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Trees.PugmasTreeType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isBeforePugmas;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isPastPugmas;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isSecondChance;

@Aliases("pugmas")
@NoArgsConstructor
public class Pugmas20Command extends CustomCommand implements Listener {
	private final Pugmas20Service service = new Pugmas20Service();
	private Pugmas20User user;
	String timeLeft = StringUtils.timespanDiff(Pugmas20.openingDay);

	public Pugmas20Command(CommandEvent event) {
		super(event);
		if (isPlayer())
			user = service.get(player());
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
		user.getFoundDays().add(day);
		service.save(user);

		send("Added day " + day + " to " + player.getName());
	}

	@Permission("group.admin")
	@Path("database delete <player>")
	void databaseDelete(OfflinePlayer player) {
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
		Material logs = getTargetBlock().getType();
		PugmasTreeType treeType = PugmasTreeType.of(logs);
		if (treeType == null)
			error("Pugmas Tree with logs " + camelCase(logs) + " not found");

		send(PREFIX + "You are looking at a " + camelCase(treeType) + " tree");
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

	@Permission("group.admin")
	@Path("kit miners ores")
	void kitMinersOres() {
		for (OreType oreType : OreType.values())
			ItemUtils.giveItem(player(), oreType.getOre());
	}

	@Permission("group.admin")
	@Path("kit miners ingots")
	void kitMinersIngot() {
		for (OreType oreType : OreType.values())
			ItemUtils.giveItem(player(), oreType.getIngot());
	}

	@Permission("group.admin")
	@Path("inventory store")
	void inventoryStore() {
		user.storeInventory();
		send(PREFIX + "Stored inventory");
	}

	@Permission("group.admin")
	@Path("inventory apply")
	void inventoryApply() {
		user.applyInventory();
	}

	@Permission("group.admin")
	@Path("npcs emeralds")
	void npcsHolograms() {
		Pugmas20.createNpcHolograms();
	}

}
