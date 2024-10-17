package com.stream.app.spring_stream_backend.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.spring_stream_backend.entities.Video;
import com.stream.app.spring_stream_backend.repositories.VideoRepository;
import com.stream.app.spring_stream_backend.services.VideoService;

import jakarta.annotation.PostConstruct;

@Service
public class VideoServiceImpl implements VideoService {
	
	@Autowired
	private VideoRepository  videoRepository;
	
	@Value("${files.video}")
	String DIR;
	
	@Value("${file.video.hls}")
	String HLS_DIR;
	
	

	public VideoServiceImpl(VideoRepository videoRepository) {
		super();
		this.videoRepository = videoRepository;
	}
	
	@PostConstruct
	public void init() {
		File file = new File(DIR);
		try {
			Files.createDirectories(Paths.get(HLS_DIR));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
	    }
		
		if (!file.exists()) {
			file.mkdir();
			System.out.println("Folder created");
		}
		else {
			System.out.println("Folder already created");
		}
	}

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
			 //processing video
            processVideo(savedVideo.getVideoId());
			return savedVideo;
		}
		catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in processing video ");
        }
		
	}

	@Override
	public Video getById(String videoId) {
		Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
		return video;
	}

	@Override
	public Video getByTitle(String title) {
		return null;
	}

	@Override
	public List<Video> getAll() {
		return videoRepository.findAll();
	}

	@Override
	public String processVideo(String videoId) {
		Video video = videoRepository.getById(videoId);
		String filePath = video.getPath();
		Path videoPath = Paths.get(filePath);
		
		Path outputPath = Paths.get(HLS_DIR, videoId);
   
		try {
			Files.createDirectories(outputPath);
			
			String ffmpegCmd = String.format(
			        "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
			        videoPath, outputPath, outputPath
			);
			System.out.println(ffmpegCmd);

			// Directly use ffmpeg command without /bin/bash
			ProcessBuilder processBuilder = new ProcessBuilder("C:\\ffmpeg-7.1-essentials_build\\bin\\ffmpeg.exe", "-i", videoPath.toString(),
			        "-c:v", "libx264",
			        "-c:a", "aac",
			        "-strict", "-2",
			        "-f", "hls",
			        "-hls_time", "10",
			        "-hls_list_size", "0",
			        "-hls_segment_filename", outputPath + "/segment_%3d.ts",
			        outputPath + "/master.m3u8"
			);
			processBuilder.inheritIO();
			Process process = processBuilder.start();

			int exit = process.waitFor();
			if (exit != 0) {
				throw new RuntimeException("video processing failed!!");
			}
			 return videoId;
		}
		catch(IOException ie) {
			throw new RuntimeException("Video processing failed.");
		}
		catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
	}

}
