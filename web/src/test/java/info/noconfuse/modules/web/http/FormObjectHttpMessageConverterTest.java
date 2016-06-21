package info.noconfuse.modules.web.http;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by zzp on 6/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("src/test/webapp")
@ContextConfiguration(locations = {"classpath:spring-mvc.xml"})
public class FormObjectHttpMessageConverterTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * 测试 application/x-www-form-urlencoded 类型的请求对应到 @RequestParam 的情况, 此为 Spring 原生能力
     * @throws Exception
     */
    @Test
    public void testFormParams() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/test/params")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "Michael")
                .param("lastName", "jackson")
                .param("age", "66"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        Assert.assertTrue(result.getResponse().getStatus() == 200);
    }

    /**
     * 测试 application/json 类型的请求对应到 @RequestBody 的情况, 此为 Spring 自带 {@link org.springframework.http.converter.json.MappingJackson2HttpMessageConverter} 提供的能力
     * @throws Exception
     */
    @Test
    public void testJsonConvert() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/test/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Michael\",\"lastName\":\"Jackson\",\"age\":66}"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        Assert.assertTrue(result.getResponse().getStatus() == 200);
    }

    /**
     * 测试 application/x-www-form-urlencoded 类型的请求对应到 @RequestBody 的情况, 此为本项目中 {@link info.noconfuse.modules.web.http.FormObjectHttpMessageConverter} 提供的能力
     * @throws Exception
     */
    @Test
    public void testFormConvert() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/test/form")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "Michael")
                .param("lastName", "jackson")
                .param("age", "66"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        Assert.assertTrue(result.getResponse().getStatus() == 200);
    }
}
