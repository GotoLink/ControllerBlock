package nekto.controller.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.registry.GameData;
import nekto.controller.animator.Mode;
import nekto.controller.item.ItemRemote;
import nekto.controller.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class TileEntityAnimator extends TileEntityBase<List<Object[]>> {
	private int frame = 0, delay = 0, count = 0, max = -1;
	private Mode currMode = Mode.ORDER;
	private boolean removed;
    private int timing = -1;

	public TileEntityAnimator() {
		super(1);
	}

    @Override
    public void tick(){
        if(timing<0)
            return;
        if(timing<2+getDelay()){
            timing++;
            return;
        }
        resetTime();
        if (!(isWaiting() && isPowered()))
            if (isPowered() || getFrame() != 0) {
                if (getFrame() < getBaseList().size())
                    previousFrame();
                nextFrame();
                setCount(getCount() + 1);
            }
        if (!isPowered() && getFrame() == 0) {
            setCount(0);
            timing=-1;
        }
    }

    private void resetTime(){
        timing = 0;
    }

    private void nextFrame() {
        switch (getMode()) {
            case LOOP:
                if (getFrame() + 1 >= getBaseList().size())
                    setFrame(0);
                else
                    setFrame(getFrame() + 1);
                break;
            case RANDOM:
                setFrame(worldObj.rand.nextInt(getBaseList().size()));
                break;
            case ORDER:
                if (getFrame() + 1 >= getBaseList().size()) {
                    setFrame(getFrame() - 1);
                    setMode(Mode.REVERSE);
                } else
                    setFrame(getFrame() + 1);
                break;
            case REVERSE:
                if (getFrame() == 0) {
                    setFrame(1);
                    setMode(Mode.ORDER);
                } else
                    setFrame(getFrame() - 1);
                break;
        }
        Iterator<Object[]> itr = getBaseList().get(getFrame()).listIterator();//build next frame
        setUnactiveBlocks(itr);
    }

    private void previousFrame() {
        Iterator<Object[]> oldItr = getBaseList().get(getFrame()).listIterator();//erase previous frame
        setActiveBlocks(oldItr);
    }

    @Override
    public void onRedstoneChange() {
        if (isPowered())//Powered but previously not powered
        {
            if (!hasRemoved()) {
                for (int frame = 0; frame < getBaseList().size(); frame++) {
                    if (getFrame() != frame) {
                        Iterator<Object[]> itr = getBaseList().get(frame).listIterator();
                        setActiveBlocks(itr);
                    }
                }
                setRemoved(true);
            }
            timing = 0;
        } else//Not powered but previously powered
        {
            if (getMode() == Mode.ORDER || getMode() == Mode.LOOP)
                setMode(Mode.REVERSE);
            else if (getMode() != Mode.REVERSE) {
                for (int frame = 0; frame < getBaseList().size(); frame++) {
                    Iterator<Object[]> itr = getBaseList().get(frame).listIterator();
                    setUnactiveBlocks(itr);//Make all the blocks reappear
                }
                setRemoved(false);
                setFrame(0);
            }
        }
        resetTime();
        PacketHandler.sendDescription(this, this.worldObj);
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
                if(objects!=null){
                    data = new int[objects.length];
                    for(int i = 1; i < objects.length; i++){
                        data[i] = (Integer) objects[i];
                    }
                    data[0] = GameData.getBlockRegistry().getId((Block) objects[0]);
                    tag.setIntArray(Integer.toString(block), data);
                }
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
        this.timing = isPowered() && this.timing < 0 ? 0 : -1;
		for (int i = 0; i < length; i++) {
			NBTTagCompound tag = par1NBTTagCompound.getTagList("frames", Constants.NBT.TAG_COMPOUND).getCompoundTagAt(i);
			List<Object[]> list = new ArrayList<Object[]>();
			@SuppressWarnings("unchecked")
			Iterator<String> itr = tag.func_150296_c().iterator();
            int[] data;
            Object[] objects;
			while (itr.hasNext()){
                data = tag.getIntArray(itr.next());
                if(data!=null){
                    objects = new Object[data.length];
                    objects[0] = GameData.getBlockRegistry().getObjectById(data[0]);
                    for(int j = 1; j < objects.length; j++){
                        objects[j] = data[j];
                    }
                    list.add(objects);
                }
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
	 * @return the number of ticks (minus 2) between updates in #tick() ie, between frames
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
