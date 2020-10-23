package me.pugabyte.bncore.utils;

import me.pugabyte.bncore.BNCore;

import java.util.Arrays;

public enum Env {
	DEV,
	PROD;

	public static boolean applies(Env... envs) {
		return Arrays.asList(envs).contains(BNCore.getEnv());
	}
}
