/*
 *  Author: Sam6982
 */
package nekto.controller.tile;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public final class TileEntityController extends TileEntityBase<Object[]> {
    public TileEntityController() {
        super(0);
    }

    @Override
    protected List<Object[]> getBlockList() {
        return getBaseList();
    }

    @Override
    protected void onRedstoneChange() {
        if (isPowered()) {
            setActiveBlocks(getBlockList().iterator());
        } else {
            setUnactiveBlocks(getBlockList().iterator());
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        Object[] objects;
        int[] data;
        for (int index = 0; index < getBaseList().size(); index++) {
            objects = getBaseList().get(index);
            if (objects != null) {
                data = new int[objects.length];
                for (int i = 1; i < objects.length; i++) {
                    data[i] = (Integer) objects[i];
                }
                data[0] = GameData.getBlockRegistry().getId((Block) objects[0]);
                par1NBTTagCompound.setIntArray(Integer.toString(index), data);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        int count = par1NBTTagCompound.getInteger("length");
        Object[] objects;
        int[] data;
        for (int index = 0; index < count; index++) {
            data = par1NBTTagCompound.getIntArray(Integer.toString(index));
            if (data.length > 0) {
                objects = new Object[data.length];
                objects[0] = GameData.getBlockRegistry().getObjectById(data[0]);
                for (int i = 1; i < objects.length; i++) {
                    objects[i] = data[i];
                }
                this.getBaseList().add(objects);
            }
        }
    }

    @Override
    public String getInventoryName() {
        return "Controller.inventory";
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return i < getSizeInventory() && itemstack.getItem() instanceof ItemBlock;
    }

    @Override
    public String getName() {
        return "controller";
    }
}
