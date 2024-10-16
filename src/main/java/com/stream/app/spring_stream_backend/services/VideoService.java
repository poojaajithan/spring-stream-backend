package com.stream.app.spring_stream_backend.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.stream.app.spring_stream_backend.entities.Video;

public interface VideoService {
	
	//save video
	Video save(Video video, MultipartFile file);
	
	//get video by id
	Video getById(String videoId);
	
	//get video by title
	Video getByTitle(String title);
	
	//get all videos
	List<Video> getAll();
	
	//video processing
	String processVideo(String videoId);
}
