package cn.com.onlinetool.jt808.bean.down.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static cn.com.onlinetool.jt808.consts.JT808Constant.*;


/**
 * 终端参数设置
 */
@ToString(callSuper = true)
@Data
public class Down0x8103 extends BasePacket implements Serializable {


    private int paramCount; //参数总数1字节
    private List<Down0x8103ParamItem> paramList = new ArrayList<>();//参数列表

    public Down0x8103(String terminalPhone) {
        this.getHeader().setTerminalPhone(terminalPhone);
        this.getHeader().setMsgId(DOWN_PARAM_SET_REQ);
    }

    @Override
    public ByteBuf toByteBufMsg() {
        ByteBuf bb = super.toByteBufMsg();
        bb.writeByte(this.paramCount);
        for (Down0x8103ParamItem item : this.paramList) {
            bb.writeInt(item.getId());
            bb.writeByte(item.getLen());
            bb.writeBytes(item.getContent());
        }
        return bb;
    }

    @Data
    public static class Down0x8103ParamItem implements Serializable{
        private int id;//参数id 4字节
        private int len;//参数长度 1字节
        private byte[] content;//参数内容
        private Integer numContent;//参数内容-数值
        private String strContent;//参数内容-字符串

        public Down0x8103ParamItem(int id, byte[] content) {
            this.id = id;
            this.content = content;
            this.len = this.content.length;
        }

    }

}
