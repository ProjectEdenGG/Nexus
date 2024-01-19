package gg.projecteden.nexus.models.mcmmo;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.mcmmo.reset.McMMOResetProvider.ResetSkillType;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "mcmmo_prestige_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class McMMOPrestigeUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<ResetSkillType, Integer> prestiges = new ConcurrentHashMap<>();
	private Map<SkillTokenType, Integer> tokens = new ConcurrentHashMap<>();

	public int getPrestige(ResetSkillType skill) {
		return prestiges.getOrDefault(skill, 0);
	}

	public void prestige(ResetSkillType skill) {
		prestiges.put(skill, getPrestige(skill) + 1);
	}

	public void prestigeAll() {
		for (ResetSkillType skill : ResetSkillType.values())
			prestige(skill);
	}

	public int getTokens(SkillTokenType tokenType) {
		return tokens.getOrDefault(tokenType, 0);
	}

	public void setTokens(SkillTokenType tokenType, int amount) {
		tokens.put(tokenType, amount);
	}

	public void giveTokens(SkillTokenType tokenType, int amount) {
		setTokens(tokenType, getTokens(tokenType) + amount);
	}

	public void takeTokens(SkillTokenType tokenType, int amount) {
		setTokens(tokenType, getTokens(tokenType) - amount);
	}

	public enum SkillTokenType {
		ACROBATICS,
		ALCHEMY,
		ARCHERY,
		AXES,
		EXCAVATION,
		FISHING,
		HERBALISM,
		MINING,
		REPAIR,
		SWORDS,
		TAMING,
		UNARMED,
		WOODCUTTING,
		GRANDMASTER,
		;

		public static boolean isToken(String name) {
			try {
				valueOf(name.toUpperCase());
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
	}

}
