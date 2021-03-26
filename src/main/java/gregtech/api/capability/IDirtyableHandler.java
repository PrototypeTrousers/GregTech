package gregtech.api.capability;

import gregtech.api.metatileentity.MetaTileEntity;

import java.util.HashSet;
import java.util.Iterator;

public interface IDirtyableHandler {

    default void dirtyEntity(boolean isExport) {
        Iterator<MetaTileEntity> iterator = getDirtiables().iterator();
        while (iterator.hasNext()) {
            MetaTileEntity mte = iterator.next();
            if (mte != null && mte.isValid()) {
                if (isExport) mte.setOutputsDirty(true);
                else mte.setInputsDirty(true);
            } else {
                iterator.remove();
            }
        }
    }

    HashSet<MetaTileEntity> getDirtiables();

    default void addEntityToSetDirty(MetaTileEntity metaTileEntity) {
        getDirtiables().add(metaTileEntity);
    }
}
