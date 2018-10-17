package org.jahia.loganalyzer.itests;

/*
 * #%L
 * Jahia Log Analyzer
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.karaf.features.BootFinished;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.junit.Assert;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

import javax.inject.Inject;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;
import java.io.*;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

/**
 * Created by loom on 27.07.15.
 */
public abstract class BaseIT {

    public static final String RMI_SERVER_PORT = "44445";
    public static final String HTTP_PORT = "9081";
    public static final String RMI_REG_PORT = "1100";
    protected static final String URL = "http://localhost:" + HTTP_PORT;
    static final Long COMMAND_TIMEOUT = 30000L;
    static final Long SERVICE_TIMEOUT = 30000L;
    static final long BUNDLE_TIMEOUT = 30000L;
    @Inject
    protected BundleContext bundleContext;
    @Inject
    protected FeaturesService featureService;
    @Inject
    protected SessionFactory sessionFactory;
    ExecutorService executor = Executors.newCachedThreadPool();
    /**
     * To make sure the tests run only when the boot features are fully installed
     */
    @Inject
    BootFinished bootFinished;

    /*
    * Explode the dictionary into a ,-delimited list of key=value pairs
    */
    @SuppressWarnings("rawtypes")
    private static String explode(Dictionary dictionary) {
        Enumeration keys = dictionary.keys();
        StringBuffer result = new StringBuffer();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            result.append(String.format("%s=%s", key, dictionary.get(key)));
            if (keys.hasMoreElements()) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    /**
     * Provides an iterable collection of references, even if the original array is null
     */
    @SuppressWarnings("rawtypes")
    private static Collection<ServiceReference> asCollection(ServiceReference[] references) {
        return references != null ? Arrays.asList(references) : Collections.<ServiceReference>emptyList();
    }

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*,org.apache.http.client.methods;status=provisional");
        return probe;
    }

    public File getConfigFile(String path) {
        java.net.URL res = this.getClass().getResource(path);
        if (res == null) {
            throw new RuntimeException("Config resource " + path + " not found");
        }
        return new File(res.getFile());
    }

    @Configuration
    public Option[] config() {
        MavenArtifactUrlReference karafDistributionUrl = maven()
                .groupId("org.apache.karaf")
                .artifactId("apache-karaf")
                .versionAsInProject()
                .type("tar.gz");
        MavenUrlReference karafStandardFeaturesUrl = maven()
                .groupId("org.apache.karaf.features")
                .artifactId("standard")
                .classifier("features")
                .type("xml")
                .versionAsInProject();
        MavenUrlReference karafCxfRepo = maven()
                .groupId("org.apache.cxf.karaf")
                .artifactId("apache-cxf")
                .classifier("features")
                .type("xml")
                .versionAsInProject();
        MavenUrlReference checkerRepo = maven()
                .groupId("org.jahia.loganalyzer")
                .artifactId("loganalyzer-karaf")
                .classifier("features")
                .type("xml")
                .versionAsInProject();
        List<Option> options = new ArrayList<>();
        if (System.getProperty("karafDebug") != null) {
            options.add(KarafDistributionOption.debugConfiguration("6005", true));
        }
        options.add(karafDistributionConfiguration().frameworkUrl(karafDistributionUrl).name("Apache Karaf").unpackDirectory(new File("target/exam")));
        options.add(features(karafCxfRepo, "cxf"));
        options.add(features(karafStandardFeaturesUrl, "scheduler"));
        options.add(features(karafStandardFeaturesUrl, "aries-blueprint"));
        options.add(features(checkerRepo, "loganalyzer-karaf"));
        // enable JMX RBAC security, thanks to the KarafMBeanServerBuilder
        options.add(configureSecurity().disableKarafMBeanServerBuilder());
        options.add(keepRuntimeFolder());
        options.add(logLevel(LogLevel.INFO));
        options.add(systemProperty("org.jahia.loganalyzer.itests.elasticsearch.transport.port").value("9500"));
        options.add(systemProperty("org.jahia.loganalyzer.itests.elasticsearch.cluster.name").value("logAnalyzerITests"));

        options.add(replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", getConfigFile("/etc/org.ops4j.pax.logging.cfg")));
        options.add(editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", HTTP_PORT));
        options.add(editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", RMI_REG_PORT));
        options.add(editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", RMI_SERVER_PORT));
        return options.toArray(new Option[options.size()]);
    }

    /**
     * Executes a shell command and returns output as a String.
     * Commands have a default timeout of 10 seconds.
     *
     * @param command    The command to execute
     * @param principals The principals (e.g. RolePrincipal objects) to run the command under
     * @return
     */
    protected String executeCommand(final String command, Principal... principals) {
        return executeCommand(command, COMMAND_TIMEOUT, false, principals);
    }

    /**
     * Executes a shell command and returns output as a String.
     * Commands have a default timeout of 10 seconds.
     *
     * @param command    The command to execute.
     * @param timeout    The amount of time in millis to wait for the command to execute.
     * @param silent     Specifies if the command should be displayed in the screen.
     * @param principals The principals (e.g. RolePrincipal objects) to run the command under
     * @return
     */
    protected String executeCommand(final String command, final Long timeout, final Boolean silent, final Principal... principals) {
        waitForCommandService(command);

        String response;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        final SessionFactory sessionFactory = getOsgiService(SessionFactory.class);
        final Session session = sessionFactory.create(System.in, printStream, System.err);

        final Callable<String> commandCallable = new Callable<String>() {
            public String call() throws Exception {
                try {
                    if (!silent) {
                        System.err.println(command);
                    }
                    session.execute(command);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                printStream.flush();
                return byteArrayOutputStream.toString();
            }
        };

        FutureTask<String> commandFuture;
        if (principals.length == 0) {
            commandFuture = new FutureTask<String>(commandCallable);
        } else {
            // If principals are defined, run the command callable via Subject.doAs()
            commandFuture = new FutureTask<String>(new Callable<String>() {
                public String call() throws Exception {
                    Subject subject = new Subject();
                    subject.getPrincipals().addAll(Arrays.asList(principals));
                    return Subject.doAs(subject, new PrivilegedExceptionAction<String>() {
                        public String run() throws Exception {
                            return commandCallable.call();
                        }
                    });
                }
            });
        }

        try {
            executor.submit(commandFuture);
            response = commandFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace(System.err);
            response = "SHELL COMMAND TIMED OUT: ";
        } catch (ExecutionException e) {
            Throwable cause = e.getCause().getCause();
            throw new RuntimeException(cause.getMessage(), cause);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return response;
    }

    protected <T> T getOsgiService(Class<T> type, long timeout) {
        return getOsgiService(type, null, timeout);
    }

    protected <T> T getOsgiService(Class<T> type) {
        return getOsgiService(type, null, SERVICE_TIMEOUT);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <T> T getOsgiService(Class<T> type, String filter, long timeout) {
        ServiceTracker tracker = null;
        try {
            String flt;
            if (filter != null) {
                if (filter.startsWith("(")) {
                    flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")" + filter + ")";
                } else {
                    flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")(" + filter + "))";
                }
            } else {
                flt = "(" + Constants.OBJECTCLASS + "=" + type.getName() + ")";
            }
            Filter osgiFilter = FrameworkUtil.createFilter(flt);
            tracker = new ServiceTracker(bundleContext, osgiFilter, null);
            tracker.open(true);
            // Note that the tracker is not closed to keep the reference
            // This is buggy, as the service reference may change i think
            Object svc = type.cast(tracker.waitForService(timeout));
            if (svc == null) {
                Dictionary dic = bundleContext.getBundle().getHeaders();
                System.err.println("Test bundle headers: " + explode(dic));

                for (ServiceReference ref : asCollection(bundleContext.getAllServiceReferences(null, null))) {
                    System.err.println("ServiceReference: " + ref);
                }

                for (ServiceReference ref : asCollection(bundleContext.getAllServiceReferences(null, flt))) {
                    System.err.println("Filtered ServiceReference: " + ref);
                }

                throw new RuntimeException("Gave up waiting for service " + flt);
            }
            return type.cast(svc);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid filter", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForCommandService(String command) {
        // the commands are represented by services. Due to the asynchronous nature of services they may not be
        // immediately available. This code waits the services to be available, in their secured form. It
        // means that the code waits for the command service to appear with the roles defined.

        if (command == null || command.length() == 0) {
            return;
        }

        int spaceIdx = command.indexOf(' ');
        if (spaceIdx > 0) {
            command = command.substring(0, spaceIdx);
        }
        int colonIndx = command.indexOf(':');
        String scope = (colonIndx > 0) ? command.substring(0, colonIndx) : "*";
        String name = (colonIndx > 0) ? command.substring(colonIndx + 1) : command;
        try {
            long start = System.currentTimeMillis();
            long cur = start;
            while (cur - start < SERVICE_TIMEOUT) {
                if (sessionFactory.getRegistry().getCommand(scope, name) != null) {
                    return;
                }
                Thread.sleep(100);
                cur = System.currentTimeMillis();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void waitForService(String filter, long timeout) throws InvalidSyntaxException, InterruptedException {
        ServiceTracker<Object, Object> st = new ServiceTracker<Object, Object>(bundleContext, bundleContext.createFilter(filter), null);
        try {
            st.open();
            st.waitForService(timeout);
        } finally {
            st.close();
        }
    }

    protected Bundle waitBundleState(String symbolicName, int state) {
        long endTime = System.currentTimeMillis() + BUNDLE_TIMEOUT;
        while (System.currentTimeMillis() < endTime) {
            Bundle bundle = findBundleByName(symbolicName);
            if (bundle != null && bundle.getState() == state) {
                return bundle;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        Assert.fail("Manadatory bundle " + symbolicName + " not found.");
        throw new IllegalStateException("Should not be reached");
    }

    public JMXConnector getJMXConnector() throws Exception {
        return getJMXConnector("karaf", "karaf");
    }

    public JMXConnector getJMXConnector(String userName, String passWord) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + RMI_REG_PORT + "/karaf-root");
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        String[] credentials = new String[]{userName, passWord};
        env.put("jmx.remote.credentials", credentials);
        JMXConnector connector = JMXConnectorFactory.connect(url, env);
        return connector;
    }

    public void assertFeatureInstalled(String featureName) throws Exception {
        String name;
        String version;
        if (featureName.contains("/")) {
            name = featureName.substring(0, featureName.indexOf("/"));
            version = featureName.substring(featureName.indexOf("/") + 1);
        } else {
            name = featureName;
            version = null;
        }
        assertFeatureInstalled(name, version);
    }

    public void assertFeatureInstalled(String featureName, String featureVersion) throws Exception {
        Feature featureToAssert = featureService.getFeatures(featureName, featureVersion)[0];
        Feature[] features = featureService.listInstalledFeatures();
        for (Feature feature : features) {
            if (featureToAssert.equals(feature)) {
                return;
            }
        }
        Assert.fail("Feature " + featureName + (featureVersion != null ? "/" + featureVersion : "") + " should be installed but is not");
    }

    public void assertFeaturesInstalled(String... expectedFeatures) throws Exception {
        Set<String> expectedFeaturesSet = new HashSet<String>(Arrays.asList(expectedFeatures));
        Feature[] features = featureService.listInstalledFeatures();
        Set<String> installedFeatures = new HashSet<String>();
        for (Feature feature : features) {
            installedFeatures.add(feature.getName());
        }
        String msg = "Expecting the following features to be installed : " + expectedFeaturesSet + " but found " + installedFeatures;
        Assert.assertTrue(msg, installedFeatures.containsAll(expectedFeaturesSet));
    }

    public void assertFeatureNotInstalled(String featureName) throws Exception {
        String name;
        String version;
        if (featureName.contains("/")) {
            name = featureName.substring(0, featureName.indexOf("/"));
            version = featureName.substring(featureName.indexOf("/") + 1);
        } else {
            name = featureName;
            version = null;
        }
        assertFeatureNotInstalled(name, version);
    }

    public void assertFeatureNotInstalled(String featureName, String featureVersion) throws Exception {
        Feature featureToAssert = featureService.getFeatures(featureName, featureVersion)[0];
        Feature[] features = featureService.listInstalledFeatures();
        for (Feature feature : features) {
            if (featureToAssert.equals(feature)) {
                Assert.fail("Feature " + featureName + (featureVersion != null ? "/" + featureVersion : "") + " is installed whereas it should not be");
            }
        }
    }

    public void assertContains(String expectedPart, String actual) {
        assertTrue("Should contain '" + expectedPart + "' but was : " + actual, actual.contains(expectedPart));
    }

    public void assertContainsNot(String expectedPart, String actual) {
        Assert.assertFalse("Should not contain '" + expectedPart + "' but was : " + actual, actual.contains(expectedPart));
    }

    protected void assertBundleInstalled(String name) {
        Assert.assertNotNull("Bundle " + name + " should be installed", findBundleByName(name));
    }

    protected void assertBundleNotInstalled(String name) {
        Assert.assertNull("Bundle " + name + " should not be installed", findBundleByName(name));
    }

    protected Bundle findBundleByName(String symbolicName) {
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getSymbolicName().equals(symbolicName)) {
                return bundle;
            }
        }
        return null;
    }

    protected void installAndAssertFeature(String feature) throws Exception {
        featureService.installFeature(feature, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
        assertFeatureInstalled(feature);
    }

    protected void installAssertAndUninstallFeature(String feature, String version) throws Exception {
        installAssertAndUninstallFeatures(feature + "/" + version);
    }

    protected void installAssertAndUninstallFeatures(String... feature) throws Exception {
        boolean success = false;
        try {
            for (String curFeature : feature) {
                featureService.installFeature(curFeature, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
                assertFeatureInstalled(curFeature);
            }
            success = true;
        } finally {
            for (String curFeature : feature) {
                System.out.println("Uninstalling " + curFeature);
                try {
                    featureService.uninstallFeature(curFeature, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
                } catch (Exception e) {
                    if (success) {
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * The feature service does not uninstall feature dependencies when uninstalling a single feature.
     * So we need to make sure we uninstall all features that were newly installed.
     *
     * @param featuresBefore
     * @throws Exception
     */
    protected void uninstallNewFeatures(Set<Feature> featuresBefore)
            throws Exception {
        Feature[] features = featureService.listInstalledFeatures();
        for (Feature curFeature : features) {
            if (!featuresBefore.contains(curFeature)) {
                try {
                    System.out.println("Uninstalling " + curFeature.getName());
                    featureService.uninstallFeature(curFeature.getName(), curFeature.getVersion(),
                            EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void close(Closeable closeAble) {
        if (closeAble != null) {
            try {
                closeAble.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

}
