package gg.projecteden.nexus.features.test;

import com.destroystokyo.paper.entity.Pathfinder.PathResult;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTFile;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.DebugDotCommand;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.matchdata.MonsterMazeMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.PixelPaintersMatchData;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features.AutoTool;
import gg.projecteden.nexus.features.store.perks.visuals.NPCListener;
import gg.projecteden.nexus.features.wither.fights.CorruptedFight.CorruptedCounterAttacks;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.NBTPlayer;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.BiomeTag.BiomeClimateType;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.ItemUtils.PotionWrapper;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerMovementUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.SoundBuilder.SoundCooldown;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar.SummaryStyle;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.ExpBarCountdown;
import gg.projecteden.nexus.utils.Tasks.QueuedTask;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@HideFromWiki
@Permission(Group.ADMIN)
@NoArgsConstructor
public class TestCommand extends CustomCommand implements Listener {

	public TestCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public void _shutdown() {
		shutdownBossBars();
	}

	@Path("jump [player]")
	void jump(@Arg("self") Player player) {
		PlayerMovementUtils.jump(player);
	}

	@Path("hover")
	void hover() {
		player().setGravity(!player().hasGravity());
	}

	@Path("potionEffect")
	void potion() {
		send("&3Potion Data:");
		ItemStack item = getToolRequired();
		if (!(item.getItemMeta() instanceof PotionMeta potionMeta))
			return;

		String materialName = camelCase(item.getType().name());
		send("&3 - Material = &e" + materialName);

		send("&3 - Potion Type = &e" + potionMeta.getBasePotionType());

		Potion potion = NMSUtils.toNMS(item).get(DataComponents.POTION_CONTENTS).potion().orElse(null).value();
		send("&3 - Potion Name = &e" + potion.name());

		send("&3 - Vanilla Effects:");
		for (MobEffectInstance mobEffect : potion.getEffects()) {
			send("&3 -- Desc = &e" + Arrays.stream(mobEffect.getDescriptionId().split("\\.")).toList().getLast());
		}

		send("&3 - Custom Effects:");
		for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
			send("&3 -- Type = &e" + potionEffect.getType().getKey().getKey());

			MobEffect mobEffect = PotionWrapper.toNMS(potionEffect.getType());
			MobEffectInstance mobEffectInst = new MobEffectInstance(Holder.direct(mobEffect), potionEffect.getDuration(), potionEffect.getAmplifier(), potionEffect.isAmbient(), potionEffect.hasParticles());
			send("&3 -- Inst Desc = &e" + Arrays.stream(mobEffectInst.getDescriptionId().split("\\.")).toList().getLast());
		}

		PotionWrapper potionWrapper = PotionWrapper.of(potion, potionMeta.getCustomEffects());
		String potionName = potionWrapper.getEffects().stream()
			.map(StringUtils::formatPotionData)
			.collect(Collectors.joining(", "));

		send("&3Result = &e" + potionName + " " + materialName);
	}

	@EventHandler
	public void on(PlayerToggleSneakEvent event) {
		if (!event.getPlayer().hasGravity())
			event.getPlayer().setGravity(true);
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

	@Path("progressBar <progress> <goal> [--style] [--length] [--seamless]")
	void progressBar(
		int progress,
		int goal,
		@Switch @Arg("NONE") SummaryStyle summaryStyle,
		@Switch @Arg("100") int length,
		@Switch boolean seamless
	) {
		send(ProgressBar.builder()
			.progress(progress)
			.goal(goal)
			.summaryStyle(summaryStyle)
			.length(length)
			.seamless(seamless)
			.build());
	}

	@Path("getBlockStandingOn")
	void getBlockStandingOn() {
		Block block = BlockUtils.getBlockStandingOn(player());
		if (Nullables.isNullOrAir(block))
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

	@Path("loreize")
	void loreize() {
		String lorem = Gradient.of(List.of(ChatColor.WHITE, ChatColor.GRAY)).apply(
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut " +
			"labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
			"nisi ut aliquip ex ea commodo consequat."
		);

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
		@Arg(permission = Group.STAFF, tabCompleter = Player.class) String three,
		@Arg(value = "4", permission = Group.STAFF, tabCompleter = Player.class) String four,
		@Arg(value = "5", permission = Group.ADMIN, tabCompleter = Player.class) String five
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

			send(PREFIX + camelCase(BlockUtils.getDirection(directionTestMap.remove(uuid()), targetBlockExact.getLocation())));
		}
	}

	@Path("allowedRegionsTest")
	void allowedRegionsTest() {
		new WorldEditUtils(player()).paster().file("allowedRegionsTest").at(location()).regionMask("allowedRegionsTest").pasteAsync();
		send("Pasted schematic allowedRegionsTest");
	}

	@Path("timespan <timespan> <formatType>")
	void timespan(Timespan timespan, FormatType formatType) {
		send(timespan.format(formatType));
	}

	@Path("affectsSpawning toggle [player]")
	void affectsSpawning_toggle(@Arg("self") Player player) {
		player.setAffectsSpawning(!player.getAffectsSpawning());
		send(PREFIX + "&e" + Nickname.of(player) + " " + (player.getAffectsSpawning() ? "&ais now" : "&cis no longer") + " &3affecting mob spawns");
	}

	@Path("affectsSpawning status [player]")
	void affectsSpawning_status(@Arg("self") Player player) {
		send(PREFIX + "&e" + Nickname.of(player) + " " + (player.getAffectsSpawning() ? "&ais" : "&cis not") + " &3affecting mob spawns");
	}

	@Path("bypassInsomnia toggle [player]")
	void bypassInsomnia_toggle(@Arg("self") Player player) {
		player.setBypassInsomnia(!player.getPlayer().doesBypassInsomnia());
		send(PREFIX + "&e" + Nickname.of(player) + " " + (player.doesBypassInsomnia() ? "&ais now" : "&cis no longer") + " &3bypassing insomnia");
	}

	@Path("bypassInsomnia status [player]")
	void bypassInsomnia_status(@Arg("self") Player player) {
		send(PREFIX + "&e" + Nickname.of(player) + " " + (player.doesBypassInsomnia() ? "&ais" : "&cis not") + " &3bypassing insomnia");
	}

	@Path("setTabListName <text...>")
	void setTabListName(String text) {
		player().setPlayerListName(StringUtils.colorize(text));
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
		for (Block block : BlockUtils.getBlocksInRadius(origin, 3)) {
			Distance distance = Distance.distance(block, origin);
			if (distance.lt(1))
				player().sendBlockChange(block.getLocation(), Material.RED_CONCRETE.createBlockData());
			else if (distance.lt(2))
				player().sendBlockChange(block.getLocation(), Material.ORANGE_CONCRETE.createBlockData());
			else if (distance.lt(3))
				player().sendBlockChange(block.getLocation(), Material.YELLOW_CONCRETE.createBlockData());
		}
	}

	@Path("trimItemNames")
	void trimItemNames() {
		Block block = getTargetBlock();
		if (!(block.getState() instanceof Container state))
			return;

		for (ItemStack content : state.getInventory().getContents()) {
			if (Nullables.isNullOrAir(content))
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

	@Path("soundbuilder cooldowns [context]")
	void soundbuilder_cooldowns(String context) {
		for (SoundCooldown<?> cooldown : SoundBuilder.cooldowns(SoundCooldown.class, context))
			send(cooldown.toString());
	}
	
	@Path("autotool")
	void autotool() {
		AutoTool.getBestTool(player(), Arrays.asList(PlayerUtils.getHotbarContents(player())), getTargetBlockRequired());
	}

	@Path("maxDurabilities")
	void maxDurabilities() {
		for (ToolType type : ToolType.values())
			for (Material tool : type.getTools())
				Dev.GRIFFIN.send(tool.name() + " " + tool.getMaxDurability());
	}

	@Path("monstermaze pathfinding locations")
	void monstermaze_pathfinding_locations() {
		final Arena arena = ArenaManager.get("TestMonsterMaze");
		final Match match = MatchManager.get(arena);
		final MonsterMazeMatchData matchData = match.getMatchData();

		final Mob mob = (Mob) match.getEntities().get(0);
		final PathResult currentPath = mob.getPathfinder().getCurrentPath();
		if (currentPath == null)
			error("No path found");

		for (Location point : currentPath.getPoints())
			DebugDotCommand.play(player(), point, ColorType.RED);

		send("Highlighted " + currentPath.getPoints().size() + " points");
	}

	@SneakyThrows
	@Path("getOfflineVehicle <player>")
	void getOfflineVehicle(Nerd nerd) {
		Block air = getTargetBlock().getRelative(BlockFace.UP);
		if (!MaterialTag.ALL_AIR.isTagged(air.getType()))
			error("You must be looking at the ground");

		NBTFile dataFile = new NBTPlayer(nerd).getNbtFile();
		NBTCompound rootVehicle = dataFile.getCompound("RootVehicle");
		if (rootVehicle == null)
			error("RootVehicle compound is null");

		NBTCompound entityCompound = rootVehicle.getCompound("Entity");
		if (entityCompound == null)
			error("Entity compound is null");

		String id = entityCompound.getString("id");
		EntityType type = EntityType.valueOf(id.replace("minecraft:", "").toUpperCase());

		Entity horse = world().spawnEntity(air.getLocation(), type);
		NBTEntity nbt = new NBTEntity(horse);
		nbt.mergeCompound(entityCompound);

		dataFile.setObject("RootVehicle", null);
		dataFile.save();
		send(PREFIX + "Respawned " + camelCase(type) + " and deleted original");
	}

	@Path("advancements [player] [page]")
	void advancements(@Arg("self") Player player, @Arg("1") int page) {
		BiFunction<Advancement, String, JsonBuilder> formatter = (advancement, index) -> {
			JsonBuilder json = json(" ");
			AdvancementProgress progress = player.getAdvancementProgress(advancement);
			json.next((progress.isDone() ? "&e" : "&c") + advancement.getKey().getKey());

			json.hover("&eAwarded Criteria:");
			for (String criteria : progress.getAwardedCriteria()) {
				String text = "&7- &e" + criteria;
				Date dateAwarded = progress.getDateAwarded(criteria);
				if (dateAwarded != null)
					text += " &7- " + TimeUtils.shortDateFormat(dateAwarded.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				json.hover(text);
			}

			json.hover(" ");
			json.hover("&cRemaining Criteria:");
			for (String criteria : progress.getRemainingCriteria())
				json.hover("&7- &c" + criteria);

			return json;
		};

		new Paginator<Advancement>()
			.values(PlayerUtils.getAdvancements().values())
			.formatter(formatter)
			.command("/test advancements " + player.getName())
			.page(page)
			.send();
	}

	@Path("editableSmartInventory")
	void editableSmartInventory() {
		new EditableSmartInventory().open(player());
	}

	private static class EditableSmartInventory extends InventoryProvider {
		@Override
		public void init() {
			for (int row = 0; row < 6; row++)
				for (int column = 0; column < 9; column++)
					contents.setEditable(row, column, true);
		}
	}

	@Path("selfContentsSmartInventory")
	void selfContentsSmartInventory() {
		new SelfContentsSmartInventory().open(player());
	}

	private static class SelfContentsSmartInventory extends InventoryProvider {
		@Override
		public void init() {
			for (int row = 0; row < 6; row++) {
				for (int column = 0; column < 9; column++) {
					int finalRow = row;
					int finalColumn = column;
					contents.set(SlotPos.of(row, column), ClickableItem.of(new ItemStack(Material.STONE), e -> PlayerUtils.send(viewer, "Clicked top slot " + finalRow + ", " + finalColumn)));
				}
			}
			for (int row = 0; row < 4; row++) {
				for (int column = 0; column < 9; column++) {
					int finalRow = row;
					int finalColumn = column;
					selfContents.set(SlotPos.of(row, column), ClickableItem.of(new ItemStack(Material.STONE), e -> PlayerUtils.send(viewer, "Clicked bottom slot " + finalRow + ", " + finalColumn)));
				}
			}
		}
	}

	/*
		/test calculateNoSplitSpacing __NOSPLIT:10__&f敷__NOSPLIT__&8Team Deathmatch --speed=3 --start=50
		/test calculateNoSplitSpacing __NOSPLIT:10__&f敷__NOSPLIT:114__&8Team Deathmatch
	 */
	@Path("calculateNoSplitSpacing <title...> [--speed] [--rows] [--start]")
	void calculateNoSplitSpacing(String title, @Switch @Arg("2") int speed, @Arg("6") int rows, @Switch int start) {
		new NoSplitSpacingCalculator(title, speed, rows, start).open(player());
	}

	private static class NoSplitSpacingCalculator extends InventoryProvider {
		private final String originalTitle;
		@Getter
		private String title;
		private final int speed;
		private final int rows;

		private int index;

		@Override
		protected int getRows(Integer page) {
			return rows;
		}

		public NoSplitSpacingCalculator(String title, int speed, int rows, int start) {
			final Matcher matcher = Pattern.compile("__NOSPLIT:\\d+__").matcher(title);
			while (matcher.find()) {
				String group = matcher.group();
				String number = group.replaceAll("[^\\d]", "");
				title = title.replace(group, CustomTexture.minus(Integer.parseInt(number)));
			}

			this.originalTitle = title;
			this.title = title;
			this.speed = speed;
			this.rows = rows;
			this.index = start;
		}

		@Override
		public void init() {
			if (!originalTitle.contains("__NOSPLIT__"))
				return;

			final AtomicInteger taskId = new AtomicInteger();

			Tasks.wait(speed, () -> {
				if (!isOpen()) {
					if (index > 0)
						PlayerUtils.send(viewer, "Spaces: " + index);
					Tasks.cancel(taskId.get());
					return;
				}

				title = originalTitle.replaceFirst("__NOSPLIT__", CustomTexture.minus(index));
				++index;
				open(viewer);
			});
		}

	}

}
