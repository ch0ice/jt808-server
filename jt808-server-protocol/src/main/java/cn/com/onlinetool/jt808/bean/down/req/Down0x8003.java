package cn.com.onlinetool.jt808.bean.down.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@ToString(callSuper = true)
@Data
public class Down0x8003 extends BasePacket implements Serializable {

    public BasePacket build(BasePacket msg, int flowId) {
        Header header = getHeader();
        header.setMsgId(0x8003);
        BeanUtils.copyProperties(msg.getHeader(), header);
        //设置
        header.setFlowId(flowId);
        return this;
    }


}