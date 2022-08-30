package gg.projecteden.nexus.models.fakenpcs.npcs.traits;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LookCloseTrait extends Trait {
	protected int radius = 10;
}
