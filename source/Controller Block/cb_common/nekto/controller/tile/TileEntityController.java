/*
 *  Author: Sam6982
 */
package nekto.controller.tile;

import java.util.List;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityController extends TileEntityBase<Object[]> {
	public TileEntityController() {
		super(2);
	}

	@Override
	protected List<Object[]> getBlockList() {
		return getBaseList();
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
        Object[] objects;
        int[] data;
		for (int index = 0; index < getBaseList().size(); index++) {
            objects = getBaseList().get(index);
            data = new int[objects.length];
            System.arraycopy(objects, 1, data, 1, objects.length-1);
            data[0] = GameData.blockRegistry.getId((Block) objects[0]);
			par1NBTTagCompound.setIntArray(Integer.toString(index), data);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		int count = par1NBTTagCompound.getInteger("length");
        Object[] objects;
        int[] data;
		for (int i = 0; i < count; i++) {
            data = par1NBTTagCompound.getIntArray(Integer.toString(i));
            objects = new Object[data.length];
            objects[0] = GameData.blockRegistry.get(data[0]);
            System.arraycopy(data, 1, objects, 1, data.length-1);
			this.getBaseList().add(objects);
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
