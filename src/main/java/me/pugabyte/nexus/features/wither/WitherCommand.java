package me.pugabyte.nexus.features.wither;

import eden.utils.TimeUtils.Time;
import fr.minuskube.inv.SmartInventory;
import lombok.SneakyThrows;
import me.pugabyte.nexus.features.chat.Chat.Broadcast;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.commands.staff.admin.RebootCommand;
import me.pugabyte.nexus.features.warps.Warps;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Redirect(from = "/wchat", to = "/wither chat")
public class WitherCommand extends CustomCommand {

	public WitherCommand(CommandEvent event) {
		super(event);
	}

	public boolean betaMode = true;

	@SneakyThrows
	@Path("challenge")
	void fight() {
		if (!PlayerUtils.isStaffGroup(player()) && betaMode)
			error("The wither is currently being beta tested by staff. It should be back soon!");
		if (RebootCommand.isQueued())
			error("Server reboot is queued, cannot start a new fight");
		if (worldGroup() != WorldGroup.SURVIVAL)
			error("You cannot fight the wither in " + camelCase(worldGroup()));
		if (WitherChallenge.currentFight != null)
			error("The wither is currently being fought. Please wait!");
		if (WitherChallenge.maintenance && !PlayerUtils.isStaffGroup(player()))
			error("The wither arena is currently under maintenance, please wait");
		if (!checkHasItems()) return;
		if (!WitherChallenge.queue.contains(uuid()))
			WitherChallenge.queue.add(uuid());
		else if (WitherChallenge.queue.indexOf(uuid()) > 0)
			error("You are already in the queue. You are spot #" + (WitherChallenge.queue.indexOf(uuid()) + 1));
		if (WitherChallenge.queue.indexOf(uuid()) == 0) {
			SmartInventory.builder()
					.size(3, 9)
					.provider(new DifficultySelectionMenu())
					.title("Select Difficulty")
					.build().open(player());
			return;
		} else
			send(PREFIX + "You have been added to the queue. You are #" + WitherChallenge.queue.size() + " in line. " +
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
		for (ItemStack item : neededItems) {
			if (!player().getInventory().contains(item.getType(), item.getAmount()))
				missingItems.add(item);
		}
		if (missingItems.size() != 0) {
			tellNeededItems(missingItems);
			return false;
		}
		return true;
	}

	public void tellNeededItems(List<ItemStack> mats) {
		send(PREFIX + "&cYou do not have the necessary items in your inventory to spawn the wither. You are missing:");
		for (ItemStack item : mats) {
			send("&c - " + camelCase(item.getType()) + (item.getAmount() > 1 ? " &ex &c" + item.getAmount() : ""));
		}

	}

	@Path("invite <player>")
	void invite(Player player) {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");
		if (!WitherChallenge.currentFight.getHostPlayer().equals(player()))
			error("You are not the host of the current party");
		if (WitherChallenge.currentFight.isStarted())
			error("You cannot invite players after the fight has started");
		send(PREFIX + "You have invited &e" + player.getName() + " &3to fight the wither with you");
		send(player, json(PREFIX + "&e" + player().getName() + " &3has invited you to challenge the wither in " +
				WitherChallenge.currentFight.getDifficulty().getTitle() + " &3mode. ")
				.next("&e&lClick here to join").command("/wither join").hover("&eYou will be added to the wither queue"));
	}

	@Path("join")
	void join() {
		if (!PlayerUtils.isStaffGroup(player()) && betaMode)
			error("The wither is currently being beta tested by staff. It should be back soon!");
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");
		if (WitherChallenge.currentFight.getParty().contains(player().getUniqueId()))
			error("You have already joined the current party! Please wait for the host to start the match.");
		if (WitherChallenge.currentFight.getParty().size() == 4)
			error("The current party is already full");
		if (WitherChallenge.currentFight.isStarted())
			error("The party has already begun the fight!");
		WitherChallenge.currentFight.getParty().add(uuid());
		WitherChallenge.currentFight.broadcastToParty("&e" + player().getName() + " &3has joined the party");
	}

	@Path("abandon")
	void abandon() {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");
		if (!WitherChallenge.currentFight.getHostPlayer().equals(player()))
			error("You are not the host of the challenging party");
		if (WitherChallenge.currentFight.isStarted())
			error("You cannot abandon the fight once it has already begun! Use &c/wither quit &3to resign");
		WitherChallenge.currentFight.broadcastToParty("The host has abandoned the fight and the party has been disbanded");
		WitherChallenge.currentFight.getAlivePlayers().forEach(uuid -> {
			OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
			if (offlinePlayer.getPlayer() != null)
				Warps.spawn(offlinePlayer.getPlayer());
		});
		WitherChallenge.reset();
	}

	@Path("quit")
	void quit() {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");
		if (!WitherChallenge.currentFight.getParty().contains(uuid()))
			error("You are not in the current party.");
		if (WitherChallenge.currentFight.isStarted()) {
			WitherChallenge.currentFight.processPlayerQuit(player(), "quit");
		} else if (WitherChallenge.currentFight.getParty().size() == 1) {
			WitherChallenge.reset();
			send(PREFIX + "You have forfeited the fight. You will keep your items");
		} else {
			WitherChallenge.currentFight.broadcastToParty("&e" + name() + " &3has left the party");
			WitherChallenge.currentFight.getParty().remove(uuid());
		}
	}

	@Path("start")
	void start() {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");
		if (!WitherChallenge.currentFight.getHostPlayer().equals(player()))
			error("You are not the host of the challenging party");
		if (!checkHasItems()) return;
		player().getInventory().removeItem(new ItemStack(Material.WITHER_SKELETON_SKULL, 3), new ItemStack(Material.SOUL_SAND, 4));
		int partySize = WitherChallenge.currentFight.getParty().size();
		String ingame = "&e" + WitherChallenge.currentFight.getHostPlayer().getName() +
				(partySize > 1 ? " and " + (partySize - 1) + " other" + ((partySize - 1 > 1) ? "s" : "") + " &3are" : " &3is") +
				" challenging the wither to a fight in " + WitherChallenge.currentFight.getDifficulty().getTitle() + " &3mode";
		String discord = WitherChallenge.currentFight.getHostPlayer().getName() +
				(partySize > 1 ? " and " + (partySize - 1) + " other" + ((partySize - 1 > 1) ? "s" : "") + " are" : " is") +
				" challenging the wither to a fight in " + StringUtils.camelCase(WitherChallenge.currentFight.getDifficulty().name()) + " mode";

		Broadcast.ingame().prefix("Wither").message(ingame).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();
		Broadcast.discord().prefix("Wither").message(discord).send();

		WitherChallenge.currentFight.teleportPartyToArena();
		Tasks.Countdown.builder()
				.duration(Time.SECOND.x(10))
				.onSecond(i -> {
					if (i == 10)
						WitherChallenge.currentFight.broadcastToParty("The fight will begin in...");
					WitherChallenge.currentFight.broadcastToParty("&e" + i + "s...");
				}).doZero(false)
				.onComplete(() -> WitherChallenge.currentFight.start())
				.start();
	}

	@Path("chat <message...>")
	void chat(String message) {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");
		if (!WitherChallenge.currentFight.getParty().contains(player().getUniqueId()))
			error("You are not in the challenging party.");
		WitherChallenge.currentFight.broadcastToParty("&e" + name() + " &3> &e" + message);
	}

	@Path("spectate")
	void spectate() {
		if (!PlayerUtils.isStaffGroup(player()) && betaMode)
			error("The wither is currently being beta tested by staff. It should be back soon!");
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with &c/wither challenge");
		if (!WitherChallenge.currentFight.isStarted())
			error("The current fight has not started yet. Please wait for it to start");
		if (WitherChallenge.currentFight.getSpectators().contains(uuid()))
			error("You are already spectating the current fight");
		if (WitherChallenge.currentFight.getAlivePlayers().contains(uuid()))
			error("You cannot spectate the match as a party member");
		player().teleport(WitherChallenge.cageLoc);
		player().setGameMode(GameMode.SPECTATOR);
	}

	@Path("reset")
	@Permission("group.staff")
	void reset() {
		WitherChallenge.reset(false);
		send(PREFIX + "Arena successfully reset");
		if (WitherChallenge.queue.size() > 0)
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
		WitherChallenge.maintenance = !WitherChallenge.maintenance;
		send(PREFIX + "Wither arena maintenance mode " + (WitherChallenge.maintenance ? "&aenabled" : "&cdisabled"));
	}

}
