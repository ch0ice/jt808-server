package cn.com.onlinetool.jt808.bean.down.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 查询指定终端参数
 */
@ToString(callSuper = true)
@Data
public class Down0x8106 extends BasePacket implements Serializable {
    private int paramCount;//参数总数1字节
    private int[] paramIds;//参数id列表4*n字节

    public Down0x8106(String terminalPhone){
        this.getHeader().setTerminalPhone(terminalPhone);
        this.getHeader().setMsgId(JT808Constant.DOWN_PARAM_DEST_FIND_REQ);
    }

    @Override
    public ByteBuf toByteBufMsg() {
        ByteBuf bb = super.toByteBufMsg();
        bb.writeByte(this.paramCount);
        for (int paramId : this.paramIds) {
            bb.writeInt(paramId);
        }
        return bb;
    }
}
