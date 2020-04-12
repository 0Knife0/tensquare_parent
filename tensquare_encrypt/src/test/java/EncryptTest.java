import com.tensquare.encrypt.EncryptApplication;
import com.tensquare.encrypt.rsa.RsaKeys;
import com.tensquare.encrypt.service.RsaService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EncryptApplication.class)
public class EncryptTest {

    @Autowired
    private RsaService rsaService;

    @Before
    public void before() throws Exception{
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void genEncryptDataByPubKey() {
        String data = "{\"title\":\"java\"}";

        try {

            String encData = rsaService.RSAEncryptDataPEM(data, RsaKeys.getServerPubKey());

            System.out.println("data: " + data);
            System.out.println("encData: " + encData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void gdeDecryptDataByPrv() throws Exception {
        String data = "kfj5Xhfw0+gImQQ3JkPbSDflVeY8WsZKT98NkfojKL1UpuXPQeMSPauqJPygksGXdn7GwM6jo5PU5rEpbBwZlSc5/1HAJbSG6Wc84VXLUGngDoSBXZLgZUPjwynAora2etDyiF43fOzqagNSyvUIFjcrRt33E62etGJSVa2yzApgj5jsESFt+G+NIZqBXyM3gmSHbVsusbai1PuJhtDtU+LMHiPrlVF873fPQiOuCnEXiJYcNSNuq9MjCo3gX9d9sXbBTh12EuUoIpU3XHQrRUOaVFGi+02GWH7Z/URAP0ZEydPlDAbGh0yxXzsR10QOunQzzphFG2iR05URRh76fQ==";
        String decData = rsaService.RSADecryptDataPEM(data,RsaKeys.getServerPrvKeyPkcs8());
        System.out.println(decData);
    }
}
