package launcher;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

//this registers the servlets manually
public class InlineServlet { //this won't be registered by AllServlets
    static int defaultPort = 8081;

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(defaultPort);
        tomcat.getConnector(); //we need to manually call in tomcat 9+

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());

        //register a servlet
        Tomcat.addServlet(ctx, "helloInline", new HttpServlet() {

            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response)
                    throws IOException {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/plain");

                try (Writer writer = response.getWriter()) {
                    writer.write("Hello, World!");
                    writer.flush();
                }
            }
        });
        ctx.addServletMappingDecoded("/*", "helloInline");

        tomcat.start();
        tomcat.getServer().await();
    }

}
