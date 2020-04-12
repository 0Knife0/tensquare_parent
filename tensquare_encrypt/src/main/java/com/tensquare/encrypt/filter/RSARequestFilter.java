package com.tensquare.encrypt.filter;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import com.tensquare.encrypt.rsa.RsaKeys;
import com.tensquare.encrypt.service.RsaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RSARequestFilter extends ZuulFilter {

    @Autowired
    private RsaService rsaService;

    @Override
    public String filterType() {
        // 过滤器在什么环境执行，解密操作需要在转发之前执行
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        // 过滤器执行的顺序
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        // 是否使用过滤器
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 过滤器具体执行的逻辑
        System.out.println("过滤器执行了");

        // 获取requestContext容器
        RequestContext ctx = RequestContext.getCurrentContext();

        // 获取request和response
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        // 声明存放加解密后的数据
        String encData = null, decData = null;

        try {
            // 通过request获取inputStream
            ServletInputStream inputStream = request.getInputStream();

            // 从inputStream中获取加密数据
            encData = StreamUtils.copyToString(inputStream, Charsets.UTF_8);
            //System.out.println(encData);

            // 对加密数据进行解密操作
            if (!Strings.isNullOrEmpty(encData)){
                decData = rsaService.RSADecryptDataPEM(encData, RsaKeys.getServerPrvKeyPkcs8());
                //System.out.println(decData);
            }

            // 把解密后的数据进行转发
            if (!Strings.isNullOrEmpty(decData)){
                byte[] bytes = decData.getBytes();

                // 使用requestContext进行数据妆发
                ctx.setRequest(new HttpServletRequestWrapper(request) {
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        return new ServletInputStreamWrapper(bytes);
                    }

                    @Override
                    public int getContentLength() {
                        return bytes.length;
                    }

                    @Override
                    public long getContentLengthLong() {
                        return bytes.length;
                    }
                });
            }
            // 设置请求头中的context-Type，为json格式数据
            ctx.addZuulRequestHeader("context-Type",
                    MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
