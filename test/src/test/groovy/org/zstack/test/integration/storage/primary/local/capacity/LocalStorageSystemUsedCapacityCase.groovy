package org.zstack.test.integration.storage.primary.local.capacity

import org.springframework.http.HttpEntity
import org.zstack.core.db.Q
import org.zstack.sdk.ClusterInventory
import org.zstack.sdk.HostInventory
import org.zstack.sdk.PrimaryStorageInventory
import org.zstack.storage.primary.local.LocalStorageHostRefVO
import org.zstack.storage.primary.local.LocalStorageHostRefVO_
import org.zstack.storage.primary.local.LocalStorageKvmBackend
import org.zstack.test.integration.storage.StorageTest
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.SubCase

/**
 * Created by lining on 2020/12/28.
 */

class LocalStorageSystemUsedCapacityCase extends SubCase {
    EnvSpec env

    @Override
    void clean() {
        env.delete()
    }

    @Override
    void setup() {
        useSpring(StorageTest.springSpec)
    }

    @Override
    void environment() {
        env = env {

            zone {
                name = "zone"
                description = "test"

                cluster {
                    name = "cluster"
                    hypervisorType = "KVM"

                    attachPrimaryStorage("local")
                }

                localPrimaryStorage {
                    name = "local"
                    url = "/local_ps"
                }

            }
        }
    }

    @Override
    void test() {
        env.create {
            testAddHost()
        }
    }

    void testAddHost() {
        ClusterInventory cluster = env.inventoryByName("cluster")
        PrimaryStorageInventory ps = env.inventoryByName("local")

        LocalStorageKvmBackend.InitRsp initRsp = null;
        env.afterSimulator(LocalStorageKvmBackend.INIT_PATH) { LocalStorageKvmBackend.InitRsp rsp, HttpEntity<String> e ->
            rsp.totalCapacity = 10
            rsp.availableCapacity = 5
            rsp.localStorageUsedCapacity = 2
            initRsp = rsp
            return rsp
        }

        HostInventory host = addKVMHost {
            name = "host1"
            managementIp = "127.0.0.3"
            username = "root"
            password = "password"
            clusterUuid = cluster.uuid
        }

        assert initRsp != null

        LocalStorageHostRefVO hostRefVO = Q.New(LocalStorageHostRefVO.class)
                .eq(LocalStorageHostRefVO_.hostUuid, host.uuid).find()
        assert hostRefVO.totalCapacity == initRsp.totalCapacity
        assert hostRefVO.totalPhysicalCapacity == initRsp.totalCapacity
        assert hostRefVO.systemUsedCapacity == initRsp.totalCapacity - initRsp.availableCapacity - initRsp.localStorageUsedCapacity
        assert hostRefVO.availablePhysicalCapacity == initRsp.availableCapacity
        assert hostRefVO.availableCapacity == hostRefVO.availableCapacity

        ps = queryPrimaryStorage {
            conditions=["uuid=${ps.uuid}".toString()]
        }[0]
        assert ps.systemUsedCapacity == hostRefVO.systemUsedCapacity

        initRsp = null;
        env.afterSimulator(LocalStorageKvmBackend.INIT_PATH) { LocalStorageKvmBackend.InitRsp rsp, HttpEntity<String> e ->
            rsp.totalCapacity = 10
            rsp.availableCapacity = 5
            rsp.localStorageUsedCapacity = 3
            initRsp = rsp
            return rsp
        }
        reconnectHost {
            uuid = host.uuid
        }

        assert initRsp != null
        hostRefVO = Q.New(LocalStorageHostRefVO.class)
                .eq(LocalStorageHostRefVO_.hostUuid, host.uuid).find()
        assert hostRefVO.totalCapacity == initRsp.totalCapacity
        assert hostRefVO.totalPhysicalCapacity == initRsp.totalCapacity
        assert hostRefVO.systemUsedCapacity == initRsp.totalCapacity - initRsp.availableCapacity - initRsp.localStorageUsedCapacity
        assert hostRefVO.availablePhysicalCapacity == initRsp.availableCapacity
        assert hostRefVO.availableCapacity == hostRefVO.availableCapacity

        ps = queryPrimaryStorage {
            conditions=["uuid=${ps.uuid}".toString()]
        }[0]
        assert ps.systemUsedCapacity == hostRefVO.systemUsedCapacity

    }
}
