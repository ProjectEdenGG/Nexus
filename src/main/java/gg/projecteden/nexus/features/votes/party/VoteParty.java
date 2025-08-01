package gg.projecteden.nexus.features.votes.party;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.voter.VotePartyData;
import gg.projecteden.nexus.models.voter.VotePartyService;
import gg.projecteden.nexus.models.voter.Voter.Vote;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;

public class VoteParty {

	@Getter
	private static final String prefix = StringUtils.getPrefix("VoteParty");

	private static final VotePartyService VOTE_PARTY_SERVICE = new VotePartyService();
	private static final VoterService VOTER_SERVICE = new VoterService();

	public static int setAmount() {
		int amount = VOTER_SERVICE.getVotesAfter(VOTE_PARTY_SERVICE.get0().getStartDate()).size();

		VotePartyData config = VOTE_PARTY_SERVICE.get0();
		config.setCurrentAmount(amount);
		VOTE_PARTY_SERVICE.save(config);

		return amount;
	}

	public static int getAmount() {
		return VOTE_PARTY_SERVICE.get0().getCurrentAmount();
	}

	public static int getCurrentTarget() {
		return VOTE_PARTY_SERVICE.get0().getCurrentTarget();
	}

	public static void process() {
		if (setAmount() >= getCurrentTarget())
			Tasks.sync(VoteParty::complete);
	}

	public static void complete() {
		if (!new CooldownService().check(UUID0, "vote-party-complete", TickTime.DAY)) {
			Nexus.severe("!!! Preventing vote party completion due to cooldown");
			return;
		}

		setCompleted(true);
		doAnimation();
		try {
			giveRewards();
		} catch (Exception ex) {
			Nexus.severe("[VoteParty] Error giving rewards");
			ex.printStackTrace();
		}

		VotePartyData config = VOTE_PARTY_SERVICE.get0();
		config.setCurrentTarget(calculateNewTarget());
		config.setStartDate(LocalDateTime.now());
		VOTE_PARTY_SERVICE.save(config);
		process();
	}

	public static void setCompleted(boolean completed) {
		VOTE_PARTY_SERVICE.edit0(vp -> vp.setCompleted(completed));
		if (completed)
			new VotePartyResetJob().schedule(60);
	}
	
	public static boolean isCompleted() {
		return VOTE_PARTY_SERVICE.get0().isCompleted();
	}

	public static int calculateNewTarget() {
		LocalDateTime expectedCompletionDate = VOTE_PARTY_SERVICE.get0().getStartDate().plusWeeks(1);
		double differenceInDays = ChronoUnit.DAYS.between(expectedCompletionDate, LocalDateTime.now());
		double ratio = ((differenceInDays - 7) / 7) * -1;

		int newTarget = Math.abs((int) (getCurrentTarget() * (ratio)));
		return Math.round(newTarget / 50f) * 50;
	}

	private static void doAnimation() {
		SoundUtils.Jingle.RANKUP.play(PlayerUtils.OnlinePlayers.where(VoteParty::isFeatureEnabled).get());
		new TitleBuilder()
			.players(PlayerUtils.OnlinePlayers.where(VoteParty::isFeatureEnabled).get())
			.fadeIn(5)
			.title("&e&lVote Party")
			.subtitle("&#f0f01cCompleted!")
			.stay(TimeUtils.TickTime.SECOND.x(4))
			.fadeOut(15)
			.send();

		Chat.Broadcast.ingame()
			.excludePlayers(PlayerUtils.OnlinePlayers.where(player -> !isFeatureEnabled(player)).map(OfflinePlayer::getOfflinePlayer))
			.channel(Chat.StaticChannel.GLOBAL)
			.message(VoteParty.getPrefix() + "&3The Vote Party goal has been hit! Rewards will be given to anyone who voted")
			.send();
	}

	private static void giveRewards() {
		List<Vote> votes = VOTER_SERVICE.getVotesAfter(VOTE_PARTY_SERVICE.get0().getStartDate());

		List<Nerd> nerds = votes.stream()
			.map(PlayerOwnedObject::getNerd)
			.distinct()
			.filter(VoteParty::isFeatureEnabled)
			.toList();

		for (Nerd nerd : nerds) {
			try {
				int amount = (int) votes.stream().filter(vote -> vote.getNerd().getUniqueId().equals(nerd.getUniqueId())).count();
				RewardTier tier = RewardTier.of(amount);
				if (tier == null)
					continue;

				tier.giveReward(nerd);
				Nexus.log("[VoteParty] Giving " + StringUtils.camelCase(tier) + " reward to " + nerd.getNickname());
			} catch (Exception ex) {
				Nexus.severe("[VoteParty] Error giving reward to " + nerd.getNickname());
				ex.printStackTrace();
			}
		}
	}

	public static boolean isFeatureEnabled(HasUniqueId player) {
		return true;
	}

}
