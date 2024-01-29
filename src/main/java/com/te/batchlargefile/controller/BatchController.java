package com.te.batchlargefile.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/data")
@AllArgsConstructor
public class BatchController {
	
	private final Job job;
	private final JobLauncher jobLauncher;
	
	@PostMapping("/importData")
	public void exportDatatoDb() {
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("startAt", System.currentTimeMillis()).toJobParameters();
		/*
		 * 
		 * addLong
		 * Add a new identifying Long parameter for the given key.
           Parameters:key - parameter accessor.
           parameter - runtime parameter
           Returns:a reference to this object.
           
           
           toJobParameters()
           Conversion method that takes the current state of this builder and
           returns it as a JobParameters object.
           Returns:a valid JobParameters object.
		 */
		
		try {
			jobLauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			
			e.printStackTrace();
		}
	}

}
