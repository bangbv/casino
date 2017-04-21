package vn.com.vng.gsmobile.casino.entries;

public enum CMD {
	SESSION_ERROR((byte)0,(byte)1,(byte)1),
	MY_INFO((byte)1,(byte)0,(byte)1),
	USER_INFO((byte)1,(byte)1,(byte)1),
	USER_INFO_DETAIL((byte)1,(byte)2,(byte)1),
	GAMESERVER_INFO((byte)1,(byte)3,(byte)1),
	USER_LIST((byte)1,(byte)4,(byte)1),
	RANK_LIST((byte)1,(byte)5,(byte)1),	
	ROOM_INFO((byte)1,(byte)6,(byte)1),
	OPTION_GET((byte)1,(byte)7,(byte)1),
	OPTION_UPDATE((byte)1,(byte)8,(byte)1),
	EVENT_LIST((byte)1,(byte)9,(byte)1),
	GET_INFO_FROM_SOCIAL((byte)1,(byte)10,(byte)1),
	LIST_SIMPLE_PROFILE((byte)1,(byte)11,(byte)1),
	GAME_NOTICE((byte)1,(byte)12,(byte)1),	
	SERVER_TIME((byte)1,(byte)13,(byte)1),	
	USER_CONDITION((byte)1,(byte)14,(byte)1),	
	JOIN_LOBBY((byte)2,(byte)1,(byte)1),
	SWITCH_LOBBY((byte)2,(byte)2,(byte)1),
	CREATE_ROOM((byte)2,(byte)3,(byte)1),
	JOIN_ROOM((byte)2,(byte)4,(byte)1),
	QUICK_JOIN_ROOM((byte)2,(byte)5,(byte)1),
	PUSH_UPDATEROOM((byte)2,(byte)6,(byte)1),
	PUSH_LOBBYROOM((byte)2,(byte)7,(byte)1),
	QUIT_ROOM((byte)2,(byte)8,(byte)1),
	QUIT_LOBBY((byte)2,(byte)9,(byte)1),
	INVITE_ROOM((byte)2,(byte)10,(byte)1),
	START_GAME((byte)2,(byte)11,(byte)1),
	GET_LOBBY_USER((byte)2,(byte)14,(byte)1),
	ROOM_WAIT((byte)2,(byte)15,(byte)1),
	CHAT_ROOM((byte)3,(byte)1,(byte)1),
	CHAT_LOBBY((byte)3,(byte)2,(byte)1),
	SHOP_LIST((byte)4,(byte)1,(byte)1),
	BUY_SHOP((byte)4,(byte)2,(byte)1),
	BUY_ITEM((byte)4,(byte)3,(byte)1),
	GIFT_LIST((byte)5,(byte)1,(byte)1),
	GIFT_RECEIVE((byte)5,(byte)2,(byte)1),
	GIFT_CODE_RECEIVE((byte)5,(byte)3,(byte)1),
	GIFTSYS_LIST((byte)5,(byte)4,(byte)1),
	GET_ACHIEVEMENT((byte)6,(byte)1,(byte)1),
	RECEIVE_ACHIEVEMENT((byte)6,(byte)2,(byte)1),
	MAIL_LIST((byte)7,(byte)1,(byte)1),
	READ_MAIL((byte)7,(byte)2,(byte)1),
	SEND_MAIL((byte)7,(byte)3,(byte)1),
	DELETE_MAIL((byte)7,(byte)4,(byte)1),
	CAO_BATTLE_INFO((byte)11,(byte)1,(byte)1),
	CAO_SHOW_CARDS((byte)11,(byte)2,(byte)1),
	TLMN_BATTLE_INFO((byte)12,(byte)1,(byte)1),
	TLMN_SHOW_CARDS((byte)12,(byte)2,(byte)1),
	TLMN_SKIP_TURN((byte)12,(byte)3,(byte)1),
	TLMN_UPDATE_BATTLE_INFO((byte)12,(byte)4,(byte)1),
	//TLMN_SHOW_CARDS_TINY((byte)12,(byte)5,(byte)1),
	TLMN_UPDATE_BATTLE_INFO_TINY((byte)12,(byte)5,(byte)1),
	MB_BATTLE_INFO((byte)13,(byte)1,(byte)1),
	MB_SHOW_CARDS((byte)13,(byte)2,(byte)1),
	MB_PARTNER_SHOW((byte)13,(byte)3,(byte)1),
	MB_FINISH_SHOW((byte)13,(byte)4,(byte)1),
	LOTTERY_LIST((byte)20,(byte)1,(byte)1),
	LOTTERY_RESULT((byte)20,(byte)2,(byte)1);
	
	public byte cmd;
	public byte subcmd;
	public byte version;
	private CMD(byte c1, byte c2, byte c3){
		this.cmd = c1;
		this.subcmd = c2;
		this.version = c3;
	}
	@Override
	public String toString(){
		return this.cmd+"_"+this.subcmd+"_"+this.version;
	}
	public static CMD getCMD(byte c1, byte c2, byte c3){
		for(CMD c : CMD.values())
			if(c.cmd == c1 && c.subcmd==c2 && c.version==c3)
				return c;
		return null;
	}
}
