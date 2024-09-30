package com.stream.app.spring_stream_backend.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.spring_stream_backend.entities.Video;
import com.stream.app.spring_stream_backend.repositories.VideoRepository;
import com.stream.app.spring_stream_backend.services.VideoService;

@Service
public class VideoServiceImpl implements VideoService {
	
	@Autowired
	private VideoRepository  videoRepository;
	
	@Value("${files.video}")
	String DIR;

	@Override
	public Video save(Video video, MultipartFile file) {
		
		try {
			//folder path creation
			String fileName = file.getOriginalFilename();
			String contentType = file.getContentType();
			InputStream inputStream = file.getInputStream();
			String cleanPath = StringUtils.cleanPath(fileName);
			String cleanFolder = StringUtils.cleanPath(DIR);
			
			//folder path with filename
			Path path = Paths.get(cleanFolder,cleanPath);
			System.out.println(contentType);
	        System.out.println(path);

			//copy file to folder
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
			
			//video metadata
			video.setContentType(contentType);
			video.setPath(path.toString());
			Video savedVideo = videoRepository.save(video);
			
			//metadata save
			
			return savedVideo;
		}
		catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in processing video ");
        }
		
	}

	@Override
	public Video getById(String videoId) {
		return videoRepository.getById(videoId);
	}

	@Override
	public Video getByTitle(String title) {
		return null;
	}

	@Override
	public List<Video> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
