package com.appspot.twitteybot.ui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.appspot.twitteybot.feeds.FeedReader;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Freemaker templates helper file that has the configutations and performs the
 * conversion Called by servlets
 */
public class FreeMarkerConfiguration {

    private static final String DIR_TEMPLATES = "templates";
    private static final Logger log = Logger.getLogger(FeedReader.class.getName());

    private static Configuration get() {
	Configuration cfg = new Configuration();
	try {
	    cfg.setDirectoryForTemplateLoading(new File(FreeMarkerConfiguration.DIR_TEMPLATES));
	    cfg.setObjectWrapper(new DefaultObjectWrapper());
	    log.log(Level.INFO, "Free marker templates directory", DIR_TEMPLATES);
	} catch (IOException e) {
	    log.log(Level.SEVERE, "Error with free marker directory", e);
	}
	return cfg;
    }

    /**
     * Proceses the template file and and the property map
     * 
     * @param props
     *            List of rootMap as required by freemarker
     * @param templateFile
     *            name of the template file
     * @return String with properties substituted
     * @throws IOException
     * @throws TemplateException
     * @throws TemplateException
     */
    public static String getProcessedTemplate(Map<String, ?> props, String templateFile) throws TemplateException,
	    IOException {
	Writer out = new StringWriter();
	Configuration cfg = FreeMarkerConfiguration.get();
	Template template = cfg.getTemplate(templateFile);
	template.process(props, out);
	return out.toString();
    }

    /**
     * Convinence method called by Servlets. Writes the results to the writer of
     * the servlet response
     * 
     * @param templateValues
     * @param templateFile
     * @param writer
     */
    public static void writeResponse(Map<String, Object> templateValues, String templateFile, PrintWriter writer) {
	try {
	    writer.write(getProcessedTemplate(templateValues, templateFile));
	} catch (TemplateException e) {
	    log.log(Level.WARNING, templateFile, e);
	    e.printStackTrace(writer);
	} catch (IOException e) {
	    log.log(Level.WARNING, templateFile, e);
	    e.printStackTrace(writer);
	}
    }
}
