package com.stream.app.spring_stream_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.stream.app.spring_stream_backend.services.VideoService;

@SpringBootTest
class SpringStreamBackendApplicationTests {

	@Autowired
	VideoService videoService;
	
	@Test
	void contextLoads() {
		videoService.processVideo("3fca7df4-77ba-46fa-a235-07e1d737fe8b");
	}

}
