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

public class FreeMarkerConfiguration {

    private static final String DIR_TEMPLATES = "templates";
    private static final Logger log = Logger.getLogger(FeedReader.class.getName());

    public static Configuration get() {
	Configuration cfg = new Configuration();
	try {
	    cfg.setDirectoryForTemplateLoading(new File(FreeMarkerConfiguration.DIR_TEMPLATES));
	    cfg.setObjectWrapper(new DefaultObjectWrapper());
	} catch (IOException e) {
	    log.log(Level.SEVERE, "Error with free marker directory", e);
	}
	return cfg;
    }

    public static String getProcessedTemplate(Map<String, ?> props, String templateFile) throws TemplateException {
	Writer out = new StringWriter();
	Configuration cfg = FreeMarkerConfiguration.get();
	try {
	    Template template = cfg.getTemplate(templateFile);
	    template.process(props, out);
	} catch (IOException e) {
	    log.log(Level.WARNING, "Could not find template file", e);
	    throw new TemplateException("Could not find template file", null);
	}
	return out.toString();
    }

    public static void writeResponse(Map<String, Object> templateValues, String templateFile, PrintWriter writer) {
	try {
	    writer.write(getProcessedTemplate(templateValues, templateFile));
	} catch (TemplateException e) {
	    writer.write("Internal Server Error, please try again later");
	    writer.write(e.getMessage());
	    e.printStackTrace(writer);
	    log.log(Level.WARNING, "Template Error", e);
	}
    }
}
