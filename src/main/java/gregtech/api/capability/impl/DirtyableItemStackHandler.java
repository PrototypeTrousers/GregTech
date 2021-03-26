package gregtech.api.capability.impl;

import gregtech.api.capability.IDirtyableHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashSet;

public class DirtyableItemStackHandler extends ItemStackHandler implements IItemHandlerModifiable, IDirtyableHandler {

    HashSet<MetaTileEntity> entityToSetDirty = new HashSet<>();
    private final boolean isExport;

    public DirtyableItemStackHandler(int slots, MetaTileEntity entityToSetDirty, boolean isExport) {
        super(slots);
        this.entityToSetDirty.add(entityToSetDirty);
        this.isExport = isExport;
    }

    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        dirtyEntity(isExport);
    }

    @Override
    public HashSet<MetaTileEntity> getDirtiables() {
        return this.entityToSetDirty;
    }
}
