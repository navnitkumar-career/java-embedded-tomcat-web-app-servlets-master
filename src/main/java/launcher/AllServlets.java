package launcher;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AllServlets { //scans and registers all servlets

    static int defaultPort = 8080;
    static boolean disableJsp = false;

    public static void main(String[] args) throws Exception {
        System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(defaultPort);
        if (disableJsp) {
            tomcat.setAddDefaultWebXmlToWebapp(false); //disable jasper/jsp
        }

        registerWebXml(tomcat);

        //finally start!
        tomcat.getConnector(); //we need to manually call in tomcat 9+
        tomcat.start();
        tomcat.getServer().await();
    }

    static void registerWebXml(Tomcat tomcat) throws IOException {
        //programmatic web.xml registration:::

        Path tempPath = Files.createTempDirectory("tomcat-base-dir");
        tomcat.setBaseDir(tempPath.toString());

        File root = getCurrentAppPath();
        File webContentFolder = getWebContentFolder(root);

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webContentFolder.getAbsolutePath());
        ctx.setParentClassLoader(AllServlets.class.getClassLoader());

        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(addResources(root, resources));
        ctx.setResources(resources);
    }

    private static WebResourceSet addResources(File root, WebResourceRoot resources) {
        File additionWebInfClassesFolder = new File(root.getAbsolutePath(), "target/classes");
        WebResourceSet resourceSet;
        if (additionWebInfClassesFolder.exists()) {
            resourceSet = new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClassesFolder.getAbsolutePath(), "/");
            System.out.println("Loading WEB-INF resources from as '" + additionWebInfClassesFolder.getAbsolutePath() + "'");
        } else {
            resourceSet = new EmptyResourceSet(resources);
        }

        return resourceSet;
    }

    static File getWebContentFolder(File root) throws IOException {
        //jsp/html/css/js etc
        File webContentFolder = new File(root.getAbsolutePath(), "src/main/webapp/");
        if (!webContentFolder.exists()) {
            webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
        }
        System.out.println("Configuring app with basedir: " + webContentFolder.getAbsolutePath());

        return webContentFolder;
    }

    private static File getCurrentAppPath() {
        try {
            File root;
            String runningJarPath = AllServlets.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
            int lastIndexOf = runningJarPath.lastIndexOf("/target/");
            if (lastIndexOf < 0) {
                root = new File("");
            } else {
                root = new File(runningJarPath.substring(0, lastIndexOf));
            }
            System.out.println("Application resolved root folder: " + root.getAbsolutePath());
            return root;
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

}
