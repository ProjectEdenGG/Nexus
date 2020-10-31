package me.pugabyte.bncore.features;

import fr.minuskube.inv.SmartInvsPlugin;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.recipes.CustomRecipes;
import me.pugabyte.bncore.framework.commands.Commands;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Description;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.features.Features;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.Nerd.StaffMember;
import me.pugabyte.bncore.models.nerd.NerdService;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.task.Task;
import me.pugabyte.bncore.models.task.TaskService;
import me.pugabyte.bncore.utils.ActionBarUtils;
import me.pugabyte.bncore.utils.BlockUtils;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.StringUtils.Gradient;
import me.pugabyte.bncore.utils.StringUtils.ProgressBarStyle;
import me.pugabyte.bncore.utils.StringUtils.Rainbow;
import me.pugabyte.bncore.utils.StringUtils.TimespanFormatType;
import me.pugabyte.bncore.utils.StringUtils.TimespanFormatter;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import static me.pugabyte.bncore.utils.BlockUtils.getDirection;
import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.decolorize;
import static me.pugabyte.bncore.utils.StringUtils.parseShortDate;
import static me.pugabyte.bncore.utils.StringUtils.paste;
import static me.pugabyte.bncore.utils.StringUtils.timespanDiff;
import static me.pugabyte.bncore.utils.Utils.isNullOrAir;

@Permission("group.seniorstaff")
public class BNCoreCommand extends CustomCommand {

	public BNCoreCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public void _shutdown() {
		shutdownBossBars();
	}

	@Path("reload")
	void reload() {
		File file = Paths.get("plugins/BNCore.jar").toFile();
		if (!file.exists())
			error("BNCore.jar doesn't exist, cannot reload");

		try {
			new ZipFile(file).entries();
		} catch (IOException ex) {
			error("BNCore.jar is not complete, cannot reload");
		}

		long matchCount = MatchManager.getAll().stream().filter(match -> match.isStarted() && !match.isEnded()).count();
		if (matchCount > 0)
			error("There are " + matchCount + " active matches, cannot reload");

		long invCount = Bukkit.getOnlinePlayers().stream().filter(player -> SmartInvsPlugin.manager().getInventory(player).isPresent()).count();
		if (invCount > 0)
			error("There are " + invCount + " SmartInvs menus open, cannot reload");

		runCommand("plugman reload BNCore");
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
		send("Listeners: " + BNCore.getListenerCount());
		send("Arenas: " + ArenaManager.getAll().size());
		send("Recipes: " + CustomRecipes.getRecipes().size());
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
						OfflinePlayer player = Utils.getPlayer((String) data.get("uuid"));
						if (player.isOnline() && player.getPlayer() != null)
							Utils.send(player, (String) data.get("message"));
						service.complete(task);
					}));
		});
	}

	@Path("boosts")
	void boosts() {
		List<Member> boosters = Discord.getGuild().getBoosters();
		for (Member booster : boosters) {
			send(" - " + booster.getEffectiveName());
		}
	}

	@Path("taskTest <message...>")
	void taskTest(String message) {
		new TaskService().save(new Task("command-test", new HashMap<String, Object>() {{
			put("uuid", player().getUniqueId().toString());
			put("message", message);
		}}, LocalDateTime.now().plusMinutes(1)));
	}

	@Path("getEnv")
	void getEnv() {
		send(BNCore.getEnv().name());
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

	@Path("getSpigotHex <input...>")
	void getSpigotHex(String input) {
		send(json("Click to copy").copy(decolorize(colorize(input))));
	}

	@Path("runSpigotHexCommand <commandNoSlash...>")
	void runHexCommand(String commandNoSlash) {
		runCommand(decolorize(colorize(commandNoSlash)));
	}

	@Path("setNpcName withPrefix <player>")
	void setNpcNameWithFormat(Nerd nerd) {
		runCommand("npc rename " + decolorize(colorize("&8&l[" + nerd.getRank().withColor() + "&8&l] " + nerd.getRank().getChatColor() + nerd.getName())));
	}

	@Path("setNpcName withColor <player>")
	void setNpcNameWithColor(Nerd nerd) {
		runCommand("npc rename " + decolorize(colorize(nerd.getRank().getChatColor())) + nerd.getName());
	}

	@Description("Get the last color used in a string (including formatting)")
	@Path("getLastColor <message...>")
	void getLastColor(String message) {
		send(StringUtils.getLastColor(message) + "Last color");
	}

	@Path("gradient <color1> <color2> <input>")
	void gradient(ChatColor color1, ChatColor color2, String input) {
		send(Gradient.of(color1, color2).apply(input));
	}

	@Path("rainbow <input>")
	void rainbow(String input) {
		send(Rainbow.apply(input));
	}

	@Path("getRank <player>")
	void getRank(Nerd player) {
		send(player.getRank().withColor());
	}

	@Path("getPlayer [player]")
	void getPlayer(@Arg("self") OfflinePlayer player) {
		send(player.getName());
	}

	@Description("Generate an sample exp bar cooldown")
	@Path("expCooldown <cooldown>")
	void expCooldown(@Arg("20") int cooldown) {
		final int level = player().getLevel();
		final float exp = player().getExp();
		Tasks.Countdown.builder()
				.duration(cooldown)
				.onStart(() -> player().setLevel(0))
				.onTick(ticks -> player().setExp((float) ticks / cooldown))
				.onComplete(() -> {
					player().setLevel(level);
					player().setExp(exp);
				})
				.start();
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
		WorldEditUtils worldEditUtils = new WorldEditUtils(player());
//		TODO when API saving works again
//		worldEditUtils.save(name, worldEditUtils.getPlayerSelection(player()));
//		send("Saved schematic " + name);
		GameMode originalGameMode = player().getGameMode();
		Location originalLocation = player().getLocation().clone();
		Location location = worldEditUtils.toLocation(worldEditUtils.getPlayerSelection(player()).getMinimumPoint());
		player().setGameMode(GameMode.SPECTATOR);
		player().teleport(location);
		runCommand("mcmd /copy ;; /schem save " + name + " -f");
		Tasks.wait(10, () -> {
			player().teleport(originalLocation);
			player().setGameMode(originalGameMode);
		});
	}

	@Path("schem paste <name>")
	void schemPaste(String name) {
		WorldEditUtils worldEditUtils = new WorldEditUtils(player());
		worldEditUtils.paster().file(name).at(player().getLocation()).paste();
		send("Pasted schematic " + name);
	}

	@Path("signgui")
	void signgui() {
		BNCore.getSignMenuFactory()
				.lines("1", "2", "3", "4")
				.prefix(PREFIX)
				.response(lines -> {
					for (String string : lines)
						send(string);
				})
				.open(player());
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
		Block targetBlockExact = player().getTargetBlockExact(500);
		if (isNullOrAir(targetBlockExact))
			error("You must be looking at a block");

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

	@ConverterFor(ChatColor.class)
	ChatColor convertToChatColor(String value) {
		if (StringUtils.getHexPattern().matcher(value).matches())
			return ChatColor.of(value.replaceFirst("&", ""));

		try {
			return ColorType.valueOf(value.toUpperCase()).getChatColor();
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException("Color &e" + value + "&c not found");
		}
	}

	@TabCompleterFor(ChatColor.class)
	List<String> tabCompleteChatColor(String filter) {
		return Arrays.stream(ColorType.values())
				.map(colorType -> colorType.name().toLowerCase())
				.filter(name -> name.startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	@ConverterFor(LocalDate.class)
	LocalDate convertToLocalDate(String value) {
		try { return parseShortDate(value); } catch (Exception ignore) {}
		throw new InvalidInputException("Could not parse date, correct format is MM/DD/YYYY");
	}

	@ConverterFor(Enchantment.class)
	Enchantment convertToEnchantment(String value) {
		return Enchantment.getByKey(NamespacedKey.minecraft(value));
	}

	@TabCompleterFor(Enchantment.class)
	List<String> tabCompleteEnchantment(String filter) {
		return Arrays.stream(Enchantment.values())
				.filter(enchantment -> enchantment.getKey().getKey().toLowerCase().startsWith(filter.toLowerCase()))
				.map(enchantment -> enchantment.getKey().getKey())
				.collect(Collectors.toList());
	}
}
