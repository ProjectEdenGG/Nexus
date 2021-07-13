package me.pugabyte.nexus.features.recipes.functionals.birdhouses;

import eden.annotations.Environments;
import eden.utils.Env;

@Environments(Env.TEST)
public class DepthsBirdhouse extends Birdhouse {

	@Override
	BirdhouseType getBirdhouseType() {
		return BirdhouseType.DEPTHS;
	}

}
