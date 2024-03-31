package gg.projecteden.nexus.features.votes.party;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.voter.VotePartyService;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.TitleBuilder;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class VoteParty {

	@Getter
	private static final String prefix = StringUtils.getPrefix("VoteParty");

	private static final VotePartyService VOTE_PARTY_SERVICE = new VotePartyService();
	private static final VoterService VOTER_SERVICE = new VoterService();

	public static int getAmount() {
		return VOTE_PARTY_SERVICE.get0().getCurrentAmount();
	}

	public static int getCurrentTarget() {
		return VOTE_PARTY_SERVICE.get0().getCurrentTarget();
	}

	public static void process() {
		VOTE_PARTY_SERVICE.edit0(user -> user.setCurrentAmount(VOTER_SERVICE.getVotesAfter(VOTE_PARTY_SERVICE.get0().getStartDate()).size()));

		if (getAmount() >= getCurrentTarget())
			complete();
	}

	public static void complete() {
		setCompleted(true);
		doAnimation();
		giveRewards();

		VOTE_PARTY_SERVICE.get0().setCurrentTarget(calculateNewTarget());
		VOTE_PARTY_SERVICE.get0().setStartDate(LocalDateTime.now());
		VOTE_PARTY_SERVICE.save(VOTE_PARTY_SERVICE.get0());
		process();
	}

	public static void setCompleted(boolean completed) {
		VOTE_PARTY_SERVICE.edit0(vp -> vp.setCompleted(completed));
		if (completed) {
			new VotePartyResetJob().schedule(60);
		}
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
		List<Voter.Vote> votes = VOTER_SERVICE.getVotesAfter(VOTE_PARTY_SERVICE.get0().getStartDate());

		List<Nerd> nerds = votes.stream()
			.map(PlayerOwnedObject::getNerd)
			.distinct()
			.filter(VoteParty::isFeatureEnabled)
			.toList();

		nerds.forEach(nerd -> {
			Nexus.log("Giving reward to " + nerd.getNickname());
			int amount = (int) votes.stream().filter(vote -> vote.getNerd().getUniqueId().equals(nerd.getUniqueId())).count();
			RewardTier tier = RewardTier.of(amount);
			if (tier == null) return;
//			tier.giveReward(nerd);
			Nexus.log("Tier: " + tier.name());
		});
	}

	// TODO - return true
	public static boolean isFeatureEnabled(HasUniqueId player) {
		return PlayerUtils.Dev.of(player) != null && Arrays.asList(PlayerUtils.Dev.BLAST, PlayerUtils.Dev.WAKKA, PlayerUtils.Dev.GRIFFIN, PlayerUtils.Dev.ARBY).contains(PlayerUtils.Dev.of(player));
	}

}
