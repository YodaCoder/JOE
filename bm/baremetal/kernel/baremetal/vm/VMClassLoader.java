/* VMClassLoader.java -- Reference implementation of native interface
   required by ClassLoader
   Copyright (C) 1998, 2001, 2002 Free Software Foundation

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package baremetal.vm;

import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
//import gnu.java.lang.SystemClassLoader;

//import gnu.java.util.EmptyEnumeration;

/**
 * java.lang.VMClassLoader is a package-private helper for VMs to implement
 * on behalf of java.lang.ClassLoader.
 *
 * @author John Keiser
 * @author Mark Wielaard <mark@klomp.org>
 * @author Eric Blake <ebb9@email.byu.edu>
 */
public final class VMClassLoader
{
  /**
   * Helper to define a class using a string of bytes. This assumes that
   * the security checks have already been performed, if necessary.
   * <strong>This method will be removed in a future version of GNU
   * Classpath</strong>.
   *
   * @param name the name to give the class, or null if unknown
   * @param data the data representing the classfile, in classfile format
   * @param offset the offset into the data where the classfile starts
   * @param len the length of the classfile data in the array
   * @return the class that was defined
   * @throws ClassFormatError if data is not in proper classfile format
   * @deprecated Implement
   * {@link #defineClass(ClassLoader, String, byte[], int, int, ProtectionDomain)}
   *   instead.
   */
  static final Class defineClass(ClassLoader cl, String name,
                                        byte[] data, int offset, int len)
    throws ClassFormatError {
	return null;
}

  /**
   * Helper to define a class using a string of bytes. This assumes that
   * the security checks have already been performed, if necessary.
   *
   * <strong>For backward compatibility, this just ignores the protection
   * domain; that is the wrong behavior, and you should directly implement
   * this method natively if you can.</strong>
   *
   * @param name the name to give the class, or null if unknown
   * @param data the data representing the classfile, in classfile format
   * @param offset the offset into the data where the classfile starts
   * @param len the length of the classfile data in the array
   * @param pd the protection domain
   * @return the class that was defined
   * @throws ClassFormatError if data is not in proper classfile format
   */
  public static final Class defineClass(ClassLoader cl, String name,
                                 byte[] data, int offset, int len,
                                 ProtectionDomain pd)
    throws ClassFormatError
  {
    return defineClass(cl, name, data, offset, len);
  }

  /**
   * Helper to resolve all references to other classes from this class.
   *
   * @param c the class to resolve
   */
  public static final void resolveClass(Class c) {
}

  /**
   * Helper to load a class from the bootstrap class loader.
   *
   * XXX - Not implemented; this requires native help.
   *
   * @param name the class name to load
   * @param resolve whether to resolve it
   * @return the class, loaded by the bootstrap classloader
   */
  public static final Class loadClass(String name, boolean resolve)
    throws ClassNotFoundException
  {
    throw new ClassNotFoundException(name);
  }

  /**
   * Helper to load a resource from the bootstrap class loader.
   *
   * XXX - Not implemented; this requires native help.
   *
   * @param name the resource to find
   * @return the URL to the resource
   */
  public static URL getResource(String name)
  {
    return null;
  }

  /**
   * Helper to get a list of resources from the bootstrap class loader.
   *
   * XXX - Not implemented; this requires native help.
   *
   * @param name the resource to find
   * @return an enumeration of resources
   * @throws IOException if one occurs
   */
  public static Enumeration getResources(String name) throws IOException
  {
    return null;
  }

  /**
   * Helper to get a package from the bootstrap class loader.  The default
   * implementation of returning null may be adequate, or you may decide
   * that this needs some native help.
   *
   * @param name the name to find
   * @return the named package, if it exists
   */
  public static Package getPackage(String name)
  {
    return null;
  }

  /**
   * Helper to get all packages from the bootstrap class loader.  The default
   * implementation of returning an empty array may be adequate, or you may
   * decide that this needs some native help.
   *
   * @return all named packages, if any exist
   */
  public static Package[] getPackages()
  {
    return new Package[0];
  }

  /**
   * Helper for java.lang.Integer, Byte, etc to get the TYPE class
   * at initialization time. The type code is one of the chars that
   * represents the primitive type as in JNI.
   *
   * <ul>
   * <li>'Z' - boolean</li>
   * <li>'B' - byte</li>
   * <li>'C' - char</li>
   * <li>'D' - double</li>
   * <li>'F' - float</li>
   * <li>'I' - int</li>
   * <li>'J' - long</li>
   * <li>'S' - short</li>
   * <li>'V' - void</li>
   * </ul>
   *
   * Note that this is currently a java version that converts the type code
   * to a string and calls the native <code>getPrimitiveClass(String)</code>
   * method for backwards compatibility with VMs that used old versions of
   * GNU Classpath. Please replace this method with a native method
   * <code>final static native Class getPrimitiveClass(char type);</code>
   * if your VM supports it. <strong>The java version of this method and
   * the String version of this method will disappear in a future version
   * of GNU Classpath</strong>.
   *
   * @param type the primitive type
   * @return a "bogus" class representing the primitive type
   */
  public static final Class getPrimitiveClass(char type)
  {
    Class t;
    switch (type)
      {
      case 'Z':
        t = VMBoolean.class;
        break;
      case 'B':
        t = VMByte.class;
        break;
      case 'C':
        t = VMChar.class;
        break;
      case 'D':
        t = VMDouble.class;
        break;
      case 'F':
        t = VMFloat.class;
        break;
      case 'I':
        t = VMInt.class;
        break;
      case 'J':
        t = VMLong.class;
        break;
      case 'S':
        t = VMShort.class;
        break;
      case 'V':
        t = VMVoid.class;
        break;
      default:
        throw new NoClassDefFoundError("Invalid type specifier: " + type);
      }
    return t;
  }

  /**
   * Old version of the interface, added here for backwards compatibility.
   * Called by the java version of getPrimitiveClass(char) when no native
   * version of that method is available.
   * <strong>This method will be removed in a future version of GNU
   * Classpath</strong>.
   * @param type the primitive type
   * @return a "bogus" class representing the primitive type
   */
  static final Class getPrimitiveClass(String type) {
	return null;
}

  /**
   * The system default for assertion status. This is used for all system
   * classes (those with a null ClassLoader), as well as the initial value for
   * every ClassLoader's default assertion status.
   *
   * XXX - Not implemented yet; this requires native help.
   *
   * @return the system-wide default assertion status
   */
  public static final boolean defaultAssertionStatus()
  {
    return true;
  }

  /**
   * The system default for package assertion status. This is used for all
   * ClassLoader's packageAssertionStatus defaults. It must be a map of
   * package names to Boolean.TRUE or Boolean.FALSE, with the unnamed package
   * represented as a null key.
   *
   * XXX - Not implemented yet; this requires native help.
   *
   * @return a (read-only) map for the default packageAssertionStatus
   */
  public static final Map packageAssertionStatus()
  {
    return new HashMap();
  }

  /**
   * The system default for class assertion status. This is used for all
   * ClassLoader's classAssertionStatus defaults. It must be a map of
   * class names to Boolean.TRUE or Boolean.FALSE
   *
   * XXX - Not implemented yet; this requires native help.
   *
   * @return a (read-only) map for the default classAssertionStatus
   */
  public static final Map classAssertionStatus()
  {
    return new HashMap();
  }

  public static ClassLoader getSystemClassLoader()
  {
  	return null;
  }
}
