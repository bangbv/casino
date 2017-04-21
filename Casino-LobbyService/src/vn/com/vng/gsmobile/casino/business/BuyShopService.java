package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.entries.Shop;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDBuyItem;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class BuyShopService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDBuyItem rq = (CMDBuyItem) params.get(4);
		CMDBuyItem rs = rq;
		bKq = Shop.buy(rq.uid(), rq.itemId(), rq.transactionId(), sTrid, (Channel)params.get(0));
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

