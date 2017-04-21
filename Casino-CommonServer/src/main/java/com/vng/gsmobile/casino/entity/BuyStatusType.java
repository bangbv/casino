package com.vng.gsmobile.casino.entity;

public final class BuyStatusType {
	private BuyStatusType() {
	}

	public static final byte Pending = 0;
	public static final byte Receipt = 1;
	public static final byte Finish = 2;
	public static final byte NotEnoughCash = 3;
}