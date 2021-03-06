/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.appsolve.padelcampus.utils;

import de.appsolve.padelcampus.comparators.CssAttributeComparator;
import de.appsolve.padelcampus.comparators.PathByFileNameComparator;
import de.appsolve.padelcampus.constants.Constants;
import de.appsolve.padelcampus.data.CustomerI;
import de.appsolve.padelcampus.db.dao.CssAttributeBaseDAOI;
import de.appsolve.padelcampus.db.dao.CssAttributeDAOI;
import de.appsolve.padelcampus.db.dao.CustomerDAOI;
import de.appsolve.padelcampus.db.model.CssAttribute;
import de.appsolve.padelcampus.db.model.Customer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.lesscss.LessCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author dominik
 */
@Component
public class HtmlResourceUtil {

    private static final Logger LOG = Logger.getLogger(HtmlResourceUtil.class);
    private static final String VARIABLES_LESS = "/static/less/variables.less";
    private static final String PROJECT_LESS = "/static/less/90_project.less";
    private static final String PROJECT_CSS = "/static/css/90_project.css";
    private static final String LOADER_LESS = "/static/less/96_loader.less";
    private static final String LOADER_CSS = "/static/css/96_loader.css";
    private static final String BOOTSTRAP_LESS = "/static/less/10_bootstrap.less";
    private static final String BOOTSTRAP_CSS = "/static/css/10_bootstrap.css";
    private static final String FOLDER_CSS = "/static/css";
    private static final String FOLDER_LESS = "/static/less";
    private static final String ALL_MIN_CSS = "/static/css/all.min.css";
    @Autowired
    CustomerDAOI customerDAO;
    @Autowired
    CssAttributeDAOI cssAttributeDAO;
    @Autowired
    CssAttributeBaseDAOI cssAttributeBaseDAO;
    private LessCompiler lessCompiler;

    public void updateCss(final ServletContext context) throws Exception {
        List<Customer> customers = customerDAO.findAll();
        if (customers.isEmpty()) {
            applyCustomerCss(context, getDefaultCssAttributes(), "");
        } else {
            lessCompiler = new LessCompiler();
            lessCompiler.init();
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            LOG.info(String.format("Compiling lesscss with %s cores", availableProcessors));
            ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);
            List<FutureTask<Void>> taskList = new ArrayList<>();

            for (final Customer customer : customers) {
                FutureTask<Void> futureTask = new FutureTask<>(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        try {
                            updateCss(context, customer);
                        } catch (Exception ex) {
                            LOG.error(ex, ex);
                        }
                        return null;
                    }
                });
                taskList.add(futureTask);
                executor.execute(futureTask);
            }
            for (FutureTask task : taskList) {
                task.get();
            }
            executor.shutdown();
        }
    }

    public void updateCss(ServletContext context, CustomerI customer) throws Exception {
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("customer", customer);
        List<CssAttribute> cssAttributes = cssAttributeBaseDAO.findByAttributes(attributeMap);
        if (cssAttributes.isEmpty()) {
            cssAttributes = getDefaultCssAttributes();
        }
        applyCustomerCss(context, cssAttributes, customer.getName());
    }

    private void applyCustomerCss(ServletContext context, List<CssAttribute> cssAttributes, String customerName) throws Exception {
        if (!cssAttributes.isEmpty()) {
            Path tempPath = Files.createTempDirectory(customerName);
            File tempDir = tempPath.toFile();

            //copy css sortedFiles to data directory
            copyResources(context, FOLDER_CSS, tempDir);

            //copy less sortedFiles
            copyResources(context, FOLDER_LESS, tempDir);

            //replace variables in variables.less, 90_project.less, 96_loader.less
            replaceVariables(context, cssAttributes, VARIABLES_LESS, tempDir);
            replaceVariables(context, cssAttributes, PROJECT_LESS, tempDir);
            replaceVariables(context, cssAttributes, LOADER_LESS, tempDir);


            //compile less and overwrite css sortedFiles in data directory
            if (lessCompiler == null) {
                lessCompiler = new LessCompiler();
            }
            lessCompiler.compile(new File(tempDir, PROJECT_LESS), new File(tempDir, PROJECT_CSS));
            lessCompiler.compile(new File(tempDir, BOOTSTRAP_LESS), new File(tempDir, BOOTSTRAP_CSS));
            lessCompiler.compile(new File(tempDir, LOADER_LESS), new File(tempDir, LOADER_CSS));

            Path allMinCssPath = new File(tempDir, ALL_MIN_CSS).toPath();
            //concatenate all sortedFiles into all.min.css
            String css = concatenateCss(context, new File(tempDir, FOLDER_CSS).toPath(), allMinCssPath);

            //set content for css controller
            context.setAttribute(customerName, css);
        }
    }

    private String concatenateCss(ServletContext context, Path path, Path outFile) throws FileNotFoundException, IOException {
        DirectoryStream<Path> cssFiles = Files.newDirectoryStream(path, "*.css");
        if (Files.exists(outFile)) {
            Files.delete(outFile);
        }
        List<Path> sortedFiles = new ArrayList<>();
        for (Path cssFile : cssFiles) {
            sortedFiles.add(cssFile);
        }

        Collections.sort(sortedFiles, new PathByFileNameComparator());

        for (Path cssFile : sortedFiles) {
            Files.write(outFile, Files.readAllBytes(cssFile), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        byte[] cssData;
        if (Files.exists(outFile)) {
            cssData = Files.readAllBytes(outFile);
        } else {
            //read from classpath
            InputStream is = context.getResourceAsStream(ALL_MIN_CSS);
            cssData = IOUtils.toByteArray(is);
        }
        String css = new String(cssData, Constants.UTF8);
        return css;
    }

    public String getAllMinCss(ServletContext context, String customerName) throws IOException {
        return (String) context.getAttribute(customerName);
    }

    private void copyResources(ServletContext context, String sourceFolder, File destinationFolder) throws MalformedURLException, IOException {
        Set<String> resourcePaths = context.getResourcePaths(sourceFolder);
        if (resourcePaths == null) {
            LOG.warn(String.format("Unable to find folder %s", sourceFolder));
        } else {
            for (String resourcePath : resourcePaths) {
                if (resourcePath.endsWith("/")) { //must be a directory
                    copyResources(context, resourcePath, destinationFolder);
                } else {
                    URL resource = context.getResource(resourcePath);
                    FileUtils.copyURLToFile(resource, new File(destinationFolder, resourcePath));
                }
            }
        }
    }

    private void replaceVariables(ServletContext context, List<CssAttribute> cssAttributes, String FILE_NAME, File destDir) throws IOException {
        InputStream lessIs = context.getResourceAsStream(FILE_NAME);
        Path outputPath = new File(destDir, FILE_NAME).toPath();
        String content = IOUtils.toString(lessIs, Constants.UTF8);
        for (CssAttribute attribute : cssAttributes) {
            if (!StringUtils.isEmpty(attribute.getCssValue())) {
                content = content.replaceAll(attribute.getCssDefaultValue(), attribute.getCssValue());
            }
        }

        //overwrite variables.less in data directory
        if (!Files.exists(outputPath)) {
            if (!Files.exists(outputPath.getParent())) {
                Files.createDirectory(outputPath.getParent());
            }
            Files.createFile(outputPath);
        }
        Files.write(outputPath, content.getBytes(Constants.UTF8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public List<CssAttribute> getDefaultCssAttributes() {
        List<CssAttribute> atts = new ArrayList<>();
        atts.add(getCssAttribute("bgColor", "#94cfea", "#94cfea"));
        atts.add(getCssAttribute("primaryColor", "#0094ff", "#0094ff"));
        atts.add(getCssAttribute("primaryLinkColor", "#31708f", "#31708f"));
        atts.add(getCssAttribute("primaryLinkHoverColor", "#94cfeb", "#94cfeb"));
        atts.add(getCssAttribute("headerColor", "#070056", "#070056"));
        atts.add(getCssAttribute("footerColor", "#070054", "#070054"));
        atts.add(getCssAttribute("backgroundImage", "url\\('\\/static\\/images\\/bg\\.jpg'\\)", "url('/static/images/bg.jpg')"));
        atts.add(getCssAttribute("backgroundRepeat", "no-repeat", "no-repeat"));
        atts.add(getCssAttribute("backgroundSize", "cover", "cover"));
        atts.add(getCssAttribute("loaderOpacity", "@loaderOpacity: 1", "@loaderOpacity: 1"));
        atts.add(getCssAttribute("customCss", "/\\*customCss\\*/", ""));
        return atts;
    }

    private CssAttribute getCssAttribute(String name, String cssDefaultValue, String cssValue) {
        CssAttribute att = new CssAttribute();
        att.setName(name);
        att.setCssDefaultValue(cssDefaultValue);
        att.setCssValue(cssValue);
        return att;
    }

    public List<CssAttribute> getCssAttributes() {
        List<CssAttribute> cssAttributes = cssAttributeDAO.findAll();
        List<CssAttribute> defaultCssAttributes = getDefaultCssAttributes();
        for (CssAttribute defaultAttribute : defaultCssAttributes) {
            boolean exists = false;
            for (CssAttribute att : cssAttributes) {
                if (att.getName().equals(defaultAttribute.getName())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                cssAttributes.add(defaultAttribute);
            }
        }

        Collections.sort(cssAttributes, new CssAttributeComparator());
        return cssAttributes;
    }
}
