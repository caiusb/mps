package indexer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

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
	
	private static volatile boolean areProducersDone = false;

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

	private static class FileProducer {

		private final File[] files;
		private FileFilter filter;
		private BlockingQueue<File> queue;
		private ProducerThread[] threads;

		public FileProducer(File[] files, FileFilter filter, BlockingQueue<File> queue) {
			this.files = files;
			this.filter = filter;
			this.queue = queue;
		}
		
		private static class ProducerThread extends Thread {
			
			private File file;
			private BlockingQueue<File> queue;
			private FileFilter filter;

			public ProducerThread(File file, FileFilter filter, BlockingQueue<File> queue) {
				this.file = file;
				this.filter = filter;
				this.queue = queue;
			}
			
			@Override
			public void run() {
				if (file.isDirectory())
					try {
						crawl(file);
					} catch (InterruptedException e) {
					}
				else
					queue.add(file);
			}
			
			private void crawl(File root) throws InterruptedException {
				if (!root.isDirectory())
					return;

				File[] entries = root.listFiles(filter);
				for (File entry : entries)
					if (entry.isDirectory())
						crawl(entry);
					else
						queue.add(entry);
			}
		}
		
		public void produce() {
			threads = new ProducerThread[files.length];
			for (int i=0; i<files.length; i++) {
				threads[i] = new ProducerThread(files[i], filter, queue);
				threads[i].start();
			}
		}
		
		public void join() {
			for (int i=0; i<files.length; i++) {
				try {
					threads[i].join();
				} catch (InterruptedException e) {
				}
			}
			
			areProducersDone = true;
		}
	}

	private static class FileConsumer {

		private ConcurrentMap<String, Set<File>> index;
		private BlockingQueue<File> queue;
		private int noOfThreads;
		private Thread[] threads;

		public FileConsumer(ConcurrentMap<String, Set<File>> index, BlockingQueue<File> queue, int noOfThreads) {
			this.index = index;
			this.queue = queue;
			this.noOfThreads = noOfThreads;
		}
		
		private static class ConsumerThread extends Thread {
			
			private BlockingQueue<File> queue;
			private ConcurrentMap<String, Set<File>> index;

			public ConsumerThread(BlockingQueue<File> queue, ConcurrentMap<String, Set<File>> index) {
				this.queue = queue;
				this.index = index;
				
			}
			
			@Override
			public void run() {
				while (!areProducersDone || !queue.isEmpty())
					try {
						File file = queue.take();
						indexFile(file);
					} catch (InterruptedException e) {
					}
			}
			
			public void indexFile(File file) {
				System.out.println("Indexing... " + file);
				try {
					Scanner s = new Scanner(file);
					while (s.hasNextLine()) {
						String line = s.nextLine();
						String[] split = line.split(" ");
						for (String token : split) {
							index.putIfAbsent(
									token,
									Collections
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
		
		public void consume() {
			threads = new Thread[noOfThreads];
			for (int i=0; i<noOfThreads; i++) { 
				threads[i] = new ConsumerThread(queue, index);
				threads[i].start();
			}
		}

		public void join() {
			for (int i=0; i<noOfThreads; i++)
				try {
					threads[i].join();
				} catch (InterruptedException e) {
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
			BlockingQueue<File> queue = new LinkedBlockingQueue<File>();
			
			FileProducer producer = new FileProducer(files, filter, queue);
			FileConsumer consumer = new FileConsumer(index, queue, Runtime.getRuntime().availableProcessors());
			
			producer.produce();
			consumer.consume();

			producer.join();
			consumer.join();
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
