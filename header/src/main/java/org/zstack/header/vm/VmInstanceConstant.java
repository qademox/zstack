package org.zstack.header.vm;

import org.zstack.header.configuration.PythonClass;

@PythonClass
public interface VmInstanceConstant {
    String SERVICE_ID = "vmInstance";
    String ACTION_CATEGORY = "instance";
    @PythonClass
    String USER_VM_TYPE = "UserVm";
    Integer VM_MONITOR_NUMBER = 1;

    // System limit
    int MAXIMUM_CDROM_NUMBER = 3;

    String KVM_HYPERVISOR_TYPE = "KVM";

    enum Params {
        VmInstanceSpec,
        AttachingVolumeInventory,
        AttachedDataVolumeInventories,
        DestPrimaryStorageInventoryForAttachingVolume,
        AttachNicInventory,
        AbnormalLifeCycleStruct,
        DeletionPolicy,
        AttachingIsoInventory,
        DetachingIsoUuid,
        ReleaseNicAfterDetachNic,
        VmNicInventory,
        L3NetworkInventory,
        UsedIPInventory,
        vmInventory,
        VmAllocateNicFlow_ips,
        VmAllocateNicFlow_nics,
        ApplianceVmSyncHaConfig_applianceVm,
        ApplianceVmSyncHaConfig_haUuid,
        ApplianceVmSyncHaConfig_nic,
        ApplianceVmSyncHaConfig_oldUsedIpUuid,
        ApplianceVmSyncHaConfig_oldUsedIp,
        ApplianceVmSyncHaConfig_newUsedIp,
    }

    enum VmOperation {
        NewCreate,
        Start,
        Stop,
        Pause,
        Resume,
        Reboot,
        Destroy,
        Migrate,
        AttachVolume,
        AttachNic,
        DetachNic,
        AttachIso,
        DetachIso,
        Expunge,
        ChangeImage
    }

    String USER_VM_REGEX_PASSWORD = "[\\da-zA-Z-`=\\\\\\[\\];',./~!@#$%^&*()_+|{}:\"<>?]{1,}";

    enum Capability {
        LiveMigration,
        VolumeMigration,
        Reimage
    }

    String EMPTY_CDROM = "empty";
    String NONE_CDROM = "none";
}
