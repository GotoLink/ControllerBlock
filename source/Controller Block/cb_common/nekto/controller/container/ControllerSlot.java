package nekto.controller.container;

import nekto.controller.tile.TileEntityBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ControllerSlot extends Slot {
    private final TileEntityBase control;

    public ControllerSlot(IInventory par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
        this.control = (TileEntityBase) par1iInventory;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.control.isItemValidForSlot(slotNumber, stack);
    }
}
