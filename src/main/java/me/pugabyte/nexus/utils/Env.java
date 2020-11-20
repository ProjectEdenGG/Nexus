package me.pugabyte.nexus.utils;

import me.pugabyte.nexus.Nexus;

import java.util.Arrays;

public enum Env {
	DEV,
	PROD;

	public static boolean applies(Env... envs) {
		return Arrays.asList(envs).contains(Nexus.getEnv());
	}
}
