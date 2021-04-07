package me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer;

import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.nexus.features.minigames.models.perks.PerkType;
import me.pugabyte.nexus.models.perkowner.PerkOwner;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MultiplayerMechanic extends Mechanic {

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		if (event.getMatch().isEnded())
			return;

		Minigamer victim = event.getMinigamer();
		if (victim.isRespawning()) return;

		victim.clearState();
		if (victim.getLives() != 0) {
			victim.died();
			if (victim.getLives() == 0) {
				victim.setAlive(false);
				if (victim.getMatch().getArena().getSpectateLocation() == null)
					victim.quit();
				else
					victim.toSpectate();
			} else {
				victim.respawn();
			}
		} else {
			victim.respawn();
		}

		super.onDeath(event);
	}

	@Override
	public void processJoin(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		if (match.isStarted()) {
			balance(minigamer);
			match.teleportIn(minigamer);
		} else {
			match.getArena().getLobby().join(minigamer);
		}
	}

	@Override
	public void begin(MatchBeginEvent event) {
		super.begin(event);

		Match match = event.getMatch();
		Arena arena = match.getArena();

		if (arena.getTurnTime() > 0)
			nextTurn(match);
	}

	public boolean showTurnTimerInChat() {
		return true;
	}

	public boolean shuffleTurnList() {
		return false;
	}

	abstract public void nextTurn(Match match);

	public int getMultiplier(Match match, Minigamer minigamer) {
		int maxScore = Utils.getMax(match.getMinigamers(), Minigamer::getContributionScore).getValue().intValue();
		if (minigamer.getContributionScore() <= 0)
			return 0;
		return maxScore - minigamer.getContributionScore() + 1;
	}

	public void giveRewards(Match match) {
		if (RandomUtils.randomInt(1, 50) == 1) {
			List<Minigamer> minigamers = new ArrayList<>(match.getMinigamers());
			Collections.shuffle(minigamers);
			// iterates until we find a player who is missing at least 1 collectible
			for (Minigamer minigamer : minigamers) {
				PerkOwner perkOwner = PerkOwner.service.get(minigamer.getPlayer());
				if (LocalDate.now().isBefore(perkOwner.getRandomGiftDate().plusWeeks(1)))
					continue;

				// get a random perk the player doesn't own
				Map<PerkType, Double> weights = new HashMap<>();
				List<PerkType> unownedPerks = Arrays.stream(PerkType.values()).filter(type -> !perkOwner.getPurchasedPerks().containsKey(type)).collect(Collectors.toList());
				if (unownedPerks.isEmpty())
					continue;
				// weights should be inverse of the cost (i.e. cheapest is most common/highest number)
				int maxPrice = (int) Utils.getMax(unownedPerks, PerkType::getPrice).getValue();
				int minPrice = (int) Utils.getMin(unownedPerks, PerkType::getPrice).getValue();
				unownedPerks.forEach(perkType -> weights.put(perkType, (double) (maxPrice-perkType.getPrice()+minPrice)));
				PerkType perkType = RandomUtils.getWeightedRandom(weights);

				if (perkType == null) continue; // failsafe (this shouldn't happen but just in case)

				perkOwner.getPurchasedPerks().put(perkType, false);
				perkOwner.setRandomGiftDate(LocalDate.now());
				PerkOwner.service.save(perkOwner);
				Minigames.broadcast("&e" + minigamer.getNickname() + "&3 randomly won the collectible &e" + perkType.getPerk().getName());
				break;
			}
		}

		match.getMinigamers().forEach(minigamer -> {
			PerkOwner perkOwner = PerkOwner.service.get(minigamer.getPlayer());
			// max of 1 in 2 chance of getting a reward (dependant on score)
			int multiplier = getMultiplier(match, minigamer);
			if (multiplier == 0)
				return;
			if (RandomUtils.randomInt(1, 2 * multiplier) == 1)
				perkOwner.reward(match.getArena());
		});
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		giveRewards(event.getMatch());
	}
}
