package cn.com.onlinetool.jt808.bean.down.res;

import cn.com.onlinetool.jt808.bean.BasePacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: choice
 * 查询终端参数应答
 */
@Data
public class Down0x0104 extends BasePacket {
    public Down0x0104(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();
    }

    private int serialNum;// 流水号 2字节
    private int paramCount; //参数总数1字节
    private List<Down0x0104ParamItem> paramList = new ArrayList<>();//参数列表


    private void parse() {
        ByteBuf bb = this.msgBody;
        this.setSerialNum(bb.readShort());
        this.setParamCount(bb.readByte());
        Down0x0104ParamItem item = new Down0x0104ParamItem();
        for(int i = 0; i < this.getParamCount(); i++){
            item.setId(bb.readInt());
            item.setLen(bb.readByte());
            item.setContent(readBytes(item.getLen()));
        }
    }


    @Data
    public static class Down0x0104ParamItem {
        private int id;//参数id 4字节
        private int len;//参数长度 1字节
        private byte[] content;//参数内容
        private Integer numContent;//参数内容-数值
        private String strContent;//参数内容-字符串

    }
}
