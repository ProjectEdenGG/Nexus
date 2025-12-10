package gg.projecteden.nexus.features.events.y2025.pugmas25;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.events.waypoints.CustomWaypoint;
import gg.projecteden.nexus.features.events.waypoints.WaypointIcon;
import gg.projecteden.nexus.features.events.waypoints.WaypointsManager;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Death;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Districts;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Districts.Pugmas25BiomeDistrict;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Geyser;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Intro;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25SellCrate.Pugmas25SellCrateType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.advent.Pugmas25AdventMenu;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.balloons.Pugmas25BalloonEditor;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.balloons.Pugmas25BalloonEditorUtils;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.balloons.Pugmas25BalloonManager;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.fairgrounds.Pugmas25WhacAMole;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.fairgrounds.slotmachine.Pugmas25SlotMachine;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.fairgrounds.slotmachine.Pugmas25SlotMachineReward;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.fairgrounds.slotmachine.Pugmas25SlotMachineReward.Pugmas25SlotMachineRewardType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.fairgrounds.slotmachine.Pugmas25SlotMachineRewardMenu;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.trains.Pugmas25ModelTrain;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.trains.Pugmas25Train;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25AnglerLoot;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25DeathCause;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25District;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25QuestProgress;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25NPC;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25Quest;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestWaypoint;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.pugmas25.Advent25Config;
import gg.projecteden.nexus.models.pugmas25.Advent25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Advent25Present;
import gg.projecteden.nexus.models.pugmas25.Advent25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@HideFromWiki
@NoArgsConstructor
@Aliases("pugmas")
@Redirect(from = "/advent", to = "/pugmas25 advent")
public class Pugmas25Command extends IEventCommand implements Listener {
	public String PREFIX = Pugmas25.PREFIX;

	private final Pugmas25UserService userService = new Pugmas25UserService();
	private Pugmas25User user;

	private final Pugmas25ConfigService configService = new Pugmas25ConfigService();
	private final Pugmas25Config config = configService.get0();

	private final Advent25ConfigService adventService = new Advent25ConfigService();
	private final Advent25Config adventConfig = adventService.get0();

	public Pugmas25Command(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}

	@Override
	public EdenEvent getEdenEvent() {
		return Pugmas25.get();
	}

	@Path
	void pugmas() {
		if (!user.isVisited())
			WarpType.PUGMAS25.get("hub").teleportAsync(player());
		else
			player().teleportAsync(user.getSpawnLocation(), TeleportCause.COMMAND);
	}

	@Path("npcs setup")
	@Permission(Group.ADMIN)
	void npcs_setup() {
		var npcs = InteractableNPC.getAllNPCs(Pugmas25NPC.class);
		var updated = new AtomicInteger();

		npcs.forEach(npc -> {
			var lookclose = npc.getOrAddTrait(LookClose.class);
			if (!lookclose.isEnabled())
				lookclose.toggle();
			lookclose.setTargetNPCs(true);
			lookclose.setRealisticLooking(true);
			updated.incrementAndGet();
		});

		send(PREFIX + "Updated " + updated.get() + " NPCs");
	}

	@Override
	@Path("quest progress [player]")
	protected void quest_progress(@Arg(value = "self", permission = Group.STAFF) Quester quester) {
		user = userService.get(quester);
		super.quest_progress(quester);
		Pugmas25QuestProgress.DESIGN_A_BALLOON.send(player(), user);
		Pugmas25QuestProgress.MINI_NUTCRACKERS.send(player(), user);
		Pugmas25QuestProgress.ADVENT.send(player(), user);
		line();
		send("&6Repeatable Quests:");
		Pugmas25QuestProgress.ANGLER.send(player(), user);
		Pugmas25QuestProgress.DAILY_FAIRGROUND_TOKENS.send(player(), user);
		line();
	}

	@Path("database deleteQuest <quest>")
	@Permission(Group.ADMIN)
	void database_deleteQuest(Pugmas25Quest pugmasQuest) {
		quester.getQuests().removeIf(quest -> quest.getQuest() == pugmasQuest);
		questerService.save(quester);
	}

	@Path("database deleteQuesterData")
	@Permission(Group.ADMIN)
	void database_deleteQuests() {
		quester.getQuests().removeIf(quest -> quest.getQuest() == Pugmas25Quest.INTRO || quest.getQuest() == Pugmas25Quest.DECORATE_SNOWMEN);
		questerService.save(quester);
		send("Deleted pugmas quest data from QuesterService");
	}

	@Path("database deleteUserData")
	@Permission(Group.ADMIN)
	void database_deleteUserData() {
		userService.delete(user);
		userService.save(user);
		quester.getQuests().removeIf(quest -> quest.getQuest() == Pugmas25Quest.INTRO || quest.getQuest() == Pugmas25Quest.DECORATE_SNOWMEN);
		send("Deleted pugmas quest data from QuesterService");
	}

	@Path("database deleteAllData")
	@Permission(Group.ADMIN)
	void database_deleteAllDatabaseData() {
		for (Quester _user : questerService.getAll()) {
			Quester quester = questerService.get(_user);
			for (Pugmas25Quest pugmas25Quest : Pugmas25Quest.values())
				quester.getQuests().removeIf(quest -> quest.getQuest() == pugmas25Quest);
			questerService.save(quester);
		}
		questerService.cacheAll();
		send("Deleted all pugmas quest data from QuesterService");

		userService.deleteAll();
		send("Deleted all data from Pugmas25UserService");

		new MiniGolfUserService().deleteAll();
		send("Deleted all data from MiniGolfUserService");
	}

	@Path("modelTrain length <length>")
	@Permission(Group.ADMIN)
	void modelTrain_length(@Arg(min = 2, max = 33) int length) {
		Pugmas25ModelTrain.setTrainLength(length);
		send(PREFIX + "Model train length set to " + Pugmas25ModelTrain.getTrainLength());
		Pugmas25ModelTrain.shutdown();
		Pugmas25ModelTrain.startup();
	}

	@Path("coins deposit <amount> [player]")
	@Permission(Group.ADMIN)
	void coins_add(int amount, @Arg("self") Pugmas25User user) {
		Currency.COIN_POUCH.deposit(user.getPlayer(), Price.of(amount));
	}

	@Path("coins withdraw <amount> [player]")
	@Permission(Group.ADMIN)
	void coins_remove(int amount, @Arg("self") Pugmas25User user) {
		Currency.COIN_POUCH.withdraw(user.getPlayer(), Price.of(amount), null, null);
	}

	@Path("makeSellCrate <type>")
	@Permission(Group.ADMIN)
	void makeSellCrate(Pugmas25SellCrateType type) {
		type.applyToSign(getTargetBlockRequired());
	}

	@Path("sidebar")
	@Permission(Group.ADMIN)
	void sidebar() {
		Pugmas25.get().getSidebar().handleJoin(player());
	}

	@Path("district")
	@Description("View which district you are currently in")
	@Permission(Group.ADMIN)
	void district() {
		Pugmas25District district = Pugmas25Districts.of(player());
		send(PREFIX + "Area: &e" + district.getName());

		Pugmas25BiomeDistrict biome = Pugmas25BiomeDistrict.of(player());
		if (biome != null)
			send(PREFIX + "Biome: &e" + biome.getName());

	}

	@Path("nutcrackers addLocation")
	@Permission(Group.ADMIN)
	void nutcrackers_addLocation() {
		Block block = getTargetBlockRequired(Material.BARRIER);
		config.getNutCrackerLocations().add(block.getLocation());
		configService.save(config);
		send("Added nutcrackers location: " + StringUtils.xyz(block));
	}

	@Path("nutcrackers removeLocation")
	@Permission(Group.ADMIN)
	void nutcrackers_removeLocation() {
		Block block = getTargetBlockRequired(Material.BARRIER);
		config.getNutCrackerLocations().remove(block.getLocation());
		configService.save(config);
		send("Removed nutcrackers location: " + StringUtils.xyz(block));
	}

	@Path("advent")
	@Description("Open the advent calender")
	void advent(@Arg(value = "30", permission = Group.ADMIN) @Switch int frameTicks) {
		Advent25User advent = user.advent();

		if (!advent.isUnlockedQuest())
			throw new InvalidInputException("You haven't unlocked this quest yet");

		new Pugmas25AdventMenu(advent, frameTicks).open(player());
	}

	@Path("now [timestamp]")
	@Permission(Group.ADMIN)
	void now(String timestamp) {
		if (timestamp.equalsIgnoreCase("null")) {
			Pugmas25.get().now(null);
			send(PREFIX + "Reset timestamp override");
			return;
		}

		LocalDateTime time = null;
		if (Utils.isInt(timestamp))
			time = LocalDate.of(2025, 12, Integer.parseInt(timestamp)).atStartOfDay();
		if (time == null)
			try { time = LocalDate.parse(timestamp).atStartOfDay(); } catch (Exception ignore) {}
		if (time == null)
			try { time = LocalDateTime.parse(timestamp); } catch (Exception ignore) {}
		if (time == null)
			error("Could not parse timestamp: " + timestamp + " - Try using DD or YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS");

		Pugmas25.get().now(time);
		send(PREFIX + "Set now to " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(time));
	}

	@Path("advent waypoint <day>")
	@Description("Get directions to a present you've already found")
	void advent_waypoint(Advent25Present present) {
		if (!user.advent().hasFound(present))
			error("You have not found day &e#" + present.getDay());

		WaypointsManager.showWaypoint(player(), present);
	}

	@Path("waypoints show <target> [--icon] [--color]")
	@Description("Get directions to waypoint target")
	@Permission(Group.ADMIN)
	void waypoints_show(Pugmas25QuestWaypoint target, @Switch WaypointIcon icon, @Switch Color color) {
		if (color == null)
			color = target.getColor();
		if (icon == null)
			icon = target.getIcon();

		WaypointsManager.showWaypoint(player(), new CustomWaypoint(icon, color, target.getLocation()));
	}

	@Path("waypoints hideAll")
	@Permission(Group.ADMIN)
	void waypoints_hideAll() {
		WaypointsManager.hideAllWaypoints(player());
	}

	@Path("nutcracker hasFoundTarget")
	@Permission(Group.ADMIN)
	void nutcracker_hasFoundTarget() {
		Location location = getTargetBlockRequired().getLocation();
		if (!config.getNutCrackerLocations().contains(location))
			error("Nutcracker not found in that location");

		List<String> results = new ArrayList<>();
		for (Pugmas25User _user : userService.getAll()) {
			if (_user.getFoundNutCrackers().contains(location))
				results.add("&3- &e" + _user.getNickname());
		}

		if (results.isEmpty())
			error("No one has found this nutcracker");

		send("&3Users who found this nutcracker:");
		for (String line : results) {
			send(line);
		}
	}

	@Path("angler randomAnglerLoot")
	@Permission(Group.ADMIN)
	void angler_setQuestFish() {
		config.setAnglerQuestFish(RandomUtils.randomElement(Pugmas25AnglerLoot.values()));
		configService.save(config);
		send("Set angler quest fish to " + config.getAnglerQuestFish().getCustomName());
	}

	@Path("angler set caughtAnglerQuestLoot [status] [player]")
	@Permission(Group.ADMIN)
	void angler_set_caughtAnglerQuestLoot(Boolean status, @Arg("self") Pugmas25User user) {
		if (status == null)
			status = !user.isCaughtAnglerQuestLoot();

		user.setCaughtAnglerQuestLoot(status);
		userService.save(user);
		send(PREFIX + "Set " + user.getNickname() + "'s caughtAnglerQuestLoot to " + status);
	}

	@Path("angler set completedAnglerQuest [status] [player]")
	@Permission(Group.ADMIN)
	void angler_set_completedAnglerQuest(Boolean status, @Arg("self") Pugmas25User user) {
		if (status == null)
			status = !user.isCompletedAnglerQuest();

		user.setCompletedAnglerQuest(status);
		userService.save(user);
		send(PREFIX + "Set " + user.getNickname() + "'s completedAnglerQuest to " + status);
	}

	@Path("angler status [player]")
	@Permission(Group.ADMIN)
	void angler_status(@Arg("self") Pugmas25User user) {
		send(user.getNickname() + ".isCompletedAnglerQuest(): " + user.isCompletedAnglerQuest());
		send(user.getNickname() + ".isCaughtAnglerQuestLoot(): " + user.isCaughtAnglerQuestLoot());
	}

	@Path("angler reset [player]")
	@Permission(Group.ADMIN)
	void angler_resetData(@Arg("self") Pugmas25User user) {
		user.resetAnglerQuest();
		userService.save(user);
		send(PREFIX + "Reset " + user.getNickname() + "'s angler variables");
	}

	@Path("angler resetAll")
	@Permission(Group.ADMIN)
	void angler_resetAll() {
		userService.resetAllAnglerQuests();
		send(PREFIX + "Reset everyone's angler variables");
	}

	@Path("advent tp <day>")
	@Permission(Group.ADMIN)
	void advent_tp(Advent25Present present) {
		user.advent().teleportAsync(present);
		send(PREFIX + "Teleported to day #" + present.getDay());
	}

	@Path("advent nearest")
	@Permission(Group.ADMIN)
	void advent_nearest() {
		Advent25Present nearestPresent = Collections.min(adventConfig.getPresents(), Comparator.comparing(present -> distanceTo(present).get()));

		if (nearestPresent == null)
			error("None found");

		send(PREFIX + "Nearest: #" + nearestPresent.getDay());
	}

	@Path("advent get <day>")
	@Permission(Group.ADMIN)
	void advent_get(Advent25Present present) {
		giveItem(present.getItem());
	}

	@Path("advent getAllCollected <day>")
	@Permission(Group.ADMIN)
	void advent_getCollected(@Arg(min = 1, max = 25) int day) {
		send("Collected day: " + day);
		for (Pugmas25User user : new Pugmas25UserService().getAll()) {
			if (user.advent().hasCollected(day))
				send(" - " + user.getNickname());
		}
	}

	@Path("advent config create <day>")
	@Permission(Group.ADMIN)
	void advent_config_create(@Arg(min = 1, max = 25) int day) {
		final Block block = getTargetBlockRequired();
		if (block.getType() != Material.BARRIER)
			error("You must be looking at a barrier");

		adventConfig.set(day, block.getLocation());
		adventService.save(adventConfig);

		send(PREFIX + "Advent day #" + day + " configured");
	}

	@Path("advent config createLoot <day>")
	@Permission(Group.ADMIN)
	void advent_config_createLoot(@Arg(min = 1, max = 25) int day) {
		final Block block = getTargetBlockRequired();
		if (block.getType() != Material.CHEST)
			error("You must be looking at a chest");

		Chest chest = (Chest) block.getState();
		List<ItemStack> contents = Arrays.stream(chest.getBlockInventory().getContents())
			.filter(Nullables::isNotNullOrAir)
			.collect(Collectors.toList());

		if (isNullOrEmpty(contents))
			Nexus.warn("Contents are empty");

		adventConfig.get(day).setContents(contents);
		adventService.save(adventConfig);

		send(PREFIX + "Loot for Advent day #" + day + " configured");
	}

	@Path("advent config delete <day>")
	@Permission(Group.ADMIN)
	void advent_config_delete(Advent25Present present) {
		adventConfig.remove(present);

		UUID uuid = present.getEntityUuid();
		if (uuid != null) {
			var entity = ClientSideConfig.getEntity(uuid);
			if (entity != null)
				ClientSideConfig.delete(entity);
		}

		ClientSideConfig.save();
		send(PREFIX + "Advent day #" + present.getDay() + " deleted");
	}

	@Path("death <cause>")
	@Permission(Group.ADMIN)
	void death_test(Pugmas25DeathCause deathCause) {
		Pugmas25Death.onDeath(player(), deathCause, null);
	}

	@Path("slotMachine rewards")
	@Permission(Group.ADMIN)
	void slotMachine_rewards() {
		new Pugmas25SlotMachineRewardMenu().open(player());
	}

	@Path("slotMachine roll")
	@Permission(Group.ADMIN)
	void slotMachine() {
		Pugmas25SlotMachine.get().start(player());
	}

	@Path("slotMachine simulateWin <reward> <type>")
	@Permission(Group.ADMIN)
	void slotMachine_reward(Pugmas25SlotMachineReward reward, Pugmas25SlotMachineRewardType type) {
		reward.give(player(), type);
	}

	@Path("slotMachine setup")
	@Permission(Group.ADMIN)
	void slotMachine_setup() {
		Pugmas25SlotMachine.get().init();
	}

	@Path("slotMachine reset")
	@Permission(Group.ADMIN)
	void slotMachine_reset() {
		Pugmas25SlotMachine.get().reset();
	}

	@Path("whacamole debug")
	@Permission(Group.ADMIN)
	void whacAMole_debug() {
		Pugmas25WhacAMole game = Pugmas25WhacAMole.get();
		send("Playing = " + game.isPlaying());
		send("Player = " + (game.getGamer() == null ? "null" : Nickname.of(game.getGamer())));
	}

	@Path("whacamole setup")
	@Permission(Group.ADMIN)
	void whacAMole_setup() {
		Pugmas25WhacAMole.get().init();
	}

	@Path("whacamole start")
	@Permission(Group.ADMIN)
	void whacAMole_start() {
		Pugmas25WhacAMole.get().start(player());
	}

	@Path("whacamole reset")
	@Permission(Group.ADMIN)
	void whacAMole_reset() {
		Pugmas25WhacAMole.get().reset();
	}

	@Path("geyser start")
	@Permission(Group.STAFF)
	void startGeyser() {
		send(PREFIX + "Starting geyser animation");
		Pugmas25Geyser.animate();
	}

	@Path("geyser stop")
	@Permission(Group.STAFF)
	void stopGeyser() {
		send(PREFIX + "Stopping geyser animation");
		Pugmas25Geyser.reset();
	}

	@Path("intro start")
	@Permission(Group.ADMIN)
	void intro_start() {
		Pugmas25Intro.play(player());
	}

	@Path("train debug")
	@Permission(Group.ADMIN)
	void train_debug() {
		Pugmas25Train.getDefault().build().debug(player());
	}

	@Path("train start")
	@Permission(Group.ADMIN)
	void train_start() {
		Pugmas25Train.start();
	}

	@Path("train crossings open")
	@Permission(Group.ADMIN)
	void train_crossings_open() {
		Pugmas25Train.trainCrossings.openCrossings();
	}

	@Path("train crossings close")
	@Permission(Group.ADMIN)
	void train_crossings_close() {
		Pugmas25Train.trainCrossings.closeCrossings();
	}

	@Path("balloon endSession")
	@Description("End the session without saving")
	@Permission(Group.ADMIN)
	void balloon_end() {
		if (!Pugmas25BalloonEditor.isBeingUsed())
			error("The editor is not being used");

		send(PREFIX + "Ended " + Pugmas25BalloonEditorUtils.getEditorName() + "'s session without saving");
		Pugmas25BalloonEditor.reset();
	}

	@Path("balloon listPlacements")
	@Description("List the currently placed balloons and their regions")
	@Permission(Group.ADMIN)
	void balloon_listPlacements() {
		send("Placed balloons:");
		Pugmas25BalloonManager.getUserPlacementRegions().keySet().stream().sorted().toList().forEach(regionId -> {
			Nerd nerd = Nerd.of(Pugmas25BalloonManager.getUserPlacementRegions().get(regionId));
			String uuid = nerd.getUniqueId().toString();
			String nickname = nerd.getNickname();

			send(json(" - " + regionId + " = ").group().next(nickname).hover(uuid).insert(uuid));
		});
	}

	@ConverterFor(Advent25Present.class)
	Advent25Present convertToAdvent25Present(String value) {
		if (!Utils.isInt(value))
			error("Day must be between 1 and 25");

		var day = Integer.parseInt(value);
		if (day < 1 || day > 25)
			error("Day must be between 1 and 25");

		var present = Advent25Config.get().get(day);
		if (present == null)
			error("No advent day #" + day + " configured");

		return present;
	}

	@TabCompleterFor(Advent25Present.class)
	List<String> tabCompleteAdvent25Present(String filter) {
		return IntStream.rangeClosed(1, 25).boxed()
			.map(String::valueOf)
			.filter(day -> day.startsWith(filter))
			.toList();
	}

}
