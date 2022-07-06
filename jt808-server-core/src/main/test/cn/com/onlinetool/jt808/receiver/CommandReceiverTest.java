package cn.com.onlinetool.jt808.receiver;

import cn.com.onlinetool.jt808.Application;
import cn.com.onlinetool.jt808.bean.down.req.*;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import cn.com.onlinetool.jt808.consts.TerminalParamCommandConstant;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class CommandReceiverTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final String RABBIT_EXCHANGE = "commandDown";
    private static final String TEST_TERMINAL_PHONE = "000000010086";

    @Test
    public void sendMsgWithDown0x8201(){
        log.info("send 0x8201 msg start.");
        Down0x8201 down0x8201 = new Down0x8201(TEST_TERMINAL_PHONE);
        rabbitTemplate.convertAndSend(RABBIT_EXCHANGE,"",down0x8201);
        log.info("send 0x8201 msg end.");
    }

    @Test
    public void sendMsgWithDown0x8103(){
        log.info("send 0x8103 msg start.");
        Down0x8103 down0x8103 = new Down0x8103(TEST_TERMINAL_PHONE);
        List<Down0x8103.Down0x8103ParamItem> itemList = down0x8103.getParamList();
        Down0x8103.Down0x8103ParamItem heartBeatInterval = new Down0x8103.Down0x8103ParamItem(TerminalParamCommandConstant.HEARTBEAT_INTERVAL,new byte[]{10});
        down0x8103.getParamList().add(heartBeatInterval);
        Down0x8103.Down0x8103ParamItem tcpMsgTimeout = new Down0x8103.Down0x8103ParamItem(TerminalParamCommandConstant.TCP_MSG_TIMEOUT,new byte[]{10});
        down0x8103.getParamList().add(tcpMsgTimeout);
        down0x8103.setParamCount(itemList.size());
        rabbitTemplate.convertAndSend(RABBIT_EXCHANGE,"",down0x8103);
        log.info("send 0x8103 msg end.");
    }

    @Test
    public void sendMsgWithDown0x8106(){
        log.info("send 0x8106 msg start.");
        Down0x8106 down0x8106 = new Down0x8106(TEST_TERMINAL_PHONE);
        down0x8106.setParamCount(1);
        down0x8106.setParamIds(new int[]{0x0001});
        rabbitTemplate.convertAndSend(RABBIT_EXCHANGE,"",down0x8106);
        log.info("send 0x8106 msg end.");
    }

    @Test
    public void sendMsgWithDown0x8107(){
        log.info("send 0x8107 msg start.");
        Down0x8107 down0x8107 = new Down0x8107(TEST_TERMINAL_PHONE);
        rabbitTemplate.convertAndSend(RABBIT_EXCHANGE,"",down0x8107);
        log.info("send 0x8107 msg end.");
    }

    @Test
    public void sendMsgWithDown0x8108(){
        log.info("send 0x8108 msg start.");
        Down0x8108 down0x8108 = new Down0x8108(TEST_TERMINAL_PHONE);
        down0x8108.setType(0);
        down0x8108.setManufacturerId("12345");
        String softVersionNum = "1.0";
        down0x8108.setSoftVersionNum(softVersionNum);
        down0x8108.setSoftVersionNumLen(softVersionNum.getBytes(JT808Constant.DEFAULT_CHARSET).length);
        try(InputStream is = new ClassPathResource("banner.txt").getInputStream()){
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024*4];
            int n = 0;
            while (-1 != (n = is.read(buffer))) {
                output.write(buffer, 0, n);
            }
            down0x8108.setPacket(output.toByteArray());
            down0x8108.setPacketLen(down0x8108.getPacket().length);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        rabbitTemplate.convertAndSend(RABBIT_EXCHANGE,"",down0x8108);
        log.info("send 0x8108 msg end.");
    }

}