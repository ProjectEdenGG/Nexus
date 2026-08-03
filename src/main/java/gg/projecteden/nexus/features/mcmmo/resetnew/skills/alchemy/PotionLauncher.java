package gg.projecteden.nexus.features.mcmmo.resetnew.skills.alchemy;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.Player;
import org.bukkit.entity.SplashPotion;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class PotionLauncher implements Listener {

	private static final ItemStack item = new ItemBuilder(Material.PAPER)
		.name("&ePotion Launcher")
		.model("survival/mcmmoreset/skills/alchemy/potion_launcher")
		.maxStackSize(1)
		.build();

	public static ItemStack get() {
		return item.clone();
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (!event.getAction().isRightClick()) return;
		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		if (!ItemUtils.isModelMatch(get(), event.getItem())) return;
		if (event.getPlayer().hasCooldown(event.getItem())) return;
		if (event.getClickedBlock() != null && event.getClickedBlock().getType().isInteractable()) return;

		ItemStack potion = getPotionItem(player);
		if (Nullables.isNullOrAir(potion)) return;

		Class<? extends ThrownPotion> potionClass = potion.getType() == Material.SPLASH_POTION ? SplashPotion.class : LingeringPotion.class;
		Location spawnLoc = player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().clone().multiply(0.3));

		player.getWorld().spawn(spawnLoc, potionClass,
			thrownPotion -> {
				thrownPotion.setShooter(player);
				thrownPotion.setVelocity(player.getEyeLocation().getDirection().multiply(2));
				thrownPotion.setItem(ItemBuilder.oneOf(potion).build());
				PotionMeta meta = (PotionMeta) potion.getItemMeta();
				thrownPotion.setPotionMeta(meta);
			});
		potion.subtract();
		player.setCooldown(event.getItem(), 20);
		player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1f, 0.8f);
	}

	public ItemStack getPotionItem(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (Nullables.isNullOrAir(item)) continue;
			if (item.getType() != Material.SPLASH_POTION && item.getType() != Material.LINGERING_POTION) continue;

			return item;
		}
		return null;
	}

	static {
		Nexus.registerListener(new PotionLauncher());
	}

}
