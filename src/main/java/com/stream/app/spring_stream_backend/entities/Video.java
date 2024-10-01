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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Video {
	@Id
	private String videoId;
	private String title;
	private String description;
	private String contentType;
	private String path;
	/*@ManyToOne
	private Course course;*/
}
