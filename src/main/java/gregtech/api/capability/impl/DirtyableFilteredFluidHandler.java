package gregtech.api.capability.impl;

import gregtech.api.capability.IDirtyableHandler;
import gregtech.api.metatileentity.MetaTileEntity;

public class DirtyableFilteredFluidHandler extends FilteredFluidHandler implements IDirtyableHandler {

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
}
