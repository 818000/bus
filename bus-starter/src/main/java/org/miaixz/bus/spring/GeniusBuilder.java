/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.spring;

import java.io.File;
import java.time.Duration;

import org.miaixz.bus.core.lang.Keys;
import org.miaixz.bus.core.lang.Symbol;
import org.miaixz.bus.core.xyz.ClassKit;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * 全局常量配置
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class GeniusBuilder {

    /***
     * Bus banner for miaixz.org
     */
    public static final String[] BUS_BANNER = {
            " ███╗   ███╗██╗ █████╗ ██╗██╗  ██╗███████╗    ██████╗ ██████╗  ██████╗  ",
            " ████╗ ████║██║██╔══██╗██║╚██╗██╔╝╚══███╔╝   ██╔═══██╗██╔══██╗██╔════╝  ",
            " ██╔████╔██║██║███████║██║ ╚███╔╝   ███╔╝    ██║   ██║██████╔╝██║  ███╗ ",
            " ██║╚██╔╝██║██║██╔══██║██║ ██╔██╗  ███╔╝     ██║   ██║██╔══██╗██║   ██║ ",
            " ██║ ╚═╝ ██║██║██║  ██║██║██╔╝ ██╗███████╗██╗╚██████╔╝██║  ██║╚██████╔╝ ",
            " ╚═╝     ╚═╝╚═╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═╝ ╚═════╝  " };

    /***
     * Bus boot banner
     */
    public static final String BUS_BOOT_BANNER = " :: Bus Boot :: ";

    /**
     * Spring boot banner
     */
    public static final String SPRING_BOOT_BANNER = " :: Spring Boot :: ";

    public static final String SPRING_CONTEXT_PATH = "META-INF/spring";

    public static final String DEFAULT_PROFILE_VALUE = "default";

    /** module.properties keywords **/
    public static final String SPRING_PARENT = "Spring-Parent";

    public static final String MODULE_NAME = "Module-Name";

    public static final String REQUIRE_MODULE = "Require-Module";

    public static final String MODULE_PROFILE = "Module-Profile";

    /**
     * Bus config property source key
     */
    public static final String BUS_PROPERTY_SOURCE = "configurationProperties";

    /**
     * Bus priority config key
     */
    public static final String BUS_HIGH_PRIORITY = "priorityConfig";

    /**
     * Bus scenes key
     */
    public static final String BUS_SCENES = Keys.BUS + Symbol.DOT + "scenes";

    /**
     * Bus scenes path
     */
    public static final String BUS_SCENES_PATH = Keys.BUS + File.separator + "scenes";

    /**
     * Bus logging path
     */
    public static final String BUS_LOGGING_PATH = File.separator + "logs";

    /**
     * Logging path key
     */
    public static final String LOGGING_PATH = "logging.path";

    /**
     * Logging path prefix
     */
    public static final String LOGGING_PATH_PREFIX = "logging.path.";

    /**
     * Logging level key
     */
    public static final String LOGGING_LEVEL = "logging.level";

    /**
     * Logging level prefix
     */
    public static final String LOGGING_LEVEL_PREFIX = "logging.level.";

    /**
     * Logging pattern console key
     */
    public static final String LOGGING_PATTERN_CONSOLE = "logging.pattern.console";

    /**
     * Logging pattern file key
     */
    public static final String LOGGING_PATTERN_FILE = "logging.pattern.file";

    /**
     * Spring banner file
     */
    public static final String SPRING_BANNER_TXT = "banner.txt";

    /**
     * Spring banner location
     */
    public static final String SPRING_BANNER_LOCATION = "spring.banner.location";

    /**
     * bus startup logging extra info
     */
    public static final String BUS_SWITCH_LISTENER_PREFIX_ = "bus.switch.listener.";

    /**
     * Bus banner key
     */
    public static final String BANNER = Keys.BUS + Symbol.DOT + "banner";

    /**
     * Bus cache key
     */
    public static final String CACHE = Keys.BUS + Symbol.DOT + "cache";

    /**
     * Bus bridge key
     */
    public static final String BRIDGE = Keys.BUS + Symbol.DOT + "bridge";

    /**
     * Bus cors key
     */
    public static final String CORS = Keys.BUS + Symbol.DOT + "cors";

    /**
     * Bus druid key
     */
    public static final String DRUID = Keys.BUS + Symbol.DOT + "druid";

    /**
     * Bus dubbo key
     */
    public static final String DUBBO = Keys.BUS + Symbol.DOT + "dubbo";

    /**
     * Bus elastic key
     */
    public static final String ELASTIC = Keys.BUS + Symbol.DOT + "elastic";

    /**
     * Bus vortex key
     */
    public static final String VORTEX = Keys.BUS + Symbol.DOT + "vortex";

    /**
     * Bus vortex key
     */
    public static final String HEALTH = Keys.BUS + Symbol.DOT + "health";

    /**
     * Bus i18n key
     */
    public static final String I18N = Keys.BUS + Symbol.DOT + "i18n";

    /**
     * Bus image key
     */
    public static final String IMAGE = Keys.BUS + Symbol.DOT + "image";

    /**
     * Bus limiter key
     */
    public static final String LIMITER = Keys.BUS + Symbol.DOT + "limiter";

    /**
     * Bus mongo key
     */
    public static final String MONGO = Keys.BUS + Symbol.DOT + "mongo";

    /**
     * Bus mybatis key
     */
    public static final String MYBATIS = Keys.BUS + Symbol.DOT + "mybatis";

    /**
     * Bus notify key
     */
    public static final String NOTIFY = Keys.BUS + Symbol.DOT + "notify";

    /**
     * Bus auth key
     */
    public static final String AUTH = Keys.BUS + Symbol.DOT + "auth";

    /**
     * Bus office key
     */
    public static final String OFFICE = Keys.BUS + Symbol.DOT + "office";

    /**
     * Bus pay key
     */
    public static final String PAY = Keys.BUS + Symbol.DOT + "pay";

    /**
     * Bus sensitive key
     */
    public static final String SENSITIVE = Keys.BUS + Symbol.DOT + "sensitive";

    /**
     * Bus socket key
     */
    public static final String SOCKET = Keys.BUS + Symbol.DOT + "socket";

    /**
     * Bus storage key
     */
    public static final String STORAGE = Keys.BUS + Symbol.DOT + "storage";

    /**
     * Bus wrapper key
     */
    public static final String WRAPPER = Keys.BUS + Symbol.DOT + "wrapper";

    /**
     * Bus zookeeper key
     */
    public static final String ZOOKEEPER = Keys.BUS + Symbol.DOT + "zookeeper";

    /**
     * Bus temp work key
     */
    public static final String WORK = Keys.BUS + Symbol.DOT + "work";

    /***
     * Spring application name key
     */
    public static final String APP_NAME = "spring.application.name";

    /**
     * Spring datasource key
     */
    public static final String DATASOURCE = "spring.datasource";

    /**
     * SpringCloud property source key
     */
    public static final String CLOUD_BOOTSTRAP = "bootstrap";

    /**
     * Property name for bootstrap configuration class name.
     */
    public static final String CLOUD_BOOTSTRAP_CONFIGURATION_CLASS = "org.springframework.cloud.bootstrap.BootstrapConfiguration";

    /**
     * The running stage since JVM started to
     * {@link SpringApplicationRunListener#started(ConfigurableApplicationContext, Duration)} ()}
     */
    public static final String JVM_STARTING_STAGE = "JvmStartingStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#started(ConfigurableApplicationContext, Duration)}
     * ()} to
     * {@link SpringApplicationRunListener#environmentPrepared(ConfigurableBootstrapContext, ConfigurableEnvironment)}
     * (ConfigurableEnvironment)}}
     */
    public static final String ENVIRONMENT_PREPARE_STAGE = "EnvironmentPrepareStage";

    /**
     * The running stage since
     * {@link SpringApplicationRunListener#environmentPrepared(ConfigurableBootstrapContext, ConfigurableEnvironment)}
     * (ConfigurableEnvironment)} to
     * {@link SpringApplicationRunListener#contextPrepared(ConfigurableApplicationContext)}}
     */
    public static final String APPLICATION_CONTEXT_PREPARE_STAGE = "ApplicationContextPrepareStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#contextPrepared(ConfigurableApplicationContext)} to
     * {@link SpringApplicationRunListener#contextLoaded(ConfigurableApplicationContext)}}
     */
    public static final String APPLICATION_CONTEXT_LOAD_STAGE = "ApplicationContextLoadStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#contextLoaded(ConfigurableApplicationContext)} to
     * StartupContextRefreshedListener.onApplicationEvent(ContextRefreshedEvent)
     */
    public static final String APPLICATION_CONTEXT_REFRESH_STAGE = "ApplicationContextRefreshStage";

    public static final String SPRING_BEANS_INSTANTIATE = "spring.beans.instantiate";

    public static final String SPRING_BEANS_SMART_INSTANTIATE = "spring.beans.smart-initialize";

    public static final String SPRING_CONTEXT_BEANDEF_REGISTRY_POST_PROCESSOR = "spring.context.beandef-registry.post-process";

    public static final String SPRING_CONTEXT_BEAN_FACTORY_POST_PROCESSOR = "spring.context.bean-factory.post-process";

    public static final String SPRING_BEAN_POST_PROCESSOR = "spring.context.beans.post-process";

    public static final String SPRING_CONFIG_CLASSES_ENHANCE = "spring.context.config-classes.enhance";

    private static boolean LOCAL_ENV = false;

    private static boolean TEST_ENV = false;

    /**
     * Check whether spring cloud Bootstrap environment enabled
     *
     * @return true indicates spring cloud Bootstrap environment enabled
     */
    public static boolean isSpringCloudEnvironmentEnabled(Environment environment) {
        return ClassKit.isPresent(CLOUD_BOOTSTRAP_CONFIGURATION_CLASS, null) && bootstrapEnabled(environment);
    }

    public static boolean bootstrapEnabled(Environment environment) {
        return environment.getProperty("spring.cloud.bootstrap.enabled", Boolean.class, false)
                || ClassKit.isPresent("org.springframework.cloud.bootstrap.marker.Marker", null);
    }

    /**
     * Check whether import spring cloud BootstrapConfiguration
     *
     * @return true indicates spring cloud BootstrapConfiguration is imported
     */
    public static boolean isSpringCloud() {
        return ClassKit.isPresent(CLOUD_BOOTSTRAP_CONFIGURATION_CLASS, null);
    }

    /**
     * Check whether running in spring test environment
     *
     * @return true indicates in spring test environment
     */
    public static boolean isSpringTestEnv() {
        return TEST_ENV;
    }

    /**
     * Check whether running in local development environment
     *
     * @return true indicates in local development environment
     */

    public static boolean isLocalEnv() {
        return LOCAL_ENV;
    }

    private static void initSpringTestEnv() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("loadContext".equals(stackTraceElement.getMethodName())
                    && "org.springframework.boot.test.context.SpringBootContextLoader"
                            .equals(stackTraceElement.getClassName())) {
                TEST_ENV = true;
                break;
            }
        }
    }

    public static boolean isLoggingConfig(String key) {
        return isLoggingPrefix(key) || LOGGING_PATH.equals(key) || Keys.FILE_ENCODING.equals(key);
    }

    public static boolean isLoggingPrefix(String key) {
        return key.startsWith(LOGGING_LEVEL_PREFIX) || key.startsWith(LOGGING_PATH_PREFIX);
    }

    public static boolean isLoggingPattern(String key) {
        return LOGGING_PATTERN_CONSOLE.equals(key) || LOGGING_PATTERN_FILE.equals(key);
    }

}
