package com.mooctest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
@SpringBootApplication
@ServletComponentScan
@EnableAsync
public class BugReportQCApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(BugReportQCApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BugReportQCApplication.class);
    }
}
