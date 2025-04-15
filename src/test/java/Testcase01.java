import client.WorkClient;
//import listener.ExtentTestNGIReporterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.DingTalkRobotUtil;

import java.io.IOException;
import java.lang.reflect.Method;

//@Listeners({ExtentTestNGIReporterListener.class})
public class Testcase01 extends BaseTest{
    static Logger logger = LoggerFactory.getLogger(Testcase01.class);
    WorkClient workClient ;


    @BeforeClass
    void setup(){
        //这里可以实例化很多client 来支持多账号切换
        workClient = new WorkClient("credential", "password");
    }

    @Test
    public void test01(Method method) throws IOException {
        try {
            logger.info("hello world !");
            logger.info(workClient.getToken());
        } catch (Exception e) {
            e.printStackTrace();
            DingTalkRobotUtil.sendMessage("", method.getName());
        }
//        workClient.getsomeapidone();




    }
}
