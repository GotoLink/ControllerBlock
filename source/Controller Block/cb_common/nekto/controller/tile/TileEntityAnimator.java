package nekto.controller.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.registry.GameData;
import nekto.controller.animator.Mode;
import nekto.controller.item.ItemRemote;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

public class TileEntityAnimator extends TileEntityBase<List<Object[]>> {
	private int frame = 0, delay = 0, count = 0, max = -1;
	private Mode currMode = Mode.ORDER;
	private boolean removed;

	public TileEntityAnimator() {
		super(1);
	}

	@Override
	protected List<Object[]> getBlockList() {
		return getBaseList().get(frame);
	}

	@Override
	protected String getListName() {
		return "frame " + (frame + 1);
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("frame", this.frame);
		par1NBTTagCompound.setInteger("delay", this.delay);
		par1NBTTagCompound.setInteger("max", this.max);
		par1NBTTagCompound.setInteger("count", this.count);
		par1NBTTagCompound.setShort("mode", (short) this.getMode().ordinal());
		par1NBTTagCompound.setBoolean("removed", this.removed);
		NBTTagList tags = new NBTTagList();
		for (int index = 0; index < getBaseList().size(); index++) {
			NBTTagCompound tag = new NBTTagCompound();
            Object[] objects;
            int[] data;
			for (int block = 0; block < getBaseList().get(index).size(); block++) {
                objects = getBaseList().get(index).get(block);
                data = new int[objects.length];
                System.arraycopy(objects, 1, data, 1, objects.length-1);
                data[0] = GameData.blockRegistry.getId((Block) objects[0]);
				tag.setIntArray(Integer.toString(block), data);
			}
			tags.appendTag(tag);
		}
		par1NBTTagCompound.setTag("frames", tags);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		int length = par1NBTTagCompound.getInteger("length");
		this.frame = par1NBTTagCompound.getInteger("frame");
		this.delay = par1NBTTagCompound.getInteger("delay");
		this.max = par1NBTTagCompound.getInteger("max");
		this.count = par1NBTTagCompound.getInteger("count");
		this.setMode(Mode.values()[par1NBTTagCompound.getShort("mode")]);
		this.removed = par1NBTTagCompound.getBoolean("removed");
		for (int i = 0; i < length; i++) {
			NBTTagCompound tag = par1NBTTagCompound.getTagList("frames", 10).getCompoundTagAt(i);
			List<Object[]> list = new ArrayList<Object[]>();
			@SuppressWarnings("unchecked")
			Iterator<NBTTagIntArray> itr = tag.func_150296_c().iterator();
            int[] data;
            Object[] objects;
			while (itr.hasNext()){
                data = itr.next().func_150302_c();
                objects = new Object[data.length];
                objects[0] = GameData.blockRegistry.get(data[0]);
                System.arraycopy(data, 1, objects, 1, data.length-1);
				list.add(objects);
            }
			this.getBaseList().add(list);
		}
	}

    @Override
    public String getInventoryName() {
        return "Animator.inventory";
    }

    @Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return (i == 0 && itemstack.getItem() instanceof ItemRemote);
	}

	public void setDelay(int i) {
		this.delay += i;
	}

	/**
	 * @return the number of ticks (minus 2) between scheduled updates in
	 *         {@link nekto.controller.block.BlockAnimator} ie, between frames
	 */
	public int getDelay() {
		return this.delay;
	}

	public void resetDelay() {
		this.delay = 0;
	}

	public void setFrame(int i) {
		while (getBaseList().size() <= i)
			getBaseList().add(new ArrayList<Object[]>());
		this.frame = i;
	}

	public int getFrame() {
		return this.frame;
	}

	public void setMode(Mode par1Mode) {
		this.currMode = par1Mode;
	}

	public Mode getMode() {
		return this.currMode;
	}

	public boolean isWaiting() {
		return (this.max >= 0 && this.count >= this.max);
	}

	public boolean hasRemoved() {
		return this.removed;
	}

	public void setRemoved(boolean rem) {
		this.removed = rem;
	}

	public void setMaxFrame(int number) {
		this.max = number;
	}

	public int getMaxFrame() {
		return this.max;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int ct) {
		this.count = ct;
	}

	@Override
	public String getName() {
		return "animator";
	}
}
