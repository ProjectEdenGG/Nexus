package gg.projecteden.nexus.features.events.advent;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.particles.VectorUtils;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.coupon.CouponService;
import gg.projecteden.nexus.models.jukebox.JukeboxSong;
import gg.projecteden.nexus.models.jukebox.JukeboxUser;
import gg.projecteden.nexus.models.jukebox.JukeboxUserService;
import gg.projecteden.nexus.models.trophy.TrophyType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Builder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Builder
public class AdventAnimation {
	private final Location location;
	@Builder.Default
	private final double length1 = 0.25;
	@Builder.Default
	private final double height1 = 0.5;
	@Builder.Default
	private final Particle particle1 = Particle.CRIT;
	@Builder.Default
	private final int ticks1 = 40;
	@Builder.Default
	private final double length2 = 0.25;
	@Builder.Default
	private final double height2 = 0.25;
	@Builder.Default
	private final Particle particle2 = Particle.CRIT;
	@Builder.Default
	private final int ticks2 = 40;
	@Builder.Default
	private final int randomMax = 40;
	private final int presentDay;
	private final List<ItemStack> presentContents;
	private final List<Integer> openTwiceDays;
	private final List<ItemStack> givenItems = new ArrayList<>();
	private final Player player;
	private static final JukeboxUserService userService = new JukeboxUserService();
	private static final ItemStack eventTokenCoupon = new CouponService().get0().of("event_tokens").getItem();

	public void open() {
		if (openTwiceDays.contains(presentDay)) {
			openTwice();
			return;
		}

		ItemStack chest = new ItemBuilder(ItemModelType.PUGMAS_PRESENT_ADVENT).build();
		Item item = spawnItem(location, chest, length1, height1, location.getDirection());
		int itemTaskId = particleTask(particle1, item);

		Tasks.wait(ticks1, () -> {
			Tasks.cancel(itemTaskId);
			Location location = removeItem(item);

			explodeContents(location);
		});

	}

	public void openTwice() {
		ItemStack chest = new ItemBuilder(ItemModelType.PUGMAS_PRESENT_ADVENT).build();
		Item chestItem = spawnItem(location, chest, length1, height1, location.getDirection());
		int itemTaskId = particleTask(particle1, chestItem);

		Tasks.wait(ticks1, () -> {
			Tasks.cancel(itemTaskId);
			Location location = removeItem(chestItem);
			new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(location).play();

			for (int i = 0; i < presentContents.size(); i++) {
				Item _item = spawnItem(location, chest, length2, height2, VectorUtils.getRandomDirection());
				int _itemTaskId = particleTask(particle2, _item);

				Tasks.wait(ticks2 + RandomUtils.randomInt(0, randomMax), () -> {
					Tasks.cancel(_itemTaskId);
					Location _location = removeItem(_item);
					explodeContents(_location);
				});
			}

		});
	}

	private void explodeContents(Location location) {
		new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(location).play();

		if (presentContents.isEmpty()) {
			player.sendMessage("&cReport this to an admin. Contents of present #" + presentDay + " is empty.");
			return;
		}

		List<ItemStack> excess = new ArrayList<>();
		int waitMax = 0;
		for (ItemStack itemStack : presentContents) {
			Item _item = spawnItem(location, itemStack, length2, height2, VectorUtils.getRandomDirection());
			int _itemTaskId = particleTask(particle2, _item);

			int _wait = ticks2 + RandomUtils.randomInt(0, randomMax);
			if (_wait > waitMax)
				waitMax = _wait;

			Tasks.wait(_wait, () -> {
				Tasks.cancel(_itemTaskId);

				if (!givenItems.contains(itemStack)) {
					givenItems.add(itemStack);

					boolean giveItem = true;
					ItemBuilder itemBuilder = new ItemBuilder(itemStack);
					String itemName = StringUtils.stripColor(itemStack.getItemMeta().getDisplayName());

					// special types
					TrophyType trophyType = TrophyType.of(itemStack);
					if (trophyType != null) {
						giveItem = false;
						trophyType.give(player);
					}

					if (MaterialTag.ITEMS_MUSIC_DISCS.isTagged(itemStack.getType())) {
						giveItem = false;
						giveSong(player, itemName);
					}
					//

					if (giveItem)
						excess.addAll(PlayerUtils.giveItemsAndGetExcess(player, itemBuilder.build()));
				}

				Location _location = removeItem(_item);
				new SoundBuilder(Sound.ENTITY_CHICKEN_EGG).location(_location).play();
			});
		}

		Tasks.wait(waitMax, () -> {
			PlayerUtils.giveItemsAndMailExcess(player, excess, WorldGroup.SURVIVAL);
			new SoundBuilder(Sound.ENTITY_CHICKEN_EGG).receiver(player).play();
		});
	}

	private void giveSong(Player player, String itemName) {
		JukeboxSong song = JukeboxSong.of(itemName);
		if (song == null) {
			player.sendMessage("&cReport this to an admin. Song " + itemName + " not found");
			return;
		}

		JukeboxUser user = userService.get(player);
		if (!user.owns(song)) {
			user.give(song);
			userService.save(user);
			user.sendMessage(StringUtils.getPrefix("Jukebox") + "&3You now own &e" + song.getName());
		}
	}

	@NotNull
	private Location removeItem(Item item) {
		Location location = item.getLocation();
		item.remove();
		return location;
	}

	private int particleTask(Particle particle1, Item item) {
		return Tasks.repeat(0, TimeUtils.TickTime.TICK, () -> {
			if (!item.isOnGround())
				new ParticleBuilder(particle1).count(1).extra(0).location(item.getLocation()).spawn();
		});
	}

	@NotNull
	private Item spawnItem(Location location, ItemStack itemStack, double length, double height, Vector direction) {
		Item _item = location.getWorld().dropItem(location, itemStack);
		_item.setCanPlayerPickup(false);
		_item.setCanMobPickup(false);
		_item.setVelocity(direction.multiply(length).add(new Vector(0, height, 0)));
		return _item;
	}
}
