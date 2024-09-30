package com.stream.app.spring_stream_backend.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.spring_stream_backend.entities.Video;
import com.stream.app.spring_stream_backend.services.VideoService;

@RestController
@RequestMapping("api/v1/videos")
public class VideoController {
	
	@Autowired
	private VideoService videoService;
	
	@PostMapping
	public ResponseEntity<?> create(@RequestParam("file") MultipartFile file,
												@RequestParam("title") String title,
												@RequestParam("description") String description){
		
		Video video = new Video();
		video.setTitle(title);
		video.setDescription(description);
		video.setVideoId(UUID.randomUUID().toString());
		videoService.save(video, file);
		return null;
	}

}
