package me.pugabyte.nexus.features;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import fr.minuskube.inv.SmartInvsPlugin;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.listeners.ResourceWorld;
import me.pugabyte.nexus.features.minigames.managers.ArenaManager;
import me.pugabyte.nexus.features.minigames.managers.MatchManager;
import me.pugabyte.nexus.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.nexus.features.recipes.CustomRecipes;
import me.pugabyte.nexus.framework.commands.Commands;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.features.Features;
import me.pugabyte.nexus.models.balanceconverter.BalanceConverter;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Nerd.StaffMember;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.models.task.Task;
import me.pugabyte.nexus.models.task.TaskService;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.PacketUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.SoundUtils.Jingle;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.StringUtils.ProgressBarStyle;
import me.pugabyte.nexus.utils.StringUtils.TimespanFormatType;
import me.pugabyte.nexus.utils.StringUtils.TimespanFormatter;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Tasks.ExpBarCountdown;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.WorldEditUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import static me.pugabyte.nexus.utils.BlockUtils.getBlocksInRadius;
import static me.pugabyte.nexus.utils.BlockUtils.getDirection;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.paste;
import static me.pugabyte.nexus.utils.StringUtils.timespanDiff;

@NoArgsConstructor
@Permission("group.seniorstaff")
public class NexusCommand extends CustomCommand implements Listener {
	private WorldEditUtils worldEditUtils;

	public NexusCommand(CommandEvent event) {
		super(event);
		if (isPlayer())
			worldEditUtils = new WorldEditUtils(player());
	}

	@Override
	public void _shutdown() {
		shutdownBossBars();
	}

	@Path("reload")
	void reload() {
		File file = Paths.get("plugins/Nexus.jar").toFile();
		if (!file.exists())
			error("Nexus.jar doesn't exist, cannot reload");

		try {
			new ZipFile(file).entries();
		} catch (IOException ex) {
			error("Nexus.jar is not complete, cannot reload");
		}

		long matchCount = MatchManager.getAll().stream().filter(match -> match.isStarted() && !match.isEnded()).count();
		if (matchCount > 0)
			error("There are " + matchCount + " active matches, cannot reload");

		long invCount = Bukkit.getOnlinePlayers().stream().filter(player -> SmartInvsPlugin.manager().getInventory(player).isPresent()).count();
		if (invCount > 0)
			error("There are " + invCount + " SmartInvs menus open, cannot reload");

		List<Pugmas20User> all = new Pugmas20Service().getAll();
		long torchCount = all.stream().filter(pugmas20User -> pugmas20User.isOnline() && pugmas20User.isLightingTorches() && pugmas20User.getTorchTimerTaskId() > 0).count();
		if (torchCount > 0)
			error("There are " + torchCount + " people completing the Pugmas20 torch quest, cannot reload");

		if (Pugmas20.isTreeAnimating())
			error("Pugmas tree is animating, cannot reload");

		if (player().equals(PlayerUtils.wakka()))
			SoundUtils.playSound(PlayerUtils.wakka(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO);

		runCommand("plugman reload Nexus");
	}

	@Path("debug")
	void debug() {
		Nexus.setDebug(!Nexus.isDebug());
		send(PREFIX + "Debugging " + (Nexus.isDebug() ? "&aenabled" : "&cdisabled"));
	}

	private static final LocalDateTime lastReload = LocalDateTime.now();

	@Path("lastReload")
	void lastReload() {
		send(PREFIX + "Last reloaded &e" + timespanDiff(lastReload) + " ago");
	}

	@Path("gc")
	void gc() {
		send("Collecting garbage...");
		System.gc();
		send("Garbage collected");
	}

	@Path("stats")
	void stats() {
		send("Features: " + Features.getFeatures().size());
		send("Commands: " + Commands.getCommands().size());
		send("Listeners: " + Nexus.getListenerCount());
		send("Arenas: " + ArenaManager.getAll().size());
		send("Mechanics: " + MechanicType.values().length);
		send("Recipes: " + CustomRecipes.getRecipes().size());
	}

	@Confirm
	@Path("resourceWorld reset <test>")
	void resourceWorldReset(boolean test) {
		ResourceWorld.reset(test);
	}

	@Path("listTest <player...>")
	void listTest(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		send(players.stream().map(OfflinePlayer::getName).collect(Collectors.joining(", ")));
	}

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(30), () -> {
			TaskService service = new TaskService();
			service.process("command-test").forEach(task ->
					Tasks.wait(Time.MINUTE.x(2), () -> {
						Map<String, Object> data = task.getJson();
						OfflinePlayer player = PlayerUtils.getPlayer((String) data.get("uuid"));
						if (player.isOnline() && player.getPlayer() != null)
							PlayerUtils.send(player, (String) data.get("message"));
						service.complete(task);
					}));
		});
	}

	@Path("taskTest <message...>")
	void taskTest(String message) {
		new TaskService().save(new Task("command-test", new HashMap<String, Object>() {{
			put("uuid", player().getUniqueId().toString());
			put("message", message);
		}}, LocalDateTime.now().plusMinutes(1)));
	}

	@Path("boosts")
	void boosts() {
		List<Member> boosters = Discord.getGuild().getBoosters();
		for (Member booster : boosters) {
			send(" - " + booster.getEffectiveName());
		}
	}

	@Path("getEnv")
	void getEnv() {
		send(Nexus.getEnv().name());
	}

	@Async
	@Path("getDataFile [player]")
	void getDataFile(@Arg("self") Nerd nerd) {
		send(json().next(paste(nerd.getDataFile().asNBTString())));
	}

	@Path("koda <message...>")
	void koda(String message) {
		Koda.say(message);
	}

	@Path("getRank <player>")
	void getRank(Nerd player) {
		send(player.getRank().withColor());
	}

	@Path("getPlayer [player]")
	void getPlayer(@Arg("self") OfflinePlayer player) {
		send(player.getName());
	}

	@Description("Generate an sample exp bar countdown")
	@Path("expBarCountdown <duration>")
	void expBarCountdown(@Arg("20") int duration) {
		ExpBarCountdown.builder()
				.duration(duration)
				.player(player())
				.restoreExp(true)
				.start();
	}

	@Confirm
	@Path("breakNaturally")
	void breakNaturally() {
		WorldEditUtils worldEditUtils = new WorldEditUtils(player());
		Region selection = worldEditUtils.getPlayerSelection(player());
		if (selection.getArea() > 50000)
			error("Max selection size is 50000");

		for (Block block : worldEditUtils.getBlocks(selection)) {
			if (block.getType() == Material.AIR)
				continue;

			block.breakNaturally();
		}
	}

//	@Async
//	@Path("balances <count>")
//	void balances(int limit) {
//		int count = 0;
//		for (UUID uuid : Nexus.getEssentials().getUserMap().getAllUniqueUsers()) {
//			BalanceConverterService service = new BalanceConverterService();
//			BalanceConverter converter = service.get(uuid);
//			converter.findBalances();
//			service.save(converter);
//
//			double sum = converter.sum();
//			double max = converter.max();
//			double diff = sum - max;
//			if (diff < 5000)
//				continue;
//
//			int size = converter.getBalances().size();
//			String maxFormat = StringUtils.getDf().format(max);
//			String sumFormat = StringUtils.getDf().format(sum);
//			String diffFormat = StringUtils.getDf().format(diff);
//
//			send(converter.getName() + " - Amount: " + size + " / Max: " + maxFormat + " / Sum: " + sumFormat + " / Diff: " + diffFormat);
//			if (++count > limit)
//				error("Limit reached");
//		}
//	}
//
//	@Async
//	@Path("balances set <count>")
//	void balancesSet(int limit) {
//		int count = 0;
//		for (UUID uuid : Nexus.getEssentials().getUserMap().getAllUniqueUsers()) {
//			BalanceConverterService service = new BalanceConverterService();
//			BalanceConverter converter = service.get(uuid);
//			converter.findBalances();
//			service.save(converter);
//
//			Tasks.sync(() -> runCommandAsConsole("eco set " + converter.getUuid().toString() + " " + StringUtils.getDf().format(converter.sum())));
//
//			if (++count > limit)
//				error("Limit reached");
//		}
//	}

	@Async
	@Path("balances debug <player>")
	void balances(BalanceConverter converter) {
		send(converter.toPrettyString());
	}

	@Path("movingSchematicTest <schematic> <seconds> <velocity>")
	void movingSchematicTest(String schematic, int seconds, double velocity) {
		List<FallingBlock> fallingBlocks = worldEditUtils.paster()
				.file(schematic)
				.at(player().getLocation().add(-10, 0, 0))
				.buildEntities();

		Tasks.wait(Time.SECOND.x(5), () -> {
			Tasks.Countdown.builder()
					.duration(Time.SECOND.x(seconds))
					.onTick(i -> fallingBlocks.forEach(fallingBlock -> fallingBlock.setVelocity(new Vector(velocity, 0, 0))))
					.start();

			Tasks.wait(Time.SECOND.x(seconds), () -> fallingBlocks.forEach(Entity::remove));
		});
	}

	@Path("schem buildQueue <schematic> <seconds>")
	void schemBuildQueue(String schematic, int seconds) {
		worldEditUtils.paster()
				.file(schematic)
				.at(player().getLocation().add(-10, 0, 0))
				.duration(Time.SECOND.x(seconds))
				.buildQueue();
	}

	public void shutdownBossBars() {
		bossBars.forEach((player, bossBar) -> {
			bossBar.setVisible(false);
			bossBar.removeAll();
		});
	}

	private static Map<Player, BossBar> bossBars = new HashMap<>();

	@Path("bossBar add <color> <style> <title...>")
	void bossBarAdd(BarColor color, BarStyle barStyle, String title) {
		if (bossBars.containsKey(player()))
			error("You already have a boss bar");

		BossBar bossBar = Bukkit.createBossBar(title, color, barStyle);
		bossBar.addPlayer(player());
		bossBars.put(player(), bossBar);
	}

	@Path("bossBar remove")
	void bossBarRemove() {
		if (!bossBars.containsKey(player()))
			error("You do not have a boss bar");

		BossBar bossBar = bossBars.remove(player());
		bossBar.setVisible(false);
		bossBar.removeAll();
	}

	@Path("setExp <number>")
	void setExp(float exp) {
		player().setExp(exp);
	}

	@Path("setTotalExperience <number>")
	void setTotalExperience(int exp) {
		player().setTotalExperience(exp);
	}

	@Path("setLevel <number>")
	void setLevel(int exp) {
		player().setLevel(exp);
	}

	@Path("giveExpLevels <number>")
	void giveExpLevels(int exp) {
		player().giveExpLevels(exp);
	}

	@Path("updateCommands")
	void updateCommands() {
		player().updateCommands();
	}

	@Path("actionBar <duration> <message...>")
	void actionBar(int duration, String message) {
		ActionBarUtils.sendActionBar(player(), message, duration);
	}

	@Path("progressBar <progres> <goal> [style] [length]")
	void progressBar(int progress, int goal, @Arg("NONE") ProgressBarStyle style, @Arg("25") int length) {
		send(StringUtils.progressBar(progress, goal, style, length));
	}

	@Path("setting <type> [value]")
	void setting(String type, String value) {
		if (!isNullOrEmpty(value))
			new SettingService().save(new Setting(player(), type, value));
		send("Setting: " + new SettingService().get(player(), type));
	}

	@Path("getBlockStandingOn")
	void getBlockStandingOn() {
		Block block = BlockUtils.getBlockStandingOn(player());
		if (block == null)
			send("Nothing");
		else
			send(block.getType().name());
	}

	@Path("getOnlineNerdsWith <permission>")
	void getOnlineNerdsWith(String permission) {
		send(new NerdService().getOnlineNerdsWith(permission).stream().map(Nerd::getName).collect(Collectors.joining(", ")));
	}

	@Path("setTabListName <text...>")
	void setTabListName(String text) {
		player().setPlayerListName(colorize(text));
		send("Updated");
	}

	@Path("schem save <name>")
	void schemSave(String name) {
		worldEditUtils.save(name, worldEditUtils.getPlayerSelection(player()));
		send("Saved schematic " + name);
	}

	@Path("schem paste <name>")
	void schemPaste(String name) {
		worldEditUtils.paster().file(name).at(player().getLocation()).pasteAsync();
		send("Pasted schematic " + name);
	}

	private static final Map<UUID, Clipboard> clipboards = new HashMap<>();

	@Path("clipboard copy")
	void clipboardCopy() {
		clipboards.put(uuid(), worldEditUtils.copy(worldEditUtils.getPlayerSelection(player())));
		send("Copied selection");
	}

	@Path("clipboard paste")
	void clipboardPaste() {
		if (!clipboards.containsKey(uuid()))
			error("You have not copied anything");

		worldEditUtils.paster().clipboard(clipboards.get(uuid())).at(player().getLocation()).pasteAsync();
		send("Pasted clipboard");
	}

	@Path("allowedRegionsTest")
	void allowedRegionsTest() {
		worldEditUtils.paster().file("allowedRegionsTest").at(player().getLocation()).regions("allowedRegionsTest").pasteAsync();
		send("Pasted schematic allowedRegionsTest");
	}

	@Path("copyTileEntityClientTest")
	void copyTileEntityClient() {
		PacketUtils.copyTileEntityClient(player(), player().getLocation().getBlock(), player().getLocation().add(1, 0, 0));
	}

	@Path("removeTest")
	void removeTest() {
		PlayerInventory inventory = player().getInventory();
		inventory.remove(new ItemStack(Material.DIRT, 2));
		inventory.removeItem(new ItemStack(Material.COBBLESTONE, 4));
		inventory.removeItemAnySlot(new ItemStack(Material.STONE, 6));
	}

	@Path("signgui")
	void signgui() {
		Nexus.getSignMenuFactory()
				.lines("1", "2", "3", "4")
				.prefix(PREFIX)
				.response(lines -> {
					for (String string : lines)
						send(string);
				})
				.open(player());
	}

	@Path("loreizeTest")
	void loreizeTest() {
		String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut " +
				"labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
				"nisi ut aliquip ex ea commodo consequat.";

		send(json("Test 1").hover(lorem));
		send(json("Test 2").hover(lorem).loreize(false));
	}

	@Description("A command with a 5.75s cooldown")
	@Path("cooldown")
	@Cooldown({
			@Part(value = Time.SECOND, x = 5),
			@Part(value = Time.TICK, x = 15)
	})
	void cooldown() {
		send("Hello!");
	}

	@Path("argPermTest [one] [two] [three] [four] [five]")
	void argPermTest(
			@Arg(tabCompleter = Player.class) String one,
			@Arg(value = "2", tabCompleter = Player.class) String two,
			@Arg(permission = "group.staff", tabCompleter = Player.class) String three,
			@Arg(value = "4", permission = "group.staff", tabCompleter = Player.class) String four,
			@Arg(value = "5", permission = "group.admin", tabCompleter = Player.class) String five
	) {
		send(one + " / " + two + " / " + three + " / " + four + " / " + five);
	}

	private static final Map<UUID, Location> directionTestMap = new HashMap<>();

	@Path("directionTest <blockNumber>")
	void directionTest(int blockNumber) {
		Block targetBlockExact = getTargetBlockRequired();

		if (blockNumber == 1) {
			directionTestMap.put(uuid(), targetBlockExact.getLocation());
			send(PREFIX + "Set second position to calcluate direction");
		} else {
			if (!directionTestMap.containsKey(uuid()))
				error("You must set the first position");

			send(PREFIX + camelCase(getDirection(directionTestMap.remove(uuid()), targetBlockExact.getLocation())));
		}
	}

	@Path("timespanFormatter <seconds> <formatType>")
	void timespanFormatter(int seconds, TimespanFormatType formatType) {
		send(TimespanFormatter.of(seconds).formatType(formatType).format());
	}

	@Path("voidNpc")
	void voidNpc() {
		CitizensAPI.getNPCRegistry().forEach(npc -> {
			if (npc.getEntity() != null && npc.getEntity().getLocation().getY() < 0)
				send(npc.getId());
		});
	}

	@Path("jingles <jingle>")
	void jingles(Jingle jingle) {
		jingle.play(player());
	}

	// Doesnt work
	@Path("setPowered [boolean] [doPhysics]")
	void setPowered(@Arg("true") boolean powered, @Arg("false") boolean doPhysics) {
		line();
		Block block = player().getLocation().getBlock();
		RedstoneRail rail = ((RedstoneRail) block.getBlockData());
		send("Before: " + (rail.isPowered() ? "is" : "not") + " powered");
		rail.setPowered(powered);
		block.setBlockData(rail, doPhysics);
		player().sendBlockChange(block.getLocation(), rail);
		send("After: " + (((RedstoneRail) block.getBlockData()).isPowered() ? "is" : "not") + " powered");
	}

	@Path("getPowered")
	void getPowered() {
		Block block = player().getLocation().getBlock();
		RedstoneRail rail = ((RedstoneRail) block.getBlockData());
		send((rail.isPowered() ? "is" : "not") + " powered");
	}

	@Path("clientSideBlock <material>")
	void clientSideBlock(Material material) {
		player().sendBlockChange(player().getLocation().add(0, -1, 0), Bukkit.createBlockData(material));
	}

	private static String motd = null;

	@Path("motd <text...>")
	void motd(String text) {
		motd = colorize(text.replace("\\n", System.lineSeparator()));
		send(PREFIX + "Motd updated");
	}

	@Path("motd reset")
	void motdReset() {
		motd = null;
		send(PREFIX + "Motd Reset");
	}

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		if (motd != null)
			event.setMotd(motd);
	}

	@Path("radiusTest")
	void radiusTest() {
		Location origin = player().getLocation();
		for (Block block : getBlocksInRadius(origin, 3)) {
			double distance = block.getLocation().distance(origin);
			if (distance < 1)
				block.setType(Material.RED_CONCRETE);
			else if (distance < 2)
				block.setType(Material.ORANGE_CONCRETE);
			else if (distance < 3)
				block.setType(Material.YELLOW_CONCRETE);
		}
	}

	@Path("trimItemNames")
	void trimItemNames() {
		Block block = getTargetBlock();
		if (!(block.getState() instanceof Container))
			return;

		Container state = (Container) block.getState();

		for (ItemStack content : state.getInventory().getContents()) {
			if (isNullOrAir(content))
				continue;

			ItemMeta itemMeta = content.getItemMeta();
			String displayName = itemMeta.getDisplayName();

			if (!displayName.matches(StringUtils.getColorGroupPattern() + " .*"))
				continue;

			String trimmed = displayName.replaceFirst(" ", "");

			send("\"" + displayName + "&f\" -> \"" + trimmed + "&f\"");

			itemMeta.setDisplayName(trimmed);
			content.setItemMeta(itemMeta);
		}
	}

	@ConverterFor(Nerd.class)
	Nerd convertToNerd(String value) {
		return new NerdService().get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(Nerd.class)
	List<String> tabCompleteNerd(String value) {
		return tabCompletePlayer(value);
	}

	@ConverterFor(StaffMember.class)
	StaffMember convertToStaffMember(String value) {
		OfflinePlayer player = convertToOfflinePlayer(value);
		if (!new Nerd(player).getRank().isStaff())
			error(player.getName() + " is not staff");
		return new StaffMember(player.getUniqueId());
	}

	@TabCompleterFor(StaffMember.class)
	List<String> tabCompleteStaffMember(String filter) {
		return new HoursService().getActivePlayers().stream()
				.filter(player -> new Nerd(player).getRank().isStaff())
				.map(OfflinePlayer::getName)
				.filter(name -> name != null && name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

}
