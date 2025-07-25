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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EdenEventGameConfig(
	prefix = "Wakk'a Mole",
	world = "pugmas25",
	playRegion = "pugmas25_whacamole_play"
)
@Environments({Env.PROD, Env.UPDATE})
public class Pugmas25WhacAMole extends EdenEventSinglePlayerGame {
	private static Pugmas25WhacAMole instance;

	private static final String BASE_REGION = "pugmas25_whacamole_";
	private static final String STANDS_REGION = BASE_REGION + "stands";
	private static final String CLEAN_ARROWS_REGION = BASE_REGION + "arrows";

	private final Material targetGood = Material.ZOMBIE_HEAD;
	private final Material targetBad = Material.PLAYER_HEAD;
	private List<ItemStack> targetItems;
	private final long MAX_LIFE_TICKS = TickTime.SECOND.x(3);
	private static final Map<ArmorStand, Long> activeStands = new ConcurrentHashMap<>();
	private int score = 0;

	private final List<Location> spawnLocations = new ArrayList<>();
	private final List<ArmorStand> armorStands = new ArrayList<>();
	private List<ItemStack> kit = new ArrayList<>();
	private Hologram holoTimeLeft;
	private Hologram holoScore;
	private Hologram holoPlayer;

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

		holoTimeLeft = HologramsAPI.byId(getWorld(), "pugmas25_whacamole_time_left");
		holoScore = HologramsAPI.byId(getWorld(), "pugmas25_whacamole_score");
		holoPlayer = HologramsAPI.byId(getWorld(), "pugmas25_whacamole_player");

		kit = List.of(
			new ItemBuilder(Material.BOW).enchant(Enchant.INFINITY).unbreakable().build(),
			new ItemBuilder(Material.ARROW).unbreakable().build());

		ItemBuilder head = new ItemBuilder(targetBad).skullOwner(Dev.WAKKA.getUniqueId());
		targetItems = List.of(
			new ItemBuilder(targetGood).build(),
			head.clone().build(),
			head.clone().build()
		);

		spawnLocations.clear();
		worldedit().getBlocks(worldguard().getProtectedRegion(STANDS_REGION)).forEach(block -> {
			if ((block.getBlockData() instanceof Piston piston)) {
				if (piston.isExtended()) {
					Location location = block.getLocation().clone();
					location.add(0.2, -1.5, 0.5);
					location.setYaw(-90);
					spawnLocations.add(location);
				}
			}
		});
	}

	@Override
	public void end() {
		armorStands.forEach(Entity::remove);

		Location location = getGamer().getLocation();
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).pitch(0.7).location(location).play();
		Tasks.wait(TickTime.TICK.x(6), () ->
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).pitch(0.7).location(location).play());

		super.end();
	}

	@Override
	public void reset() {
		armorStands.forEach(Entity::remove);
		armorStands.clear();
		if (getGamer() != null)
			PlayerUtils.removeItems(getGamer(), kit);
		worldguard().getEntitiesInRegion(CLEAN_ARROWS_REGION).stream()
			.filter(entity -> entity.getType() == EntityType.ARROW)
			.forEach(Entity::remove);
		score = 0;

		super.reset();
	}

	@Override
	protected boolean startChecks(Player player) {
		if (!PlayerUtils.hasRoomFor(player, kit)) {
			send(player, "&cYou don't have enough room for the kit");
			return false;
		}

		return super.startChecks(player);
	}

	@Override
	protected void preStart() {
		score = 0;
		updateHologramPlayer();
		updateHologramScore();
		updateHologramTime();
		give(kit);

		for (Location location : spawnLocations) {

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

			armorStands.add(armorStand);
		}
	}

	@Override
	protected void update() {
		updateHologramTime();
		super.update();

		Map<ArmorStand, Long> _activeStands = new HashMap<>(activeStands);

		// Remove any stands still active after maxStandTicks
		if (!_activeStands.isEmpty()) {
			for (ArmorStand stand : _activeStands.keySet()) {
				long ticks = _activeStands.get(stand);
				long tickDiff = getGameTicks() - ticks;
				if (tickDiff >= MAX_LIFE_TICKS) {
					resetStand(stand);
				}
			}
		}

		if (getGameTicks() % TimeUtils.TickTime.SECOND.x(3) == 0) {
			List<ArmorStand> standChoices = new ArrayList<>(armorStands);
			standChoices.removeAll(_activeStands.keySet());
			if (!standChoices.isEmpty()) {
				int standCount = RandomUtils.randomInt(3, 5);
				for (int i = 0; i < standCount; i++) {
					ArmorStand stand = RandomUtils.randomElement(standChoices);
					stand.getEquipment().setHelmet(new ItemStack(RandomUtils.randomElement(targetItems)), true);

					EntityUtils.forcePacket(stand);
					Location location = getStandBaseLocation(stand).add(0, 1, 0);
					stand.teleport(location);
					new SoundBuilder(Sound.BLOCK_PISTON_EXTEND).volume(0.75).location(location).play();

					activeStands.put(stand, getGameTicks());
				}
			}
		}
	}

	private void updateHologramPlayer() {
		if (holoPlayer == null)
			return;

		holoPlayer.setLine(0, "&e" + Nickname.of(getGamer()));
	}

	private void updateHologramScore() {
		if (holoScore == null)
			return;

		holoScore.setLine(0, "&3Score: &e" + score);
	}

	private void updateHologramTime() {
		if (holoTimeLeft == null)
			return;

		if (getGameTicks() % TickTime.SECOND.get() == 0) {
			int secondsLeft = (int) ((TickTime.MINUTE.get() - getGameTicks()) / TickTime.SECOND.get());
			holoTimeLeft.setLine(0, "&3Time Left: &e" + secondsLeft + "s");
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
		for (ArmorStand stand : activeStands.keySet()) {
			if (stand.getUniqueId().equals(armorStand.getUniqueId()))
				exists = true;
		}

		if (!exists)
			return;

		event.setCancelled(true);
		arrow.remove();

		ItemStack headItem = armorStand.getEquipment().getItem(EquipmentSlot.HEAD);
		if (Nullables.isNotNullOrAir(headItem)) {
			Material type = headItem.getType();
			if (type == targetGood) {
				new SoundBuilder(Sound.ENTITY_ARROW_HIT_PLAYER).location(gamer).volume(0.5).play();
				score++;
			} else if (type == targetBad) {
				new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO).location(gamer).volume(0.5).pitch(0.5).play();
				score -= 2;
			}

			updateHologramScore();
		}

		resetStand(armorStand);
	}

	private void resetStand(ArmorStand stand) {
		activeStands.remove(stand);
		stand.teleport(getStandBaseLocation(stand).subtract(0, 1, 0));
		stand.getEquipment().setHelmet(null);
	}

	private Location getStandBaseLocation(ArmorStand stand) {
		return spawnLocations.get(armorStands.indexOf(stand)).clone();
	}
}
