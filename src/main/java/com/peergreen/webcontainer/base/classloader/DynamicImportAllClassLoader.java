/**
 * Copyright 2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.webcontainer.base.classloader;

import static org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION;
import static org.osgi.framework.Constants.BUNDLE_NAME;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_VERSION;
import static org.osgi.framework.Constants.EXPORT_PACKAGE;
import static org.osgi.framework.Constants.FRAGMENT_HOST;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleReference;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.FrameworkWiring;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * ClassLoader that allows to load all classes exported on the OSGi framework.
 * @author Florent Benoit
 */
public class DynamicImportAllClassLoader extends URLClassLoader implements BundleReference {

    /**
     * List of packages already used for the fragment.
     */
    private final List<String> packagesForFragment;

    /**
     * Global filter for the auto fragment.
     */
    private final String globalFilter = System.getProperty("com.peergreen.webcontainer.base.classloader.automatic-fragment", "sun.*, com.sun.*");

    /**
     * Filters
     */
    private final List<String> filters;

    /**
     * Logger.
     */
    private final Log logger = LogFactory.getLog(DynamicImportAllClassLoader.class);

    /**
     * Classloader of the bundle.
     */
    private final ClassLoader bundleClassLoader;

    public DynamicImportAllClassLoader() {
        super(new URL[0]);
        this.packagesForFragment = new ArrayList<>();
        this.filters = new ArrayList<>();
        String[] split = this.globalFilter.split(",");
        for (String pkg : split) {
            filters.add(pkg.trim());
        }
        this.bundleClassLoader = DynamicImportAllClassLoader.class.getClassLoader();
    }

    /**
     * Loads the class with the specified <a href="#name">binary name</a>. This
     * method searches for classes in the same manner as the
     * {@link #loadClass(String, boolean)} method. It is invoked by the Java
     * virtual machine to resolve class references. Invoking this method is
     * equivalent to invoking {@link #loadClass(String, boolean)
     * <tt>loadClass(name,
     * false)</tt>}. </p>
     * @param name The <a href="#name">binary name</a> of the class
     * @return The resulting <tt>Class</tt> object
     * @throws ClassNotFoundException If the class was not found
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return this.bundleClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {

            // matching filter ?
            if (!match(name)) {
                throw e;
            }

            // class not found, is that this class is available in the system classloader ?

            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(name);
            if (clazz != null) {
                // class has been found so add the fragment
                addFragment(name.substring(0, name.lastIndexOf(".")));
                return this.bundleClassLoader.loadClass(name);
            }
            // not in system classloader so throw the exception and without adding the fragment
            throw e;
        }
    }

    /**
     * Check if the given classname match the package.
     * @param className the name of the class
     * @return true if the class is matching filter
     */
    protected boolean match(String className) {
        for (String filter : filters) {
            if (className.matches(filter)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Loads the class with the specified <a href="#name">binary name</a>.  The
     * default implementation of this method searches for classes in the
     * following order:
     *
     * <p><ol>
     *
     *   <li><p> Invoke {@link #findLoadedClass(String)} to check if the class
     *   has already been loaded.  </p></li>
     *
     *   <li><p> Invoke the {@link #loadClass(String) <tt>loadClass</tt>} method
     *   on the parent class loader.  If the parent is <tt>null</tt> the class
     *   loader built-in to the virtual machine is used, instead.  </p></li>
     *
     *   <li><p> Invoke the {@link #findClass(String)} method to find the
     *   class.  </p></li>
     *
     * </ol>
     *
     * <p> If the class was found using the above steps, and the
     * <tt>resolve</tt> flag is true, this method will then invoke the {@link
     * #resolveClass(Class)} method on the resulting <tt>Class</tt> object.
     *
     * <p> Subclasses of <tt>ClassLoader</tt> are encouraged to override {@link
     * #findClass(String)}, rather than this method.  </p>
     *
     * <p> Unless overridden, this method synchronizes on the result of
     * {@link #getClassLoadingLock <tt>getClassLoadingLock</tt>} method
     * during the entire class loading process.
     *
     * @param  name
     *         The <a href="#name">binary name</a> of the class
     *
     * @param  resolve
     *         If <tt>true</tt> then resolve the class
     *
     * @return  The resulting <tt>Class</tt> object
     *
     * @throws  ClassNotFoundException
     *          If the class could not be found
     */
    @Override
    public Class<?> loadClass(final String name, final boolean flag) throws ClassNotFoundException {
        return loadClass(name);
    }


    /**
     * Finds the resource with the given name. A resource is some data (images,
     * audio, text, etc) that can be accessed by class code in a way that is
     * independent of the location of the code.
     * <p>
     * The name of a resource is a '<tt>/</tt>'-separated path name that
     * identifies the resource.
     * <p>
     * This method will first search the parent class loader for the resource;
     * if the parent is <tt>null</tt> the path of the class loader built-in to
     * the virtual machine is searched. That failing, this method will invoke
     * {@link #findResource(String)} to find the resource.
     * </p>
     * @param name The resource name
     * @return A <tt>URL</tt> object for reading the resource, or <tt>null</tt>
     * if the resource could not be found or the invoker doesn't have adequate
     * privileges to get the resource.
     */
    @Override
    public URL getResource(String name) {
        URL url = this.bundleClassLoader.getResource(name);

        // search with super method
        if (url == null) {
            url = super.getResource(name);
        }
        // return URL
        return url;
    }

    /**
     * Finds all the resources with the given name. A resource is some data
     * (images, audio, text, etc) that can be accessed by class code in a way
     * that is independent of the location of the code.
     * <p>
     * The name of a resource is a <tt>/</tt>-separated path name that
     * identifies the resource.
     * <p>
     * The search order is described in the documentation for
     * {@link #getResource(String)}.
     * </p>
     * @param name The resource name
     * @return An enumeration of {@link java.net.URL <tt>URL</tt>} objects for
     * the resource. If no resources could be found, the enumeration will be
     * empty. Resources that the class loader doesn't have access to will not be
     * in the enumeration.
     * @throws IOException If I/O errors occur
     * @see #findResources(String)
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {

        Enumeration<URL> enumeration = this.bundleClassLoader.getResources(name);

        // Empty enumeration, try with super method
        if (!enumeration.hasMoreElements()) {
            enumeration = super.getResources(name);
        }
        // send the result
        return enumeration;
    }

    /**
     * Returns an input stream for reading the specified resource.
     * <p>
     * The search order is described in the documentation for
     * {@link #getResource(String)}.
     * </p>
     * @param name The resource name
     * @return An input stream for reading the resource, or <tt>null</tt> if the
     * resource could not be found
     */
    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream is = this.bundleClassLoader.getResourceAsStream(name);

        // Not found, try with super method
        if (is == null) {
            is = super.getResourceAsStream(name);
        }

        // send the result
        return is;
    }

    @Override
    public Bundle getBundle() {
        return FrameworkUtil.getBundle(DynamicImportAllClassLoader.class);
    }


    /**
     * Generates a fragment for the specified packageName
     * @param packageName the name of the package to generate
     * @throws ClassNotFoundException
     */
    protected void addFragment(String packageName) throws ClassNotFoundException {
        if (packagesForFragment.contains(packageName)) {
            return;
        }

        logger.warn("The application is requiring a package ''{0}'' that is not exported yet on the platform but is available in the system classloader. Enabling OSGi fragment for providing this package", packageName);

        // add the fragment
        packagesForFragment.add(packageName);

        Manifest manifest = new Manifest();

        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        manifest.getMainAttributes().putValue(EXPORT_PACKAGE, packageName);
        manifest.getMainAttributes().putValue(BUNDLE_MANIFESTVERSION, "2");
        manifest.getMainAttributes().putValue(BUNDLE_NAME, "Automatic generation of fragment bundle for package ".concat(packageName));
        manifest.getMainAttributes().putValue(BUNDLE_SYMBOLICNAME, "peergreen.auto-fragment.".concat(packageName));
        manifest.getMainAttributes().putValue(BUNDLE_VERSION, "1.0.0");
        manifest.getMainAttributes().putValue(FRAGMENT_HOST, "system.bundle; extension:=framework");


        byte[] bytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); JarOutputStream jarOutputStream = new JarOutputStream(baos, manifest)) {
            jarOutputStream.close();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            throw new ClassNotFoundException("Unable to generate a fragment for '" + packageName + "'.", e);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
           Bundle bundle = getBundle().getBundleContext().installBundle("file:///auto.fragment.for.".concat(packageName), bais);
           FrameworkWiring frameworkWiring = getBundle().getBundleContext().getBundle(0).adapt(FrameworkWiring.class);
           frameworkWiring.resolveBundles(Collections.singleton(bundle));
        } catch (BundleException | IOException e) {
            throw new ClassNotFoundException("Unable to generate a fragment for '" + packageName + "'.", e);
        }

    }

}
