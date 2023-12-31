package gg.projecteden.nexus.features.survival.avontyre;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.*;
import lombok.AllArgsConstructor;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class WeeklyWakka extends Feature implements Listener {

	private static final int npcId = 5079;
	private static final Map<Player, WeeklyWakkaData> playerMap = new HashMap<>();
	private static final ItemBuilder trackingDevice = new ItemBuilder(CustomMaterial.DETECTOR).name("Wakka Detector").lore("&eWeekly Wakka Item");


	public static ItemStack getTrackingDevice() {
		return trackingDevice.build();
	}

	public static NPC getNPC() {
		return CitizensUtils.getNPC(npcId);
	}

	private static boolean isHoldingTrackingDevice(Player player) {
		ItemStack tool = ItemUtils.getTool(player);
		if (Nullables.isNullOrAir(tool))
			return false;

		return tool.getType() == trackingDevice.material() && ItemBuilder.ModelId.of(tool) == trackingDevice.modelId();
	}

	@Override
	public void onStart() {
		final int tickIncrement = 2;
		Tasks.repeat(0, TimeUtils.TickTime.TICK.x(tickIncrement), () -> {
			for (Player player : Survival.getPlayersAtSpawn()) {
				if (!isHoldingTrackingDevice(player))
					continue;

				WeeklyWakkaData data = new WeeklyWakkaData();
				if (playerMap.containsKey(player))
					data = playerMap.remove(player);

				for (RadiusTier tier : RadiusTier.values()) {
					if (tier == RadiusTier.CLOSE) {
						if (!isActuallyClose(player))
							tier = RadiusTier.NEAR;
					}

					RadiusTier.AppliesResult result = tier.applies(player, data);
					if (result == RadiusTier.AppliesResult.CONTINUE)
						continue;

					if (result == RadiusTier.AppliesResult.PING_PLAYER) {
						data.ticks = 0;
						tier.ping(player, data);
					}

					break;
				}

				data.ticks += tickIncrement;
				playerMap.put(player, data);
			}
		});
	}

	private boolean isActuallyClose(Player player) {
		boolean onSameFloor = Math.abs(getNPC().getStoredLocation().getY() - player.getLocation().getY()) <= 5;

		return onSameFloor;
	}


	private static class WeeklyWakkaData {
		int ticks = 0;
		int frame = 0;
	}

	@AllArgsConstructor
	private enum RadiusTier {
		FAR(200, TimeUtils.TickTime.SECOND.x(3), 0.5, "&cWakka is too far away...", "&8■■■■", 34, 18),
		SEARCHING(150, TimeUtils.TickTime.SECOND.x(2), 0.7, "Wakka is somewhere...", "&a■&8■■■", 27, 18),
		AROUND(50, TimeUtils.TickTime.SECOND.x(1), 0.9, "Wakka is around...", "&a■■&8■■", 26, 15),
		NEAR(7, TimeUtils.TickTime.TICK.x(10), 1.2, "Wakka is near...", "&a■■■&8■", 23, 14),
		CLOSE(-1, TimeUtils.TickTime.TICK.x(5), 1.6, "Wakka is close!", "&a■■■■", 22, 14),
		;

		final int minRadius;
		final long cooldown;
		final double pitch;
		final String message;
		final String bars;
		final int firstRepeat;
		final int secondRepeat;

		private String getMessageAnimation(WeeklyWakkaData data) {
			String result = "o O o";

			if (data.frame == 0)
				result = "O o o";
			else if (data.frame == 2)
				result = "o o O";

			if (data.frame == 3)
				data.frame = 0;
			else
				data.frame++;

			return result;
		}

		private JsonBuilder getMessage(WeeklyWakkaData data) {
			return new JsonBuilder("租".repeat(this.firstRepeat) + "&e&l" + getMessageAnimation(data)).group()
				.next("ꈆ".repeat(this.secondRepeat) + "&3" + this.message + " &7&l[" + this.bars + "&7&l]").style(FontUtils.FontType.ACTION_BAR_LINE_1.getStyle()).group();
		}

		private void ping(Player player, WeeklyWakkaData data) {
			ActionBarUtils.sendActionBar(player, getMessage(data), TimeUtils.TickTime.SECOND.x(3));

			SoundBuilder pingSound = new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).volume(0.3).pitch(this.pitch);
			if (Vanish.isVanished(player))
				pingSound.receiver(player);
			else
				pingSound.location(player);

			pingSound.play();
		}

		public AppliesResult applies(Player player, WeeklyWakkaData data) {
			if (this == CLOSE) {
				if (data.ticks >= this.cooldown)
					return AppliesResult.PING_PLAYER;
				return AppliesResult.ON_COOLDOWN;
			}

			if (Distance.distance(getNPC().getStoredLocation(), player.getLocation()).gte(this.minRadius)) {
				if (data.ticks >= this.cooldown)
					return AppliesResult.PING_PLAYER;
				return AppliesResult.ON_COOLDOWN;
			}

			return AppliesResult.CONTINUE;
		}

		private enum AppliesResult {
			PING_PLAYER,
			ON_COOLDOWN,
			CONTINUE;
		}
	}
}