package me.pugabyte.bncore.features.stattrack;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.stattrack.models.Stat;
import me.pugabyte.bncore.features.stattrack.models.StatIncreaseEvent;
import me.pugabyte.bncore.features.stattrack.models.StatItem;
import me.pugabyte.bncore.features.stattrack.utils.StatTrackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;

// Potion effect effect absorption, only apply absorption if they are wearing armor
// damage and absorption for bow

public class StatTrackListener implements Listener {

	public StatTrackListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onStatIncrease(StatIncreaseEvent event){
		Player player = event.getPlayer();
		PlayerInventory inv = player.getInventory();
		ItemStack item = event.getItem();
		Stat stat = event.getStat();
		int value = event.getValue();

		int slot = StatTrackUtils.findItem(inv, item);

		StatItem statItem = new StatItem(item);
		statItem.parse();
		Map<Stat, Integer> stats = statItem.getStats();
		stats.put(stat, stats.getOrDefault(stat, 0) + value);
		statItem.write();

		inv.setItem(slot, statItem.getItem());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getBlock().getDrops().isEmpty()) {

			Player player = event.getPlayer();
			for (Stat stat : Stat.values()) {
				if (stat.getMaterials() == null || stat.getTool() == null) continue;
				List<Material> materials = stat.getMaterials();
				List<Material> tools = stat.getTool().getTools();

				if (materials.contains(event.getBlock().getType())) {
					ItemStack item = player.getInventory().getItemInMainHand();
					Material tool = item.getType();
					if (tools.contains(tool)) {
						StatIncreaseEvent statIncreaseEvent = new StatIncreaseEvent(player, item, stat, 1);
						Bukkit.getPluginManager().callEvent(statIncreaseEvent);
					}
				}
			}
		}
	}

	// Add this to the items lore?
//	Map<Player, Integer> hits = new HashMap<>();

	// Getting crit % -- (totalCritDmg / 100)* totalNotCritDmg

	private boolean isCritical(Player p) {
		return (p.getVelocity().getY() + 0.0784000015258789) < 0;
	}

	private boolean isPlayer(Entity e){
		return e instanceof Player;
	}

	// ItemMendEvent: DIAMOND_SWORD (2456 + 15)
	@EventHandler
	public void onItemMend(PlayerItemMendEvent event){
		String output = "ItemMendEvent: ";
		ItemStack item = event.getItem();
		int repairAmt = event.getRepairAmount();
		int oldDur = item.getType().getMaxDurability() - item.getDurability();
		output += item.getType() + "(" + oldDur + " + " + repairAmt +")";
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(output);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event){
		if(isPlayer(event.getEntity())) {
			String output = "EntityDamageEvent";
			EntityDamageEvent.DamageCause cause = event.getCause();
			Player victimP = (Player)  event.getEntity();

			double dmg = event.getDamage();
			double dmgF = event.getFinalDamage();
			double dmgFRound = (Math.round(dmgF * 100.0) / 100.0);
			double absorbed = Math.round((dmg - dmgF) * 100.0) / 100.0;

			// Fall Damage -- applies to boots
			if (cause.equals(EntityDamageEvent.DamageCause.FALL)) {
				Bukkit.broadcastMessage(" ");
				output += " > Fall -- Damage: " + dmgFRound + " | Absorbed: " + absorbed;
				Bukkit.broadcastMessage(victimP.getName());
				Bukkit.broadcastMessage(output);

			// Cactus Damage -- applies to all armor (absorption/4 and put 1/4 on each piece?)
			} else if (cause.equals(EntityDamageEvent.DamageCause.CONTACT)) {
				Bukkit.broadcastMessage(" ");
				output += " > Contact -- Damage: " + dmgFRound + " | Absorbed: " + absorbed;
				Bukkit.broadcastMessage(victimP.getName());
				Bukkit.broadcastMessage(output);

			}
			//		else {
			//				Bukkit.broadcastMessage(output + " > ??? -- " + cause);
			//		}
		}
	}

	// THIS DOES NOT WORK PROPERLY
	// adding chain of hits would need another variable to store the current chain in, in the lore maybe?
	// tie this into pvp event for getting successful hits in a row with a sword.
//	@EventHandler
//	public void onPlayerInteract(PlayerInteractEvent event){
//		if(event.hasItem()){
//			if(event.getItem().getType().toString().toLowerCase().contains("sword")){
//				if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
//					Bukkit.broadcastMessage(event.getAction().toString());
//					Bukkit.broadcastMessage("End of chain");
//				}
//			}
//		}
//	}

	// if entity will die, count kill -- absorption and damage
	@EventHandler
	public void onEntityDmgByArrow(EntityDamageByEntityEvent event){
		Entity victim = event.getEntity();
		Entity damager = event.getDamager();
		if(damager instanceof Arrow){						// If an arrow,
			double dmg = event.getDamage();
			double dmgF = event.getFinalDamage();
			double dmgFRound = (Math.round(dmgF * 100.0) / 100.0);
			double absorbed = Math.round((dmg - dmgF) * 100.0) / 100.0;
			String output = "ArrowEvent";
			Arrow arrow = (Arrow) damager;
			if(arrow.getShooter() instanceof Player){		// 		from a player,
				Player damagerP = (Player) arrow.getShooter();
				if(isPlayer(victim)){						// 			hits a player.
					Player victimP = (Player) victim;
					if(!victimP.equals(damagerP)) {			//				if player hits them self, do nothing.
						if (victimP.isBlocking()) {			//					if player is blocking with shield
							output += " > Blocked";
						}else{
							if (victimP.getHealth() < dmgF) {
								Bukkit.broadcastMessage(" ");
								output += " > PlayerDeathEvent > +1 To: " + damagerP.getName();
							}else{
								output += " -- Damage: " + dmgFRound + " | " + "Absorbed: " + absorbed;
							}
						}
						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(output);
						Bukkit.broadcastMessage(victimP.getName() + " shot by " + damagerP.getName());
					}
				}else if(victim instanceof Creature){		//			hits a creature. (monsters and animals)
					Creature creature = (Creature) victim;
					if (creature.getHealth() < dmgF) {
						Bukkit.broadcastMessage(" ");
						output += " > MobDeathEvent > +1 To: " + damagerP.getName();
					}
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(output);
					Bukkit.broadcastMessage(damagerP.getName() + " shot " + victim.getType());
				}
			}else if(arrow.getShooter() instanceof Monster){//		from a monster,
				Monster damagerM = (Monster) arrow.getShooter();
				if(isPlayer(victim)){						//			hits a player.
					Player victimP = (Player) victim;
					if (victimP.isBlocking()) {
						output += " > Blocked";
					}else{

						output += " -- Damage: " + dmgFRound + " | " + "Absorbed: " + absorbed;
					}
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(output);
					Bukkit.broadcastMessage(damagerM.getType() + " shot " + victimP.getName());
				}
			}
		}
	}

	@EventHandler
	public void onEntityDmgByEntity(EntityDamageByEntityEvent event){
		Entity victim = event.getEntity();
		Entity damager = event.getDamager();
		EntityDamageEvent.DamageCause cause = event.getCause();

		double dmg = event.getDamage();
		double dmgF = event.getFinalDamage();
		double dmgFRound = (Math.round(dmgF * 100.0) / 100.0);
		double absorbed = (Math.round((dmg - dmgF) * 100.0) / 100.0);

		// Do successful hits chain here
		// ...

		// When a player hurts a player | Add Player Kills
		if(isPlayer(victim) && isPlayer(damager)){
			Bukkit.broadcastMessage(" ");
			String output = "PvPEvent";
			Player victimP = (Player) victim;
			Player damagerP = (Player) damager;

			//This includes using shields as well
			if(cause.equals(EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK)){
				// if the player's hit is critical
				if(isCritical(damagerP)){
					output += " > Crit -- Dmg: " + dmgFRound + " | Absorbed: " + absorbed;
				}else {
					output += " > Attack -- Dmg: " + dmgFRound + " | Absorbed: " + absorbed;
				}
			}
			if(cause.equals(EntityDamageByEntityEvent.DamageCause.THORNS)){
				output += " > Thorns -- Dmg: " + dmgFRound + " | Absorbed: " + absorbed;
			}

			Bukkit.broadcastMessage(output);
			Bukkit.broadcastMessage(damagerP.getName() + " -> " + victimP.getName());

		// When a player hurts a mob
		}else if(isPlayer(damager)) {
			Bukkit.broadcastMessage(" ");
			String output = "PvEEvent";
			Player damagerP = (Player) damager;
			if(cause.equals(EntityDamageByEntityEvent.DamageCause.THORNS)){
				output += " > Thorns -- Dmg: " + dmgFRound + " | Absorbed: " + absorbed;
			}else {
				// if the player's hit is critical
				if (isCritical(damagerP)) {
					output += " > Crit -- Dmg: " + dmgFRound;
				} else {
					output += " > Dmg: " + dmgFRound;
				}
			}
			Bukkit.broadcastMessage(output);
			Bukkit.broadcastMessage(damagerP.getName() + " -> " + event.getEntity().getType());

		// When a mob hurts a player (non projectile)
		}else if(isPlayer(victim) && (damager instanceof  Monster)){
			Bukkit.broadcastMessage(" ");
			String output = "EvPEvent";
			Player victimP = ((Player) victim);
			output += " -- Dmg: " + dmgFRound + " | Absorbed: " + absorbed;
			Bukkit.broadcastMessage(output);
			Bukkit.broadcastMessage(damager.getType() + " -> " + victimP.getName());
		}else{
			if((cause.equals(EntityDamageByEntityEvent.DamageCause.PROJECTILE)) || (cause.equals(EntityDamageByEntityEvent.DamageCause.CONTACT)) || (cause.equals(EntityDamageByEntityEvent.DamageCause.FALL))){

			}else{
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage("EntityDamageByEntityEvent > ??? -- " + cause);
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDamageByEntityEvent event){
		Entity victim = event.getEntity();
		Entity killer = event.getDamager();
		String output;
		double dmgF = event.getFinalDamage();

		// If victim is Player
		if(victim instanceof Player) {
			// If killer is Player
			if(killer instanceof Player) {
				Player victimP = (Player) victim;
				Player killerP = (Player) killer;

				// if player will die
				if (victimP.getHealth() < dmgF) {
					Bukkit.broadcastMessage(" ");
					output = "PlayerDeathEvent > +1 To: " + killerP.getName();
					Bukkit.broadcastMessage(output);
				}
			}

			// if victim is monster
		}else if(victim instanceof Monster){
			// if killer is player
			if(killer instanceof Player){
				Player killerP = (Player) killer;
				Monster victimM = (Monster) victim;

				// if monster will die
				if(victimM.getHealth() < dmgF){
					Bukkit.broadcastMessage(" ");
					output = "MonsterDeathEvent > +1 To: " + killerP.getName();
					Bukkit.broadcastMessage(output);
				}

			}
		}
	}

	@EventHandler
	public void onFishCatch(PlayerFishEvent event){
		Bukkit.broadcastMessage(" ");
		PlayerFishEvent.State state = event.getState();
		String output = "PlayerFishEvent";
		String type = " Wasn't A Fish";
		if (state.equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			output += " > CaughtFish >";
			ItemStack fish = ((Item) (event.getCaught())).getItemStack();
			switch (fish.getType()) {
				case COD:
					type = " Raw Fish";
					break;
				case SALMON:
					type = " Raw Salmon";
					break;
				case PUFFERFISH:
					type = " Raw Clownfish";
					break;
			}
			Bukkit.broadcastMessage(output + type);
		}
	}

	@EventHandler
	public void onShearEntity(PlayerShearEntityEvent event){
		Bukkit.broadcastMessage(" ");
		String output = "PlayerShearEvent";
		Entity eventEntity = event.getEntity();
		switch(eventEntity.getType().toString().toLowerCase()){
			case "sheep":
				output += " > Sheep";
				break;
			case "mushroom_cow":
				output += " > MushroomCow";
				break;
			case "snowman":
				output += " > Snowman";
				break;
		}
		Bukkit.broadcastMessage(output);
	}

	// breaking blocks of the incorrect tool still counts, so like mining leaves doesnt make any sense.
	@EventHandler
	public void onBlockBreak2(BlockBreakEvent event) {
		if (!event.getBlock().getDrops().isEmpty()){
			String output = "BlockBreakEvent";
			ItemStack inHand = event.getPlayer().getEquipment().getItemInMainHand();
			String StrinHand = inHand.toString().toLowerCase();
			String strBlock = event.getBlock().getType().toString().toLowerCase();
			if (StrinHand.contains("shear")) {
				output += " > Sheared";
				switch (strBlock) {
					case "leaves":
						output += " > Leaves";
						break;
					case "vine":
						output += " > Vines";
						break;
					case "long_grass":
						output += " > Long Grass";
						break;
					default:
						output += " > ??? -- " + strBlock;
						break;
				}
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage(output);
			} else if (StrinHand.contains("pickaxe")) {
				output += " > Mined";
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage(output);
			} else if (StrinHand.contains("axe")) {
				output += " > Cut";
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage(output);
			} else if (StrinHand.contains("spade")) {
				output += " > Dug";
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage(output);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		String output = "BlockPlaceEvent";
		ItemStack inHand = event.getItemInHand();
		String StrInHand = inHand.getType().toString().toLowerCase();
		if(StrInHand.contains("hoe")){
			output += " > Tilled";
			Bukkit.broadcastMessage(" ");
			Bukkit.broadcastMessage(output);
		}
		if(StrInHand.contains("spade")){
			output += " > CreatePath";
			Bukkit.broadcastMessage(" ");
			Bukkit.broadcastMessage(output);
		}
	}

}
