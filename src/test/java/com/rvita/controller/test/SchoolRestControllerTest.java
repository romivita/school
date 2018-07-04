package com.rvita.controller.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.rvita.Application;
import com.rvita.model.School;
import com.rvita.model.Student;
import com.rvita.repository.SchoolRepository;
import com.rvita.repository.StudentRepository;

/**
 * @author Josh Long
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class SchoolRestControllerTest {


    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Student student;

    private List<School> schoolList = new ArrayList<>();

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.schoolRepository.deleteAllInBatch();
        this.studentRepository.deleteAllInBatch();

        this.student = studentRepository.save(new Student((long)32654987, "test", "test"));
        this.schoolList.add(schoolRepository.save(new School("test1")));
        this.schoolList.add(schoolRepository.save(new School("test2")));
    }

    @Test
    public void userNotFound() throws Exception {
        mockMvc.perform(post("/george/schools/")
                .content(this.json(new School(null)))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void readSingleSchool() throws Exception {
        mockMvc.perform(get("/school/" + this.schoolList.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(this.schoolList.get(0).getId().intValue())))
                .andExpect(jsonPath("$.uri", is("http://school.com/1/" + userName)))
                .andExpect(jsonPath("$.description", is("A description")));
    }

    @Test
    public void readSchools() throws Exception {
        mockMvc.perform(get("/schools/" + userName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(this.schoolList.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].uri", is("http://school.com/1/" + userName)))
                .andExpect(jsonPath("$[0].description", is("A description")))
                .andExpect(jsonPath("$[1].id", is(this.schoolList.get(1).getId().intValue())))
                .andExpect(jsonPath("$[1].uri", is("http://school.com/2/" + userName)))
                .andExpect(jsonPath("$[1].description", is("A description")));
    }

    @Test
    public void createSchool() throws Exception {
        String schoolJson = json(new School("test1"));

        this.mockMvc.perform(post("/schools/" + userName)
                .contentType(contentType)
                .content(schoolJson))
                .andExpect(status().isCreated());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}