package gg.projecteden.nexus.features.resourcepack.models;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class CustomItemCooldown {

	private static final CooldownService COOLDOWN_SERVICE = new CooldownService();

	private static final CustomMaterial FIRST = CustomMaterial.ITEM_COOLDOWN_MIN;
	private static final CustomMaterial LAST = CustomMaterial.ITEM_COOLDOWN_MAX;
	private static final int TOTAL_FRAMES = LAST.getModelId() - FIRST.getModelId();

	private int slot;
	private long tickTime;
	private String cooldownType;
	private Runnable onComplete;

	private int taskId;
	private ItemStack item;

	public CustomItemCooldown(int slot, String cooldownType, long tickTime) {
		this.slot = slot;
		this.tickTime = tickTime;
		this.cooldownType = cooldownType;

		this.item = new ItemBuilder(FIRST).name(TimeUtils.Timespan.ofSeconds(tickTime / 20L).format()).build();
	}

	public CustomItemCooldown onComplete(Runnable onComplete) {
		this.onComplete = onComplete;
		return this;
	}

	public int start(Player player) {
		COOLDOWN_SERVICE.check(player, cooldownType, tickTime);

		this.taskId = Tasks.repeat(0, 5, () -> {
			LocalDateTime cooldownTime = COOLDOWN_SERVICE.get(player).get(cooldownType);
			if (cooldownTime == null || cooldownTime.isBefore(LocalDateTime.now())) {
				try {
					onComplete.run();
					Tasks.cancel(taskId);
				}
				catch (Exception e) { throw new RuntimeException(e); }
			}
			else {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(COOLDOWN_SERVICE.getDiff(player, cooldownType));

				float secondsLeft = Duration.between(cooldownTime, LocalDateTime.now()).toSeconds() * 20;
				meta.setCustomModelData(interpolate(secondsLeft / tickTime));

				this.item.setItemMeta(meta);
				player.getInventory().setItem(slot, item);
			}
		});
		return taskId;
	}

	public int interpolate(float value) {
		return (int) Math.ceil((1 - value) * FIRST.getModelId() + value * LAST.getModelId()) + (LAST.getModelId() - FIRST.getModelId());
	}




}
