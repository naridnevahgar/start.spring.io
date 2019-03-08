package io.spring.start.site.entity;

import java.util.List;

import lombok.Data;

@Data
public class ApplicationInfo {

	private String name;
	private String memory;
	private String host;
	private int instances;
	private String domain;
	private String path;
	private List<String> services;
	
}
