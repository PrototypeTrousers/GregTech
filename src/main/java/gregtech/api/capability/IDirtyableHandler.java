package gregtech.api.capability;

import gregtech.api.metatileentity.MetaTileEntity;

import java.util.HashSet;
import java.util.Iterator;

public interface IDirtyableHandler {

    HashSet<MetaTileEntity> entityToSetDirty = new HashSet<>();

    default void dirtyEntity(boolean isExport) {
        Iterator<MetaTileEntity> iterator = entityToSetDirty.iterator();
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

    default void addEntityToSetDirty(MetaTileEntity metaTileEntity) {
        this.entityToSetDirty.add(metaTileEntity);
    }
}
