package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;

import redis.clients.jedis.Tuple;
import vn.com.vng.gsmobile.casino.entries.Rank;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDRankList;
import vn.com.vng.gsmobile.casino.flatbuffers.RankType;
import vn.com.vng.gsmobile.casino.flatbuffers.RankUser;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfoDetail;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

/**
 * @author bangbv
 */
public class RankListService implements IService {

	@SuppressWarnings("unchecked")
	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.OK;
		CMDRankList rq = (CMDRankList) params.get(4);
		int type = rq.type();
		Long ruid = rq.uid();
		String key = Rank.PREFIX + ":" + RankType.name(type);
		// Check data from cache
		List<CMDRankList> lrl = (List<CMDRankList>) LocalCache.getRankList(key);
		if (lrl == null) {
			lrl = new ArrayList<>();
			int offset = 0;
			int A = 25;
			Set<Tuple> lt = (Set<Tuple>) Lib.getRedisGame(false).getRedisConnection().zrevrangeWithScores(key, 0, 99);
			Double score = Lib.getRedisGame(false).getRedisConnection().zscore(key,ruid.toString());
			Long rank = Lib.getRedisGame(false).getRedisConnection().zrevrank(key,ruid.toString());
			if (lt != null) {
				Tuple[] alt = lt.toArray(new Tuple[lt.size()]);
				for (int p = 0; p < 4; p++) {
					FlatBufferBuilder builder = new FlatBufferBuilder(0);
					List<Integer> l = new ArrayList<>();
					int block = offset + A;
					int o = offset;
					for (int i = offset; i < alt.length; i++) {
						if (o >= block) {
							break;
						}
						Long uid = Long.parseLong(alt[i].getElement());						
						// get user info
						JsonObject j = LocalCache.get(User.USER_TABLENAME + uid);
						JsonObject j2 = LocalCache.get(User.USERESOURCE_TABLENAME + uid);
						if ((j != null) && (j2 != null)) {
							Long vip_expired = j.getLong(User.VIP_EXPIRE);
							int userOffset = UserInfo.createUserInfo(builder, 
									uid,
									builder.createString(j.getString(User.NAME)),
									builder.createString(j.getString(User.AVATAR)), 
									j.getInt(User.STATUS),
									j.getInt(User.ACCTYPE), 
									j.getInt(User.VIP), 
									j2.getLong(User.COIN), 
									j2.getInt(User.LEVEL), 
									UserInfoDetail.createUserInfoDetail(builder, 
											j2.getInt(User.EXP), 
											j2.getInt(User.WIN),
											j2.getInt(User.LOSE),
											j2.getInt(User.STAR),
											j2.getInt(User.CASH),
											User.getVipDaysRemaining(vip_expired)
										));
							int rui;
							if(type == RankType.RankGlobalLevel){
								rui = RankUser.createRankUser(builder, userOffset, j2.getInt(User.LEVEL) + 1);
							}else{
								rui = RankUser.createRankUser(builder, userOffset, (long) alt[i].getScore());
							}
							l.add(rui);
						}
						o++;
					}
					int[] lu = ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()]));
					int listOffset = CMDRankList.createListVector(builder, lu);
					int rli;
					if(rank != null && score != null){
						rli = CMDRankList.createCMDRankList(builder, ruid, rank.intValue(), score.longValue(), type, offset, listOffset);
					}else{
						rli = CMDRankList.createCMDRankList(builder, ruid, rq.myRank(), rq.myScore(), type, offset, listOffset);
					}
					builder.finish(rli);
					CMDRankList rl = CMDRankList.getRootAsCMDRankList(builder.dataBuffer());
					lrl.add(rl);
					offset = offset + A;
				}
				// save to cache
				LocalCache.put(key, lrl);
			} else {
				bKq = ErrorCode.UNKNOWN;
			}
		}
		if (bKq == ErrorCode.OK) {
			for (CMDRankList cmdRankList : lrl) {
				List<?> channels = Arrays.asList(params.get(0));
				List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, cmdRankList);
				if(cmdRankList.listLength() > 0){
					Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, channels,
							outparams);
				}
			}			
		}
		return Arrays.asList(bKq, null, null);
	}
}