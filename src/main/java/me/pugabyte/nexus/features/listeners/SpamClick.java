package me.pugabyte.nexus.features.listeners;

import com.destroystokyo.paper.event.player.PlayerAttackEntityCooldownResetEvent;
import eden.annotations.Disabled;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.models.tip.Tip;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.models.tip.TipService;
import me.pugabyte.nexus.utils.CitizensUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Disabled
public class SpamClick implements Listener {

	private static final Map<UUID, List<LocalDateTime>> spamAttacks = new HashMap<>();

	@EventHandler
	public void onAttackEntityCooldownReset(PlayerAttackEntityCooldownResetEvent event) {
		Player player = event.getPlayer();
		if (PlayerManager.get(player).isPlaying()) return;
		if (CitizensUtils.isNPC(player)) return;

		if (event.getCooledAttackStrength() == 1)
			return;

		UUID uuid = player.getUniqueId();
		LocalDateTime now = LocalDateTime.now();

		List<LocalDateTime> times = spamAttacks.get(uuid);
		if (times == null)
			times = new ArrayList<>();
		times.add(now);
		times.removeIf(localDateTime -> localDateTime.isBefore(now.minusSeconds(5)));
		spamAttacks.put(uuid, times);

		if (times.size() < 5)
			return;

		Tip tip = new TipService().get(player);
		if (tip.show(TipType.SPAM_ATTACK))
			Koda.dm(player, "Slow down! Spam attacking entities causes very little damage. Use the cooldown indicator on your HUD to determine how to space out your attacks and deal maximum damage");
	}

}

