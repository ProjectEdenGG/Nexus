package gg.projecteden.nexus.features.test;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.matchdata.PixelPaintersMatchData;
import gg.projecteden.nexus.features.store.perks.NPCListener;
import gg.projecteden.nexus.features.wither.fights.CorruptedFight.CorruptedCounterAttacks;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.BiomeTag.BiomeClimateType;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.SoundBuilder.SoundCooldown;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.ProgressBarStyle;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.ExpBarCountdown;
import gg.projecteden.nexus.utils.Tasks.QueuedTask;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.utils.TimeUtils.Timespan.TimespanBuilder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.inventivetalent.glow.GlowAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.BlockUtils.getBlocksInRadius;
import static gg.projecteden.nexus.utils.BlockUtils.getDirection;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Permission("group.admin")
@NoArgsConstructor
public class TestCommand extends CustomCommand implements Listener {

	public TestCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public void _shutdown() {
		shutdownBossBars();
	}

	@Path("sel designRegion")
	void sel_designRegion() {
		if (world() != Minigames.getWorld())
			error("Must be in minigames world");

		final WorldEditUtils worldedit = new WorldEditUtils(world());
		final PixelPaintersMatchData matchData = MatchManager.get(ArenaManager.get("PixelPainters")).getMatchData();
		worldedit.setSelection(player(), matchData.getDesignRegion());
	}

	@Path("queuedTask")
	void queuedTask() {
		QueuedTask.builder()
			.uuid(uuid())
			.type("test")
			.task(() -> send(PREFIX + "Task completed"))
			.queue(TickTime.SECOND);

		send(PREFIX + "Queued task");
	}

	@Path("scrambleInventory")
	void scrambleInventory() {
		CorruptedCounterAttacks.SCRAMBLE_INVENTORY.execute(List.of(player()));
	}

	@Path("inventoryContents")
	void inventoryContents() {
		Consumer<ItemStack> send = item -> {
			if (item != null)
				send(item.getType().name());
		};

		line();
		send("getContents()");
		for (ItemStack content : inventory().getContents())
			send.accept(content);

		line();
		send("getStorageContents()");
		for (ItemStack content : inventory().getStorageContents())
			send.accept(content);

		line();
		send("getArmorContents()");
		for (ItemStack content : inventory().getArmorContents())
			send.accept(content);

		line();
		send("getExtraContents()");
		for (ItemStack content : inventory().getExtraContents())
			send.accept(content);
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

	@Path("movingSchematic <schematic> <seconds> <velocity>")
	void movingSchematicTest(String schematic, int seconds, double velocity) {
		new WorldEditUtils(player()).paster()
			.file(schematic)
			.at(location().add(-10, 0, 0))
			.spawnFallingBlocks()
			.thenAccept(fallingBlocks -> {
				send(fallingBlocks.size() + " falling blocks spawned");
				Tasks.wait(TickTime.SECOND.x(5), () -> {
					Tasks.Countdown.builder()
						.duration(TickTime.SECOND.x(seconds))
						.onTick(i -> fallingBlocks.forEach(fallingBlock -> fallingBlock.setVelocity(new Vector(velocity, 0, 0))))
						.start();

					Tasks.wait(TickTime.SECOND.x(seconds), () -> fallingBlocks.forEach(Entity::remove));
				});
			});
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

	@Path("actionBar <duration> <message...>")
	void actionBar(int duration, String message) {
		ActionBarUtils.sendActionBar(player(), message, duration);
	}

	@Path("progressBar <progres> <goal> [style] [length]")
	void progressBar(int progress, int goal, @Arg("NONE") ProgressBarStyle style, @Arg("25") int length) {
		send(StringUtils.progressBar(progress, goal, style, length));
	}

	@Path("getBlockStandingOn")
	void getBlockStandingOn() {
		Block block = BlockUtils.getBlockStandingOn(player());
		if (BlockUtils.isNullOrAir(block))
			send("Nothing");
		else
			send(block.getType().name());
	}

	@Path("inventoryRemove")
	void inventoryRemove() {
		PlayerInventory inventory = inventory();
		inventory.remove(Material.ANDESITE);
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
	@Cooldown(value = TickTime.SECOND, x = 5.75)
	void cooldown() {
		send("Hello!");
	}

	@Async
	@Path("cooldown janitor")
	void cooldownJanitor() {
		send(PREFIX + "Janitored " + new CooldownService().janitor() + " records");
	}

	@Path("cooldown forceCME <iterations>")
	void cooldownForceCME(int iterations) {
		CooldownService service = new CooldownService();
		for (int i = 0; i < iterations; i++)
			service.check(uuid(), UUID.randomUUID().toString(), TickTime.SECOND);
	}

	@Path("argPerm [one] [two] [three] [four] [five]")
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

	@Path("allowedRegionsTest")
	void allowedRegionsTest() {
		new WorldEditUtils(player()).paster().file("allowedRegionsTest").at(location()).regionMask("allowedRegionsTest").pasteAsync();
		send("Pasted schematic allowedRegionsTest");
	}

	@Path("timespanFormatter <seconds> <formatType>")
	void timespanFormatter(int seconds, FormatType formatType) {
		send(TimespanBuilder.of(seconds).formatType(formatType).format());
	}

	@Path("setTabListName <text...>")
	void setTabListName(String text) {
		player().setPlayerListName(colorize(text));
		send("Updated");
	}

	// Doesnt work
	@Path("setPowered [boolean] [doPhysics]")
	void setPowered(@Arg("true") boolean powered, @Arg("false") boolean doPhysics) {
		line();
		Block block = location().getBlock();
		RedstoneRail rail = ((RedstoneRail) block.getBlockData());
		send("Before: " + (rail.isPowered() ? "is" : "not") + " powered");
		rail.setPowered(powered);
		block.setBlockData(rail, doPhysics);
		player().sendBlockChange(block.getLocation(), rail);
		send("After: " + (((RedstoneRail) block.getBlockData()).isPowered() ? "is" : "not") + " powered");
	}

	@Path("getPowered")
	void getPowered() {
		Block block = location().getBlock();
		RedstoneRail rail = ((RedstoneRail) block.getBlockData());
		send((rail.isPowered() ? "is" : "not") + " powered");
	}

	@Path("clientSideBlock <material>")
	void clientSideBlock(Material material) {
		player().sendBlockChange(location().add(0, -1, 0), Bukkit.createBlockData(material));
	}

	@Path("radiusTest")
	void radiusTest() {
		Location origin = location();
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
		if (!(block.getState() instanceof Container state))
			return;

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

	@Path("hasPlayedBefore")
	void hasPlayedBefore(Player player) {
		send("hasPlayedBefore: " + player.hasPlayedBefore());
	}

	@Async
	@Path("sha1 <url>")
	void sha1(String url) {
		send(Utils.createSha1(url));
	}

	@Path("glow getColor <player>")
	void getGlowColor(Player player) {
		send(GlowAPI.getGlowColor(player, player()).name());
	}

	@Path("glow set <player> <color>")
	void getGlowColor(Player player, GlowAPI.Color color) {
		GlowAPI.setGlowing(player, color, player());
	}

	@Path("forceTpNPC")
	void forceTpNPC() {
		NPC npc = CitizensUtils.getSelectedNPC(player());
		NPCListener.allowNPC(npc);
		runCommand("npc tphere");
	}

	@Path("setItemSetting <setting> [value]")
	void setItemSetting(ItemSetting setting, Boolean value) {
		ItemBuilder builder = new ItemBuilder(inventory().getItemInMainHand());
		if (value == null)
			builder.unset(setting);
		else
			builder.setting(setting, value);
		inventory().setItemInMainHand(builder.build());
	}

	@Path("testTradeable [tradeable]")
	void testTradeable(Boolean tradeable) {
		ItemBuilder item = new ItemBuilder(inventory().getItemInMainHand());
		if (tradeable != null)
			item.setting(ItemSetting.TRADEABLE, tradeable);
		Tasks.wait(1, () -> inventory().setItemInMainHand(item.build()));
		Tasks.wait(2, () -> send(String.valueOf(new ItemBuilder(inventory().getItemInMainHand()).is(ItemSetting.TRADEABLE))));
	}

	@Path("testNewHasRoomFor")
	void hasRoomFor() {
		send("" + PlayerUtils.hasRoomFor(player(), new ItemStack(Material.DIRT, 64), new ItemStack(Material.SNOWBALL, 8)));
	}

	@Path("getBiomeInfo")
	void getBiomeInfo() {
		send("Temperature: " + StringUtils.getDf().format(block().getTemperature()));
		send("Humidity: " + StringUtils.getDf().format(block().getHumidity()));
		send("Biome Climate Type: " + BiomeClimateType.of(location()));
	}

	@Path("soundbuilder cooldowns")
	void soundbuilder_cooldowns() {
		for (SoundCooldown<?> cooldown : SoundBuilder.cooldowns(SoundCooldown.class))
			send(cooldown.toString());
	}
}
