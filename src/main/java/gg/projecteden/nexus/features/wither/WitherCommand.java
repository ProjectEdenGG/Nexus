package gg.projecteden.nexus.features.wither;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.commands.staff.admin.RebootCommand;
import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.SneakyThrows;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.wither.WitherChallenge.currentFight;
import static gg.projecteden.nexus.features.wither.WitherChallenge.maintenance;
import static gg.projecteden.nexus.features.wither.WitherChallenge.queue;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

@Redirect(from = "/wchat", to = "/wither chat")
public class WitherCommand extends CustomCommand {

	public WitherCommand(CommandEvent event) {
		super(event);
	}

	public static boolean betaMode = false;

	@SneakyThrows
	@Path("challenge")
	void fight() {
		if (!isStaff() && betaMode)
			error("The wither is currently being beta tested by staff. It should be back soon!");

		if (RebootCommand.isQueued())
			error("Server reboot is queued, cannot start a new fight");

		if (worldGroup() != WorldGroup.SURVIVAL)
			error("You cannot fight the wither in " + camelCase(worldGroup()));

		if (maintenance && !Rank.of(player()).isStaff())
			error("The wither arena is currently under maintenance, please wait");

		if (!checkHasItems())
			return;

		int index = queue.indexOf(uuid());

		if (index > 0)
			error("You are already in the queue. You are spot #" + (index + 1));

		if (index == -1) {
			queue.add(uuid());
			index = queue.indexOf(uuid());
		}

		if (index == 0)
			new DifficultySelectionMenu().open(player());
		else
			send(PREFIX + "You have been added to the queue. You are #" + queue.size() + " in line. " +
					"You will be prompted when it is your time to challenge the wither. Please keep the necessary items on you to spawn the Wither");
	}

	public boolean checkHasItems() {
		List<ItemStack> missingItems = new ArrayList<>();
		List<ItemStack> neededItems = new ArrayList<>() {{
			add(new ItemStack(Material.WITHER_SKELETON_SKULL, 3));
			add(new ItemStack(Material.SOUL_SAND, 4));
			add(new ItemStack(Material.BOW));
			add(new ItemStack(Material.ARROW));
		}};

		PlayerInventory inventory = player().getInventory();
		for (ItemStack item : neededItems) {
			if (item.getType().equals(Material.BOW)) {

				if (!inventory.contains(item.getType(), item.getAmount()) && !inventory.contains(Material.CROSSBOW, item.getAmount()))
					missingItems.add(item);
			}

			if (!inventory.contains(item.getType(), item.getAmount())) {
				// if player has a crossbow instead of a bow
				if (item.getType().equals(Material.BOW) && inventory.contains(Material.CROSSBOW, item.getAmount()))
					continue;

				missingItems.add(item);
			}

		}
		if (missingItems.size() != 0) {
			tellNeededItems(missingItems);
			return false;
		}
		return true;
	}

	public void tellNeededItems(List<ItemStack> items) {
		send(PREFIX + "&cYou do not have the necessary items in your inventory to spawn the wither. You are missing:");
		for (ItemStack item : items)
			send("&c - " + camelCase(item.getType()) + (item.getAmount() > 1 ? " &ex &c" + item.getAmount() : ""));

	}

	@Path("invite <player>")
	void invite(Player player) {
		if (currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!currentFight.getHostPlayer().equals(player()))
			error("You are not the host of the current party");

		if (currentFight.isStarted())
			error("You cannot invite players after the fight has started");

		send(PREFIX + "You have invited &e" + Nickname.of(player) + " &3to fight the wither with you");
		send(player, json(PREFIX + "&e" + nickname() + " &3has invited you to challenge the wither in " +
				currentFight.getDifficulty().getTitle() + " &3mode. ")
				.next("&e&lClick here to join").command("/wither join").hover("&eYou will be added to the wither queue"));
	}

	@Path("join")
	void join() {
		if (!Rank.of(player()).isStaff() && betaMode)
			error("The wither is currently being beta tested by staff. It should be back soon!");

		if (currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (currentFight.isInParty(player()))
			error("You have already joined the current party! Please wait for the host to start the match.");

		if (currentFight.getParty().size() == 4)
			error("The current party is already full");

		if (currentFight.isStarted())
			error("The party has already begun the fight!");

		currentFight.getParty().add(uuid());
		currentFight.broadcastToParty("&e" + nickname() + " &3has joined the party");
	}

	@Path("abandon")
	void abandon() {
		if (currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!currentFight.getHostPlayer().equals(player()))
			error("You are not the host of the challenging party");

		if (currentFight.isStarted())
			error("You cannot abandon the fight once it has already begun! Use &c/wither quit &3to resign");

		currentFight.broadcastToParty("The host has abandoned the fight and the party has been disbanded");
		currentFight.alivePlayers().forEach(Warps::spawn);
		WitherChallenge.reset();
	}

	@Path("quit")
	void quit() {
		if (currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!currentFight.isInParty(player()))
			error("You are not in the current party.");

		if (currentFight.isStarted())
			currentFight.processPlayerQuit(player(), "quit");
		else if (currentFight.getParty().size() == 1) {
			WitherChallenge.reset();
			send(PREFIX + "You have forfeited the fight. You will keep your items");
		} else {
			currentFight.broadcastToParty("&e" + name() + " &3has left the party");
			currentFight.getParty().remove(uuid());
		}
	}

	@Path("start")
	void start() {
		if (currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!currentFight.getHostPlayer().equals(player()))
			error("You are not the host of the challenging party");

		if (!checkHasItems())
			return;

		removeRequiredItems(Material.WITHER_SKELETON_SKULL, 3);
		removeRequiredItems(Material.SOUL_SAND, 4);

		int partySize = currentFight.getParty().size();
		String message = "&e" + Nickname.of(currentFight.getHostPlayer()) +
				(partySize > 1 ? " and " + (partySize - 1) + " other" + ((partySize - 1 > 1) ? "s" : "") + " &3are" : " &3is") +
				" challenging the wither to a fight in " + currentFight.getDifficulty().getTitle() + " &3mode";

		if (betaMode)
			Broadcast.staffIngame().prefix("Wither").message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();
		else
			Broadcast.all().prefix("Wither").message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();

		currentFight.teleportPartyToArena();
		Tasks.Countdown.builder()
			.duration(TickTime.SECOND.x(10))
			.onSecond(i -> {
				if (currentFight != null) {
					if (i == 10)
						currentFight.broadcastToParty("The fight will begin in...");
					currentFight.broadcastToParty("&e" + i + "s...");
				}
			})
			.doZero(false)
			.onComplete(() -> {
				if (currentFight != null)
					currentFight.start();
			})
			.start();
	}

	private void removeRequiredItems(Material material, int amount) {
		int removed = 0;
		for (ItemStack content : inventory().getContents()) {
			if (isNullOrAir(content))
				continue;
			if (content.getType() != material)
				continue;

			while (content.getAmount() > 0) {
				content.setAmount(content.getAmount() - 1);
				if (++removed == amount)
					return;
			}
		}

		error("Could not remove " + (amount - removed) + " " + camelCase(material) + " from your inventory");
	}

	@Path("chat <message...>")
	void chat(String message) {
		if (currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!currentFight.isInParty(player()))
			error("You are not in the challenging party.");

		currentFight.broadcastToParty("&e" + name() + " &3> &e" + message);
	}

	@Path("spectate")
	void spectate() {
		if (!Rank.of(player()).isStaff() && betaMode)
			error("The wither is currently being beta tested by staff. It should be back soon!");

		if (currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");

		if (!currentFight.isStarted())
			error("The current fight has not started yet. Please wait for it to start");

		if (currentFight.isSpectating(player()))
			error("You are already spectating the current fight");

		if (currentFight.isAlive(player()))
			error("You cannot spectate the match as a party member");

		currentFight.getSpectators().add(player().getUniqueId());
		player().teleportAsync(WitherChallenge.cageLoc).thenRun(() ->
			Tasks.wait(3, () -> player().setGameMode(GameMode.SPECTATOR)));
	}

	@Path("reset")
	@Permission("group.staff")
	void reset() {
		WitherChallenge.reset(false);
		send(PREFIX + "Arena successfully reset");
		if (queue.size() > 0)
			send(json(PREFIX + "&eThere are players queued to fight the wither. Click here to process the queue.").command("/wither processQueue"));
	}

	@Path("processQueue")
	@Permission("group.staff")
	void processQueue() {
		WitherChallenge.processQueue();
		send(PREFIX + "Sent queue notification to the next player");
	}

	@Path("maintenance")
	@Permission("group.staff")
	void maintenance() {
		maintenance = !maintenance;
		send(PREFIX + "Wither arena maintenance mode " + (maintenance ? "&aenabled" : "&cdisabled"));
	}

	@Path("getFragment")
	@Permission("group.admin")
	void fragment() {
		PlayerUtils.giveItem(player(), WitherChallenge.getWitherFragment());
	}

}
