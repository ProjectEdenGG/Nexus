package gg.projecteden.nexus.features.crates;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.common.util.concurrent.AtomicDouble;
import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.ArmorStandEditorCommand;
import gg.projecteden.nexus.features.commands.MuteMenuCommand;
import gg.projecteden.nexus.features.listeners.common.TemporaryListener;
import gg.projecteden.nexus.features.particles.effects.CircleEffect;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.crate.CrateConfig;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.jukebox.JukeboxSong;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class CratePinatas implements Listener {

	private static final String NBT_KEY = "CRATE_TYPE";

	private static final ItemBuilder PINATA_ITEM = new ItemBuilder(CustomMaterial.PINATA_LLAMA)
		.name("&3Crate Pinata").lore("&eRight click to activate!");

	private static final CustomMaterial[] PINATAS = { CustomMaterial.PINATA_LLAMA, };

	private static final String[] SONGS = { "Abba - Dancing Queen", "Village People - YMCA", "Toto - Africa", "Smash Mouth - All Star", "Luis Fonsi - Despacito", "Daft Punk - Get Lucky",
		"Train - Hey Soul Sister", "LMFAO - Party Rock Anthem", "OMFG - Hello", "2 Unlimited - Get Ready for This", "Yifo123 - Oriental Disco Bathtub", "A-ha - Take On Me",
		"Will Smith - Gettin' Jiggy Wit It", "Avicii - Wake Me Up", "Katy Perry - Roar", "Taylor Swift - I Knew You Were Trouble",
	};

	public static void give(OfflinePlayer player, CrateType type, int amount) {
		ItemBuilder builder = PINATA_ITEM.clone();
		builder.nbt(nbt -> nbt.setString(NBT_KEY, type.name()));
		builder.amount(amount);

		PlayerUtils.giveItemAndMailExcess(player, builder.build(), WorldGroup.SURVIVAL);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!isNullOrAir(event.getClickedBlock()))
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (isNullOrAir(event.getPlayer().getInventory().getItemInMainHand()))
			return;

		if (new ItemBuilder(event.getPlayer().getInventory().getItemInMainHand()).modelId() != PINATA_ITEM.modelId())
			return;

		if (!new NBTItem(event.getPlayer().getInventory().getItemInMainHand()).hasKey(NBT_KEY))
			return;

		activate(event.getPlayer());
	}

	public void activate(Player player) {
		String nbtType = new NBTItem(player.getInventory().getItemInMainHand()).getString(NBT_KEY);
		CrateType type;
		try {
			type = CrateType.valueOf(nbtType.toUpperCase());
		} catch (Exception ignore) {
			Nexus.debug("Invalid Crate Type on Pinata: " + nbtType);
			return;
		}

		new PinataInstance(player, type);
		player.getInventory().getItemInMainHand().subtract();
	}

	public static class PinataInstance implements TemporaryListener {

		@Getter
		private final Player player;
		private final CrateType type;
		private final ItemStack pinata;

		private ArmorStand stand;
		private PositionSongPlayer songPlayer;
		private int rewardsTask;
		private int rotateTask;
		private int particleTask;
		private int particleTask2;
		private int rollsLeft = 10;
		private List<Item> items = new ArrayList<>();
		private List<CrateConfig.CrateLoot> loots = new ArrayList<>();
		private boolean stopped;

		public PinataInstance(Player player, CrateType type) {
			this.player = player;
			this.type = type;
			Nexus.registerTemporaryListener(this);
			pinata = new ItemBuilder(RandomUtils.randomElement(PINATAS)).build();
			start();
		}

		public void start() {
			Tasks.wait(TimeUtils.TickTime.MINUTE.x(2), () -> {
				if (!stopped)
					stop();
			}); // Hard Stop

			Item item = player.getWorld().spawn(player.getEyeLocation(), Item.class, _item -> {
				_item.setItemStack(pinata);
				_item.setCanMobPickup(false);
				_item.setPickupDelay((short) 32767);
				_item.setVelocity(player.getLocation().getDirection().normalize());
			});

			AtomicInteger taskId = new AtomicInteger();
			taskId.set(
				Tasks.repeat(4, 2, () -> {
					new ParticleBuilder(Particle.REDSTONE)
						.color(RandomUtils.randomInt(0, 255), RandomUtils.randomInt(0, 255), RandomUtils.randomInt(0, 255))
						.location(item.getLocation())
						.extra(1)
						.allPlayers()
						.spawn();

					if (!item.isOnGround())
						return;

					Tasks.cancel(taskId.get());

					Tasks.wait(20, () -> {
						item.remove();
						spawn(item);
					});
				})
			);
		}

		double totalAnimationTime = 150;

		private void spawn(Item item) {
			new ParticleBuilder(Particle.EXPLOSION_HUGE)
				.location(item.getLocation().clone().add(0, .5, 0))
				.count(10)
				.offset(.5, .5, .5)
				.allPlayers()
				.spawn();

			stand = ArmorStandEditorCommand.summon(item.getLocation(), as -> {
				as.setInvisible(true);
				as.getEquipment().setHelmet(pinata);
				as.setInvulnerable(false);
			});
			stand.setVelocity(new Vector(0, 1, 0));

			try {
				songPlayer = new PositionSongPlayer(JukeboxSong.of(RandomUtils.randomElement(SONGS)).getSong());
				songPlayer.setTargetLocation(stand.getLocation());
				songPlayer.setPlaying(true);
				songPlayer.setTick((short) 0);
				songPlayer.setCategory(SoundCategory.RECORDS);

				for (Player player : PlayerUtils.OnlinePlayers.getAll()) {
					if (!MuteMenuUser.hasMuted(player, MuteMenuCommand.MuteMenuProvider.MuteMenuItem.JUKEBOX))
						songPlayer.addPlayer(player);
				}
			} catch (Exception ex) {
				Nexus.log("Error while playing song for pinata");
				ex.printStackTrace();
			}

			Tasks.wait(TimeUtils.TickTime.SECOND.x(2), this::startGivingRewards);

			Tasks.wait(TimeUtils.TickTime.SECOND.x(RandomUtils.randomInt(30, 45)), this::stop);

			AtomicDouble rotation = new AtomicDouble(0);
			AtomicBoolean increasing = new AtomicBoolean(true);
			AtomicBoolean left = new AtomicBoolean();

			rotateTask = Tasks.repeat(1, 1, () -> {
				if (rotation.getAndAdd(1) == this.totalAnimationTime) {
					rotation.set(0);
					increasing.set(!increasing.get());
				}

				Location loc = stand.getLocation().clone().add(0, (increasing.get() ? 1 : -1) * this.toSinWave(rotation.get()), 0);
				loc.setYaw(loc.getYaw() + 5);
				stand.teleport(loc);

//				stand.setHeadPose(stand.getHeadPose().add(0, 0, (left.get() ? 1 : -1) * .02));
//				if (Math.abs(Math.toDegrees(stand.getHeadPose().getZ())) > 10)
//					left.set(!left.get());
			});

			Vector vector = new Vector(0, 1.5, 0);
			particleTask = CircleEffect.builder()
				.location(stand.getLocation().clone().add(0, 3, 0))
				.updateVector(vector)
				.density(100)
				.radius(2)
				.ticks(-1)
				.randomRotation(true)
				.rainbow(true)
				.fast(false)
				.start()
				.getTaskId();

			particleTask2 = CircleEffect.builder()
				.location(stand.getLocation().clone().add(0, 3, 0))
				.updateVector(vector)
				.density(100)
				.radius(2)
				.ticks(-1)
				.randomRotation(true)
				.rainbow(true)
				.fast(false)
				.startDelay(20)
				.start()
				.getTaskId();
		}

		private double toSinWave(double time) {
			return Math.sin(time / ((this.totalAnimationTime) / Math.PI)) * (1 / (2 * (this.totalAnimationTime / Math.PI)));
		}

		private void startGivingRewards() {
			rewardsTask = Tasks.repeat(TimeUtils.TickTime.SECOND.x(1), TimeUtils.TickTime.SECOND.x(2), this::rollReward);
		}

		private void rollReward() {
			if (rollsLeft-- <= 0)
				return;

			CrateConfig.CrateLoot loot = CrateHandler.pickCrateLoot(type, player);
			loots.add(loot);
			ItemStack item = loot.getDisplayItem();

			items.add(stand.getLocation().getWorld().spawn(stand.getLocation().clone().add(0, 3, 0), Item.class, i -> {
				i.setItemStack(item);
				i.setCanMobPickup(false);
				i.setCanPlayerPickup(false);
				i.customName(new JsonBuilder(loot.getDisplayName()).build());
				i.setVelocity(new Vector(RandomUtils.randomDouble(-.25, .25), .25, RandomUtils.randomDouble(-.25, .25)));

				Color color = Color.fromRGB(RandomUtils.randomInt(0, 255), RandomUtils.randomInt(0, 255), RandomUtils.randomInt(0, 255));
				AtomicInteger taskId = new AtomicInteger();
				taskId.set(
					Tasks.repeat(2, 1, () -> {
						if (i.isOnGround())
							Tasks.cancel(taskId.get());

						new ParticleBuilder(Particle.REDSTONE)
							.color(color)
							.location(i.getLocation().clone().add(0, .25, 0))
							.extra(1)
							.allPlayers()
							.spawn();
					})
				);

				Tasks.wait(10, () -> i.setCustomNameVisible(true));
			}));
		}

		private void stop() {
			loots.forEach(loot -> PlayerUtils.giveItemsAndMailExcess(player, loot.getItems(), WorldGroup.SURVIVAL));
			items.forEach(Entity::remove);
			Tasks.cancel(rewardsTask);
			Tasks.cancel(rotateTask);
			Tasks.cancel(particleTask);
			Tasks.cancel(particleTask2);
			if (stand != null)
				stand.remove();
			if (songPlayer != null)
				songPlayer.destroy();
			Nexus.unregisterTemporaryListener(this);
			stopped = true;
		}

		@EventHandler
		public void onPunch(EntityDamageByEntityEvent event) {
			if (!event.getEntity().equals(stand))
				return;

			event.setCancelled(true);

			if (!event.getDamager().equals(player))
				return;

			rollReward();
		}
	}

}
