package com.jvmutil.mbean;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;


/**
 * @author Robert HG (254963746@qq.com) on 9/15/15.
 */
public class JVMInfo implements JVMInfoMBean {

    private static final JVMInfo instance = new JVMInfo();

    public static JVMInfo getInstance() {
        return instance;
    }

    private RuntimeMXBean runtimeMXBean;
    private ClassLoadingMXBean classLoadingMXBean;
    private CompilationMXBean compilationMXBean;
    private Properties properties;
    private String inputArguments;
    private int availableProcessors = 0;
    private String pid;

    private JVMInfo() {
        classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        compilationMXBean = ManagementFactory.getCompilationMXBean();
        runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        properties = System.getProperties();
    }

    private String getSystemProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public Date getStartTime() {
        return new Date(runtimeMXBean.getStartTime());
    }

    @Override
    public String getJVM() {
        return runtimeMXBean.getVmName() + " (" + runtimeMXBean.getVmVersion() + ", "
                + getSystemProperty("java.vm.info") + ")";
    }

    @Override
    public String getJavaVersion() {
        return getSystemProperty("java.version");
    }

    @Override
    public String getPID() {
        if (null == pid) {
            pid = runtimeMXBean.getName().split("@")[0];
        }
        return pid;
    }

    @Override
    public String getInputArguments() {
        if (null == inputArguments) {
            inputArguments = runtimeMXBean.getInputArguments().toString();
        }
        return inputArguments;
    }

    @Override
    public String getJavaHome() {
        return getSystemProperty("java.home");
    }

    @Override
    public String getArch() {
        return getSystemProperty("os.arch");
    }

    @Override
    public String getOSName() {
        return getSystemProperty("os.name");
    }

    @Override
    public String getOSVersion() {
        return getSystemProperty("os.version");
    }

    @Override
    public String getJavaSpecificationVersion() {
        return getSystemProperty("java.specification.version");
    }

    @Override
    public String getJavaLibraryPath() {
        return getSystemProperty("java.library.path");
    }

    @Override
    public String getFileEncode() {
        return getSystemProperty("file.encoding");
    }

    @Override
    public int getAvailableProcessors() {
        if (availableProcessors == 0) {
            availableProcessors = Runtime.getRuntime().availableProcessors();
        }
        return availableProcessors;
    }

    @Override
    public int getLoadedClassCount() {
        return classLoadingMXBean.getLoadedClassCount();
    }

    @Override
    public long getTotalLoadedClassCount() {
        return classLoadingMXBean.getTotalLoadedClassCount();
    }

    @Override
    public long getUnloadedClassCount() {
        return classLoadingMXBean.getUnloadedClassCount();
    }

    @Override
    public long getTotalCompilationTime() {
        return compilationMXBean.getTotalCompilationTime();
    }

   /* @Override
    public String getHostName() {
        return NetUtils.getLocalHostName();
    }

    @Override
    public String getLocalIp() {
        return NetUtils.getLocalHost();
    }
*/
    

	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();
		try {
			Class<? extends JVMInfo> clazz = this.getClass();
			// 获取当前类的全部属性
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				// 遍历属性得到属性名
				String fieldName = field.getName();
				// 如果是用于序列化的直接过滤掉
				if ("serialVersionUID".equals(fieldName)) {
					continue;
				}
				// 判断属性的类型，主要是区分boolean和其他类型
				Class<?> type = field.getType();

				// boolean的取值是is,其他是get
				String methodName = (type.getTypeName().equals("boolean") ? "is" : "get")
						+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
				
				// 通过方法名得到方法对象
				Method method = clazz.getMethod(methodName);
				// 得到这个方法的返回值
				Object resultObj = method.invoke(this);
				if (resultObj != null && !"".equals(resultObj)) {
					result.append(fieldName).append(":").append(resultObj).append("|");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
	}

}
