package io.spring.demo.backpressure;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;


@SpringBootApplication
public class DemoBackpressureApplication implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(DemoBackpressureApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(DemoBackpressureApplication.class, args);
	}


	@Override
	public void run(String... args) throws InterruptedException {

		DemoSubscriber s = new DemoSubscriber();

		RSocketRequester.builder()
				.metadataMimeType(MimeTypeUtils.TEXT_PLAIN)
				.connectTcp("localhost", 8765)
				.flatMapMany(r -> r.route("").retrieveFlux(String.class))
				.subscribe(s);


		for (Scanner sc = new Scanner(System.in); !s.isDisposed() ; s.awaitBatch()) {
			System.out.println("\nEnter demand (int): ");
			try {
				int n = sc.nextInt();
				System.out.println("");
				s.requestBatch(n);
			}
			catch (Exception ex) {
				logger.info("Cancelling");
				s.cancel();
			}
		}
	}


	/**
	 * Subscriber to request a batch of items at at time.
 	 */
	private static class DemoSubscriber extends BaseSubscriber<String> {

		private volatile CountDownLatch latch = new CountDownLatch(0);


		@Override
		protected void hookOnSubscribe(Subscription subscription) {
			// Do not request any items to start...
		}

		@Override
		protected void hookOnNext(String value) {
			logger.info("[" + this.latch.getCount() + "] '" + value + "'");
			this.latch.countDown();
		}

		public void requestBatch(int n) {
			this.latch = new CountDownLatch(n);
			request(n);
		}

		public void awaitBatch() throws InterruptedException {
			this.latch.await();
		}
	}

}
