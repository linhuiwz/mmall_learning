mmall_learning

package com.lh.concurrent;

import java.util.concurrent.TimeUnit;

public class TimeUnitTest {
	public static void main(String[] args) throws InterruptedException {
		// 睡眠13分钟
		//TimeUnit.MINUTES.sleep(13);
		// Thread.sleep(780000); // 这样写你知道是多久吗？
		// Thread.sleep(13*60*1000); // 这样写会稍微好些
		// 睡眠1小时
		//TimeUnit.HOURS.sleep(1);
		// Thread.sleep(3600000);
		System.out.println("============START============");
		TimeUnitTest test = new TimeUnitTest();
		Thread thread = new Thread(() -> test.work());
		thread.start();
		// 10秒内Join
		TimeUnit.SECONDS.timedJoin(thread, 10);
		// thread.join(10000);
		System.out.println("============END============");
	}

	public synchronized void work() {
		System.out.println("Begin Work");
		try {
			// 等待30秒后，自动唤醒继续执行
			TimeUnit.SECONDS.timedWait(this, 5);
			// wait(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Work End");
	}

}


package com.lh.concurrent;

import java.util.concurrent.TimeUnit;

public class WaitAndNotifyTest {

	public static void main(String[] args) throws InterruptedException {
		Object lock = new Object();
		TaskA ta = new TaskA(lock);
		TaskB tb = new TaskB(lock);
		
		Thread t = new Thread(() -> ta.work());
		Thread t2 = new Thread(() -> tb.work());
		t.start();
		t2.start();
		
		t.join();
		t2.join();
		
		System.out.println("END");
	}
	
}


class TaskA {
	
	private Object lock;
	
	public TaskA(Object lock) {
		this.lock = lock;
	}

	public void work() {
		System.out.println("A starts to work...");
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		synchronized (lock) {
			try {
				System.out.println("A is waiting for B ...");
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("A ends to work...");
	}
}

class TaskB {
	
	private Object lock;
	
	public TaskB(Object lock) {
		this.lock = lock;
	}

	public void work() {
		System.out.println("B starts to work...");
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		synchronized (lock) {
			lock.notify();
			System.out.println("B notifies A");
		}
		
		System.out.println("B ends to work...");
	}
}

package com.lh.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreTest {

	public static void main(String[] args) {
		WC wc = new WC();

		new Thread(() -> wc.use()).start();
		new Thread(() -> wc.use()).start();
		new Thread(() -> wc.use()).start();
		
		new Thread(() -> wc.use()).start();
		new Thread(() -> wc.use()).start();
	}
}

class WC {
	private Semaphore semaphore = new Semaphore(3);// 最大线程许可量

	public void use() {
		try {
			// 获得许可
			semaphore.acquire();
			System.out.println(Thread.currentThread().getName() + " 正在使用卫生间");
			TimeUnit.SECONDS.sleep(3);
			System.out.println(Thread.currentThread().getName() + " 使用完毕");

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 释放许可
			semaphore.release();
		}
	}
}

package com.lh.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumerModelBlockQueueImpl {

	public static void main(String[] args) {
		final int maxSize = 10;// 产品最大库存量
		BlockingQueue<Product> buffer = new LinkedBlockingQueue<Product>(
				maxSize);
		ExecutorService es = Executors.newFixedThreadPool(5);

		// 两个生产者
		es.execute(new Producer(buffer));
		es.execute(new Producer(buffer));

		// 三个消费者
		es.execute(new Consumer(buffer));
		es.execute(new Consumer(buffer));
		es.execute(new Consumer(buffer));

		es.shutdown();

	}

	// 产品
	static class Product {
		private String name;

		public Product(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	// 生产者
	static class Producer implements Runnable {
		private BlockingQueue<Product> buffer;

		public Producer(BlockingQueue<Product> buffer) {
			this.buffer = buffer;
		}

		public void run() {
			while (true) {
				Product product = new Product("MAC");
				try {
					buffer.put(product);
					System.out.println("生产者["
							+ Thread.currentThread().getName() + "]生产了一个产品："
							+ product);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 消费者
	static class Consumer implements Runnable {
		private BlockingQueue<Product> buffer;

		public Consumer(BlockingQueue<Product> buffer) {
			this.buffer = buffer;
		}

		public void run() {
			while (true) {
				try {
					System.out.println("消费者["
							+ Thread.currentThread().getName() + "]消费了一个产品："
							+ buffer.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}



package com.lh.concurrent;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockTest {

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private String content = "Old";

	public void write() {
		lock.writeLock().lock();
		System.out.println(Thread.currentThread() + " LOCK");
		try {
			try {
				// 模拟方法需要执行100毫秒
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			content = "New";
			System.out.println(Thread.currentThread() + " Write content to: "
					+ content);
		} finally {
			System.out.println(Thread.currentThread() + " UNLOCK");
			lock.writeLock().unlock();

		}

	}

	public void read() {
		lock.readLock().lock();
		System.out.println(Thread.currentThread() + " LOCK");
		try {
			try {
				// 模拟方法需要执行100毫秒
				Thread.sleep(100);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread() + " Read content is: "
					+ content);
		} finally {
			System.out.println(Thread.currentThread() + " UNLOCK");
			lock.readLock().unlock();
		}

	}

	public static void main(String[] args) {
		final ReentrantReadWriteLockTest test = new ReentrantReadWriteLockTest();
		// 使用Java 8 lambda 简化代码
		new Thread(() -> test.write()).start();
		new Thread(() -> test.read()).start();
		new Thread(() -> test.read()).start();

	}

}


package com.lh.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {

	private final Lock lock = new ReentrantLock();

	private String content = "Old";

	public void write() {
		lock.lock();
		// 由于ReentrantLock是可重入锁，所以可以重复的加锁。
		// lock.lock();
		System.out.println(Thread.currentThread() + " LOCK");
		try {
			try {
				// 模拟方法需要执行100毫秒
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			content = "New";
			System.out.println(Thread.currentThread() + " Write content to: "
					+ content);
		} finally {
			System.out.println(Thread.currentThread() + " UNLOCK");
			lock.unlock();
			// 进行多少次加锁操作，也需要对应多少次解锁操作。

		}

	}

	public void read() {
		lock.lock();
		System.out.println(Thread.currentThread() + " LOCK");
		try {
			try {
				// 模拟方法需要执行100毫秒
				Thread.sleep(100);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread() + " Read content is: "
					+ content);
		} finally {
			System.out.println(Thread.currentThread() + " UNLOCK");
			lock.unlock();
		}

	}

	public static void main(String[] args) {
		final ReentrantLockTest test = new ReentrantLockTest();
		// 使用Java 8 lambda 简化代码
		new Thread(() -> test.write()).start();
		new Thread(() -> test.read()).start();
		new Thread(() -> test.read()).start();

	}

}


package com.lh.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {

	// 定义一个barrier并设置parties，当线程数达到parties后，barrier失效，线程可以继续运行，在未达到parties值之前，线程将持续等待。
	static CyclicBarrier barrier = new CyclicBarrier(3,
			() -> System.out.println("栅栏：“这么多猪，我恐怕扛不住了”"));

	static void go() {
		System.out.println("小猪[" + Thread.currentThread().getName()
				+ "] 在栅栏边等待其他小猪");
		try {
			barrier.await();// 等待数+1
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		System.out.println("猪到齐了，小猪[" + Thread.currentThread().getName()
				+ "] 与其他小猪一起冲破栅栏");
	}

	public static void main(String[] args) {
		new Thread(() -> go()).start();
		new Thread(() -> go()).start();
		new Thread(() -> go()).start();
	}

}


package com.lh.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionTest {

	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();

	public void work() {
		lock.lock();
		try {
			try {
				System.out.println("Begin Work");
				condition.await();
				System.out.println("Begin End");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			lock.unlock();
		}
	}

	public void continueWork() {
		lock.lock();
		try {
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ConditionTest test = new ConditionTest();
		new Thread(() -> test.work()).start();

		// 等待3000毫秒后唤醒，继续工作。
		Thread.sleep(3000);
		test.continueWork();
	}
}


package com.lh.concurrent;

public class AtomicityProblemTest {
	public static int sharedValue;
	//每次将sharedValue的值增加10
	
	public static void increment() {
		for (int i = 0; i < 10; i++) {
			sharedValue++;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		int maxThreads = 10000;
		for (int i = 0; i < maxThreads; i++) {
			Thread thread = new Thread(() -> increment());
			thread.start();
		}
		Thread.sleep(3000);//等待所有子线程执行完成
		System.out.println(sharedValue);
	}
}


package com.lh.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerTest {

	public static AtomicInteger sharedValue = new AtomicInteger();

	// 每次将sharedValue的值增加10

	public static void increment() {
		for (int i = 0; i < 10; i++) {
			sharedValue.incrementAndGet();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		int maxThreads = 10000;
		for (int i = 0; i < maxThreads; i++) {
			Thread thread = new Thread(() -> increment());
			thread.start();
		}
		Thread.sleep(3000);// 等待所有子线程执行完成
		System.out.println(sharedValue);
	}
}


