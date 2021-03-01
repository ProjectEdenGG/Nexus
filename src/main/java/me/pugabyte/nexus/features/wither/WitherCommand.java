package me.pugabyte.nexus.features.wither;

import fr.minuskube.inv.SmartInventory;
import lombok.SneakyThrows;
import me.pugabyte.nexus.features.warps.Warps;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Permission("group.staff")
public class WitherCommand extends CustomCommand {

	public WitherCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path("challenge")
	void fight() {
		if (WitherChallenge.currentFight != null)
			error("The wither is currently being fought. Please wait!");
		if (!hasItems())
			error("You do not have the necessary items in your inventory to spawn the wither");
		SmartInventory.builder()
				.size(3, 9)
				.provider(new DifficultySelectionMenu())
				.title("Select Difficulty")
				.build().open(player());
	}

	public boolean hasItems() {
		if (!player().getInventory().contains(Material.WITHER_SKELETON_SKULL, 3)) return false;
		return player().getInventory().contains(Material.SOUL_SAND, 4);
	}

	@Path("invite <player>")
	void invite(Player player) {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with /wither challenge");
		if (WitherChallenge.currentFight.host != player())
			error("You are not the host of the current party");
		send(PREFIX + "You have invited &e" + player.getName() + " &3to fight the wither with you");
		send(player, json(PREFIX + "&e" + player().getName() + " &3has invited you to challenge the wither in " +
				WitherChallenge.currentFight.getDifficulty().getTitle() + " &3mode.")
				.next("&e&lClick here to join").command("/wither join").hover("&eYou will be added to the wither queue"));
	}

	@Path("join")
	void join() {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with /wither challenge");
		if (WitherChallenge.currentFight.getParty().contains(player().getUniqueId()))
			error("You have already joined the current party! Please wait for the host to start the match.");
		WitherChallenge.currentFight.getParty().add(uuid());
		WitherChallenge.currentFight.broadcastToParty("&e" + player().getName() + " &3has joined the party");
	}

	@Path("abandon")
	void abandon() {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with /wither challenge");
		if (WitherChallenge.currentFight.host != player())
			error("You are not the host of the challenging party");
		WitherChallenge.currentFight.broadcastToParty("The host has abandoned the fight and the party has been disbanded");
		WitherChallenge.currentFight.getAlivePlayers().forEach(uuid -> {
			OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
			if (offlinePlayer.getPlayer() != null)
				Warps.spawn(offlinePlayer.getPlayer());
		});
		WitherChallenge.reset();
	}

	@Path("start")
	void start() {
		if (WitherChallenge.currentFight == null)
			error("There is currently no challenging party. You can make one with /wither challenge");
		if (WitherChallenge.currentFight.host != player())
			error("You are not the host of the challenging party");
		if (!hasItems())
			error("You do not have the necessary items in your inventory to spawn the wither");
		player().getInventory().removeItem(new ItemStack(Material.WITHER_SKELETON_SKULL, 3), new ItemStack(Material.SOUL_SAND, 4));
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

	@Path("reset")
	void reset() {
		WitherChallenge.reset();
	}


}
