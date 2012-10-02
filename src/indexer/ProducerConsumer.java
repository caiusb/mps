package indexer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import util.StopWatch;

/*
 In this example we will transform a sequential implementation of a simple file
 indexer to a producer-consumer version of it. The indexer reads all the files from 
 one (or more) directories and produces a map between words and the files that contain them, 
 i.e. it generates a Map<String, Set<File>>. 

 Steps:

 1. Find a good split between the "producer" part and the "consumer" part of
 the algorithm. The aim is to have a producer that crawls the directory structure and
 generates File objects (there are other split options, you can explore them if you
 like). The consumer feeds off the File objects and indexes them.
 Make the necessary transformations such that the "producer" part and the "consumer"
 part are separate (inner) classes. For now, don't worry about having more than 
 one producer and more than one consumer. Also, don't worry about how they are 
 interfaced. For this step you can use a simple data structure like a Set to get 
 all results from the "producer" and then send them to the "consumer". 

 2. The algorithm has a list of directories (as Files) as input. Make it such that 
 each file is processed by a different producer.

 3. The result of the consumer is, for now, a set of Files. Make it such that each
 file is processed by a separate "consumer".

 4. Make the "producers" work in parallel. You can choose any solution you like, i.e.
 simple Threads, Runnable tasks, thread pools, etc. Use a concurrent collection
 to it thread-safe.

 5. Make sure the producers finish before starting the consumers. Figure out a way 
 of stopping the producers when they have finished their work.

 6. Parallelize the consumers in a similar manner. Don't worry about running 
 the producers and consumers in parallel, for now. Make the consumers feed on the set given by
 the producers.

 7. Replace the Set<File> used to communicate between producers and consumers with a BlockingQueue.

 8. Make producers and consumers run in parallel. Make sure the application still terminates.
 */

public class ProducerConsumer {

	private static void startIndexing(File[] files) {
		FileFilter filter = new FileFilter() {
			public boolean accept(File file) {
				return true;
			}
		};

		Indexer indexer = new Indexer(files, filter);
		try {
			indexer.compute();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("The index contains: "
				+ indexer.getIndex().get("the").size());
	}

	private static class FileProducer implements Callable<Set<File>> {

		private final File file;
		private Set<File> filesToIndex;
		private FileFilter filter;

		public FileProducer(File file, FileFilter filter) {
			this.file = file;
			this.filter = filter;
		}

		@Override
		public Set<File> call() throws Exception {
			filesToIndex = new HashSet<File>();
			if (file.isDirectory())
				crawl(file);
			else if (!filesToIndex.contains(file))
				filesToIndex.add(file);

			return filesToIndex;
		}

		private void crawl(File root) throws InterruptedException {
			if (!root.isDirectory())
				return;

			File[] entries = root.listFiles(filter);
			for (File entry : entries)
				if (entry.isDirectory())
					crawl(entry);
				else if (!filesToIndex.contains(entry))
					filesToIndex.add(entry);
		}
	}

	private static class FileConsumer implements Callable<Object> {

		private ConcurrentMap<String, Set<File>> index;
		private Set<File> files;
		private File file;

		public FileConsumer(File file, ConcurrentMap<String, Set<File>> index) {
			this.file = file;
			this.index = index;
		}

		@Override
		public Object call() {
			indexFile(file);
			return null;
		}

		public void indexFile(File file) {
			System.out.println("Indexing... " + file);
			try {
				Scanner s = new Scanner(file);
				while (s.hasNextLine()) {
					String line = s.nextLine();
					String[] split = line.split(" ");
					for (String token : split) {
						index.putIfAbsent(token, Collections
									.newSetFromMap(new ConcurrentHashMap<File, Boolean>()));
						Set<File> set = index.get(token);
						set.add(file);
					}
				}
				s.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	static class Indexer {
		private ConcurrentMap<String, Set<File>> index = new ConcurrentHashMap<String, Set<File>>();
		private FileFilter filter;
		private File[] files;

		public Indexer(File[] files, FileFilter filter) {
			this.files = files;
			this.filter = filter;
		}

		public Map<String, Set<File>> getIndex() {
			return index;
		}

		public void compute() throws InterruptedException {
			Set<File> fileSet = new HashSet<File>();
			List<Callable<Set<File>>> tasks = new ArrayList<Callable<Set<File>>>();
			for (File file : files) {
				FileProducer producer = new FileProducer(file, filter);
				tasks.add(producer);
			}
			ExecutorService pool = Executors.newFixedThreadPool(Runtime
					.getRuntime().availableProcessors());
			List<Future<Set<File>>> results = pool.invokeAll(tasks);
			pool.shutdown();
			while (!pool.awaitTermination(1, TimeUnit.HOURS))
				;
			for (Future<Set<File>> result : results) {
				try {
					fileSet.addAll(result.get());
				} catch (ExecutionException e) {
				}
			}
			

			List<Callable<Object>> consumerTasks = new ArrayList<Callable<Object>>();
			for (File file : fileSet) {
				FileConsumer consumer = new FileConsumer(file, index);
				consumerTasks.add(consumer);
			}
			ExecutorService consumerPool = Executors.newFixedThreadPool(Runtime
					.getRuntime().availableProcessors());
			consumerPool.invokeAll(consumerTasks);
			consumerPool.shutdown();
			while (!consumerPool.awaitTermination(1, TimeUnit.HOURS))
				;
		}
	}

	public static void main(String[] args) {
		File[] files = new File[1];
		files[0] = new File("resources/data");
		StopWatch.start();
		startIndexing(files);
		StopWatch.stop();
		StopWatch.log("Runtime: ");
	}
}
