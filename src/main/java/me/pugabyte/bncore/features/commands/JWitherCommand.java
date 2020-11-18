package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.MaterialTag.MatchMode;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class JWitherCommand extends CustomCommand implements Listener {

	public String PREFIX = StringUtils.getPrefix("Wither");

	public static Map<Entity, Integer> witherSuffocationPoints = new HashMap<>();
	public static List<Player> playerWhoWonStars = new ArrayList<>();
	//Set to true when this goes live
	public static boolean enabled = false;

	public JWitherCommand(CommandEvent event) {
		super(event);
	}

	@Path("(join|return)")
	void join() {
		if (!enabled)
			error("The wither world is currently being reset. Please wait");
		Warp warp = new WarpService().get("wither", WarpType.NORMAL);
		warp.teleport(player());
		send(PREFIX + "You have entered the wither world. &cBeware!");
	}

	@Path("claim")
	void claim() {
		if (!playerWhoWonStars.contains(player().getPlayer()))
			error("You have not won any stars");
		ItemUtils.giveItem(player(), new ItemStack(Material.NETHER_STAR));
		BNCore.getEcon().withdrawPlayer(player(), 50000);
		playerWhoWonStars.remove(player().getPlayer());
		send(PREFIX + "You have received your nether star. If you do not have room in your inventory, it may be dropped at your feet.");
	}

	@Path("pass")
	void pass() {
		if (!playerWhoWonStars.contains(player().getPlayer()))
			error("You have not won any stars");
		playerWhoWonStars.remove(player().getPlayer());
		send(PREFIX + "You have passed on receiving your nether star.");
	}

	@Path("reset")
	@Permission("group.staff")
	void reset() {
		enabled = false;
		witherSuffocationPoints.clear();
		int wait = 0;
		Tasks.wait(wait, () -> runCommandAsConsole("mv delete wither"));
		Tasks.wait(wait += 5, () -> runCommandAsConsole("mv confirm"));
		Tasks.wait(wait += 2, () -> runCommandAsConsole("cp plugins/../wither_base/ plugins/../wither"));
		Tasks.wait(wait += 5, () -> runCommandAsConsole("rm plugins/../wither/uid.dat"));
		Tasks.wait(wait += 5, () -> runCommandAsConsole("mv import wither nether"));
		Tasks.wait(wait, () -> {
			//enabled = true;
			send("Succesfully reset the wither world");
		});

	}

	@Path("disable")
	@Permission("group.seniorstaff")
	void disable() {
		enabled = false;
		send(PREFIX + "The wither world is now &cdisabled");
	}

	@Path("enable")
	@Permission("group.seniorstaff")
	void enable() {
		enabled = true;
		send(PREFIX + "The wither world is now &aenabled");
	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if (!event.getEntity().getType().equals(EntityType.WITHER)) return;
		if (event.getLocation().getWorld().getName().equalsIgnoreCase("wither")) return;
		if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER)) return;
		if (!enabled) {
			event.setCancelled(true);
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onStarSpawn(ItemSpawnEvent event) {
		if (!event.getEntity().getItemStack().getType().equals(Material.NETHER_STAR)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onWitherSuffocation(EntityDamageEvent event) {
		if (!event.getEntityType().equals(EntityType.WITHER)) return;
		if (!event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) return;
		if (!enabled) return;
		Entity entity = event.getEntity();
		witherSuffocationPoints.put(entity, witherSuffocationPoints.getOrDefault(entity, 0) + 1);
		if (witherSuffocationPoints.get(entity) > 30) {
			entity.getLocation().getNearbyPlayers(50).forEach(player ->
					send(player, PREFIX + "&cThe wither was killed because of suffocation. No star was rewarded."));
			entity.remove();
			witherSuffocationPoints.remove(entity);
		}
		if (witherSuffocationPoints.get(entity) > 15) {
			entity.getLocation().getNearbyPlayers(50).forEach(player ->
					send(player, PREFIX + "&cWarning! It appears you may be trying to suffocate your wither. This is cheating! Free it or face a forfeit."));
		}
	}

	@EventHandler
	public void onDeathOfPlayer(PlayerDeathEvent event) {
		if (!event.getEntity().getWorld().getName().equalsIgnoreCase("wither")) return;
		if (!enabled) return;
		Player player = event.getEntity().getPlayer();
		event.setDeathMessage(null);
		send(player, PREFIX + "You died while in the wither world. You can use &c/wither return &3to go back to the world spawn");
	}

	@EventHandler
	public void onDeathOfWither(EntityDeathEvent event) {
		if (!event.getEntityType().equals(EntityType.WITHER)) return;
		if (!enabled) return;
		Player killer = event.getEntity().getKiller();
		witherSuffocationPoints.remove(event.getEntity());
		processKill(killer);
	}

	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		if (!event.getEntity().getWorld().getName().equalsIgnoreCase("wither")) return;
		if (!enabled) return;

		List<Material> materialsToBlock = new ArrayList<Material>() {{
			add(Material.GLOWSTONE);
			add(Material.GLOWSTONE_DUST);
			add(Material.MAGMA_BLOCK);
			add(Material.MAGMA_CREAM);
			add(Material.BONE_BLOCK);
			add(Material.BOOK);
			add(Material.BOOKSHELF);
			add(Material.END_ROD);
			add(Material.SOUL_SAND);
			add(Material.GRAVEL);
			add(Material.GOLD_BLOCK);
			add(Material.GOLD_INGOT);
			add(Material.GOLD_NUGGET);
			add(Material.QUARTZ);
			addAll(MaterialTag.ALL_TERRACOTTAS.getValues());
			addAll(MaterialTag.CARPETS.getValues());
			addAll(MaterialTag.STAIRS.getValues());
			addAll(MaterialTag.SLABS.getValues());
			addAll(new MaterialTag("NETHER", MatchMode.CONTAINS).getValues());
		}};
		if (materialsToBlock.contains(event.getItem().getItemStack().getType()))
			event.setCancelled(true);
	}

	public void processKill(Player player) {
		if (!enabled) return;
		Chat.broadcastIngame(StringUtils.colorize(PREFIX + "&e" + player.getName() + "&3 has beat the wither in a battle to the death!"));
		Discord.send("**[Wither]** " + player.getName() + " has beat the wither in a battle to the death!");
		if (RandomUtils.chanceOf(20)) {
			if (BNCore.getEcon().getBalance(player) > 50000) {
				send(player, PREFIX + "You won a nether star!");
				send(player, PREFIX + "To receive it, you must pay a fee of $50,000 (400% discount from the market!)");
				send(player, json("  &eClick one  &3||  ")
						.next("&a&lClaim").command("wither claim").hover("&eClick &3to claim your nether star for a fee of &e$50,000").group()
						.next("&3  ||  ").next("&c&lPass").command("wither pass").hover("&eClick &3to pass on the reward").group());
				playerWhoWonStars.add(player);
			} else
				send(player, PREFIX + "You won a nether star, but &eyou did not have enough money for it &3($50,000). Try again when you have the funds!");
		} else
			send(player, PREFIX + "Unfortunately, &eyou did not win anything&3. Thanks for player, better luck next time!");
	}

}
