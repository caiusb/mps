package bank;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Fix the deadlock below by giving up lock acquisition if
 * a certain time has passed, i.e. lock.tryLock(...)
 */

public class BankTryLock {

	public static class Account {
		private long balance;
		private Lock lock;

		public Account(long balance) {
			this.balance = balance;
			lock = new ReentrantLock();
		}

		void withdraw(long amount) {
			this.balance -= amount;
		}

		void deposit(long amount) {
			this.balance += amount;
		}

		public long getBalance() {
			return this.balance;
		}

		public Lock getLock() {
			return lock;
		}
	}

	static class CrazyTeller extends Thread {
		private final Account from;
		private final Account to;

		public CrazyTeller(Account from, Account to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public void run() {
			for (int i = 0; i < 1000; i++) {
				long amount;
				try {
					amount = nap();

					while (true) {
						from.getLock().lock();
						boolean locked = to.getLock().tryLock(10,
								TimeUnit.MILLISECONDS);
						if (locked)
							break;

						from.getLock().unlock();
					}

					from.withdraw(amount);
					to.deposit(amount);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					from.getLock().unlock();
					to.getLock().unlock();
				}
			}
		}

		long nap() throws InterruptedException {
			Thread.sleep((long) (Math.random() * 2));
			return (long) (Math.random() * 100);
		}
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		long balanceA = 100000;
		long balanceB = 10000;
		Account a = new Account(balanceA);
		Account b = new Account(balanceB);
		CrazyTeller x = new CrazyTeller(a, b);
		CrazyTeller y = new CrazyTeller(b, a);
		x.start();
		y.start();
		x.join();
		y.join();
		System.out.println("Account a:" + a.getBalance());
		System.out.println("Account b:" + b.getBalance());
		System.out.println("Checksum: "
				+ (balanceA + balanceB - a.getBalance() - b.getBalance()));
	}
}
