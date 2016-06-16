package info.noconfuse.modules.web.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>使 @RequestBody 注解在处理 application/x-www-form-urlencoded 类型的请求时仍然可用.</p>
 * <p>在对请求数据进行读操作时实际是用的是 {@link FormHttpMessageConverter} 来执行.</p>
 * <p>Created by zzp on 4/16/2016</p>
 */
public class FormObjectHttpMessageConverter implements HttpMessageConverter<Object> {

    private final FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final LinkedMultiValueMap<String, String> LINKED_MULTI_VALUE_MAP = new LinkedMultiValueMap<>();

    private static final Class<? extends MultiValueMap<String, ?>> LINKED_MULTI_VALUE_MAP_CLASS
            = (Class<? extends MultiValueMap<String, ?>>) LINKED_MULTI_VALUE_MAP.getClass();

    @Override
    public boolean canRead(Class clazz, MediaType mediaType) {
        return objectMapper.canSerialize(clazz) && formHttpMessageConverter.canRead(MultiValueMap.class, mediaType);
    }

    @Override
    public boolean canWrite(Class clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return formHttpMessageConverter.getSupportedMediaTypes();
    }

    @Override
    public Object read(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Map<String, String> input = formHttpMessageConverter.read(LINKED_MULTI_VALUE_MAP_CLASS, inputMessage).toSingleValueMap();
        return objectMapper.convertValue(input, clazz);
    }

    @Override
    public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Writing is not supported by this HttpMessageConverter");
    }

}
