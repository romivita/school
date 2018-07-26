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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class StudentRestControllerTest {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	private String name = "EscuelaTest";

	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	private School school;

	private List<Student> studentList = new ArrayList<>();

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);

		assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

		this.studentRepository.deleteAllInBatch();
		this.schoolRepository.deleteAllInBatch();

		this.school = schoolRepository.save(new School(name));
		this.studentList.add(studentRepository.save(new Student((long) 34567890, "John", "Doe")));
		this.studentList.add(studentRepository.save(new Student((long) 33444555, "John", "Dos")));
	}

	@Test
	public void Given_UserIsNotCreated_When_InvalidStudentIdIsUsedTo_Then_StudentNotFoundWillBeReturned() throws Exception {
		mockMvc.perform(get("/student/2").content(this.json(new Student(null, null, null))).contentType(contentType))
				.andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void Given_UserIsCreated_When_ValidStudentIdIsUsedTo_Then_UserWillBeReturned() throws Exception {
		mockMvc.perform(get("/student/" + this.studentList.get(0).getId())).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.id", is(this.studentList.get(0).getId().intValue())))
				.andExpect(jsonPath("$.du", is(this.studentList.get(0).getDu().intValue())))
				.andExpect(jsonPath("$.firstName", is(this.studentList.get(0).getFirstName().toString())))
				.andExpect(jsonPath("$.lastName", is(this.studentList.get(0).getLastName().toString())));

	}

	@Test
	public void Given_UsersAreCreated_When_All_Then_AllUsersWillBeReturned() throws Exception {
		mockMvc.perform(get("/student/all")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(this.studentList.get(0).getId().intValue())))
				.andExpect(jsonPath("$[0].du", is(this.studentList.get(0).getDu().intValue())))
				.andExpect(jsonPath("$[0].firstName", is(this.studentList.get(0).getFirstName().toString())))
				.andExpect(jsonPath("$[0].lastName", is(this.studentList.get(0).getLastName().toString())))
				.andExpect(jsonPath("$[1].id", is(this.studentList.get(1).getId().intValue())))
				.andExpect(jsonPath("$[1].du", is(this.studentList.get(1).getDu().intValue())))
				.andExpect(jsonPath("$[1].firstName", is(this.studentList.get(1).getFirstName().toString())))
				.andExpect(jsonPath("$[1].lastName", is(this.studentList.get(1).getLastName().toString())));
	}

	@Test
	public void Given_NewUser_When_AllValidPArametersAreSent_Then_NewUserWillBeReturned() throws Exception {
		String studentJson = json(new Student((long) 34567890, "John", "Doe"));

		this.mockMvc.perform(post("/student/add?du=32654987&firstName=Test&lastName=Test&school=1")
				.contentType(contentType).content(studentJson)).andDo(print()).andExpect(status().isOk());
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}