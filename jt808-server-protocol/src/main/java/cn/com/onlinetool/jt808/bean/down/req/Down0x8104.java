package cn.com.onlinetool.jt808.bean.down.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 查询终端参数
 */
@ToString(callSuper = true)
@Data
public class Down0x8104 extends BasePacket implements Serializable {


    public Down0x8104(String terminalPhone){
        this.getHeader().setTerminalPhone(terminalPhone);
        this.getHeader().setMsgId(JT808Constant.DOWN_PARAM_FIND_REQ);
    }

    @Override
    public ByteBuf toByteBufMsg() {
        return super.toByteBufMsg();
    }
}
