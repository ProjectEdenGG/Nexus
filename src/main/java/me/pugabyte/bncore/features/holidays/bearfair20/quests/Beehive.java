package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MainIsland;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.send;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.chime;

// NPC BEES: 2730, 2731
public class Beehive implements Listener {
	private String allowedMsg = "The defending swarm seems calmed by the flower's pleasant aroma. The queen beckons you to enter.";
	private String deniedMsg = "The swarming hoards of bees seem disturbed by your presence. Perhaps a peace offering would calm them?";
	private Location enterLoc = new Location(BearFair20.getWorld(), -1084, 135, -1548, 228, 20);
	private Location exitLoc = new Location(BearFair20.getWorld(), -1088, 136, -1548, 40, 0);
	private ItemStack key = MainIsland.rareFlower.clone();
	public static String beehiveRg = "bearfair2020_beehive";
	private String enterRg = beehiveRg + "_enter";
	private String exitRg = beehiveRg + "_exit";
	private String queenRg = beehiveRg + "_queen";

	public Beehive() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onHiveRegionsEnter(RegionEnteredEvent event) {
		String id = event.getRegion().getId();
		Player player = event.getPlayer();

		if (id.equalsIgnoreCase(enterRg)) {
			BearFairService service = new BearFairService();
			BearFairUser user = service.get(player);
			if (user.isQuest_Hive_Access())
				allowed(player);
			else {
				if (player.getInventory().contains(key)) {
					allowed(player);
				} else {
					denied(player);
				}
			}

		} else if (id.equalsIgnoreCase(exitRg)) {
			player.teleport(exitLoc);

		} else if (id.equalsIgnoreCase(queenRg)) {
			String queen = "&3Queen Bee &7> &f";
			String you = "&b&lYOU &7> &f";
			ItemStack honeyBottle = getHoneyBottle(player);
			ItemStack rareFlower = getRareFlower(player);

			if (honeyBottle == null && rareFlower != null) {
				send(queen + "What brings you to my grand halls, traveler?", player);
				Tasks.wait(80, () -> send(you + "I humbly request a blessing of honey from you, your grace, " +
						"so I may make the greatest stroopwafel.", player));
				Tasks.wait(160, () -> send(queen + "I would gladly give you what you seek, but alas, your timing " +
						"is poor for we are currently building our honey reserves for the winter and the new generation. " +
						"If you would bring me a bottle of honey from the surface, I can bless it for you.", player));
				Tasks.wait(240, () -> send(you + "Of course, I will return soon.", player));
			} else if (honeyBottle != null && rareFlower != null) {
				player.getInventory().remove(MainIsland.rareFlower.clone());
				player.getInventory().remove(honeyBottle);

				send(queen + "May this blessing grant you a divine Stroopwafel. Now please, feel free to visit any time. " +
						"You've shown yourself to be a benefactor in the ways of the golden nectar.", player);

				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				user.setQuest_Hive_Access(true);
				service.save(user);

				Tasks.wait(60, () -> {
					Utils.giveItem(player, MainIsland.blessedHoneyBottle.clone());
					chime(player);
				});
			}
		}
	}

	private void allowed(Player player) {
		player.teleport(enterLoc);
		send(allowedMsg, player);
	}

	private void denied(Player player) {
		player.addPotionEffects(Collections.singletonList
				(new PotionEffect(PotionEffectType.BLINDNESS, 40, 250, false, false, false)));
		player.teleport(exitLoc);
		player.playSound(enterLoc, Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 0.5F, 1F);
		Tasks.wait(5, () -> send(deniedMsg, player));
	}

	private ItemStack getHoneyBottle(Player player) {
		for (ItemStack itemStack : player.getInventory()) {
			if (!BearFair20.isBFItem(itemStack)) continue;
			if (!itemStack.getType().equals(Material.HONEY_BOTTLE)) continue;
			return itemStack;
		}
		return null;
	}

	private ItemStack getRareFlower(Player player) {
		for (ItemStack itemStack : player.getInventory()) {
			if (!BearFair20.isBFItem(itemStack)) continue;
			if (!itemStack.getType().equals(Material.BLUE_ORCHID)) continue;
			return itemStack;
		}
		return null;
	}

}
