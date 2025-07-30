package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.EdenEventGameConfig;
import gg.projecteden.nexus.features.events.EdenEventSinglePlayerGame;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EdenEventGameConfig(
	prefix = "Whac-a-Wakka",
	world = "pugmas25",
	playRegion = "pugmas25_whacamole_play"
)
@Environments({Env.PROD, Env.UPDATE})
public class Pugmas25WhacAMole extends EdenEventSinglePlayerGame {
	private static Pugmas25WhacAMole instance;

	private static final String BASE_REGION = "pugmas25_whacamole_";
	private static final String STANDS_REGION = BASE_REGION + "stands";
	private static final String CLEAN_ARROWS_REGION = BASE_REGION + "arrows";

	private static final List<ItemStack> KIT = List.of(
		new ItemBuilder(Material.BOW).enchant(Enchant.INFINITY).unbreakable().build(),
		new ItemBuilder(Material.ARROW).unbreakable().build());

	private static final String TARGET_GOOD_KEY = "wakka";
	private static final ItemStack TARGET_GOOD_ITEM = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(Dev.WAKKA.getUniqueId()).name(TARGET_GOOD_KEY).build();
	private static final ItemStack TARGET_BAD_ITEM = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(Dev.GRIFFIN.getUniqueId()).name("griffin").build();
	private static final List<ItemStack> TARGET_ITEM_POOL = List.of(TARGET_GOOD_ITEM.clone(), TARGET_BAD_ITEM.clone(), TARGET_BAD_ITEM.clone());

	private static final long ROUND_TICKS = TickTime.SECOND.x(3);

	private final List<Location> spawnLocations = new ArrayList<>();
	private final List<ArmorStand> armorStands = new ArrayList<>();
	private final List<ArmorStand> activeStands = new ArrayList<>();

	private Hologram holoTimeLeft;
	private Hologram holoScore;
	private Hologram holoPlayer;
	private int score = 0;

	public Pugmas25WhacAMole() {
		instance = this;
	}

	public static Pugmas25WhacAMole get() {
		return instance;
	}

	@Override
	public long getMaxGameTicks() {
		return TickTime.MINUTE.x(1);
	}

	@Override
	public void init() {
		super.init();

		instance.holoTimeLeft = HologramsAPI.byId(getWorld(), "pugmas25_whacamole_time_left");
		instance.holoScore = HologramsAPI.byId(getWorld(), "pugmas25_whacamole_score");
		instance.holoPlayer = HologramsAPI.byId(getWorld(), "pugmas25_whacamole_player");

		instance.spawnLocations.clear();
		worldedit().getBlocks(worldguard().getProtectedRegion(STANDS_REGION)).forEach(block -> {
			if ((block.getBlockData() instanceof Piston piston)) {
				if (piston.isExtended()) {
					Location location = block.getLocation().clone();
					location.add(0.2, -1.5, 0.5);
					location.setYaw(-90);
					instance.spawnLocations.add(location);
				}
			}
		});
	}

	@Override
	public void end() {
		instance.armorStands.forEach(Entity::remove);

		Location location = getGamer().getLocation();
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).pitch(0.7).location(location).play();
		Tasks.wait(TickTime.TICK.x(6), () ->
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).pitch(0.7).location(location).play());

		super.end();
	}

	@Override
	public void reset() {
		instance.armorStands.forEach(Entity::remove);
		instance.armorStands.clear();
		if (getGamer() != null)
			PlayerUtils.removeItems(getGamer(), KIT);
		worldguard().getEntitiesInRegion(CLEAN_ARROWS_REGION).stream()
			.filter(entity -> entity.getType() == EntityType.ARROW)
			.forEach(Entity::remove);
		instance.score = 0;

		super.reset();
	}

	@Override
	protected boolean startChecks(Player player) {
		if (!PlayerUtils.hasRoomFor(player, KIT)) {
			send(player, "&cYou don't have enough room for the kit");
			return false;
		}

		return super.startChecks(player);
	}

	@Override
	protected void preStart() {
		instance.score = 0;
		instance.updateHologramPlayer();
		instance.updateHologramScore();
		instance.updateHologramTime();
		instance.give(KIT);

		for (Location location : instance.spawnLocations) {

			ArmorStand armorStand = getGamer().getWorld().spawn(location, ArmorStand.class, stand -> {
				stand.setRightArmPose(EulerAngle.ZERO);
				stand.setLeftArmPose(EulerAngle.ZERO);
				stand.setHeadPose(EulerAngle.ZERO);
				stand.setVisible(false);
				stand.setInvulnerable(true);
				stand.setGravity(false);
				stand.setBasePlate(false);
				stand.setDisabledSlots(EquipmentSlot.values());
			});

			instance.armorStands.add(armorStand);
		}
	}

	@Override
	protected void update() {
		updateHologramTime();
		super.update();

		long gameTicks = getGameTicks();
		boolean preNextRound = (gameTicks + 8) % ROUND_TICKS == 0;
		if (preNextRound) {
			for (ArmorStand stand : new ArrayList<>(instance.activeStands)) {
				new SoundBuilder(Sound.BLOCK_PISTON_CONTRACT).volume(1.5).pitch(2).location(stand.getLocation()).play();
				resetStand(stand);
			}
		}

		boolean nextRound = gameTicks % ROUND_TICKS == 0;
		if (!nextRound)
			return;

		incrementRound();

		List<ArmorStand> standChoices = new ArrayList<>(instance.armorStands);
		if (standChoices.isEmpty())
			return;

		int standCount = getStandCountFromRound(standChoices);

		boolean hasMinGoodTarget = false;
		boolean hasMinBadTarget = false;
		for (int i = 0; i < standCount; i++) {
			ArmorStand stand = RandomUtils.randomElement(standChoices);
			ItemStack targetHead;
			if (!hasMinGoodTarget) {
				targetHead = TARGET_GOOD_ITEM.clone();
				hasMinGoodTarget = true;
			} else if (!hasMinBadTarget) {
				targetHead = TARGET_BAD_ITEM.clone();
				hasMinBadTarget = true;
			} else
				targetHead = RandomUtils.randomElement(TARGET_ITEM_POOL).clone();

			stand.getEquipment().setHelmet(targetHead, true);

			EntityUtils.forcePacket(stand);
			stand.teleport(getStandBaseLocation(stand).add(0, 1, 0));
			new SoundBuilder(Sound.BLOCK_PISTON_EXTEND).volume(1.5).pitch(2).location(stand.getLocation()).play();

			instance.activeStands.add(stand);
		}
	}

	private int getStandCountFromRound(List<ArmorStand> standChoices) {
		int currentRound = getCurrentRound();

		if (currentRound > 18)
			return standChoices.size();

		if (currentRound > 12)
			return RandomUtils.randomInt(7, 12);

		if (currentRound > 6)
			return RandomUtils.randomInt(5, 8); // max 7 inactive stands

		return RandomUtils.randomInt(3, 5); // max 10 inactive stands
	}

	private void updateHologramPlayer() {
		if (instance.holoPlayer == null)
			return;

		instance.holoPlayer.setLine(0, "&3[&e" + Nickname.of(getGamer()) + "&3]");
	}

	private void updateHologramScore() {
		if (instance.holoScore == null)
			return;

		instance.holoScore.setLine(0, "&3Score: &e" + instance.score);
	}

	private void updateHologramTime() {
		if (instance.holoTimeLeft == null)
			return;

		if (getGameTicks() % TickTime.SECOND.get() == 0) {
			int secondsLeft = (int) ((TickTime.MINUTE.get() - getGameTicks()) / TickTime.SECOND.get());
			instance.holoTimeLeft.setLine(0, "&3Time Left: &e" + secondsLeft + "s");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow))
			return;

		if (!(arrow.getShooter() instanceof Player player))
			return;

		if (!Pugmas25.get().shouldHandle(player))
			return;

		if (!instance.isPlaying())
			return;

		Player gamer = instance.getGamer();
		if (gamer == null || !gamer.getUniqueId().equals(player.getUniqueId()))
			return;

		if (!(event.getHitEntity() instanceof ArmorStand armorStand))
			return;

		boolean exists = false;
		for (ArmorStand stand : instance.activeStands) {
			if (stand.getUniqueId().equals(armorStand.getUniqueId()))
				exists = true;
		}

		if (!exists)
			return;

		event.setCancelled(true);
		arrow.remove();

		ItemStack headItem = armorStand.getEquipment().getItem(EquipmentSlot.HEAD);
		if (Nullables.isNotNullOrAir(headItem) && headItem.getType() == Material.PLAYER_HEAD) {
			String key = StringUtils.stripColor(new ItemBuilder(headItem).name());
			if (key.equalsIgnoreCase(TARGET_GOOD_KEY)) {
				new SoundBuilder(Sound.ENTITY_ARROW_HIT_PLAYER).location(gamer).volume(0.5).play();
				instance.score++;
			} else {
				new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO).location(gamer).volume(0.5).pitch(0.5).play();
				instance.score -= 2;
			}

			instance.updateHologramScore();
		}

		instance.resetStand(armorStand);
	}

	private void resetStand(ArmorStand stand) {
		instance.activeStands.remove(stand);
		stand.teleport(getStandBaseLocation(stand).subtract(0, 1, 0));
		stand.getEquipment().setHelmet(new ItemStack(Material.AIR));
	}

	private Location getStandBaseLocation(ArmorStand stand) {
		return instance.spawnLocations.get(instance.armorStands.indexOf(stand)).clone();
	}
}
