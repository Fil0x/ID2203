package host;

import host.register.ReadImposeWriteConsultMajorityHost;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;

public class Spawner extends ComponentDefinition {

	public Spawner() {
//		List<TAddress> all = Grid.getAllNodes();
//    	for (int i = 0; i < all.size(); i++) {
//    		create(BroadcastComponentHost.class, new BroadcastComponentHost.Init(all.get(i), all));
//    	}
		
		
//		 try {
//			TAddress address1 = new TAddress(InetAddress.getByName("127.0.0.1"), 20000);
//			TAddress address2 = new TAddress(InetAddress.getByName("127.0.0.1"), 20001);
//			
//			create(Pp2pLinkHost.class, new Pp2pLinkHost.Init(address1, address2));
//			create(Pp2pLinkHost.class, new Pp2pLinkHost.Init(address2, address1));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
         
//		create(NodeHost.class, Init.NONE);
		
		create(ReadImposeWriteConsultMajorityHost.class, Init.NONE);
	}
}
