package gregtech.api.capability.impl;

import gregtech.api.capability.IDirtyableHandler;
import gregtech.api.metatileentity.MetaTileEntity;

import java.util.HashSet;

public class DirtyableFilteredFluidHandler extends FilteredFluidHandler implements IDirtyableHandler {

    HashSet<MetaTileEntity> entityToSetDirty = new HashSet<>();
    private final boolean isExport;

    public DirtyableFilteredFluidHandler(int capacity, MetaTileEntity entityToSetDirty, boolean isExport) {
        super(capacity);
        this.entityToSetDirty.add(entityToSetDirty);
        this.isExport = isExport;
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        dirtyEntity(isExport);
    }

    @Override
    public HashSet<MetaTileEntity> getDirtiables() {
        return this.entityToSetDirty;
    }
}
