package com.te.batchlargefile.config;

import org.springframework.batch.item.ItemProcessor;

import com.te.batchlargefile.entity.DataFile;

public class BatchProcessor implements ItemProcessor<DataFile, DataFile> {

	@Override
	public DataFile process(DataFile item) throws Exception {

		if(item.getPlace().equalsIgnoreCase("Nunavut")) {
			return item;
		}
          
		return null;
	}

}
