package uk.codingbadgers.bootstrap.tasks;

import uk.codingbadgers.bootstrap.Bootstrap;

import java.util.concurrent.CountDownLatch;

public abstract class AsyncTask implements Task {

    private final CountDownLatch latch;

    public AsyncTask(CountDownLatch latch) {
        this.latch = latch;
    }

    public void execute(Bootstrap bootstrap) {
        this.run(bootstrap);
        this.latch.countDown();
    }
}
