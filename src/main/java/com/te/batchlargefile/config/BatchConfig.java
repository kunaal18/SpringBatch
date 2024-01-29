package com.te.batchlargefile.config;


import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.te.batchlargefile.entity.DataFile;
import com.te.batchlargefile.repository.BatchRepository;

import lombok.AllArgsConstructor;

@Configuration
@EnableBatchProcessing
/*
 * this annotation tell to the spring boot for this application is particular
 * application user want to enable the batch processing
 */
@AllArgsConstructor
public class BatchConfig {

	private final JobBuilderFactory jobbuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final BatchRepository batchRepository;
	private final DataSource dataSource;

	/*
	 * Step 1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
	 * create a reader bean FlatFileItemReader is a class given by the spring
	 * batch using this class we can read the data from the csv file
	 */
	@Bean
	public FlatFileItemReader<DataFile> reader() {

		System.err.println("reader////////////");
		FlatFileItemReader<DataFile> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new ClassPathResource("SampleCSVFile_10600kb.csv"));
		itemReader.setName("csvReader");
		itemReader.setLinesToSkip(1); //this will skips the lines of the data from the file if we get error
		itemReader.setLineMapper(new DefaultLineMapper<>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
                        setDelimiter(DELIMITER_COMMA);
                        setNames(new String[]{"department","name","price","price2","price3","price4","place","organization","gst"});
//                        setIncludedFields(new int[] {0,1,2,3)
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
					setTargetType(DataFile.class);
				}});
			}
		});
		return itemReader;

	}
	
	//Another way of set the line Mapper in reader bean
	public LineMapper<DataFile> lineMapper(){
		DefaultLineMapper<DataFile> lineMapper = new DefaultLineMapper<>();
		
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setNames("custId","custName","custAge","place");
		
		BeanWrapperFieldSetMapper<DataFile> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(DataFile.class);
		
		lineMapper.setFieldSetMapper(fieldSetMapper);
		lineMapper.setLineTokenizer(lineTokenizer);
		
		return lineMapper;
		
		
	}
	
	@Bean
	public BatchProcessor processor(){
		System.err.println("Processor////////////////////");
		return new BatchProcessor();
	}
	
	@Bean
	public RepositoryItemWriter<DataFile> writer(){
		
		System.err.println("writer ...................");
		RepositoryItemWriter<DataFile> writer = new RepositoryItemWriter<>();
		writer.setRepository(batchRepository);
		writer.setMethodName("save");
		return writer;
	}
	
//	@Bean
//	public JdbcBatchItemWriter<DataFile> writer(){
//		JdbcBatchItemWriter<DataFile> writer = new JdbcBatchItemWriter<>();
//		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
//		writer.setSql("insert into largedb.data_file(department,name,price,price2,price3,price4,place,organization,gst) values (:department,:name,:price,:price2,:price3,:price4,:place,:organization,:gst)");
//		writer.setDataSource(dataSource);
//		return writer;
//		
//	}
	@Bean
	public Step step() {
		
		System.err.println("Step Calling ''''''''''''''''''''");
		return this.stepBuilderFactory.get("step")
				.<DataFile,DataFile>chunk(10)
				/*
				 * <DataFile, DataFile>: The first type parameter (DataFile) specifies the type of
				 *  item to be read by the reader, and the second type parameter 
				 *  (DataFile) specifies the type of item to be written by the writer. 
				 *  In this case, both the input and output items are of type DataFile.
				 */
				.reader(reader())
				.processor(processor())
				.writer(writer())
//				.taskExecutor(executor())
				.build()
				;
	}
	
	@Bean
	public Job job() {
		
		System.err.println("Job Execution,,,,,,,,,,,,,,,,,,,,,,,,,,");
		return jobbuilderFactory.get("importData")
				.incrementer( new RunIdIncrementer())
				.flow(step())
				.end()
//				.start(step())
//				.next(step2())
				.build();
		
	}
	
//	@Bean
//	public TaskExecutor executor(){
//		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
//		taskExecutor.setConcurrencyLimit(10);
//		return taskExecutor;
//	}
//	   
}
