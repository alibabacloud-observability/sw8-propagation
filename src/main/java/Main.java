import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import skywalking.ContextCarrier;
import skywalking.GlobalIdGenerator;
import skywalking.SW8CarrierItem;

/**
 * @author carpela <luhao.wh@alibaba-inc.com>
 * @date 2022/12/1
 * <p>
 * https://github.com/apache/skywalking/blob/master/docs/en/protocols/Skywalking-Cross-Process-Propagation-Headers-Protocol-v3.md
 */
public class Main {

    // 1-YTRlYzZmYzhjY2FiNGJiNGI2ODIwNjQ2OThjYzk3ZTYuNzQuMTYyMTgzODExMDQ1NTAwMDk=-YTRlYzZmYzhjY2FiNGJiNGI2ODIwNjQ2OThjYzk3ZTYuNzQuMTYyMTgzODExMDQ1NTAwMDg=-2-b25lbW9yZS1h-ZTFkMmZiYjYzYmJhNDMwNDk5YWY4OTVjMDQwZTMyZmVAMTkyLjE2OC4xLjEwMQ==-L29uZW1vcmUtYS9nZXQ=-MTkyLjE2OC4xLjEwMjo4MA==//
    //以-字符进行分割，可以得到：
    //
    //1，采样，表示这个追踪需要采样并发送到后端。
    //YTRlYzZmYzhjY2FiNGJiNGI2ODIwNjQ2OThjYzk3ZTYuNzQuMTYyMTgzODExMDQ1NTAwMDk=，追踪ID，解码后为：a4ec6fc8ccab4bb4b682064698cc97e6.74.16218381104550009
    //YTRlYzZmYzhjY2FiNGJiNGI2ODIwNjQ2OThjYzk3ZTYuNzQuMTYyMTgzODExMDQ1NTAwMDg=，父追踪片段ID，解码后为：a4ec6fc8ccab4bb4b682064698cc97e6.74.16218381104550009
    //2，父跨度ID。
    //b25lbW9yZS1h，父服务名称，解码后为：onemore-a
    //ZTFkMmZiYjYzYmJhNDMwNDk5YWY4OTVjMDQwZTMyZmVAMTkyLjE2OC4xLjEwMQ==，父服务实例标识，解码后为：e1d2fbb63bba430499af895c040e32fe@192.168.1.101
    //L29uZW1vcmUtYS9nZXQ=，父服务的端点，解码后为：/onemore-a/get
    //MTkyLjE2OC4xLjEwMjo4MA==，本请求的目标地址，解码后为：192.168.1.102:80

    public static String generateSW8() {
        ContextCarrier cc = new ContextCarrier();
        cc.setTraceId(GlobalIdGenerator.generate()); // 按照 Skywalking 代码生成的
        cc.setTraceSegmentId(GlobalIdGenerator.generate()); // 按照 skywalking 代码生成的
        cc.setParentService("sw8-propagation"); // 上游应用名
        cc.setParentServiceInstance("e1d2fbb63bba430499af895c040e32fe@192.168.1.101"); // 该应用的具体节点标识
        cc.setSpanId(0); // 0 表示入口
        cc.setParentEndpoint("/sw8-propagation/api/entry");
        cc.setAddressUsedAtClient("192.168.1.102:80");

        String content = cc.serialize(ContextCarrier.HeaderVersion.v3);
        System.out.println("generate content of sw8 protocol: " + content);
        return content;
    }

    public static void main(String[] args) throws Exception {
        String sw8 = generateSW8(); // 生成 header value

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8081/user/async")
                .addHeader(SW8CarrierItem.HEADER_NAME, sw8)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (response.isSuccessful()) {
                System.out.println("success: " + (body == null ? "" : body.string()));
            } else {
                System.err.println("error,statusCode=" + response.code() + " ,body=" + (body == null ? "" : body.string()));
            }
        }

    }
}
