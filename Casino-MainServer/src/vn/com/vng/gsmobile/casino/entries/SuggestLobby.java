package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestLobby {

	public static Byte getSuggestLobby(Long uid, Byte game_type){
		User u = new User(uid);
		Number suggest_lobby = u.getConditionValue(CondType.LobbySuggest, game_type);
		Byte kq = suggest_lobby.byteValue();
		return kq;
	}
	public static boolean updateSuggestLobby(Long uid, Byte game_type, Byte new_suggest_lobby){
		boolean isNewSuggest = false;
		User u = new User(uid);
		Number suggest_lobby = u.getConditionValue(CondType.LobbySuggest, game_type);
		if(new_suggest_lobby != suggest_lobby.byteValue()){
			List<List<?>> conds = new ArrayList<>();
			conds.add(Arrays.asList(String.format(User.PATTERN_COND, game_type.toString(), User.COND_GAME_LOBBY_SUGGEST), new_suggest_lobby, CondUpdateType.Upsert));
			u.setConditionValue(conds);
			isNewSuggest = true;
		}
		return isNewSuggest;
	}
}
