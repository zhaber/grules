package org.grules.utils

import org.codehaus.groovy.reflection.MixinInMetaClass
import org.codehaus.groovy.runtime.HandleMetaClass

/**
 * Provides utility methods for class objects.
 */
class ClassUtils {

  static boolean hasMixin(object, Class mixinClass) {
    MetaClassImpl metaClassImpl = (object.metaClass as HandleMetaClass).delegate
    if (metaClassImpl instanceof ExpandoMetaClass) {
      return ((metaClassImpl as ExpandoMetaClass).mixinClasses.any {
        MixinInMetaClass mixinInMetaClass ->
        mixinInMetaClass.mixinClass.theClass == mixinClass
      })
    }
  }
}

