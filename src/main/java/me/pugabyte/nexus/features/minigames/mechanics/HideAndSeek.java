package me.pugabyte.nexus.features.minigames.mechanics;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.HideAndSeekMatchData;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.ActionBarUtils.sendActionBar;
import static me.pugabyte.nexus.utils.LocationUtils.blockLocationsEqual;
import static me.pugabyte.nexus.utils.LocationUtils.getCenteredLocation;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.plural;

public class HideAndSeek extends Infection {
	private static final int SOLIDIFY_PLAYER_AT = Time.SECOND.x(5);

	@Override
	public String getName() {
		return "Hide and Seek";
	}

	@Override
	public String getDescription() {
		return "Hide from the hunters!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.GRASS_BLOCK);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onJoin(MatchJoinEvent event) {
		super.onJoin(event);
		Minigamer minigamer = event.getMinigamer();
		Player player = minigamer.getPlayer();
		ItemStack menuItem = new ItemBuilder(Material.NETHER_STAR).name("&3&lSelect your Block").build();
		player.getInventory().setItem(0, menuItem);
	}

	// Select unique concrete blocks
	@EventHandler
	public void setPlayerBlock(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (event.getItem().getType() != Material.NETHER_STAR) return;
		if (!Utils.ActionGroup.CLICK_AIR.applies(event)) return;

		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld())) return;

		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isInLobby(this)) return;

		Match match = minigamer.getMatch();
		if (match.isStarted()) return;

		new HideAndSeekMenu(match).open(player);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		HideAndSeekMatchData matchData = match.getMatchData();
		if (matchData.getMapMaterials().size() == 0) {
			criticalErrorAbort("Arena has no blocks whitelisted!", match);
			return;
		}

		for (Minigamer minigamer : match.getMinigamers()) {
			if (isZombie(minigamer)) {
				continue;
			}

			minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 255, true, false, false));

			MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, matchData.getBlockChoice(minigamer));
			disguise.setEntity(minigamer.getPlayer());
			disguise.startDisguise();
			DisguiseAPI.setActionBarShown(minigamer.getPlayer(), false);
		}

		int taskId = match.getTasks().repeat(0, 1, () -> {
			for (Minigamer minigamer : match.getMinigamers()) {
				if (isZombie(minigamer)) continue;

				Player player = minigamer.getPlayer();
				UUID userId = player.getUniqueId();
				Map<Minigamer, Location> solidPlayers = matchData.getSolidPlayers();
				int immobileTicks = minigamer.getImmobileTicks();
				Material blockChoice = matchData.getBlockChoice(userId);
				String blockName = camelCase(blockChoice);

				// if player just moved, break their disguise
				if (immobileTicks < SOLIDIFY_PLAYER_AT && solidPlayers.containsKey(minigamer)) {
					blockChange(minigamer, solidPlayers.remove(minigamer), Material.AIR);
					if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
						player.removePotionEffect(PotionEffectType.INVISIBILITY);
					matchData.getSolidBlocks().remove(minigamer.getPlayer().getUniqueId()).remove();
				}

				// check how long they've been still
				if (immobileTicks < Time.SECOND.x(2)) {
					sendActionBar(player, "&bYou are currently partially disguised as a " + blockName);
				} else if (immobileTicks < SOLIDIFY_PLAYER_AT) {
					// countdown until solidification
					int seconds = (int) Math.ceil((SOLIDIFY_PLAYER_AT - immobileTicks) / 20d);
					String display = String.format(plural("&dFully disguising in %d second", seconds) + "...", seconds);
					sendActionBar(player, display);
				} else {
					if (!solidPlayers.containsKey(minigamer)) {
						Location location = minigamer.getPlayerLocation();
						if (immobileTicks == SOLIDIFY_PLAYER_AT && MaterialTag.ALL_AIR.isTagged(location.getBlock().getType())) {
							solidPlayers.put(minigamer, location);
							FallingBlock fallingBlock = minigamer.getPlayer().getWorld().spawnFallingBlock(getCenteredLocation(location), blockChoice.createBlockData());
							fallingBlock.setGravity(false);
							fallingBlock.setHurtEntities(false);
							fallingBlock.setDropItem(false);
							matchData.getSolidBlocks().put(minigamer.getPlayer().getUniqueId(), fallingBlock);
							// add invisibility to hide their falling block disguise
							player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1, true, false, false));
							player.sendBlockChange(location, Material.AIR.createBlockData());
						} else
							sendActionBar(player, "&cYou cannot fully disguise inside non-air blocks!");
					} else {
						matchData.getSolidBlocks().get(minigamer.getPlayer().getUniqueId()).setTicksLived(1);
						blockChange(minigamer, solidPlayers.get(minigamer), blockChoice);
						player.sendBlockChange(solidPlayers.get(minigamer), Material.AIR.createBlockData());
						sendActionBar(player, "&aYou are currently fully disguised as a " + blockName);
					}
				}
			}
		});
		match.getTasks().register(taskId);
	}

	protected void blockChange(Minigamer origin, Location location, Material block) {
		origin.getMatch().getMinigamers().forEach(minigamer -> {
			if (!minigamer.equals(origin))
				minigamer.getPlayer().sendBlockChange(location, block.createBlockData());
		});
	}

	public void cleanup(Minigamer minigamer) {
		DisguiseAPI.undisguiseToAll(minigamer.getPlayer());
	}

	public void cleanup(Match match) {
		match.getMinigamers().forEach(this::cleanup);
		((HideAndSeekMatchData) match.getMatchData()).getSolidBlocks().forEach(($, fallingBlock) -> fallingBlock.remove());
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		super.onQuit(event);
		cleanup(event.getMinigamer());
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		cleanup(event.getMatch());
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		super.onDeath(event);
		cleanup(event.getMinigamer());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		// this method is basically checking to see if a hunter has swung at a hider's fake block
		Minigamer minigamer = PlayerManager.get(event.getPlayer());

		if (
				minigamer.isPlaying(this) &&
						isZombie(minigamer) &&
						event.getAction() == Action.LEFT_CLICK_BLOCK &&
						event.getHand() != null &&
						event.getHand().equals(EquipmentSlot.HAND)
		) {
			HideAndSeekMatchData matchData = minigamer.getMatch().getMatchData();
			Location blockLocation;
			if (event.getClickedBlock() != null)
				blockLocation = event.getClickedBlock().getLocation();
			else {
				return;
			}

			for (Map.Entry<Minigamer, Location> entry : matchData.getSolidPlayers().entrySet()) {
				Minigamer target = entry.getKey();
				Location location = entry.getValue();
				if (blockLocationsEqual(blockLocation, location)) {
					EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(minigamer.getPlayer(), target.getPlayer(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1);
					e.callEvent();
					if (e.isCancelled()) return;

					minigamer.getPlayer().attack(target.getPlayer());
					target.setImmobileTicks(0);
					SoundUtils.playSound(minigamer.getPlayer(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS);
					SoundUtils.playSound(target.getPlayer(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS);
					return;
				}
			}
		}
	}

	@Override
	public boolean canUseBlock(Minigamer minigamer, Block block) {
		return false;
	}

	public static class HideAndSeekMenu extends MenuUtils implements InventoryProvider {
		private final Match match;
		public HideAndSeekMenu(Match match) {
			this.match = match;
		}

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
					.provider(this)
					.title(colorize("&3&lSelect your Block"))
					.size(MenuUtils.getRows(match.getArena().getBlockList().size(), 1), 9)
					.build()
					.open(viewer, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);
			HideAndSeekMatchData matchData = match.getMatchData();
			List<Material> materials = matchData.getMapMaterials();
			List<ClickableItem> clickableItems = new ArrayList<>();
			materials.forEach(material -> {
				ItemStack itemStack = new ItemStack(material);
				clickableItems.add(ClickableItem.from(itemStack, e -> {
					matchData.getBlockChoices().put(player.getUniqueId(), material);
					player.closeInventory();
					PlayerUtils.send(player, "&3You have selected "+camelCase(material));
				}));
			});
			addPagination(player, contents, clickableItems);
		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {

		}
	}
}
