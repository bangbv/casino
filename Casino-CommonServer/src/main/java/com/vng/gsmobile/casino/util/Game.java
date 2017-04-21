package com.vng.gsmobile.casino.util;

public class Game {
	private Game() {
	}

	public static final byte GameType_TLMN = 1;
	public static final byte GameType_TALA = 2;
	public static final byte GameType_BALA = 3;

	public static final String[] names = { "GameType_TLMN", "GameType_TALA", "GameType_BALA" };

	public static final int[] listGame = {GameType_TLMN,GameType_TALA,GameType_BALA};
	public static String name(int g) {
		return names[g];
	}
}
