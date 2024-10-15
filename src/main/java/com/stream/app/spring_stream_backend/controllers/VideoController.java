package com.stream.app.spring_stream_backend.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.spring_stream_backend.entities.Video;
import com.stream.app.spring_stream_backend.payload.CustomMessage;
import com.stream.app.spring_stream_backend.services.VideoService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

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
		Video savedVideo = videoService.save(video, file);
		
		if (savedVideo != null) {
			return ResponseEntity.status(HttpStatus.OK).body(video);
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomMessage.builder()
															.message("Video not uploaded")
															.success(false)
															.build());
		}
	}
	
	
	//stream video
	@GetMapping("/stream/{videoId}")
	public ResponseEntity<Resource> stream(@PathVariable String videoId){
		Video video = videoService.getById(videoId);
		String contentType = video.getContentType();
		String filePath = video.getPath();
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		Resource resource = new FileSystemResource(filePath);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);	
	}
	
	@GetMapping
	public List<Video> getAll(){
		return videoService.getAll();
	}
	
	//stream video in chunks
	@GetMapping("/stream/range/{videoId}")
	public ResponseEntity<Resource> streamVideoRange(@PathVariable String videoId, 
													@RequestHeader(value="Range",required=false) String range){
		System.out.println("Range : " + range);
		Video video = videoService.getById(videoId);
		Path path = Paths.get(video.getPath());
		Resource resource = new FileSystemResource(path);
		String contentType = video.getContentType();
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		
		long fileLength = path.toFile().length();
		
		if (range == null) {
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
		}
		
		long rangeStart=0, rangeEnd=0;
		String [] ranges = range.replace("bytes ", "").split("-");
		rangeStart = Long.parseLong(ranges[0]);
		if (range.length()>1) {
			rangeEnd = Math.min(fileLength-1, Long.parseLong(ranges[1]));
		}
		System.out.println("range start : " + rangeStart);
        System.out.println("range end : " + rangeEnd);
        
        try (InputStream inputStream = Files.newInputStream(path)) 
        {
        	inputStream.skip(rangeStart);
        	long contentLength = rangeEnd-rangeStart+1;
            byte[] data = new byte[(int) contentLength];
            int read = inputStream.read(data, 0, data.length);
            System.out.println("read(number of bytes) : " + read);
        	HttpHeaders headers = new HttpHeaders();
        	headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("X-Content-Type-Options", "nosniff");
            headers.setContentLength(contentLength);
        	return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
        			.headers(headers)
        			.contentType(MediaType.parseMediaType(contentType))
                    .body(new ByteArrayResource(data));
        } 
        catch (IOException e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
			
	}
}
