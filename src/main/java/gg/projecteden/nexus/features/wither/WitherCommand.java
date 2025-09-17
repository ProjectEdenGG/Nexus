package gg.projecteden.nexus.features.wither;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.features.wither.fights.CorruptedFight;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.witherarena.WitherArenaConfig;
import gg.projecteden.nexus.models.witherarena.WitherArenaConfigService;
import gg.projecteden.nexus.utils.FuzzyItemStack;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.SneakyThrows;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Redirect(from = "/wchat", to = "/wither chat")
public class WitherCommand extends CustomCommand {

	public WitherCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path("challenge")
	@Description("Challenge the wither to a fight")
	void fight() {
		if (!isStaff() && WitherArenaConfig.isBeta())
			error("The wither is currently being beta tested by staff. It should be back soon!");

		if (Nexus.isMaintenanceQueued())
			error("Server maintenance is queued, cannot start a new fight");

		if (worldGroup() != WorldGroup.SURVIVAL)
			error("You cannot fight the wither in " + camelCase(worldGroup()));

		if (WitherArenaConfig.isMaintenance() && !isStaff())
			error("The wither arena is currently under maintenance, please wait");

		if (!checkHasItems())
			return;

		if (WitherChallenge.currentFight != null)
			error("The wither is already being fought, please try again later");

		new DifficultySelectionMenu().open(player());

		/*
		final WitherArenaConfigService service = new WitherArenaConfigService();
		final WitherArenaConfig config = service.get0();
		final List<UUID> queue = config.getQueue();
		int index = queue.indexOf(uuid());

		if (index > 0)
			error("You are already in the queue. You are spot #" + (index + 1));

		if (index == -1) {
			queue.add(uuid());
			service.save(config);
			index = queue.indexOf(uuid());
		}

		if (index == 0)
			new DifficultySelectionMenu().open(player());
		else
			send(PREFIX + "You have been added to the queue. You are #" + queue.size() + " in line. " +
					"You will be prompted when it is your time to challenge the wither. Please keep the necessary items on you to spawn the Wither");
		 */
	}

	public void tellNeededItems(List<FuzzyItemStack> items) {
		send(PREFIX + "&cYou do not have the necessary items in your inventory to spawn the wither. You are missing:");
		for (FuzzyItemStack item : items)
			send("&c - " + StringUtils.pretty(item));
	}

	@Path("invite <player>")
	@Description("Invite players to your party")
	void invite(Player player) {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!WitherChallenge.currentFight.getHostPlayer().equals(player()))
			error("You are not the host of the current party");

		if (WitherChallenge.currentFight.isStarted())
			error("You cannot invite players after the fight has started");

		send(PREFIX + "You have invited &e" + Nickname.of(player) + " &3to fight the wither with you");
		send(player, json(PREFIX + "&e" + nickname() + " &3has invited you to challenge the wither in " +
				WitherChallenge.currentFight.getDifficulty().getTitle() + " &3mode. ")
				.next("&e&lClick here to join").command("/wither join").hover("&eYou will be added to the wither queue"));
	}

	@Path("join")
	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	// TODO Only work if invited
	void join() {
		if (!Rank.of(player()).isStaff() && WitherArenaConfig.isBeta())
			error("The wither is currently being beta tested by staff. It should be back soon!");

		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (WitherChallenge.currentFight.isInParty(player()))
			error("You have already joined the current party! Please wait for the host to start the match.");

		if (WitherChallenge.currentFight.getParty().size() == 4)
			error("The current party is already full");

		if (WitherChallenge.currentFight.isStarted())
			error("The party has already begun the fight!");

		WitherChallenge.currentFight.getParty().add(uuid());
		WitherChallenge.currentFight.broadcastToParty("&e" + nickname() + " &3has joined the party");
	}

	@Path("abandon")
	@Description("Cancel your challenge")
	void abandon() {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!WitherChallenge.currentFight.getHostPlayer().equals(player()))
			error("You are not the host of the challenging party");

		if (WitherChallenge.currentFight.isStarted())
			error("You cannot abandon the fight once it has already begun! Use &c/wither quit &3to resign");

		WitherChallenge.currentFight.broadcastToParty("The host has abandoned the fight and the party has been disbanded");
		WitherChallenge.currentFight.alivePlayers().forEach(Warps::survival);
		WitherChallenge.reset();
	}

	@Path("quit")
	@Description("Forfeit the fight")
	void quit() {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!WitherChallenge.currentFight.isInParty(player()))
			error("You are not in the current party.");

		if (WitherChallenge.currentFight.isStarted())
			WitherChallenge.currentFight.processPlayerQuit(player(), "quit");
		else if (WitherChallenge.currentFight.getParty().size() == 1) {
			WitherChallenge.reset();
			send(PREFIX + "You have forfeited the fight. You will keep your items");
		} else {
			WitherChallenge.currentFight.broadcastToParty("&e" + name() + " &3has left the party");
			WitherChallenge.currentFight.getParty().remove(uuid());
		}
	}

	@Path("start")
	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	void start() {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!WitherChallenge.currentFight.getHostPlayer().equals(player()))
			error("You are not the host of the challenging party");

		if (WitherChallenge.currentFight.isStarted())
			error("The fight has already started!");

		if (!checkHasItems())
			return;

		removeRequiredItems();

		int partySize = WitherChallenge.currentFight.getParty().size();
		String message = "&e" + Nickname.of(WitherChallenge.currentFight.getHostPlayer()) +
			(partySize > 1 ? " and " + (partySize - 1) + " other" + ((partySize - 1 > 1) ? "s" : "") + " &3are" : " &3is") +
			" challenging the wither to a fight in " + WitherChallenge.currentFight.getDifficulty().getTitle() + " &3mode";

		if (WitherArenaConfig.isBeta())
			Broadcast.staffIngame().prefix("Wither").message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();
		else
			Broadcast.all().prefix("Wither").message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();

		WitherChallenge.currentFight.teleportPartyToArena();
		Tasks.Countdown.builder()
			.duration(TickTime.SECOND.x(10))
			.onSecond(i -> {
				if (WitherChallenge.currentFight != null) {
					if (i == 10)
						WitherChallenge.currentFight.broadcastToParty("The fight will begin in...");
					WitherChallenge.currentFight.broadcastToParty("&e" + i + "s...");
				}
			})
			.doZero(false)
			.onComplete(() -> {
				if (WitherChallenge.currentFight != null)
					WitherChallenge.currentFight.start();
			})
			.start();
	}

	private static final Map<FuzzyItemStack, Boolean> required = new LinkedHashMap<>() {{
		put(new FuzzyItemStack(Material.WITHER_SKELETON_SKULL, 3), true);
		put(new FuzzyItemStack(Set.of(Material.SOUL_SAND, Material.SOUL_SOIL), 4), true);
		put(new FuzzyItemStack(Set.of(Material.BOW, Material.CROSSBOW), 1), false);
		put(new FuzzyItemStack(MaterialTag.ARROWS, 1), false);
	}};

	public boolean checkHasItems() {
		PlayerInventory inventory = player().getInventory();
		List<FuzzyItemStack> missing = new ArrayList<>();

		requiredItems:
		for (FuzzyItemStack item : required.keySet()) {
			for (Material material : item.getMaterials())
				if (inventory.contains(material, item.getAmount()))
					continue requiredItems;
			missing.add(item);
		}

		if (!missing.isEmpty()) {
			tellNeededItems(missing);
			return false;
		}

		return true;
	}

	private void removeRequiredItems() {
		for (FuzzyItemStack item : required.keySet())
			if (required.get(item))
				removeRequiredItems(item);
	}

	private void removeRequiredItems(FuzzyItemStack item) {
		for (Material material : item.getMaterials()) {
			int removed = 0;
			for (ItemStack content : inventory().getContents()) {
				if (Nullables.isNullOrAir(content))
					continue;
				if (content.getType() != material)
					continue;

				while (content.getAmount() > 0) {
					content.setAmount(content.getAmount() - 1);
					if (++removed == item.getAmount())
						return;
				}
			}
		}

		error("Could not remove %s from your inventory".formatted(StringUtils.pretty(item)));
	}

	@Path("chat <message...>")
	@Description("Send a message to all members of the party")
	void chat(String message) {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!WitherChallenge.currentFight.isInParty(player()))
			error("You are not in the challenging party.");

		WitherChallenge.currentFight.broadcastToParty("&e" + name() + " &3> &e" + message);
	}

	@Path("spectate")
	@Description("Spectate the fight")
	void spectate() {
		error("Wither spectating is currently disabled due to a bug");

		if (!Rank.of(player()).isStaff() && WitherArenaConfig.isBeta())
			error("The wither is currently being beta tested by staff. It should be back soon!");

		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!WitherChallenge.currentFight.isStarted())
			error("The current fight has not started yet. Please wait for it to start");

		if (WitherChallenge.currentFight.isSpectating(player()))
			error("You are already spectating the current fight");

		if (WitherChallenge.currentFight.isAlive(player()))
			error("You cannot spectate the match as a party member");

		WitherChallenge.currentFight.getSpectators().add(player().getUniqueId());
		player().teleportAsync(WitherChallenge.cageLoc).thenRun(() ->
			Tasks.wait(3, () -> player().setGameMode(GameMode.SPECTATOR)));
	}

	@Path("reset")
	@Permission(Group.STAFF)
	@Description("Reset the wither arena")
	void reset() {
		WitherChallenge.reset(false);
		send(PREFIX + "Arena successfully reset");
		final List<UUID> queue = new WitherArenaConfigService().get0().getQueue();
		if (queue.size() > 0)
			send(json(PREFIX + "&eThere are " + queue.size() + " players queued to fight the wither. Click here to process the queue.").command("/wither processQueue"));
	}

	@Path("processQueue")
	@Permission(Group.STAFF)
	@Description("Proccess the fight queue")
	void processQueue() {
		WitherChallenge.processQueue();
		send(PREFIX + "Sent queue notification to the next player");
	}

	@Path("maintenance")
	@Permission(Group.STAFF)
	@Description("Enable maintenance mode")
	void maintenance() {
		new WitherArenaConfigService().edit0(config -> config.setMaintenance(!WitherArenaConfig.isMaintenance()));
		send(PREFIX + "Wither arena maintenance mode " + (WitherArenaConfig.isMaintenance() ? "&aenabled" : "&cdisabled"));
	}

	@Path("beta")
	@Permission(Group.ADMIN)
	@Description("Enable beta mode")
	void beta() {
		new WitherArenaConfigService().edit0(config -> config.setBeta(!WitherArenaConfig.isBeta()));
		send(PREFIX + "Wither arena beta mode " + (WitherArenaConfig.isBeta() ? "&aenabled" : "&cdisabled"));
	}

	@Path("getFragment")
	@Permission(Group.ADMIN)
	@Description("Receive a wither fragment")
	void fragment() {
		PlayerUtils.giveItem(player(), WitherChallenge.WITHER_FRAGMENT);
	}

	@Path("testCounterAttack <counterAttack>")
	@Permission(Group.ADMIN)
	@Description("Test a counter attack")
	void testCounterAttack(CorruptedFight.CorruptedCounterAttacks counterAttack) {
		if (WitherChallenge.currentFight instanceof CorruptedFight)
			counterAttack.execute(WitherChallenge.currentFight.alivePlayers());
	}

}
