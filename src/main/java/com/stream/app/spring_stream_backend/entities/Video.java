package com.stream.app.spring_stream_backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "yt_videos")

public class Video {
	@Id
	private String videoId;
	private String title;
	private String description;
	private String contentType;
	private String path;
	/*@ManyToOne
	private Course course;*/
	
	public Video() {
		super();
	}
	
	public Video(String videoId, String title, String description, String contentType, String path) {
		super();
		this.videoId = videoId;
		this.title = title;
		this.description = description;
		this.contentType = contentType;
		this.path = path;
	}

	public String getVideoId() {
		return videoId;
	}
	
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
