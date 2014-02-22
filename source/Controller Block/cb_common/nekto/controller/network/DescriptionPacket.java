package nekto.controller.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import nekto.controller.tile.TileEntityAnimator;

public class DescriptionPacket implements IMessage {
    public int[] data = new int[7];
    public boolean edit;
    public short mode;
    public DescriptionPacket(){}
    public DescriptionPacket(TileEntityAnimator animator){
        this.data[0] = animator.xCoord;
        this.data[1] = animator.yCoord;
        this.data[2] = animator.zCoord;
        this.edit = animator.isEditing();
        this.data[3] = animator.getFrame();
        this.data[4] = animator.getMaxFrame();
        this.data[5] = animator.getCount();
        this.data[6] = animator.getDelay();
        this.mode = (short) animator.getMode().ordinal();
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        for(int i = 0; i<data.length; i++)
            data[i] = buf.readInt();
        edit = buf.readBoolean();
        mode = buf.readShort();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for(int i = 0; i<data.length; i++)
            buf.writeInt(data[i]);
        buf.writeBoolean(edit);
        buf.writeShort(mode);
    }
}
