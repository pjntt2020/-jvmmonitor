import com.google.gson.Gson;
import com.jvmutil.JVMCollector;
import com.jvmutil.JVMMonitor;

import java.util.TimerTask;
import java.util.concurrent.locks.LockSupport;

public class jvmtest {
	public static void main(String[] args) {
		final Gson gson = new Gson();
		JVMMonitor.start();

		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// System.out.println(gson.toJson(JVMCollector.collect()));

				System.out.println("JVMMONITOR"
						.concat("|")
						.concat("|")
						.concat(JVMCollector.collect().getMemoryMap().toString())
						.concat("|")
						.concat(JVMCollector.collect().getGcMap().toString())
						.concat("|")
						.concat(JVMCollector.collect().getThreadMap().toString()).replace("}", "").replace("{", "").replace("=", ":").replace(", ", "|"));
			}
		}, 1000, 5000);

		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println(">>>>>>>>>>");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, 3000, 3000);

		LockSupport.park();
		JVMMonitor.stop();

	}
}
