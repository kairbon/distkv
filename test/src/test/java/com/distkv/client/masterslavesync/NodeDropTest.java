package com.distkv.client.masterslavesync;

import com.distkv.common.NodeInfo;
import com.distkv.common.NodeStatus;
import com.distkv.common.id.NodeId;
import com.distkv.server.metaserver.client.DmetaClient;
import com.distkv.server.metaserver.server.bean.GetGlobalViewResponse;
import com.distkv.server.metaserver.server.bean.HeartbeatResponse;
import com.distkv.supplier.DmetaTestUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

@Test(singleThreaded = true)
public class NodeDropTest {

  @Test
  public void testNodeDrop() {
    DmetaTestUtil.startAllDmetaProcess();
    try {
      DmetaClient client = new DmetaClient(DmetaTestUtil.defaultMetaAddress);
      TimeUnit.MILLISECONDS.sleep(500);
      NodeInfo nodeInfo = NodeInfo.newBuilder()
          .setAddress("test")
          .setNodeId(NodeId.nil())
          .setIsMaster(false)
          .build();
      HeartbeatResponse heartbeatResponse = client.heartbeat(nodeInfo);
      TimeUnit.SECONDS.sleep(1);
      GetGlobalViewResponse globalViewResponse0 = client.getGlobalView();
      Assert.assertEquals(globalViewResponse0
          .getGlobalView().get("1").getMap().get("test").getStatus(),
          NodeStatus.RUNNING);
      TimeUnit.SECONDS.sleep(3);
      GetGlobalViewResponse globalViewResponse1 = client.getGlobalView();
      Assert.assertEquals(globalViewResponse1
              .getGlobalView().get("1").getMap().get("test").getStatus(),
          NodeStatus.DEAD);
    } catch (Exception e) {
      Assert.fail();
    } finally {
      DmetaTestUtil.stopAllDmetaProcess();
    }
  }
}